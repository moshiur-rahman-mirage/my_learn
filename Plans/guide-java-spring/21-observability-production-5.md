@@ 3 | TOPIC | Practice — Coding drill: Production incidents | Ten realistic OrderHub incidents: diagnose with logs, metrics, traces and dumps, then fix in code or configuration | MUST
# Coding drill: Production incidents

Ten incidents of the kind senior interviews love ("it is 3 a.m., what do you do?"). For each: read the symptoms, decide which signal you would check first and what you expect to see, then write the fix. The method is always the same: **detect** (alert on a symptom), **locate** (metrics, then traces to find the service and operation), **explain** (logs, dumps), **mitigate** (rollback, scale, feature flag), **fix** (code or config), **prevent** (test, alert, guardrail).

## 1. Pods restart with exit code 137, no OutOfMemoryError in the logs
order-service pods on Kubernetes restart every few hours. `kubectl describe pod` shows `Last State: Terminated, Reason: OOMKilled, Exit Code: 137`. Container limit 1 GiB; JVM started with `-Xmx1g`.

Hint: the kernel killed the container, not the JVM; heap is only part of process memory.

<details><summary>Solution</summary>

Diagnosis: `-Xmx1g` lets the heap alone use the whole limit; metaspace, thread stacks (200 Tomcat threads), code cache, direct buffers (Kafka, Netty) and GC structures come on top, so total RSS crosses 1 GiB and the OOM killer sends SIGKILL. Check `container_memory_working_set_bytes` against the limit and `jvm_memory_used_bytes{area="nonheap"}`; enable `-XX:NativeMemoryTracking=summary` and run `jcmd 1 VM.native_memory summary` in a pod.

```yaml
# k8s container fix
          env:
            - name: JAVA_TOOL_OPTIONS
              value: >-
                -XX:MaxRAMPercentage=70
                -XX:MaxMetaspaceSize=192m
                -XX:ReservedCodeCacheSize=128m
                -Xss512k
                -XX:+ExitOnOutOfMemoryError
          resources:
            requests: { memory: "1Gi", cpu: "500m" }
            limits:   { memory: "1Gi" }
```

```yaml
# application.yml: fewer threads if the service is I/O-bound
server:
  tomcat:
    threads:
      max: 100
spring:
  threads:
    virtual:
      enabled: true          # Java 21: request handling on virtual threads, tiny stacks
```
Heap is now about 700 MiB with explicit caps for the rest. Prevention: alert when working set exceeds 90% of the limit, and load-test with production limits.
</details>

## 2. Requests hang, then 500 "Connection is not available, request timed out after 30000ms"
Under a traffic peak, order-service returns 500s after exactly 30 seconds. Metrics: `hikaricp_connections_active` flat at 10 (pool max), `hikaricp_connections_pending` 150. Traces show long spans for `http post inventory-service` inside `orderhub.checkout`.

Hint: what holds a connection while waiting for the network?

<details><summary>Solution</summary>

Diagnosis: `@Transactional` on the checkout method opens a connection at the start and holds it during the 2-second inventory HTTP call. With 10 connections, throughput is capped at about 5 checkouts per second; everything else waits `connectionTimeout` (30 s) and fails. Hikari leak detection confirms long holds.

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      connection-timeout: 3000          # fail fast instead of 30 s queues
      leak-detection-threshold: 2000    # logs a stack trace when a connection is held > 2 s
