@@ 1 | MODULE | Phase 5 — APIs, Integration and Microservices | REST design, JSON/XML/SOAP, microservices patterns, messaging | SHOULD
# Phase 5: APIs, integration and microservices

PKSF's systems talk to POs, banks and government services. Expect questions on **REST design, SOAP/XML integration, microservice patterns and resilience**.

@@ 2 | MODULE | REST and Data Formats | REST API design, JSON with Jackson, XML, SOAP | SHOULD
How to design clean APIs and exchange data in JSON, XML and SOAP.

@@ 3 | TOPIC | REST API Design | Resources, URLs, status codes, versioning, pagination, idempotency | MUST
# REST API design

**REST** (Representational State Transfer) is a style: everything is a **resource** (a noun) with a URL, and you act on it using **HTTP methods** (verbs).

**Analogy:** a library. Books are resources (`/books/42`). You don't shout "GIVE ME BOOK"; you use standard actions: borrow (GET), donate (POST), replace (PUT), remove (DELETE).

## URL design

| Action | Method + URL | Success code |
|---|---|---|
| List members | `GET /api/v1/members?page=0&size=20&status=ACTIVE` | 200 |
| Get one | `GET /api/v1/members/42` | 200 / 404 |
| Create | `POST /api/v1/members` | 201 + Location |
| Replace | `PUT /api/v1/members/42` | 200 |
| Partial update | `PATCH /api/v1/members/42` | 200 |
| Delete | `DELETE /api/v1/members/42` | 204 |
| Sub-resource | `GET /api/v1/members/42/loans` | 200 |
| Action (not CRUD) | `POST /api/v1/loans/7/approve` | 200 |

**Rules**
- Use **nouns, plural**: `/members`, not `/getMember`.
- Lowercase, with hyphens: `/loan-products`.
- Filter, sort and page with query params: `?status=ACTIVE&sort=name,asc`.
- Version the API: `/api/v1/...` (or a header). Never break existing clients.
- Be **stateless**: each request carries everything needed (e.g. a JWT). The server keeps no session between calls, which makes horizontal scaling easy.

## Consistent responses

```json
// GET /api/v1/members?page=0&size=2
{
  "content": [ { "id": 1, "name": "Rahim" }, { "id": 2, "name": "Karim" } ],
  "page": 0, "size": 2, "totalElements": 57, "totalPages": 29
}

// 400 error (ProblemDetail, RFC 7807)
{ "type": "about:blank", "title": "Validation failed", "status": 400,
  "detail": "phone: Invalid BD mobile", "instance": "/api/v1/members" }
```
- Clients always know where to find the data and how errors look.

## Idempotency key for payments

```http
POST /api/v1/repayments
Idempotency-Key: 6f1c2a90-3b1e-4d0a-9a77-0c2b4d1e9f11
Content-Type: application/json

{ "loanId": 7, "amount": 500.00 }
```
```java
@PostMapping("/repayments")
public ResponseEntity<RepaymentResponse> repay(@RequestHeader("Idempotency-Key") String key,
                                               @Valid @RequestBody RepaymentRequest req) {
    return idempotencyStore.find(key)                      // seen this key before?
            .map(ResponseEntity::ok)                       // yes: return the SAME result again
            .orElseGet(() -> {
                RepaymentResponse r = service.repay(req);  // no: process it once
                idempotencyStore.save(key, r);
                return ResponseEntity.status(201).body(r);
            });
}
```
**Line by line**
- The client generates a unique key per payment attempt. If the network drops and it **retries**, it sends the same key.
- The server remembers keys (DB table with a unique constraint, or Redis with a TTL). A repeated key returns the stored response instead of charging twice.
- **Analogy:** a receipt number. If you show the cashier the same receipt twice, they say "already paid".

## HATEOAS, caching, rate limiting (know the words)
- **HATEOAS:** responses include links to the next possible actions.
- **ETag / If-None-Match → 304 Not Modified:** the client reuses its cached copy.
- **Rate limiting → 429 Too Many Requests** with a `Retry-After` header.

