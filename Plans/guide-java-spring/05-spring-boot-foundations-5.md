@@ 3 | TOPIC | Practice — Phase 5 review: Spring Boot Foundations | Cheat sheet, key snippets, top 10 questions and rapid-fire flashcards for Phase 5 | MUST
# Phase 5 review: Spring Boot Foundations

## Cheat sheet

| Idea | Remember |
|---|---|
| Spring Framework | IoC container + modules (MVC, data, transactions, AOP) |
| IoC and DI | The container creates beans and injects dependencies, usually via constructors |
| Bean | Object managed by the container; singleton by default |
| Spring Boot | Starters + auto-configuration + embedded server + externalized config + Actuator |
| Starter | Dependency bundle with managed versions (`spring-boot-starter-web`) |
| Auto-configuration | Conditional `@Configuration` classes; back off when you define your own bean |
| Maven coordinates | `groupId:artifactId:version` |
| Lifecycle | validate → compile → test → package → verify → install → deploy |
| Scopes | compile (default), provided, runtime (JDBC driver), test |
| Boot parent | Manages versions via `spring-boot-dependencies` BOM |
| Fat jar | `spring-boot-maven-plugin` repackage, `java -jar` |
| Layout | `src/main/java`, `src/main/resources`, `src/test/java` |
| Main class | Root package, so scanning covers all features |
| `@SpringBootApplication` | `@SpringBootConfiguration` + `@EnableAutoConfiguration` + `@ComponentScan` |
| Debug auto-config | `--debug` conditions report |
| `@RestController` | `@Controller` + `@ResponseBody`, return values become JSON |
| Request flow | Tomcat → DispatcherServlet → HandlerMapping → controller → HttpMessageConverter |
| Input | `@PathVariable` (identity), `@RequestParam` (filters), `@RequestBody` (JSON) |
| Errors for bad input | 400 (missing, type mismatch, bad JSON), 415 (wrong content type), 405 (wrong method) |
| Config precedence | CLI args > env vars > profile files > default file > defaults |
| Reading config | `@Value("${key:default}")`, `@ConfigurationProperties` for groups |
| Layers | Controller (HTTP) → Service (rules) → Repository (data) |
| Status from exceptions | `@ResponseStatus(HttpStatus.NOT_FOUND)` on the exception class |
| DTOs | Records for requests and responses; prevent over-posting and leaks |
| Lombok | Compile-time boilerplate; avoid `@Data` on entities; records for DTOs |

### Key code snippets
```java
// Main class
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderHubApplication.class, args);
    }
}
```

```java
// Thin controller with DTOs and status codes
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;
    private final ProductMapper mapper;

    public ProductController(ProductService service, ProductMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return mapper.toResponse(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody CreateProductRequest req) {
        Product saved = service.create(mapper.toDomain(req));
        return ResponseEntity.created(URI.create("/api/products/" + saved.id())).body(mapper.toResponse(saved));
    }
}
```

```java
// Service with a business rule and a mapped exception
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) { this.repository = repository; }

    public Product findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class ProductNotFoundException extends RuntimeException {
    ProductNotFoundException(Long id) { super("Product " + id + " not found"); }
}
```

```java
// Query params: optional and defaults
@GetMapping
public List<ProductResponse> list(@RequestParam(required = false) String q,
                                  @RequestParam(defaultValue = "false") boolean inStock) { /* ... */ }
```

```yaml
# application.yml with a custom property and a profile file
spring:
  application:
    name: orderhub
  profiles:
    active: dev
orderhub:
  inventory:
    low-stock-threshold: 5
```

