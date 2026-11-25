@@ 3 | TOPIC | Practice — Phase 23 review: Last 48 hours cheat sheet | The whole guide compressed: phase-by-phase rules, key snippets, the answers to memorize, interview-day checklist and rapid-fire flashcards | MUST
# Phase 23 review: Last 48 hours cheat sheet

Read this the evening before and again the morning of the interview. It compresses Phases 1 to 22 into the rules, numbers and snippets interviewers ask about most. If a line feels unfamiliar, open that topic's note and read its Recall cards; do not try to learn new material in the last 48 hours.

## The 48-hour plan
| When | Do |
|---|---|
| 48 h before | One timed run of the Advanced mock round; note weak spots |
| 36 h before | Re-read the topic notes for the weak spots; redo 10 tricky output questions |
| 24 h before | Rehearse your 2-minute pitch and 3 STAR stories aloud; one system design on paper |
| Evening before | Read this sheet once; prepare setup (IDE, camera, water); sleep |
| Morning of | Read the "answers to memorize" and the flashcards only |

---

## Phases 1 to 4: Java foundations, OOP, core APIs, collections

| Topic | Remember |
|---|---|
| JDK, JRE, JVM | JDK = tools plus JRE; JRE = JVM plus libraries; JVM runs bytecode (interpreter plus JIT) |
| Primitives | 8 types; `int` 32-bit, `long` 64-bit; defaults only for fields, locals must be assigned |
| Wrapper cache | `Integer.valueOf` caches -128 to 127; compare wrappers with `equals` |
| Promotion | `byte`, `short`, `char` promote to `int` in arithmetic; `+=` has an implicit cast |
| Pass-by-value | Always; object references are copied, so you can mutate but not reassign the caller's object |
| Static vs instance | Static belongs to the class; static methods are hidden, not overridden |
| Init order | Parent static, child static, parent instance init plus constructor, child instance init plus constructor |
| Overloading vs overriding | Compile time by declared types (widening, then boxing, then varargs) vs runtime by object type |
| Abstract class vs interface | State and constructors vs contract with default, static and private methods; one class, many interfaces |
| `equals` and `hashCode` | Equal objects must have equal hash codes; override both or use records |
| Composition over inheritance | Inheritance breaks encapsulation (fragile base class); delegate instead |
| Records | Immutable data carriers with generated constructor, accessors, `equals`, `hashCode`, `toString` (Java 16) |
| Immutable class | `final` class, `private final` fields, no setters, defensive copies in and out |
| Exceptions | Checked must be handled or declared; unchecked for bugs; never catch `Throwable` |
| finally | Always runs (except JVM halt); a `return` in finally overrides and swallows |
| try-with-resources | Closes in reverse order; close exceptions become suppressed |
| String | Immutable, pooled literals; `StringBuilder` for loops; `equals` not `==` |
| BigDecimal | Build from `String`; `compareTo` for value equality; always set scale and `RoundingMode` |
| java.time | `Instant` for timestamps, `LocalDate` for dates, `ZonedDateTime` for zones; immutable |
| ArrayList vs LinkedList | Array, O(1) random access, cache-friendly vs nodes, rarely faster in practice |
| HashSet, LinkedHashSet, TreeSet | Hash, hash plus insertion order, sorted (red-black tree, O(log n)) |
| Fail-fast iterator | `modCount` check throws CME; remove with `Iterator.remove` or `removeIf` |
| Comparable vs Comparator | Natural order inside the class vs external strategies (`Comparator.comparing(...).thenComparing(...)`) |
| `List.of` vs `unmodifiableList` | Truly immutable, nulls rejected vs read-only view of a mutable list |

```java
// equals and hashCode the modern way
record ProductId(String sku) { }

// Defensive copy in an immutable class
final class Basket {
    private final List<String> items;
    public Basket(List<String> items) { this.items = List.copyOf(items); }
    public List<String> items() { return items; }               // already unmodifiable
}

// Custom exception keeps the cause
throw new OrderImportException("Cannot read " + path, e);
```

## Phase 5: Spring Boot foundations

