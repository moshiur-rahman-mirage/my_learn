@@ 3 | TOPIC | Practice — Phase 14 review: Spring Security | Cheat sheet, key snippets, top 10 questions and rapid-fire flashcards for Spring Security | MUST
# Phase 14 review: Spring Security

## Cheat sheet

| Idea | Remember |
|---|---|
| Authentication | Who are you? Credentials in, authenticated `Authentication` out |
| Authorization | What may you do? Rules on URLs, methods, objects |
| 401 vs 403 | 401 = not authenticated (entry point); 403 = authenticated but denied (access denied handler) |
| Entry into Spring | Tomcat → `DelegatingFilterProxy` → `FilterChainProxy` → first matching `SecurityFilterChain` |
| Filter order | Context holder, headers, CORS, CSRF, logout, auth filters, anonymous, exception translation, `AuthorizationFilter` last |
| Where the user lives | `SecurityContextHolder`, a `ThreadLocal`; cleared after each request |
| Users | `UserDetailsService.loadUserByUsername`; `DaoAuthenticationProvider` checks the password |
| Passwords | `createDelegatingPasswordEncoder()` = `{bcrypt}`; slow, salted, adaptive; never SHA-256 |
| Config style | `SecurityFilterChain` `@Bean`, lambda DSL; no `WebSecurityConfigurerAdapter` (removed 6.0) |
| Rule order | Top to bottom, first match wins, `anyRequest()` last |
| `hasRole("X")` | Checks `ROLE_X`; `hasAuthority` checks the exact string |
| Stateless | `SessionCreationPolicy.STATELESS`, token on every request |
| JWT | header.payload.signature, Base64URL, signed not encrypted; short `exp` |
| JWT in Spring | `oauth2ResourceServer(rs -> rs.jwt())`, `JwtDecoder`, `JwtAuthenticationConverter` |
| Default JWT authorities | `scope` claim → `SCOPE_x` |
| OAuth2 roles | Resource owner, client, authorization server, resource server |
| Flows | Auth code + PKCE (users), client credentials (services); implicit and password deprecated |
| OIDC | ID token + userinfo + discovery on top of OAuth2 |
| Method security | `@EnableMethodSecurity`, `@PreAuthorize`, proxy-based, self-invocation bypasses it |
| CSRF | Needed when cookies authenticate; safe to disable for header-only bearer APIs |
| CORS | Browser relaxation of SOP; configure `CorsConfigurationSource` + `http.cors()` |
| Top vulnerabilities | IDOR, injection, mass assignment, XSS, exposed actuators, leaked secrets |

### Key code snippets
```java
// The modern filter chain (stateless JWT API)
@Bean
SecurityFilterChain api(HttpSecurity http, JwtAuthenticationConverter converter) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .oauth2ResourceServer(rs -> rs.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .build();
}
```

```java
// Password encoder and user loading
@Bean
PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}

@Override
public UserDetails loadUserByUsername(String username) {
    return repo.findByUsername(username)
               .map(u -> User.withUsername(u.username).password(u.passwordHash).roles(u.role.name()).build())
               .orElseThrow(() -> new UsernameNotFoundException(username));
}
```

```java
// Map a "roles" claim to ROLE_ authorities
@Bean
JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
    roles.setAuthoritiesClaimName("roles");
    roles.setAuthorityPrefix("ROLE_");
    JwtAuthenticationConverter c = new JwtAuthenticationConverter();
    c.setJwtGrantedAuthoritiesConverter(roles);
    return c;
}
```

```java
// Ownership with method security
@PreAuthorize("hasRole('ADMIN') or @orderAccess.isOwner(#orderId, authentication)")
public OrderDto getOrder(Long orderId) { return repo.findById(orderId).map(OrderDto::from).orElseThrow(); }
```

```yaml
# Trusting an external identity provider
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://login.example.com/realms/orderhub
          audiences: orderhub-api
```

