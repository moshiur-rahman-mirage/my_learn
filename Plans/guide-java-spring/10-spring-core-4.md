@@ 3 | TOPIC | Practice — Coding drill: Spring Core | Ten interview tasks on wiring, scopes, lifecycle, configuration properties, conditions and events | MUST
# Coding drill: Spring Core

Ten tasks of the kind interviewers ask in a Spring round: "fix this wiring", "what does this print", "write the configuration for X". Try each on paper or in OrderHub before opening the solution. All tasks use Spring Boot 3.5 and Java 21.

## 1. Fix the field injection
Refactor this class to constructor injection and make it unit-testable without Spring.

```java
// Snippet: Spring imports (org.springframework.*) and surrounding classes omitted for brevity
@Service
public class InvoiceService {
    @Autowired private OrderRepository orders;
    @Autowired private TaxCalculator tax;

    public BigDecimal total(long orderId) {
        Order o = orders.findById(orderId).orElseThrow();
        return o.subtotal().add(tax.taxFor(o));
    }
}
```
**Hint:** final fields, one constructor, no annotation.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/invoice/InvoiceService.java
package com.orderhub.invoice;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
    private final OrderRepository orders;
    private final TaxCalculator tax;

    public InvoiceService(OrderRepository orders, TaxCalculator tax) {   // implicit @Autowired
        this.orders = orders;
        this.tax = tax;
    }

    public BigDecimal total(long orderId) {
        Order o = orders.findById(orderId).orElseThrow();
        return o.subtotal().add(tax.taxFor(o));
    }
}
```
A test can now write `new InvoiceService(fakeRepo, fakeTax)`. Fields are `final`, so they can never be null or reassigned after construction, and the dependencies are visible in the signature.
</details>

## 2. Two implementations, one injection point
`EmailSender` has `SmtpEmailSender` and `SesEmailSender`. Startup fails with `NoUniqueBeanDefinitionException`. Make SES the default everywhere, but let `AdminAlertService` use SMTP.

**Hint:** `@Primary` on one bean, `@Qualifier` at one injection point.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/mail/MailBeans.java
package com.orderhub.mail;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

interface EmailSender { void send(String to, String text); }

@Component("smtp")
class SmtpEmailSender implements EmailSender {
    public void send(String to, String text) { System.out.println("SMTP " + to); }
}

@Component("ses")
@Primary
class SesEmailSender implements EmailSender {
    public void send(String to, String text) { System.out.println("SES " + to); }
}

@Service
class AdminAlertService {
    private final EmailSender sender;
    AdminAlertService(@Qualifier("smtp") EmailSender sender) { this.sender = sender; }
    void alert(String msg) { sender.send("ops@orderhub.example", msg); }
}
```
Every unqualified `EmailSender` gets SES; `AdminAlertService` gets SMTP because `@Qualifier` filters candidates before `@Primary` is considered.
</details>

## 3. Strategy map from a list
Write a `DiscountService` that receives every `DiscountRule` bean (each has `String code()` and `BigDecimal apply(BigDecimal total)`) and applies the rule chosen by a coupon code. Unknown codes return the total unchanged.

**Hint:** inject `List<DiscountRule>` and collect to a map.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/discount/DiscountService.java
package com.orderhub.discount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {
    private final Map<String, DiscountRule> rules;

    public DiscountService(List<DiscountRule> all) {
        // toMap throws IllegalStateException on duplicate codes: fail fast at startup
        this.rules = all.stream().collect(Collectors.toMap(DiscountRule::code, Function.identity()));
    }

    public BigDecimal apply(String coupon, BigDecimal total) {
        DiscountRule rule = rules.get(coupon);
        return rule == null ? total : rule.apply(total);
    }
}

// File: src/main/java/com/orderhub/discount/Rules.java
package com.orderhub.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

interface DiscountRule {
    String code();
    BigDecimal apply(BigDecimal total);
}