@@ 3 | TOPIC | JSON with Jackson | Serialization, annotations, dates, BigDecimal | SHOULD
# JSON with Jackson

Spring Boot uses **Jackson** to convert Java objects ↔ JSON automatically.
- **Serialization:** Java object → JSON (the response).
- **Deserialization:** JSON → Java object (the `@RequestBody`).

**Analogy:** Jackson is a translator who converts Bangla letters (Java objects) into English letters (JSON) and back.

```java
@JsonInclude(JsonInclude.Include.NON_NULL)          // skip null fields in the output
@JsonIgnoreProperties(ignoreUnknown = true)         // don't fail on extra JSON fields
public record LoanDto(
        Long id,
        @JsonProperty("loan_no") String loanNo,     // JSON name differs from the Java name
        BigDecimal amount,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate disbursedOn,
        @JsonIgnore String internalNote) {}          // never sent or read

// Manual use
ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
String json = mapper.writeValueAsString(dto);                      // object -> JSON
LoanDto back = mapper.readValue(json, LoanDto.class);              // JSON -> object
List<LoanDto> list = mapper.readValue(arrayJson, new TypeReference<List<LoanDto>>() {});
```

**Line by line**
- `@JsonInclude(NON_NULL)`: `"note": null` is left out, giving a smaller response.
- `ignoreUnknown = true`: if a partner sends extra fields, don't crash. It makes the API **tolerant** of change.
- `@JsonProperty("loan_no")`: maps snake_case JSON to camelCase Java.
- `@JsonFormat(pattern = ...)`: the date format. Spring Boot already registers `JavaTimeModule`, and by default writes ISO dates (`2025-11-15`).
- `@JsonIgnore`: hides sensitive or internal fields.
- `TypeReference<List<LoanDto>>`: needed for generic types because of type erasure. Java can't know the element type otherwise.

**Global settings** (application.yml):
```yaml
spring:
  jackson:
    default-property-inclusion: non_null
    serialization:
      write-dates-as-timestamps: false
    deserialization:
      fail-on-unknown-properties: false
```

**BigDecimal tip:** some JavaScript clients lose precision with huge numbers. For money, you can send amounts as strings (`"amount": "50000.00"`) with `@JsonFormat(shape = JsonFormat.Shape.STRING)`.

@@ 3 | TOPIC | XML with JAXB and Jackson XML | Producing and parsing XML in Spring | SHOULD
# XML with JAXB and Jackson XML

Banks and government systems often still use **XML**. Spring can produce JSON **or** XML from the same endpoint.

```xml
<!-- dependency -->
<dependency>
  <groupId>com.fasterxml.jackson.dataformat</groupId>
  <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```

```java
@JacksonXmlRootElement(localName = "Loan")
public record LoanXml(
        @JacksonXmlProperty(isAttribute = true) Long id,
        @JacksonXmlProperty(localName = "MemberName") String memberName,
        @JacksonXmlProperty(localName = "Amount") BigDecimal amount) {}

@GetMapping(value = "/loans/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
public LoanXml get(@PathVariable Long id) { ... }
```
Request with `Accept: application/xml` → response:
```xml
<Loan id="7">
  <MemberName>Rahim</MemberName>
  <Amount>50000.00</Amount>
</Loan>
```

**Line by line**
- With `jackson-dataformat-xml` on the classpath, Spring can write XML.
- `@JacksonXmlRootElement(localName = "Loan")`: the outer tag name.
- `isAttribute = true`: `id` becomes `<Loan id="7">` instead of a child element.
- `produces = {JSON, XML}`: **content negotiation**. The client's `Accept` header decides the format.

## JAXB (Jakarta XML Binding): the classic standard