```java
// Testing
mvc.perform(get("/api/orders/mine")
        .with(jwt().jwt(j -> j.claim("cid", 7L)).authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
   .andExpect(status().isOk());
```

## The 10 most likely interview questions

1. **Authentication vs authorization, 401 vs 403?** Identity vs permission; 401 means log in, 403 means you are known but not allowed.
2. **How does a request flow through Spring Security?** `DelegatingFilterProxy` hands it to `FilterChainProxy`, which picks a `SecurityFilterChain`; auth filters fill the `SecurityContext`, `ExceptionTranslationFilter` maps failures, `AuthorizationFilter` decides.
3. **How do you configure security in Boot 3?** A `SecurityFilterChain` bean with the lambda DSL; `UserDetailsService` and `PasswordEncoder` beans; the adapter class is gone.
4. **How are passwords stored?** BCrypt (or Argon2) via `DelegatingPasswordEncoder` with `{id}` prefixes, salted and slow; upgrades through `UserDetailsPasswordService`.
5. **How does JWT authentication work?** Login once, receive a signed token, send it as a bearer header; the server verifies signature and expiry with no session.
6. **How do you revoke a JWT?** Short expiry, revocable rotating refresh tokens, denylist of `jti` or token-version check if instant revocation is needed.
7. **OAuth2 vs OIDC and which flow when?** OAuth2 = delegated authorization with access tokens; OIDC adds identity via ID tokens; auth code + PKCE for users, client credentials for services.
8. **How do you make customers see only their own orders?** `@PreAuthorize` with an ownership bean, user-scoped queries, `/me` endpoints, never trusting ids from the body.
9. **Why might `@PreAuthorize` not work?** Missing `@EnableMethodSecurity`, self-invocation, private or final methods, object not a Spring bean, missing `-parameters`.
10. **Should you disable CSRF, and how is CORS different?** Disable only when no cookies authenticate; CORS lets other origins read responses and must be wired into the security chain for preflights.

## Rapid-fire flashcards

Q: Which Spring Security filter makes the final access decision for a URL?
A: `AuthorizationFilter`.

Q: Which bean name does DelegatingFilterProxy look up?
A: `springSecurityFilterChain`, the `FilterChainProxy`.

Q: What does Spring Boot create when you add the security starter and define nothing?
A: A chain securing all requests with form login and HTTP Basic, plus a generated user whose password is logged at startup.

Q: What is the default BCrypt strength in Spring Security?
A: 10 (2^10 rounds).

Q: What error do you get when a stored password has no {id} prefix and a DelegatingPasswordEncoder is used?
A: "There is no PasswordEncoder mapped for the id null".

Q: Which starter adds JWT validation for an API in Spring Boot?
A: `spring-boot-starter-oauth2-resource-server`.

Q: Which header and value does a JWT client send?
A: `Authorization: Bearer <token>`.

Q: Which claim holds a JWT's expiry and in what unit?
A: `exp`, seconds since the Unix epoch.

Q: Which annotation replaced @EnableGlobalMethodSecurity?
A: `@EnableMethodSecurity` (Spring Security 5.6, default in 6).

Q: Which HTTP method does a browser use for a CORS preflight?
A: `OPTIONS`.

Q: Which CSRF token repository suits a JavaScript frontend that must read the token?
A: `CookieCsrfTokenRepository.withHttpOnlyFalse()`.

Q: How do you make @Async methods see the caller's SecurityContext?
A: Wrap the executor in `DelegatingSecurityContextAsyncTaskExecutor`.

@@ 3 | TOPIC | Practice — Mock interview: Intermediate round | A 45-minute mid-level interview script across Phases 6 to 14 with model answers and weak, good and strong rubrics | MUST
# Mock interview: Intermediate round