@Component
class TenPercentOff implements DiscountRule {
    public String code() { return "SAVE10"; }
    public BigDecimal apply(BigDecimal t) { return t.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_EVEN); }
}

@Component
class FiveOff implements DiscountRule {
    public String code() { return "FIVEOFF"; }
    public BigDecimal apply(BigDecimal t) { return t.subtract(new BigDecimal("5")).max(BigDecimal.ZERO); }
}
```
`apply("SAVE10", 80.00)` returns `72.00`; `apply("NOPE", 80.00)` returns `80.00`. Adding a rule is one new class, no change to the service.
</details>

## 4. Predict the output: prototype inside a singleton
```java
@Component @Scope("prototype")
class Counter { private int n; int next() { return ++n; } }

@Component
class Ticketing {
    private final Counter counter;
    Ticketing(Counter counter) { this.counter = counter; }
    int ticket() { return counter.next(); }
}

// in a runner:
System.out.println(ticketing.ticket() + " " + ticketing.ticket() + " " + ticketing.ticket());
```

<details><summary>Solution</summary>

```text
1 2 3
```
The prototype is created once, when the singleton `Ticketing` is constructed, and reused forever. To get a fresh `Counter` per call (output `1 1 1`), inject `ObjectProvider<Counter>` and call `getObject()` inside `ticket()`:

```java
// File: src/main/java/com/orderhub/demo/Ticketing.java
package com.orderhub.demo;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
class Ticketing {
    private final ObjectProvider<Counter> counters;
    Ticketing(ObjectProvider<Counter> counters) { this.counters = counters; }
    int ticket() { return counters.getObject().next(); }   // new prototype every call
}
```
</details>

## 5. Predict the output: @Configuration proxying
```java
@Configuration
class Cfg {
    @Bean Pool pool() { System.out.println("pool created"); return new Pool(); }
    @Bean A a() { return new A(pool()); }
    @Bean B b() { return new B(pool()); }
}
```
How many times is "pool created" printed? What if the annotation becomes `@Configuration(proxyBeanMethods = false)`?

<details><summary>Solution</summary>

Full mode:
```text
pool created
```
Lite mode:
```text
pool created
pool created
pool created
```
In full mode the CGLIB proxy intercepts `pool()` calls and returns the singleton. In lite mode, `a()` and `b()` call `pool()` as plain Java methods, so two extra unmanaged pools are created. The fix for lite mode is parameter injection:

```java
// File: src/main/java/com/orderhub/demo/Cfg.java
package com.orderhub.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class Cfg {
    @Bean Pool pool() { System.out.println("pool created"); return new Pool(); }
    @Bean A a(Pool pool) { return new A(pool); }
    @Bean B b(Pool pool) { return new B(pool); }
}
```
</details>

## 6. Typed configuration with validation
Bind this YAML to an immutable, validated object and fail startup if `timeout` is missing or `retries` is negative.

```yaml
orderhub:
  inventory-client:
    base-url: http://inventory:8081
    timeout: 2s
    retries: 3
```
**Hint:** record, `@ConfigurationProperties`, `@Validated`, `Duration`.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/inventory/InventoryClientProperties.java
package com.orderhub.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "orderhub.inventory-client")
public record InventoryClientProperties(
        @NotNull URI baseUrl,
        @NotNull Duration timeout,
        @Min(0) int retries) { }
```
Register it with `@ConfigurationPropertiesScan` on the main class. `2s` is converted to `Duration.ofSeconds(2)`, `base-url` binds to `baseUrl` through relaxed binding. A missing `timeout` or `retries: -1` stops startup with a "Binding to target ... failed" report naming the property and reason.
</details>

## 7. Feature flag with a fallback
Create a `FraudCheck` bean: `ExternalFraudCheck` when `orderhub.fraud.enabled=true`, otherwise `AllowAllFraudCheck`. Exactly one bean must exist in every case, including when the property is absent.

**Hint:** two complementary `@ConditionalOnProperty` with `matchIfMissing`.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/fraud/FraudConfig.java
package com.orderhub.fraud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

