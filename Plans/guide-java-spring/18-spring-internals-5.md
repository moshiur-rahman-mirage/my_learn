@@ 3 | TOPIC | Practice — Coding drill: Spring internals puzzles | Ten puzzles on proxies, transactions, bean lifecycle, auto-configuration and the MVC pipeline with code and explanations | MUST
# Coding drill: Spring internals puzzles

Ten puzzles of the kind senior interviewers put on a whiteboard: "what does this print?", "why is this null?", "fix this". Predict the answer before opening the solution, and say the internal mechanism aloud.

## 1. Self-invocation and REQUIRES_NEW
`placeOrder` should commit the audit row even if the order fails. It does not. Why, and fix it.

```java
// Spring snippet: org.springframework imports omitted for brevity
@Service
public class OrderService {
    @Transactional
    public void placeOrder(Order o) {
        repository.save(o);
        audit("placed " + o.getId());
        throw new IllegalStateException("payment declined");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void audit(String msg) {
        auditRepository.save(new AuditEntry(msg));
    }
}
```
Hint: who calls `audit`?

<details><summary>Solution</summary>

`audit` is called through `this`, so the proxy is bypassed and REQUIRES_NEW is ignored. The audit insert joins the outer transaction and is rolled back with it. Move `audit` into its own bean:

```java
// Spring snippet: org.springframework imports omitted for brevity
@Service
public class AuditService {
    private final AuditRepository auditRepository;
    public AuditService(AuditRepository auditRepository) { this.auditRepository = auditRepository; }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void audit(String msg) { auditRepository.save(new AuditEntry(msg)); }
}

@Service
public class OrderService {
    private final OrderRepository repository;
    private final AuditService auditService;

    public OrderService(OrderRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional
    public void placeOrder(Order o) {
        repository.save(o);
        auditService.audit("placed " + o.getId());     // through the proxy: new transaction
        throw new IllegalStateException("payment declined");
    }
}
```
Now the audit commits on its own connection while the order rolls back.
</details>

## 2. Predict the output: lifecycle order
```java
// Spring snippet: org.springframework imports omitted for brevity
@Component
class Probe implements InitializingBean, BeanNameAware {
    @Autowired Clock clock;
    Probe() { System.out.println("ctor clock=" + clock); }
    @Override public void setBeanName(String n) { System.out.println("name=" + n + " clock=" + (clock != null)); }
    @PostConstruct void pc() { System.out.println("postConstruct"); }
    @Override public void afterPropertiesSet() { System.out.println("afterPropertiesSet"); }
}
```
Hint: field injection happens in `populateBean`.

<details><summary>Solution</summary>

```text
ctor clock=null
name=probe clock=true
postConstruct
afterPropertiesSet
```
Constructor first (fields not injected yet), then `populateBean` injects `clock`, then Aware callbacks, then `postProcessBeforeInitialization` (where `CommonAnnotationBeanPostProcessor` runs `@PostConstruct`), then `afterPropertiesSet`. Constructor injection avoids the null in the constructor.
</details>

## 3. The silent commit
Why does this commit the order even though the method throws?

```java
// Spring snippet: org.springframework imports omitted for brevity
@Transactional
public void importOrders(Path file) throws IOException {
    orderRepository.save(new Order("A"));
    Files.readAllLines(file);           // throws NoSuchFileException
}
```
Hint: what kind of exception is `IOException`?

<details><summary>Solution</summary>

`NoSuchFileException` is a checked `IOException`. Spring's default rule rolls back only on `RuntimeException` and `Error`, so it commits. Fix:

```java
// Spring snippet: org.springframework imports omitted for brevity
@Transactional(rollbackFor = IOException.class)
public void importOrders(Path file) throws IOException {
    orderRepository.save(new Order("A"));
    Files.readAllLines(file);
}
```
Or wrap in an unchecked domain exception (`throw new ImportFailedException(file, e)`), or enable `rollbackOn = RollbackOn.ALL_EXCEPTIONS` globally (Spring 6.2).
</details>

## 4. Final method on a proxy
`priceService.vatFor(order)` throws `NullPointerException` in production, but the unit test passes. Why?

