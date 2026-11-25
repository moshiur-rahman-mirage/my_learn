@@ 3 | TOPIC | Practice — Coding drill: Spring Boot scenario questions (30) | Thirty production scenarios on transactions, JPA, performance, security, messaging, caching and deployment with model answers and code | MUST
# Coding drill: Spring Boot scenario questions (30)

Thirty "in production we saw X, what do you do?" questions. These are the heart of mid and senior Spring Boot interviews. For each one, answer aloud in under 4 minutes using this structure, then open the model answer:

1. **Clarify and hypothesize:** name the two or three most likely causes.
2. **Diagnose:** what you would look at (logs, metrics, thread dump, SQL log, actuator endpoint) to confirm.
3. **Fix:** the concrete change, ideally with code.
4. **Prevent:** a test, alert or design rule so it does not happen again.

The model answers use OrderHub (order, inventory and notification services; PostgreSQL, Redis, Kafka; Spring Boot 3.5). Code snippets show only the relevant part.

## 1. The rollback that did not happen
`OrderService.placeOrder` is `@Transactional`. It saves the order, then calls `paymentGateway.charge()`, which throws `PaymentDeclinedException extends Exception`. The order row stays in the database. Why?

<details><summary>Model answer</summary>

By default Spring rolls back only on unchecked exceptions (`RuntimeException` and `Error`). A checked exception commits the transaction, mirroring EJB semantics. Confirm by enabling `logging.level.org.springframework.transaction.interceptor=TRACE`, which logs "Completing transaction... after exception" with commit.

Fix either by making the exception unchecked (preferred for business failures) or by declaring the rule:

```java
@Transactional(rollbackFor = PaymentDeclinedException.class)
public Order placeOrder(PlaceOrderCommand cmd) throws PaymentDeclinedException {
    Order order = orders.save(Order.from(cmd));
    paymentGateway.charge(order.getId(), order.getTotal());   // throws checked exception
    return order;
}
```

Better design: do not call a slow external payment API inside a database transaction at all (it holds a connection for the whole call). Save the order as `PENDING_PAYMENT`, commit, charge, then update status, with a saga or outbox for consistency. Prevent regressions with an integration test that asserts the row count after a declined payment.
</details>

## 2. @Transactional ignored
`OrderService.importAll()` loops and calls `this.importOne(row)`, which is annotated `@Transactional(propagation = REQUIRES_NEW)`. One bad row rolls back nothing and the others are not isolated either. Why?

<details><summary>Model answer</summary>

Spring transactions are applied by a proxy around the bean. `this.importOne(...)` is a self-invocation: it calls the target object directly and bypasses the proxy, so the annotation is never seen. The same applies to `@Async`, `@Cacheable`, `@Retryable` and `@PreAuthorize`.

Fixes, in order of preference: move `importOne` into a separate bean, or use `TransactionTemplate` programmatically.

```java
@Service
class OrderImportService {
    private final RowImporter importer;            // separate bean, called through its proxy
    OrderImportService(RowImporter importer) { this.importer = importer; }

    void importAll(List<Row> rows) {
        for (Row row : rows) {
            try { importer.importOne(row); }
            catch (RuntimeException e) { log.warn("row {} failed: {}", row.id(), e.getMessage()); }
        }
    }
}

@Service
class RowImporter {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importOne(Row row) { /* save entities */ }
}
```

Mention also that `@Transactional` on a `private` method is ignored with proxies, and on `final` methods with CGLIB. Prevent with an integration test that checks one failing row does not affect others.
</details>

## 3. LazyInitializationException in the controller
`GET /api/orders/42` returns the `Order` entity. Jackson serialization fails with `LazyInitializationException: could not initialize proxy - no Session` on `order.lines`. What happened and what is the right fix?

<details><summary>Model answer</summary>

`lines` is `@OneToMany` (lazy by default). The service's transaction (and persistence context) closed when the service method returned; Jackson touches the lazy collection afterwards, outside any session. (If `spring.jpa.open-in-view` were left at its default `true`, it would "work" by keeping the session open during rendering, which hides N+1 queries and holds connections longer; Boot logs a warning for that reason.)

Right fix: never return entities from controllers. Load exactly what the response needs inside the transaction and map to a DTO.

```java
// Spring Data JPA repository (org.springframework.data.jpa.repository.JpaRepository)
interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o left join fetch o.lines where o.id = :id")
    Optional<Order> findWithLines(Long id);
}

@Transactional(readOnly = true)
public OrderDto get(long id) {
    Order o = orders.findWithLines(id).orElseThrow(() -> new OrderNotFoundException(id));
    return OrderDto.from(o);                       // mapping happens while the session is open
}
```

Set `spring.jpa.open-in-view: false` explicitly so the problem shows up in tests rather than as hidden queries in production. Avoid `FetchType.EAGER` as a "fix": it causes N+1 elsewhere.
</details>

## 4. The order list makes 101 queries
`GET /api/orders?page=0&size=100` takes 1.2 s. The SQL log shows one select on `orders` followed by 100 selects on `customers`. Fix it.

