@@ 2 | MODULE | Databases | SQL, replication, sharding and data modeling | MUST
The hardest tier to scale. Know every lever and its cost.

@@ 3 | TOPIC | SQL vs NoSQL | Pick by data shape, consistency and scale needs | MUST
# SQL vs NoSQL

| | SQL (PostgreSQL, MySQL) | NoSQL (Mongo, Cassandra, Redis, DynamoDB) |
|---|---|---|
| Model | Tables, relations, joins | Key-value, document, wide-column, graph |
| Schema | Fixed, enforced | Flexible |
| Transactions | Full ACID | Often per-record/partition |
| Scale | Vertical + replicas, sharding is manual | Built for horizontal scale |
| Query | Powerful ad-hoc SQL | Designed around known access patterns |

## How to decide
- **Default to PostgreSQL.** It handles most systems up to very large scale (JSONB, full-text, partitioning).
- Choose NoSQL when: massive write throughput, simple access by key, flexible/nested documents, geo-distribution, or a specific model (graph, time-series).
- Often use **both** (polyglot persistence): PostgreSQL for orders, Redis for cache, Elasticsearch for search.

## Spring Data: same app, different stores

```java
interface OrderRepo extends JpaRepository<Order, Long> {}           // PostgreSQL
interface SessionRepo extends CrudRepository<Session, String> {}     // Redis
interface ProductDocRepo extends MongoRepository<Product, String> {} // MongoDB
```

## ACID vs BASE
- **ACID:** Atomic, Consistent, Isolated, Durable.
- **BASE:** Basically Available, Soft state, Eventually consistent.

Q: "Why not just use MongoDB for everything?"
A: Multi-document transactions, joins and strong constraints are easier and safer in SQL. Use Mongo when the data is genuinely document-shaped with flexible schema.

@@ 3 | TOPIC | RDBMS | ACID, indexes, isolation levels and transactions | MUST
# RDBMS essentials

## ACID
- **Atomicity:** all or nothing (rollback).
- **Consistency:** constraints always hold.
- **Isolation:** concurrent transactions don't see each other's partial work.
- **Durability:** committed data survives crashes (WAL).

## Isolation levels

| Level | Prevents | Still allows |
|---|---|---|
| Read committed (PG default) | Dirty reads | Non-repeatable, phantom |
| Repeatable read (MySQL default) | + Non-repeatable | Phantom (PG blocks it) |
| Serializable | Everything | Slowest, retries needed |

## Indexes (B-tree)

```sql
CREATE INDEX idx_orders_user_created ON orders (user_id, created_at DESC);
-- supports: WHERE user_id = ? ORDER BY created_at DESC   (leftmost-prefix rule)
```
- Indexes speed reads but slow writes and use space. Index columns in `WHERE`, `JOIN`, `ORDER BY`.

## Spring transaction pitfalls

```java
@Service class A {
    @Transactional public void outer() { inner(); }   // inner() is a plain self-call
    @Transactional(propagation = REQUIRES_NEW)
    public void inner() { ... }                       // proxy bypassed: annotation ignored!
}
```
- `@Transactional` works through a **proxy**; self-invocation and `private` methods skip it. Call through another bean.
- Checked exceptions do **not** roll back by default: use `rollbackFor`.

Q: What is a deadlock and how to avoid it?
A: Two transactions wait for each other's locks. Avoid by locking rows in a consistent order, keeping transactions short, and retrying on `DeadlockLoserDataAccessException`.

@@ 3 | TOPIC | Replication | Copy data to more nodes for reads, safety and failover | MUST
# Database replication

Keep copies of data on several nodes.

| Mode | Behavior | Trade-off |
|---|---|---|
| Synchronous | Commit waits for replica ack | No data loss, higher write latency |
| Asynchronous | Commit returns immediately | Fast, but lag and possible loss on fail-over |
| Semi-sync | Wait for at least one replica | Balance |

## Purposes
1. **Read scaling** (replicas serve reads).
2. **High availability** (promote a replica).
3. **Backups / analytics** off the primary.

## Monitor lag

```sql
-- PostgreSQL, on the primary
SELECT client_addr, replay_lag FROM pg_stat_replication;
```
- Alert when lag exceeds your tolerance; route sensitive reads to the primary.

## Remember
- Replication scales **reads only**. Write throughput still bounded by a single primary → sharding.
- Replication is not backup: a bad `DELETE` replicates instantly. Keep PITR backups.

Q: How do you scale writes?
A: Vertical scaling, batching/async writes, reduce write amplification (indexes), then shard by a good key.

@@ 3 | TOPIC | Sharding | Split data across databases by a shard key | MUST
# Sharding (horizontal partitioning)

Split one big table across **N independent databases**, each holding a subset of rows.

```text
user_id % 4 == 0 -> shard0   |  1 -> shard1  |  2 -> shard2  |  3 -> shard3
```