interface FraudCheck { boolean allowed(long customerId); }

@Configuration(proxyBeanMethods = false)
class FraudConfig {

    @Bean
    @ConditionalOnProperty(name = "orderhub.fraud.enabled", havingValue = "true")
    FraudCheck externalFraudCheck() {
        return customerId -> customerId % 13 != 0;      // pretend remote scoring
    }

    @Bean
    @ConditionalOnProperty(name = "orderhub.fraud.enabled", havingValue = "false", matchIfMissing = true)
    FraudCheck allowAllFraudCheck() {
        return customerId -> true;
    }
}
```
`true` creates only the external check; `false` or absent creates only allow-all. Complementary conditions avoid both "no bean" and "two beans" failures, and do not depend on evaluation order like `@ConditionalOnMissingBean` would in user configuration.
</details>

## 8. Lifecycle: warm a cache safely
`CategoryCache` must load categories from `CategoryRepository` at startup and log the count. A teammate wrote the loading code in the constructor using a field-injected repository and gets an NPE. Fix it two ways.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/catalog/CategoryCache.java
package com.orderhub.catalog;

import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CategoryCache {
    private final CategoryRepository repository;
    private volatile List<String> categories = List.of();

    // Way 1: constructor injection, the dependency is available immediately
    public CategoryCache(CategoryRepository repository) {
        this.repository = repository;
    }

    // Way 2: keep the constructor trivial and load in @PostConstruct,
    // which runs after ALL injection (also works with field injection)
    @PostConstruct
    void load() {
        categories = List.copyOf(repository.findAllNames());
        System.out.println("loaded " + categories.size() + " categories");
    }

    public List<String> all() { return categories; }
}
```
The NPE happened because field injection runs after the constructor. Either receive the repository in the constructor, or move the loading into `@PostConstruct`. If loading needs a transaction or another proxy feature, use an `ApplicationReadyEvent` listener instead, because proxies do not exist yet during `@PostConstruct`.
</details>

## 9. Send the email only if the order commits
This code sometimes emails customers for orders that were rolled back. Fix it.

```java
@Transactional
public Order place(OrderRequest req) {
    Order o = repository.save(Order.from(req));
    emailService.sendConfirmation(o);      // remote call inside the transaction
    stock.reserve(o);                       // may throw OutOfStockException
    return o;
}
```

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/order/OrderService.java (excerpt)
package com.orderhub.order;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final StockService stock;
    private final ApplicationEventPublisher events;

    public OrderService(OrderRepository repository, StockService stock, ApplicationEventPublisher events) {
        this.repository = repository;
        this.stock = stock;
        this.events = events;
    }

    @Transactional
    public Order place(OrderRequest req) {
        Order o = repository.save(Order.from(req));
        stock.reserve(o);                                  // may throw: rollback
        events.publishEvent(new OrderPlacedEvent(o.id(), o.customerEmail(), o.total()));
        return o;
    }
}

// File: src/main/java/com/orderhub/notification/ConfirmationListener.java
package com.orderhub.notification;

import com.orderhub.order.OrderPlacedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class ConfirmationListener {
    private final EmailService emailService;
    ConfirmationListener(EmailService emailService) { this.emailService = emailService; }

    @Async
    @TransactionalEventListener      // AFTER_COMMIT
    void onPlaced(OrderPlacedEvent e) {
        emailService.sendConfirmation(e.orderId(), e.customerEmail());
    }
}
```
The email now runs only after commit, on another thread, so a slow mail server does not hold the database transaction open either. If the JVM crashes between commit and email, the email is lost; the outbox pattern in Phase 20 closes that gap.
</details>

## 10. Profile-specific beans and a test
Write a `StorageService` with `LocalDiskStorage` for every profile except `prod`, and `S3Storage` for `prod`. Write a test proving the `test` profile gets local disk.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/storage/StorageBeans.java
package com.orderhub.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

interface StorageService { String store(String name, byte[] data); }

@Component
@Profile("!prod")
class LocalDiskStorage implements StorageService {
    public String store(String name, byte[] data) { return "file:/tmp/orderhub/" + name; }
}

@Component
@Profile("prod")
class S3Storage implements StorageService {
    public String store(String name, byte[] data) { return "s3://orderhub-prod/" + name; }
}

// File: src/test/java/com/orderhub/storage/StorageProfileTest.java
package com.orderhub.storage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class StorageProfileTest {
    @Autowired StorageService storage;

    @Test
    void testProfileUsesLocalDisk() {
        assertThat(storage.store("a.pdf", new byte[0])).startsWith("file:");
    }
}
```
Complementary expressions (`prod` and `!prod`) guarantee exactly one bean in every environment.
</details>

