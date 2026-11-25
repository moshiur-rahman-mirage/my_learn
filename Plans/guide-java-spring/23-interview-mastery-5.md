@@ 3 | TOPIC | Practice — Mock interview: Full loop (Java, Spring, coding, design) | A full onsite-style loop of four 45-minute rounds plus behavioural, with model answers, code and a hiring rubric | MUST
# Mock interview: Full loop (Java, Spring, coding, design)

A complete interview loop as run by many product companies for a mid-to-senior Java backend role: four technical rounds of 45 minutes and a short behavioural round. Do it in one day with breaks, as close to real conditions as you can: a timer, a plain editor without auto-completion for the coding round, and a whiteboard or paper for design. Score each round with its rubric, then use the final table to decide what to review.

| Round | Focus | Time |
|---|---|---|
| A | Core Java and JVM | 45 min |
| B | Spring Boot and data | 45 min |
| C | Coding (two problems) | 45 min |
| D | System design | 45 min |
| E | Behavioural and project | 20 min |

---

## Round A: Core Java and JVM (45 minutes)

**Warm-up (5 min).** "What changed in Java between 8 and 21 that you actually use?"
**Model answer:** Lambdas and streams (8), `var` (10), `HttpClient` (11), switch expressions and text blocks (14 and 15), records (16), sealed classes and pattern matching for `instanceof` (16 and 17), pattern matching for `switch` and record patterns (21), virtual threads (21), sequenced collections (21). Pick three you used and say where: records for DTOs, switch patterns over a sealed `PaymentResult`, virtual threads in a Spring Boot 3.2+ service. Mention that Java 25 is the current LTS (scoped values final, compact source files and instance main methods, flexible constructor bodies).

**Concept questions (25 min)**

1. **"How does `HashMap` work, and what changed in Java 8?"**
   An array of buckets; `hash()` spreads the high bits of `hashCode()` and the index is `(n - 1) & hash`. Collisions chain in a linked list; since Java 8 a bucket with 8 or more entries (when the table has at least 64 buckets) becomes a red-black tree, so worst-case lookup is O(log n) instead of O(n). The table doubles when size exceeds capacity times load factor (0.75), and Java 8 splits each bucket into "stay" and "move by old capacity" lists without rehashing every key. Mutable keys or a broken `equals`/`hashCode` contract break lookups.

2. **"`ConcurrentHashMap` vs `Collections.synchronizedMap`?"**
   `synchronizedMap` wraps every method in one lock, so all threads serialize, and compound actions still need external locking. `ConcurrentHashMap` (Java 8+) uses CAS for empty buckets and synchronizes only on the first node of a bucket, reads are lock-free, and it offers atomic compound operations (`computeIfAbsent`, `merge`). It forbids null keys and values so that `get` returning null is unambiguous. Iterators are weakly consistent and never throw `ConcurrentModificationException`.

3. **"Explain the Java Memory Model's happens-before in practical terms."**
   Without synchronization, one thread may never see another's writes, or see them in a different order, because of CPU caches and compiler reordering. Happens-before edges guarantee visibility and ordering: unlocking a monitor happens-before later locking of it, a volatile write happens-before later reads of that variable, `Thread.start()` happens-before the thread's actions, and a thread's actions happen-before another thread returns from `join()`. Final fields are safely visible after construction if `this` does not escape. Practical rule: share mutable state only through locks, volatile, atomics or concurrent collections.

4. **"A service has an `OutOfMemoryError: Metaspace` after many redeploys in an application server. Why?"**
   Metaspace holds class metadata. Each redeploy creates a new class loader; if anything from outside the application still references the old loader (a `ThreadLocal` in a container thread, a JDBC driver registered in `DriverManager`, a static cache in a shared library), the whole loader and all its classes cannot be unloaded. Find the GC root path to the old loader in a heap dump (Eclipse MAT "path to GC roots" on the class loader). Fat-jar Spring Boot apps restarted as processes avoid this class of leak.

5. **"What is the difference between `CompletableFuture.thenApply`, `thenCompose` and `thenCombine`?"**
   `thenApply` maps the result synchronously (`T -> U`). `thenCompose` chains another async step that returns a future (`T -> CompletableFuture<U>`), flattening it, like `flatMap`. `thenCombine` waits for two independent futures and merges their results. Error handling uses `exceptionally`, `handle` or `whenComplete`, and blocking I/O should run on a dedicated executor, not the common pool.