## Strategies

| Strategy | How | Issue |
|---|---|---|
| Range | `id 1-1M` → shard A | Hot shards (latest range) |
| Hash | `hash(key) % N` | Resharding moves most data |
| Consistent hashing | Ring with virtual nodes | Only ~1/N keys move on change |
| Directory/lookup | Mapping table | Lookup is a bottleneck/SPOF |

## Choosing the shard key (most-asked)
- High cardinality, even distribution, matches the main query (e.g., `user_id` for per-user data).
- Bad: `country` (skew), `created_at` (hot latest shard).

## Spring Boot: routing by tenant/shard

```java
class ShardRouter extends AbstractRoutingDataSource {
    @Override protected Object determineCurrentLookupKey() {
        return "shard" + (ShardContext.userId() % 4);
    }
}
```
- ShardContext is set from the request (e.g., filter). Prefer proven tools (Vitess, Citus, ShardingSphere) over hand-rolled.

## Costs
- Cross-shard joins/transactions are hard or impossible; avoid by modeling per-shard data.
- Resharding is painful; globally unique IDs needed (Snowflake/ULID).
- Hot keys (celebrity user) still overload one shard.

Q: Sharding vs partitioning?
A: Partitioning splits a table inside one DB (e.g., PostgreSQL partitions by month). Sharding spreads data over multiple DB servers.

@@ 3 | TOPIC | Federation | Split databases by function | MUST
# Federation (functional partitioning)

Split one database into several **by business function**: users DB, products DB, orders DB.

```text
Before:  one DB (users + products + orders)
After:   users-db | products-db | orders-db   (each with its own load and replicas)
```

## Pros / cons
- (+) Smaller DBs → better cache hit rate, less write contention, independent scaling.
- (-) Cross-database joins need app-level code or API calls; more connections to manage.
- It is the data-side of **microservices** (database per service).

## Spring Boot: multiple DataSources

```java
@Configuration
@EnableJpaRepositories(basePackages = "app.users", entityManagerFactoryRef = "usersEmf",
                       transactionManagerRef = "usersTx")
class UsersDbConfig { /* DataSource + EMF + TxManager for users-db */ }
```
- A `@Transactional` spans only one manager; a transaction cannot cover two DBs without JTA/saga.

Q: Federation or sharding first?
A: Federation is simpler and mirrors service boundaries, so start there. Shard a single domain's table only when it alone outgrows a server.

@@ 3 | TOPIC | Denormalization | Trade write cost and redundancy for faster reads | MUST
# Denormalization

Normalized data avoids duplication but needs **joins**. Denormalize by storing redundant data to **avoid joins** on hot read paths.

```text
Normalized:  orders(user_id) JOIN users(name)
Denormalized: orders(user_id, user_name)   // copy name into orders
```

## When
- Read-heavy; joins are the bottleneck; data rarely changes.

## Costs
- Writes must update every copy; risk of inconsistency; more storage.

## Spring/JPA options

```java
@Entity @Immutable
@Subselect("SELECT o.id, u.name FROM orders o JOIN users u ON o.user_id = u.id")
class OrderView { ... }          // read model

// Or a materialized view in PostgreSQL
// CREATE MATERIALIZED VIEW order_summary AS ...;  REFRESH MATERIALIZED VIEW CONCURRENTLY order_summary;
```
- In **NoSQL** denormalization is the norm: model by query (embed `user` snapshot in the order document).
- Snapshot data that must not change later: order stores the **price at purchase** and address copy, not a reference.

Q: Name a time you should *not* denormalize.
A: Frequently updated values (user name edits) copied to millions of rows; use a join/cache or an event to propagate.

@@ 3 | TOPIC | SQL Tuning | Find slow queries, add the right indexes, fix N+1 | SHOULD
# SQL tuning

## Method
1. **Measure:** slow query log, `pg_stat_statements`.
2. **Explain:** `EXPLAIN (ANALYZE, BUFFERS) ...` → look for `Seq Scan` on big tables.
3. **Fix:** index, rewrite query, reduce data.
4. **Re-measure.**

## High-impact fixes
- Add index matching the filter/sort (composite, leftmost-prefix).
- Avoid `SELECT *`; select only needed columns.
- Avoid functions on indexed columns: `WHERE lower(email)=?` skips the index (use a functional index).
- **Keyset pagination** instead of deep `OFFSET`.
- Batch inserts; use appropriate types.

```sql
-- Slow at page 10,000
SELECT * FROM orders ORDER BY id LIMIT 20 OFFSET 200000;
-- Fast (keyset)
SELECT * FROM orders WHERE id > :lastSeenId ORDER BY id LIMIT 20;
```

## N+1 problem in JPA (very common question)