@@ 3 | TOPIC | Practice — Phase 10 review: Spring Core | Cheat sheet, key snippets, top 10 questions and rapid-fire flashcards for Spring Core | MUST
# Phase 10 review: Spring Core

## Cheat sheet

| Idea | Remember |
|---|---|
| IoC | The container creates and wires objects; your classes do not call `new` on collaborators |
| DI | Dependencies passed in; constructor injection preferred |
| Bean definition | Recipe (class, scope, deps, lazy, init, destroy); created before any object |
| BeanFactory vs ApplicationContext | Lazy and minimal vs eager singletons, events, Environment, i18n |
| refresh() | Definitions, BeanFactoryPostProcessors, register BeanPostProcessors, web server, singletons, ContextRefreshedEvent |
| Injection styles | Constructor (required, final), setter (optional), field (avoid) |
| Field injection timing | After the constructor, so fields are null inside it |
| Circular deps | Prohibited by default since Boot 2.6; refactor or use `@Lazy` parameter |
| Resolution order | Type, then `@Qualifier`, then `@Primary`, then `@Fallback`, then parameter name |
| All implementations | `List<T>` ordered by `@Order`, `Map<String, T>` keyed by bean name |
| Stereotypes | `@Repository` adds exception translation; `@Service` is semantic; `@RestController` = `@Controller` + `@ResponseBody` |
| Scan root | Package of the `@SpringBootApplication` class |
| `@Configuration` | CGLIB proxy so inter-bean calls return singletons; `proxyBeanMethods = false` disables it |
| Scopes | singleton (default), prototype, request, session, application, websocket |
| Prototype | No destroy callbacks; inside a singleton use `ObjectProvider` |
| Lifecycle | Constructor, injection, Aware, BPP before (`@PostConstruct`), afterPropertiesSet, initMethod, BPP after (proxy) |
| Destroy order | `@PreDestroy`, `DisposableBean.destroy`, destroyMethod |
| Profiles | `application-{profile}.yml`, `@Profile`, `SPRING_PROFILES_ACTIVE` |
| Precedence | Command line over env vars over profile files over base file |
| `@Value` vs `@ConfigurationProperties` | One value or SpEL vs typed, validated, relaxed-bound tree |
| Conditions | Evaluated at definition time; OnClass, OnProperty, OnMissingBean drive auto-config |
| Events | Sync by default; `@Async` for async; `@TransactionalEventListener` runs after commit |

## Key code snippets

```java
// Snippet: Spring imports (org.springframework.*) and surrounding classes omitted for brevity
// Constructor injection with an optional dependency
@Service
public class ProductService {
    private final ProductRepository repository;
    private final ObjectProvider<ProductEventsPublisher> publisher;

    public ProductService(ProductRepository repository, ObjectProvider<ProductEventsPublisher> publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }
}
```

```java
// Qualifier vs primary, and a strategy list
@Component @Primary class CardGateway implements PaymentGateway { }
@Component("paypal") class PaypalGateway implements PaymentGateway { }

CheckoutService(PaymentGateway defaultGateway,
                @Qualifier("paypal") PaymentGateway paypal,
                List<PaymentGateway> all) { }
```