| Topic | Remember |
|---|---|
| Spring vs Boot | Spring = DI container plus modules; Boot = auto-configuration, starters, embedded server, opinionated defaults |
| `@SpringBootApplication` | `@Configuration` plus `@EnableAutoConfiguration` plus `@ComponentScan` (from its package down) |
| Maven scopes | compile, provided, runtime, test; `mvn clean verify`; parent manages versions via BOM |
| Request data | `@PathVariable` (identity), `@RequestParam` (filters), `@RequestBody` (JSON payload) |
| Layers | Controller (HTTP), service (business, transactions), repository (data) |
| DTOs | Never expose entities; records as request and response types |
| Lombok | Convenient, but `@Data` on JPA entities breaks `equals`, `hashCode` and lazy loading |

## Phases 6 and 7: Generics, modern Java, functional Java and streams

| Topic | Remember |
|---|---|
| PECS | Producer `extends`, consumer `super`: `copy(List<? super T> dst, List<? extends T> src)` |
| Type erasure | Generic types removed at runtime; no `new T()`, no `instanceof List<String>` |
| Reflection | Powers Spring and Jackson; slow and breaks encapsulation in app code |
| Sealed plus pattern matching | Exhaustive `switch` over a sealed hierarchy without `default` (Java 17 and 21) |
| Versions | 8 lambdas and streams; 11 `HttpClient`, `var` in lambdas; 17 records, sealed; 21 virtual threads, switch patterns, sequenced collections; 25 LTS: scoped values, compact source files |
| Functional interfaces | `Function`, `Supplier`, `Consumer`, `Predicate`, `BiFunction`, `UnaryOperator` |
| Stream laziness | Nothing runs without a terminal operation; elements flow one at a time; short-circuiting ops stop early |
| Collectors | `groupingBy` (with downstream), `partitioningBy`, `toMap` (merge function for duplicates) |
| Optional | Return type only; `orElseThrow`, `map`, `orElseGet`; never `get()` without a check, never fields or parameters |
| Parallel streams | Common ForkJoinPool; only for large CPU-bound, stateless, splittable work |

```java
Map<String, Long> countByDept = employees.stream()
        .collect(Collectors.groupingBy(Employee::dept, Collectors.counting()));

Map<Boolean, List<Order>> bigAndSmall = orders.stream()
        .collect(Collectors.partitioningBy(o -> o.total().compareTo(new BigDecimal("100")) > 0));

String result = switch (payment) {                   // sealed interface Payment permits Card, Cash
    case Card c when c.amount() > 1000 -> "review";
    case Card c -> "card " + c.last4();
    case Cash cash -> "cash";
};
```

## Phase 8: Collections internals

| Topic | Remember |
|---|---|
| HashMap | Buckets array, index `(n - 1) & hash`, load factor 0.75, resize doubles, treeify at 8 per bucket (table at least 64) |
| LinkedHashMap | Insertion or access order; LRU with `accessOrder = true` and `removeEldestEntry` |
| TreeMap | Red-black tree, O(log n), `floorKey`, `ceilingKey`, `subMap` |
| ConcurrentHashMap | CAS plus per-bin locks, lock-free reads, no nulls, atomic `compute` and `merge` |
| CME | Single-threaded structural modification during iteration; fail-fast is best effort |
| Special maps | `EnumMap` (array-backed), `WeakHashMap` (weak keys), `IdentityHashMap` (`==`) |

## Phase 9 and 16: Concurrency

| Topic | Remember |
|---|---|
| Thread states | NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED |
| Runnable vs Callable | No result and no checked exception vs result and exceptions via `Future` |
| `synchronized` | Mutual exclusion plus visibility; reentrant; monitor per object |
| `volatile` | Visibility and ordering, not atomicity |
| Deadlock | Four conditions; fix with a global lock order or `tryLock` with timeout |
| Atomics and CAS | `AtomicInteger`, `LongAdder` for hot counters; ABA problem with `AtomicStampedReference` |
| Executors | Bounded queues and named threads; `newFixedThreadPool` has an unbounded queue; always shut down |
| CompletableFuture | `thenApply`, `thenCompose`, `thenCombine`, `allOf`, `exceptionally`, `orTimeout`; custom executor for I/O |
| BlockingQueue | Producer-consumer with backpressure (`put` blocks when full) |
| Locks | `ReentrantLock` (tryLock, fairness, conditions), `ReadWriteLock`, `StampedLock` (optimistic reads) |
| Synchronizers | `CountDownLatch` (one-shot), `CyclicBarrier` (reusable), `Semaphore` (permits), `Phaser` |
| ForkJoin | Work stealing, recursive tasks |
| Virtual threads | Cheap, unmount when blocked; do not pool; pinning fixed for `synchronized` in Java 24 |
| ThreadLocal | Always `remove()` in pooled threads; scoped values in Java 25 |
| Thread-safe design | Immutability first, then confinement, then concurrent collections, then locks |