<details><summary>Model answer</summary>

Classic N+1: the list query loads orders, then mapping to DTOs touches `order.getCustomer().getName()` for each lazy `@ManyToOne` proxy. Confirm with `spring.jpa.show-sql` or better, Hibernate statistics (`spring.jpa.properties.hibernate.generate_statistics=true`) and a datasource proxy that counts queries per request.

Fixes: a fetch join or entity graph for the page, or a DTO projection that selects only what the screen needs.

```java
interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "customer")
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // Or a projection: one query, only the needed columns
    @Query("""
        select new com.orderhub.order.api.OrderRow(o.id, c.name, o.status, o.total)
        from Order o join o.customer c
        where o.status = :status
        """)
    Page<OrderRow> findRows(OrderStatus status, Pageable pageable);
}
```

Do not `join fetch` a collection (`@OneToMany`) together with pagination: Hibernate paginates in memory and warns `HHH90003004`. For collections, use `@BatchSize(size = 50)` or `hibernate.default_batch_fetch_size` to turn N queries into N/50. Prevent with a test asserting the query count (for example with a datasource-proxy based assertion).
</details>

## 5. "Connection is not available, request timed out after 30000ms"
Under a traffic spike, the order service logs `SQLTransientConnectionException` from HikariCP and response times jump to 30 s. CPU on the database is 20 percent. What is going on?

<details><summary>Model answer</summary>

The pool (10 connections by default) is exhausted and threads wait `connectionTimeout` (30 s) for a connection. Since the database is idle, connections are being **held** rather than used: long transactions that wrap remote calls, a leaked connection, open-in-view keeping connections during rendering, or slow queries holding locks.

Diagnose: Actuator metrics `hikaricp.connections.active`, `.pending` and `hikaricp.connections.usage` (time held); enable `leakDetectionThreshold`; thread dump to see what holding threads are doing (often waiting on HTTP inside `@Transactional`).

```yaml
spring:
  jpa:
    open-in-view: false
  datasource:
    hikari:
      maximum-pool-size: 20          # sized from measurement, not guessed
      connection-timeout: 2000       # fail fast instead of piling up for 30 s
      leak-detection-threshold: 10000
```

Fix the root cause: move external calls out of transactions, add indexes for slow queries, and keep transactions short. Increasing the pool helps only up to what the database can handle (a common starting formula is cores times 2 plus effective spindles on the DB server). With virtual threads enabled, add a bulkhead because the thread pool no longer limits concurrency.
</details>

## 6. p99 latency spikes every few minutes
Average latency is 40 ms, but p99 jumps to 2 s every few minutes. How do you investigate?

<details><summary>Model answer</summary>

Periodic spikes suggest something periodic: GC pauses, a scheduled job, cache expiry causing a stampede, connection pool resizing, or a noisy neighbour. Steps:

1. Correlate the spike times on dashboards: `jvm.gc.pause` (Micrometer), CPU throttling in Kubernetes (`container_cpu_cfs_throttled_periods_total`), Hikari pending connections, cache hit ratio, and `@Scheduled` job logs.
2. Pick a slow trace in the tracing UI (Tempo or Zipkin) and see which span is slow: DB, downstream HTTP or the service itself.
3. Capture a JFR recording during a spike: `jcmd <pid> JFR.start duration=5m filename=spike.jfr`.

Typical fixes: tune GC or move to G1 or ZGC with a sensible heap and container memory limit; raise CPU limits if throttled; add jitter to cache TTLs and use request coalescing; move the batch job to a separate instance or off-peak.

```java
@Bean
RedisCacheManagerBuilderCustomizer jitteredTtl() {
    return builder -> builder.withCacheConfiguration("products",
            RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10).plusSeconds(ThreadLocalRandom.current().nextInt(120))));
}
```

Prevent with an SLO alert on p99 rather than averages.
</details>

## 7. NoUniqueBeanDefinitionException after adding a second implementation
OrderHub had one `PaymentGateway` (`StripeGateway`). After adding `PaypalGateway`, startup fails with `NoUniqueBeanDefinitionException: expected single matching bean but found 2`. How do you fix it properly?

<details><summary>Model answer</summary>

Injection by type now finds two candidates. Options: mark one as `@Primary` (a default), inject by `@Qualifier` where a specific one is needed, or inject all of them and choose at runtime (strategy pattern), which is the right model if the gateway depends on the order.

```java
interface PaymentGateway {
    String provider();
    PaymentResult charge(long orderId, BigDecimal amount);
}

@Service
class PaymentRouter {
    private final Map<String, PaymentGateway> byProvider;

    PaymentRouter(List<PaymentGateway> gateways) {         // Spring injects every implementation
        this.byProvider = gateways.stream()
                .collect(Collectors.toMap(PaymentGateway::provider, g -> g));
    }

    PaymentResult charge(Order order) {
        PaymentGateway gw = byProvider.get(order.getPaymentProvider());
        if (gw == null) throw new UnsupportedProviderException(order.getPaymentProvider());
        return gw.charge(order.getId(), order.getTotal());
    }
}
```

