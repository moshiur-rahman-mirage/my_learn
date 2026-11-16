@@ 1 | MODULE | Phase 5 — Cloud Design Patterns | Varying importance: get an overview of each rather than mastering all | SHOULD
# Phase 5: Cloud design patterns

These are named, reusable solutions (from the Azure Architecture Center) you can quote in interviews: "I'd use the **Outbox + CQRS**" or "put a **Circuit Breaker** here". Learn **problem → solution → trade-off → Spring example** for each. Priority tags show what to master (MUST) vs recognize (NICE).

Some patterns appear under several categories in the source plan; the first appearance has the full note, repeats focus on the category angle.

@@ 2 | MODULE | Messaging | Decoupling with queues and events | SHOULD
Patterns built on message brokers.

@@ 3 | TOPIC | Queue-based Load Leveling | A queue buffers bursts so the consumer works at a steady rate | SHOULD
# Queue-based load leveling

**Problem:** traffic spikes overwhelm a downstream service (DB, payment API).
**Solution:** put a **queue between** producer and consumer. Producers enqueue fast; consumers drain at a steady pace.

```text
1000 req/s burst -> [ queue ] -> consumer at 100 msg/s -> DB (never overloaded)
```

## Spring Boot

```java
@PostMapping("/orders")
ResponseEntity<Void> place(@RequestBody OrderReq r) {
    kafka.send("orders", r.id(), r);               // cheap, returns immediately
    return ResponseEntity.accepted().build();       // 202
}

@KafkaListener(topics = "orders", concurrency = "5")   // consumer capacity decides the rate
void process(OrderReq r) { orderService.save(r); }
```

## Trade-offs
- (+) Absorbs spikes, availability if consumer is down (messages wait), independent scaling.
- (-) Not for request-response needing instant result; added latency; must monitor **queue depth/lag**.

Q: Queue grows forever?
A: Consumers are slower than average input. Scale consumers or reduce work; the queue only smooths **temporary** bursts.

@@ 3 | TOPIC | Publisher/Subscriber | One event, many independent subscribers | SHOULD
# Publisher/Subscriber

**Problem:** several services need to react to the same event; the publisher shouldn't know them.
**Solution:** publish to a **topic**; each subscriber gets its own copy.

```text
OrderPlaced -> topic -> [Email svc] [Inventory svc] [Analytics svc]
```

## Spring Boot + Kafka (each group = one subscriber service)

```java
@KafkaListener(topics = "order.placed", groupId = "email")     void email(OrderPlaced e) { ... }
@KafkaListener(topics = "order.placed", groupId = "inventory") void stock(OrderPlaced e) { ... }
```
- Different `groupId` = separate subscribers each receive every message. Same group = they share the load (competing consumers).

## Trade-offs
- (+) Loose coupling, add subscribers without changing the publisher.
- (-) Eventual consistency, harder to trace flow, schema evolution needs care (use a schema registry).

Q: Pub/sub vs queue?
A: Queue: one consumer gets each message. Pub/sub: every subscriber gets a copy.

@@ 3 | TOPIC | Competing Consumers | Multiple workers read one queue to share load | SHOULD
# Competing consumers

**Solution:** run N identical consumers on the same queue; each message is processed by **exactly one**.

```java
@KafkaListener(topics = "image.resize", groupId = "resizer", concurrency = "4")
void resize(ImageJob j) { ... }          // scale pods up to partition count
```
- Kafka: parallelism ≤ number of **partitions**. RabbitMQ: add consumers freely.

## Needs
- **Idempotent** consumers (redelivery after a crash), visibility timeout/ack, DLQ for poison messages.
- Ordering is lost across consumers unless partitioned by key.

Q: More consumers than partitions?
A: Extra consumers sit idle. Add partitions (consider ordering/rebalance cost).

@@ 3 | TOPIC | Priority Queue | Urgent messages are processed first | SHOULD
# Priority queue

**Problem:** premium or urgent jobs must not wait behind bulk work.

## Solutions
- Broker support (RabbitMQ `x-max-priority`).
- **Separate queues/topics per priority** with dedicated consumers (more predictable; Kafka has no native priority).

```java
@KafkaListener(topics = "jobs.high", concurrency = "6")   void high(Job j) { ... }
@KafkaListener(topics = "jobs.low",  concurrency = "2")   void low(Job j)  { ... }
```
- Guard against **starvation** of low priority (reserve minimum capacity or age-up priority).

Q: Priority in Kafka?
A: Use separate topics per priority and allocate more consumers to high; one topic cannot reorder.

@@ 3 | TOPIC | Async Request Reply | Return 202 then let the client poll for the result | SHOULD
# Asynchronous request-reply

**Problem:** the backend work is slow, but the client uses HTTP request-response.
**Solution:** accept, return `202 Accepted` + status URL; client polls (or gets a callback).

```text
POST /exports            -> 202 Location: /exports/9f1
GET  /exports/9f1        -> 200 {"status":"RUNNING"}   ...later...
GET  /exports/9f1        -> 303 See Other -> /files/export.csv
```