A realistic 45-minute mid-level Java and Spring Boot interview covering the Intermediate phases: generics and modern Java (6), streams (7), collections internals (8), concurrency (9), Spring core (10), REST (11), JPA (12), testing (13) and security (14). Run it with a friend acting as the interviewer, or answer aloud with a timer and record yourself. Read the model answer only after answering.

**Timing**

| Block | Minutes | What it checks |
|---|---|---|
| 1. Warm-up | 5 | Can you describe your work clearly? |
| 2. Concept questions | 15 | Depth on core Java and Spring |
| 3. Coding task | 12 | Clean, correct code under time pressure |
| 4. Scenario (production problem) | 7 | Debugging reasoning |
| 5. Design question | 6 | Putting the pieces together |

**How to score yourself:** for each block pick weak (1), good (2) or strong (3). 13 to 15 total means you are ready for mid-level rounds; 9 to 12 means review the weak phases; below 9 means go back through the phase reviews first.

## 1. Warm-up (5 minutes)

**Interviewer:** "Tell me about a backend project you built. What was the stack, and what was the hardest technical problem?"

**Model answer (spoken, about 90 seconds):**
"I built OrderHub, an order management backend in Java 21 and Spring Boot 3.5 with PostgreSQL. It exposes REST endpoints for products, customers and orders, uses Spring Data JPA with Flyway migrations, and is secured with JWT through Spring Security's resource server support, with roles and method-level ownership checks so customers only see their own orders. It is tested with JUnit 5, Mockito, MockMvc slices and Testcontainers. The hardest problem was performance on the order list endpoint: it made one query per order to load lines, the N+1 problem. I found it by enabling SQL logging in a test, and fixed it with a fetch join through an entity graph plus pagination, which took the endpoint from about 200 queries to 2. I also wrote a test that asserts the query count so it does not regress."

| Weak | Good | Strong |
|---|---|---|
| Lists technologies only; no problem or no detail | Describes the system and one problem | Clear structure (what, stack, problem, how found, how fixed, result with numbers, prevention) in under two minutes |

## 2. Concept questions (15 minutes)

### Q1. How does `HashMap` work, and what changed in Java 8?
**Model answer:** A `HashMap` is an array of buckets. `put` computes `hashCode()`, spreads the high bits (`h ^ (h >>> 16)`), and picks a bucket with `(n - 1) & hash`. Collisions are chained in a linked list; since Java 8 a bucket with more than 8 entries (when the table has at least 64 buckets) becomes a red-black tree, so worst-case lookup is O(log n) instead of O(n). When size exceeds capacity times load factor (0.75), the table doubles and entries are redistributed. Keys must have consistent `equals` and `hashCode` and should be immutable, or entries become unreachable.

| Weak | Good | Strong |
|---|---|---|
| "It stores key-value pairs" | Buckets, hashing, collisions, resize | Adds treeification thresholds, hash spreading, mutable-key bug, not thread-safe and when to use `ConcurrentHashMap` |

### Q2. What is the difference between `synchronized`, `volatile` and `AtomicInteger`?
**Model answer:** `volatile` guarantees visibility and ordering for a single variable but not atomicity, so `count++` on a volatile is still a race. `synchronized` gives mutual exclusion plus visibility: only one thread runs the block, and changes are visible to the next thread that takes the same lock. `AtomicInteger` uses lock-free compare-and-set for atomic single-variable updates like `incrementAndGet`, faster than locking under low contention. Use `volatile` for flags, atomics for counters, and locks for invariants that span several variables.

| Weak | Good | Strong |
|---|---|---|
| Confuses visibility with atomicity | Correct distinction with the `count++` example | Mentions happens-before, CAS, `LongAdder` for high contention, and picks the tool per case |

### Q3. How does `@Transactional` work, and when does it not work?
**Model answer:** Spring creates a proxy around the bean. On a call to an annotated public method, the proxy's `TransactionInterceptor` asks the `PlatformTransactionManager` to begin or join a transaction depending on propagation (default `REQUIRED`), calls the method, and commits, or rolls back on unchecked exceptions and `Error` (checked exceptions do not roll back unless `rollbackFor` says so). It does not apply when a method calls another annotated method in the same class (self-invocation skips the proxy), on private methods, or when the exception is caught inside the method.