Mention that a `Map<String, PaymentGateway>` can also be injected directly, keyed by bean name.
</details>

## 8. Startup fails with a circular reference
After a refactor, startup fails: "The dependencies of some of the beans in the application context form a cycle: orderService -> paymentService -> orderService". It worked in Boot 2.5. What do you do?

<details><summary>Model answer</summary>

Since Boot 2.6, circular references are prohibited by default (`spring.main.allow-circular-references=false`). With constructor injection a cycle can never be satisfied; with field injection Spring used to resolve it with early references, hiding a design problem.

Do not just set the property to true. Break the cycle: extract the shared logic into a third bean, or invert the dependency with an event.

```java
// Before: PaymentService calls orderService.markPaid(...)  -> cycle
// After: PaymentService publishes an event, OrderService listens
@Service
class PaymentService {
    private final ApplicationEventPublisher events;
    PaymentService(ApplicationEventPublisher events) { this.events = events; }

    void onPaymentConfirmed(long orderId) {
        events.publishEvent(new PaymentConfirmed(orderId));
    }
}

@Service
class OrderService {
    @TransactionalEventListener
    void on(PaymentConfirmed e) { /* mark order paid */ }
}
```

`@Lazy` on one constructor parameter also breaks the cycle at runtime (it injects a proxy), but treat it as a temporary measure.
</details>

## 9. Production uses the wrong database URL
In Kubernetes, the order service connects to `localhost:5432` instead of the production database, although `application-prod.yml` has the right URL. How do you debug and fix it?

<details><summary>Model answer</summary>

Likely causes: the `prod` profile is not active (`SPRING_PROFILES_ACTIVE` missing or misspelled), the file is not on the classpath (wrong resource folder or not packaged), or a higher-precedence source overrides it (an environment variable like `SPRING_DATASOURCE_URL`, a ConfigMap mounted as `config/application.yml`, or a command-line argument).

Diagnose: the startup log line "The following 1 profile is active: ..."; the Actuator `env` endpoint (secured) shows each property with its source, for example `spring.datasource.url` from `systemEnvironment`; `configprops` shows bound values.

```yaml
# deployment.yaml (excerpt)
env:
  - name: SPRING_PROFILES_ACTIVE
    value: prod
  - name: SPRING_DATASOURCE_URL
    valueFrom:
      secretKeyRef:
        name: orderhub-db
        key: url
```

Remember the order (highest wins): command-line args, then OS environment variables, then profile-specific files outside the jar, then files outside the jar, then profile-specific inside, then `application.yml` inside. Prevent with a startup check bean that fails fast when the URL contains `localhost` under the `prod` profile.
</details>

## 10. Two support agents edit the same order
Agent A changes the shipping address, agent B changes the status at the same moment. B's save overwrites A's address change. Fix it.

<details><summary>Model answer</summary>

A lost update: both read version N, both write, last write wins. Add optimistic locking with a `@Version` column; Hibernate adds `where version = ?` to the update and throws `ObjectOptimisticLockingFailureException` when zero rows are updated.

```java
@Entity
class Order {
    @Id @GeneratedValue Long id;
    @Version Long version;               // incremented on every update
    String shippingAddress;
    @Enumerated(EnumType.STRING) OrderStatus status;
}

@RestControllerAdvice
class ConcurrencyErrors {
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ProblemDetail conflict() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "The order was changed by someone else. Reload and try again.");
    }
}
```

Expose the version to the client as an `ETag` and require `If-Match` on `PUT` so the check covers the whole read-edit-save cycle across HTTP requests. Use pessimistic locking (`@Lock(PESSIMISTIC_WRITE)`) only for short, high-contention operations like decrementing stock.
</details>

## 11. Customers are charged twice
The mobile app times out on `POST /api/payments` and retries; sometimes customers are charged twice. Design the fix.

<details><summary>Model answer</summary>

`POST` is not idempotent, and a timeout does not mean the first request failed. Require an `Idempotency-Key` header (a UUID generated by the client per payment attempt). Store the key with the result; a repeated key returns the stored response instead of charging again. A unique constraint makes it safe under concurrent duplicates.

```java
@PostMapping("/api/payments")
public ResponseEntity<PaymentResponse> pay(@RequestHeader("Idempotency-Key") UUID key,
                                           @Valid @RequestBody PaymentRequest req) {
    return idempotency.find(key)
            .map(saved -> ResponseEntity.ok(saved))                        // replay stored result
            .orElseGet(() -> ResponseEntity.status(HttpStatus.CREATED).body(payments.charge(key, req)));
}

@Transactional
public PaymentResponse charge(UUID key, PaymentRequest req) {
    idempotencyRepo.save(new IdempotencyRecord(key, "IN_PROGRESS"));     // unique PK on key
    PaymentResponse res = gateway.charge(req, key.toString());           // pass the key to the provider too
    idempotencyRepo.complete(key, res);
    return res;
}
```

A concurrent duplicate hits the unique constraint (`DataIntegrityViolationException`), which you map to 409 "request in progress". Pass the same key to the payment provider (Stripe and others support idempotency keys) so the external side is safe too. Expire keys after 24 hours.
</details>