```java
@XmlRootElement(name = "Member")
@XmlAccessorType(XmlAccessType.FIELD)
public class MemberXml {
    @XmlAttribute private Long id;
    @XmlElement(name = "Name") private String name;
}

JAXBContext ctx = JAXBContext.newInstance(MemberXml.class);
Marshaller m = ctx.createMarshaller();
m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
m.marshal(member, System.out);                                       // object -> XML
MemberXml back = (MemberXml) ctx.createUnmarshaller().unmarshal(new File("member.xml"));  // XML -> object
```
- **Marshal** = object → XML. **Unmarshal** = XML → object.
- `@XmlAccessorType(FIELD)`: map fields directly (no getters needed).
- JAXB is used heavily with SOAP and **XSD** schemas (XSD = the rulebook describing which XML is valid).

**Security:** disable external entities when parsing untrusted XML, to prevent **XXE attacks** (malicious XML that reads server files).

**JSON vs XML:** JSON is lighter and native to JavaScript. XML supports attributes, namespaces and strict schema validation (XSD), which is why older enterprise systems use it.

@@ 3 | TOPIC | SOAP Web Services | WSDL, envelope, Spring-WS endpoint and client | SHOULD
# SOAP web services

**SOAP** is a strict **XML-based protocol** with a formal contract (the **WSDL**). It is common in banking, telecom and government integrations.

**Analogy:** REST is a casual phone call. SOAP is a formal government letter with a fixed format, an official envelope, stamps and a reference number.

## The SOAP message
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ln="http://pksf.org.bd/loans">
  <soapenv:Header/>                        <!-- optional: security tokens, transaction ids -->
  <soapenv:Body>
    <ln:GetLoanRequest>
      <ln:loanId>7</ln:loanId>
    </ln:GetLoanRequest>
  </soapenv:Body>
</soapenv:Envelope>
```
- **Envelope:** the outer wrapper. **Header:** metadata (WS-Security). **Body:** the actual request. **Fault:** an error response.
- It is always sent as HTTP **POST** to one URL. The operation is named inside the body.

## WSDL: the contract
An XML document describing the **operations** (GetLoan), the **messages** (GetLoanRequest/Response), the **types** (XSD) and the **endpoint address**. Tools generate client code from it.

## Contract-first SOAP endpoint with Spring-WS

```java
@Endpoint
public class LoanEndpoint {
    private static final String NS = "http://pksf.org.bd/loans";
    private final LoanService service;
    public LoanEndpoint(LoanService service) { this.service = service; }