```java
List<Order> orders = repo.findAll();             // 1 query
orders.forEach(o -> o.getItems().size());        // +N queries (lazy load per order)

// Fix
@Query("select o from Order o join fetch o.items")  // or @EntityGraph(attributePaths = "items")
List<Order> findAllWithItems();
```
- Enable `spring.jpa.properties.hibernate.generate_statistics` or p6spy in dev to count queries.

Q: Query is slow although an index exists. Why?
A: The planner may ignore it (low selectivity, type mismatch, function on column, stale stats). Read the plan, run `ANALYZE`, fix the predicate.

@@ 2 | MODULE | NoSQL | Key-value, document, wide-column and graph stores | MUST
Each type fits a specific access pattern.

@@ 3 | TOPIC | Key-Value Store | Hash map at scale: get/put by key | MUST
# Key-value store

`get(key)`, `put(key, value)`, O(1). Value is opaque.

**Examples:** Redis, Memcached, DynamoDB, etcd.

## Use cases
- Cache, sessions, rate-limit counters, feature flags, shopping cart, leaderboards (Redis sorted sets).

## Spring Data Redis

```java
@Service
class RateLimiter {
    private final StringRedisTemplate redis;
    boolean allow(String userId) {
        String key = "rl:" + userId + ":" + (System.currentTimeMillis() / 60_000);
        Long n = redis.opsForValue().increment(key);       // atomic
        if (n == 1) redis.expire(key, Duration.ofMinutes(1));
        return n <= 100;                                    // 100 req/min
    }
}
```
- `INCR` is atomic, so no race between instances. Fixed window is simple; sliding window/token bucket are smoother.

## Limits
- No rich queries; modeling must be by key. Data must fit memory (Redis) or be partitioned.

Q: How does Redis stay fast?
A: In-memory, single-threaded command execution (no locks), efficient data structures, and I/O multiplexing.

@@ 3 | TOPIC | Document Store | JSON documents with flexible schema | MUST
# Document store

Stores self-contained JSON/BSON documents, queryable by fields.

**Examples:** MongoDB, Couchbase, Firestore.

```json
{ "_id": "o-1001", "user": { "id": 7, "name": "Rahim" },
  "items": [ { "sku": "A1", "qty": 2, "price": 150 } ], "status": "PAID" }
```
- Order + items + user snapshot in **one document** → read with one lookup, no joins.

## Spring Data MongoDB

```java
@Document("orders")
record Order(@Id String id, User user, List<Item> items, String status) {}

interface OrderRepo extends MongoRepository<Order, String> {
    List<Order> findByUserIdAndStatus(long userId, String status);
}
```

## Embed vs reference
- **Embed** when data is read together and bounded in size (items).
- **Reference** when unbounded or shared (comments of a viral post).
- Document size limit (16 MB in Mongo): don't embed unbounded arrays.

Q: Good fit for a document store?
A: Catalogs, content, user profiles, event payloads with varying attributes.

@@ 3 | TOPIC | Wide Column Store | Rows with dynamic columns, partitioned for massive writes | MUST
# Wide-column store

Data grouped by **partition key**, sorted by **clustering key** inside a partition. Optimized for huge write volume and time-series.

**Examples:** Cassandra, HBase, ScyllaDB, Bigtable.

```sql
-- Cassandra: design the table for ONE query
CREATE TABLE messages_by_chat (
  chat_id uuid, sent_at timeuuid, sender text, body text,
  PRIMARY KEY ((chat_id), sent_at)
) WITH CLUSTERING ORDER BY (sent_at DESC);

SELECT * FROM messages_by_chat WHERE chat_id = ? LIMIT 50;   -- latest 50, one partition
```

## Rules
- **Query-first modeling:** one table per query; duplicate data freely.
- No joins; avoid huge partitions (bound with a time bucket: `(chat_id, day)`).
- Tunable consistency (ONE/QUORUM/ALL), write path is LSM-tree (append, very fast).

## Use cases
- Chat history, IoT/telemetry, activity feeds, audit logs.

Q: Why is Cassandra good at writes?
A: Writes go to a commit log and memtable (sequential, in-memory) and are flushed as immutable SSTables; no read-before-write, no in-place update.

@@ 3 | TOPIC | Graph Databases | Nodes and edges for relationship-heavy queries | SHOULD
# Graph databases

Store **nodes** and **relationships**; traversal is cheap (no expensive joins).

**Examples:** Neo4j, Amazon Neptune.

```cypher
// Friends of friends who like the same movie
MATCH (me:User {id: 7})-[:FRIEND]->()-[:FRIEND]->(fof)-[:LIKES]->(m:Movie)<-[:LIKES]-(me)
RETURN DISTINCT fof.name, m.title
```
- The same query in SQL needs several self-joins that explode as depth grows.

## Use cases
- Social networks, recommendations, fraud rings, knowledge graphs, access-control graphs.