**Output question (5 min).**
```java
List<String> names = new ArrayList<>(List.of("ann", "bob", "cy"));
names.stream().map(String::toUpperCase);
names.replaceAll(String::toUpperCase);
System.out.println(names);
Integer a = 1000, b = 1000;
System.out.println(a.equals(b) + " " + (a == b));
```
**Answer:** `[ANN, BOB, CY]` then `true false`. The first stream has no terminal operation so it does nothing; `replaceAll` mutates the list in place. 1000 is outside the Integer cache, so `==` compares two different objects while `equals` compares values.

**Design-ish Java question (10 min).** "Design an immutable `Money` value type."
```java
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);   // normalize scale
    }

    public static Money of(String amount, String currencyCode) {
        return new Money(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money times(int quantity) {
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("currency mismatch: " + currency + " vs " + other.currency);
        }
    }

    public static void main(String[] args) {
        Money price = Money.of("19.999", "USD");
        System.out.println(price.times(3).plus(Money.of("0.5", "USD")));
        System.out.println(Money.of("2.0", "EUR").equals(Money.of("2.00", "EUR")));
    }
}
```
Output:
```text
Money[amount=60.50, currency=USD]
true
```
Points to say: a record gives final fields, `equals` and `hashCode`; the compact constructor validates and normalizes the scale so `2.0` and `2.00` are equal (plain `BigDecimal.equals` would say false); `BigDecimal` and `Currency` are immutable, so no defensive copies are needed; operations return new instances; mixing currencies fails fast.

| Area | Weak | Good | Strong |
|---|---|---|---|
| Collections | Knows APIs only | Explains buckets, treeification, CHM basics | Resize split, null rules, weakly consistent iteration, contract pitfalls |
| Concurrency and JMM | "Use synchronized" | Visibility vs atomicity | Happens-before edges, safe publication, executor choice |
| JVM | Vague about memory | Knows areas and GC basics | Diagnoses with dumps and GC roots, explains class loader leaks |
| Code | Mutable, `double` money | Immutable with BigDecimal | Normalized scale, validation, value semantics explained |

---

## Round B: Spring Boot and data (45 minutes)

**Concept questions (25 min)**

1. **"What happens when a Spring Boot application starts?"**
   `SpringApplication.run` prepares the `Environment` (property sources, profiles), creates the `ApplicationContext` type based on the classpath (servlet, reactive or none), loads bean definitions from component scanning and `@Configuration` classes, runs `BeanFactoryPostProcessor`s (including the one that processes `@Configuration` and auto-configuration imports with their conditions), registers `BeanPostProcessor`s, instantiates singletons (dependency injection, `@PostConstruct`, proxies for AOP and transactions), starts the embedded web server, then publishes `ApplicationStartedEvent`, runs `CommandLineRunner` and `ApplicationRunner` beans, and publishes `ApplicationReadyEvent`.

2. **"Explain propagation `REQUIRED` vs `REQUIRES_NEW` with a real use case."**
   `REQUIRED` (default) joins an existing transaction or starts one; an exception anywhere marks the whole transaction rollback-only. `REQUIRES_NEW` suspends the current transaction and runs in a new one with its own commit, using a second connection. Use case: an audit log entry that must be saved even if the business transaction rolls back. Caveats: it needs a separate bean (proxy), it can deadlock if the inner transaction waits for locks held by the suspended outer one, and it doubles connection usage under load.

3. **"How do you solve N+1 in Spring Data JPA, and what are the trade-offs of each approach?"**
   Fetch join in JPQL (one query, but cannot paginate collections safely and duplicates parent rows), `@EntityGraph` (declarative, same caveats), `@BatchSize` or `default_batch_fetch_size` (N/batch queries, works with pagination), and DTO projections (only needed columns, best for read endpoints). Detect it with Hibernate statistics or query-count assertions in tests, and keep `open-in-view` off.

4. **"How would you secure OrderHub's APIs?"**
   OAuth2 resource server validating JWTs from an identity provider (signature via JWKS, issuer, audience, expiry), a `SecurityFilterChain` that is stateless with CSRF disabled for the bearer-token API, URL rules for coarse access, `@PreAuthorize` for business rules such as "customers see only their orders", CORS for the web front end only, rate limiting at the gateway, secrets from a vault or Kubernetes secrets, and actuator endpoints on a separate port with authentication. Short access tokens plus refresh tokens handle revocation.

5. **"What is in your test strategy for a Spring Boot service?"**
   Many fast unit tests (JUnit 5, Mockito) for domain logic; slice tests (`@WebMvcTest` with MockMvc for controllers and validation, `@DataJpaTest` with Testcontainers PostgreSQL for queries); a few `@SpringBootTest` integration tests with Testcontainers for PostgreSQL and Kafka covering whole flows; contract tests for APIs and events consumed by other services; and load tests for critical endpoints. Reuse one container per test run and keep context configurations consistent so Spring's context cache works.