    @PayloadRoot(namespace = NS, localPart = "GetLoanRequest")
    @ResponsePayload
    public GetLoanResponse getLoan(@RequestPayload GetLoanRequest request) {
        GetLoanResponse resp = new GetLoanResponse();
        resp.setLoan(service.findSoapLoan(request.getLoanId()));
        return resp;
    }
}
```
**Line by line**
- `@Endpoint`: like `@RestController`, but for SOAP.
- `@PayloadRoot(namespace, localPart)`: routes messages whose body root element is `<GetLoanRequest>` in that namespace to this method.
- `@RequestPayload` / `@ResponsePayload`: Spring unmarshals the XML into a JAXB object and marshals the response back.
- `GetLoanRequest`/`GetLoanResponse` are **generated from the XSD** (contract-first) with the `jaxb2-maven-plugin`.
- A `WsConfigurerAdapter` config class exposes the WSDL at `/ws/loans.wsdl`.

## Calling a SOAP service (client)
```java
public class BankClient extends WebServiceGatewaySupport {
    public BalanceResponse balance(String acc) {
        BalanceRequest req = new BalanceRequest();
        req.setAccountNo(acc);
        return (BalanceResponse) getWebServiceTemplate().marshalSendAndReceive(req);
    }
}
```
- `marshalSendAndReceive`: object → SOAP XML → HTTP POST → parse the response XML → object.

| | REST | SOAP |
|---|---|---|
| Format | JSON (usually), any | XML only |
| Contract | OpenAPI (optional) | WSDL (mandatory) |
| Transport | HTTP verbs | Usually POST over HTTP |
| Standards | Simple | WS-Security, WS-ReliableMessaging, ACID transactions |
| Best for | Web/mobile apps | Strict enterprise/bank integrations |

@@ 2 | MODULE | Microservices Architecture | Decomposition, infrastructure, communication, resilience, sagas | SHOULD
**Microservices:** split one big app into small, independently deployable services, each owning its data.
**Analogy:** a shopping mall of small shops (microservices) vs one giant department store (monolith). Each shop can renovate on its own, but now you need corridors, security and maps for the whole mall.

@@ 3 | TOPIC | Monolith vs Microservices and Decomposition | Trade-offs, bounded contexts, database per service | SHOULD
# Monolith vs microservices

| | Monolith | Microservices |
|---|---|---|
| Deploy | One unit | Each service separately |
| Scale | Whole app | Only the busy service |
| Tech | One stack | Each service can differ |
| Complexity | Low (one process) | High (network, distributed data, ops) |
| Team | Small team | Many teams, each owning services |
| Failure | One bug can take everything down | Failures isolated (if designed well) |

**Honest advice for interviews:** "Start with a **well-structured modular monolith**. Split into microservices when there is a clear need: independent scaling, team autonomy, different release cycles." Interviewers like this balanced answer.

## How to split: by business capability (DDD bounded contexts)

For a PKSF-like system:
```
member-service      → members, samities, KYC
loan-service        → loan products, applications, schedules
collection-service  → repayments, ledger
reporting-service   → PAR, dashboards (read models)
notification-service→ SMS, email
auth (Keycloak)     → users, roles, tokens
```
- **Bounded context:** an area where a word has one clear meaning. "Member" in the loan context (a borrower) may hold different data than "member" in the notification context (just a phone number).
- **Don't split by technical layer** (a "database service", a "UI service"). That creates chatty, coupled services.

## Database per service
Each service **owns its tables**. Others ask through its API or events, never by reading its database directly.
- ✓ Services can change their schema independently.
- ✗ No cross-service JOINs or ACID transactions. You need **API composition**, **events** and **sagas** instead.

**Analogy:** each shop in the mall keeps its own stock room. If you need something from another shop, you ask at its counter; you don't walk into its stock room.

**Signs of a "distributed monolith" (bad):** services must be deployed together, share one database, and call each other synchronously in long chains.

@@ 3 | TOPIC | Core Infrastructure | API gateway, service discovery, config server, load balancing | SHOULD
# Core microservices infrastructure

```
Client ──► API Gateway ──► loan-service (x3 instances)
              │                 │
              │                 ├──► member-service
              │                 └──► Kafka ──► notification-service
              └── auth check (JWT from Keycloak)

Service discovery (Eureka / Kubernetes DNS) • Config server • Tracing (Zipkin/Tempo) • Logs (ELK/Loki)
```

| Component | Job | Analogy | Tools |
|---|---|---|---|
| **API Gateway** | Single entry point: routing, auth, rate limiting, CORS | The mall's main entrance with security | Spring Cloud Gateway, Kong, Nginx |
| **Service discovery** | Find where services are running (IP/port changes) | The mall directory board | Eureka, Consul, **Kubernetes DNS** |
| **Config server** | Central configuration for all services | Head office sends rules to every shop | Spring Cloud Config, K8s ConfigMaps |
| **Load balancer** | Spread requests over instances | Several open cash counters | Spring Cloud LoadBalancer, K8s Service |
| **Circuit breaker** | Stop calling a failing service | An electric fuse | Resilience4j |
| **Distributed tracing** | Follow one request across services | A parcel tracking number | Micrometer Tracing + Zipkin/Tempo |

## Spring Cloud Gateway route example

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: loans
          uri: lb://loan-service              # lb = load-balanced via discovery
          predicates:
            - Path=/api/loans/**
          filters:
            - StripPrefix=1                   # /api/loans/7 -> /loans/7
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```
**Line by line**
- `uri: lb://loan-service`: don't hard-code an IP. Ask discovery for the instances of "loan-service" and balance between them.
- `predicates: Path=...`: which requests match this route.
- `StripPrefix=1`: removes the first path segment (`/api`) before forwarding.
- `RequestRateLimiter`: a token bucket in Redis. 10 requests per second on average, bursts up to 20, and beyond that 429.

