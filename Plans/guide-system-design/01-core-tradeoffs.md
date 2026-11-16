@@ 1 | MODULE | Phase 1 — Introduction & Core Trade-offs | The fundamental trade-offs behind every design | MUST
# Phase 1: Introduction & core trade-offs

A system design interview has **no single right answer**. The interviewer checks whether you can clarify requirements, pick trade-offs, and justify them. Every later phase is a tool; this phase is the thinking behind choosing the tool.

**Java/Spring Boot angle:** you will be asked to sketch Spring Boot services, their database, cache and queue, and to say what breaks at 10x load.

@@ 2 | MODULE | Introduction | What system design is and how to approach it | MUST
Start here: what the interview is and a repeatable framework.

@@ 3 | TOPIC | What is System Design? | Designing components, data flow and trade-offs to meet requirements at scale | MUST
# What is system design?

System design = deciding **which components** (services, databases, caches, queues), **how they talk**, and **how they behave under load and failure**, so that functional and non-functional requirements are met.

**Analogy:** building a city, not a house. A house (one Spring Boot app + one DB) works for 100 people. A city needs roads (network), water supply (caching/queues), zoning (service boundaries) and emergency services (failover).

## Two kinds of requirements

| Type | Examples |
|---|---|
| Functional | "User can shorten a URL", "Place an order" |
| Non-functional (NFR) | 10k requests/sec, p99 < 200 ms, 99.99% availability, data never lost |

NFRs drive the architecture. A CRUD API with 10 users and one with 10 million need different designs for the *same* features.

## Monolith to scaled system (typical story)

```text
Stage 1:  Client -> Spring Boot app -> PostgreSQL          (works to ~1k RPS)
Stage 2:  + Redis cache, + read replica                    (reads scale)
Stage 3:  + Load balancer, N stateless app instances       (compute scales)
Stage 4:  + Kafka, async workers                           (spikes absorbed)
Stage 5:  + Sharding / microservices / multi-region        (data & team scale)
```
- Each stage is added only when a measured bottleneck demands it. Over-engineering is also a design failure.

## Remember
- Design is about **trade-offs**, never "best technology".
- Always tie a choice to a requirement: "I add Redis *because* reads are 100:1 vs writes."

Q: Is system design about picking popular tools?
A: No. It is about meeting requirements (scale, latency, availability, cost) with justified trade-offs. Tools are the implementation.

Q: Monolith or microservices first?
A: Usually a well-structured monolith first; split when team size, deploy independence or scaling needs justify the network and operational cost.

@@ 3 | TOPIC | How to approach System Design? | A 5-step interview framework: requirements, estimation, API, high-level design, deep dive | MUST
# How to approach a system design interview

Use the same framework every time (about 45 minutes):

| Step | Time | What you do |
|---|---|---|
| 1. Clarify requirements | 5 min | Features in/out of scope, users, read/write ratio, NFRs |
| 2. Estimate scale | 5 min | QPS, storage, bandwidth (back-of-envelope) |
| 3. API + data model | 5 min | Endpoints, main entities |
| 4. High-level design | 10 min | Boxes and arrows: client, LB, services, DB, cache, queue |
| 5. Deep dive + trade-offs | 15 min | Bottlenecks, sharding, failures, consistency, monitoring |

## Back-of-envelope example: URL shortener

```text
100M new URLs/month  -> ~40 writes/sec
Read:write = 100:1   -> ~4,000 reads/sec
5 years storage      -> 100M * 12 * 5 = 6B URLs * 500 B = ~3 TB
```
- Say your numbers aloud; the interviewer wants the *method*, not exact digits.

## Sketch the API in Spring Boot terms

```java
@RestController
@RequestMapping("/api/v1/urls")
class UrlController {
    @PostMapping
    ResponseEntity<UrlDto> create(@Valid @RequestBody CreateUrlRequest req) { ... } // 201

    @GetMapping("/{code}")
    ResponseEntity<Void> redirect(@PathVariable String code) { ... }               // 302
}
```
- Writing 2-3 endpoints early proves you understand the contract before the boxes.

## Common mistakes
- Jumping to Kafka/Kubernetes before asking requirements.
- Giving one design without naming trade-offs.
- Ignoring failure ("what if the DB dies?") and monitoring.

Q: Interviewer says "design Twitter". First move?
A: Ask: which features (post, timeline, follow)? How many users/DAU? Read-heavy? Latency target? Then estimate scale, then design.

