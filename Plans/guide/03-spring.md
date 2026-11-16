@@ 1 | MODULE | Phase 3 — Spring and Spring Boot | IoC/DI, Boot, REST APIs, JPA/Hibernate, transactions | MUST
# Phase 3: Spring and Spring Boot

Spring Boot is the **core backend skill** in the TOR. Expect questions on DI, bean lifecycle, auto-configuration, REST design, JPA and transactions.

@@ 2 | MODULE | Spring Core | IoC container, DI, beans, AOP | MUST
Spring Core is the engine underneath everything: it **creates your objects and wires them together** so you don't have to.

@@ 3 | TOPIC | IoC and Dependency Injection | Why Spring creates objects for you; constructor injection | MUST
# IoC and dependency injection

**IoC (Inversion of Control):** you don't create your dependencies with `new`. A **container** (Spring) creates them and hands them to you.
**DI (Dependency Injection):** the way IoC is done. Dependencies are "injected" through the constructor, a setter or a field.

**Analogy:** in a hotel you don't bring your own bed and towels; the hotel (the container) provides them. You just say what you need. You can swap a hard pillow for a soft one without changing yourself.

```java
// WITHOUT DI: tightly coupled
class LoanService {
    private final LoanRepository repo = new JdbcLoanRepository();   // stuck with this one
}

// WITH DI: loosely coupled
public interface LoanRepository { Loan save(Loan l); }

@Repository
class JpaLoanRepository implements LoanRepository { ... }

@Service
public class LoanService {
    private final LoanRepository repo;
    private final SmsSender sms;

    public LoanService(LoanRepository repo, SmsSender sms) {   // constructor injection
        this.repo = repo;
        this.sms = sms;
    }

    public Loan approve(Loan loan) {
        Loan saved = repo.save(loan);
        sms.send(loan.getPhone(), "Your loan is approved");
        return saved;
    }
}
```

**Line by line**
- `new JdbcLoanRepository()` hard-codes the implementation, so you can't swap it or mock it in tests.
- `@Repository` / `@Service`: tell Spring "create one object (a **bean**) of this class and manage it".
- Constructor parameters: Spring sees that LoanService needs a `LoanRepository` and an `SmsSender`, finds matching beans and passes them in.
- With a **single constructor**, `@Autowired` is optional.
- `private final`: dependencies can't change after construction (immutability).

## Three injection styles

| Style | Example | Verdict |
|---|---|---|
| **Constructor** | `LoanService(LoanRepository r)` | ✓ **Recommended**: final fields, easy to test, fails fast if missing |
| Setter | `@Autowired void setRepo(...)` | Optional dependencies only |
| Field | `@Autowired private LoanRepository repo;` | ✗ Hidden dependencies, can't be final, hard to test |

**Testing benefit:** `new LoanService(mockRepo, mockSms)` works in a plain unit test with no Spring at all.

## When two beans match
```java
@Service
public class NotificationService {
    public NotificationService(@Qualifier("smsSender") Sender sender) { ... }
}
// or mark one implementation @Primary
```
- With two `Sender` beans, Spring throws `NoUniqueBeanDefinitionException`. `@Qualifier` picks one by name, and `@Primary` sets the default.

**ApplicationContext** is the container that holds all the beans. `BeanFactory` is the basic version; ApplicationContext adds events, i18n and AOP.

@@ 3 | TOPIC | Bean Scopes and Lifecycle | singleton, prototype, request; @PostConstruct, @PreDestroy | MUST
# Bean scopes and lifecycle

## Scopes

| Scope | How many instances | Use |
|---|---|---|
| **singleton** (default) | One per container | Stateless services, repositories |
| prototype | A new one each time it is requested | Stateful helpers |
| request | One per HTTP request | Request-specific data |
| session | One per HTTP session | User session data |

**Analogy:** singleton = the office printer that everybody shares. Prototype = disposable paper cups, a new one each time.

**Warning:** singleton beans are shared by all threads (all users). **Never keep request-specific data in a field of a singleton.**

```java
@Service
public class BadService {
    private String currentUser;   // ✗ shared by ALL requests: user A can see user B's data
}
```

## Lifecycle

1. Instantiate (constructor)
2. Inject dependencies
3. `@PostConstruct` method runs
4. The bean is ready and used
5. On shutdown: `@PreDestroy` runs

```java
@Component
public class ExchangeRateCache {
    private final Map<String, BigDecimal> rates = new ConcurrentHashMap<>();
    private final RateClient client;

    public ExchangeRateCache(RateClient client) { this.client = client; }

    @PostConstruct
    void load() {                                // runs once after injection
        rates.putAll(client.fetchAll());
    }

    @PreDestroy
    void cleanup() {                             // runs on graceful shutdown
        rates.clear();
    }
}

@Component
@Scope("prototype")                              // a NEW instance every time it is requested
class ReportBuilder { ... }
```

**Line by line**
- The constructor runs **before** injection is complete for field/setter injection, so put "start-up work that needs dependencies" in `@PostConstruct`.
- `@PostConstruct`: warm up caches, validate configuration.
- `@PreDestroy`: close connections, flush buffers.
- `ConcurrentHashMap`: because the singleton is shared across threads.
- `@Scope("prototype")` on `ReportBuilder`: each injection or `getBean()` call creates a fresh object, which is safe for holding per-report state.

**Prototype inside a singleton:** the prototype is injected only **once**, when the singleton is created. To get a fresh one each time, inject `ObjectProvider<MyProto>` and call `getObject()`.

**Circular dependency:** A needs B and B needs A through constructors. Spring Boot fails at startup. Fix the design (extract a third class) instead of hacking around it with `@Lazy`.

@@ 3 | TOPIC | Stereotypes and Configuration | @Component, @Service, @Repository, @Controller, @Configuration, @Bean | MUST
# Stereotypes and configuration

## Stereotype annotations (put on your own classes)

| Annotation | Layer | Extra behaviour |
|---|---|---|
| `@Component` | Generic bean | none |
| `@Service` | Business logic | none (documents intent) |
| `@Repository` | Data access | Translates DB exceptions into Spring's `DataAccessException` |
| `@Controller` | Web MVC (returns views) | Request mapping |
| `@RestController` | REST API | `@Controller` + `@ResponseBody` (returns JSON) |

They're all found by **component scanning**: Spring scans the package of your `@SpringBootApplication` class and its sub-packages.

## @Configuration + @Bean (for classes you didn't write)