**In Kubernetes:** you often don't need Eureka or a Config Server. K8s Services provide DNS discovery and load balancing, and ConfigMaps/Secrets provide configuration.

@@ 3 | TOPIC | Service Communication | Sync REST/Feign vs async messaging | SHOULD
# Service communication

## Synchronous (request → wait → response)
REST/gRPC calls. Simple, but the caller **waits** and **fails if the callee is down**.

```java
@FeignClient(name = "member-service")              // resolved via discovery / K8s DNS
public interface MemberClient {
    @GetMapping("/members/{id}")
    MemberDto get(@PathVariable Long id);
}

@Service
@RequiredArgsConstructor
class LoanService {
    private final MemberClient members;
    LoanDto create(LoanRequest req) {
        MemberDto m = members.get(req.memberId());  // a remote HTTP call that looks like a method call
        if (!m.active()) throw new BusinessRuleException("Member inactive");
        ...
    }
}
```
**Line by line**
- `@FeignClient`: you write only an interface, and Spring Cloud OpenFeign generates the HTTP client.
- `name = "member-service"`: the logical service name, not a URL.
- It looks like a local call, but **it's the network**: it can be slow or fail. Add timeouts, retries and a circuit breaker.
- Enable it with `@EnableFeignClients`.

## Asynchronous (fire an event, don't wait)

```java
// loan-service publishes
kafkaTemplate.send("loan-approved", loan.getId().toString(), new LoanApproved(loan.getId(), loan.getMemberPhone()));

// notification-service consumes, whenever it's ready
@KafkaListener(topics = "loan-approved", groupId = "notification")
void on(LoanApproved e) { sms.send(e.phone(), "Your loan is approved"); }
```
- loan-service doesn't know or care who listens. If notification-service is down, the messages wait in Kafka until it comes back.

| | Sync (REST/Feign) | Async (Kafka/RabbitMQ) |
|---|---|---|
| Coupling | Temporal: both must be up | Loose |
| Response | Immediate | Eventually |
| Use for | Queries that need an answer now | Notifications, workflows, data sync |

**Analogy:** sync is a **phone call** (both people must be free right now). Async is **WhatsApp** (send it now, they read it when they can).

@@ 3 | TOPIC | Resilience with Resilience4j | Timeouts, retries, circuit breaker, fallback, bulkhead | SHOULD
# Resilience with Resilience4j

In a distributed system, **something is always failing**. Resilience patterns stop one failing service from bringing down everything (a **cascading failure**).

```java
@Service
@RequiredArgsConstructor
public class CreditCheckService {
    private final CreditBureauClient client;

    @CircuitBreaker(name = "creditBureau", fallbackMethod = "fallback")
    @Retry(name = "creditBureau")
    @TimeLimiter(name = "creditBureau")
    public CompletableFuture<CreditScore> score(String nid) {
        return CompletableFuture.supplyAsync(() -> client.score(nid));
    }

    private CompletableFuture<CreditScore> fallback(String nid, Throwable ex) {
        return CompletableFuture.completedFuture(CreditScore.unknown());   // degrade gracefully
    }
}
```
```yaml
resilience4j:
  circuitbreaker:
    instances:
      creditBureau:
        sliding-window-size: 20
        failure-rate-threshold: 50          # open if 50% of the last 20 calls failed
        wait-duration-in-open-state: 30s    # stay open for 30s, then test again
        permitted-number-of-calls-in-half-open-state: 3
  retry:
    instances:
      creditBureau:
        max-attempts: 3
        wait-duration: 500ms
        enable-exponential-backoff: true
  timelimiter:
    instances:
      creditBureau:
        timeout-duration: 2s
```