```

```java
package com.orderhub.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class CheckoutFix {
    private final OrderRepository orders;
    private final ResilientInventory inventory;
    private final TransactionTemplate tx;

    public CheckoutFix(OrderRepository orders, ResilientInventory inventory, TransactionTemplate tx) {
        this.orders = orders;
        this.inventory = inventory;
        this.tx = tx;
    }

    // No @Transactional here: the network call happens with no connection held
    public Order checkout(PlaceOrderCommand cmd) {
        Order order = tx.execute(s -> orders.save(Order.pending(cmd)));       // short tx 1
        inventory.reserve(order.getId(), cmd.lines());                         // network, no connection
        return tx.execute(s -> {                                               // short tx 2
            Order o = orders.findById(order.getId()).orElseThrow();
            o.confirm();
            return o;
        });
    }
}
```
Also check `spring.jpa.open-in-view`: if true (the default), the session and its connection stay open for the whole request when lazy loading occurs; set it to false. Prevention: alert on `hikaricp_connections_pending > 0` for 2 minutes.
</details>

## 3. Everything slows down when inventory is slow
Inventory's p99 went from 100 ms to 8 s during its database maintenance. Within minutes order-service and the gateway time out on all endpoints, including `/api/products` which never calls inventory.

Hint: look at Tomcat thread metrics and a thread dump.

<details><summary>Solution</summary>

Diagnosis: `tomcat_threads_busy_threads` hits the maximum; `GET /actuator/threaddump` shows 200 threads in `SocketInputStream.read` inside the inventory client. No read timeout and no bulkhead, so the slow dependency consumed every request thread and unrelated endpoints starved (cascading failure).

```java
package com.orderhub.order.client;

import java.time.Duration;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
class TimeoutFixConfig {
    @Bean
    @LoadBalanced
    RestClient.Builder lbBuilder(RestClientBuilderConfigurer configurer) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(500));
        factory.setReadTimeout(Duration.ofSeconds(1));       // inventory p99 is 100 ms normally
        return configurer.configure(RestClient.builder()).requestFactory(factory);
    }
}
```

```yaml
resilience4j:
  bulkhead:
    instances:
      inventory:
        max-concurrent-calls: 25       # at most 25 of 200 threads may wait on inventory
        max-wait-duration: 0
  circuitbreaker:
    instances:
      inventory:
        slow-call-duration-threshold: 800ms
        slow-call-rate-threshold: 50
        minimum-number-of-calls: 20
        wait-duration-in-open-state: 15s
```
With `@Bulkhead` and `@CircuitBreaker` on the inventory gateway (Phase 20), slow inventory now affects only the checkout path, which fails fast with 503, while products keep working.
</details>

## 4. Heap grows until the pod dies every two days
`jvm_memory_used_bytes{area="heap"}` after each full GC climbs steadily (a sawtooth whose minimum rises). GC time increases; finally the JVM exits with `OutOfMemoryError: Java heap space` thanks to `ExitOnOutOfMemoryError`.

Hint: take a heap dump and look at the dominator tree.

<details><summary>Solution</summary>

Diagnosis: a heap dump (`jcmd 1 GC.heap_dump /tmp/heap.hprof` in the pod, or `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp`) opened in Eclipse MAT shows a `ConcurrentHashMap` in `ProductPriceCache` retaining 1.4 GB: an unbounded "cache" keyed by product id plus currency plus customer group, never evicted.

```java
package com.orderhub.order.pricing;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class ProductPriceCache {
    record Key(long productId, String currency, String customerGroup) { }

    private final Cache<Key, BigDecimal> cache;

    public ProductPriceCache(MeterRegistry registry) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(50_000)                       // bounded
                .expireAfterWrite(Duration.ofMinutes(10))  // stale prices age out
                .recordStats()
                .build();
        CaffeineCacheMetrics.monitor(registry, cache, "productPrices");   // size, hits, evictions
    }

    public BigDecimal price(long productId, String currency, String group, Function<Key, BigDecimal> loader) {
        return cache.get(new Key(productId, currency, group), loader);
    }
}
```
Never expose `/actuator/heapdump` publicly (it contains personal data); take dumps through `kubectl exec` with care and delete them afterwards. Prevention: every cache must be bounded and have metrics.
</details>

## 5. Async logs lose the trace id
In Grafana, the logs of `@Async` receipt sending and of `CompletableFuture` price lookups have empty `traceId` fields, so support cannot follow requests.

Hint: thread locals do not follow work onto other threads.

<details><summary>Solution</summary>

```java
package com.orderhub.order.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Configuration
class PropagationConfig {
    // Name applicationTaskExecutor: used by @Async and replaces Boot's default executor
    @Bean
    ThreadPoolTaskExecutor applicationTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return builder.threadNamePrefix("app-")
                .taskDecorator(new ContextPropagatingTaskDecorator())
                .build();
    }
}