```java
@GetMapping("/exports/{id}")
ResponseEntity<?> status(@PathVariable String id) {
    var j = repo.get(id);
    if (!j.done()) return ResponseEntity.ok(Map.of("status", "RUNNING"));
    return ResponseEntity.status(SEE_OTHER).location(URI.create(j.resultUrl())).build();
}
```
- Add `Retry-After` header; expire old jobs.

Q: Alternative to polling?
A: Webhook, SSE or WebSocket push to the client when finished.

@@ 3 | TOPIC | Pipes and Filters | Break processing into reusable stages connected by channels | SHOULD
# Pipes and filters

**Problem:** one big processing routine is hard to reuse, test and scale.
**Solution:** split into **filters** (single-purpose steps) connected by **pipes** (queues/streams).

```text
upload -> [virus scan] -> [resize] -> [watermark] -> [store]
```

## Spring examples
- Streams in-process: `list.stream().map(scan).map(resize)...`
- **Spring Cloud Stream / Spring Integration** channels between stages; each stage can scale separately.
- Servlet `Filter` chain and Spring Cloud Gateway filters are the same idea.

```java
@Bean Function<Upload, Upload> scan()   { return u -> antivirus.check(u); }
@Bean Function<Upload, Upload> resize() { return u -> img.resize(u); }
// spring.cloud.function.definition=scan|resize|store
```
- Trade-off: more hops/latency; failure and idempotency handling per stage.

Q: Where do you see this daily?
A: ETL pipelines, image/video processing, Spring Security filter chain.

@@ 3 | TOPIC | Choreography | Services react to each other's events with no central coordinator | SHOULD
# Choreography

Each service **listens to events and emits its own**; no orchestrator.

```text
OrderPlaced -> Payment svc -> PaymentDone -> Inventory svc -> StockReserved -> Shipping svc
```

## vs Orchestration

| | Choreography | Orchestration (saga orchestrator) |
|---|---|---|
| Control | Distributed | Central coordinator |
| Coupling | Loose | Orchestrator knows all |
| Visibility | Hard to see whole flow | Easy |
| Good for | Few simple steps | Complex, many steps |

```java
@KafkaListener(topics = "payment.done")
void onPaid(PaymentDone e) { stock.reserve(e.orderId()); events.publish(new StockReserved(e.orderId())); }
```
- Failure → publish compensating events (see Compensating Transaction). Risk: cyclic dependencies, "event spaghetti".

Q: Choreography or orchestration for a 7-step order flow?
A: Orchestration (e.g., Temporal, Camunda, a Spring state machine): clearer state, timeouts and compensation.

@@ 3 | TOPIC | Claim Check | Send a reference, not the large payload | NICE
# Claim check

**Problem:** message brokers have size limits (Kafka default 1 MB) and big payloads slow them.
**Solution:** store the payload in blob storage, send only a **reference** (the claim ticket).

```java
String key = s3.put(bytes);
kafka.send("video.uploaded", new VideoUploaded(videoId, key));   // tiny message

@KafkaListener(topics = "video.uploaded")
void on(VideoUploaded e) { byte[] data = s3.get(e.key()); ... }
```
- Handle cleanup (lifecycle rules) and access control (pre-signed URLs).

@@ 3 | TOPIC | Sequential Convoy | Process related messages in order while others run in parallel | NICE
# Sequential convoy

**Problem:** messages of the same entity (one order, one account) must be processed **in order**, but different entities can run in parallel.

**Solution:** partition/session by entity key.

```java
kafka.send("account.events", accountId, event);      // key => same partition => FIFO per account
```
- Kafka partition key, RabbitMQ consistent-hash exchange, Azure Service Bus sessions.
- Trade-off: a slow message blocks its convoy; hot keys limit parallelism.

@@ 3 | TOPIC | Scheduling Agent Supervisor | Coordinate distributed steps with an agent, a supervisor and a retry | NICE
# Scheduler agent supervisor

**Problem:** a multi-step distributed job can fail midway.
**Solution:** three roles:
- **Scheduler** starts steps and records state.
- **Agent** performs a step.
- **Supervisor** watches for failed/timed-out steps and restarts or compensates.

```text
State table: job 7 | step=payment | status=RUNNING | started 10:00
Supervisor (every minute): RUNNING > 5 min -> retry or mark FAILED + compensate
```
- Modern equivalents: Temporal/Camunda workflows, Spring Batch + restart, saga orchestrator with timeouts.

@@ 2 | MODULE | Data Management | Caching, partitioning, CQRS and storage patterns | SHOULD
Patterns for storing and reading data at scale.

@@ 3 | TOPIC | Cache-Aside | Load into the cache on demand | SHOULD
# Cache-aside (pattern view)

Application reads the cache first; on miss reads the DB and **populates the cache**; writes update the DB and **invalidate** the key.

```java
@Cacheable(value = "product", key = "#id")
public Product get(Long id) { return repo.findById(id).orElseThrow(); }
@CacheEvict(value = "product", key = "#id")
public void update(Long id, ProductDto d) { ... }
```
- See *Caching Strategies → Cache Aside* for stampede, TTL and race details.

