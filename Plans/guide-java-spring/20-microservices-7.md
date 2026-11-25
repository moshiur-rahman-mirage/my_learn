@@ 3 | TOPIC | Practice — Phase 20 review: Microservices | Cheat sheet, key snippets, top 10 questions and rapid-fire flashcards for microservices with Spring Cloud | MUST
# Phase 20 review: Microservices

## Cheat sheet

| Idea | Remember |
|---|---|
| Monolith vs microservices | Start with a modular monolith; split for teams, scaling profile, release cadence or fault isolation |
| Distributed monolith | Shared DB or coordinated deploys: all costs, no benefits |
| Availability of a chain | Multiply: five 99.9% services in a row give about 99.5% |
| Strangler fig | Route one capability at a time to the new service via the gateway |
| Bounded context | One consistent model and language; best first guess for a service |
| Aggregate | Consistency unit, changed via its root, one aggregate per transaction |
| Anti-corruption layer | Translate other contexts' models at the edge |
| Spring Cloud version | 2025.0.x for Boot 3.5; import the `spring-cloud-dependencies` BOM |
| Gateway | Routes = URI + predicates + filters; `server-webflux` or `server-webmvc` starters |
| `lb://name` | Resolve via discovery and Spring Cloud LoadBalancer |
| Gateway retries | Only GET or idempotent requests |
| Eureka | Heartbeat 30 s, eviction 90 s, client cache 30 s, self-preservation, AP |
| Kubernetes discovery | Services and DNS plus readiness probes; Eureka usually unnecessary |
| Config Server | `spring.config.import=configserver:`; service-profile beats service beats application-profile beats application |
| Refresh | `@RefreshScope` proxies and JavaBean `@ConfigurationProperties`; DataSource needs restart; Bus for all instances |
| HTTP clients | HTTP Interfaces (`@HttpExchange`) over `RestClient` recommended; OpenFeign in maintenance |
| Timeouts | Always set connect and read timeouts; Feign default read is 60 s |
| Circuit breaker | CLOSED, OPEN, HALF_OPEN; failure and slow-call thresholds; minimum calls |
| Aspect order | Retry ( CircuitBreaker ( RateLimiter ( TimeLimiter ( Bulkhead ( call ) ) ) ) ) |
| Fallback placement | Outermost annotation; do not retry `CallNotPermittedException` |
| Bulkhead vs rate limiter | Concurrent calls vs calls per period |
| 2PC | Avoid across services: blocking, availability, no Kafka support |
| Saga | Local transactions + compensations; choreography or orchestration |
| Outbox | Event row in the same transaction; relay publishes; at-least-once |
| Idempotent consumer | Processed-message table or natural idempotency |
| Kafka ordering | Per partition; key by aggregate id |
| Consumer group | One consumer per partition; parallelism capped by partitions |
| Delivery | Commit after processing = at-least-once |
| Poison pill | Non-retryable exceptions straight to DLT (`<topic>.DLT`) |
| RabbitMQ | Exchange (direct, topic, fanout, headers) to queues via bindings; DLX; prefetch |
| Zero trust | Every service validates JWTs (issuer, audience, scopes) |
| Service identity | OAuth2 client credentials, cached tokens, mTLS via SSL bundles |