## 12. Event sent, transaction rolled back
After saving an order, the service publishes `OrderPlaced` to Kafka inside the same `@Transactional` method. Sometimes the inventory service reserves stock for orders that do not exist. Why, and how do you fix it?

<details><summary>Model answer</summary>

The Kafka send and the database commit are two systems without a shared transaction (the dual-write problem). If the DB commit fails after the send, consumers see a phantom event; if the app crashes after commit but before send, the event is lost.

Fix with the transactional outbox: write the event into an `outbox` table in the same DB transaction, and publish it asynchronously (a poller, or Debezium CDC reading the table).

```java
@Transactional
public Order place(PlaceOrderCommand cmd) {
    Order order = orders.save(Order.from(cmd));
    outbox.save(new OutboxEvent(UUID.randomUUID(), "Order", order.getId(),
            "OrderPlaced", json.write(OrderPlaced.from(order))));        // same transaction
    return order;
}

@Scheduled(fixedDelay = 500)
@Transactional
public void relay() {
    for (OutboxEvent e : outbox.findTop100ByPublishedFalseOrderByCreatedAt()) {
        kafka.send("orders", e.aggregateId().toString(), e.payload()).join();
        e.markPublished();
    }
}
```

Delivery is at-least-once, so consumers must be idempotent (store processed event ids). A quick partial fix is `@TransactionalEventListener(phase = AFTER_COMMIT)` to send after commit, but it can still lose events on a crash.
</details>

## 13. A Kafka consumer processes the same message twice
The notification service sometimes sends the same email twice. Why does this happen and how do you make it safe?

<details><summary>Model answer</summary>

Kafka gives at-least-once delivery by default: if the consumer processes a record and crashes (or a rebalance happens) before committing the offset, the record is delivered again. Producer retries can also duplicate without idempotent producers.

Make the consumer idempotent: record processed event ids in the same transaction as the side effect, or use a natural unique key.

```java
@KafkaListener(topics = "orders", groupId = "notification")
@Transactional
public void on(OrderShipped event) {
    if (processed.existsById(event.eventId())) return;      // duplicate: skip
    processed.save(new ProcessedEvent(event.eventId()));    // unique PK
    emailOutbox.save(Email.shippedNotice(event));           // actual send happens via outbox relay
}
```

Configure `enable.idempotence=true` on producers (the default since Kafka 3.0) and a `DefaultErrorHandler` with a dead-letter topic for poison messages so one bad record does not block the partition. Exactly-once semantics in Kafka only cover Kafka-to-Kafka pipelines, not emails or external databases.
</details>

## 14. A slow dependency takes the whole service down
The shipping-quote API starts responding in 20 s. Within minutes the order service stops responding to all endpoints, even those that never call shipping. Explain and fix.

<details><summary>Model answer</summary>

Cascading failure through resource exhaustion: every request calling shipping holds a request thread (and maybe a DB connection) for 20 s; the shared Tomcat pool fills up, and unrelated endpoints queue behind them. Fix in layers:

1. **Timeouts** on the client (connect 1 s, read 2 s). Without a timeout nothing else helps.
2. **Circuit breaker** to fail fast while shipping is unhealthy and serve a fallback.
3. **Bulkhead** to cap concurrent shipping calls so they cannot take every thread.

```java
@Bean
RestClient shippingClient(RestClient.Builder builder) {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(Duration.ofSeconds(1));
    factory.setReadTimeout(Duration.ofSeconds(2));
    return builder.baseUrl("http://shipping").requestFactory(factory).build();
}

@CircuitBreaker(name = "shipping", fallbackMethod = "defaultQuote")
@Bulkhead(name = "shipping")
public Quote quote(long orderId) {
    return shippingClient.get().uri("/quotes/{id}", orderId).retrieve().body(Quote.class);
}

Quote defaultQuote(long orderId, Throwable t) {
    return Quote.flatRate();                       // degrade gracefully
}
```

Add retries only with backoff and only for idempotent calls, and alert on circuit state changes (`resilience4j.circuitbreaker.state` metric).
</details>

## 15. Memory climbs until OOMKilled
The inventory service pod is OOMKilled every two days. Heap usage graph climbs steadily even after full GCs. How do you find and fix it?

<details><summary>Model answer</summary>

A steadily rising post-GC baseline means a leak: objects still reachable. Common culprits in Spring apps: an unbounded `static Map` or home-made cache, `ThreadLocal` values never removed in pooled threads, listeners registered but never removed, or a huge Hibernate persistence context in a long batch transaction. If the heap is fine but the pod is killed, it is native memory: container limit too close to `-Xmx` (metaspace, thread stacks, direct buffers).

Diagnose: run with `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps`, or take a dump with `jcmd <pid> GC.heap_dump /dumps/inv.hprof`; open it in Eclipse MAT and check the Dominator Tree and "Leak Suspects".