Q: Why delete instead of update on write?
A: Avoids out-of-order writes leaving stale values in the cache.

@@ 3 | TOPIC | Sharding | Split data across partitions | SHOULD
# Sharding (pattern view)

Horizontal partitioning of data across stores so each handles a subset of load and size.

- Pick a **shard key** with high cardinality and even spread (`userId`, `tenantId`).
- Strategies: hash, range, directory/lookup, consistent hashing.
- Costs: cross-shard queries/transactions, resharding, hot shards.

```java
int shard = Math.floorMod(Objects.hash(userId), shardCount);
```
- See *Databases → Sharding* for full detail and Spring routing.

Q: Hot shard (celebrity user)?
A: Split the hot key (salting), cache it, or dedicate a shard.

@@ 3 | TOPIC | CQRS | Separate write model from read model | SHOULD
# CQRS (Command Query Responsibility Segregation)

**Problem:** one model cannot serve both complex writes and fast, differently shaped reads.
**Solution:** **commands** change state through a write model; **queries** read from a separate, read-optimized model, updated asynchronously.

```text
Command -> Write DB (normalized) -> event -> Projection -> Read DB (denormalized / Elasticsearch)
Query   -> Read DB
```

## Spring Boot

```java
@Service class OrderCommandService {
    @Transactional
    void place(PlaceOrder cmd) {
        Order o = orders.save(Order.from(cmd));
        outbox.save(new OutboxEvent("OrderPlaced", o.id()));   // same transaction
    }
}

@KafkaListener(topics = "orders.events")
void project(OrderPlaced e) { readRepo.save(OrderView.from(e)); }   // eventual consistency

@GetMapping("/orders/{id}") OrderView get(@PathVariable Long id) { return readRepo.find(id); }
```

## Trade-offs
- (+) Independent scaling/optimization of read and write; simple read queries.
- (-) Eventual consistency, more moving parts. **Don't use for plain CRUD.**

Q: Is CQRS the same as event sourcing?
A: No. CQRS can use one DB with two models. Event sourcing stores events as the source of truth and pairs well with CQRS.

@@ 3 | TOPIC | Event Sourcing | Store the sequence of events instead of current state | SHOULD
# Event sourcing

State is derived by **replaying immutable events**.

```text
AccountOpened(100) -> Deposited(50) -> Withdrawn(30)   => balance 120
```

```java
record Deposited(String accountId, BigDecimal amount, Instant at) {}

class Account {
    BigDecimal balance = ZERO;
    void apply(Object e) {
        if (e instanceof Deposited d) balance = balance.add(d.amount());
        else if (e instanceof Withdrawn w) balance = balance.subtract(w.amount());
    }
    static Account rebuild(List<Object> events) { var a = new Account(); events.forEach(a::apply); return a; }
}
```
- Use **snapshots** every N events so rebuilds are fast. Axon Framework and EventStoreDB are common in Java.

## Trade-offs
- (+) Full audit trail, time travel, easy to build new projections.
- (-) Complex queries (need CQRS read models), event schema versioning, learning curve.

Q: Where is it a good fit?
A: Banking/ledgers, audit-heavy domains, collaborative editing. Overkill for simple CRUD.

@@ 3 | TOPIC | Materialized View | Precomputed query results for fast reads | SHOULD
# Materialized view

**Problem:** a complex join/aggregation is too slow to run on each request.
**Solution:** precompute and store the result; refresh periodically or on change.

```sql
CREATE MATERIALIZED VIEW daily_sales AS
  SELECT date_trunc('day', created_at) d, sum(total) FROM orders GROUP BY 1;
REFRESH MATERIALIZED VIEW CONCURRENTLY daily_sales;      -- needs a unique index
```
```java
@Scheduled(cron = "0 */5 * * * *") @SchedulerLock(name = "refreshSales")
void refresh() { jdbc.execute("REFRESH MATERIALIZED VIEW CONCURRENTLY daily_sales"); }
```
- Trade-off: staleness vs speed. Also the read side of CQRS is a materialized view maintained by events.

@@ 3 | TOPIC | Index Table | Extra tables to support queries on non-key fields | NICE
# Index table

**Problem:** a store (DynamoDB, Cassandra) is fast only by primary key; you must query by another attribute.
**Solution:** maintain a **secondary lookup table** keyed by that attribute.

```text
users(user_id PK, email, ...)          -> main table
users_by_email(email PK, user_id)      -> index table (write to both)
```
- Use native secondary indexes when available (DynamoDB GSI). Manual index tables risk inconsistency: update both (batch/transaction) or via events.

@@ 3 | TOPIC | Static Content Hosting | Serve static assets from cheap storage/CDN | SHOULD
# Static content hosting

Don't make Spring Boot serve JS/CSS/images/downloads. Put them in object storage (S3) behind a CDN.