@@ 2 | MODULE | Core Trade-offs | Performance, scalability, latency, throughput, availability, consistency | MUST
The vocabulary every interviewer expects you to use precisely.

@@ 3 | TOPIC | Performance vs Scalability | Slow for one user vs slow under load | MUST
# Performance vs scalability

- **Performance problem:** the system is slow **for a single user**.
- **Scalability problem:** the system is fast for one user but **slow under heavy load**.

**Analogy:** a single cashier who is slow = performance. A fast cashier but 1000 customers and one counter = scalability.

## Vertical vs horizontal scaling

| | Vertical (scale up) | Horizontal (scale out) |
|---|---|---|
| How | Bigger machine | More machines |
| Limit | Hardware ceiling, single point of failure | Needs stateless design, LB, data partitioning |
| Cost | Expensive at the top | Cheap commodity nodes |

## Spring Boot: make the service horizontally scalable

```java
// BAD: state in the JVM, breaks when a 2nd instance appears
private final Map<String, Cart> carts = new ConcurrentHashMap<>();

// GOOD: state in Redis (shared), any instance can serve any request
@Service
class CartService {
    private final RedisTemplate<String, Cart> redis;
    Cart get(String userId) { return redis.opsForValue().get("cart:" + userId); }
}
```
- Local in-memory state ties a user to one instance; externalizing it (Redis/DB/JWT) lets you add instances freely.

## Remember
- Measure first (profiler, p99) to know which problem you have.
- Performance fixes: indexes, caching, fewer queries. Scalability fixes: stateless nodes, LB, sharding, async.

Q: Your API is slow for one request. Does adding servers help?
A: No. That is a performance problem (query, N+1, algorithm). More servers only help throughput under load.

@@ 3 | TOPIC | Latency vs Throughput | Time per request vs requests per second | MUST
# Latency vs throughput

- **Latency:** time to finish **one** request (ms).
- **Throughput:** requests (or bytes) handled **per second**.

**Analogy:** a highway. Latency = time one car needs A to B. Throughput = cars per hour. A wider road (more lanes) raises throughput but a single car is not faster.

## Percentiles, not averages

| Metric | Meaning |
|---|---|
| p50 | Half the requests are faster |
| p95 / p99 | The slow tail users actually feel |

Averages hide the tail. "avg 50 ms, p99 3 s" is a bad system.

## Little's Law (quick sizing)

```text
concurrent requests = throughput * latency
1000 req/s * 0.2 s = 200 requests in flight
```
- Tomcat default 200 threads: if latency rises to 1 s at 1000 req/s you need 1000 threads, so requests queue and latency explodes.

## Spring Boot knobs

```yaml
server:
  tomcat:
    threads:
      max: 200          # concurrency ceiling
spring:
  datasource:
    hikari:
      maximum-pool-size: 20   # DB concurrency ceiling
```
- Often the **DB pool**, not Tomcat, caps throughput. Raising threads without raising downstream capacity only moves the queue.

## Trade-off
- Batching/queues raise throughput but add latency. Synchronous direct calls lower latency but may cap throughput.

Q: How do you improve throughput without hurting latency?
A: Scale out stateless instances, cache hot reads, remove blocking I/O, size pools correctly. Avoid adding queues in the user-facing path.

@@ 3 | TOPIC | Availability vs Consistency | Under a network partition you choose one | MUST
# Availability vs consistency

- **Consistency:** every read sees the latest write.
- **Availability:** every request gets a (non-error) response, even if data may be stale.

When a network **partition** splits your nodes you cannot have both (see CAP next). Even without a partition there is a latency vs consistency trade-off.

## Real example: bank vs social feed

| System | Pick | Why |
|---|---|---|
| Bank transfer, inventory count | Consistency | Wrong balance or oversell is costly |
| Likes count, news feed | Availability | Slightly stale is fine, downtime is not |

## Spring Boot example: both modes in one app

```java
// Strong: one transaction, read from primary
@Transactional
public void transfer(Long from, Long to, BigDecimal amt) {
    Account a = repo.findByIdForUpdate(from);   // SELECT ... FOR UPDATE
    ...
}

// Available/eventual: read from replica or cache
@Transactional(readOnly = true)
public long likeCount(Long postId) { return cache.get(postId); } // may be a few seconds old
```
- Choose per use case inside the same product, not one global answer.

Q: Give a case where you deliberately choose availability.
A: Product page view counts or timeline: serve cached/stale data rather than fail; reconcile later.

@@ 2 | MODULE | CAP Theorem | Consistency, Availability, Partition tolerance | MUST
Under a network partition a distributed store must choose C or A.