```java
// Thread-safe lazy singleton: holder idiom
class Registry {
    private Registry() { }
    private static class Holder { static final Registry INSTANCE = new Registry(); }
    public static Registry get() { return Holder.INSTANCE; }
}

// Parallel calls with timeout
var a = CompletableFuture.supplyAsync(() -> stock(id), ioExecutor);
var b = CompletableFuture.supplyAsync(() -> price(id), ioExecutor);
Result r = a.thenCombine(b, Result::new).orTimeout(2, TimeUnit.SECONDS).join();
```

## Phase 10: Spring Core

| Topic | Remember |
|---|---|
| IoC and DI | Container creates and wires objects; you depend on abstractions |
| Constructor injection | Preferred: immutable, testable, fails fast, reveals too many dependencies |
| Multiple beans | `@Primary` default, `@Qualifier` specific, inject `List` or `Map` for strategies |
| Stereotypes | `@Component`, `@Service`, `@Repository` (exception translation), `@Controller` |
| `@Configuration` and `@Bean` | Full mode proxies so `@Bean` methods calling each other return singletons |
| Scopes | singleton (default), prototype, request, session; prototype in singleton needs `ObjectProvider` |
| Lifecycle | Constructor, injection, aware callbacks, `@PostConstruct`, ready, `@PreDestroy` |
| Profiles | `spring.profiles.active`; `application-{profile}.yml`; `@Profile` |
| `@ConfigurationProperties` | Type-safe, validated, grouped config; prefer over scattered `@Value` |
| Conditionals | `@ConditionalOnProperty`, `OnClass`, `OnMissingBean` |
| Events | `ApplicationEventPublisher`; `@TransactionalEventListener(phase = AFTER_COMMIT)` |

```java
@ConfigurationProperties(prefix = "orderhub.payments")
@Validated
record PaymentProperties(@NotBlank String provider, @DurationMin(seconds = 1) Duration timeout) { }
```

## Phase 11: REST APIs

| Topic | Remember |
|---|---|
| Methods | GET safe and idempotent; PUT and DELETE idempotent; POST neither; PATCH not necessarily |
| Status codes | 200, 201 plus Location, 202, 204, 400, 401 (who are you), 403 (not allowed), 404, 409, 422, 429, 500, 503 |
| Idempotency keys | Make POST retry-safe; unique key plus stored result |
| ResponseEntity | Status, headers (ETag, Location, Cache-Control) and body |
| Validation | `@Valid` on body, `List<@Valid T>` for nested, `@Validated` for method parameters |
| Errors | `@RestControllerAdvice` plus `ProblemDetail` (RFC 9457) |
| Pagination | `Pageable`; keyset for deep pages; cap page size |
| Versioning | URI, header, media type; Boot 4 built-in `version` attribute |
| CORS | Browser-enforced; configure allowed origins, never `*` with credentials |
| Clients | `RestClient` (blocking, 6.1), `WebClient` (reactive), `@HttpExchange` interfaces |

```java
@PostMapping("/api/orders")
ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req, UriComponentsBuilder uri) {
    OrderResponse created = service.create(req);
    return ResponseEntity.created(uri.path("/api/orders/{id}").build(created.id())).body(created);
}

@ExceptionHandler(OrderNotFoundException.class)
ProblemDetail notFound(OrderNotFoundException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
}
```

## Phase 12: Spring Data JPA