```text
React build -> S3 bucket -> CloudFront CDN
API calls   -> api.example.com (Spring Boot)
```
- Cheaper, faster, reduces load on app servers. Use hashed filenames + long cache; configure CORS between the UI and API.
- For user uploads use **pre-signed URLs** (see Valet Key) so uploads go straight to S3.

@@ 3 | TOPIC | Valet Key | Give clients a limited, expiring token to access a resource directly | NICE
# Valet key

**Problem:** routing large uploads/downloads through your app wastes bandwidth and threads.
**Solution:** the app issues a **short-lived signed URL** granting limited access to storage; the client talks to storage directly.

```java
@GetMapping("/upload-url")
String uploadUrl(@RequestParam String name, Principal p) {
    var req = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .putObjectRequest(b -> b.bucket("uploads").key(p.getName() + "/" + name))
        .build();
    return presigner.presignPutObject(req).url().toString();
}
```
- Scope narrowly (one object, one action), short expiry, validate content type/size after upload.

Q: Why not stream the file through the API?
A: It ties up Tomcat threads and bandwidth; storage services are built for it.

@@ 2 | MODULE | Design & Implementation | Structure, gateways and migration patterns | SHOULD
Patterns for composing and evolving services.

@@ 3 | TOPIC | Strangler Fig | Replace a legacy system gradually | SHOULD
# Strangler fig

**Problem:** a big-bang rewrite is risky.
**Solution:** put a **facade/proxy** in front; route one feature at a time to the new service while the rest stays in the legacy app, until legacy is empty.

```yaml
spring.cloud.gateway.routes:
  - id: new-orders
    uri: lb://order-service            # new
    predicates: [ Path=/api/orders/** ]
  - id: legacy
    uri: http://legacy-app             # everything else
    predicates: [ Path=/** ]
```
- Steps: identify seam → build new → route (maybe canary %) → migrate data → retire old.
- Keep data in sync during transition (CDC/dual-write with care).

Q: Why not rewrite from scratch?
A: Long time without value, high risk, moving target. Strangler delivers value incrementally and is reversible.

@@ 3 | TOPIC | Sidecar | Helper process deployed alongside the main service | SHOULD
# Sidecar

A second container in the **same pod** provides cross-cutting capabilities (logging, proxy, mTLS, config) without changing app code or language.

```text
Pod: [ Spring Boot app ]  <-localhost->  [ Envoy proxy / log shipper / Vault agent ]
```
- **Service mesh (Istio, Linkerd)** is sidecars everywhere: mTLS, retries, metrics outside the app.
- Trade-offs: extra resources, one more thing to upgrade; latency of an extra hop.

Q: Library vs sidecar?
A: Library (e.g., Resilience4j) is per-language and in-process. Sidecar is language-agnostic and managed centrally.

@@ 3 | TOPIC | Ambassador | A helper proxy handling outbound network concerns | SHOULD
# Ambassador

An out-of-process proxy next to the client that handles **outgoing calls**: retries, circuit breaking, TLS, routing, monitoring.

```text
Spring Boot app -> localhost ambassador -> (retry, TLS, auth) -> remote service
```
- Useful for legacy apps that cannot be modified. A sidecar used for **egress**.
- Trade-off: extra hop, one more component to run.

@@ 3 | TOPIC | Anti-Corruption Layer | Translate between your model and a legacy/external model | SHOULD
# Anti-corruption layer (ACL)

**Problem:** integrating with a legacy or third-party system would leak its ugly model into your clean domain.
**Solution:** a translation layer (adapter/facade) between them.

```java
@Component
class LegacyCustomerAdapter {                      // ACL
    private final LegacyCrmClient crm;
    Customer find(String id) {
        LegacyRec r = crm.getREC(id);              // CUST_NM, ST_CD ...
        return new Customer(id, r.CUST_NM(), mapStatus(r.ST_CD()));   // our clean model
    }
}
```
- Your domain depends on an interface; only the adapter knows the legacy format. Pairs well with Strangler Fig.

@@ 3 | TOPIC | Backends for Frontend | A dedicated API per client type | SHOULD
# Backends for Frontend (BFF)

Web, mobile and partner apps need different payloads. Create **one backend per frontend** that aggregates and shapes data for it.

```text
Mobile app -> Mobile BFF --\
Web app    -> Web BFF -------> order, user, catalog services
```
```java
@GetMapping("/mobile/home")
HomeDto home() {                         // one call instead of five
    return new HomeDto(users.me(), orders.recent(3), promos.top(2));
}
```
- (+) Optimized per client, fewer round trips. (-) Duplicate code, more services; keep BFFs thin.

@@ 3 | TOPIC | Gateway Routing | Route requests to services by path/header | SHOULD
# Gateway routing

A single entry point exposes one URL and routes to many services.

```yaml
spring.cloud.gateway.routes:
  - id: users
    uri: lb://user-service
    predicates: [ Path=/api/users/** ]
  - id: orders
    uri: lb://order-service
    predicates: [ Path=/api/orders/**, Header=X-Version, v2 ]
```
- Clients don't know internal topology; enables versioning, canary and A/B routing.
- The gateway can become a bottleneck/SPOF: run it redundantly and keep it thin.

