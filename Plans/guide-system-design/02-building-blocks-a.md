@@ 1 | MODULE | Phase 2 — Building Blocks | Components used to build scalable systems | MUST
# Phase 2: Building blocks

These are the boxes you draw on the whiteboard: DNS, CDN, load balancer, app servers, databases, caches, queues and the protocols between them. Know **what each does, when to use it, its failure mode, and the Spring Boot equivalent**.

@@ 2 | MODULE | Background Jobs | Work done outside the request path | SHOULD
Move slow or periodic work out of the user's request.

@@ 3 | TOPIC | Event-Driven | Jobs triggered by events such as a message or a file upload | SHOULD
# Event-driven background jobs

A job runs because **something happened** (order placed, file uploaded), not because a clock ticked.

```text
POST /orders -> save order -> publish OrderPlaced -> 202/201 to user
                                   |
                            consumer: send email, reserve stock, update analytics
```

## Spring Boot: in-process and broker-based

```java
// In-process (same JVM, lost on crash)
@Service class OrderService {
    private final ApplicationEventPublisher events;
    @Transactional public void place(Order o) { repo.save(o); events.publishEvent(new OrderPlaced(o.id())); }
}
@Component class EmailListener {
    @Async @TransactionalEventListener(phase = AFTER_COMMIT)
    void on(OrderPlaced e) { mail.send(e.id()); }
}

// Durable (survives crash): Kafka
@KafkaListener(topics = "orders.placed", groupId = "email")
void handle(OrderPlaced e) { mail.send(e.id()); }
```
- `AFTER_COMMIT` avoids sending an email for a transaction that later rolls back.
- Use a broker when the work must survive restarts or run in another service.

## Remember
- Handlers must be **idempotent** (events can be delivered twice).
- Dual-write trap: saving to DB and publishing to Kafka are two systems. Use the **Transactional Outbox** pattern for reliability.

Q: How do you guarantee the event is published if the DB commit succeeds?
A: Transactional outbox: write the event to an outbox table in the same DB transaction; a relay (Debezium or poller) publishes it to Kafka.

@@ 3 | TOPIC | Schedule Driven | Jobs triggered by time, such as cron | SHOULD
# Schedule-driven background jobs

Run at fixed times or intervals: nightly report, expire old sessions, retry failed payments.

```java
@Component
class ReportJob {
    @Scheduled(cron = "0 0 2 * * *")          // every day 02:00
    @SchedulerLock(name = "dailyReport", lockAtMostFor = "PT30M")   // ShedLock
    void run() { reportService.generate(); }
}
```
- `@EnableScheduling` runs the method on **every instance**. With 3 pods the job runs 3 times. Use **ShedLock** (DB/Redis lock), Quartz clustered mode, or a Kubernetes CronJob so only one runs.

## Options

| Tool | Use when |
|---|---|
| `@Scheduled` + ShedLock | Simple periodic jobs |
| Quartz (JDBC store) | Persistent, clustered, many jobs |
| Kubernetes CronJob | Job as its own container |
| Spring Batch | Large chunked processing with restart |

## Remember
- Make jobs idempotent and restartable; log start/end; alert on failure.

Q: A @Scheduled job fires twice in production. Why?
A: Multiple instances each run the scheduler. Add a distributed lock (ShedLock) or run it as a single CronJob.

@@ 3 | TOPIC | Returning Results | How a client learns the outcome of a background job | SHOULD
# Returning results from background jobs

The user already got a response; how do they learn the result?

| Approach | How | Use when |
|---|---|---|
| Polling | Client calls `GET /jobs/{id}` | Simple, a few seconds delay OK |
| Webhook/callback | Server calls the client's URL | Server-to-server |
| WebSocket / SSE | Server pushes | Live UI updates |
| Email/notification | Out-of-band | Long jobs (report ready) |

## Async request-reply with Spring Boot

```java
@PostMapping("/reports")
ResponseEntity<Void> start(@RequestBody ReportReq r) {
    String id = jobs.submit(r);
    return ResponseEntity.accepted().location(URI.create("/reports/" + id)).build(); // 202
}

@GetMapping("/reports/{id}")
ResponseEntity<?> status(@PathVariable String id) {
    Job j = jobs.get(id);
    return j.done() ? ResponseEntity.ok(j.result())
                    : ResponseEntity.ok(Map.of("status", "RUNNING"));
}
```
- `202 Accepted` + `Location` header is the standard contract.