@Service
class PriceLookups {
    private final Executor executor;

    PriceLookups(ThreadPoolTaskExecutor applicationTaskExecutor) { this.executor = applicationTaskExecutor; }

    CompletableFuture<Long> priceAsync(long productId) {
        // pass the decorated executor; the common ForkJoinPool would lose the context
        return CompletableFuture.supplyAsync(() -> 1250L, executor);
    }
}
```
`ContextPropagatingTaskDecorator` captures MDC and the current observation (span) when the task is submitted and restores them on the worker thread. Also check Kafka (`observation-enabled`) and clients built without Boot's builders.
</details>

## 6. Prometheus runs out of memory after a release
Right after release 1.4.30, Prometheus memory doubles and dashboards time out. The release added a custom metric for order lookups.

Hint: count series per metric name.

<details><summary>Solution</summary>

PromQL `topk(5, count by (__name__) ({__name__=~".+"}))` shows `orders_lookup_seconds_bucket` with 2.3 million series. The code tagged the timer with the raw path:

```java
// the bug
Timer.builder("orders.lookup").tag("path", request.getRequestURI()).register(registry);  // /api/orders/918273
```

Fix in code plus a safety net:

```java
package com.orderhub.order.metrics;

import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CardinalityGuards {
    // Drop any "path" tag and cap distinct "uri" values as a last line of defence
    @Bean
    MeterFilter dropPathTag() {
        return MeterFilter.ignoreTags("path");
    }

    @Bean
    MeterFilter capUriTag() {
        return MeterFilter.maximumAllowableTags("http.server.requests", "uri", 200, MeterFilter.deny());
    }
}
```
Use the route template (`/api/orders/{id}`), which Spring's own `http.server.requests` already does. Put order ids in traces and logs, not metrics.
</details>

## 7. A burst of 502s during every deployment
Each rollout of order-service produces 200 to 400 `502 Bad Gateway` at the ingress over about 5 seconds, although `server.shutdown=graceful` is set.

Hint: compare when the pod stops accepting connections with when the ingress stops sending them.

<details><summary>Solution</summary>

Diagnosis: endpoint removal and SIGTERM happen in parallel. Tomcat stops accepting immediately on SIGTERM, while ingress-nginx and kube-proxy still route to the pod for a few seconds.

```yaml
    spec:
      terminationGracePeriodSeconds: 60
      containers:
        - name: order-service
          lifecycle:
            preStop:
              sleep:
                seconds: 10          # keep serving while routing converges
          readinessProbe:
            httpGet: { path: /actuator/health/readiness, port: 9081 }
            periodSeconds: 5
  strategy:
    rollingUpdate:
      maxUnavailable: 0
      maxSurge: 1
```

```yaml
# application.yml
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s   # 10 + 30 + cleanup < 60 grace period
```
Verify with a load test (`k6` or `hey`) running during `kubectl rollout restart`: zero non-2xx responses expected.
</details>

## 8. All pods restart together during a database blip
A 40-second PostgreSQL failover caused every order-service pod to restart, and the service was down for 4 minutes instead of 40 seconds.

Hint: what does the liveness probe check?

<details><summary>Solution</summary>

Diagnosis: the liveness probe pointed at `/actuator/health`, which includes the `db` indicator. The database outage failed liveness, Kubernetes restarted all pods, and cold starts plus startup probes extended the outage.

```yaml
# application.yml
management:
  endpoint:
    health:
      probes:
        enabled: true
      group:
        liveness:
          include: livenessState              # internal state only
        readiness:
          include: readinessState,db          # DB affects traffic, never restarts
```

```yaml
          livenessProbe:
            httpGet: { path: /actuator/health/liveness, port: 9081 }
            periodSeconds: 10
            failureThreshold: 3
          readinessProbe:
            httpGet: { path: /actuator/health/readiness, port: 9081 }
            periodSeconds: 5
            failureThreshold: 2