@@ 3 | TOPIC | Gateway Offloading | Move cross-cutting concerns to the gateway | SHOULD
# Gateway offloading

Handle shared functions **at the gateway** instead of in each service: TLS termination, authentication (JWT validation), rate limiting, CORS, compression, request logging.

```java
@Bean
SecurityWebFilterChain security(ServerHttpSecurity http) {
    return http.oauth2ResourceServer(o -> o.jwt(withDefaults()))     // validate JWT once, at the edge
               .authorizeExchange(a -> a.anyExchange().authenticated()).build();
}
```
```yaml
filters:
  - name: RequestRateLimiter
    args: { redis-rate-limiter.replenishRate: 10, redis-rate-limiter.burstCapacity: 20 }
```
- Trade-off: services must still enforce authorization for sensitive operations (defense in depth).

@@ 3 | TOPIC | Gateway Aggregation | Combine several service calls into one client call | SHOULD
# Gateway aggregation

The gateway calls multiple services, merges results, and returns one response (fewer client round trips).

```java
@GetMapping("/dashboard")
Mono<Dashboard> dashboard() {
    return Mono.zip(users.me(), orders.recent(), notifications.unread(),
                    Dashboard::new);                // calls run in parallel
}
```
- Handle partial failure (timeouts, fallback per part) so one slow service doesn't fail everything.
- Same idea as BFF/GraphQL.

@@ 3 | TOPIC | CQRS | Separate read and write models in design | SHOULD
# CQRS (design view)

See the full note under *Data Management → CQRS*.

**Design angle:** use CQRS when read and write workloads differ widely, e.g., a high-write order service and a heavily-read product search. Command side validates and persists; query side serves optimized views. Keep it local to the **bounded context** that needs it, not system-wide.

Q: Smell that you shouldn't use CQRS?
A: Simple CRUD with similar read and write shapes: the extra model adds cost with no benefit.

@@ 3 | TOPIC | Leader Election | Choose one instance to coordinate work | SHOULD
# Leader election

**Problem:** among N identical instances, exactly **one** must perform a task (scheduler, cache refresher, partition assigner).
**Solution:** instances compete for a **lock/lease**; the winner is the leader and renews the lease; if it dies the lease expires and another takes over.

```java
// Spring Integration with a lock registry (Redis/JDBC/ZooKeeper)
@Bean LockRegistry lockRegistry(RedisConnectionFactory cf) { return new RedisLockRegistry(cf, "leader", 30_000); }

void run() {
    Lock l = registry.obtain("scheduler");
    if (l.tryLock()) { try { doWork(); } finally { l.unlock(); } }
}
```
- Alternatives: ZooKeeper/etcd, Kubernetes Lease API (`spring-cloud-kubernetes-leader`), ShedLock for scheduled jobs.
- Beware **split brain** from clock skew or GC pauses: use fencing tokens.

Q: Why a lease instead of a plain lock?
A: A crashed holder never releases a plain lock; a lease expires automatically.

@@ 3 | TOPIC | Pipes & Filters | Chain of single-purpose processing steps | SHOULD
# Pipes and filters (design view)

See *Messaging → Pipes and Filters*.

**Design angle:** decompose a processing workflow into small, reusable stages that can be reordered, scaled and tested independently (e.g., Spring Security filter chain, Spring Cloud Gateway filters, Spring Cloud Stream function composition).

@@ 3 | TOPIC | Static Content Hosting | Offload static files from the app | SHOULD
# Static content hosting (design view)

See *Data Management → Static Content Hosting*.

**Design angle:** a clean separation of the SPA/assets (CDN + object storage) from APIs (Spring Boot) makes the API tier smaller, cheaper to scale and easier to deploy independently.

@@ 3 | TOPIC | External Config Store | Keep configuration outside deployed artifacts | NICE
# External configuration store

Configuration (URLs, flags, secrets) lives **outside** the application package so it can change without a redeploy and differ per environment.

```yaml
# bootstrap/application.yml
spring.config.import: "configserver:http://config:8888"      # Spring Cloud Config
```
- Options: Spring Cloud Config (Git), Kubernetes ConfigMaps/Secrets, HashiCorp Vault, AWS Parameter Store/Secrets Manager.
- `@RefreshScope` + `/actuator/refresh` (or Spring Cloud Bus) reloads values at runtime.
- **Secrets** must be encrypted/rotated; never commit them to Git.

Q: 12-factor rule?
A: "Store config in the environment", never hardcode it in the code or image.

@@ 3 | TOPIC | Compute Resource Consolidation | Host several tasks in one compute unit to save cost | NICE
# Compute resource consolidation

Combine multiple small, related workloads into one deployable unit (process/pod) to cut cost and operational overhead.

- Example: several lightweight Spring Boot modules in one service, or multiple low-traffic tasks sharing a worker.
- Trade-offs: loses independent scaling and fault isolation (noisy neighbor); keep clear module boundaries.
- Good for low, steady load; bad for workloads with different scaling or security needs.