| Weak | Good | Strong |
|---|---|---|
| "It makes the method a transaction" | Proxy, commit and rollback, self-invocation | Adds propagation (`REQUIRES_NEW` use case), rollback rules for checked exceptions, `readOnly` benefits, and that security and transaction proxies share the same mechanism |

### Q4. What is the N+1 problem in JPA and how do you fix it?
**Model answer:** Loading N parents with one query and then touching a lazy association on each triggers N more queries. Fixes: a fetch join (`join fetch`) or `@EntityGraph` for the use case, batch fetching (`@BatchSize` or `hibernate.default_batch_fetch_size`) to load children in groups, or a DTO projection that selects only what the screen needs. Pagination with a collection fetch join is a trap (Hibernate paginates in memory), so page the parent ids first, then fetch.

| Weak | Good | Strong |
|---|---|---|
| Suggests switching everything to EAGER | Explains the cause and fetch join | Adds batch size, projections, the pagination trap and how to detect it (SQL logs, query-count assertions) |

### Q5. Walk me through what happens in Spring Security when a request with a JWT arrives.
**Model answer:** The servlet container calls `DelegatingFilterProxy`, which delegates to `FilterChainProxy`. It picks the matching `SecurityFilterChain`. `BearerTokenAuthenticationFilter` extracts the token and calls the `JwtDecoder`, which verifies the signature, expiry and issuer; the `JwtAuthenticationConverter` maps claims to authorities, and the resulting `JwtAuthenticationToken` goes into the `SecurityContextHolder`. `AuthorizationFilter` then checks the URL rules; on denial, `ExceptionTranslationFilter` returns 401 for anonymous or 403 for authenticated users. If allowed, the request reaches the controller, where method security can apply further checks. The context is cleared at the end of the request.

| Weak | Good | Strong |
|---|---|---|
| "Spring checks the token" | Filters, decoder, context, authorization | Names the proxy and chain selection, 401 vs 403 path, claims-to-authorities mapping, statelessness and context clearing |

### Q6. Unit test vs slice test vs `@SpringBootTest`?
**Model answer:** A unit test instantiates the class directly with mocks (Mockito), runs in milliseconds and tests logic. A slice test like `@WebMvcTest` or `@DataJpaTest` starts only part of the context (web layer or JPA layer) to test framework integration such as JSON mapping, validation, security rules or queries. `@SpringBootTest` starts the whole application, often with Testcontainers for a real database, to verify wiring end to end; it is the slowest, so you have fewer of them. That is the test pyramid.

| Weak | Good | Strong |
|---|---|---|
| "We use `@SpringBootTest` for everything" | Explains the three levels | Adds context caching, `@MockitoBean`, Testcontainers with `@ServiceConnection`, and what each level should catch |

## 3. Coding task (12 minutes)

**Interviewer:** "Given a list of orders, return the total revenue per customer for PAID orders only, sorted by revenue descending, top 3. Then expose it as `GET /api/admin/reports/top-customers` for admins only."

```java
record Order(long id, String customer, String status, BigDecimal total) { }
```