```java
@Configuration
public class AppConfig {

    @Bean
    public RestClient paymentClient(RestClient.Builder builder) {
        return builder.baseUrl("https://pay.example.com").build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Profile("dev")                         // only in the dev profile
    public SmsSender fakeSms() { return new ConsoleSmsSender(); }
}
```

**Line by line**
- `@Configuration`: "this class defines beans".
- `@Bean` method: its **return value** becomes a bean, and the method name (`paymentClient`) is the bean name.
- Parameters (like `RestClient.Builder`) are injected automatically.
- Use `@Bean` for **third-party classes** (you can't add `@Component` to library code) or when creation needs logic.
- `@Profile("dev")`: the bean exists only when the dev profile is active.

**Why `@Configuration` and not `@Component` for bean classes?** Spring proxies `@Configuration` classes so that calling one `@Bean` method from another returns the **same singleton** instead of creating a new object.

**Analogy:** `@Component` is a product that labels itself "please stock me". `@Bean` is the shop manager writing an order for a product made by another company.

@@ 3 | TOPIC | Spring AOP | Cross-cutting concerns, proxies, @Around advice | SHOULD
# Spring AOP (Aspect-Oriented Programming)

**AOP** adds the same behaviour (logging, security, transactions, timing) to many methods **without editing them**.

**Analogy:** airport security. Every passenger passes through the same checkpoint before boarding, but the planes and passengers don't contain any security code themselves.

Terms:
- **Aspect:** the class holding the cross-cutting logic.
- **Advice:** *what* to do and *when* (`@Before`, `@After`, `@AfterReturning`, `@AfterThrowing`, `@Around`).
- **Pointcut:** *where*: which methods (an expression).
- **Join point:** a point during execution (a method call) where the advice runs.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timed {}                          // our own marker annotation

@Aspect
@Component
@Slf4j
public class TimingAspect {

    @Around("@annotation(Timed)")                   // pointcut: methods marked @Timed
    public Object time(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();                   // run the real method
        } finally {
            log.info("{} took {} ms", pjp.getSignature().toShortString(),
                     System.currentTimeMillis() - start);
        }
    }
}

@Service
class ReportService {
    @Timed
    public Report buildParReport() { ... }          // automatically timed
}
```

**Line by line**
- `@interface Timed`: a custom annotation. `RUNTIME` retention is needed so that Spring can see it while the app runs.
- `@Aspect @Component`: registers the aspect as a bean.
- `@Around(...)`: wraps the method, so code runs **before and after**.
- `pjp.proceed()`: calls the original method. Forget it and the real method never runs.
- `finally`: logs the time even if the method throws.

## How it works: proxies
Spring wraps your bean in a **proxy** object. Callers talk to the proxy, which runs the advice and then calls your real method.

**Self-invocation trap:** if a method in the same class calls `this.buildParReport()`, the call **bypasses the proxy**, so no advice runs. The same applies to `@Transactional`, `@Cacheable` and `@Async`.

**Proxy types:** JDK dynamic proxy (when the bean implements an interface) or CGLIB (subclassing, the default in Boot). Because CGLIB subclasses, **final classes/methods and private methods can't be advised**.

@@ 2 | MODULE | Spring Boot Essentials | Auto-configuration, starters, properties, profiles, actuator | MUST
Spring Boot = Spring + **sensible defaults**. It removes most configuration so that you can start an app in minutes.

@@ 3 | TOPIC | How Spring Boot Works | @SpringBootApplication, starters, auto-configuration, embedded server | MUST
# How Spring Boot works

```java
@SpringBootApplication
public class LoanApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoanApplication.class, args);
    }
}
```

**Line by line**
- `@SpringBootApplication` = three annotations in one:
  - `@SpringBootConfiguration`: this is a configuration class.
  - `@EnableAutoConfiguration`: configure beans automatically based on the classpath.
  - `@ComponentScan`: find `@Component`/`@Service`/... in this package and its sub-packages.
- `SpringApplication.run(...)`: creates the ApplicationContext, runs auto-configuration, starts the **embedded Tomcat** on port 8080.

## Starters
A **starter** is one dependency that pulls in a tested set of libraries.
- `spring-boot-starter-web`: Spring MVC + Jackson + Tomcat.
- `spring-boot-starter-data-jpa`: Hibernate + Spring Data + HikariCP.
- `spring-boot-starter-security`, `-validation`, `-actuator`, `-test`, `-data-redis`.

**Analogy:** a starter is a **meal kit**: one box with every ingredient for the recipe, in quantities that work together.

## Auto-configuration
Boot looks at what's on the classpath and creates beans for you **only if you haven't defined your own**.
- PostgreSQL driver + `spring.datasource.url` present → it creates a `DataSource` (HikariCP pool).
- Jackson present → it configures an `ObjectMapper`.

```java
// Inside Spring Boot (simplified) — how auto-config decides
@AutoConfiguration
@ConditionalOnClass(DataSource.class)            // only if the class exists on the classpath
public class DataSourceAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean                    // only if YOU didn't define one
    public DataSource dataSource(DataSourceProperties props) { ... }
}
```
- `@ConditionalOnClass`: the library must be present.
- `@ConditionalOnMissingBean`: **your bean wins**. That's how you override defaults.
- See what was configured and why: run with `--debug` to get the "conditions evaluation report".
- The list of auto-configurations lives in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

**Analogy:** a smart home. When it detects that you plugged in a TV, it sets up the remote automatically, unless you've already set one up yourself.

**Embedded server:** Tomcat runs inside your JAR. Deploy with just `java -jar app.jar`, no separate server install.

@@ 3 | TOPIC | Configuration and Profiles | application.yml, @Value, @ConfigurationProperties, profiles | MUST
# Configuration and profiles

```yaml
# src/main/resources/application.yml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/loans
    username: app
    password: ${DB_PASSWORD}          # read from an environment variable
  jpa:
    hibernate:
      ddl-auto: validate              # never 'update' or 'create' in production
    open-in-view: false
loan:
  max-amount: 500000
  interest-rate: 0.12
  allowed-pos: [PO1, PO2]
```

```java
// Option 1: @Value for a single value
@Value("${loan.max-amount}")
private BigDecimal maxAmount;

// Option 2 (preferred): type-safe grouped properties
@ConfigurationProperties(prefix = "loan")
@Validated
public record LoanProperties(
        @NotNull BigDecimal maxAmount,
        @NotNull BigDecimal interestRate,
        List<String> allowedPos) {}

