@@ 1 | MODULE | Phase 3 — Performance Antipatterns | Common causes of poor performance under load | SHOULD
# Phase 3: Performance antipatterns

Interviewers ask: "The system is slow under load. How do you find and fix it?" Each antipattern below is a **symptom → cause → fix** you can name quickly.

@@ 2 | MODULE | Performance Antipatterns | Ten classic mistakes and their fixes | SHOULD
Recognize and fix common bottlenecks.

@@ 3 | TOPIC | Busy Database | Too much logic and load pushed onto the database | SHOULD
# Busy database

**Symptom:** DB CPU 90%, app servers idle, latency grows with load.
**Cause:** business logic in stored procedures, heavy computation/formatting in SQL, every read hits the DB.

## Fixes
- Move computation to the app tier (easy to scale out); keep the DB for storage and set-based filtering.
- **Cache** hot reads, add **read replicas**, add missing **indexes**, move reports to a replica/warehouse.
- Use pagination and projections.

```java
// Bad: DB sorts/aggregates 5M rows for every dashboard hit
@Query(value = "SELECT ... GROUP BY ... ORDER BY ...", nativeQuery = true)
// Better: pre-aggregate on a schedule / materialized view, and cache the result
@Cacheable("dashboard") public Stats dashboard() { return statsRepo.latest(); }
```

Q: Why not scale the DB by adding more app servers?
A: More app servers produce more DB connections and queries. If the DB is the bottleneck you must reduce its work (cache, indexes, replicas, sharding).

@@ 3 | TOPIC | Busy Frontend | Heavy processing on the request thread or the browser | SHOULD
# Busy front end

**Symptom:** request threads blocked doing CPU-heavy or long work (image resize, PDF, large JSON transform), so the server cannot accept new requests.

## Fixes
- Offload heavy work to **background workers/queue**; respond `202 Accepted`.
- In the browser: Web Workers, pagination/virtual lists.
- Use a dedicated pool for CPU work instead of Tomcat threads.

```java
@PostMapping("/reports")
ResponseEntity<Void> create(@RequestBody ReportReq req) {
    queue.send(req);                       // worker generates the PDF
    return ResponseEntity.accepted().build();
}
```
- Tomcat has ~200 threads; 200 concurrent 5-second PDF jobs freeze the whole API.

Q: How do you detect it?
A: Thread dump shows request threads busy in your code; high CPU on web tier; response time degrades for unrelated endpoints.

@@ 3 | TOPIC | Chatty I/O | Many small network or disk calls instead of a few big ones | SHOULD
# Chatty I/O

**Symptom:** thousands of tiny DB queries/HTTP calls per request; latency dominated by round trips.

## Classic: N+1 queries and loop-calls

```java
// Chatty: 1 + N calls
for (Long id : orderIds) { users.add(userClient.get(id)); }

// Batched: 1 call
List<User> users = userClient.getAll(userIds);              // POST /users/batch
```
- JPA N+1: use `join fetch`, `@EntityGraph`, `@BatchSize`.
- JDBC: `saveAll` with `hibernate.jdbc.batch_size=50` and `order_inserts=true`.

## Fixes
- Batch, aggregate (BFF/GraphQL), cache, use bulk endpoints, co-locate chatty services.

Q: 1 ms per call × 1000 calls?
A: ~1 second added to one request. Round trips, not bandwidth, cause the damage; batching collapses them.

@@ 3 | TOPIC | Extraneous Fetching | Retrieving more data than needed | SHOULD
# Extraneous fetching

**Symptom:** big payloads, high DB I/O and memory, though the screen shows 3 fields.

## Causes and fixes
- `SELECT *` / loading whole entities → **projections**.
- Returning all rows → **pagination**.
- Eager fetching of large relations → `LAZY` + fetch explicitly.

```java
interface OrderSummary { Long getId(); BigDecimal getTotal(); }          // interface projection
Page<OrderSummary> findByUserId(Long userId, Pageable p);                  // only 2 columns, paged
```
- DTOs instead of entities in controllers; avoid serializing `@OneToMany` graphs.
- Filter at the source (`WHERE`) not in Java streams.

Q: Spring Data default for `findAll()` on a 10M row table?
A: Loads everything into memory (OOM risk). Always page or stream.

@@ 3 | TOPIC | Improper Instantiation | Re-creating expensive shared objects on every request | SHOULD
# Improper instantiation