```
Also tune Hikari so the app recovers by itself after failover (`spring.datasource.hikari.max-lifetime` below the database's idle cutoff, short `connection-timeout`) and keep retries with backoff in the Kafka consumers.
</details>

## 9. Kafka consumer lag grows and records are processed several times
notification-service lag on `order-events` climbs to 500,000. Logs show repeated "Revoking previously assigned partitions" and the same order ids being emailed several times.

Hint: compare batch processing time with `max.poll.interval.ms`.

<details><summary>Solution</summary>

Diagnosis: each poll returns 500 records; each email takes about 800 ms (slow SMTP), so a batch takes 400 s, longer than `max.poll.interval.ms` (300 s). The broker evicts the consumer, rebalances, and the uncommitted batch is redelivered, forever.

```yaml
spring:
  kafka:
    consumer:
      properties:
        max.poll.records: 50              # 50 x 0.8 s = 40 s per poll
        max.poll.interval.ms: 300000
    listener:
      ack-mode: record                    # commit progress after each record
      concurrency: 6                      # one thread per partition
```

```java
package com.orderhub.notification;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class IdempotentEmailListener {
    private final JdbcClient jdbc;
    private final EmailSender email;

    IdempotentEmailListener(JdbcClient jdbc, EmailSender email) {
        this.jdbc = jdbc;
        this.email = email;
    }

    @KafkaListener(topics = "order-events", groupId = "notification-service")
    void on(ConsumerRecord<String, String> record) {
        String eventId = new String(record.headers().lastHeader("eventId").value());
        try {
            jdbc.sql("insert into processed_message(consumer, message_id) values ('email', ?)")
                .param(eventId).update();
        } catch (DuplicateKeyException duplicate) {
            return;                                   // redelivered after a rebalance
        }
        email.send(Long.parseLong(record.key()), "Order update");
    }
}
```
Longer term, send emails asynchronously from a bounded pool or batch them with the SMTP provider's API, and alert on consumer lag growth rate.
</details>

## 10. Need DEBUG logs from one pod without a redeploy
Customers report intermittent wrong totals for orders with discounts. You suspect `com.orderhub.order.pricing` but INFO logs show nothing. You need DEBUG for 10 minutes, only for that package, with minimal risk.

Hint: Actuator can change levels at runtime; find a failing request by its trace id.

<details><summary>Solution</summary>

```bash
# 1. Find a failing request: the customer gave the X-Trace-Id from the response
#    Grafana Tempo or Jaeger: search trace 4bf92f3577b34da6a3ce929d0e0e4736 -> pod order-service-6c8d7f9b5d-tx5lm

# 2. Port-forward the management port of that pod (not exposed publicly)
kubectl port-forward pod/order-service-6c8d7f9b5d-tx5lm 9081:9081 -n orderhub

# 3. Raise the level for one package only
curl -u ops:"$OPS_PASSWORD" -X POST localhost:9081/actuator/loggers/com.orderhub.order.pricing \
     -H 'Content-Type: application/json' -d '{"configuredLevel":"DEBUG"}'

# 4. Reproduce, then query logs: traceId plus logger
#    {app="order-service"} | json | log_logger=~"com.orderhub.order.pricing.*" | log_level="DEBUG"

# 5. Reset to the configured default
curl -u ops:"$OPS_PASSWORD" -X POST localhost:9081/actuator/loggers/com.orderhub.order.pricing \
     -H 'Content-Type: application/json' -d '{"configuredLevel":null}'