@@ 2 | MODULE | Reliability — Availability | Patterns that keep the system reachable | SHOULD
Patterns tagged for availability.

@@ 3 | TOPIC | Deployment Stamps | Deploy independent copies of the whole stack | SHOULD
# Deployment stamps

Deploy multiple **identical, self-contained copies ("stamps")** of the full stack (app + DB + cache), each serving a subset of tenants or a region.

```text
Stamp EU: LB + app + DB  (tenants A-M)
Stamp US: LB + app + DB  (tenants N-Z)
Global router directs a tenant to its stamp.
```
- (+) Failure/blast radius limited to one stamp, easy scale-out by adding stamps, data residency compliance.
- (-) More infrastructure, routing/tenant-mapping needed, cross-stamp queries are hard.
- Infrastructure as code (Terraform/Helm) makes stamping repeatable.

@@ 3 | TOPIC | Geodes | Geographically distributed, symmetric nodes serving any request | SHOULD
# Geodes

Deploy **identical nodes (geodes) across regions**, each able to serve **any** request, backed by a geo-replicated data store. Users hit the nearest geode.

- (+) Low latency worldwide, survive a regional outage.
- (-) Needs multi-region data replication → eventual consistency or conflict resolution (Cosmos DB, Cassandra, DynamoDB Global Tables, CockroachDB).
- Route using geo-DNS / global load balancer; keep apps stateless.

Q: Geodes vs deployment stamps?
A: Geodes are interchangeable and share one global data layer. Stamps are isolated copies each owning a slice of data.

@@ 3 | TOPIC | Throttling | Limit consumption to protect the system | SHOULD
# Throttling (rate limiting)

Control how many requests a client/tenant may make, to protect the service and keep fairness.

## Algorithms

| Algorithm | Idea |
|---|---|
| Fixed window | N requests per minute (burst at boundary) |
| Sliding window | Smoother |
| Token bucket | Tokens refill at rate r; burst up to capacity |
| Leaky bucket | Constant outflow rate |

## Resilience4j / Bucket4j in Spring

```java
Bucket bucket = Bucket.builder()
    .addLimit(Bandwidth.simple(100, Duration.ofMinutes(1))).build();

if (!bucket.tryConsume(1)) {
    return ResponseEntity.status(429).header("Retry-After", "30").build();
}
```
- For many instances keep counters in **Redis** (Spring Cloud Gateway `RequestRateLimiter`, Bucket4j-Redis).
- Return `429` with `Retry-After`; throttle by API key/user/IP; differentiate tiers.

Q: Throttling vs load shedding?
A: Throttling limits per client by policy; load shedding drops requests when the whole system is overloaded.

@@ 3 | TOPIC | Health Endpoint Monitoring | Expose health checks for probes and balancers | SHOULD
# Health endpoint monitoring (availability view)

Expose `/health` endpoints so external monitors and load balancers can detect and remove unhealthy instances → higher availability.

- Check dependencies (DB, broker) in a **readiness** check, keep a cheap **liveness** check.
- Spring Boot Actuator: `/actuator/health/liveness`, `/actuator/health/readiness`.
- Full note: *Reliability — Resiliency → Health Endpoint Monitoring*.

@@ 3 | TOPIC | Queue-Based Load Leveling | Buffer spikes with a queue | SHOULD
# Queue-based load leveling (availability view)

A queue lets the producer accept requests even when the consumer is slow or down, so the system stays **available** during spikes and partial outages; messages are processed when capacity returns.

See the full note under *Messaging → Queue-based Load Leveling* (Spring Boot + Kafka example).

@@ 2 | MODULE | Reliability — High Availability | Patterns that remove single points of failure | SHOULD
Patterns tagged for high availability.

@@ 3 | TOPIC | Deployment Stamps | Isolated full-stack copies for HA | SHOULD
# Deployment stamps (HA view)

Each stamp is independent, so a failure (bad deploy, DB outage) only affects the tenants in that stamp. Combine with multi-AZ inside each stamp. Full note: *Reliability — Availability → Deployment Stamps*.

@@ 3 | TOPIC | Geodes | Multi-region active-active for HA | SHOULD
# Geodes (HA view)

Because every region can serve every request, losing a whole region only shifts traffic to others (RTO near zero). Cost: multi-region data replication and consistency trade-offs. Full note: *Reliability — Availability → Geodes*.

@@ 3 | TOPIC | Bulkhead | Isolate resources so one failure doesn't sink everything | SHOULD
# Bulkhead (HA view)

Like ship compartments: separate thread pools/connection pools per dependency so a slow dependency exhausts only its own pool.

```java
@Bulkhead(name = "payment", type = Bulkhead.Type.SEMAPHORE)
public PaymentResult pay(PaymentReq r) { ... }
```
- Full note with config under *Reliability — Resiliency → Bulkhead*.

@@ 3 | TOPIC | Health Endpoint Monitoring | Detect failed instances automatically | SHOULD
# Health endpoint monitoring (HA view)