**Line by line**
- `@CircuitBreaker`: tracks failures. When too many fail, it **opens** and calls go straight to the fallback without touching the sick service.
- `fallbackMethod`: same parameters plus a `Throwable`. It returns a safe default (cached data, "unknown", a queued request).
- `@Retry`: tries again for **transient** errors. Exponential backoff waits 0.5 s, 1 s, 2 s, so we don't hammer the service. **Only retry idempotent operations** (or use idempotency keys).
- `@TimeLimiter`: gives up after 2 s instead of waiting forever. It needs an async return type.

## Circuit breaker states
```
CLOSED (normal) ──too many failures──► OPEN (reject calls, use fallback)
   ▲                                      │ after wait time
   └──── test calls succeed ── HALF_OPEN ◄┘ (allow a few test calls)
```
**Analogy:** the electric fuse in your house. When there's a short circuit it trips (OPEN) to protect everything else. After a while you try switching it on again (HALF_OPEN). If it holds, power is back (CLOSED).

**Bulkhead:** limits how many concurrent calls go to one dependency, so a slow service can't use up all your threads. **Analogy:** a ship's watertight compartments. One leak doesn't sink the whole ship.

@@ 3 | TOPIC | Distributed Transactions — Saga and Outbox | Consistency across services without 2PC | SHOULD
# Distributed transactions: saga and outbox

**Problem:** "Disburse a loan" touches loan-service (mark it disbursed), collection-service (create the ledger entry) and notification-service. Each has **its own database**, so there is no single ACID transaction.

**Two-phase commit (2PC)** locks everything across services. It is slow and fragile, so microservices avoid it.

## Saga: a chain of local transactions plus compensations

Each step commits locally and triggers the next. If a step fails, earlier steps are **undone with compensating actions**.

```
1. loan-service:        mark loan APPROVED        (compensate: mark CANCELLED)
2. collection-service:  create disbursement entry (compensate: post a REVERSAL entry)
3. payment-service:     send money to bKash       (if this fails → run the compensations for 2, then 1)
```
**Analogy:** booking a trip: flight, then hotel, then car. If the car booking fails, you cancel the hotel and the flight. You can't "roll back time", but you can **undo with new actions**.

Two styles:
- **Choreography:** each service listens for events and reacts (no central boss). Simple for 2–3 steps, but hard to follow for many.
- **Orchestration:** a central orchestrator tells each service what to do next. Easier to see the whole flow (tools: Camunda, Temporal).

## Transactional outbox: never lose an event

**Problem:** you save the loan to the DB **and** publish a Kafka event. If the app crashes between the two, the DB says "approved" but nobody was told (or the reverse).

```java
@Transactional
public void approve(Long loanId) {
    Loan loan = loans.findById(loanId).orElseThrow();
    loan.approve();
    outbox.save(new OutboxEvent("LoanApproved", loanId, toJson(loan)));   // SAME transaction
}

@Scheduled(fixedDelay = 1000)
public void publishOutbox() {
    for (OutboxEvent e : outbox.findTop100ByPublishedFalseOrderById()) {
        kafka.send("loan-events", e.getAggregateId().toString(), e.getPayload());
        e.markPublished();
    }
}
```
**Line by line**
- The business change and the event row are written in **one local DB transaction**: both or neither.
- A separate poller (or **Debezium CDC**, which reads the DB log) publishes the unpublished events to Kafka.
- If publishing fails, it retries later. This gives **at-least-once** delivery, so consumers must be **idempotent** (e.g. store processed event IDs and skip duplicates).
- **Analogy:** instead of trying to mail a letter at the exact moment you write it in your diary, you write "to send: letter X" in the diary itself. An assistant checks the diary and mails everything marked "to send".

@@ 3 | TOPIC | Observability | Logs, metrics, traces, correlation ids | SHOULD
# Observability