### Key code snippets
```xml
<!-- Spring Cloud BOM for Boot 3.5 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dependencies</artifactId>
    <version>2025.0.0</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

```yaml
# Gateway route with circuit breaker and safe retry
spring:
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: orders
              uri: lb://order-service
              predicates:
                - Path=/api/orders/**
              filters:
                - name: CircuitBreaker
                  args:
                    name: orders
                    fallbackUri: forward:/fallback/orders
                - name: Retry
                  args:
                    retries: 2
                    methods: GET
```

```java
// HTTP Interface client with load balancing and timeouts
// (imports from org.springframework.web.service.annotation and org.springframework.web.client omitted)
@HttpExchange("/api/inventory")
public interface InventoryClient {
    @PostExchange("/reservations")
    ReservationResponse reserve(@RequestHeader("Idempotency-Key") String key, @RequestBody ReserveRequest req);
}

@Bean
InventoryClient inventoryClient(@LoadBalanced RestClient.Builder builder) {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(Duration.ofSeconds(1));
    factory.setReadTimeout(Duration.ofSeconds(2));
    RestClient rc = builder.baseUrl("http://inventory-service").requestFactory(factory).build();
    return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(rc)).build().createClient(InventoryClient.class);
}
```

```java
// Resilience4j: fallback on the outermost aspect
@Retry(name = "inventory", fallbackMethod = "fallback")
@CircuitBreaker(name = "inventory")
@Bulkhead(name = "inventory")
public ReservationResponse reserve(String key, ReserveRequest req) {
    return client.reserve(key, req);
}

private ReservationResponse fallback(String key, ReserveRequest req, Exception e) {
    throw new InventoryUnavailableException("Inventory unavailable", e);
}
```

```java
// Transactional outbox: business row and event row commit together
@Transactional
public Order place(long customerId, List<Line> lines) {
    Order order = orders.save(Order.pending(customerId, lines));
    outbox.save(new OutboxEvent(UUID.randomUUID(), "Order", String.valueOf(order.getId()),
            "OrderPlaced", json.writeValueAsString(new OrderPlaced(order.getId(), lines))));
    return order;
}
```

```java
// Kafka: retries then dead-letter topic, poison pills skip retries
@Bean
DefaultErrorHandler errorHandler(KafkaTemplate<String, String> template) {
    var handler = new DefaultErrorHandler(new DeadLetterPublishingRecoverer(template),
                                          new FixedBackOff(1000L, 2));
    handler.addNotRetryableExceptions(JsonProcessingException.class);
    return handler;
}
```

```yaml
# Resource server: validate issuer and audience
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://keycloak:8180/realms/orderhub
          audiences: inventory-service
```

## The 10 most likely interview questions

1. **When would you choose microservices, and when not?** When several teams need independent releases, parts have very different scaling needs, or fault isolation matters, and the platform (CI/CD, observability) exists. Not for small teams or unclear domains: use a modular monolith.
2. **How do you find service boundaries?** Bounded contexts from DDD and event storming; high cohesion inside, little traffic across; aligned with team ownership; never one service per table.
3. **What does an API gateway do?** Single entry point: routing, auth, rate limiting, CORS, correlation ids, retries and fallbacks; no business logic.
4. **How does service discovery work?** Instances register and heartbeat to Eureka; clients cache the registry and load-balance with Spring Cloud LoadBalancer; on Kubernetes, Services and DNS do it.
5. **How do you make synchronous calls safe?** Timeouts, circuit breaker, bulkhead, retry with backoff only for idempotent calls, idempotency keys, error mapping, and fallbacks.
6. **Explain the circuit breaker states.** CLOSED records outcomes; OPEN rejects calls after thresholds; HALF_OPEN lets trial calls decide. Include slow-call thresholds and minimum call counts.
7. **How do you keep data consistent across services?** Sagas with compensating transactions, events published via the transactional outbox, idempotent consumers; avoid 2PC.
8. **How do Kafka partitions, keys and consumer groups work?** Key hashes to a partition (ordering per key); each partition goes to one consumer per group; groups read independently; parallelism capped by partition count.
9. **How do you handle duplicates and poison messages?** At-least-once delivery means idempotent consumers (processed ids); non-retryable failures go straight to a DLT, transient ones retry with backoff first.
10. **How do you secure service-to-service calls?** Zero trust: each service is a resource server validating issuer, audience and scopes; client credentials tokens for service identity; token relay for user identity; mTLS and network policies.

## Rapid-fire flashcards

Q: Which Spring Cloud release train matches Spring Boot 3.5?
A: Spring Cloud 2025.0.x (Northfields).

Q: What replaced Netflix Ribbon and Hystrix in Spring Cloud?
A: Spring Cloud LoadBalancer and Resilience4j.

Q: What is the default Eureka lease renewal interval and expiration?
A: Heartbeat every 30 seconds; eviction after 90 seconds without renewal.

Q: Which property loads remote config from Spring Cloud Config Server in Boot 3?
A: `spring.config.import=configserver:http://host:8888`.

Q: Which exception does an open Resilience4j circuit breaker throw?
A: `CallNotPermittedException`.

Q: What dependency is required for Resilience4j annotations to work in Spring Boot 3?
A: `spring-boot-starter-aop` (plus `resilience4j-spring-boot3`).

Q: What problem does the transactional outbox solve?
A: The dual write between the database and the message broker.

Q: What suffix does Spring Kafka's `DeadLetterPublishingRecoverer` use by default?
A: `.DLT`, for example `order-events.DLT`.

Q: Which RabbitMQ exchange type routes by wildcard patterns like `order.*`?
A: A topic exchange.

Q: Which OAuth2 grant represents a service rather than a user?
A: Client credentials.