```xml
<!-- POM essentials -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.0</version>
</parent>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

```bash
./mvnw clean package
java -jar target/orderhub-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --server.port=9090
```

## The 10 most likely interview questions

1. **Spring vs Spring Boot?** Spring is the IoC container and modules; Boot adds starters, auto-configuration, an embedded server, externalized config and Actuator on top, so apps need almost no setup.
2. **What is dependency injection and why use it?** The container supplies a class's collaborators (via its constructor) instead of the class creating them; it decouples code and makes unit testing with fakes easy.
3. **What does `@SpringBootApplication` do?** Combines configuration, component scanning from its package downward, and conditional auto-configuration.
4. **How does auto-configuration work and how do you override it?** Conditional configuration classes listed in `AutoConfiguration.imports`; define your own bean or set properties, and Boot backs off.
5. **`@Controller` vs `@RestController`?** View names vs response bodies; `@RestController` adds `@ResponseBody`.
6. **`@PathVariable` vs `@RequestParam` vs `@RequestBody`?** URL path identity vs query-string options vs JSON body.
7. **What happens when a request hits your API?** Tomcat thread → `DispatcherServlet` → `HandlerMapping` finds the method → arguments resolved and converted → method runs → Jackson writes JSON → response.
8. **Why controller, service, repository layers?** Separation of concerns: HTTP, business rules and data access change for different reasons and can be tested separately.
9. **Why DTOs?** Stable API contracts, no over-posting, no leaking internal or lazy-loaded fields.
10. **How do you configure different environments?** Profile files (`application-prod.yml`), environment variables and command-line args with Boot's precedence; secrets from the environment, never committed.

## Rapid-fire flashcards

Q: What is the default scope of a Spring bean?
A: Singleton, one instance per application context.

Q: Which starter brings Spring MVC, Jackson and embedded Tomcat?
A: `spring-boot-starter-web`.

Q: Which Maven scope fits a JDBC driver?
A: `runtime`.

Q: How do you see why an auto-configuration did or did not apply?
A: Start the app with `--debug` and read the conditions evaluation report.

Q: What HTTP status does Spring MVC return for a valid path called with the wrong HTTP method?
A: 405 Method Not Allowed.

Q: How do you return 201 Created with a `Location` header from a controller?
A: `ResponseEntity.created(URI.create("/api/products/" + id)).body(response)`.

Q: Which environment variable sets `server.port`?
A: `SERVER_PORT`.

Q: What does `@Value("${orderhub.region:eu-west}")` inject when the property is missing?
A: The default "eu-west".

Q: What makes a thrown exception produce a 404 without a handler method?
A: `@ResponseStatus(HttpStatus.NOT_FOUND)` on the exception class.

Q: Why use `@RequiredArgsConstructor` in a Lombok-based Spring service?
A: It generates the constructor for `final` dependencies, which Spring uses for constructor injection.

@@ 3 | TOPIC | Practice — Mock interview: Beginner round | A 45-minute beginner Java and Spring Boot interview script with model answers and scoring rubrics | MUST
# Mock interview: Beginner round

A realistic 45-minute first technical round for a junior or entry-level Java backend role, covering Phases 1 to 5. Run it with a friend acting as the interviewer, or record yourself answering aloud. Answer before reading the model answer, then score yourself with the rubric.

| Time | Section | Focus |
|---|---|---|
| 0–5 min | Warm-up | Introduce yourself and your project |
| 5–20 min | Concept questions | Java core, OOP, collections, exceptions, Spring Boot |
| 20–35 min | Coding task | Collections and streams-free plain Java, then a REST endpoint |
| 35–40 min | Scenario question | Debug a Spring Boot problem |
| 40–45 min | Design question | Design a small REST API |

## Part 1. Warm-up (5 minutes)

**Interviewer:** "Tell me about yourself and one project you built with Java or Spring Boot."

**Model answer (about 90 seconds):**
"I'm a backend developer focused on Java 21 and Spring Boot. Recently I built OrderHub, an order management backend. Version one is a REST API for products with full CRUD: list with filters, get by id, create, update, delete, plus stock adjustment and a low-stock report. I structured it in layers: thin controllers that handle HTTP and map DTOs, a service that holds business rules such as unique SKUs and non-negative stock, and a repository, currently an in-memory `ConcurrentHashMap` because beans are singletons used by many threads. Errors map to proper status codes: 404 for unknown products, 409 for duplicate SKUs, 400 for invalid input. Configuration like the low-stock threshold comes from `application.yml` with a dev profile for seed data. Next I'm moving it to PostgreSQL with Spring Data JPA and adding validation and tests."

| Weak | Good | Strong |
|---|---|---|
| Lists technologies without a project, or talks for five minutes | Describes a project and its features clearly | Explains structure and decisions (layers, status codes, thread safety), what they learned and what comes next, in under two minutes |

## Part 2. Concept questions (15 minutes)

### Q1. What is the difference between JDK, JRE and JVM?
**Model answer:** The JVM is the virtual machine that runs bytecode, with class loading, the JIT compiler and garbage collection. The JRE is the JVM plus the standard libraries needed to run programs. The JDK is the JRE plus development tools such as `javac`, `jar`, `jshell` and `jcmd`. Since Java 11, Oracle ships only the JDK, and apps are often packaged with a trimmed runtime built by `jlink`.

| Weak | Good | Strong |
|---|---|---|
| Mixes them up | Correct nesting JDK ⊃ JRE ⊃ JVM | Adds what each part does (JIT, GC, tools) and the modern packaging note |

### Q2. What is the contract between `equals` and `hashCode`, and what breaks if you violate it?
**Model answer:** If two objects are equal by `equals`, they must have the same `hashCode`; equal hash codes do not imply equality. Hash-based collections use `hashCode` to find the bucket and `equals` to confirm. If you override `equals` without `hashCode`, two equal objects land in different buckets, so a `HashSet` stores duplicates and `HashMap.get` misses. Records generate both correctly.

```java
record Sku(String code) { }   // equals and hashCode generated from code
Set<Sku> set = new HashSet<>(List.of(new Sku("A1"), new Sku("A1")));
// set.size() == 1
```

| Weak | Good | Strong |
|---|---|---|
| "hashCode is for performance" only | States the rule and the HashSet consequence | Explains bucket lookup step by step, mentions mutable keys and records |

### Q3. Checked vs unchecked exceptions? Which do you use in a Spring service?
**Model answer:** Checked exceptions extend `Exception` (not `RuntimeException`) and the compiler forces callers to catch or declare them, like `IOException`. Unchecked exceptions extend `RuntimeException` and represent programming errors or conditions callers usually cannot fix. In Spring services I use custom unchecked exceptions like `ProductNotFoundException`, because they keep signatures clean, roll back transactions by default, and are translated into HTTP status codes centrally.

| Weak | Good | Strong |
|---|---|---|
| Only definitions | Definitions plus examples | Connects to Spring: rollback rules and centralized mapping to status codes |

### Q4. ArrayList vs LinkedList, and HashMap vs TreeMap?
**Model answer:** `ArrayList` is a resizable array with O(1) index access and amortized O(1) appends; `LinkedList` has O(1) operations at the ends but O(n) index access and much more memory per element, so `ArrayList` is the default and `ArrayDeque` covers queues. `HashMap` offers O(1) average lookups with no ordering; `TreeMap` keeps keys sorted with O(log n) operations and navigation methods like `floorKey`. I choose `TreeMap` only when I need sorted keys or range queries, and `LinkedHashMap` when I need insertion order.

| Weak | Good | Strong |
|---|---|---|
| Says LinkedList is faster for inserts without qualification | Correct Big-O for both pairs | Mentions cache locality, `ArrayDeque`, `LinkedHashMap` and a concrete use for `floorKey` |

### Q5. What is Spring Boot, and what does `@SpringBootApplication` do?
**Model answer:** Spring Boot builds on the Spring Framework's IoC container and adds starters, auto-configuration, an embedded server and externalized configuration, so an app runs with `java -jar` and minimal setup. `@SpringBootApplication` combines `@SpringBootConfiguration` (a configuration class), `@ComponentScan` (scan this package and sub-packages for components) and `@EnableAutoConfiguration` (apply conditional auto-configurations that back off when I define my own beans). I can inspect decisions with `--debug`.

| Weak | Good | Strong |
|---|---|---|
| "It makes Spring easier" | Names the three annotations | Explains scanning scope, conditional back-off and how to debug it |

### Q6. Walk me through what happens when `GET /api/products/1` reaches your app.
**Model answer:** Tomcat accepts the connection and hands the request to a worker thread, which calls the `DispatcherServlet`. It asks the `HandlerMapping` registry which method matches GET and the path pattern `/api/products/{id}`, then the `HandlerAdapter` resolves arguments, converting "1" to a `Long` for the `@PathVariable`. My controller calls the service, which calls the repository; if nothing is found the service throws `ProductNotFoundException`, which becomes 404 through `@ResponseStatus`. Otherwise the controller maps the product to a `ProductResponse`, and Jackson's message converter writes it as JSON with status 200.

| Weak | Good | Strong |
|---|---|---|
| "The controller returns the product" | Mentions DispatcherServlet and JSON conversion | Full chain: thread, handler mapping, argument conversion, layers, exception path, message converter |

### Q7. Constructor injection vs field injection?
**Model answer:** Constructor injection passes dependencies through the constructor into `final` fields: dependencies are explicit, the object is always fully initialized, it is immutable, and unit tests can just call `new`. Field injection with `@Autowired` on private fields hides dependencies, prevents `final`, and needs reflection or a Spring context in tests. Spring recommends constructor injection, and with a single constructor no `@Autowired` is needed.

| Weak | Good | Strong |
|---|---|---|
| "Both work" | Prefers constructor injection with one reason | Gives several reasons (immutability, testability, explicit dependencies, circular dependency detection) |

## Part 3. Coding task (15 minutes)

**Interviewer:** "Part A: Given a list of orders, return the total amount per customer, sorted by total descending, then by customer name. Plain Java, no streams required. Part B: expose it as `GET /api/reports/customer-totals`."

```java
record Order(String customer, BigDecimal amount) { }
// Input: (ann, 30.00), (bob, 12.50), (ann, 20.00), (cid, 45.00), (bob, 37.50)
// Expected: ann 50.00, bob 50.00, cid 45.00  (equal totals sorted by name)
```

**Model solution, part A:**
```java
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerTotals {
    record Order(String customer, BigDecimal amount) { }
    record CustomerTotal(String customer, BigDecimal total) { }

    static List<CustomerTotal> totals(List<Order> orders) {
        Map<String, BigDecimal> sums = new LinkedHashMap<>();
        for (Order o : orders) {
            sums.merge(o.customer(), o.amount(), BigDecimal::add);      // O(1) per order
        }
        List<CustomerTotal> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : sums.entrySet()) {
            result.add(new CustomerTotal(e.getKey(), e.getValue()));
        }
        result.sort(Comparator.comparing(CustomerTotal::total).reversed()
                .thenComparing(CustomerTotal::customer));
        return result;
    }

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("ann", new BigDecimal("30.00")),
                new Order("bob", new BigDecimal("12.50")),
                new Order("ann", new BigDecimal("20.00")),
                new Order("cid", new BigDecimal("45.00")),
                new Order("bob", new BigDecimal("37.50")));
        totals(orders).forEach(t -> System.out.println(t.customer() + " " + t.total()));
    }
}
```
```text
ann 50.00
bob 50.00
cid 45.00
```
Explain while coding: `merge` with `BigDecimal::add` sums per customer; `BigDecimal` because money must be exact; the comparator sorts totals descending (`BigDecimal.compareTo` ignores scale) and breaks ties by name; time O(n + k log k) for n orders and k customers.

**Model solution, part B:**
```java
package com.orderhub.report;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