@SpringBootApplication
@ConfigurationPropertiesScan          // enables scanning for @ConfigurationProperties
public class LoanApplication { ... }

@Service
class LoanService {
    LoanService(LoanProperties props) { ... props.maxAmount() ... }
}
```

**Line by line**
- `${DB_PASSWORD}`: **never commit secrets**. Read them from environment variables or a secret store.
- `ddl-auto: validate`: Hibernate checks that the tables match the entities but never changes them. Use Flyway/Liquibase for schema changes.
- `open-in-view: false`: closes the DB session after the service layer (avoids hidden lazy queries in controllers).
- `@Value("${...}")`: fine for one or two values.
- `@ConfigurationProperties`: binds a whole group into an object. It is type-safe, validated with `@Validated`, and `max-amount` maps to `maxAmount` automatically (relaxed binding).

## Profiles: different settings per environment

```
application.yml          # shared defaults
application-dev.yml      # local DB, debug logs
application-prod.yml     # real DB, strict settings
```
Activate with `SPRING_PROFILES_ACTIVE=prod` or `--spring.profiles.active=prod`.

**Analogy:** profiles are like clothes for different occasions. It's the same person (the app), dressed for home (dev), office (test) or a wedding (prod).

**Property precedence (highest wins):** command-line args → environment variables → `application-{profile}.yml` → `application.yml`. That's why Docker/K8s can override anything with environment variables (`SPRING_DATASOURCE_URL`).

@@ 3 | TOPIC | Actuator and Logging | Health checks, metrics, SLF4J and log levels | MUST
# Actuator and logging

## Actuator: production-ready endpoints

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true          # /actuator/health/liveness and /readiness for Kubernetes
```

| Endpoint | Shows |
|---|---|
| `/actuator/health` | UP/DOWN (DB, disk, Redis...) |
| `/actuator/metrics` | JVM memory, HTTP request counts and timings |
| `/actuator/prometheus` | Metrics in Prometheus format (needs `micrometer-registry-prometheus`) |
| `/actuator/info` | App version and build info |

- **Security:** only expose what you need, and protect the rest. `/env` and `/heapdump` can leak secrets.
- **Analogy:** Actuator is the car's dashboard: fuel, temperature, speed, warning lights.

## Logging with SLF4J (Logback underneath)

```java
@Slf4j                                     // Lombok creates: private static final Logger log = ...
@Service
public class RepaymentService {
    public void record(Long loanId, BigDecimal amount) {
        log.info("Recording repayment loanId={} amount={}", loanId, amount);
        try {
            ...
        } catch (Exception e) {
            log.error("Repayment failed for loanId={}", loanId, e);   // last arg = the exception (stack trace)
            throw e;
        }
    }
}
```

**Line by line**
- `{}` placeholders: the string is only built if that level is enabled, which is faster than `"..." + x`.
- Pass the exception as the **last argument** to get the full stack trace.
- **Never log passwords, tokens or full national IDs.**

**Levels (low → high):** TRACE < DEBUG < INFO < WARN < ERROR. Setting INFO shows INFO, WARN and ERROR.

```yaml
logging:
  level:
    root: INFO
    bd.org.pksf: DEBUG
    org.hibernate.SQL: DEBUG     # print SQL (dev only)
```

In production, log in **JSON** with a **correlation/trace id** so that logs from many services can be searched together (ELK, Loki).

@@ 3 | TOPIC | Project Structure | Layered architecture: controller, service, repository, DTO | MUST
# Project structure

## Layered architecture

```
Client → Controller → Service → Repository → Database
          (HTTP)     (business)   (data)
```

| Layer | Responsibility | Must NOT |
|---|---|---|
| **Controller** | HTTP: parse the request, validate, call the service, return the response | Contain business rules or SQL |
| **Service** | Business logic, transactions | Know about HTTP (no `HttpServletRequest`) |
| **Repository** | Database access | Contain business rules |
| **DTO** | The shape of request/response data | Be a JPA entity |
| **Entity** | Maps a database table | Be returned directly from the API |

**Analogy: a restaurant.** The waiter (controller) takes the order, the chef (service) cooks by the recipe, and the storeroom keeper (repository) fetches ingredients. A waiter who starts cooking makes a mess.

## Package by feature (recommended)

```
bd.org.pksf.loan
├── LoanApplication.java
├── common/            (exceptions, config, security)
├── member/
│   ├── MemberController.java
│   ├── MemberService.java
│   ├── MemberRepository.java
│   ├── Member.java            (entity)
│   └── dto/MemberRequest.java, MemberResponse.java
└── loan/
    ├── LoanController.java
    ├── LoanService.java
    └── ...
```

- **Package by feature** keeps everything about "loan" together. That makes it easier to find code and later split it into a microservice.
- **Package by layer** (`controllers/`, `services/`...) scatters one feature across many folders.

**Why not return entities from controllers?**
1. **Security:** you may leak fields (password hash, internal flags).
2. **Lazy loading** errors or infinite JSON loops (Member → Loans → Member...).
3. **Coupling:** renaming a DB column would break the API contract.

@@ 2 | MODULE | Building REST APIs | Controllers, DTOs, validation, error handling, OpenAPI | MUST
How to build clean, validated, well-documented REST endpoints with Spring MVC.

@@ 3 | TOPIC | REST Controller CRUD | @RestController, mappings, ResponseEntity, pagination | MUST
# A REST controller for CRUD

```java
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor                     // Lombok: constructor for final fields
public class MemberController {

    private final MemberService service;

    @GetMapping
    public Page<MemberResponse> list(@RequestParam(required = false) String name,
                                     @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.search(name, pageable);
    }

    @GetMapping("/{id}")
    public MemberResponse get(@PathVariable Long id) {
        return service.get(id);              // throws NotFoundException -> 404
    }

    @PostMapping
    public ResponseEntity<MemberResponse> create(@Valid @RequestBody MemberRequest req) {
        MemberResponse created = service.create(req);
        URI location = URI.create("/api/v1/members/" + created.id());
        return ResponseEntity.created(location).body(created);   // 201 + Location header
    }

    @PutMapping("/{id}")
    public MemberResponse update(@PathVariable Long id, @Valid @RequestBody MemberRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)   // 204
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
```