```

```java
package com.orderhub.order.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class DiscountCalculator {
    private static final Logger log = LoggerFactory.getLogger(DiscountCalculator.class);

    BigDecimal apply(BigDecimal total, BigDecimal percent) {
        BigDecimal discount = total.multiply(percent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
        log.atDebug()
           .addKeyValue("total", total)
           .addKeyValue("percent", percent)
           .addKeyValue("discount", discount)
           .log("Discount applied");                  // structured fields, cheap when DEBUG is off
        return total.subtract(discount);
    }
}
```
The DEBUG output revealed a `double`-based percentage parsed upstream (`0.1 + 0.2` style rounding), fixed by using `BigDecimal` end to end. Level changes via `/actuator/loggers` affect only that pod and vanish on restart, which limits risk; reset explicitly anyway.
</details>

@@ 3 | TOPIC | Practice — Phase 21 review: Observability and Production | Cheat sheet, key snippets, top 10 questions and rapid-fire flashcards for observability and production readiness | MUST
# Phase 21 review: Observability and Production

## Cheat sheet

| Idea | Remember |
|---|---|
| Three pillars | Logs (what happened), metrics (how much, how fast), traces (where time went); link them by trace id |
| Actuator exposure | Only `health` over HTTP by default; explicit include list; separate management port |
| Actuator access | Boot 3.4+: `management.endpoint.<id>.access` none, read-only, unrestricted |
| Dangerous endpoints | heapdump, env, configprops, threaddump, shutdown |
| Health groups | liveness = internal only; readiness = can include DB and critical dependencies |
| Custom health | `HealthIndicator` bean; cheap checks with timeouts |
| SLF4J | Facade; Logback default; parameterized `{}`; exception as last argument |
| MDC | Thread-local context; clear in `finally`; propagate with `ContextPropagatingTaskDecorator` |
| Structured logs | Boot 3.4+: `logging.structured.format.console: ecs` (or logstash, gelf) |
| Runtime log levels | `POST /actuator/loggers/{name}` |
| Micrometer meters | Counter, Gauge, Timer, DistributionSummary, LongTaskTimer |
| Percentiles | Publish histograms; aggregate with `histogram_quantile`; never average p99s |
| Cardinality | Bounded tag values only; ids go to logs and traces |
| RED and USE | Rate, errors, duration for services; utilization, saturation, errors for resources |
| Tracing stack | Micrometer Tracing + OTel bridge + OTLP exporter; Sleuth is dead |
| Propagation | W3C `traceparent` default; Kafka needs `observation-enabled` |
| Sampling | `management.tracing.sampling.probability` default 0.1; tail sampling in the collector |
| Graceful shutdown | Default since Boot 3.4; `timeout-per-shutdown-phase` 30 s |
| preStop | Sleep 5 to 15 s to beat the endpoint-removal race |
| Grace period | Above preStop plus drain plus cleanup, else SIGKILL (137) |
| Startup probe | Protects slow starts from liveness |
| Docker layers | `jarmode=tools extract --layers`: dependencies, loader, snapshot deps, application |
| Entrypoint | Exec form so Java gets SIGTERM |
| JVM memory | `MaxRAMPercentage` around 70 to 75; OOMKilled 137 means total process memory over the limit |
| Faster startup | CDS (Boot 3.3+), `Java 25+` AOT cache, native image |
| Kubernetes config | ConfigMap env vars, Secrets as files via `configtree:` |
| Resources | Memory request = limit; avoid tight CPU limits (throttling) |
| Rollout | `maxUnavailable: 0`, `maxSurge: 1`, PDB, `rollout undo` |
| CI/CD | Build once, tag git SHA, scan, GitOps, canary with metric analysis |
| Migrations | Expand and contract, backward compatible |

### Key code snippets
```yaml
# Production actuator, probes, tracing and JSON logs
management:
  server:
    port: 9081
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,loggers
  endpoint:
    health:
      probes:
        enabled: true
      group:
        readiness:
          include: readinessState,db
  tracing:
    sampling:
      probability: 0.1
  otlp:
    tracing:
      endpoint: http://otel-collector:4318/v1/traces
logging:
  structured:
    format:
      console: ecs
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

```java
// Custom health indicator
@Component("inventory")
class InventoryHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        try {
            client.ping();
            return Health.up().build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
```

```java
// MDC done right
try (MDC.MDCCloseable c = MDC.putCloseable("orderId", String.valueOf(orderId))) {
    log.info("Placing order with {} lines", lines);
} catch (IllegalStateException e) {
    log.error("Placing order {} failed", orderId, e);   // exception last
}
```

```java
// Business metrics with bounded tags
Counter.builder("orders.placed").tag("channel", channel).register(registry).increment();
Timer.builder("orders.placement").publishPercentileHistogram().register(registry).record(() -> place(cmd));
Gauge.builder("orders.pending", repo, r -> r.countByStatus(PENDING)).register(registry);
```

```java
// Custom span and metric in one with the Observation API
Observation.createNotStarted("orderhub.pricing", observationRegistry)
        .lowCardinalityKeyValue("pricing.strategy", "standard")
        .highCardinalityKeyValue("order.id", String.valueOf(orderId))
        .observe(() -> calculate(orderId));
```

```text
# Dockerfile essentials
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY extracted/dependencies/ ./
COPY extracted/spring-boot-loader/ ./
COPY extracted/snapshot-dependencies/ ./
COPY extracted/application/ ./
USER spring:spring
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java", "-jar", "application.jar"]
```

```yaml
# Kubernetes container essentials
          resources:
            requests: { cpu: "500m", memory: "768Mi" }
            limits:   { memory: "768Mi" }
          lifecycle:
            preStop:
              sleep: { seconds: 10 }
          startupProbe:
            httpGet: { path: /actuator/health/liveness, port: 9081 }
            failureThreshold: 24
            periodSeconds: 5
          livenessProbe:
            httpGet: { path: /actuator/health/liveness, port: 9081 }
          readinessProbe:
            httpGet: { path: /actuator/health/readiness, port: 9081 }
```

## The 10 most likely interview questions

1. **Liveness vs readiness?** Liveness restarts a broken container and checks only internal state; readiness removes a pod from traffic and may check critical dependencies like the database.
2. **How do you trace a request across microservices?** Micrometer Tracing with the OpenTelemetry bridge propagates W3C `traceparent` over HTTP and Kafka, exports spans via OTLP to Jaeger or Tempo, and puts the trace id into every log line through MDC.
3. **What is MDC and what are its pitfalls?** A thread-local logging context; it must be cleared after each request and copied to async threads.
4. **What would you monitor for a Spring Boot service?** RED per endpoint, JVM heap and GC, pools (Hikari pending, executor queues), downstream latency and circuit breakers, Kafka lag, business metrics; alert on symptoms and SLOs.
5. **What is high cardinality?** Too many distinct tag values creating too many time series; keep tags bounded.
6. **Why histograms for latency?** Percentiles from buckets can be aggregated across instances; averages hide tails and per-instance percentiles cannot be combined.
7. **How do you achieve zero-downtime deployments?** Graceful shutdown, preStop sleep, readiness probes, `maxUnavailable: 0`, backward-compatible APIs and migrations, canary with automatic rollback.
8. **How do you build a good Docker image for Spring Boot?** Multi-stage, layered extraction, JRE base, non-root, exec-form entrypoint, `MaxRAMPercentage`, immutable tags, scanning.
9. **A pod is OOMKilled without an OutOfMemoryError. Why?** Total process memory exceeded the container limit; size heap as a percentage and account for non-heap memory.
10. **How do you secure Actuator?** Explicit exposure, separate management port, authentication for sensitive endpoints, heapdump disabled, health details only when authorized.

## Rapid-fire flashcards

Q: Which Actuator endpoints are exposed over HTTP by default in Spring Boot 3?
A: Only `health`.

Q: Which property switches Spring Boot 3.4+ console logs to JSON?
A: `logging.structured.format.console` set to `ecs`, `logstash` or `gelf`.

Q: Which decorator copies MDC and trace context to `@Async` threads?
A: `ContextPropagatingTaskDecorator`.

Q: What is the default trace sampling probability in Spring Boot?
A: 0.1 (10%).

Q: Which header carries trace context by default in Spring Boot 3?
A: `traceparent` (W3C Trace Context).

Q: What does exit code 137 mean for a container?
A: It was killed with SIGKILL: OOM-killed or past the termination grace period.

Q: Which property sets the graceful shutdown wait time per lifecycle phase?
A: `spring.lifecycle.timeout-per-shutdown-phase` (default 30 s).

Q: Which jar mode extracts Spring Boot 3.3+ jars into layers?
A: `-Djarmode=tools` with `extract --layers`.

Q: How does Spring Boot import Kubernetes Secrets mounted as files?
A: `spring.config.import=configtree:/path/`.

Q: What is the expand and contract pattern?
A: Making schema changes in backward-compatible steps: add and backfill, switch code, remove old structures in a later release.