| Topic | Remember |
|---|---|
| JPA vs Hibernate vs Spring Data | Specification vs implementation vs repository abstraction on top |
| Relationships | Owning side has the foreign key (`@ManyToOne`); `mappedBy` on the inverse side |
| Fetch defaults | `@ManyToOne` and `@OneToOne` eager, collections lazy; make everything lazy |
| N+1 | Fetch join, `@EntityGraph`, `@BatchSize`, DTO projections; detect with statistics |
| Cascade and orphanRemoval | Parent controls child lifecycle; keep both sides in sync with helper methods |
| Derived queries | `findByStatusAndCreatedAtAfter`; `@Query` for anything complex; `@Modifying` for updates |
| `@Transactional` | Service layer; rollback on unchecked; `readOnly = true` for queries |
| Propagation | REQUIRED (join), REQUIRES_NEW (suspend, new connection), NESTED (savepoint), MANDATORY, NEVER |
| Isolation | READ COMMITTED (PostgreSQL default) prevents dirty reads; REPEATABLE READ; SERIALIZABLE |
| Entity states | Transient, managed, detached, removed; dirty checking on flush |
| Locking | `@Version` optimistic (conflict exception, 409) vs `PESSIMISTIC_WRITE` (`SELECT ... FOR UPDATE`) |
| Flyway | `V1__init.sql`, never edit applied migrations, `ddl-auto=validate`, expand and contract |
| Auditing | `@CreatedDate`, `@LastModifiedBy` with `@EnableJpaAuditing` |

```java
// jakarta.persistence.* entity mapping (Spring Data JPA, org.springframework.boot:spring-boot-starter-data-jpa)
@Entity
class Order {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) Long id;
    @Version Long version;
    @ManyToOne(fetch = FetchType.LAZY) Customer customer;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderLine> lines = new ArrayList<>();

    public void addLine(OrderLine line) { lines.add(line); line.setOrder(this); }   // keep both sides in sync
}
```

## Phase 13: Testing