**Line by line**
- `@RestController`: every method's return value is written as JSON (via Jackson).
- `@RequestMapping("/api/v1/members")`: the base URL. `v1` = API versioning.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: map the HTTP methods.
- `@PathVariable Long id`: takes `42` from `/members/42`.
- `@RequestParam`: reads `?name=rahim`. `required = false` makes it optional.
- `Pageable`: Spring builds it from `?page=0&size=20&sort=name,asc`. `Page<T>` returns the content plus totals.
- `@Valid @RequestBody`: turn the JSON body into a `MemberRequest`, **then validate it**. Invalid input → 400.
- `ResponseEntity.created(location)`: status **201 Created** plus a `Location` header pointing to the new resource.
- `@ResponseStatus(NO_CONTENT)`: a successful delete with no body → **204**.

**Analogy:** the controller is a receptionist who checks your form is filled in properly, sends it to the right department, and hands you back a receipt.

@@ 3 | TOPIC | DTOs and Validation | Bean Validation annotations and custom validators | MUST
# DTOs and validation

A **DTO (Data Transfer Object)** defines exactly what the API accepts or returns, separate from the database entity.
**Analogy:** a bank deposit slip. It asks only for what's needed. You don't hand the clerk your whole bank file.

```java
public record MemberRequest(
        @NotBlank @Size(max = 100)           String name,
        @NotBlank @Pattern(regexp = "\\d{10}|\\d{13}|\\d{17}",
                           message = "NID must be 10, 13 or 17 digits")
                                             String nationalId,
        @NotBlank @Pattern(regexp = "01[3-9]\\d{8}", message = "Invalid BD mobile")
                                             String phone,
        @NotNull @Past                       LocalDate dateOfBirth,
        @NotNull @Positive                   Long samityId) {}

public record LoanRequest(
        @NotNull Long memberId,
        @NotNull @DecimalMin("1000.00") @DecimalMax("500000.00")
        @Digits(integer = 12, fraction = 2)  BigDecimal amount,
        @Min(1) @Max(60)                     int months) {}

public record MemberResponse(Long id, String name, String phone, String samityName) {
    public static MemberResponse from(Member m) {
        return new MemberResponse(m.getId(), m.getName(), m.getPhone(), m.getSamity().getName());
    }
}
```

**Line by line**
- `@NotBlank`: not null and not just spaces (for Strings). `@NotNull`: just not null (for any type).
- `@Size(max = 100)`: limits string length (match it to the DB column size).
- `@Pattern(regexp = ...)`: must match the regex. `message` customises the error text.
- `@Past`: the date must be before today.
- `@DecimalMin/@DecimalMax`: a range for BigDecimal. `@Digits`: maximum integer and fraction digits.
- `@Min/@Max`: a range for whole numbers.
- `MemberResponse.from(entity)`: a small **mapper** that converts entity → DTO and exposes only safe fields. MapStruct can generate mappers for big projects.
- Validation only runs when the parameter is marked `@Valid` (or the class is `@Validated`).

## Custom validator (business rule)

```java
@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BdPhoneValidator.class)
public @interface BdPhone {
    String message() default "Invalid Bangladeshi phone";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class BdPhoneValidator implements ConstraintValidator<BdPhone, String> {
    public boolean isValid(String v, ConstraintValidatorContext ctx) {
        return v == null || v.matches("01[3-9]\\d{8}");   // null is handled by @NotNull
    }
}
```
- `@Constraint(validatedBy = ...)`: links the annotation to the class that checks it.
- `message`, `groups` and `payload` are required boilerplate for every constraint annotation.
- Returning `true` for null is the convention: let `@NotNull` decide whether null is allowed.

@@ 3 | TOPIC | Global Exception Handling | @RestControllerAdvice and ProblemDetail error responses | MUST
# Global exception handling

Instead of try/catch in every controller, handle errors **in one place** and return a consistent JSON error.

**Analogy:** a hospital's emergency desk. Whatever goes wrong anywhere in the building, patients are routed to one desk that follows a standard procedure.

```java
public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg) { super(msg); }
}
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String msg) { super(msg); }
}

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail notFound(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail rule(BusinessRuleException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail conflict() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Record was changed by someone else. Reload and try again.");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception ex) {
        log.error("Unexpected error", ex);                     // full details in the LOG
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong");                        // generic message to the CLIENT
    }
}
```

**Line by line**
- `@RestControllerAdvice`: applies to **all** controllers and returns JSON.
- `@ExceptionHandler(X.class)`: "when X is thrown anywhere, run this method".
- `ProblemDetail` (Spring 6): the standard **RFC 7807** error format: `type`, `title`, `status`, `detail`, `instance`.
- `MethodArgumentNotValidException`: thrown when `@Valid` fails. We collect each field's error into a map.
- `ObjectOptimisticLockingFailureException`: two users edited the same record, so return **409 Conflict**.
- Catch-all `Exception`: log the details, but **never send the stack trace to the client** (security).
- Spring picks the **most specific** handler first.

Example response:
```json
{ "type": "about:blank", "title": "Validation failed", "status": 400,
  "errors": { "phone": "Invalid BD mobile", "name": "must not be blank" } }
```

@@ 3 | TOPIC | OpenAPI, File Upload and HTTP Clients | Swagger docs, MultipartFile, RestClient | SHOULD
# OpenAPI, file upload and HTTP clients

## OpenAPI / Swagger: auto-generated API docs

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.6.0</version>
</dependency>
```
```java
@Operation(summary = "Create a member", description = "Registers a new member in a samity")
@ApiResponse(responseCode = "201", description = "Created")
@PostMapping
public ResponseEntity<MemberResponse> create(@Valid @RequestBody MemberRequest req) { ... }
```
- Open `/swagger-ui.html` to get interactive docs where you can try requests. The raw spec is at `/v3/api-docs`.
- **Analogy:** a restaurant menu with photos. Clients know exactly what they can order and what arrives.

## File upload

```java
@PostMapping(value = "/members/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public void uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
    if (file.isEmpty()) throw new BusinessRuleException("Empty file");
    if (!List.of("image/jpeg", "image/png").contains(file.getContentType()))
        throw new BusinessRuleException("Only JPG/PNG allowed");
    Path target = Path.of("uploads", id + ".jpg");
    Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
}
```
- `consumes = MULTIPART_FORM_DATA`: the request is a form upload, not JSON.
- **Validate the type and size.** Never trust the original file name (path traversal: `../../etc/passwd`). Generate your own name.
- Size limits: `spring.servlet.multipart.max-file-size=5MB`.

## Calling other APIs: RestClient (Spring 6.1+)

```java
@Service
public class PaymentGatewayClient {
    private final RestClient client;