@@ 3 | TOPIC | AP - Availability + Partition Tolerance | Keep answering during a partition, accept stale data | MUST
# AP systems

During a partition each side **keeps serving** requests, so replicas may diverge. They **reconcile later** (eventual consistency).

**Examples:** Cassandra, DynamoDB (default), Riak, DNS, Couchbase.

## How divergence is fixed
- **Last-write-wins (timestamp)**: simple, can lose writes.
- **Vector clocks / CRDTs**: detect or merge concurrent updates.
- **Read repair / anti-entropy**: replicas sync in the background.

## Spring Data Cassandra example

```java
CassandraTemplate template = ...;
template.getCqlOperations().execute(
    SimpleStatement.newInstance("INSERT INTO orders ...")
        .setConsistencyLevel(DefaultConsistencyLevel.ONE)); // fast, available, may be stale
```
- Consistency level **ONE** = answer from the nearest replica (AP behaviour). **QUORUM** moves toward CP.

## Remember
- AP fits: shopping cart, feeds, sensor data, anything where "always up" beats "always exact".

Q: Why can't you have CA in a distributed system?
A: Network partitions are unavoidable, so P is mandatory. The real choice is C or A *when a partition happens*.

@@ 3 | TOPIC | CP - Consistency + Partition Tolerance | Refuse to answer rather than return stale data | MUST
# CP systems

During a partition the minority side **rejects requests** (becomes unavailable) so no one reads or writes stale data.

**Examples:** ZooKeeper, etcd, HBase, MongoDB (majority writes), Redis Cluster (best effort), traditional RDBMS with sync replication.

## Quorum idea

```text
N = 3 replicas, W = 2 (write quorum), R = 2 (read quorum)
W + R > N  =>  every read overlaps the latest write  => strong consistency
```
- If only 1 node is reachable, W=2 cannot be met, so the write fails (unavailable). That is the "CP" cost.

## Spring example: fail fast instead of lying

```java
@Retryable(retryFor = TransientDataAccessException.class, maxAttempts = 3)
@Transactional
public void debit(Long id, BigDecimal amt) { ... }   // if primary unreachable, throw, never use stale replica
```
- For money, a clean error is better than a wrong balance.

## Remember
- CP fits: payments, inventory, leader election, config/coordination.

Q: Is PostgreSQL CP or AP?
A: A single-primary PostgreSQL with sync replication behaves CP: if the primary is unreachable writes fail. Async replicas serving reads give eventual (stale) reads.

@@ 2 | MODULE | Consistency Patterns | Weak, eventual and strong consistency | MUST
How fresh a read is after a write.

@@ 3 | TOPIC | Weak Consistency | After a write, reads may or may not see it | MUST
# Weak consistency

After a write, reads **might not** see it, and there is no promise when they will. Best-effort.

**Examples:** VoIP/video calls (dropped frames are not replayed), real-time games, memcached.

**Analogy:** a live TV broadcast. If you miss a second, it is gone.

## When to use
- Data loss/staleness is cheap and speed matters most.
- A cache you can rebuild: a miss just goes to the DB.

```java
// Cache that may lose entries any time: acceptable because DB is source of truth
String v = cache.get(key);
if (v == null) v = loadFromDb(key);
```

Q: Where would weak consistency be unacceptable?
A: Orders, payments, inventory: anything where losing or misreading a write costs money.

@@ 3 | TOPIC | Eventual Consistency | Replicas converge given time without new writes | MUST
# Eventual consistency

After a write, reads will **eventually** see it (typically milliseconds to seconds) if no new writes arrive. Replicas are updated **asynchronously**.

**Examples:** DNS, email, S3 (previously), Cassandra, DynamoDB, async DB replicas.

**Analogy:** gossip. Everyone hears the news, just not at the same instant.

## Read-your-writes problem (classic bug)

```text
User updates profile -> write to primary
Next page load       -> read from replica (lag 500 ms) -> sees OLD profile
```

## Spring Boot fix: pin reads after a write

```java
@Transactional                       // primary
public Profile update(Profile p) { return repo.save(p); }

@Transactional(readOnly = true)      // routed to replica via AbstractRoutingDataSource
public Profile get(Long id) { ... }
```
- Typical fixes: read from primary for a few seconds after a write, return the updated object in the write response, or use a session token/version.

## Remember
- With events (Kafka) the consumer side is eventually consistent by design; make handlers **idempotent**.