```java
// Spring snippet: org.springframework imports omitted for brevity
@Service
public class PriceService {
    private final TaxRates rates;
    public PriceService(TaxRates rates) { this.rates = rates; }

    @Cacheable("prices")
    public BigDecimal price(long productId) { return BigDecimal.TEN; }

    public final BigDecimal vatFor(Order order) {
        return order.total().multiply(rates.current());
    }
}
```
Hint: in the unit test, `PriceService` is created with `new`.

<details><summary>Solution</summary>

`@Cacheable` makes Spring create a CGLIB proxy. The final method cannot be overridden, so it runs on the proxy instance, whose `rates` field was never set (Objenesis skips constructors). The unit test used the real object, so it worked. Remove `final`:

```java
// Spring snippet: org.springframework imports omitted for brevity
public BigDecimal vatFor(Order order) {
    return order.total().multiply(rates.current());
}
```
</details>

## 5. UnexpectedRollbackException
Explain the exception and give two fixes.

```java
// Spring snippet: org.springframework imports omitted for brevity
@Transactional
public void checkoutAll(List<Long> ids) {
    for (Long id : ids) {
        try {
            checkoutService.checkout(id);    // @Transactional (REQUIRED) in another bean
        } catch (RuntimeException e) {
            log.warn("skip {}", id);
        }
    }
}
```
Hint: who owns the transaction?

<details><summary>Solution</summary>

`checkout` joins the outer transaction. When it throws, its interceptor marks the shared transaction rollback-only. The outer method swallows the exception and returns normally, so at commit Spring finds the flag, rolls back everything and throws `UnexpectedRollbackException`.

Fix A, each checkout independent:
```java
// Spring snippet: org.springframework imports omitted for brevity
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void checkout(long id) { /* ... */ }
```
Fix B, no outer transaction at all, and each call starts its own:
```java
// Spring snippet: org.springframework imports omitted for brevity
public void checkoutAll(List<Long> ids) {           // no @Transactional
    for (Long id : ids) {
        try { checkoutService.checkout(id); }
        catch (RuntimeException e) { log.warn("skip {}", id); }
    }
}
```
Fix B is usually better: no long outer transaction and no held connection.
</details>

## 6. Why is my auto-configured bean not created?
A shared library has this configuration in package `com.acme.metrics`, and OrderHub's main class is in `com.orderhub`. The bean never appears. Why?

```java
// Spring snippet: org.springframework imports omitted for brevity
@Configuration
@ConditionalOnClass(MeterRegistry.class)
public class AcmeMetricsConfig {
    @Bean
    @ConditionalOnMissingBean
    AcmeMetrics acmeMetrics(MeterRegistry registry) { return new AcmeMetrics(registry); }
}
```
Hint: how does Boot discover classes outside your packages?

<details><summary>Solution</summary>

Component scanning covers only `com.orderhub` and below, and the class is not registered as an auto-configuration. Make it a proper auto-configuration:

```java
// Spring snippet: org.springframework imports omitted for brevity
@AutoConfiguration(after = CompositeMeterRegistryAutoConfiguration.class)
@ConditionalOnClass(MeterRegistry.class)
public class AcmeMetricsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(MeterRegistry.class)
    AcmeMetrics acmeMetrics(MeterRegistry registry) { return new AcmeMetrics(registry); }
}
```
```text
# META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
com.acme.metrics.AcmeMetricsAutoConfiguration
```
The `after` attribute makes sure the `MeterRegistry` bean definition exists when `@ConditionalOnBean` is evaluated.
</details>

## 7. Write a minimal JDK proxy-based "transaction" wrapper
Without Spring, write `Transactional.wrap(target, iface)` that prints `BEGIN`, calls the method, and prints `COMMIT`, or `ROLLBACK` if it throws a `RuntimeException`.

Hint: `Proxy.newProxyInstance` and unwrap `InvocationTargetException`.

<details><summary>Solution</summary>