    public PaymentGatewayClient(RestClient.Builder builder) {
        this.client = builder.baseUrl("https://gateway.example.com").build();
    }

    public PaymentResult charge(PaymentRequest req) {
        return client.post()
                .uri("/payments")
                .header("Idempotency-Key", req.reference())
                .body(req)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                          (rq, rs) -> { throw new BusinessRuleException("Payment rejected"); })
                .body(PaymentResult.class);
    }
}
```
- `RestClient`: a modern, fluent, **synchronous** client (it replaces the old `RestTemplate`).
- `.body(req)` serialises the object to JSON. `.body(PaymentResult.class)` parses the response.
- `onStatus`: turn HTTP errors into your own exceptions.
- `Idempotency-Key`: lets the gateway detect a retried request so that it doesn't charge twice.
- `WebClient`: the reactive/non-blocking alternative. **OpenFeign**: declarative clients (an interface + annotations) used in microservices.
- **Always set timeouts** (connect/read), or a slow remote API can hang all your threads.

@@ 2 | MODULE | Spring Data JPA and Hibernate | Entities, relationships, repositories, N+1, transactions, locking | MUST
**JPA** is the standard Java API for mapping objects to tables (ORM). **Hibernate** is the implementation. **Spring Data JPA** generates repositories for you.
**Analogy:** ORM is a translator between the Java world (objects) and the SQL world (tables).

@@ 3 | TOPIC | Entity Mapping | @Entity, @Id, @Column, enums, auditing fields | MUST
# Entity mapping

```java
@Entity
@Table(name = "loan")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String loanNo;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal principal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status = LoanStatus.PENDING;

    private LocalDate disbursedOn;

    @Version
    private Long version;                          // optimistic locking

    @CreationTimestamp
    private Instant createdAt;

    @Transient
    private BigDecimal displayOnly;                // not stored in the DB
}
```

**Line by line**
- `@Entity`: this class maps to a table. `@Table(name = "loan")`: the exact table name.
- JPA **needs a no-arg constructor** (Hibernate creates objects by reflection). `PROTECTED` hides it from normal code.
- `@Id`: the primary key.
- `GenerationType.IDENTITY`: the DB auto-increments (`BIGSERIAL`). `SEQUENCE` uses a DB sequence and is better for batch inserts in Postgres.
- `@Column(nullable=false, unique=true, length=30)`: documents the constraints (the real ones should be in your migration SQL).
- `precision = 14, scale = 2`: `NUMERIC(14,2)` for money.
- `@Enumerated(EnumType.STRING)`: stores `"ACTIVE"`, not `1`. ORDINAL breaks if you reorder the enum.
- `@Version`: Hibernate increments it on each update and rejects stale updates (see Locking).
- `@CreationTimestamp`: filled automatically on insert.
- `@Transient`: a field that is not persisted.

**Entity equality tip:** don't use Lombok `@Data` on entities. Its generated `equals`/`hashCode`/`toString` touch lazy relations, which causes extra queries or infinite loops.

## Spring Data auditing (who and when)

```java
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class Auditable {
    @CreatedDate     private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;
    @CreatedBy       private String createdBy;
    @LastModifiedBy  private String updatedBy;
}
// + @EnableJpaAuditing and an AuditorAware<String> bean that returns the logged-in username
```
- `@MappedSuperclass`: its fields are copied into each subclass's table, so there is no separate table.
- Every entity that `extends Auditable` gets automatic audit columns, which is important for financial data.

@@ 3 | TOPIC | Relationships | @ManyToOne, @OneToMany, @ManyToMany, owning side, cascade | MUST
# Relationships

```java
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "samity_id")                       // FK column in the member table
    private Samity samity;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();

    public void addLoan(Loan loan) {                      // helper keeps BOTH sides in sync
        loans.add(loan);
        loan.setMember(this);
    }
}

@Entity
public class Loan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;                                // OWNING side (has the FK)
}
```

**Line by line**
- `@ManyToOne`: many members belong to one samity. The **FK lives on this side** (`samity_id`).
- `fetch = LAZY`: don't load the Samity until it's accessed. **The default for @ManyToOne is EAGER, so always set LAZY.**
- `@JoinColumn(name = "member_id")`: the FK column name.
- `@OneToMany(mappedBy = "member")`: the **inverse side**. "The FK is managed by the `member` field in Loan." It has no column of its own.
- **Owning side** = the side with the FK (the `@ManyToOne`). Only changes on the owning side are saved. That's why `addLoan` sets **both** sides.
- `cascade = ALL`: saving or deleting a member also saves or deletes its loans.
- `orphanRemoval = true`: removing a loan from the list deletes it from the DB.

**Analogy:** in a school, each student's form has a "class teacher" field (the owning side, the FK). The teacher's list of students is just a view built from those forms.

| Annotation | Default fetch |
|---|---|
| `@ManyToOne`, `@OneToOne` | **EAGER** (change it to LAZY!) |
| `@OneToMany`, `@ManyToMany` | LAZY |

## Many-to-many
```java
@ManyToMany
@JoinTable(name = "user_role",
           joinColumns = @JoinColumn(name = "user_id"),
           inverseJoinColumns = @JoinColumn(name = "role_id"))
private Set<Role> roles = new HashSet<>();
```
- This creates a join table. If the link needs extra columns (e.g. `assigned_at`), model the join table as its own entity with two `@ManyToOne` fields instead.

**Careful with cascade REMOVE on financial data:** you usually never delete loans. Use a status (`CLOSED`) or soft delete.

@@ 3 | TOPIC | Spring Data Repositories | Derived queries, @Query, projections, Specifications | MUST
# Spring Data repositories

You write an **interface**, and Spring generates the implementation at runtime.

```java
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // 1. Derived queries: SQL is generated from the method name
    List<Loan> findByStatus(LoanStatus status);
    Optional<Loan> findByLoanNo(String loanNo);
    List<Loan> findByMemberIdAndStatusOrderByDisbursedOnDesc(Long memberId, LoanStatus status);
    boolean existsByLoanNo(String loanNo);
    long countByStatus(LoanStatus status);
    Page<Loan> findByPrincipalGreaterThan(BigDecimal min, Pageable pageable);

    // 2. JPQL: queries on ENTITIES and fields, not tables
    @Query("select l from Loan l where l.member.samity.id = :samityId and l.status = 'ACTIVE'")
    List<Loan> activeInSamity(@Param("samityId") Long samityId);

    // 3. Native SQL for Postgres-specific features
    @Query(value = """
            SELECT po_id, SUM(outstanding) AS total
            FROM loan WHERE status = 'ACTIVE' GROUP BY po_id
            """, nativeQuery = true)
    List<PoOutstanding> outstandingPerPo();

    // 4. Update query
    @Modifying
    @Query("update Loan l set l.status = 'OVERDUE' where l.nextDueDate < :today and l.status = 'ACTIVE'")
    int markOverdue(@Param("today") LocalDate today);
}