**Observability** = being able to understand what is happening inside a system from the outside. It has **three pillars**:

| Pillar | Question | Tools |
|---|---|---|
| **Logs** | What happened? (events with details) | Logback JSON → ELK / Loki |
| **Metrics** | How much / how fast? (numbers over time) | Micrometer → Prometheus → Grafana |
| **Traces** | Where did the time go across services? | Micrometer Tracing / OpenTelemetry → Zipkin / Tempo / Jaeger |

**Analogy:** a hospital patient. **Logs** = the nurse's notes, **metrics** = the heart-rate monitor, **traces** = the patient's journey through reception → X-ray → doctor, with times.

```xml
<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-actuator</artifactId></dependency>
<dependency><groupId>io.micrometer</groupId><artifactId>micrometer-registry-prometheus</artifactId></dependency>
<dependency><groupId>io.micrometer</groupId><artifactId>micrometer-tracing-bridge-otel</artifactId></dependency>
```
```java
@Service
@RequiredArgsConstructor
class RepaymentService {
    private final MeterRegistry registry;

    public void repay(RepaymentRequest r) {
        Timer.Sample t = Timer.start(registry);
        ...
        registry.counter("repayments.total", "po", r.poCode()).increment();   // a business metric
        t.stop(registry.timer("repayments.duration"));
    }
}
```
```yaml
logging:
  pattern:
    level: "%5p [${spring.application.name},%X{traceId:-},%X{spanId:-}]"
management:
  tracing:
    sampling:
      probability: 0.1          # trace 10% of requests in production
```

**Line by line**
- Micrometer is the "SLF4J of metrics": you write metrics once and export them to Prometheus/Datadog/etc.
- `counter(..., "po", code)`: a tag lets Grafana show repayments per PO. Avoid tags with huge numbers of values (like userId), which explode the number of series.
- `Timer`: measures durations, giving percentiles (p95, p99).
- `traceId` in every log line: the **same id** flows through every service a request touches, so you can search all the logs of one request.
- Sampling 10%: tracing every request is costly.

**Golden signals to alert on:** latency, traffic, errors, saturation (CPU/memory/pool usage).

@@ 2 | TOPIC | Messaging with Kafka and RabbitMQ | Topics, partitions, consumer groups, queues, delivery guarantees | SHOULD
# Messaging: Kafka and RabbitMQ

A **message broker** sits between services. Producers send messages, and consumers receive them later.
**Analogy:** a post office. The sender doesn't need the receiver to be home.

## Kafka: a distributed, replayable **log**

- **Topic:** a named stream (`loan-events`).
- **Partition:** a topic is split into ordered partitions for parallelism. **Order is guaranteed only within a partition.**
- **Key:** messages with the same key go to the same partition, so all events of loan #7 stay in order.
- **Consumer group:** consumers sharing a group split the partitions between them (each partition goes to one consumer in the group). Different groups each get **all** messages.
- **Offset:** a consumer's position in a partition. Messages **stay** after being read (by retention time), so you can replay them.

```java
// Producer
@Service
@RequiredArgsConstructor
class LoanEventPublisher {
    private final KafkaTemplate<String, LoanEvent> kafka;
    void publish(LoanEvent e) {
        kafka.send("loan-events", e.loanId().toString(), e);   // key = loanId -> per-loan ordering
    }
}

// Consumer
@Component
class PortfolioProjector {
    @KafkaListener(topics = "loan-events", groupId = "reporting")
    void handle(LoanEvent e) {
        if (processed.exists(e.eventId())) return;            // idempotent: skip duplicates
        updateReadModel(e);
        processed.save(e.eventId());
    }
}
```
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      acks: all                       # wait for all replicas: no data loss
      properties:
        enable.idempotence: true
    consumer:
      auto-offset-reset: earliest
      enable-auto-commit: false       # commit only after successful processing