```java
// Leak found: unbounded cache keyed by request parameters
private static final Map<String, StockView> CACHE = new ConcurrentHashMap<>();   // grows forever

// Fix: bounded cache with expiry (Caffeine via Spring Cache)
@Bean
CacheManager cacheManager() {
    CaffeineCacheManager m = new CaffeineCacheManager("stock");
    m.setCaffeine(Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(Duration.ofMinutes(5)));
    return m;
}
```

For containers, prefer `-XX:MaxRAMPercentage=75` over a fixed `-Xmx` and leave headroom for non-heap memory. Alert on post-GC heap, not on raw heap usage.
</details>

## 16. Startup takes 50 seconds
Kubernetes kills new pods because startup exceeds the probe budget. The app takes 50 s to start. How do you speed it up?

<details><summary>Model answer</summary>

First measure: enable `BufferingApplicationStartup` and read `/actuator/startup` to see which beans take longest; check the log timestamps for Flyway, Hibernate and connection establishment.

```java
public static void main(String[] args) {
    SpringApplication app = new SpringApplication(InventoryApplication.class);
    app.setApplicationStartup(new BufferingApplicationStartup(2048));
    app.run(args);
}
```

Typical findings and fixes: a bean doing remote calls in `@PostConstruct` (move to an `ApplicationReadyEvent` listener or make it lazy); a large classpath scan (narrow `@ComponentScan`, remove unused starters); Hibernate schema validation on hundreds of entities; Flyway with many migrations (squash baselines). Platform options: Class Data Sharing (`-XX:SharedArchiveFile`, or Boot 3.3+ CDS support), Spring AOT, GraalVM native image for very fast starts, and CRaC. On the Kubernetes side, use a `startupProbe` with a generous budget so liveness does not kill a slow but healthy start.
</details>

## 17. Logged-out users can still call the API
OrderHub uses stateless JWTs with a 24-hour expiry. A user logs out (or an account is disabled), but their token still works. What do you do?

<details><summary>Model answer</summary>

A stateless JWT is valid until it expires; the server keeps no session to delete. Options:

- **Short-lived access tokens** (5 to 15 minutes) plus a **refresh token** stored server side (rotated on each use, revocable). Logout revokes the refresh token; the access token dies soon.
- **Denylist** of revoked token ids (`jti`) in Redis with TTL equal to the remaining token lifetime, checked in a filter. Cheap if the list is small.
- **Token version** per user stored in the DB or cache and embedded in the token; incrementing it invalidates all of a user's tokens (useful on password change or disable).

```java
@Component
class RevocationFilter extends OncePerRequestFilter {
    private final StringRedisTemplate redis;
    RevocationFilter(StringRedisTemplate redis) { this.redis = redis; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwt
                && Boolean.TRUE.equals(redis.hasKey("revoked:" + jwt.getToken().getId()))) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token revoked");
            return;
        }
        chain.doFilter(req, res);
    }
}
```

Register it after the bearer token filter. In an OAuth2 setup with an authorization server (Keycloak and similar), short access tokens plus refresh token revocation is the standard answer.
</details>

## 18. CORS error in the browser, Postman works
The React app on `https://shop.orderhub.com` calling `https://api.orderhub.com/api/orders` gets "blocked by CORS policy". Postman works fine. Fix it.

<details><summary>Model answer</summary>

CORS is enforced by browsers, not servers; Postman ignores it. The browser sends a preflight `OPTIONS` for non-simple requests (JSON body, `Authorization` header) and needs `Access-Control-Allow-Origin` and related headers in the response. With Spring Security, the preflight must be handled before authentication, otherwise it gets 401 without CORS headers.

```java
@Bean
SecurityFilterChain api(HttpSecurity http) throws Exception {
    return http
            .cors(Customizer.withDefaults())               // uses the CorsConfigurationSource bean
            .csrf(csrf -> csrf.disable())                  // stateless JWT API
            .authorizeHttpRequests(a -> a.anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
            .build();
}

@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOrigins(List.of("https://shop.orderhub.com"));
    c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    c.setAllowedHeaders(List.of("Authorization", "Content-Type", "Idempotency-Key"));
    c.setAllowCredentials(true);
    c.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", c);
    return source;
}
```

Never combine `allowCredentials(true)` with origin `*` (browsers reject it); use `allowedOriginPatterns` if you need wildcards. If a gateway sits in front, configure CORS in one place only, or duplicate headers will break it.
</details>

## 19. GET works, POST returns 403
After adding Spring Security with form login for the admin UI, every `POST` from the JavaScript client returns 403, while `GET` works. Why?

<details><summary>Model answer</summary>

CSRF protection is enabled by default for session-based security. State-changing requests (`POST`, `PUT`, `DELETE`) must include a CSRF token; without it the `CsrfFilter` rejects them with 403. `GET` is considered safe and is not checked.

For a browser app using session cookies, keep CSRF and send the token: Spring Security can expose it in a cookie the JavaScript reads.

```java
http.csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())   // cookie XSRF-TOKEN
        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()));          // as in the Spring Security SPA guide
// The client sends header X-XSRF-TOKEN with the cookie value (Axios and Angular do this automatically).
```