```java
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

public class TxProxyDemo {
    interface AccountService { void transfer(int amount); }

    static class AccountServiceImpl implements AccountService {
        public void transfer(int amount) {
            if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
            System.out.println("transfer " + amount);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T wrap(T target, Class<T> iface) {
        return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] { iface },
            (proxy, method, args) -> {
                System.out.println("BEGIN");
                try {
                    Object result = method.invoke(target, args);
                    System.out.println("COMMIT");
                    return result;
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();          // the real exception
                    System.out.println(cause instanceof RuntimeException ? "ROLLBACK" : "COMMIT");
                    throw cause;
                }
            });
    }

    public static void main(String[] args) {
        AccountService service = wrap(new AccountServiceImpl(), AccountService.class);
        service.transfer(50);
        try {
            service.transfer(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("caught: " + e.getMessage());
        }
    }
}
```
Output:
```text
BEGIN
transfer 50
COMMIT
BEGIN
ROLLBACK
caught: amount must be positive
```
`Method.invoke` wraps exceptions in `InvocationTargetException`; rethrowing the cause keeps the caller's exception type, as Spring's `AopUtils.invokeJoinpointUsingReflection` does. Checked exceptions commit, mirroring Spring's default rule.
</details>

## 8. The bean that misses its proxy
After adding this post-processor, `@Transactional` on `CustomerService` stops working and a log line says it is "not eligible for getting processed by all BeanPostProcessors". Fix it.

```java
// Spring snippet: org.springframework imports omitted for brevity
@Component
public class CustomerCountLogger implements BeanPostProcessor {
    private final CustomerService customers;
    public CustomerCountLogger(CustomerService customers) { this.customers = customers; }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        if (name.equals("orderService")) System.out.println("customers: " + customers.count());
        return bean;
    }
}
```
Hint: when are BPPs and their dependencies created?

<details><summary>Solution</summary>

BPPs are created in `registerBeanPostProcessors`, so `CustomerService` is created early, before the auto-proxy creator is registered, and never gets its transactional proxy. Remove the eager dependency:

```java
// Spring snippet: org.springframework imports omitted for brevity
public class CustomerCountLogger implements BeanPostProcessor {
    private final ObjectProvider<CustomerService> customers;     // resolved later, on first use
    public CustomerCountLogger(ObjectProvider<CustomerService> customers) { this.customers = customers; }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        if (name.equals("orderService")) System.out.println("customers: " + customers.getObject().count());
        return bean;
    }
}

@Bean
static CustomerCountLogger customerCountLogger(ObjectProvider<CustomerService> customers) {
    return new CustomerCountLogger(customers);
}
```
Better still, this is not post-processing at all: log the count in an `ApplicationReadyEvent` listener.
</details>

## 9. Interceptor versus filter
You must reject requests to `/api/admin/**` unless header `X-Admin-Token` matches, and you also need the controller method name in the log. Write it as an interceptor and explain why not a filter.

Hint: only one of them knows the handler.

<details><summary>Solution</summary>

```java
// Spring snippet: org.springframework imports omitted for brevity
public class AdminTokenInterceptor implements HandlerInterceptor {
    private final String expected;
    public AdminTokenInterceptor(String expected) { this.expected = expected; }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        if (!expected.equals(req.getHeader("X-Admin-Token"))) {
            String method = handler instanceof HandlerMethod hm ? hm.getMethod().getName() : "unknown";
            System.out.println("rejected admin call to " + method);
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}

@Configuration
class AdminWebConfig implements WebMvcConfigurer {
    @Value("${orderhub.admin-token}") String token;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminTokenInterceptor(token)).addPathPatterns("/api/admin/**");
    }
}
```
A filter runs before the `DispatcherServlet` and cannot see the `HandlerMethod`. In a real app, security belongs in Spring Security's filter chain; the interceptor is right when the decision needs handler metadata.
</details>

## 10. Circular dependency after a Boot upgrade
After upgrading to Boot 3, this fails with "The dependencies of some of the beans in the application context form a cycle". Remove the cycle properly.

```java
// Spring snippet: org.springframework imports omitted for brevity
@Service class CartService {
    @Autowired PricingService pricing;
    BigDecimal total(Cart c) { return pricing.priceAll(c.items()); }
}
@Service class PricingService {
    @Autowired CartService carts;
    BigDecimal priceAll(List<Item> items) { /* ... */ return BigDecimal.ZERO; }
    BigDecimal discountFor(long cartId) { return carts.find(cartId).size() > 5 ? BigDecimal.TEN : BigDecimal.ZERO; }
}
```
Hint: what does `PricingService` actually need from carts?

<details><summary>Solution</summary>

`PricingService` only needs the number of items, which it can receive as a parameter. Pass data, not services, and use constructor injection:

