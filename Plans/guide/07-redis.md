@@ 1 | MODULE | Phase 7 — Redis Caching and Sessions | Data types, caching patterns, Spring Cache, sessions, locks, rate limiting | SHOULD
# Phase 7: Redis caching and sessions

**Redis** is an in-memory key-value store: extremely fast (sub-millisecond), used for caching, sessions, rate limiting, locks and queues.

**Analogy:** your database is a big warehouse at the edge of town. Redis is the small shelf right next to the shop counter, holding the items people ask for most often.

@@ 2 | TOPIC | Redis Fundamentals | Data types, core commands, TTL, persistence, eviction | SHOULD
# Redis fundamentals

## Data types and commands

```bash
# STRING: simple value (also counters)
SET po:7:name "Shakti Foundation"
GET po:7:name
SET otp:01712345678 "482913" EX 300     # expires in 300 seconds
INCR stats:repayments:2025-11-15       # atomic counter
TTL otp:01712345678                    # seconds left (-1 = no expiry, -2 = missing)

# HASH: an object with fields
HSET member:42 name "Rahim" phone "01712345678" po "PO-07"
HGET member:42 name
HGETALL member:42

# LIST: ordered, push/pop at the ends (queues)
LPUSH sms:queue "msg1"
RPOP sms:queue

# SET: unique members
SADD loan:7:viewers "rahim" "karim"
SISMEMBER loan:7:viewers "rahim"

# SORTED SET: members with a score (leaderboards, rankings, time windows)
ZADD po:collection:2025-11 1500000 "PO-07" 980000 "PO-12"
ZREVRANGE po:collection:2025-11 0 9 WITHSCORES   # top 10 POs by collection

DEL po:7:name
EXPIRE member:42 3600
```

**Line by line**
- **Key naming:** use `object:id:field` with colons. It is readable and groups related keys.
- `EX 300`: the key **deletes itself** after 5 minutes. Perfect for OTPs.
- `INCR`: atomic. Even if 100 servers increment at the same moment, no count is lost (Redis runs commands one at a time).
- **Hash:** like a small row/object. Fields can be updated individually.
- **List:** `LPUSH` + `RPOP` = a FIFO queue.
- **Set:** no duplicates. `SISMEMBER` is O(1).
- **Sorted set:** kept ordered by score. `ZREVRANGE ... 0 9` gives the top 10.

## Why is Redis so fast?
1. Data lives in **RAM**.
2. A **single-threaded** command loop: no locks needed, and every command is atomic.
3. Simple data structures with O(1)/O(log n) operations.

## Persistence (it is in memory, but can survive restarts)
- **RDB:** periodic snapshots (compact, but you may lose the last few minutes).
- **AOF:** an append-only log of every write (safer, bigger files).
- Often both are enabled. For a pure cache, persistence can be off.