Q: Polling vs WebSocket?
A: Polling is simple and stateless but wasteful. WebSocket/SSE gives instant push but needs sticky connections and more infra. Use SSE for one-way updates.

@@ 2 | MODULE | Domain Name System | Turning names into IP addresses | MUST
How the browser finds your server.

@@ 3 | TOPIC | DNS | Resolution flow, record types, TTL and DNS-based routing | MUST
# DNS

DNS maps `api.example.com` → an IP. It is the **phone book** of the internet.

## Resolution flow

```text
Browser cache -> OS cache -> Recursive resolver (ISP / 8.8.8.8)
   -> Root (.) -> TLD (.com) -> Authoritative NS for example.com -> IP
```
Results are cached for the record's **TTL**.

## Record types

| Record | Purpose |
|---|---|
| A / AAAA | Name → IPv4 / IPv6 |
| CNAME | Alias to another name (not allowed at apex) |
| MX | Mail servers |
| NS | Authoritative name servers |
| TXT | Verification, SPF |

## DNS as a design tool
- **Round-robin / weighted:** multiple A records spread load.
- **Latency/geo routing** (Route 53): send users to the nearest region.
- **Failover:** health check removes a dead IP.
- **Trade-off:** low TTL = fast failover but more lookups; high TTL = cached, slow to change.

## Java gotcha: JVM caches DNS

```java
// In a long-lived Spring Boot app behind a changing IP (cloud LB), stale DNS breaks calls.
java.security.Security.setProperty("networkaddress.cache.ttl", "30");
```
- By default the JVM may cache successful lookups for a long time (forever if a SecurityManager is present). Set a short TTL for services behind changing addresses.

Q: What happens when you type a URL?
A: DNS lookup → TCP handshake → TLS handshake → HTTP request → LB → app → response → render.

@@ 2 | MODULE | Content Delivery Networks | Serve static content from the edge | MUST
Move bytes closer to users.

@@ 3 | TOPIC | Push CDNs | You upload content to the CDN ahead of time | MUST
# Push CDN

You **push** new/changed files to the CDN yourself. Content sits on edge servers until you remove it.

```text
Deploy -> upload assets to CDN origin storage (S3/GCS) -> replicated to edges
```

## Pros / cons
- (+) Full control, no first-request miss, good for small sites with rare changes.
- (-) You manage uploads and invalidation; wastes storage for rarely used files.

## Use cases
- Versioned build artifacts: `app.3f9a1c.js` (long cache, new name on change).
- Big files released at a known time (game patch, video).

Q: Push vs pull, which for a React build?
A: Push (or deploy to S3 + CDN) with hashed filenames and `Cache-Control: max-age=31536000, immutable`; only `index.html` is short-lived.

@@ 3 | TOPIC | Pull CDNs | CDN fetches from your origin on first request | MUST
# Pull CDN

The CDN **pulls** a file from your origin on the first miss, caches it for the TTL, then serves it from the edge.

```text
User -> Edge (miss) -> Origin (Spring Boot / S3) -> Edge caches -> User
User -> Edge (hit) -> User
```

## Pros / cons
- (+) Easy: just point CDN at origin. Only popular content is cached.
- (-) First request is slow; origin must handle bursts after TTL expiry (use `stale-while-revalidate`).

## Spring Boot: control the cache with headers

```java
@GetMapping("/images/{id}")
ResponseEntity<byte[]> image(@PathVariable Long id) {
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePublic())
        .eTag(hashOf(id))
        .body(bytes(id));
}
```
- `Cache-Control` tells the CDN and browser how long to keep it; `ETag` allows cheap revalidation (304).

## Remember
- Never cache personalized responses with `public`. Use `Vary` / `private` / `no-store`.

Q: How do you purge a bad file?
A: CDN invalidation API or (better) versioned URLs so the new content has a new URL.

@@ 2 | MODULE | Load Balancers | Distributing traffic across instances | MUST
The front door of a scalable service.

@@ 3 | TOPIC | LB vs Reverse Proxy | Both sit in front of servers, with different primary goals | MUST
# Load balancer vs reverse proxy