## Spring Data Neo4j

```java
@Node class User { @Id Long id; String name; @Relationship("FRIEND") Set<User> friends; }
interface UserRepo extends Neo4jRepository<User, Long> {}
```

Q: When not to use a graph DB?
A: Simple CRUD and aggregates, or when relationships are shallow. Scaling a graph across shards is hard.

@@ 2 | MODULE | Types of Caching | Where you can cache in the request path | MUST
Cache at every layer, closest to the user first.

@@ 3 | TOPIC | Client Caching | Browser and mobile cache using HTTP headers | MUST
# Client caching

The fastest request is the one never sent. Browser/mobile apps cache responses using headers.

| Header | Meaning |
|---|---|
| `Cache-Control: max-age=3600` | Fresh for 1 hour, no request |
| `ETag` + `If-None-Match` | Revalidate, server replies 304 (no body) |
| `Cache-Control: no-store` | Never store (sensitive) |

```java
@GetMapping("/products/{id}")
ResponseEntity<Product> get(@PathVariable Long id, WebRequest req) {
    Product p = service.get(id);
    String etag = "\"" + p.version() + "\"";
    if (req.checkNotModified(etag)) return null;      // sends 304 automatically
    return ResponseEntity.ok().eTag(etag).body(p);
}
```
- `checkNotModified` saves bandwidth and serialization; the DB read can also be skipped with a cheap version check.

Q: How to cache static JS safely?
A: Hash the filename and set `max-age=1 year, immutable`; a new deploy changes the URL.

@@ 3 | TOPIC | CDN Caching | Edge caching of static and cacheable API responses | MUST
# CDN caching

CDN edge nodes cache responses near users, absorbing most traffic before it reaches your origin.

## What to cache
- Static assets (JS, CSS, images, video).
- Public API responses (`GET /products`, `/config`) with short TTL.

## Key concepts
- **Cache key:** URL + selected headers/query. Normalize to raise hit ratio.
- **TTL & invalidation:** purge by URL/tag or use versioned URLs.
- **Origin shield:** one mid-tier cache to reduce origin hits.
- **`Vary`:** `Vary: Accept-Encoding`; avoid `Vary: Cookie` (kills hit rate).

```java
ResponseEntity.ok()
    .header("Cache-Control", "public, s-maxage=60, stale-while-revalidate=30")
    .body(catalog);       // CDN (s-maxage) keeps 60s; browser uses default
```
- `s-maxage` targets shared caches (CDN) separately from browsers.

Q: Metric to watch?
A: **Cache hit ratio** (>90% for static); and origin offload.

@@ 3 | TOPIC | Web Server Caching | Reverse-proxy cache (Nginx, Varnish) in front of the app | MUST
# Web server / reverse-proxy caching

A reverse proxy keeps copies of responses and serves them without touching the application.

```nginx
proxy_cache_path /var/cache/nginx keys_zone=api:50m max_size=1g inactive=10m;
location /api/catalog {
    proxy_cache api;
    proxy_cache_valid 200 1m;
    proxy_cache_use_stale error timeout updating;   # serve stale if app is down
    proxy_pass http://spring_backend;
}
```

## Benefits
- Protects Spring Boot from traffic spikes; serves stale content during outages.
- Request collapsing (`proxy_cache_lock`): 1000 identical misses → 1 origin call (stops stampede).

## Remember
- Only cache **safe, idempotent, non-personalized** responses. Key by user/tenant if content differs.

Q: Difference vs CDN cache?
A: Same idea; CDN is global and edge-located, a reverse-proxy cache is in your data center in front of the app.

@@ 3 | TOPIC | Database Caching | DB buffer cache, query cache and tuning | MUST
# Database caching

The database already caches internally.

- **Buffer pool / shared_buffers:** hot data pages stay in RAM. Fit the working set in memory.
- **Query plan cache / prepared statements:** parse and plan once.
- **MySQL query cache** was removed (8.0): invalidation cost outweighed benefit.
- **Materialized views:** precomputed results.

## Tuning actions
- Enough RAM for the working set, right `shared_buffers`/`innodb_buffer_pool_size`.
- Use **prepared statements** and a connection pool.

```yaml
spring.datasource.hikari:
  maximum-pool-size: 20
  data-source-properties:
    prepStmtCacheSize: 250       # MySQL driver prepared-statement cache
    cachePrepStmts: true
```

## Hibernate second-level cache
- L1 = per `EntityManager` (always on). L2 = shared across sessions (Ehcache/Hazelcast/Redis); good for read-mostly reference data.
```java
@Entity @Cache(usage = CacheConcurrencyStrategy.READ_ONLY) class Country { ... }
```

Q: When is L2 cache dangerous?
A: With multiple app instances and writes: stale data unless the cache is distributed/invalidated. Use it only for read-mostly data.