```java
// Spring snippet: org.springframework imports omitted for brevity
@Service
class PricingService {
    BigDecimal priceAll(List<Item> items) { return BigDecimal.ZERO; }
    BigDecimal discountFor(int itemCount) { return itemCount > 5 ? BigDecimal.TEN : BigDecimal.ZERO; }
}

@Service
class CartService {
    private final PricingService pricing;
    CartService(PricingService pricing) { this.pricing = pricing; }

    BigDecimal total(Cart c) {
        return pricing.priceAll(c.items()).subtract(pricing.discountFor(c.items().size()));
    }
}
```
The dependency now goes one way. Re-enabling `spring.main.allow-circular-references` would only hide the design problem.
</details>

@@ 3 | TOPIC | Practice — Phase 18 review: Spring Internals | Cheat sheet, key code, top 10 questions and rapid-fire flashcards for Spring internals | MUST
# Phase 18 review: Spring Internals

## Cheat sheet

| Idea | Remember |
|---|---|
| `run()` | Environment, create context, `refresh()`, runners, `ApplicationReadyEvent` |
| `refresh()` key steps | BFPPs (config parsing), register BPPs, `onRefresh` (create Tomcat), create singletons, `finishRefresh` (start Tomcat) |
| Config parsing | `ConfigurationClassPostProcessor`, a `BeanDefinitionRegistryPostProcessor` |
| Bean creation | Constructor, merged-definition BPPs, early reference, populate, Aware, before-init BPPs (`@PostConstruct`), `afterPropertiesSet`, after-init BPPs (proxy) |
| BFPP | Edits definitions; declare with `static @Bean` |
| BPP | Edits or replaces instances; proxies created in `postProcessAfterInitialization` |
| Three caches | `singletonObjects`, `earlySingletonObjects`, `singletonFactories` |
| Cycles | Constructor cycles never resolvable; Boot 2.6+ forbids all by default |
| AOP | Aspect = pointcut + advice; Spring AOP = runtime proxies, method execution only |
| Proxy types | JDK = interfaces; CGLIB = subclass; Boot default CGLIB |
| Proxy limits | No self-invocation, private, static or final methods |
| `@Transactional` | `TransactionInterceptor` + `PlatformTransactionManager` + `ThreadLocal`-bound resources |
| Rollback | Unchecked and `Error` by default; `rollbackFor` for checked |
| Rollback-only | Inner failure caught by outer gives `UnexpectedRollbackException` |
| Auto-config | `AutoConfiguration.imports`, deferred import, conditions, back-off with `@ConditionalOnMissingBean` |
| Debug auto-config | `--debug` or `/actuator/conditions` |
| Starter | autoconfigure module plus starter pom; `name-spring-boot-starter`; test with `ApplicationContextRunner` |
| MVC flow | Filters, `DispatcherServlet`, HandlerMapping, preHandle, HandlerAdapter (resolvers, converters), postHandle, exception resolvers, afterCompletion |
| Startup | Measure with `/actuator/startup`; CDS; Spring AOT; native = closed world |

### Key code snippets
```java
// Spring snippet: org.springframework imports omitted for brevity
// Static BFPP and a proxy-creating BPP
@Bean
static BeanFactoryPostProcessor lazyReports() {
    return factory -> factory.getBeanDefinition("salesReportService").setLazyInit(true);
}

public class TimedBpp implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        if (!bean.getClass().isAnnotationPresent(Timed.class)) return bean;
        ProxyFactory pf = new ProxyFactory(bean);
        pf.addAdvice((MethodInterceptor) inv -> { long t = System.nanoTime(); try { return inv.proceed(); }
            finally { System.out.println(name + " " + (System.nanoTime() - t) + " ns"); } });
        return pf.getProxy();
    }
}
```

```java
// Spring snippet: org.springframework imports omitted for brevity
// An aspect with annotation binding
@Aspect
@Component
public class AuditAspect {
    @Around("@annotation(audited)")
    public Object audit(ProceedingJoinPoint pjp, Audited audited) throws Throwable {
        Object result = pjp.proceed();
        log.info("action={} args={}", audited.action(), Arrays.toString(pjp.getArgs()));
        return result;
    }
}
```