Q: How do you handle "I saved but I don't see it"?
A: Read-your-writes: route that user's next reads to the primary, or return fresh data from the write response and cache it client-side.

@@ 3 | TOPIC | Strong Consistency | After a write, every read sees it | MUST
# Strong consistency

After a write completes, **all** later reads return it. Data is replicated **synchronously**.

**Examples:** RDBMS with ACID transactions, Spanner, ZooKeeper, etcd.

**Cost:** higher write latency (wait for replicas) and lower availability during partitions.

## Spring Boot example: prevent lost update

```java
@Entity
class Account {
    @Id Long id;
    BigDecimal balance;
    @Version Long version;          // optimistic lock
}

@Transactional
public void withdraw(Long id, BigDecimal amt) {
    Account a = repo.findById(id).orElseThrow();
    a.setBalance(a.getBalance().subtract(amt));   // UPDATE ... WHERE id=? AND version=?
}   // concurrent writer -> ObjectOptimisticLockingFailureException -> retry
```
- `@Version` gives correctness on a single DB. Across services you need a saga or 2PC; strong consistency spanning services is expensive.

## Remember
- Use strong consistency only where required (money, stock). Everything else can be eventual.

Q: Optimistic vs pessimistic locking?
A: Optimistic (@Version) assumes conflicts are rare and retries on conflict. Pessimistic (`SELECT FOR UPDATE`) blocks others; use when contention is high.

@@ 2 | MODULE | Availability Patterns | Fail-over and replication | MUST
How to stay up when machines die.

@@ 3 | TOPIC | Fail-Over: Active - Active | Both nodes serve traffic; one dies, the other takes all | MUST
# Active-active fail-over

Two or more nodes **all serve traffic** at the same time. If one dies the load balancer sends everything to the survivors.

```text
         +--> App A (50%) --+
Client -> LB                 +--> DB
         +--> App B (50%) --+
```

## Pros / cons
- (+) No idle hardware, instant fail-over, scales reads.
- (-) Each node must handle the **full** load alone after a failure (capacity planning).
- (-) If the nodes hold data, you need conflict resolution (hard for databases).

## Spring Boot: health-driven LB

```yaml
management:
  endpoints.web.exposure.include: health
  endpoint.health.probes.enabled: true   # /actuator/health/liveness & readiness
```
- The LB/Kubernetes calls `/readiness`; an unhealthy instance is removed from rotation automatically.

Q: Active-active with a database?
A: Stateless app tier is easy. For the DB you need multi-master with conflict resolution or partition data by region, which is complex; many teams keep a single writer.

@@ 3 | TOPIC | Fail-Over: Active - Passive | Standby takes over only when the active fails | MUST
# Active-passive fail-over

One node is **active**; a **passive standby** waits. A heartbeat detects failure and the standby is promoted (VIP moves).

- **Hot standby:** already running and in sync, fail-over in seconds.
- **Cold standby:** started on demand, slow.

```text
Client -> Active (serves) ~~heartbeat~~ Passive (idle, replicating)
```

## Pros / cons
- (+) Simple, no conflict resolution.
- (-) Idle cost, small downtime window, risk of **split brain** (both think they are active) → use fencing / quorum.

## Example: PostgreSQL primary + standby

```text
Primary --WAL streaming--> Standby
Patroni / pg_auto_failover promotes standby when primary stops responding
App connects via a virtual IP or a connection string with multiple hosts:
jdbc:postgresql://db1,db2/app?targetServerType=primary
```
- The JDBC driver tries hosts until it finds the primary, so the Spring Boot app survives a promotion without code change.

Q: What is split brain and how to avoid it?
A: Two nodes both act as primary and accept writes, causing divergence. Prevent with quorum/leader election and fencing (STONITH).

@@ 3 | TOPIC | Replication: Master - Slave | One writer, many read replicas | MUST
# Master-slave (primary-replica) replication

The **master** takes all writes and streams changes to **slaves** (replicas) that serve reads.

```text
Writes -> Master --async--> Replica 1 -> reads
                 --async--> Replica 2 -> reads
```

## Pros / cons
- (+) Scales **reads**; backups from a replica; simple.
- (-) Replication lag → stale reads. Master is a write bottleneck and single point of failure until promotion.

## Spring Boot read/write splitting

```java
class RoutingDataSource extends AbstractRoutingDataSource {
    @Override protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? "replica" : "primary";
    }
}
```
- `@Transactional(readOnly = true)` routes to a replica; everything else hits the primary. Use `LazyConnectionDataSourceProxy` so the routing sees the final read-only flag.