**Symptom:** high GC, slow requests, socket/connection exhaustion.
**Cause:** creating objects that should be shared and reused.

## Java examples

```java
// Bad: new client (and connection pool) per call
public String call() { return new RestTemplate().getForObject(url, String.class); }

// Good: singleton bean reused
@Bean RestClient restClient(RestClient.Builder b) { return b.build(); }
```
- Also: new `ObjectMapper`, `Pattern.compile`, `DateTimeFormatter`, `HttpClient`, `DataSource` per request.
- Spring beans are singletons by default, so inject them; never `new` a service or client in a hot path.
- Thread-safe, immutable objects (`ObjectMapper`, `DateTimeFormatter`) are safe to share.

Q: How do you notice?
A: Many TIME_WAIT sockets, frequent GC, profiler showing constructor/`Pattern.compile` hot spots.

@@ 3 | TOPIC | Monolithic Persistence | One datastore for every workload | SHOULD
# Monolithic persistence

**Symptom:** the single relational DB serves transactions, full-text search, analytics, sessions, queues and caching; each workload hurts the others.

## Fix: polyglot persistence (right store per workload)

| Workload | Better store |
|---|---|
| Transactions | PostgreSQL |
| Cache, sessions, counters | Redis |
| Full-text search | Elasticsearch/OpenSearch |
| Analytics/reports | Data warehouse / ClickHouse |
| Events/queues | Kafka |
| Blobs (images, PDFs) | S3/MinIO |

```java
// don't store 5MB PDFs as BLOBs in the orders table
String key = s3.upload(file);     // store only the object key in DB
order.setInvoiceKey(key);
```
- Cost: more systems to run and keep consistent (CDC/outbox). Add stores only for a measured pain.

Q: When is one DB perfectly fine?
A: Early stage and moderate scale. Don't add stores speculatively.

@@ 3 | TOPIC | No Caching | Recomputing or refetching the same data every time | SHOULD
# No caching

**Symptom:** the same query/computation runs thousands of times per minute; DB/CPU high; reads far outnumber writes.

## Fix
- Identify hot, read-mostly, expensive-to-compute data; add a cache layer (HTTP/CDN, Redis, Caffeine).
- Set TTLs, handle invalidation and stampede.

```java
@Cacheable(value = "exchangeRates", key = "#currency")     // 1 external call per currency per TTL
public Rate rate(String currency) { return forexClient.fetch(currency); }
```
- Measure **hit ratio**; a cache with a 20% hit ratio may not pay for itself.

## Opposite mistake
- Caching everything (stale data, memory pressure). Don't cache highly dynamic or user-specific data without keys.

Q: Is caching a premature optimization?
A: Not when data is clearly read-heavy and expensive. Profile first, then cache where the numbers justify it.

@@ 3 | TOPIC | Noisy Neighbor | One tenant or workload steals shared resources | SHOULD
# Noisy neighbor

**Symptom:** latency spikes for everyone when one customer/job runs a heavy task on shared infrastructure.

## Fixes
- **Per-tenant rate limits and quotas.**
- **Bulkheads:** separate thread pools, connection pools, queues per tenant/feature.
- Resource limits (Kubernetes requests/limits, cgroup), separate heavy tenants to dedicated pools/shards.
- Move batch/report workloads off the OLTP path.

```java
// Resilience4j bulkhead: reports cannot starve checkout
@Bulkhead(name = "reports", type = Bulkhead.Type.THREADPOOL)
public CompletableFuture<Report> generate(...) { ... }
```
- Kubernetes: set `resources.limits` so one pod cannot consume the whole node.

Q: Multi-tenant SaaS, one tenant runs a giant export. What protects others?
A: Quotas + rate limits, separate pool/queue for exports, and run exports on a replica.

@@ 3 | TOPIC | Retry Storm | Aggressive retries amplify an outage | SHOULD
# Retry storm

A dependency slows down → clients time out and **retry immediately** → load multiplies (3 retries = 4× traffic) → dependency collapses completely.

## Fixes
- **Exponential backoff + jitter**, a **retry budget** (max attempts), retry only **idempotent** and transient errors.
- **Circuit breaker** to stop calling a failing service.
- Retry at **one layer only** (3 layers × 3 retries = 27 calls).

```java
RetryConfig cfg = RetryConfig.custom()
    .maxAttempts(3)
    .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(200, 2.0, 0.5))   // jitter
    .retryOnException(e -> e instanceof IOException)
    .build();
```
- Without jitter all clients retry at the same instant (thundering herd).