public interface PoOutstanding {          // interface projection: only these columns
    Long getPoId();
    BigDecimal getTotal();
}
```

**Line by line**
- `JpaRepository<Loan, Long>`: entity type and ID type. You get `save`, `findById`, `findAll(Pageable)`, `deleteById`, `count`... for free.
- `findByStatus`: Spring parses the name: find... By... `Status` → `WHERE status = ?`.
- `And`, `Or`, `OrderBy...Desc`, `GreaterThan`, `Between`, `Like`, `In`, `IsNull`: keywords in method names.
- `member.samity.id` in JPQL: navigates relationships. Hibernate writes the joins.
- `:samityId` + `@Param`: a **named parameter**. Values are bound safely (no SQL injection).
- `nativeQuery = true`: raw SQL. It is DB-specific but lets you use window functions, CTEs, etc.
- `@Modifying`: required for UPDATE/DELETE queries. It needs a transaction. Returns the number of rows changed.
- **Projection interface** (`PoOutstanding`): reads only the needed columns, which is faster than loading full entities.

**Analogy:** a derived query is like telling a librarian "find books by author Rahman ordered by year". The sentence itself is the query.

## Dynamic filters: Specifications
```java
public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {}

static Specification<Loan> hasStatus(LoanStatus s) {
    return (root, query, cb) -> s == null ? null : cb.equal(root.get("status"), s);
}
static Specification<Loan> minAmount(BigDecimal min) {
    return (root, query, cb) -> min == null ? null : cb.ge(root.get("principal"), min);
}
repo.findAll(hasStatus(status).and(minAmount(min)), pageable);
```
- Each Specification is one optional WHERE condition. Returning `null` means "no filter". Combine them with `and`/`or` for search screens with many optional filters.

@@ 3 | TOPIC | N+1 Problem and Fetching | Lazy loading, JOIN FETCH, @EntityGraph, DTO projections | MUST
# The N+1 problem

You load **N** parents with 1 query, then touching each parent's lazy relation fires **1 more query per parent**: **N+1 queries** in total.

**Analogy:** you need the phone numbers of 100 students. Instead of asking the office once for the full list, you walk to the office 100 times, once per student.

```java
// Service code that looks innocent
List<Loan> loans = loanRepo.findAll();                 // 1 query
for (Loan l : loans) {
    System.out.println(l.getMember().getName());       // +1 query PER loan (lazy load)
}
// 100 loans -> 101 queries!
```

## Fixes

```java
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Fix 1: JOIN FETCH loads loans + members in ONE query
    @Query("select l from Loan l join fetch l.member where l.status = :status")
    List<Loan> findWithMember(@Param("status") LoanStatus status);

    // Fix 2: @EntityGraph does the same declaratively
    @EntityGraph(attributePaths = {"member", "member.samity"})
    List<Loan> findByStatus(LoanStatus status);

    // Fix 3: a DTO projection selects only what's needed (best for read-only screens)
    @Query("select new bd.org.pksf.loan.LoanRow(l.id, l.loanNo, m.name, l.principal) " +
           "from Loan l join l.member m where l.status = :status")
    List<LoanRow> rows(@Param("status") LoanStatus status);
}
```
```yaml
# Fix 4: batch fetching loads lazy relations in groups of 50 with IN (...)
spring.jpa.properties.hibernate.default_batch_fetch_size: 50
```

**Line by line**
- `join fetch l.member`: a SQL JOIN **and** fills in the `member` field, so there is no extra query later.
- `@EntityGraph(attributePaths = ...)`: "also load these relations eagerly, for this query only".
- `select new ...LoanRow(...)`: builds DTOs directly from the query. Nothing is managed by Hibernate, so it is the fastest option for lists and reports.
- `default_batch_fetch_size`: if you still lazy-load, Hibernate loads 50 members with one `WHERE id IN (...)` instead of 50 single queries.

**Careful:** `join fetch` of a **collection** combined with pagination makes Hibernate paginate in memory (warning HHH90003004). For pages, fetch the parent IDs first, or use batch fetching.

**How to detect N+1:** turn on SQL logging (`spring.jpa.show-sql=true` or `logging.level.org.hibernate.SQL=DEBUG`) and count the queries, or use Hibernate statistics.

**LazyInitializationException:** you touched a lazy relation **after the transaction/session closed** (e.g. in the controller with `open-in-view: false`). Fix it by fetching what you need in the service (JOIN FETCH/DTO), not by switching to EAGER.

@@ 3 | TOPIC | Transactions | @Transactional, propagation, rollback rules, read-only | MUST
# Transactions with @Transactional

```java
@Service
@RequiredArgsConstructor
public class RepaymentService {

    private final LoanRepository loans;
    private final TransactionRepository txns;
    private final AuditService audit;

    @Transactional                                   // all or nothing
    public void repay(Long loanId, BigDecimal amount) {
        Loan loan = loans.findById(loanId).orElseThrow(() -> new NotFoundException("Loan"));
        if (amount.compareTo(loan.getOutstanding()) > 0)
            throw new BusinessRuleException("Amount exceeds outstanding");

        loan.setOutstanding(loan.getOutstanding().subtract(amount));   // no save() needed: dirty checking
        txns.save(new LedgerEntry(loan, amount, "REPAYMENT"));
        audit.log("REPAY", loanId);                  // runs in its own transaction (REQUIRES_NEW)
    }

    @Transactional(readOnly = true)                  // optimisation for reads
    public LoanResponse get(Long id) { ... }
}