| | Load balancer | Reverse proxy |
|---|---|---|
| Goal | Spread load across many servers | Single entry point for (one or many) servers |
| Features | Health checks, algorithms | TLS termination, caching, compression, auth, routing |
| Examples | AWS ALB/NLB, HAProxy | Nginx, Envoy, API gateway |

In practice **Nginx/Envoy/ALB do both**. The words overlap; explain the *functions*.

```text
Client -> Nginx (TLS, gzip, rate limit) -> [Spring Boot 1, 2, 3]
```

## Forwarded headers in Spring Boot

```yaml
server:
  forward-headers-strategy: framework   # honor X-Forwarded-For / -Proto behind the proxy
```
- Without this the app sees the proxy's IP and `http` instead of the client's IP and `https` (breaks redirects and rate limiting).

Q: Why TLS termination at the proxy?
A: Central certificate management and offloaded CPU; traffic inside the private network can be plain or re-encrypted (mTLS) as needed.

@@ 3 | TOPIC | Load Balancing Algorithms | Round robin, least connections, hash and more | MUST
# Load balancing algorithms

| Algorithm | How | Best for |
|---|---|---|
| Round robin | Next server in order | Equal servers, short uniform requests |
| Weighted round robin | More requests to bigger servers | Mixed hardware |
| Least connections | Fewest active connections | Long/variable requests |
| IP / consistent hash | Same key → same server | Sticky sessions, cache locality |
| Random (power of two choices) | Pick 2 at random, take the less loaded | Large clusters |

## Sticky sessions: avoid them
- They tie a user to one server; if it dies the session is lost and load becomes uneven.
- Prefer stateless apps (JWT) or shared session store (Spring Session + Redis).

## Client-side load balancing in Spring Cloud

```java
@Bean @LoadBalanced
RestClient.Builder restClient() { return RestClient.builder(); }

restClient.get().uri("http://inventory-service/stock/{id}", id).retrieve()...
```
- Spring Cloud LoadBalancer resolves `inventory-service` via service discovery and round-robins instances **inside the client**, with no central LB.

Q: Which algorithm for WebSocket servers?
A: Least connections (long-lived, uneven load), maybe consistent hashing if you need affinity.

@@ 3 | TOPIC | Layer 7 Load Balancing | Routes by HTTP content (path, header, cookie) | MUST
# Layer 7 (application) load balancing

Understands HTTP: it reads the URL, headers, cookies and body metadata, then decides.

```text
/api/orders/**   -> order-service pool
/api/payments/** -> payment-service pool
Host: admin.x.com -> admin pool
```

## Pros / cons
- (+) Smart routing, A/B and canary, TLS termination, WAF, rate limit, caching.
- (-) Slower and more CPU than L4 (it must parse requests, terminate TLS).

## Example: Spring Cloud Gateway as an L7 router

```yaml
spring.cloud.gateway.routes:
  - id: orders
    uri: lb://order-service
    predicates: [ Path=/api/orders/** ]
    filters: [ StripPrefix=0 ]
  - id: canary
    uri: lb://order-service-v2
    predicates: [ Path=/api/orders/**, Weight=orders, 10 ]   # 10% traffic
```

Q: When pick L7 over L4?
A: When routing depends on request content (microservices by path, canary, header-based tenant routing) or you need TLS/WAF.

@@ 3 | TOPIC | Layer 4 Load Balancing | Routes by IP and port (TCP/UDP) | MUST
# Layer 4 (transport) load balancing

Looks only at **IP + port**. It forwards TCP/UDP packets without reading HTTP.

## Pros / cons
- (+) Very fast, very high throughput, any protocol (databases, gRPC passthrough, MQTT).
- (-) No content-based routing; cannot cache or inspect.

## Example mapping
- AWS **NLB** = L4 (millions of connections/sec, static IP). AWS **ALB** = L7.
- Use L4 in front of Kafka brokers, database proxies, raw TCP services, or in front of L7 proxies.

```text
Client -> NLB (L4) -> Nginx/Envoy (L7, routing) -> Spring Boot pods
```

Q: L4 or L7 for a gRPC service?
A: L7 that supports HTTP/2 (ALB/Envoy) lets you balance per-request. A plain L4 balances per-connection, and one long HTTP/2 connection can pin all traffic to a single pod.