## Eviction: what happens when memory is full
```
maxmemory 2gb
maxmemory-policy allkeys-lru
```
- `allkeys-lru`: remove the least recently used keys (the best for caches).
- `volatile-lru`: only keys that have a TTL. `noeviction`: refuse writes (for data you can't lose).

## High availability
- **Replication:** a primary plus replicas.
- **Sentinel:** monitors and fails over automatically.
- **Cluster:** shards keys across many nodes (16,384 hash slots).

@@ 2 | TOPIC | Caching Patterns and Spring Cache | Cache-aside, write-through, TTL, invalidation, stampede | SHOULD
# Caching patterns and Spring Cache with Redis

## The patterns

| Pattern | How | Analogy |
|---|---|---|
| **Cache-aside (lazy loading)** | The app checks the cache; on a miss, reads the DB and stores the result | Check your fridge; if empty, go to the shop and stock the fridge |
| **Write-through** | Write to the cache **and** the DB together | Update both your notebook and the office register at once |
| **Write-behind** | Write to the cache, flush to the DB later | Risky for money: data can be lost |
| **Read-through** | The cache library loads from the DB itself on a miss | The fridge restocks itself |

**Cache-aside is the most common**, and it is what Spring's `@Cacheable` does.

## Manual cache-aside

```java
@Service
@RequiredArgsConstructor
public class PoService {
    private final StringRedisTemplate redis;
    private final PoRepository repo;
    private final ObjectMapper json;

    public PoDto get(Long id) throws JsonProcessingException {
        String key = "po:" + id;
        String cached = redis.opsForValue().get(key);                // 1. try the cache
        if (cached != null) return json.readValue(cached, PoDto.class);

        PoDto dto = PoDto.from(repo.findById(id).orElseThrow());     // 2. miss -> DB
        redis.opsForValue().set(key, json.writeValueAsString(dto),
                                Duration.ofMinutes(30));             // 3. store with a TTL
        return dto;
    }

    public void update(Long id, PoRequest req) {
        repo.save(...);
        redis.delete("po:" + id);                                    // 4. invalidate on write
    }
}
```
**Line by line**
- Step 1: a hit returns immediately. A miss returns `null`.
- Step 2: read the source of truth, the database.
- Step 3: `Duration.ofMinutes(30)`: a **TTL** means stale data fixes itself eventually.
- Step 4: on update, **delete** the key (don't update it). The next read reloads fresh data. Deleting avoids race conditions where two writers leave the cache holding the older value.

## The same thing with Spring Cache annotations

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
  cache:
    type: redis
    redis:
      time-to-live: 30m
```
```java
@SpringBootApplication
@EnableCaching
public class App {}

@Service
public class PoService {

    @Cacheable(value = "po", key = "#id", unless = "#result == null")
    public PoDto get(Long id) { return PoDto.from(repo.findById(id).orElseThrow()); }

    @CachePut(value = "po", key = "#id")              // always run, and refresh the cache with the result
    public PoDto update(Long id, PoRequest req) { ... return dto; }

    @CacheEvict(value = "po", key = "#id")
    public void delete(Long id) { repo.deleteById(id); }

    @CacheEvict(value = "po", allEntries = true)      // clear the whole "po" cache
    public void reloadAll() {}
}
```
**Line by line**
- `@EnableCaching`: turns on the caching proxy.
- `@Cacheable(value = "po", key = "#id")`: the Redis key becomes `po::7`. If it is present, the method is **not executed**.
- `unless = "#result == null"`: don't cache empty results.
- `@CachePut`: runs the method every time and stores the new value.
- `@CacheEvict`: removes the entry when the data changes.
- The cached objects must be serializable. Configure a JSON serializer (`GenericJackson2JsonRedisSerializer`) via a `RedisCacheConfiguration` bean so that the values in Redis are readable.
- Proxy rule again: calling a `@Cacheable` method **from the same class** skips the cache.

## Common problems

| Problem | Meaning | Fix |
|---|---|---|
| **Stale data** | The cache shows an old value | TTL + evict on write |
| **Cache stampede** | A hot key expires and 1,000 requests hit the DB at once | A lock so that only one request reloads (`@Cacheable(sync = true)`), early refresh |
| **Cache penetration** | Requests for IDs that don't exist always miss | Cache "null" briefly, a Bloom filter |
| **Avalanche** | Many keys expire at the same moment | Add random jitter to TTLs |

**What to cache:** read-heavy, rarely changing data (PO lists, loan products, configuration, dashboard aggregates).
**What not to cache:** live balances where you need exact, current values. Read those from the DB inside a transaction.

@@ 2 | TOPIC | Sessions, Rate Limiting and Distributed Locks | Spring Session, token buckets, SET NX locks, pub/sub | SHOULD
# Sessions, rate limiting and distributed locks

## 1. Shared HTTP sessions with Spring Session

**Problem:** with 3 app instances behind a load balancer, a session stored in instance A's memory is unknown to B, so the user is "logged out" randomly.
**Fix:** store sessions in Redis, where every instance can see them.

```xml
<dependency>
  <groupId>org.springframework.session</groupId>
  <artifactId>spring-session-data-redis</artifactId>
</dependency>
```
```yaml
spring:
  session:
    timeout: 30m
```
- That's it: `HttpSession` is now backed by Redis. Any instance can serve any request.
- **Analogy:** instead of each receptionist keeping visitor notes in their own drawer, all notes go in one shared filing cabinet.

## 2. Rate limiting (fixed window)

```java
public boolean allow(String user) {
    String key = "rate:" + user + ":" + (System.currentTimeMillis() / 60000);  // per-minute bucket
    Long count = redis.opsForValue().increment(key);                          // atomic INCR
    if (count != null && count == 1) redis.expire(key, Duration.ofMinutes(1)); // first hit sets the expiry
    return count != null && count <= 100;                                      // 100 requests/minute
}
```
**Line by line**
- The key includes the **current minute number**, so each minute gets a new counter.
- `increment`: atomic across all app instances.
- On the first request of the window we set a TTL, so old counters clean themselves up.
- More than 100 → reject with **429 Too Many Requests**. It protects login/OTP endpoints from brute force.
- More precise options: a sliding window (a sorted set of timestamps) or a token bucket (Bucket4j, the Spring Cloud Gateway rate limiter).

## 3. Distributed lock

**Problem:** the nightly "mark overdue" job runs on all 3 instances at once.

```java
public boolean tryLock(String name, String owner, Duration ttl) {
    Boolean ok = redis.opsForValue().setIfAbsent("lock:" + name, owner, ttl);   // SET key val NX PX ttl
    return Boolean.TRUE.equals(ok);
}

String me = UUID.randomUUID().toString();
if (tryLock("overdue-job", me, Duration.ofMinutes(10))) {
    try { runJob(); }
    finally { releaseIfOwner("overdue-job", me); }   // delete only if the value is still 'me' (Lua script)
}
```
**Line by line**
- `setIfAbsent` = Redis `SET NX`: it succeeds **only if the key doesn't exist**. Only one instance wins.
- `ttl`: if the winner crashes, the lock expires on its own, so we never get stuck forever.
- The `owner` value: when releasing, check that the lock is still ours, so we don't delete a lock another instance took after ours expired. Do the check-and-delete atomically with a Lua script.
- In practice, use **Redisson** (`RLock`) or **ShedLock** for scheduled jobs.
- **Analogy:** a "room occupied" sign with a built-in timer that flips back to "free" after 10 minutes, in case someone forgets.

## 4. Pub/Sub and Streams
- `PUBLISH`/`SUBSCRIBE`: fire-and-forget broadcast (e.g. "clear the local cache" to all instances). Offline subscribers miss messages.
- **Redis Streams:** a persistent log with consumer groups, like a lightweight Kafka.

## 5. Other uses
OTP storage (TTL), JWT denylist (store a revoked token id until its expiry), leaderboards (sorted sets), counting unique visitors (HyperLogLog).

@@ 2 | TOPIC | Phase 7 — Interview Questions | Quick Q&A revision for Redis | SHOULD
# Phase 7 interview questions

**Q. Why is Redis fast?**
A. It works in memory, runs a single-threaded event loop without locking, uses efficient data structures, and has a simple protocol.

**Q. Redis data types?**
A. String, Hash, List, Set, Sorted Set, plus Streams, Bitmaps, HyperLogLog and Geo.

**Q. Explain cache-aside.**
A. Read from the cache. On a miss, load from the DB and populate the cache with a TTL. On writes, update the DB and evict the cache entry.

**Q. How do you keep the cache consistent with the DB?**
A. Evict on write (rather than update), use TTLs as a safety net, keep the DB as the source of truth, and don't cache data that needs strict real-time accuracy.

**Q. What is a cache stampede and how do you prevent it?**
A. Many requests reload the same expired hot key at once. Prevent it with a single-flight lock (`@Cacheable(sync=true)`), early refresh and jittered TTLs.

**Q. How do you share sessions across multiple instances?**
A. Spring Session with Redis, or go stateless with JWT.

**Q. How do you implement a distributed lock with Redis?**
A. `SET key owner NX PX ttl`, release with an atomic owner check (Lua), or use Redisson/ShedLock.

**Q. RDB vs AOF?**
A. RDB takes periodic snapshots (compact, possible data loss). AOF logs every write (durable, larger). They can be combined.

**Q. What eviction policy for a cache?**
A. allkeys-lru (or allkeys-lfu).

**Q. Redis vs Memcached?**
A. Redis has rich data types, persistence, replication, pub/sub and Lua. Memcached is a simple multi-threaded string cache.