@Service
class AuditService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, Long id) { ... } // saved even if the caller later rolls back
}
```

**Line by line**
- `@Transactional`: Spring's proxy starts a transaction before the method and **commits** after it. If a `RuntimeException` escapes, it **rolls back**.
- Updating the outstanding and inserting the ledger entry happen together or not at all. **Analogy:** a cashier only gives the receipt once the money is actually in the drawer. Either both happen or neither.
- `loan.setOutstanding(...)` with no `save()`: the loan is **managed**, so Hibernate notices the change (**dirty checking**) and issues an UPDATE on commit.
- `readOnly = true`: Hibernate skips dirty checking, and the DB may optimise. Use it for queries.
- `REQUIRES_NEW`: suspends the outer transaction and runs in a new one. The audit row stays even if the repayment fails.

## Propagation (what happens if a transaction already exists?)

| Propagation | Behaviour |
|---|---|
| **REQUIRED** (default) | Join the existing one, or create a new one |
| REQUIRES_NEW | Always a new one (suspend the outer) |
| SUPPORTS | Join if one exists, otherwise run without |
| MANDATORY | Must already exist, otherwise throw |
| NOT_SUPPORTED | Run without a transaction (suspend the outer) |
| NEVER | Throw if one exists |
| NESTED | A savepoint inside the outer transaction |

## Rollback rules (a classic trap)
- By default it rolls back on **unchecked** exceptions (RuntimeException, Error) only.
- **Checked exceptions do NOT roll back.** Use `@Transactional(rollbackFor = Exception.class)` if needed.
- If you catch the exception inside the method and don't rethrow it, the transaction **commits**.

## Why @Transactional sometimes "doesn't work"
1. **Self-invocation:** `this.repay()` from the same class skips the proxy.
2. The method is **private** (proxies only intercept public methods).
3. The exception is checked or swallowed.
4. The class isn't a Spring bean (created with `new`).

**Isolation:** `@Transactional(isolation = Isolation.SERIALIZABLE)`. The default uses the DB default (Read Committed in Postgres).

@@ 3 | TOPIC | Locking | Optimistic (@Version) vs pessimistic (SELECT FOR UPDATE) | MUST
# Locking: preventing lost updates

**Problem:** two officers open the same loan (outstanding 5,000). Both record 1,000 repayments. Both save 4,000. **One payment is lost.**

## Optimistic locking: "assume no conflict, check at the end"

```java
@Entity
public class Loan {
    @Version
    private Long version;
    ...
}
```
What Hibernate runs:
```sql
UPDATE loan SET outstanding = 4000, version = 6
WHERE id = 42 AND version = 5;      -- 0 rows updated if someone else already changed it
```
- If 0 rows are updated, Hibernate throws `ObjectOptimisticLockingFailureException`, which we map to **409 Conflict** ("reload and try again").
- No DB locks are held, so it scales well. Best when conflicts are **rare**.
- **Analogy:** editing a shared Google Doc offline. When you sync, if someone else changed it first, you're asked to merge.

## Pessimistic locking: "lock first, then work"

```java
public interface LoanRepository extends JpaRepository<Loan, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Loan l where l.id = :id")
    Optional<Loan> findByIdForUpdate(@Param("id") Long id);
}