**Coding in Spring (15 min).** "Write the endpoint `GET /api/customers/{id}/orders?status=PAID&page=0&size=20` returning a page of order summaries. Only the customer themself or an admin may call it."

```java
// File: order-service/src/main/java/com/orderhub/order/api/CustomerOrdersController.java
package com.orderhub.order.api;

import jakarta.validation.constraints.Max;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class CustomerOrdersController {

    private final OrderQueryService queries;

    public CustomerOrdersController(OrderQueryService queries) {
        this.queries = queries;
    }

    @GetMapping("/api/customers/{customerId}/orders")
    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.token.claims['customer_id']")
    public Page<OrderSummary> orders(@PathVariable long customerId,
                                     @RequestParam(defaultValue = "PAID") OrderStatus status,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") @Max(100) int size) {
        return queries.byCustomer(customerId, status,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }
}
```

```java
// File: order-service/src/main/java/com/orderhub/order/api/OrderQueryService.java
package com.orderhub.order.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

interface OrderSummaryRepository extends JpaRepository<OrderEntity, Long> {
    @Query(value = """
            select new com.orderhub.order.api.OrderSummary(o.id, o.status, o.totalCents, o.createdAt)
            from OrderEntity o where o.customerId = :customerId and o.status = :status
            """,
           countQuery = "select count(o) from OrderEntity o where o.customerId = :customerId and o.status = :status")
    Page<OrderSummary> findSummaries(long customerId, OrderStatus status, Pageable pageable);
}

@Service
public class OrderQueryService {
    private final OrderSummaryRepository repo;

    public OrderQueryService(OrderSummaryRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Page<OrderSummary> byCustomer(long customerId, OrderStatus status, Pageable pageable) {
        return repo.findSummaries(customerId, status, pageable);
    }
}
```

Points to say: DTO projection (no entities leave the service, no N+1); `readOnly = true` lets Hibernate skip dirty checking and lets routing send it to a replica; size capped at 100 with method validation; `@PreAuthorize` uses the JWT claim (the claim name depends on your identity provider); an index on `(customer_id, status, created_at DESC)`; for deep pages switch to keyset pagination. In Boot 3.3+ you would also enable `spring.data.web.pageable.serialization-mode=via-dto` (or return a custom page DTO) for a stable JSON shape.

**Scenario (5 min).** "The `orders` endpoint works locally but returns 500 in production with `PSQLException: ERROR: column o.customer_id does not exist`."
**Model answer:** The schema and the code are out of sync: the Flyway migration did not run in production (disabled by profile, different `spring.flyway.locations`, or the app connects with a user lacking DDL rights and the migration failed earlier), or it ran against a different schema (`search_path`). Check the `flyway_schema_history` table and startup logs, and Actuator's `/actuator/flyway`. Fix by running the migration through the pipeline, and prevent with `spring.jpa.hibernate.ddl-auto=validate` so the app refuses to start with a mismatched schema.

| Area | Weak | Good | Strong |
|---|---|---|---|
| Container and startup | "Spring creates beans" | Main phases in order | Post-processors, conditions, proxies, events, runners |
| Transactions | Default behaviour only | Propagation with example | Connection usage, deadlock risk, proxy requirement |
| JPA | Knows N+1 name | Fetch join or entity graph | All options with pagination trade-offs and detection |
| Security | "Add JWT" | Resource server plus roles | Claims-based method security, CSRF reasoning, actuator hardening |
| Code | Returns entities | DTO page | Projection, validation, security expression, index, keyset mention |

---

## Round C: Coding (45 minutes, two problems)

**Interview etiquette:** restate the problem, ask about input sizes and edge cases, state the approach and complexity before coding, write clean code, then test with an example aloud.

### Problem 1 (15 min): Order analytics with streams
"Given a list of orders (customer, status, lines with product, quantity and unit price): (1) revenue per customer for PAID orders, highest first; (2) the top K products by quantity sold, excluding CANCELLED orders, ties broken by product name."