| Topic | Remember |
|---|---|
| JUnit 5 | `@Test`, `@ParameterizedTest`, `@BeforeEach`, `assertThrows`, AssertJ for fluent asserts |
| Mockito | `when().thenReturn()`, `verify()`, `ArgumentCaptor`; mock collaborators, not value objects |
| Slices | `@WebMvcTest` (MVC only), `@DataJpaTest` (JPA only, transactional rollback), `@SpringBootTest` (everything) |
| Mocks in context | `@MockitoBean` (Boot 3.4+; `@MockBean` removed in Boot 4) |
| Testcontainers | Real PostgreSQL and Kafka in Docker; `@ServiceConnection` (Boot 3.1+) |
| Pyramid | Many unit, fewer slice and integration, few end-to-end |

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean OrderService service;

    @Test
    void returns404WhenMissing() throws Exception {
        when(service.get(9L)).thenThrow(new OrderNotFoundException(9L));
        mvc.perform(get("/api/orders/9")).andExpect(status().isNotFound());
    }
}
```

## Phase 14: Spring Security

| Topic | Remember |
|---|---|
| AuthN vs AuthZ | Who you are (401) vs what you may do (403) |
| Filter chain | `DelegatingFilterProxy` to `FilterChainProxy` to `SecurityFilterChain`s; filters run before controllers |
| Passwords | `BCryptPasswordEncoder` or `DelegatingPasswordEncoder`; never store plain or fast hashes |
| JWT | Stateless, signed, short-lived; validate signature, issuer, audience, expiry; revocation needs refresh tokens or a denylist |
| OAuth2 and OIDC | Authorization (access tokens) vs authentication (ID token); Boot as resource server or client |
| Method security | `@EnableMethodSecurity`, `@PreAuthorize("hasRole('ADMIN') or ...")`; proxy-based |
| CSRF | Needed for cookie sessions; can be disabled for pure bearer-token APIs |

```java
@Bean
SecurityFilterChain api(HttpSecurity http) throws Exception {
    return http.csrf(c -> c.disable())
               .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .authorizeHttpRequests(a -> a.requestMatchers("/actuator/health/**").permitAll()
                                            .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                                            .anyRequest().authenticated())
               .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
               .build();
}
```

## Phase 15: JVM internals and performance

| Topic | Remember |
|---|---|
| Class loading | Bootstrap, platform, application; parent delegation; load, link, initialize |
| Memory areas | Heap (young: eden and survivors; old), metaspace, thread stacks, code cache, direct memory |
| JIT | Interpreter, C1, C2 with tiered compilation; escape analysis enables scalar replacement |
| JMM | Happens-before: monitor unlock to lock, volatile write to read, start, join, final fields |
| GC basics | Generational hypothesis; minor vs major vs full GC; GC roots |
| Collectors | Serial, Parallel (throughput), G1 (default, balanced), ZGC (sub-millisecond pauses, generational since Java 21) , Shenandoah |
| Flags | `-Xms`, `-Xmx`, `-XX:MaxRAMPercentage`, `-Xlog:gc*`, `-XX:+HeapDumpOnOutOfMemoryError` |
| Leaks | Static collections, unbounded caches, ThreadLocals, listeners, class loader leaks |
| Tools | `jcmd` (thread dump, heap dump, JFR), JDK Mission Control, Eclipse MAT, async-profiler |

## Phase 17: Design principles and patterns

| Topic | Remember |
|---|---|
| SOLID | Single responsibility, open-closed, Liskov substitution, interface segregation, dependency inversion |
| Singleton | Enum or holder idiom; in Spring, just a singleton bean |
| Factory and Builder | Hide creation logic; builders for many optional parameters |
| Adapter, Decorator, Facade | Convert interface; add behaviour by wrapping; simplify a subsystem |
| Proxy | Same interface, controls access: Spring AOP, transactions, lazy loading |
| Strategy, Template, Observer | Swap algorithms; fixed skeleton with hooks (`JdbcTemplate`); events |
| Chain of responsibility | Security filter chain, interceptors |
| Patterns in Spring | Factory (`BeanFactory`), singleton, proxy (AOP), template (`RestTemplate`, `JdbcTemplate`), observer (events), front controller (`DispatcherServlet`) |

## Phase 18: Spring internals

| Topic | Remember |
|---|---|
| Startup | Environment, context, bean definitions, `BeanFactoryPostProcessor`, `BeanPostProcessor`, singletons, web server, ready event |
| BFPP vs BPP | Modifies bean definitions before creation vs wraps or modifies bean instances (proxies are created here) |
| Circular dependencies | Prohibited by default since Boot 2.6; redesign, events, or `@Lazy` as a stopgap |
| AOP | Aspect, advice, pointcut, join point; proxy-based, method execution only |
| JDK vs CGLIB | Interface-based vs subclass (Boot default); CGLIB cannot proxy final classes or methods |
| `@Transactional` | Proxy, `TransactionInterceptor`, transaction manager, thread-bound resources; self-invocation trap |
| Auto-configuration | `AutoConfiguration.imports`, conditions, after user config, `--debug` report |
| Custom starter | Auto-configure module plus starter POM; `@ConditionalOnMissingBean` so users can override |
| DispatcherServlet | Filters, `HandlerMapping`, interceptors, `HandlerAdapter`, argument resolvers, message converters, exception resolvers |
| AOT and native | Build-time bean analysis; GraalVM native image starts in milliseconds, longer builds, reflection hints |

## Phase 19: Advanced data and caching

| Topic | Remember |
|---|---|
| HikariCP | Small pools (10 to 20), `connection-timeout` short, leak detection, metrics `active` and `pending` |
| Indexes | B-tree on filter and sort columns; composite index order matters; `EXPLAIN ANALYZE` |
| Batching | `hibernate.jdbc.batch_size`, SEQUENCE ids (IDENTITY disables batching), flush and clear |
| Spring Cache | `@Cacheable`, `@CachePut`, `@CacheEvict`; proxy-based; `sync = true` against stampedes |
| Redis | Shared cache, TTLs, distributed locks, rate limits, pub/sub |
| Second-level cache | Hibernate entity cache (Ehcache, JCache); for read-mostly reference data |
| Read replicas | `AbstractRoutingDataSource` keyed by `readOnly` transactions; replication lag |
| Spring Batch | Job, step, chunk (reader, processor, writer), restartable via job repository |

## Phase 20: Microservices

| Topic | Remember |
|---|---|
| Monolith vs microservices | Start modular monolith; split for team autonomy and independent scaling, not fashion |
| DDD | Bounded contexts define service boundaries; aggregates define consistency boundaries |
| Gateway | Routing, auth, rate limiting, CORS in one place (Spring Cloud Gateway) |
| Discovery | Eureka or Kubernetes DNS; client-side vs server-side load balancing |
| Config | Spring Cloud Config or Kubernetes ConfigMaps and Secrets |
| Service calls | OpenFeign or `@HttpExchange`; always timeouts |
| Resilience4j | Circuit breaker (fail fast), retry (backoff, idempotent only), rate limiter, bulkhead, time limiter |
| Saga and outbox | Local transactions plus events plus compensations; outbox for atomic state plus event |
| Kafka | Topics, partitions (ordering per key), consumer groups, offsets, at-least-once, idempotent consumers, DLT |
| RabbitMQ | Exchanges (direct, topic, fanout), queues, acks; routing-rich, no replay |
| Service security | mTLS, OAuth2 client credentials, token relay |

## Phase 21: Observability and production

| Topic | Remember |
|---|---|
| Actuator | health, info, metrics, prometheus, env (secured); separate management port |
| Logging | SLF4J API, Logback; MDC for trace and request ids; structured JSON logs |
| Metrics | Micrometer; counters, timers, gauges; RED and USE methods; low-cardinality tags |
| Tracing | Micrometer Tracing with OpenTelemetry; W3C `traceparent`; sampling |
| Shutdown and probes | `server.shutdown=graceful`; liveness (restart me) vs readiness (send me traffic); preStop sleep |
| Docker | Layered jars, non-root user, JRE base image, `MaxRAMPercentage`, Buildpacks or Jib |
| Kubernetes | Deployment, Service, ConfigMap, Secret, probes, resources (requests and limits), HPA |
| CI/CD | Build, test (Testcontainers), scan, image, deploy with canary or rolling update, rollback |

```yaml
server:
  shutdown: graceful
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  endpoint:
    health:
      probes:
        enabled: true
  tracing:
    sampling:
      probability: 0.1