Q: Replica is lagging; user sees old data. Fix?
A: Read from primary after writes (read-your-writes), monitor lag, or accept eventual consistency for non-critical reads.

@@ 3 | TOPIC | Replication: Master - Master | Multiple writers, needs conflict resolution | MUST
# Master-master (multi-primary) replication

Several nodes accept **writes** and replicate to each other.

## Pros / cons
- (+) Write availability if a node dies; writes close to users in each region.
- (-) **Write conflicts** (same row updated on two masters). Needs last-write-wins, versioning or app-level merge.
- (-) Loosened consistency; harder to reason about; auto-increment ID collisions (use UUID/ULID/Snowflake).

## Conflict example

```text
t1: Master A sets price = 10
t1: Master B sets price = 12     (concurrently)
=> replicas disagree. LWW keeps the later timestamp (one update silently lost).
```

## Tip for the interview
- Prefer **single-writer per key** (partition by region/user) over true multi-master unless you must write everywhere.
- Mention clocks: LWW depends on synchronized clocks → use logical/hybrid clocks.

Q: When pick master-master?
A: Multi-region active-active writes with tolerance for eventual consistency (e.g., collaborative, shopping cart). Avoid for financial ledgers.

@@ 2 | MODULE | Availability in Numbers | The nines and how components combine | SHOULD
Translate "nines" into downtime and combine components.

@@ 3 | TOPIC | 99.9% Availability - three 9s | About 8.77 hours downtime per year | SHOULD
# Three nines (99.9%)

| Period | Allowed downtime |
|---|---|
| Per year | ~8h 46m |
| Per month | ~43m 50s |
| Per week | ~10m 5s |
| Per day | ~1m 26s |

Achievable with a **single redundant tier** and manual/slow recovery. Typical for internal tools.

## SLO vs SLA vs SLI
- **SLI:** the measurement (e.g., % of successful requests).
- **SLO:** your internal target (99.9%).
- **SLA:** the contract with penalties (usually looser than SLO).

Q: Is a planned 30-minute monthly maintenance within 99.9%?
A: Only just (43 min/month budget). Planned windows usually count unless the SLA excludes them; prefer zero-downtime deploys (rolling/blue-green).

@@ 3 | TOPIC | 99.99% Availability - four 9s | About 52 minutes downtime per year | SHOULD
# Four nines (99.99%)

| Period | Allowed downtime |
|---|---|
| Per year | ~52m 36s |
| Per month | ~4m 23s |
| Per week | ~1m 5s |

A human cannot react in 4 minutes: needs **automatic** fail-over, health checks, multi-AZ, zero-downtime deploys and no single point of failure.

## What it takes (Spring Boot service)
- N≥2 instances across availability zones behind a load balancer.
- Readiness/liveness probes + auto restart (Kubernetes).
- DB with automatic fail-over (multi-AZ), connection retry.
- Circuit breaker, timeouts and retries so a slow dependency does not take you down (Resilience4j).
- Rolling/canary deploys, backward-compatible DB migrations (Flyway).

Q: Cost of each extra nine?
A: Roughly an order of magnitude more cost and complexity. Pick the nines the business actually needs.

@@ 3 | TOPIC | Availability in Parallel vs Sequence | Serial multiplies, parallel improves | SHOULD
# Availability: sequence vs parallel

## In sequence (all must work)
```text
A = A1 * A2
99.9% * 99.9% = 99.8%   (worse than either)
```
Chain of services: `Gateway -> Order -> Payment -> DB`. Each dependency **reduces** total availability.

## In parallel (any one is enough)
```text
A = 1 - (1 - A1) * (1 - A2)
1 - (0.001 * 0.001) = 99.9999%
```
Redundant instances behind a load balancer **raise** availability.

## Interview insight
- A request that synchronously calls 5 services at 99.9% each = 99.5% total (~1.8 days/year down).
- Fixes: fewer synchronous hops, async messaging, caching fallbacks, redundancy (parallel) for each tier.

## Example

```java
@CircuitBreaker(name = "inventory", fallbackMethod = "cachedStock")
public Stock stock(Long sku) { return inventoryClient.get(sku); }

Stock cachedStock(Long sku, Throwable t) { return cache.get(sku); }  // degrade, don't fail
```
- A fallback turns a hard sequential dependency into a soft one.

Q: Two 99% services in series, availability?
A: 0.99 * 0.99 = 98.01%. Two in parallel: 1 - 0.01*0.01 = 99.99%.