@@ 3 | TOPIC | Application Caching | In-process (Caffeine) and distributed (Redis) caches | MUST
# Application caching

## Local vs distributed

| | Local (Caffeine) | Distributed (Redis) |
|---|---|---|
| Latency | ~100 ns | ~1 ms network |
| Consistency across pods | Each pod differs | Shared |
| Capacity | JVM heap | Large |
| Use | Hot, small, tolerant of staleness | Shared state, larger data |

Often both: **L1 Caffeine + L2 Redis**.

## Spring Cache abstraction

```java
@EnableCaching
@Configuration class CacheConfig {
    @Bean RedisCacheManager cacheManager(RedisConnectionFactory cf) {
        return RedisCacheManager.builder(cf)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
            .build();
    }
}

@Service class ProductService {
    @Cacheable(value = "product", key = "#id")
    public Product get(Long id) { return repo.findById(id).orElseThrow(); }

    @CacheEvict(value = "product", key = "#p.id")
    public void update(Product p) { repo.save(p); }
}
```
- `@Cacheable` is also proxy-based: self-invocation bypasses the cache. Always set a **TTL**.

## Hard problems
- **Stampede:** a hot key expires, thousands hit the DB → `@Cacheable(sync = true)`, jittered TTL, request coalescing.
- **Penetration:** requests for non-existent keys → cache null briefly or Bloom filter.
- **Hot key / big key**; **invalidation** ("one of the two hard things").

Q: Cache or DB — which is the source of truth?
A: DB. The cache is a disposable copy; the system must still work (slower) if it is empty.

@@ 2 | MODULE | Caching Strategies | How data enters and leaves the cache | MUST
Read and write patterns.

@@ 3 | TOPIC | Cache Aside | App checks cache first, loads from DB on miss | MUST
# Cache-aside (lazy loading)

```text
read:  cache hit? -> return
       miss -> read DB -> put in cache -> return
write: update DB, then DELETE the cache key
```

```java
public Product get(Long id) {
    Product p = redis.get("p:" + id);
    if (p != null) return p;
    p = repo.findById(id).orElseThrow();
    redis.set("p:" + id, p, Duration.ofMinutes(10));
    return p;
}

@Transactional
public void update(Product p) {
    repo.save(p);
    redis.delete("p:" + p.id());    // invalidate, don't update (avoids race ordering bugs)
}
```
- `@Cacheable` in Spring is cache-aside.

## Pros / cons
- (+) Only requested data cached; cache failure doesn't break reads; simple.
- (-) First read slow; stale window; stampede on expiry.
- Race: read-miss loads old value while a write invalidates → short stale entry. TTL bounds it.

Q: Delete vs update the cache on write?
A: Delete. Updating risks writing an older value out of order under concurrency.

@@ 3 | TOPIC | Write-through | Write to cache and DB together synchronously | MUST
# Write-through

Every write goes to the **cache and DB synchronously**; reads always hit a warm cache.

```text
write -> cache (update) -> DB (update) -> ack
read  -> cache (always populated)
```

## Pros / cons
- (+) Cache never stale for written data; fast reads.
- (-) Higher write latency (two writes); caches data that may never be read.

## Spring example

```java
@CachePut(value = "product", key = "#p.id")   // write-through-like: always refresh cache
@Transactional
public Product save(Product p) { return repo.save(p); }
```
- Combine with cache-aside for reads. Good for read-heavy data where freshness matters (profiles).

Q: Write-through failure case?
A: Cache updated but DB write fails (or reverse) → inconsistency. Order the operations and handle rollback, or let the cache provider do the DB write.

@@ 3 | TOPIC | Write-behind | Write to cache, flush to DB asynchronously | MUST
# Write-behind (write-back)

Write to cache, ack quickly, and **flush to DB later in batches**.

```text
write -> cache -> ack
          \-> queue -> batch flush to DB every N sec
```

## Pros / cons
- (+) Very fast writes, batching reduces DB load, absorbs spikes.
- (-) **Data loss** if the cache dies before the flush; eventual consistency; complex.

## Use cases
- Counters (views, likes), telemetry, where small loss is OK.

```java
// view counter: increment in Redis, flush periodically
redis.opsForValue().increment("views:" + postId);

@Scheduled(fixedDelay = 5000)
void flush() { /* read-and-reset counters, then UPDATE posts SET views = views + :n */ }
```
- For must-not-lose data use a durable log (Kafka) as the buffer instead of memory.

Q: Why not write-behind for payments?
A: Acknowledged but unsaved data can be lost. Payments need durable synchronous writes.

@@ 3 | TOPIC | Refresh Ahead | Reload hot entries before they expire | SHOULD
# Refresh-ahead

Predict which keys will be requested and **refresh them just before TTL expiry** so users never see a miss.