```java
// Java config in lite mode: pass dependencies as parameters
@Configuration(proxyBeanMethods = false)
class ClientsConfig {
    @Bean Clock clock() { return Clock.systemUTC(); }
    @Bean RestClient inventoryClient(RestClient.Builder b) { return b.baseUrl("http://inventory:8081").build(); }
}
```

```java
// Snippet: Spring imports (org.springframework.*) and surrounding classes omitted for brevity
// Typed configuration
@Validated
@ConfigurationProperties(prefix = "orderhub.order")
public record OrderProperties(@Min(1) int maxItems, @DefaultValue("10m") Duration reservationTimeout) { }
```

```yaml
# Profiles and precedence
spring:
  profiles:
    group:
      prod: "prod-db,json-logs"
---
spring:
  config:
    activate:
      on-profile: dev
logging:
  level:
    com.orderhub: DEBUG
```

```java
// Conditions and events
@Bean
@ConditionalOnProperty(name = "orderhub.fraud.enabled", havingValue = "true")
FraudCheck externalFraudCheck() { return id -> true; }

@Async
@TransactionalEventListener
void onOrderPlaced(OrderPlacedEvent e) { emailService.sendConfirmation(e.orderId(), e.customerEmail()); }
```

## The 10 most likely interview questions

1. **What are IoC and DI?** IoC moves object creation and wiring to the container; DI is the technique of passing dependencies in from outside, preferably through the constructor.
2. **Why constructor injection?** Final fields, always-valid objects, visible dependencies, testable with `new`, circular dependencies fail fast.
3. **BeanFactory vs ApplicationContext?** Minimal lazy container vs full container with eager singletons, events, Environment, message sources and automatic post-processor registration.
4. **How does Spring choose among multiple beans?** Qualifier filter, then primary, then fallback, then parameter name; otherwise `NoUniqueBeanDefinitionException`.
5. **`@Component` vs `@Service` vs `@Repository`?** All scanned; `@Repository` adds persistence exception translation; `@Service` is semantic.
6. **Why are `@Configuration` classes proxied?** So `@Bean` methods calling each other get the container's singleton; lite mode turns that off.
7. **What scopes exist and what is the prototype-in-singleton problem?** singleton, prototype, request, session, application; a prototype injected into a singleton is created only once, so use `ObjectProvider` or `@Lookup`.
8. **Describe the bean lifecycle.** Instantiate, inject, Aware, BPP before (`@PostConstruct`), `afterPropertiesSet`, initMethod, BPP after (proxy); on close `@PreDestroy`, `destroy`, destroyMethod.
9. **`@Value` vs `@ConfigurationProperties`?** Single value or SpEL vs typed, validated object for a prefix with relaxed binding.
10. **How does auto-configuration back off?** Auto-configuration beans are guarded by `@ConditionalOnMissingBean`, and auto-configurations are processed after user configuration, so your bean wins.

## Rapid-fire flashcards

Q: Since which Spring version is `@Autowired` optional on a single constructor?
A: Spring 4.3.

Q: What is the default bean name for a class `OrderService` annotated with `@Service`?
A: `orderService`.

Q: Which post-processor parses `@Configuration` classes and component scans?
A: `ConfigurationClassPostProcessor`.

Q: Which Spring scope does not get destroy callbacks?
A: Prototype.

Q: What exception does a request-scoped bean throw when used on a thread with no request?
A: `ScopeNotActiveException` (no thread-bound request found).

Q: Which runs first: `@PostConstruct` or `afterPropertiesSet()`?
A: `@PostConstruct`.

Q: How do you enable IDE autocompletion for your own `@ConfigurationProperties`?
A: Add the `spring-boot-configuration-processor` dependency to generate metadata.

Q: What prints the Spring Boot conditions evaluation report?
A: Starting the app with `--debug` (or `debug=true`).

Q: What is the default phase of `@TransactionalEventListener`?
A: `AFTER_COMMIT`.

Q: Which env var sets the property `orderhub.payment.provider`?
A: `ORDERHUB_PAYMENT_PROVIDER`.