```

## Phase 22: Reactive and what's new

| Topic | Remember |
|---|---|
| Reactor | `Mono` 0..1, `Flux` 0..N; lazy until subscribe; `flatMap` concurrent unordered, `concatMap` ordered |
| Threads | `publishOn` switches downstream, `subscribeOn` switches the source; blocking goes to `boundedElastic` |
| WebFlux vs MVC | Event loop on Netty vs thread per request; WebFlux for streaming, gateways and backpressure |
| Backpressure | `request(n)`; buffer, drop, latest, error; `limitRate`; `flatMap(f, concurrency)` |
| R2DBC | Reactive SQL; no relationships or lazy loading; transactions through Reactor Context |
| Virtual threads vs reactive | Same scalability goal; virtual threads keep blocking code simple; protect downstreams |
| Boot 4 and Framework 7 | Nov 2025; Java 17 min, Java 25 ready; Jakarta EE 11; modular auto-config; JSpecify; API versioning; HTTP service groups; Jackson 3; core `@Retryable` and `@ConcurrencyLimit`; Undertow and `@MockBean` removed |

---

## The answers to memorize (say them word for word)

1. **Java pass-by-value:** "Java is always pass-by-value. For objects the value is a copy of the reference, so a method can change the object's state but cannot make the caller's variable point to a different object."
2. **`equals` and `hashCode`:** "If two objects are equal, they must have the same hash code; otherwise hash-based collections cannot find them. I override both together, or use a record."
3. **HashMap:** "An array of buckets indexed by the spread hash; collisions chain and turn into red-black trees at 8 entries; it resizes at 75 percent load by doubling."
4. **Checked vs unchecked:** "Checked exceptions are recoverable conditions the compiler forces you to handle; unchecked ones signal bugs. Spring rolls back on unchecked by default."
5. **`volatile`:** "It guarantees visibility and ordering through a happens-before edge, but not atomicity, so `count++` still needs atomics or locks."
6. **DI:** "The container constructs objects and injects their dependencies, so classes depend on abstractions and are easy to test. I use constructor injection."
7. **`@Transactional`:** "A proxy intercepts the call, starts or joins a transaction through the transaction manager, and commits or rolls back on unchecked exceptions. It does not apply to self-invocation or private methods."
8. **N+1:** "One query loads N parents and lazy loading fires one query per parent. I fix it with fetch joins, entity graphs, batch fetching or DTO projections, and I detect it with query counts in tests."
9. **Optimistic vs pessimistic locking:** "Optimistic uses a version column and fails on conflict, best for low contention; pessimistic locks rows with `SELECT FOR UPDATE`, best for short high-contention updates."
10. **JWT:** "A signed, self-contained token validated without a session lookup; keep it short-lived and use refresh tokens for revocation."
11. **Microservices consistency:** "No distributed transactions: each service commits locally, publishes events through an outbox, consumers are idempotent, and failures trigger compensating actions in a saga."
12. **Circuit breaker:** "After failures pass a threshold the breaker opens and calls fail fast with a fallback; after a wait it lets a few trial calls through to decide whether to close."
13. **Virtual threads:** "JVM-managed threads that unmount from carrier threads while blocked, so blocking code scales to huge concurrency; I still limit access to scarce downstream resources."
14. **Liveness vs readiness:** "Liveness failing means restart the container; readiness failing means stop sending it traffic. Readiness must not depend on shared downstreams."

## Interview-day checklist
```text
[ ] Clarify before answering: scope, scale, constraints
[ ] Structure answers: definition, how it works, example, trade-off, pitfall
[ ] For scenarios: hypothesize, diagnose with tools, fix, prevent
[ ] For coding: restate, examples, approach plus complexity, code, test aloud
[ ] For design: requirements, estimates, API, data, diagram, deep dives, trade-offs
[ ] Say "I don't know exactly, but here is how I would find out" instead of guessing
[ ] Use OrderHub or your real project as the example whenever possible
[ ] Ask the interviewer one thoughtful question at the end (team practices, on-call, tech debt)
```

## Rapid-fire flashcards

Q: What range does the Integer cache cover by default?
A: -128 to 127.

Q: What does `1 + 2 + "3" + 4 + 5` print in Java?
A: `3345`.

Q: Which collection should you choose for a thread-safe map with atomic updates?
A: `ConcurrentHashMap` with `compute`, `merge` or `computeIfAbsent`.

Q: At what bucket size does a Java 8+ HashMap bin become a tree?
A: 8 entries, provided the table has at least 64 buckets.

Q: What is the default rollback rule of Spring `@Transactional`?
A: Roll back on unchecked exceptions and errors, commit on checked exceptions.

Q: Why is `@Transactional` ignored on a method called via `this`?
A: Self-invocation bypasses the Spring proxy that applies the transaction.

Q: Which annotation enables `@PreAuthorize` in Spring Security 6?
A: `@EnableMethodSecurity`.

Q: What does `spring.jpa.open-in-view=false` change?
A: The persistence context closes with the transaction, so lazy loading outside the service fails fast instead of issuing hidden queries.

Q: Which ID strategy disables Hibernate JDBC insert batching?
A: `GenerationType.IDENTITY`.

Q: What HTTP status should an optimistic locking conflict return?
A: 409 Conflict.

Q: What is the difference between liveness and readiness probes?
A: Liveness failure restarts the container; readiness failure removes it from load balancing.

Q: Which Kafka delivery guarantee is the default for consumers, and what must consumers therefore be?
A: At-least-once; consumers must be idempotent.

Q: Which property enables virtual threads in Spring Boot 3.2+?
A: `spring.threads.virtual.enabled=true`.

Q: When was Spring Boot 4.0 released and what Java version does it require at minimum?
A: November 2025; Java 17 minimum, with Java 25 recommended.

Q: What are the three things a strong scenario answer always contains after the fix?
A: How you confirmed the cause, how you prevent recurrence (test or alert), and the trade-off of the fix.