```text
TTL = 10 min. When a hit happens after minute 8 -> async reload in background; return current value.
```

## Pros / cons
- (+) Low read latency, avoids stampede on hot keys.
- (-) Needs good prediction; wasted refreshes on keys no longer used.

## Caffeine support

```java
Caffeine.newBuilder()
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .refreshAfterWrite(8, TimeUnit.MINUTES)       // async reload, serves old value meanwhile
    .build(key -> loadFromDb(key));               // LoadingCache
```
- `refreshAfterWrite` returns the stale value instantly and reloads on a background thread.

Q: Refresh-ahead vs cache-aside?
A: Cache-aside loads on a miss (user waits). Refresh-ahead loads before the miss, ideal for a small set of very hot keys.

@@ 2 | MODULE | Asynchronism | Queues and decoupling | MUST
Do slow work later, smooth bursts.

@@ 3 | TOPIC | Message Queues | Producers and consumers decoupled by a broker | MUST
# Message queues

A broker stores messages; producers don't wait for consumers.

```text
Order API --publish--> [ Kafka / RabbitMQ ] --consume--> Email / Inventory / Analytics
```

## Why
- **Decoupling**, **buffering spikes**, **retry**, **fan-out**, independent scaling.

## RabbitMQ vs Kafka

| | RabbitMQ | Kafka |
|---|---|---|
| Model | Smart broker, queues, routing | Distributed append-only log |
| Delivery | Push, message removed after ack | Pull, retained; consumers track offset |
| Ordering | Per queue | Per partition |
| Best for | Task queues, complex routing | High-throughput streams, replay, event sourcing |

## Spring Boot Kafka

```java
@Service class Publisher {
    private final KafkaTemplate<String, OrderPlaced> kafka;
    void send(OrderPlaced e) { kafka.send("orders", e.orderId(), e); }    // key => same partition => ordered per order
}

@KafkaListener(topics = "orders", groupId = "inventory", concurrency = "3")
void on(OrderPlaced e) { inventory.reserve(e); }
```

## Delivery semantics
- At-most-once, **at-least-once (default, need idempotent consumers)**, exactly-once (Kafka transactions, limited scope).
- Failed messages → retry with backoff → **dead-letter topic (DLT)**.

Q: How to keep order in Kafka?
A: Use the same key so messages land in the same partition; one consumer per partition in a group.

@@ 3 | TOPIC | Task Queues | Background job processing with workers | MUST
# Task queues

A task queue holds **units of work** (resize image, send invoice) executed by worker processes. Focus is on job execution: retries, scheduling, priorities, results.

**Examples:** Celery (Python), Sidekiq (Ruby), Spring: `@Async` + executor, Spring Batch, RabbitMQ workers, Quartz, JobRunr/db-scheduler.

## Spring Boot `@Async` with a bounded pool

```java
@EnableAsync @Configuration
class AsyncConfig {
    @Bean ThreadPoolTaskExecutor taskExecutor() {
        var ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(8); ex.setMaxPoolSize(16);
        ex.setQueueCapacity(500);                                    // bounded: back pressure
        ex.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return ex;
    }
}
@Async void generatePdf(Long id) { ... }
```
- In-memory `@Async` is **not durable**: a crash loses queued tasks. For durable tasks use a broker or DB-backed jobs.

## Remember
- Tasks need retry with exponential backoff, idempotency, timeouts, and a dead-letter store.

Q: Message queue vs task queue?
A: A message queue transports messages between services; a task queue is a pattern/product built on it for running jobs with workers, retries and scheduling.

@@ 3 | TOPIC | Back Pressure | Slow down or reject producers when consumers are overloaded | MUST
# Back pressure

If producers are faster than consumers, queues grow without bound → memory exhaustion and huge latency. Back pressure **pushes the overload back** to the producer.

## Techniques
- **Bounded queues** that block or reject (return `503`/`429`).
- **Rate limiting** at the gateway.
- **Load shedding:** drop low-priority work.
- Consumers **pull** at their own pace (Kafka polling) and scale out by lag.
- Reactive streams (`Flux`) with demand signalling.

```java
// Reactor: consumer controls the rate
Flux.range(1, 1_000_000)
    .onBackpressureBuffer(1_000, dropped -> log.warn("dropped"))
    .publishOn(Schedulers.boundedElastic())
    .subscribe(this::process, e -> {}, () -> {}, s -> s.request(10));

// Servlet: reject when saturated
if (!executor.getThreadPoolExecutor().getQueue().offer(task)) throw new ResponseStatusException(TOO_MANY_REQUESTS);
```
- Autoscale consumers on **consumer lag** (KEDA with Kafka).

Q: Queue depth keeps growing. What do you do?
A: Add consumers (up to partition count), speed up processing, apply back pressure/rate limit producers, and shed or defer low-priority work.