Automatic fail-over requires automatic detection: LB/Kubernetes probes hit health endpoints and route traffic only to healthy instances. Tune probe intervals and failure thresholds to balance quick detection and false positives.

@@ 3 | TOPIC | Circuit Breaker | Stop calling a failing dependency | SHOULD
# Circuit breaker (HA view)

When a dependency is failing, **open the circuit** and fail fast/fallback instead of piling up blocked threads. This protects your own availability.

```java
@CircuitBreaker(name = "inventory", fallbackMethod = "cached")
public Stock stock(String sku) { return client.get(sku); }
```
- Full states and config under *Reliability — Resiliency → Circuit Breaker*.

@@ 2 | MODULE | Reliability — Resiliency | Surviving failures gracefully | MUST
Most-asked pattern group: failure handling.

@@ 3 | TOPIC | Circuit Breaker | Fail fast when a dependency is unhealthy | MUST
# Circuit breaker

**Problem:** a slow/failing dependency makes every caller wait for timeouts, exhausting threads and cascading the failure.
**Solution:** a state machine wrapping calls:

```text
CLOSED  --failure rate > 50%-->  OPEN (fail immediately)
OPEN    --after wait 30s-->      HALF-OPEN (allow a few trial calls)
HALF-OPEN --trial succeeds--> CLOSED     --trial fails--> OPEN
```

## Resilience4j with Spring Boot

```yaml
resilience4j.circuitbreaker.instances.payment:
  sliding-window-size: 20
  failure-rate-threshold: 50
  wait-duration-in-open-state: 30s
  permitted-number-of-calls-in-half-open-state: 3
  slow-call-duration-threshold: 2s
```
```java
@CircuitBreaker(name = "payment", fallbackMethod = "pending")
@TimeLimiter(name = "payment")
public CompletableFuture<Receipt> pay(PayReq r) { return CompletableFuture.supplyAsync(() -> client.pay(r)); }

CompletableFuture<Receipt> pending(PayReq r, Throwable t) { return completedFuture(Receipt.pending(r.id())); }
```
- Always pair with **timeouts**. Choose a meaningful fallback (cached data, default, queue for later), not just an error.
- Annotation order (Resilience4j default): Retry ( CircuitBreaker ( RateLimiter ( TimeLimiter ( Bulkhead ( func ) ) ) ) ).

Q: Circuit breaker vs retry?
A: Retry handles brief transient errors. Circuit breaker stops retrying/calling when the failure is persistent. Use both: retry inside, breaker around.

@@ 3 | TOPIC | Retry | Retry transient failures safely | MUST
# Retry

Transient failures (network blip, 503, deadlock) often succeed on a second try.

## Rules
1. Retry only **transient** errors (timeouts, 503, 429) not 400/404.
2. Only **idempotent** operations (or use an idempotency key).
3. **Exponential backoff + jitter**; cap attempts and total time.
4. Retry at **one layer** only.

```java
@Retry(name = "inventory", fallbackMethod = "fallback")
Stock stock(String sku) { return client.get(sku); }
```
```yaml
resilience4j.retry.instances.inventory:
  max-attempts: 3
  wait-duration: 200ms
  enable-exponential-backoff: true
  exponential-backoff-multiplier: 2
  retry-exceptions: [ java.io.IOException, java.util.concurrent.TimeoutException ]
```
- Spring Retry (`@Retryable`) is the simpler alternative inside a single service.
- Honor `Retry-After` from the server.

Q: Danger of retries?
A: Retry storms and duplicate side effects; mitigate with backoff + jitter, circuit breaker, idempotency.

@@ 3 | TOPIC | Bulkhead | Partition resources so failures stay contained | MUST
# Bulkhead

Isolate resources (threads, connections) per dependency or feature so one slow part cannot consume everything.

```yaml
resilience4j.bulkhead.instances.reports:
  max-concurrent-calls: 5          # semaphore: at most 5 report jobs at once
resilience4j.thread-pool-bulkhead.instances.search:
  core-thread-pool-size: 4
  max-thread-pool-size: 8
  queue-capacity: 20
```
```java
@Bulkhead(name = "reports")
public Report generate(...) { ... }          // 6th concurrent call gets BulkheadFullException -> 503/429
```
- Also: separate **HikariCP pools** for OLTP and reporting, separate Kubernetes deployments for critical vs batch.

Q: Bulkhead vs rate limiter?
A: Bulkhead limits concurrent in-flight calls (resource isolation). Rate limiter limits calls per time window.

@@ 3 | TOPIC | Compensating Transaction | Undo completed steps when a later step fails | MUST
# Compensating transaction

In distributed workflows you can't hold a database transaction across services. If step 3 fails, run **compensations** for steps 1-2: this is the **Saga** pattern.

```text
Create order -> Reserve stock -> Charge payment (FAILS)
                ^ release stock <- cancel order    (compensations, reverse order)
```

## Orchestrated saga in Spring