```java
// Spring snippet: org.springframework imports omitted for brevity
// Transaction boundaries without self-invocation problems
private final TransactionTemplate tx;     // new TransactionTemplate(transactionManager)

public void processAll(List<Long> ids) {
    for (Long id : ids) {
        tx.executeWithoutResult(status -> processOne(id));   // one transaction per item
    }
}
```

```java
// Auto-configuration skeleton
@AutoConfiguration(after = DataSourceAutoConfiguration.class)
@ConditionalOnClass(JdbcClient.class)
@ConditionalOnProperty(prefix = "orderhub.audit", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AuditProperties.class)
public class AuditAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    AuditSink auditSink() { return new LogAuditSink(); }
}
// META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
// com.orderhub.audit.autoconfigure.AuditAutoConfiguration
```

```java
// Spring snippet: org.springframework imports omitted for brevity
// Testing conditions
new ApplicationContextRunner()
    .withConfiguration(AutoConfigurations.of(AuditAutoConfiguration.class))
    .withPropertyValues("orderhub.audit.enabled=false")
    .run(ctx -> assertThat(ctx).doesNotHaveBean(AuditSink.class));
```

```yaml
# Diagnostics
debug: true                                  # conditions evaluation report
logging:
  level:
    org.springframework.orm.jpa.JpaTransactionManager: DEBUG
    org.springframework.web: DEBUG
spring:
  main:
    allow-circular-references: false         # keep the default
```

## The 10 most likely interview questions

1. **Why does `@Transactional` self-invocation not work?** The call goes through `this`, not the proxy, so `TransactionInterceptor` never runs.
2. **How does auto-configuration work?** `AutoConfigurationImportSelector` (deferred) loads classes from `AutoConfiguration.imports`, filters by conditions, and registers them after user config so `@ConditionalOnMissingBean` backs off.
3. **JDK proxy vs CGLIB?** Interface implementation vs subclass; Boot defaults to CGLIB; final methods and classes cannot be CGLIB-proxied.
4. **How are circular dependencies resolved?** Early references via the three-level cache, only for field or setter injection of singletons; Boot 2.6+ forbids them by default; refactor instead.
5. **Walk through a request in Spring MVC.** Filters, `DispatcherServlet`, HandlerMapping, interceptors' preHandle, HandlerAdapter with argument resolvers and converters, controller, return value handler, postHandle, exception resolvers, afterCompletion.
6. **BeanPostProcessor vs BeanFactoryPostProcessor?** Instances vs definitions; BPPs create proxies; BFPPs must be static `@Bean`s.
7. **What happens in `refresh()`?** Parse config via BFPPs, register BPPs, create web server, instantiate singletons, start lifecycle beans, publish events.
8. **Which exceptions roll back a transaction?** Unchecked and errors; configure `rollbackFor` for checked.
9. **How do you write a custom starter?** Autoconfigure module with conditional beans, properties and the imports file; starter pom; `ApplicationContextRunner` tests.
10. **How would you reduce startup time?** Measure with `/actuator/startup`, fix slow beans, use CDS or AOT cache, consider Spring AOT plus native for scale-to-zero.

## Rapid-fire flashcards

Q: Which Spring Boot event means the application is ready to serve traffic?
A: `ApplicationReadyEvent`.

Q: Which class implements @EnableAutoConfiguration's import logic?
A: `AutoConfigurationImportSelector`, a `DeferredImportSelector`.

Q: What Spring Boot property controls the default proxy type?
A: `spring.aop.proxy-target-class` (default true, meaning CGLIB).

Q: Which bean post-processor creates proxies for @Aspect and @Transactional beans?
A: `AnnotationAwareAspectJAutoProxyCreator`.

Q: What does Propagation.MANDATORY do when no transaction exists?
A: Throws `IllegalTransactionStateException`.

Q: Which annotation processor gives IDE completion for a starter's properties?
A: `spring-boot-configuration-processor`.

Q: Which DispatcherServlet hook can still modify a REST response body?
A: `ResponseBodyAdvice.beforeBodyWrite`.

Q: Which interceptor callback runs even when the controller throws?
A: `afterCompletion`.

Q: Where does Spring AOT write bean registration code?
A: Generated `__BeanDefinitions` classes under `target/spring-aot`.

Q: Which test helper hides classes to test @ConditionalOnClass?
A: `FilteredClassLoader` with `ApplicationContextRunner`.