For a pure stateless API authenticated with bearer tokens in the `Authorization` header (no cookies), CSRF can be disabled because the browser does not attach the token automatically. Turning on `logging.level.org.springframework.security=DEBUG` shows exactly which filter rejected the request.
</details>

## 20. Page 5,000 of the order history is very slow
`GET /api/orders?page=5000&size=20` takes 4 s, while page 0 takes 20 ms. Why, and what do you change?

<details><summary>Model answer</summary>

Offset pagination (`LIMIT 20 OFFSET 100000`) makes the database read and discard 100,000 rows before returning 20, and Spring Data's `Page` also runs a `count(*)` over the whole filter. Cost grows with the page number.

Switch deep or infinite-scroll pagination to keyset (seek) pagination: remember the last seen sort key and query "after" it, using an index.

```java
interface OrderRepository extends JpaRepository<Order, Long> {
    // Index: CREATE INDEX idx_orders_customer_created ON orders (customer_id, created_at DESC, id DESC);
    @Query("""
        select o from Order o
        where o.customerId = :customerId
          and (o.createdAt < :lastCreatedAt or (o.createdAt = :lastCreatedAt and o.id < :lastId))
        order by o.createdAt desc, o.id desc
        """)
    List<Order> nextPage(long customerId, Instant lastCreatedAt, long lastId, Limit limit);
}
```

Return a cursor (`nextCursor` encoding `createdAt` and `id`) instead of page numbers. Use `Slice` instead of `Page` when the UI does not need a total count. Spring Data 3.1+ also offers `ScrollPosition` and `Window` for keyset scrolling.
</details>

## 21. @Async does nothing
`notificationService.sendWelcomeEmail(user)` is annotated `@Async`, but the HTTP request still waits 3 s for the email, and when the SMTP server is down, nobody sees the error. Explain.

<details><summary>Model answer</summary>

Three separate problems are common: `@EnableAsync` is missing; the method is called from the same class (self-invocation bypasses the proxy); or it is invoked but its exceptions vanish because a `void` `@Async` method has nowhere to throw to.

```java
@Configuration
@EnableAsync
class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(4);
        ex.setMaxPoolSize(8);
        ex.setQueueCapacity(500);                 // bounded queue
        ex.setThreadNamePrefix("async-");
        ex.initialize();
        return ex;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.error("Async failure in {}", method.getName(), ex);
    }
}
```

Return `CompletableFuture<Void>` when the caller needs the outcome. For emails that must not be lost, `@Async` is the wrong tool (a pod restart loses queued tasks): write to an outbox table or publish to a queue. Since Boot 3.2 with virtual threads enabled, the auto-configured async executor uses virtual threads.
</details>

## 22. The nightly job runs three times
A `@Scheduled(cron = "0 0 2 * * *")` job that charges subscriptions ran three times after scaling to three pods. Fix it.

<details><summary>Model answer</summary>

`@Scheduled` runs in every JVM; Spring has no cluster awareness. Use a distributed lock so only one instance runs each execution, and make the job idempotent anyway.

```java
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
class SchedulingConfig {
    @Bean
    LockProvider lockProvider(DataSource ds) {
        return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(ds))
                .usingDbTime()
                .build());
    }
}

@Component
class SubscriptionBilling {
    @Scheduled(cron = "0 0 2 * * *", zone = "UTC")
    @SchedulerLock(name = "subscriptionBilling", lockAtLeastFor = "PT1M")
    void chargeDueSubscriptions() { /* idempotent: charge only subscriptions not billed for this period */ }
}
```

ShedLock stores the lock in a `shedlock` table (or Redis). Alternatives: a Kubernetes `CronJob` running a separate process, Quartz in clustered mode, or Spring Batch with a job repository that rejects duplicate job instances. Idempotency (a unique "billed for period" constraint) is the safety net.
</details>

## 23. Users see stale prices after an update
Product prices are cached with `@Cacheable("products")` using the default cache. After an admin updates a price, some users see the old price for hours. There are four pods. Explain and fix.

<details><summary>Model answer</summary>

The default `ConcurrentMapCacheManager` is a local, in-memory cache with no expiry. The update evicts only on the pod that handled it (if eviction exists at all); the other three keep the stale entry forever.

Use a shared cache (Redis) with TTLs and evict or update on write:

```java
@Cacheable(cacheNames = "products", key = "#id")
public ProductDto get(long id) { return ProductDto.from(repo.findById(id).orElseThrow()); }

@CachePut(cacheNames = "products", key = "#result.id()")
@Transactional
public ProductDto updatePrice(long id, BigDecimal price) {
    Product p = repo.findById(id).orElseThrow();
    p.changePrice(price);
    return ProductDto.from(p);
}
```

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 10m
```

Caveat: `@CachePut` runs before the transaction commits, so a rollback leaves a wrong cache entry; evicting after commit (`@TransactionalEventListener`) is safer. For hot keys, protect against stampedes with `@Cacheable(sync = true)`. If you need local caches for speed, use a two-level cache with invalidation messages via Redis pub/sub.
</details>

## 24. Validation is skipped for order lines
`POST /api/orders` with `@Valid @RequestBody CreateOrderRequest` rejects a blank customer name, but accepts lines with quantity -5. Why?

<details><summary>Model answer</summary>

Bean Validation does not cascade into nested objects or collection elements unless the field is marked `@Valid`. The constraints on `LineRequest` exist but are never evaluated.

```java
record CreateOrderRequest(
        @NotBlank String customerName,
        @NotEmpty @Size(max = 50) List<@Valid LineRequest> lines) { }   // cascade into each element