Q: Why jitter?
A: It spreads retries randomly in time so thousands of clients don't hit the recovering service in synchronized waves.

@@ 3 | TOPIC | Synchronous I/O | Blocking threads while waiting for slow I/O | SHOULD
# Synchronous I/O

**Symptom:** low CPU but high latency; threads all waiting on network/disk; throughput capped by thread count.

## Fixes
- Use **async/non-blocking** I/O (WebFlux + R2DBC/WebClient) when I/O waits dominate.
- Move non-critical work off the request (events/queue).
- **Java 21 virtual threads:** keep blocking code, but threads are cheap.

```yaml
spring.threads.virtual.enabled: true     # Spring Boot 3.2+ : Tomcat uses virtual threads
```

```java
// Parallel independent calls instead of sequential waits
var user = CompletableFuture.supplyAsync(() -> userClient.get(id), executor);
var orders = CompletableFuture.supplyAsync(() -> orderClient.list(id), executor);
return new Profile(user.join(), orders.join());      // latency = max, not sum
```
- Virtual threads help blocking I/O, not CPU-bound work. Avoid `synchronized` pinning on blocking calls.

Q: WebFlux or virtual threads?
A: Virtual threads give most of the scalability benefit with simple imperative code; WebFlux fits streaming/backpressure-heavy designs.

@@ 1 | MODULE | Phase 4 — Monitoring | Observe health, availability and performance | MUST
# Phase 4: Monitoring

"You can't fix what you can't see." Be ready to explain the **three pillars (metrics, logs, traces)**, the **golden signals**, and how you alert.

@@ 2 | MODULE | Monitoring | Health, availability, performance, security, usage | MUST
What to observe and how.

@@ 3 | TOPIC | Health Monitoring | Is the system functioning correctly right now? | MUST
# Health monitoring

Probes that report whether each instance and dependency is healthy.

## Liveness vs readiness (Kubernetes)

| Probe | Question | On failure |
|---|---|---|
| Liveness | Is the process stuck? | Restart the pod |
| Readiness | Can it take traffic now? | Remove from load balancer |
| Startup | Finished booting? | Delay other probes |

```yaml
management:
  endpoint.health:
    probes.enabled: true
    show-details: when-authorized
  health.db.enabled: true
```
- Expose `/actuator/health/readiness`. Include DB/Kafka in **readiness** only; putting a dependency in **liveness** can restart all pods during a DB outage (making it worse).

```java
@Component
class PaymentGatewayHealth implements HealthIndicator {
    public Health health() { return gateway.ping() ? Health.up().build() : Health.down().build(); }
}
```

Q: DB is down. Should liveness fail?
A: No. Restarting the app does not fix the DB. Fail readiness (stop traffic) and let it recover.

@@ 3 | TOPIC | Availability Monitoring | Measure uptime from the user's point of view | MUST
# Availability monitoring

Measure whether the service is reachable and working **from outside**, like a user.

## Techniques
- **Synthetic checks / probes:** call `/login`, `/search` every minute from multiple regions.
- **Real user monitoring (RUM)**, error rate from the load balancer.
- Compute **SLI = good requests / total requests**; compare to SLO (99.9%).
- **Error budget:** the allowed failures (0.1%); if spent, freeze features and fix reliability.

```text
SLO 99.9% over 30 days  -> budget 43 minutes
Burn-rate alert: page when you burn the budget 14x too fast
```
- Internal "up" can be true while users get 500s; external checks catch DNS/cert/LB issues.

Q: Why measure from outside?
A: Internal health misses network, DNS, CDN and certificate problems that real users experience.

@@ 3 | TOPIC | Performance Monitoring | Latency, throughput, errors, saturation | MUST
# Performance monitoring

## Golden signals (Google SRE)
1. **Latency** (p50/p95/p99)
2. **Traffic** (requests/sec)
3. **Errors** (5xx rate)
4. **Saturation** (CPU, memory, thread/DB pool usage, queue depth)

Related: RED (Rate, Errors, Duration) for services, USE (Utilization, Saturation, Errors) for resources.

## Micrometer + Prometheus in Spring Boot