@Transactional
public void repay(Long id, BigDecimal amt) {
    Loan loan = repo.findByIdForUpdate(id).orElseThrow();   // SELECT ... FOR UPDATE
    loan.setOutstanding(loan.getOutstanding().subtract(amt));
}                                                            // lock released on commit
```
- `PESSIMISTIC_WRITE` → SQL `SELECT ... FOR UPDATE`: other transactions trying to lock that row **wait**.
- It must run inside a transaction, because the lock is held until commit/rollback.
- Best when conflicts are **frequent** or the cost of a conflict is high (account balances).
- Risk: waiting and deadlocks. Keep the transactions short. You can set a lock timeout hint.
- **Analogy:** taking the only key to the record room. Others wait outside until you return it.

| | Optimistic | Pessimistic |
|---|---|---|
| Mechanism | Version column check | DB row lock |
| Conflicts | Detected at commit → retry | Prevented by waiting |
| Best for | Low contention, web forms | High contention, balances |

@@ 3 | TOPIC | Persistence Context and Entity States | Managed, detached, dirty checking, first-level cache | SHOULD
# Persistence context and entity states

The **persistence context** (Hibernate `Session` / JPA `EntityManager`) is a **working area** that tracks the entities loaded in the current transaction.

**Analogy:** a shopping cart. Items in the cart (managed entities) are tracked. When you check out (commit), every change is billed (flushed to the DB).

## Entity states

| State | Meaning |
|---|---|
| **Transient (new)** | `new Loan()`, not known to Hibernate |
| **Managed** | Loaded or saved in the current context, so changes are tracked |
| **Detached** | Was managed, but the context closed (e.g. after the transaction) |
| **Removed** | Scheduled for deletion on flush |

```java
@Transactional
public void demo() {
    Loan a = repo.findById(1L).orElseThrow();   // SELECT, a is MANAGED
    Loan b = repo.findById(1L).orElseThrow();   // no SQL: first-level cache, a == b
    a.setStatus(LoanStatus.CLOSED);             // just a Java change...
}                                               // commit -> flush -> UPDATE loan SET status='CLOSED'
```

**Line by line**
- The second `findById` comes from the **first-level cache** (the context): the same object, no query.
- `setStatus`: no explicit save. At flush, Hibernate compares each managed entity with the snapshot it took at load time (**dirty checking**) and writes the differences.
- After the method ends the entity becomes **detached**. Changing it then does nothing unless you `save()` (merge) it again.

**Flush** = send pending SQL to the DB (it happens before commit, before some queries, or on `flush()`). **Commit** = make it permanent.

`save()` on a new entity → `persist` (INSERT). On a detached entity → `merge` (copy its state into a managed instance).

@@ 3 | TOPIC | Migrations with Flyway and Batching | Versioned schema changes and bulk inserts | MUST
# Flyway migrations and batching

## Flyway: version control for your database schema

Never let Hibernate create production tables (`ddl-auto=update`). Write **SQL migration files** that run in order and are tracked.

```
src/main/resources/db/migration/
├── V1__create_member.sql
├── V2__create_loan.sql
└── V3__add_loan_due_date_index.sql
```
```sql
-- V2__create_loan.sql
CREATE TABLE loan (
    id          BIGSERIAL PRIMARY KEY,
    loan_no     VARCHAR(30) NOT NULL UNIQUE,
    member_id   BIGINT NOT NULL REFERENCES member(id),
    principal   NUMERIC(14,2) NOT NULL CHECK (principal > 0),
    outstanding NUMERIC(14,2) NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    version     BIGINT NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX ix_loan_member ON loan(member_id);
```

**Line by line**
- File name `V2__create_loan.sql`: `V` + version + **two underscores** + description.
- At startup, Flyway checks the `flyway_schema_history` table and runs only the files not yet applied, **in order**.
- **Never edit an applied migration** (its checksum would fail). Add a new V4 file instead.
- `CHECK`, `REFERENCES`, `UNIQUE`, `NOT NULL`: the database enforces the rules even if the app has a bug.
- **Analogy:** like Git commits for the database. Everyone (dev, test, prod) replays the same history and ends with the same schema.

## Batch inserts

```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc.batch_size: 50
        order_inserts: true
        order_updates: true
```
```java
@Transactional
public void importInstallments(List<Installment> list) {
    for (int i = 0; i < list.size(); i++) {
        em.persist(list.get(i));
        if (i % 50 == 0) { em.flush(); em.clear(); }   // send a batch, free memory
    }
}
```
- `batch_size: 50`: Hibernate sends 50 INSERTs in one network round trip instead of 50 trips.
- `flush()` + `clear()`: write the batch and empty the persistence context, so 1 million entities don't fill memory.
- **IDENTITY ids disable insert batching** (Hibernate must get each id immediately). Use `GenerationType.SEQUENCE` with `allocationSize = 50` for batch imports.
- For very large loads, use JDBC `batchUpdate` or PostgreSQL `COPY`.

@@ 2 | TOPIC | Other Spring Capabilities | @Scheduled, @Async, events, caching, Spring Batch | SHOULD
# Other Spring capabilities

```java
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
public class LoanApplication { ... }

@Component
@RequiredArgsConstructor
class OverdueJob {
    private final LoanRepository repo;

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Dhaka")    // every day at 01:00
    @Transactional
    public void markOverdue() {
        int n = repo.markOverdue(LocalDate.now());
        log.info("Marked {} loans overdue", n);
    }
}

@Service
class NotificationService {
    @Async                                                   // runs on another thread
    public CompletableFuture<Void> sendSms(String phone, String text) {
        smsGateway.send(phone, text);
        return CompletableFuture.completedFuture(null);
    }
}

@Service
class PoService {
    @Cacheable(value = "po", key = "#id")                    // cache the result by id
    public PoDto get(Long id) { return mapper.toDto(repo.findById(id).orElseThrow()); }

    @CacheEvict(value = "po", key = "#id")                   // remove from the cache on update
    public void update(Long id, PoRequest req) { ... }
}
```

**Line by line**
- `@Scheduled(cron = "0 0 1 * * *")`: the fields are **second minute hour day month weekday**. This means 01:00:00 every day. `fixedRate = 60000` means every 60 seconds.
- `zone = "Asia/Dhaka"`: without it the server's time zone is used.
- With **several app instances**, every instance runs the job. Use **ShedLock** (a DB lock) so that only one does.
- `@Async`: the caller returns immediately and the method runs in a thread pool. Configure the pool (`ThreadPoolTaskExecutor`), otherwise the defaults may not suit you.
- `@Cacheable`: the first call runs the method and stores the result. Later calls with the same key return the cached value without running the method.
- `@CacheEvict`: delete the stale entry when data changes. The backing store can be Redis (see Phase 7).
- All of these use **proxies**, so self-invocation won't trigger them.

**Spring events:** `ApplicationEventPublisher.publishEvent(...)` + `@EventListener`. `@TransactionalEventListener(phase = AFTER_COMMIT)` runs only if the transaction committed, which is perfect for "send SMS after the payment is saved".

**Spring Batch:** for large jobs such as "import 1 million installments from CSV every night". It uses chunks (read → process → write 1000 at a time) and supports restart from failure.

@@ 2 | TOPIC | Phase 3 — Interview Questions | Quick Q&A revision for Spring and Spring Boot | MUST
# Phase 3 interview questions

**Q. What is IoC and DI?**
A. IoC: the container creates and wires objects instead of your code. DI is how it does that: dependencies are passed in (preferably through the constructor), which makes code loosely coupled and testable.

**Q. Why prefer constructor injection?**
A. Dependencies can be final, they are visible in the signature, the object is never half-built, it is easy to unit test with `new`, and circular dependencies show up at startup.

**Q. What does @SpringBootApplication do?**
A. It combines @SpringBootConfiguration, @EnableAutoConfiguration and @ComponentScan.

**Q. How does auto-configuration work?**
A. Boot loads its auto-configuration classes. Each one is guarded by conditions such as @ConditionalOnClass and @ConditionalOnMissingBean, so it creates beans only when the library is present and you haven't defined your own.

**Q. @Component vs @Bean?**
A. @Component goes on your own class (found by scanning). @Bean goes on a method in a @Configuration class and is used for third-party objects or custom creation logic.

**Q. Default bean scope? Is it thread-safe?**
A. Singleton. It is not automatically thread-safe, so keep singletons stateless.

**Q. @Controller vs @RestController?**
A. @RestController = @Controller + @ResponseBody. It returns data (JSON) instead of a view name.

**Q. PUT vs PATCH?**
A. PUT replaces the whole resource and is idempotent. PATCH partially updates it.

**Q. What is the N+1 problem? How do you fix it?**
A. One query loads N parents, then lazy loading fires one query per parent. Fix it with JOIN FETCH, @EntityGraph, DTO projections or batch fetching.

**Q. Why does @Transactional sometimes not work?**
A. Self-invocation bypasses the proxy, the method is private or final, the bean is not managed by Spring, or the exception is checked or swallowed (no rollback).

**Q. Explain propagation REQUIRED vs REQUIRES_NEW.**
A. REQUIRED joins the current transaction or creates one. REQUIRES_NEW suspends the current one and always starts a new, independent transaction (e.g. audit logging).

**Q. Optimistic vs pessimistic locking?**
A. Optimistic uses a @Version column, detects conflicts at update time and fails with 409, which suits low contention. Pessimistic uses SELECT FOR UPDATE, so others wait, which suits high contention such as balances.

**Q. What is dirty checking?**
A. Hibernate compares managed entities with their loaded snapshots at flush and automatically issues UPDATEs for the changes, with no explicit save needed.

**Q. Why use DTOs instead of returning entities?**
A. To avoid leaking internal fields, lazy-loading errors and JSON recursion, and to decouple the API contract from the DB schema.

**Q. ddl-auto in production?**
A. `validate` or `none`. Schema changes go through Flyway/Liquibase migrations.

**Q. How do you handle exceptions globally?**
A. @RestControllerAdvice with @ExceptionHandler methods that return ProblemDetail (RFC 7807) with the right status codes.

**Q. What is Spring Boot Actuator?**
A. Production endpoints for health, metrics, info and Prometheus, used for monitoring and Kubernetes probes.