```java
void placeOrder(Order o) {
    try {
        orders.create(o);
        inventory.reserve(o);
        payments.charge(o);
    } catch (Exception e) {
        inventory.release(o);      // compensation must be idempotent and retryable
        orders.cancel(o);
        throw e;
    }
}
```
- In production use an orchestrator/state machine (Temporal, Camunda, Axon saga) persisting state so it survives crashes.
- Compensation isn't a perfect rollback: an email already sent can't be unsent (send a "cancelled" email).
- Prefer **semantic undo** ("refund") over deleting data.

Q: 2PC vs Saga?
A: 2PC gives atomicity but locks resources and blocks on failure. Saga is eventually consistent, scalable, requires compensations; preferred for microservices.

@@ 3 | TOPIC | Health Endpoint Monitoring | Expose liveness and readiness checks | MUST
# Health endpoint monitoring

Each service exposes an endpoint that reports its state so external tools can check it and act.

```yaml
management:
  endpoint.health.probes.enabled: true
  endpoints.web.exposure.include: health,info
```
```yaml
# Kubernetes
livenessProbe:  { httpGet: { path: /actuator/health/liveness,  port: 8080 }, periodSeconds: 10 }
readinessProbe: { httpGet: { path: /actuator/health/readiness, port: 8080 }, periodSeconds: 5, failureThreshold: 3 }
```
- Return quickly; no heavy checks; secure details; include dependency checks in readiness, not liveness.
- Deep checks (call DB) vs shallow; cache the result for a few seconds to avoid hammering dependencies.

Q: What's wrong with a health check that always returns 200?
A: It reports the process alive even when it cannot serve traffic (DB down, deadlock), so the LB keeps sending requests to a broken instance.

@@ 3 | TOPIC | Leader Election | Resilient single-coordinator | SHOULD
# Leader election (resiliency view)

For resilience the leader role must **automatically move** if the leader fails: use lease-based election with timeouts (ZooKeeper, etcd, Kubernetes Lease, Redis lock with TTL) and fencing tokens to stop a stale leader acting. Full note: *Design & Implementation → Leader Election*.

@@ 3 | TOPIC | Queue-Based Load Leveling | Buffer to survive overload | MUST
# Queue-based load leveling (resiliency view)

A queue decouples producer and consumer, so a **consumer outage or slow-down doesn't fail the producer**: messages wait and are processed later. Add DLQ, retry with backoff, and idempotent consumers. Full note: *Messaging → Queue-based Load Leveling*.

@@ 3 | TOPIC | Scheduler Agent Supervisor | Recover failed distributed steps | NICE
# Scheduler agent supervisor (resiliency view)

A supervisor monitors long-running distributed jobs and retries or compensates failed/timed-out steps, so a crashed worker doesn't leave work half done. Today use Temporal/Camunda/Spring Batch restart or a saga orchestrator with timeouts. Full note: *Messaging → Scheduling Agent Supervisor*.

@@ 2 | MODULE | Reliability — Security | Identity and access patterns | SHOULD
Patterns that protect access.

@@ 3 | TOPIC | Federated Identity | Delegate authentication to an external identity provider | SHOULD
# Federated identity

Users sign in with an **identity provider (IdP)** (Keycloak, Google, Azure AD, Okta) using **OIDC/SAML**; your app trusts the issued token instead of storing passwords.

```text
User -> App -> redirect to IdP -> login (MFA/SSO) -> ID/access token (JWT) -> App validates
```

## Spring Boot resource server

```yaml
spring.security.oauth2.resourceserver.jwt.issuer-uri: https://keycloak.example.com/realms/app
```
```java
@Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(a -> a.anyRequest().authenticated())
               .oauth2ResourceServer(o -> o.jwt(withDefaults())).build();
}
```
- (+) SSO, centralized MFA/policy, no password storage. (-) Dependency on the IdP: make it highly available.
- Validate `iss`, `aud`, `exp`, signature (JWKS); keep access tokens short-lived.

Q: OAuth2 vs OIDC?
A: OAuth2 is authorization (access tokens for APIs). OIDC adds authentication (ID token about the user).

@@ 3 | TOPIC | Gatekeeper | A hardened front component validates requests before the trusted backend | SHOULD
# Gatekeeper

A dedicated, **hardened** front-end proxy validates and sanitizes requests, then passes only valid ones to trusted back-end hosts that have minimal exposure.

```text
Internet -> Gatekeeper (WAF, authN, input validation, rate limit) -> private network -> app/DB
```
- Limits blast radius: if the gatekeeper is compromised it holds no secrets/data; the backend is not internet-reachable.
- In practice: API gateway + WAF + private subnets + security groups.
- Still validate input in the backend (defense in depth).

@@ 3 | TOPIC | Valet Key | Delegate narrow access with a signed token | SHOULD
# Valet key (security view)

A **time-limited, scoped, signed token** lets a client access a specific resource directly without receiving your credentials (S3 pre-signed URL, SAS token).

- Principle of **least privilege**: one object, one operation, short expiry.
- Never reuse long-lived credentials or leak signing keys; log issuance.
- Full note with Java example: *Data Management → Valet Key*.