```java
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderAnalytics {

    record Order(long id, String customer, String status, List<Line> lines) { }
    record Line(String product, int quantity, BigDecimal unitPrice) {
        BigDecimal total() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
    }

    // 1. Revenue per customer for PAID orders, highest first
    static Map<String, BigDecimal> revenueByCustomer(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.status().equals("PAID"))
                .collect(Collectors.groupingBy(Order::customer,
                        Collectors.reducing(BigDecimal.ZERO,
                                o -> o.lines().stream().map(Line::total).reduce(BigDecimal.ZERO, BigDecimal::add),
                                BigDecimal::add)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    // 2. Top K products by quantity sold (all statuses except CANCELLED), ties broken by name
    static List<String> topProducts(List<Order> orders, int k) {
        return orders.stream()
                .filter(o -> !o.status().equals("CANCELLED"))
                .flatMap(o -> o.lines().stream())
                .collect(Collectors.groupingBy(Line::product, Collectors.summingInt(Line::quantity)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(k)
                .map(e -> e.getKey() + "=" + e.getValue())
                .toList();
    }

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order(1, "asha", "PAID", List.of(new Line("pen", 3, new BigDecimal("1.50")), new Line("book", 1, new BigDecimal("12.00")))),
                new Order(2, "lee", "PAID", List.of(new Line("book", 2, new BigDecimal("12.00")))),
                new Order(3, "asha", "CANCELLED", List.of(new Line("lamp", 5, new BigDecimal("30.00")))),
                new Order(4, "omar", "NEW", List.of(new Line("pen", 4, new BigDecimal("1.50")))),
                new Order(5, "asha", "PAID", List.of(new Line("mug", 2, new BigDecimal("8.25")))));

        System.out.println(revenueByCustomer(orders));
        System.out.println(topProducts(orders, 2));
    }
}
```

Output:
```text
{asha=33.00, lee=24.00}
[pen=7, book=3]
```

**Explanation:** asha's PAID orders total 4.50 + 12.00 + 16.50 = 33.00; the cancelled lamp order is excluded. `groupingBy` with a downstream `reducing` sums `BigDecimal` exactly (never `double` for money); sorting entries by value and collecting into a `LinkedHashMap` keeps the order. For top K, `summingInt` aggregates, and the comparator sorts by count descending then name. Complexity: O(n) aggregation plus O(m log m) sort over m distinct products; for huge m and small k, a size-k min-heap (`PriorityQueue`) gives O(m log k).

### Problem 2 (25 min): LRU cache
"Implement an LRU cache with O(1) `get` and `put` and a fixed capacity. Do not use `LinkedHashMap` (then tell me how you would with it). Make it safe for concurrent use."

```java
import java.util.HashMap;
import java.util.Map;

public class LruCache<K, V> {

    // Doubly linked list node: O(1) unlink and move-to-front
    private static final class Node<K, V> {
        final K key;
        V value;
        Node<K, V> prev, next;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> index = new HashMap<>();
    private final Node<K, V> head = new Node<>(null, null);   // sentinel: most recent after head
    private final Node<K, V> tail = new Node<>(null, null);   // sentinel: least recent before tail

    public LruCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public synchronized V get(K key) {
        Node<K, V> node = index.get(key);
        if (node == null) return null;
        unlink(node);
        addFront(node);                     // mark as most recently used
        return node.value;
    }

    public synchronized void put(K key, V value) {
        Node<K, V> node = index.get(key);
        if (node != null) {
            node.value = value;
            unlink(node);
            addFront(node);
            return;
        }
        if (index.size() == capacity) {
            Node<K, V> lru = tail.prev;     // evict least recently used
            unlink(lru);
            index.remove(lru.key);
        }
        node = new Node<>(key, value);
        addFront(node);
        index.put(key, node);
    }

    private void unlink(Node<K, V> n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void addFront(Node<K, V> n) {
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<K, V> n = head.next; n != tail; n = n.next) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(n.key).append('=').append(n.value);
        }
        return sb.append(']').toString();
    }

    public static void main(String[] args) {
        LruCache<String, Integer> cache = new LruCache<>(3);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        System.out.println(cache);
        cache.get("a");                     // a becomes most recent
        cache.put("d", 4);                  // evicts b
        System.out.println(cache);
        System.out.println("b -> " + cache.get("b"));
        cache.put("c", 30);                 // update moves c to front
        System.out.println(cache);
    }
}
```

Output:
```text
[c=3, b=2, a=1]
[d=4, a=1, c=3]
b -> null
[c=30, d=4, a=1]
```

**Explanation:** a `HashMap` from key to node gives O(1) lookup; a doubly linked list with sentinels gives O(1) move-to-front and eviction from the tail, with no null checks for edge cases. `synchronized` on every public method makes it thread-safe; note that even `get` mutates the list, so a read-write lock would not help (reads are writes here). For high concurrency, say you would use Caffeine (Window TinyLFU, lock-striped buffers) rather than a home-made cache. With `LinkedHashMap`: `new LinkedHashMap<>(16, 0.75f, true)` (access order) and override `removeEldestEntry` to return `size() > capacity`.