@@ 3 | TOPIC | Idempotent Operations | Repeating a request has the same effect as doing it once | MUST
# Idempotent operations

Networks fail and clients/brokers **retry**. If a retry charges a card twice, you lose money. An idempotent operation gives the same result no matter how many times it runs.

- Naturally idempotent: `PUT /users/7`, `DELETE /users/7`, `SET x = 5`.
- Not: `POST /payments`, `balance = balance - 10`.

## Idempotency key pattern

```java
@PostMapping("/payments")
ResponseEntity<Payment> pay(@RequestHeader("Idempotency-Key") String key, @RequestBody PayReq req) {
    return service.payOnce(key, req);
}

@Transactional
ResponseEntity<Payment> payOnce(String key, PayReq req) {
    Optional<IdemRecord> prev = idemRepo.findById(key);
    if (prev.isPresent()) return prev.get().response();          // replay stored result
    Payment p = gateway.charge(req);
    idemRepo.save(new IdemRecord(key, p));                        // PK on key => duplicate insert fails
    return ok(p);
}
```
- A **unique constraint** on the key makes concurrent duplicates fail safely.

## Idempotent consumer

```java
@KafkaListener(...)
@Transactional
void on(OrderPlaced e) {
    if (!processed.insertIfAbsent(e.eventId())) return;   // INSERT ... ON CONFLICT DO NOTHING
    inventory.reserve(e);
}
```

Q: How do you get "exactly-once" in practice?
A: At-least-once delivery + idempotent processing (dedupe by event/idempotency key).

@@ 2 | MODULE | Communication | Protocols and API styles | MUST
How components talk.

@@ 3 | TOPIC | HTTP | Request/response protocol, methods, status codes, versions | MUST
# HTTP

Stateless request-response protocol over TCP (HTTP/3 over QUIC).

## Methods

| Method | Safe | Idempotent | Typical use |
|---|---|---|---|
| GET | yes | yes | Read |
| POST | no | no | Create / action |
| PUT | no | yes | Replace |
| PATCH | no | no* | Partial update |
| DELETE | no | yes | Remove |

## Status codes
`200` OK, `201` Created, `204` No content, `301/302` redirect, `304` Not modified, `400` bad request, `401` unauthenticated, `403` forbidden, `404`, `409` conflict, `429` too many requests, `500` server error, `502/503/504` gateway/unavailable/timeout.

## Versions
- **1.1:** keep-alive, one request at a time per connection (head-of-line blocking).
- **2:** multiplexing, header compression, binary.
- **3:** QUIC/UDP, no TCP head-of-line blocking.

## Spring Boot client with timeouts (always set them)

```java
RestClient client = RestClient.builder()
    .requestFactory(new JdkClientHttpRequestFactory(
        HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build()))
    .build();
```
- Missing timeouts are the #1 cause of cascading failures: threads block forever on a slow dependency.

Q: 401 vs 403?
A: 401 = not authenticated (who are you?). 403 = authenticated but not allowed.

@@ 3 | TOPIC | TCP | Reliable, ordered, connection-oriented transport | MUST
# TCP

Guarantees **ordered, reliable** delivery via handshake, sequence numbers, acks, retransmission, flow and congestion control.

```text
Client SYN ->  <- SYN-ACK  Client ACK ->     (3-way handshake, 1 RTT)
... data ...
FIN / ACK ... (close)
```

## Costs
- Handshake latency (plus TLS), head-of-line blocking on loss, slow start.
- Mitigate: **connection pooling / keep-alive**, HTTP/2.

## Why it matters in Java
- Opening a connection per request is expensive → use pooled clients (Hikari for DB, Apache/Reactor Netty client pools).

```yaml
spring.datasource.hikari.maximum-pool-size: 20
```
- `TIME_WAIT` exhaustion at high rates happens when connections are not reused.

Q: Why TCP for payments but UDP for video calls?
A: Payments need every byte correct and ordered. Calls prefer low latency; a late packet is useless, so retransmission is wasted.

@@ 3 | TOPIC | UDP | Connectionless, low-latency, no delivery guarantee | MUST
# UDP

Sends datagrams with **no handshake, no ordering, no retransmission**. Lower overhead and latency.

**Used by:** DNS, video/voice (WebRTC), gaming, metrics (StatsD), QUIC/HTTP3 (reliability built on top).

| | TCP | UDP |
|---|---|---|
| Connection | Yes | No |
| Reliable / ordered | Yes | No |
| Overhead | Higher | Lower |
| Broadcast/multicast | No | Yes |

## Java sketch

```java
try (DatagramSocket socket = new DatagramSocket()) {
    byte[] data = "metric:1|c".getBytes(UTF_8);
    socket.send(new DatagramPacket(data, data.length, InetAddress.getByName("statsd"), 8125));
}
```
- Fire-and-forget: acceptable to lose an occasional metric.