record LineRequest(
        @NotBlank String productId,
        @Positive @Max(100) int quantity) { }
```

Related traps: validating method parameters of a service (`@NotNull Long id`) requires `@Validated` on the class (it uses an AOP proxy); `@Valid` on `@RequestParam` or `@PathVariable` constraints is enforced in Spring 6.1+ via built-in method validation. Failures surface as `MethodArgumentNotValidException` (body) or `HandlerMethodValidationException` (parameters), which your `@RestControllerAdvice` should map to a 400 `ProblemDetail` listing field errors.
</details>

## 25. Errors return HTML and inconsistent JSON
Some failing endpoints return a Whitelabel HTML page, some return `{"timestamp":..., "error": ...}`, and some return a custom `{"msg": ...}`. Clients cannot parse errors reliably. Standardize it.

<details><summary>Model answer</summary>

Use one `@RestControllerAdvice` that maps exceptions to RFC 9457 `ProblemDetail` responses (`application/problem+json`), and enable Spring's built-in problem details for framework exceptions.

```yaml
spring:
  mvc:
    problemdetails:
      enabled: true
```

```java
@RestControllerAdvice
class ApiErrors extends ResponseEntityExceptionHandler {     // handles Spring MVC exceptions as ProblemDetail

    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail notFound(OrderNotFoundException e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        pd.setType(URI.create("https://api.orderhub.com/problems/order-not-found"));
        pd.setProperty("orderId", e.orderId());
        return pd;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail unexpected(Exception e) {
        log.error("Unhandled error", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");  // no stack traces to clients
    }
}
```

Errors thrown in filters (security) never reach the advice; configure the `AuthenticationEntryPoint` and `AccessDeniedHandler` to write `ProblemDetail` too. Add the trace id as a property so support can find the logs.
</details>

## 26. Importing 100,000 products takes 10 minutes
A CSV import calls `repository.saveAll(products)` in one transaction. It takes 10 minutes and sometimes runs out of memory. Speed it up.

<details><summary>Model answer</summary>

Causes: `GenerationType.IDENTITY` disables Hibernate JDBC batching (each insert must return its id immediately), so there are 100,000 round trips; the persistence context holds 100,000 managed entities (memory and slower dirty checking); batching is not even enabled by default.

```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
        order_inserts: true
  datasource:
    url: jdbc:postgresql://db/orderhub?reWriteBatchedInserts=true   # PostgreSQL driver multi-row inserts
```

```java
// Spring Data JPA entity and service (org.springframework.transaction.annotation.Transactional)
@Entity
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 50)
    Long id;
}

@Transactional
public void importChunk(List<Product> chunk) {
    for (int i = 0; i < chunk.size(); i++) {
        em.persist(chunk.get(i));
        if (i % 50 == 49) { em.flush(); em.clear(); }    // keep the persistence context small
    }
}
```

For pure bulk loads, `JdbcTemplate.batchUpdate` or PostgreSQL `COPY` is faster still, and Spring Batch adds chunking, restartability and skip policies. Commit in chunks (for example 1,000 rows) so a failure does not roll back everything.
</details>

## 27. Every deployment causes a burst of 502 errors
During rolling updates, clients see 502 and connection reset errors for about 10 seconds. Fix the deployment.

<details><summary>Model answer</summary>

Two gaps: new pods receive traffic before they are ready, and old pods are killed while requests are in flight (or before the load balancer stops routing to them).

```yaml
# application.yml
server:
  shutdown: graceful                  # stop accepting, finish in-flight requests
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
management:
  endpoint:
    health:
      probes:
        enabled: true                 # /actuator/health/liveness and /readiness
```

```yaml
# deployment.yaml (excerpt)
readinessProbe:
  httpGet: { path: /actuator/health/readiness, port: 8080 }
  periodSeconds: 5
livenessProbe:
  httpGet: { path: /actuator/health/liveness, port: 8080 }
  periodSeconds: 10
lifecycle:
  preStop:
    exec:
      command: ["sh", "-c", "sleep 10"]   # let endpoints and load balancers deregister the pod
terminationGracePeriodSeconds: 45
strategy:
  rollingUpdate: { maxUnavailable: 0, maxSurge: 1 }