**Model answer:**
```java
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TopCustomers {

    record Order(long id, String customer, String status, BigDecimal total) { }
    record CustomerRevenue(String customer, BigDecimal revenue) { }

    static List<CustomerRevenue> topCustomers(List<Order> orders, int limit) {
        Map<String, BigDecimal> revenue = orders.stream()
                .filter(o -> "PAID".equals(o.status()))
                .collect(Collectors.groupingBy(Order::customer,
                         Collectors.reducing(BigDecimal.ZERO, Order::total, BigDecimal::add)));
        return revenue.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))        // stable tie-break
                .limit(limit)
                .map(e -> new CustomerRevenue(e.getKey(), e.getValue()))
                .toList();
    }

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order(1, "alice", "PAID", new BigDecimal("50.00")),
                new Order(2, "bob", "PAID", new BigDecimal("120.00")),
                new Order(3, "alice", "PAID", new BigDecimal("80.00")),
                new Order(4, "carol", "CANCELLED", new BigDecimal("999.00")),
                new Order(5, "dave", "PAID", new BigDecimal("30.00")),
                new Order(6, "erin", "PAID", new BigDecimal("130.00")));
        topCustomers(orders, 3).forEach(System.out::println);
    }
}
```
Output:
```text
CustomerRevenue[customer=alice, revenue=130.00]
CustomerRevenue[customer=erin, revenue=130.00]
CustomerRevenue[customer=bob, revenue=120.00]
```
The endpoint:
```java
@RestController
@RequestMapping("/api/admin/reports")
class ReportController {
    private final ReportService reports;
    ReportController(ReportService reports) { this.reports = reports; }

    @GetMapping("/top-customers")
    @PreAuthorize("hasRole('ADMIN')")
    List<CustomerRevenue> topCustomers(@RequestParam(defaultValue = "3") @Min(1) @Max(100) int limit) {
        return reports.topCustomers(limit);
    }
}
```
**What to say while coding:** "`BigDecimal` for money, never `double`. `groupingBy` with `reducing` sums per customer. I sort by value descending with a tie-break so the output is deterministic. In production I would not load all orders into memory; I would push this into the database with a JPQL aggregate: `select new CustomerRevenue(o.customer.name, sum(o.total)) from Order o where o.status = 'PAID' group by o.customer.name order by sum(o.total) desc` with a `Pageable` of size 3, and the URL rule for `/api/admin/**` already requires ADMIN, with `@PreAuthorize` as defense in depth."

| Weak | Good | Strong |
|---|---|---|
| Uses `double`, loops with mutable maps and bugs, forgets the filter | Correct stream solution and secured endpoint | Also tie-break, validation of `limit`, explains moving the aggregation into SQL, and mentions a test |

## 4. Scenario question (7 minutes)

**Interviewer:** "After a release, users report that the 'My orders' page sometimes shows a 500 error, and logs show `LazyInitializationException: could not initialize proxy - no Session`. At the same time, the new admin endpoint `POST /api/admin/products` returns 403 for admins. How do you investigate both?"

**Model answer:**
"For the `LazyInitializationException`: it means a lazy association was touched after the persistence context closed. With `spring.jpa.open-in-view` disabled (which we do on purpose), that happens when a controller or Jackson serializes an entity whose lazy collection was not loaded inside the transaction. 'Sometimes' suggests it depends on data, for example only orders with lines or only when the second-level path is hit. I would find the stack trace line where serialization touches `Order.lines`, then fix it by returning a DTO built inside a `@Transactional(readOnly = true)` service method, using a fetch join or entity graph for the lines. I would not fix it by enabling open-in-view or switching to EAGER. Then I add a test with `@DataJpaTest` or a MockMvc test over real data.

For the 403 on POST: GET endpoints work, so authentication is fine. A 403 only on a POST points to CSRF first: if the new release added a session-based chain or changed the chain order, the API request might now be handled by a chain with CSRF enabled. I would turn on TRACE logging for `org.springframework.security` and look for 'Invalid CSRF token' or which chain matched. The second suspect is authorities: if the new endpoint uses `hasAuthority('ADMIN')` while tokens carry `ROLE_ADMIN`, or the JWT converter changed and roles now come through as `SCOPE_`. I would decode a real admin token and compare with the rule. The fix is whichever the log shows, plus a MockMvc test with `jwt()` for that endpoint so the regression cannot return."