```

**Line by line**
- `send(topic, key, value)`: the key decides the partition. The same loan always lands in the same partition.
- `groupId = "reporting"`: another group (e.g. "notification") would receive the same events independently.
- `processed.exists(...)`: Kafka gives **at-least-once** delivery by default, so a message may arrive twice. Consumers must be idempotent.
- `acks: all` + idempotent producer: the strongest durability, with no duplicates caused by producer retries.
- `enable-auto-commit: false`: if the app crashes mid-processing, the message is re-delivered instead of lost.

## RabbitMQ: a smart **queue** router
- The producer sends to an **exchange**, and the exchange routes to **queues** by binding rules (direct, topic, fanout).
- A message is **removed** once a consumer acknowledges it.
- Rich routing, per-message TTL, priority, and **dead-letter queues** for failed messages.

| | Kafka | RabbitMQ |
|---|---|---|
| Model | Log (messages kept, replayable) | Queue (deleted after ack) |
| Throughput | Very high | High |
| Ordering | Per partition | Per queue |
| Best for | Event streaming, event sourcing, analytics | Task queues, complex routing, RPC-style work |

## Delivery guarantees
- **At most once:** may lose messages, never duplicates them.
- **At least once:** never loses them, may duplicate them (the most common choice, combined with idempotent consumers).
- **Exactly once:** possible within Kafka with transactions, but end-to-end it is usually achieved as "at least once + idempotency".

**Dead Letter Queue (DLQ):** a message that keeps failing (e.g. bad data) is moved aside after N retries, so that it doesn't block the others and can be inspected later.

@@ 2 | TOPIC | Phase 5 — Interview Questions | Quick Q&A revision for APIs and microservices | MUST
# Phase 5 interview questions

**Q. What makes an API RESTful?**
A. Resources identified by URLs, standard HTTP methods, stateless requests, standard status codes, cacheable responses and a uniform interface.

**Q. PUT vs POST?**
A. POST creates a new resource (not idempotent). PUT creates or replaces the resource at a known URL (idempotent).

**Q. How do you version an API?**
A. A URL path (`/api/v1`), a header (`Accept: application/vnd.pksf.v2+json`) or a query param. Keep old versions working until clients migrate.

**Q. REST vs SOAP?**
A. REST is a lightweight style, usually JSON over HTTP verbs. SOAP is a strict XML protocol with a WSDL contract and WS-* standards (security, reliability), common in banks and government.

**Q. Monolith vs microservices: what would you start with?**
A. A modular monolith. Split out services when independent scaling, deployment or team ownership is clearly needed, because microservices add network and data complexity.

**Q. What is an API gateway?**
A. A single entry point that routes requests to services and handles cross-cutting concerns: auth, rate limiting, CORS, logging.

**Q. What is a circuit breaker?**
A. It stops calls to a failing service after a failure threshold (OPEN), returns a fallback, then lets test calls through (HALF_OPEN) before closing again. It prevents cascading failures.

**Q. How do you handle transactions across microservices?**
A. A saga: a sequence of local transactions with compensating actions, using choreography or orchestration, plus the outbox pattern for reliable event publishing.

**Q. Kafka vs RabbitMQ?**
A. Kafka is a durable, replayable, partitioned log with high throughput for event streams. RabbitMQ is a message broker with flexible routing, where messages are deleted after acknowledgement, good for task queues.

**Q. How does Kafka guarantee ordering?**
A. Only within a partition. Use the same key (e.g. loanId) so that related messages go to the same partition.

**Q. What is idempotency and why does it matter in messaging?**
A. Processing the same message twice has the same effect as once. At-least-once delivery means duplicates happen, so consumers deduplicate (e.g. by event id).

**Q. What is distributed tracing?**
A. Propagating a trace id across service calls so that one request's path and timings can be followed end to end (Zipkin/Tempo/Jaeger).

**Q. Sync vs async communication?**
A. Sync (REST) is simple and immediate but couples availability. Async (events) is decoupled and resilient but eventually consistent.