@@ 3 | TOPIC | Horizontal Scaling | Add more machines, make the app stateless | MUST
# Horizontal scaling

Add identical instances behind a load balancer instead of buying a bigger one.

## Requirements
1. **Stateless services:** no user state in memory/local disk.
2. **Shared state externalized:** DB, Redis, object storage.
3. **Idempotent/replay-safe** operations (retries happen).
4. **Auto-scaling** on CPU, RPS or queue depth.

## Spring Boot checklist

| Local (breaks) | Externalize to |
|---|---|
| `HttpSession` | Spring Session + Redis, or JWT |
| In-memory cache | Redis / Caffeine + invalidation |
| Local file uploads | S3 / MinIO |
| `@Scheduled` job | ShedLock / CronJob |
| In-memory ID counter | DB sequence / Snowflake |

```java
@EnableRedisHttpSession    // session now lives in Redis, any pod serves any user
```

## Kubernetes auto-scale (concept)

```yaml
kind: HorizontalPodAutoscaler
spec: { minReplicas: 3, maxReplicas: 20,
        metrics: [ { type: Resource, resource: { name: cpu, target: { type: Utilization, averageUtilization: 70 } } } ] }
```
- Min 3 across zones means one failure still leaves capacity.

Q: Can you scale the database horizontally like app servers?
A: Not easily. Reads: replicas. Writes: sharding. That is why data is the hard part.

@@ 2 | MODULE | Application Layer | Service boundaries and discovery | MUST
Split the app tier and let services find each other.

@@ 3 | TOPIC | Microservices | Small independent services owning their data | MUST
# Microservices

An app built as **small services**, each owning one business capability and its **own database**, deployed independently, communicating over the network (REST/gRPC/events).

## Monolith vs microservices

| | Monolith | Microservices |
|---|---|---|
| Deploy | One unit | Per service |
| Scale | Whole app | Per service |
| Complexity | In code | In network/ops |
| Data | One DB, ACID | DB per service, eventual consistency |
| Failure | One bug can kill all | Partial failure, needs resilience |

## Typical Spring Boot stack
- Spring Boot + Spring Cloud Gateway (edge), Eureka/Kubernetes DNS (discovery), Config Server, Resilience4j, Kafka, Micrometer + OpenTelemetry (tracing).

## Interview talking points
- Split by **business domain** (DDD bounded contexts), not by technical layer.
- No shared database between services (otherwise it is a distributed monolith).
- Cross-service transactions → **Saga** (choreography or orchestration), not 2PC.
- Pain: network failure, latency, versioning, debugging → need tracing, timeouts, circuit breakers.

```java
@FeignClient(name = "payment-service", fallback = PaymentFallback.class)
interface PaymentClient { @PostMapping("/payments") PaymentResult pay(PaymentReq r); }
```

Q: When NOT to use microservices?
A: Small team, unclear domain, early product. Start with a modular monolith; extract services when a module needs independent scale/deploy.

@@ 3 | TOPIC | Service Discovery | How services find healthy instances | MUST
# Service discovery

IPs change (autoscaling, restarts). Instead of hard-coding, services **register** and clients **look up** by name.

## Two models

| Model | How | Example |
|---|---|---|
| Client-side | Client asks registry, picks an instance | Eureka + Spring Cloud LoadBalancer |
| Server-side | Client calls a stable name; platform routes | Kubernetes Service, AWS ALB |

```text
payment-service (3 pods) --register/heartbeat--> Registry
order-service asks "payment-service" -> [10.0.1.4, 10.0.2.9, 10.0.3.7]
```

## Spring Boot with Eureka

```java
@SpringBootApplication @EnableDiscoveryClient
class PaymentApp {}
```
```yaml
eureka.client.service-url.defaultZone: http://eureka:8761/eureka
```
- On Kubernetes you usually skip Eureka: a `Service` gives a stable DNS name (`http://payment-service`) and kube-proxy balances.

## Remember
- Registries need **health checks + TTL** to evict dead nodes, and are themselves clustered (Consul/etcd/ZooKeeper are CP).

Q: Eureka is AP. What does that imply?
A: During partitions it keeps serving possibly stale registrations (self-preservation mode), so clients must tolerate calling a dead instance: use timeouts and retries.