| Weak | Good | Strong |
|---|---|---|
| Guesses ("restart", "make it EAGER", "disable security") | Explains the root cause of each and a reasonable fix | Systematic: reads the stack trace and security TRACE log, lists ranked hypotheses (CSRF, chain order, authority prefix), fixes properly (DTO in transaction, correct rule), adds regression tests |

## 5. Design question (6 minutes)

**Interviewer:** "Design the order placement endpoint for OrderHub: a customer places an order with several products. Stock must not go negative, the customer must not be charged twice if they double-click, and only the customer themselves can place orders for their account."

**Model answer (structure out loud):**
1. **API:** `POST /api/orders` with body `{ "lines": [{ "productId": 1, "quantity": 2 }] }` and an `Idempotency-Key` header. The customer id comes from the JWT (`cid` claim), never from the body. Response `201 Created` with `Location: /api/orders/{id}`; validation errors return 400 Problem Details; insufficient stock returns 409.
2. **Security:** URL rule `authenticated()` plus `hasRole('CUSTOMER')`; no customer id in the request means no IDOR.
3. **Validation:** Bean Validation on the request record (`@NotEmpty` lines, `@Positive` quantity, at most 50 lines).
4. **Transaction:** one `@Transactional` service method: load products, check and decrement stock, create the order and lines, compute the total with `BigDecimal` from the database prices (never trust prices from the client).
5. **Stock concurrency:** either optimistic locking with `@Version` on `Product` and a retry on `OptimisticLockException`, or an atomic conditional update `update product set stock = stock - :q where id = :id and stock >= :q` and check the updated row count. For hot products, the conditional update avoids retries.
6. **Double-click / idempotency:** store the `Idempotency-Key` with the customer id and the resulting order id in a table with a unique constraint; a repeated key returns the original response instead of creating a second order. A unique constraint is safer than a check-then-insert.
7. **Payment:** do not call the payment provider inside the database transaction; create the order as `PENDING_PAYMENT`, commit, then charge with the same idempotency key at the provider, and update the status (or publish an event after commit with `@TransactionalEventListener(phase = AFTER_COMMIT)`).
8. **Testing:** unit test the pricing and stock rules, `@WebMvcTest` for validation and security, a Testcontainers test that fires two concurrent orders for the last item and asserts exactly one succeeds.

```java
@PostMapping("/api/orders")
@PreAuthorize("hasRole('CUSTOMER')")
ResponseEntity<OrderDto> place(@Valid @RequestBody PlaceOrderRequest req,
                               @RequestHeader("Idempotency-Key") String key,
                               @AuthenticationPrincipal Jwt jwt) {
    Long customerId = ((Number) jwt.getClaim("cid")).longValue();
    OrderDto order = orderService.place(customerId, req, key);
    return ResponseEntity.created(URI.create("/api/orders/" + order.id())).body(order);
}
```
```java
@Modifying
@Query("update Product p set p.stock = p.stock - :qty where p.id = :id and p.stock >= :qty")
int decrementStock(@Param("id") Long id, @Param("qty") int qty);   // 0 rows updated = insufficient stock -> 409
```

| Weak | Good | Strong |
|---|---|---|
| Only describes the controller; trusts customer id and prices from the client | Transaction, validation, security from the token, some locking idea | Covers idempotency with a unique key, atomic stock update or optimistic locking with trade-offs, payment outside the transaction, after-commit events, error codes, and a concurrency test |

## After the mock: what to review
| If you were weak on | Go back to |
|---|---|
| HashMap, ConcurrentHashMap | Phase 8 topics and review |
| volatile, synchronized, atomics | Phase 9 topics |
| Proxies, `@Transactional` | Phase 10 and Phase 12 transactions |
| N+1, lazy loading | Phase 12 fetching topics |
| Test levels | Phase 13 review |
| Security flow, 401 vs 403, CSRF | [[Security Filter Chain Architecture]], [[CSRF, CORS and Common Vulnerabilities]] |
| Ownership checks | [[Method Security with @PreAuthorize]] |