record Order(String customer, BigDecimal amount) { }
record CustomerTotal(String customer, BigDecimal total) { }

@Service
class ReportService {
    private final List<Order> orders = List.of(            // stands in for an OrderRepository
            new Order("ann", new BigDecimal("30.00")),
            new Order("bob", new BigDecimal("12.50")),
            new Order("ann", new BigDecimal("20.00")),
            new Order("cid", new BigDecimal("45.00")),
            new Order("bob", new BigDecimal("37.50")));

    List<CustomerTotal> customerTotals() {
        Map<String, BigDecimal> sums = new LinkedHashMap<>();
        orders.forEach(o -> sums.merge(o.customer(), o.amount(), BigDecimal::add));
        return sums.entrySet().stream()
                .map(e -> new CustomerTotal(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(CustomerTotal::total).reversed()
                        .thenComparing(CustomerTotal::customer))
                .toList();
    }
}

@RestController
@RequestMapping("/api/reports")
class ReportController {
    private final ReportService service;

    ReportController(ReportService service) { this.service = service; }

    @GetMapping("/customer-totals")
    List<CustomerTotal> customerTotals() {
        return service.customerTotals();
    }
}
```
```http
GET /api/reports/customer-totals HTTP/1.1

HTTP/1.1 200
Content-Type: application/json

[{"customer":"ann","total":50.00},{"customer":"bob","total":50.00},{"customer":"cid","total":45.00}]
```

Follow-ups the interviewer may ask: "Why not `double`?" (binary floating point cannot represent 0.10 exactly). "What if there are millions of orders?" (aggregate in the database with `GROUP BY` and `ORDER BY`, Phase 12). "Where does the logic belong?" (service, not controller).

| Weak | Good | Strong |
|---|---|---|
| Uses `double`, nested loops, or cannot sort by two keys | Correct result with a map and a comparator | Clean `merge` and comparator chain, `BigDecimal`, Big-O stated, logic in a service, mentions database aggregation for scale |

## Part 4. Scenario question (5 minutes)

**Interviewer:** "You add a new `OrderController` in package `com.shop.orders`. The app starts without errors, but every call to `/api/orders` returns 404. Separately, after adding `spring-boot-starter-data-jpa`, the app will not start at all. What is going on in each case?"

**Model answer:**
- **404:** the main class is in `com.orderhub`, and `@SpringBootApplication` only scans its own package and sub-packages, so `com.shop.orders` is never scanned and the controller is not registered. I would confirm with `/actuator/mappings` or the absence of a mapping in debug logs, then move the controller under `com.orderhub.order` (best) or add `scanBasePackages`.
- **Startup failure:** the JPA starter brings JDBC, so `DataSourceAutoConfiguration` tries to create a `DataSource`, finds no `spring.datasource.url` and no embedded database, and fails with "Failed to configure a DataSource". Fix by configuring the database URL and driver (or H2 for development), or exclude the auto-configuration until the database is ready. The conditions report (`--debug`) shows exactly which auto-configuration triggered.

| Weak | Good | Strong |
|---|---|---|
| Guesses ("restart the IDE", "add @Autowired") | Identifies scanning and DataSource causes | Explains both with the underlying mechanism, says how to verify (mappings, conditions report) and gives the preferred fix |

## Part 5. Design question (5 minutes)

**Interviewer:** "Design the REST API for managing customers and their delivery addresses in OrderHub. What endpoints, status codes and DTOs would you have?"

**Model answer:**

| Method and path | Purpose | Success | Errors |
|---|---|---|---|
| `GET /api/customers?email=&page=0&size=20` | Search and page | 200 | 400 bad params |
| `GET /api/customers/{id}` | One customer | 200 | 404 |
| `POST /api/customers` | Register | 201 + `Location` | 400 invalid, 409 email taken |
| `PUT /api/customers/{id}` | Replace profile | 200 | 400, 404 |
| `DELETE /api/customers/{id}` | Delete or deactivate | 204 | 404 |
| `GET /api/customers/{id}/addresses` | List addresses | 200 | 404 customer |
| `POST /api/customers/{id}/addresses` | Add address | 201 | 400, 404 |
| `DELETE /api/customers/{id}/addresses/{addressId}` | Remove address | 204 | 404 |

```java
record RegisterCustomerRequest(String email, String name, String password) { }
record CustomerResponse(Long id, String email, String name, Instant memberSince) { }
record AddressRequest(String street, String city, String postalCode, String country) { }
record AddressResponse(Long id, String street, String city, String postalCode, String country) { }
```

Key points to say: plural nouns and nesting for owned sub-resources; ids in the path, filters and paging in the query; request DTOs without `id` or timestamps (no over-posting), response DTOs without the password hash; 201 with `Location` on create, 204 on delete, 409 for the unique email rule; layered implementation with the email rule in the service; validation and `ProblemDetail` errors next (Phase 11); and email uniqueness finally enforced by a database unique constraint (Phase 12).

| Weak | Good | Strong |
|---|---|---|
| Verb URLs like `/createCustomer`, everything returns 200 | Resource URLs and correct methods and status codes | Adds DTO design (no password in responses), nested sub-resources, paging, error cases, where rules live and how uniqueness is enforced |

## Scoring summary

| Section | Weight | Your score (1 = weak, 2 = good, 3 = strong) |
|---|---|---|
| Warm-up | 10% | |
| Concept questions (average of 7) | 35% | |
| Coding task | 30% | |
| Scenario | 10% | |
| Design | 15% | |

A weighted average of 2 or more is a pass for a beginner round; 2.5 or more is a strong hire signal. Revisit the topics behind any 1: [[equals() and hashCode() Contract]], [[HashMap, LinkedHashMap and TreeMap]], [[Exception Hierarchy: Checked vs Unchecked]], [[@SpringBootApplication Explained]], [[Layered Architecture: Controller, Service, Repository]], [[DTOs and Mapping]].