```

The `preStop` sleep covers the delay between Kubernetes sending SIGTERM and the load balancer removing the pod. `terminationGracePeriodSeconds` must exceed preStop plus graceful shutdown time. Readiness must not include downstream dependencies that would make all pods unready at once.
</details>

## 28. Following a request across services
A customer reports that order 9182 failed. The request passed through the gateway, order service and inventory service. Logs are in Loki, but you cannot connect lines across services. What do you set up?

<details><summary>Model answer</summary>

Distributed tracing with context propagation, plus trace ids in every log line. In Boot 3, Micrometer Tracing with the OpenTelemetry bridge propagates W3C `traceparent` headers through `RestClient`, `WebClient`, `KafkaTemplate` and listeners (when built from Boot's auto-configured builders), and puts `traceId` and `spanId` into the MDC.

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
```

```yaml
management:
  tracing:
    sampling:
      probability: 0.1               # 10 percent in production, 1.0 locally
  otlp:
    tracing:
      endpoint: http://otel-collector:4318/v1/traces
logging:
  pattern:
    correlation: "[${spring.application.name:},%X{traceId:-},%X{spanId:-}] "
```

```java
// Add business context so you can search by order id
@Observed(name = "order.place", contextualName = "place-order")
public Order place(PlaceOrderCommand cmd) {
    MDC.put("orderId", String.valueOf(cmd.orderId()));
    try { return doPlace(cmd); } finally { MDC.remove("orderId"); }
}
```

Use structured JSON logs (Boot 3.4+ `logging.structured.format.console=ecs`) so Loki or Elasticsearch can filter by `traceId` and `orderId`. Return the trace id in error responses for support. For Kafka, enable observation on the template and listener container (`spring.kafka.template.observation-enabled=true`, `spring.kafka.listener.observation-enabled=true`).
</details>

## 29. One endpoint calls three services sequentially
`GET /api/orders/{id}/details` calls customer, inventory and shipping services one after the other (150 ms each, 450 ms total). They are independent. Make it faster in Spring MVC.

<details><summary>Model answer</summary>

Run the calls concurrently; total latency becomes the slowest call (about 150 ms). In Spring MVC use `CompletableFuture` with a dedicated executor (never the common pool for blocking I/O), or virtual threads.

```java
@Service
class OrderDetailsService {
    private final ExecutorService io = Executors.newVirtualThreadPerTaskExecutor();   // Java 21

    OrderDetails details(long id) {
        var customer = CompletableFuture.supplyAsync(() -> customers.get(id), io);
        var stock    = CompletableFuture.supplyAsync(() -> inventory.check(id), io);
        var shipping = CompletableFuture.supplyAsync(() -> shippingApi.status(id), io)
                .completeOnTimeout(ShippingStatus.UNKNOWN, 300, TimeUnit.MILLISECONDS)
                .exceptionally(ex -> ShippingStatus.UNKNOWN);                         // optional dependency

        return CompletableFuture.allOf(customer, stock, shipping)
                .thenApply(v -> new OrderDetails(customer.join(), stock.join(), shipping.join()))
                .orTimeout(1, TimeUnit.SECONDS)
                .join();
    }
}
```

Discuss which calls are required (fail the request) versus optional (fallback), propagate tracing context to the worker threads (Micrometer's `ContextExecutorService` or Boot's auto-configured task executor with a `ContextPropagatingTaskDecorator`), and set per-call timeouts. With Java 25+, structured concurrency (preview) expresses this with automatic cancellation of siblings.
</details>

## 30. Renaming a column without downtime
The `orders.cust_name` column must become `customer_name`. The service runs 4 pods with rolling deploys, so old and new versions run at the same time. Plan the Flyway migration.

<details><summary>Model answer</summary>

A plain `ALTER TABLE ... RENAME COLUMN` breaks the old pods still running during the rollout. Use expand and contract across several releases:

```sql
-- Release 1: V12__add_customer_name.sql (expand)
ALTER TABLE orders ADD COLUMN customer_name VARCHAR(100);
UPDATE orders SET customer_name = cust_name WHERE customer_name IS NULL;   -- batch it for big tables
```

```java
// Release 1 code: write both columns, read the new one with fallback
@Column(name = "customer_name") String customerName;
@Column(name = "cust_name")     String legacyCustName;

@PrePersist @PreUpdate
void syncLegacy() { this.legacyCustName = this.customerName; }
```

```sql
-- Release 2: V13__backfill_and_constrain.sql (after all pods run release 1)
UPDATE orders SET customer_name = cust_name WHERE customer_name IS NULL;
ALTER TABLE orders ALTER COLUMN customer_name SET NOT NULL;

-- Release 3: V14__drop_cust_name.sql (contract, after no code reads cust_name)
ALTER TABLE orders DROP COLUMN cust_name;
```

Every migration must be backward compatible with the previous app version. Big backfills run in batches to avoid long locks; on PostgreSQL, create indexes with `CREATE INDEX CONCURRENTLY` (in a non-transactional Flyway migration). Never edit an applied migration: Flyway's checksum validation will fail the startup.
</details>

## Score yourself
| Level | What your answers looked like |
|---|---|
| Weak | Named a fix without diagnosis, or guessed ("increase the pool", "add @Transactional") |
| Good | Correct cause and fix with the right Spring feature |
| Strong | Cause, how to confirm it with concrete tools, fix with code, prevention (test, alert, design rule) and trade-offs |