```yaml
management.endpoints.web.exposure.include: health,prometheus
management.metrics.distribution.percentiles-histogram.http.server.requests: true
```
```java
@Timed("order.place")                          // needs TimedAspect bean
public Order place(...) { ... }

Counter.builder("orders.failed").tag("reason", "payment").register(registry).increment();
```
- Useful JVM metrics: heap used, GC pause, thread count, **HikariCP active/pending connections**, Tomcat busy threads.

Q: Why p99 not average?
A: A few very slow requests hurt real users but vanish in averages.

@@ 3 | TOPIC | Security Monitoring | Detect attacks and abuse | MUST
# Security monitoring

Watch for suspicious behavior and keep an audit trail.

## What to track
- Failed logins, brute force, impossible travel, privilege changes.
- Spikes in 401/403/429, unusual IPs, scraping, SQL injection patterns (WAF).
- Access to sensitive data, admin actions (**audit log**).
- Dependency vulnerabilities (SCA), leaked secrets.

```java
@Component
class AuthEvents {
    @EventListener
    void onFail(AbstractAuthenticationFailureEvent e) {
        audit.warn("login_failed user={} ip={}", e.getAuthentication().getName(), ip());
        metrics.counter("auth.failed").increment();      // alert on spike
    }
}
```
- Never log passwords, tokens, PII. Use **correlation IDs**; ship logs to central SIEM (ELK/Splunk).

Q: How detect brute force?
A: Count failed logins per user/IP in a window, rate limit/lock out, alert on thresholds, add CAPTCHA/MFA.

@@ 3 | TOPIC | Usage Monitoring | Understand how features and resources are used | SHOULD
# Usage monitoring

Track **how the product is used**: feature adoption, per-tenant traffic, API quotas, cost, capacity trends.

## Why
- Capacity planning, billing/metering, product decisions, detecting abusive clients.

```java
// per-tenant API usage metric
registry.counter("api.requests", "tenant", tenantId, "endpoint", path).increment();
```
- Watch **cardinality**: never tag metrics by `userId` or `requestId` (millions of series crash Prometheus). Use logs/analytics events for high-cardinality data.

Q: Metric label cardinality problem?
A: Each unique label combo creates a time series; unbounded values (user IDs) blow up memory/cost. Keep labels low-cardinality.

@@ 3 | TOPIC | Instrumentation | Metrics, logs and distributed traces | MUST
# Instrumentation

Emit signals from code. Three pillars:

| Pillar | Tool | Answers |
|---|---|---|
| Metrics | Micrometer → Prometheus | "Is something wrong?" (numbers over time) |
| Logs | Logback JSON → ELK/Loki | "What happened?" (events) |
| Traces | OpenTelemetry → Jaeger/Tempo | "Where is the time spent?" (request across services) |

## Distributed tracing

```text
trace-id abc  [gateway 5ms] -> [order 40ms] -> [payment 900ms]  <- the slow span
```
Each service passes `traceparent` header; Spring Boot 3 does it with Micrometer Tracing.

```yaml
management.tracing.sampling.probability: 0.1      # sample 10% in production
logging.pattern.level: "%5p [${spring.application.name},%X{traceId},%X{spanId}]"
```
- Put `traceId` in every log line so you can jump from an alert → trace → logs.
- Use **structured (JSON) logs** and log levels; avoid logging in tight loops.

Q: Monitoring vs observability?
A: Monitoring watches known failure signals. Observability lets you ask new questions about unknown problems using rich metrics/logs/traces.

@@ 3 | TOPIC | Visualization & Alerts | Dashboards and actionable alerts | MUST
# Visualization and alerts

## Dashboards (Grafana)
- One overview per service: golden signals, dependencies, deploy markers.
- Drill-down: service → instance → endpoint.

## Good alerts
- **Symptom-based** (users hurt): "error rate > 2% for 5 min", "p99 > 1s".
- Not cause-based noise ("CPU > 70%").
- Each alert has a **runbook**, owner and severity; page only when action is needed now.
- Use **multi-window burn-rate** alerts for SLOs.

```yaml
# Prometheus alert
- alert: HighErrorRate
  expr: sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
        / sum(rate(http_server_requests_seconds_count[5m])) > 0.02
  for: 5m
  labels: { severity: page }
```
- Alert fatigue makes people ignore pages; remove noisy alerts.

Q: What do you do when paged at 3 AM?
A: Check the dashboard (golden signals), recent deploys, dependency health; mitigate first (rollback, scale, failover), then root-cause in a blameless post-mortem.