| Area | Weak | Good | Strong |
|---|---|---|---|
| Problem 1 | Uses `double`, unordered output | Correct collectors | Exact money, stable ordering, complexity, heap alternative for top K |
| Problem 2 | O(n) eviction or broken links | O(1) map plus list | Sentinels, thread safety with reasoning, LinkedHashMap and Caffeine alternatives |
| Process | Codes silently | Explains approach | Clarifies, states complexity first, tests with an example and edge cases |

---

## Round D: System design (45 minutes)

**Prompt:** "Design the order placement flow for an e-commerce platform handling 1 million orders per day with flash-sale peaks. Use Spring Boot for the services."

Run the 7-step framework from [[Practice — Coding drill: System design with Spring Boot (URL shortener, order service, notification service)]] using Design 2 (order service). The interviewer will push on these points; have the answers ready:

| Push | Strong answer |
|---|---|
| "Why not one database transaction across order, stock and payment?" | Separate services own their data; XA hurts availability and Kafka cannot join it. Saga with outbox and idempotent consumers gives eventual consistency with explicit compensations. |
| "How do you avoid overselling?" | Conditional `UPDATE ... WHERE available >= qty` plus a `CHECK` constraint; for flash sales, Redis Lua reservation with async persistence and reconciliation. |
| "Client retries after a timeout?" | `Idempotency-Key` unique per order request; the replay returns the original result. |
| "Payment provider down?" | Circuit breaker, order stays `PENDING_PAYMENT`, retry with backoff, expire after N minutes and release stock. |
| "How do you know it works in production?" | SLOs on placement latency and success rate, saga stage metrics, alerts on outbox lag and stuck orders, traces across services. |
| "What breaks first at 10 times the load?" | Hot stock rows and the orders write path: shard stock rows, partition orders, scale Kafka partitions, add a waiting room. |

```java
// The saga's state machine in order-service (orchestration variant, if asked)
public enum OrderStatus {
    PENDING, STOCK_RESERVED, CONFIRMED, FAILED, CANCELLED;

    public OrderStatus on(String event) {
        return switch (this) {
            case PENDING -> switch (event) {
                case "StockReserved" -> STOCK_RESERVED;
                case "StockRejected" -> FAILED;
                default -> throw new IllegalStateException(event + " in " + this);
            };
            case STOCK_RESERVED -> switch (event) {
                case "PaymentCaptured" -> CONFIRMED;
                case "PaymentFailed" -> FAILED;      // triggers ReleaseStock compensation
                default -> throw new IllegalStateException(event + " in " + this);
            };
            case CONFIRMED, FAILED, CANCELLED -> throw new IllegalStateException("terminal state " + this);
        };
    }
}
```

| Area | Weak | Good | Strong |
|---|---|---|---|
| Requirements and estimates | None | Listed | Quantified and used to justify choices |
| Architecture | Boxes without reasons | Sensible services, DB, Kafka | Clear ownership, sync vs async boundaries, failure isolation |
| Consistency | Ignores it | Saga named | Outbox, idempotency, compensations, ordering keys |
| Operations | Not mentioned | Metrics mentioned | SLOs, alerts, tracing, rollout and rollback plan |

---

## Round E: Behavioural and project (20 minutes)

Questions (pick three):
1. "Tell me about a project you are proud of." (2-minute pitch)
2. "Tell me about a production incident you handled."
3. "Tell me about a disagreement with a colleague or manager."
4. "Tell me about a mistake and what you changed afterwards."
5. "How do you keep your skills current?"

Model answers: use the STAR stories in [[Practice — Coding drill: Explain your project (STAR and architecture walkthrough)]]. For question 5, be concrete: "I follow the Spring and Java release notes; for Boot 4 I upgraded a side project and wrote down the migration issues (Jackson 3 packages, `@MockitoBean`, modular starters); I read the JEPs for virtual threads and structured concurrency and benchmarked them against WebFlux."

| Weak | Good | Strong |
|---|---|---|
| Generic, "we", no numbers | Structured STAR | First-person ownership, measured results, honest learning, concise |

---

## Final hiring rubric
| Rounds at Strong | Rounds at Weak | Typical decision |
|---|---|---|
| 3 or more | 0 | Strong hire, possibly senior level |
| 2 | 0 | Hire |
| 1 | 1 | Lean hire or lean no, depending on which round was weak |
| 0 | 2 or more | No hire; review the weak areas with the phase reviews and retry in two weeks |

After the loop, write down every question where you hesitated and map it to its phase. Re-read those topic notes and their Recall cards, then repeat the round that scored lowest.