Q: Is UDP "unreliable"?
A: It gives no guarantee; the application adds reliability if needed (QUIC does it in user space).

@@ 3 | TOPIC | REST | Resource-oriented APIs over HTTP | MUST
# REST

Resources identified by URLs, manipulated through HTTP verbs, **stateless**, usually JSON.

```java
@RestController
@RequestMapping("/api/v1/orders")
class OrderController {
    @GetMapping("/{id}")  Order get(@PathVariable Long id) { ... }               // 200 / 404
    @PostMapping          ResponseEntity<Order> create(@Valid @RequestBody OrderReq r) {
        Order o = service.create(r);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + o.id())).body(o);   // 201
    }
    @DeleteMapping("/{id}") @ResponseStatus(NO_CONTENT) void delete(@PathVariable Long id) { ... }
}
```

## Design points interviewers like
- Nouns + plural, proper status codes, **pagination** (`?page=&size=` or cursor), filtering/sorting.
- **Versioning** (`/v1`), **idempotency** keys for POST, consistent error body (ProblemDetail).
- **HATEOAS** exists but is rarely used.

## Pros / cons
- (+) Simple, cacheable, tooling everywhere.
- (-) Over/under-fetching, chatty for mobile, no strict contract (use OpenAPI).

Q: PUT vs PATCH?
A: PUT replaces the whole resource (idempotent). PATCH changes part of it.

@@ 3 | TOPIC | RPC | Call a remote function like a local one | MUST
# RPC (Remote Procedure Call)

The client calls `paymentService.charge(req)` and a framework turns it into a network call (serialize, send, deserialize). Action-oriented, not resource-oriented.

**Examples:** gRPC, Thrift, JSON-RPC, Java RMI (legacy).

## RPC vs REST

| | RPC | REST |
|---|---|---|
| Style | Verbs/methods | Resources |
| Coupling | Tighter (shared contract/stubs) | Looser |
| Performance | Binary, fast | Text JSON |
| Use | Internal service-to-service | Public APIs |

## The big warning
"Network calls look like local calls but are not": latency, failure, partial success. Always add **timeouts, retries (idempotent only), circuit breakers**.

```java
@FeignClient(name = "inventory", url = "${inventory.url}")
interface InventoryClient { @GetMapping("/stock/{sku}") Stock stock(@PathVariable String sku); }  // declarative RPC-ish
```

Q: Why internal RPC and public REST?
A: RPC is efficient and typed for internal calls you control; REST is universal and easy for third parties.

@@ 3 | TOPIC | gRPC | HTTP/2 + Protocol Buffers RPC framework | MUST
# gRPC

RPC over **HTTP/2** using **Protocol Buffers** (binary, schema-first). Supports unary, server-streaming, client-streaming and bidirectional streaming.

```proto
syntax = "proto3";
service InventoryService { rpc GetStock (StockRequest) returns (StockReply); }
message StockRequest { string sku = 1; }
message StockReply   { int32 quantity = 1; }
```

## Spring Boot (grpc-spring-boot-starter)

```java
@GrpcService
class InventoryGrpc extends InventoryServiceGrpc.InventoryServiceImplBase {
    @Override public void getStock(StockRequest r, StreamObserver<StockReply> out) {
        out.onNext(StockReply.newBuilder().setQuantity(repo.qty(r.getSku())).build());
        out.onCompleted();
    }
}
```

## Pros / cons
- (+) Small and fast, strong typed contract, code generation, streaming, deadlines.
- (-) Browsers need gRPC-Web, harder to debug (binary), needs L7/HTTP2-aware LB.
- Evolve schemas safely: only add fields, never reuse field numbers.

Q: When pick gRPC over REST?
A: Internal low-latency microservice calls, streaming, polyglot teams with strict contracts.

@@ 3 | TOPIC | GraphQL | Client chooses exactly the fields it needs | SHOULD
# GraphQL

One endpoint; the client sends a query describing the shape of the response. Fixes over-fetching and many round trips.

```graphql
query { order(id: 7) { id total user { name } items { sku qty } } }
```

## Spring for GraphQL

```java
@Controller
class OrderGraphql {
    @QueryMapping Order order(@Argument Long id) { return orders.find(id); }
    @SchemaMapping Customer user(Order o)        { return users.find(o.userId()); }
}
```

## Pitfalls
- **N+1 resolvers:** use `@BatchMapping` / DataLoader to batch.
- Hard to cache at HTTP level (all POST to one URL); need persisted queries.
- Expensive queries: enforce depth/complexity limits, auth per field.

## When
- Mobile/BFF aggregating many services, rapidly changing UIs. Not needed for simple CRUD.

Q: GraphQL vs REST trade-off?
A: GraphQL gives client flexibility and fewer round trips, at the cost of server complexity, caching difficulty and query-cost control.
