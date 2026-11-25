@@ 3 | TOPIC | Practice — Coding drill: REST API scenarios | Ten interview tasks on endpoint design, status codes, validation, error handling, pagination, idempotency, files and HTTP clients | MUST
# Coding drill: REST API scenarios

Ten tasks of the kind asked in a Spring Boot coding round or take-home review: design an endpoint, fix a broken controller, choose a status code, or harden an outbound call. Try each before opening the solution. Spring Boot 3.5, Java 21.

## 1. Fix the RPC-style controller
Rewrite this controller to follow REST conventions (methods, URIs, status codes).

```java
// Snippet: Spring imports (org.springframework.*) and surrounding classes omitted for brevity
@RestController
public class CustomerApi {
    @PostMapping("/getCustomer")          public Customer get(@RequestBody IdRequest r) { return repo.find(r.id()); }
    @PostMapping("/createCustomer")       public Customer create(@RequestBody Customer c) { return repo.save(c); }
    @GetMapping("/deleteCustomer/{id}")   public String delete(@PathVariable long id) { repo.delete(id); return "ok"; }
}
```
**Hint:** nouns in URIs, the method carries the verb, 201 with `Location`, 204 for delete.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/customer/CustomerController.java
package com.orderhub.customer;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }

    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable long id) {
        return service.get(id);                                   // CustomerNotFoundException -> 404 via advice
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest req) {
        CustomerResponse created = service.create(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);    // 201 + Location
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();                // 204
    }
}
```
Reads use GET (safe, cacheable), the id lives in the path, creation returns 201 with `Location`, and deletion is a DELETE returning 204. A GET that deletes would be triggered by crawlers and prefetchers.
</details>

## 2. Choose the status codes
For each OrderHub situation give the status code: (a) malformed JSON, (b) `quantity: 0` fails `@Min(1)`, (c) order 999 does not exist, (d) cancel a shipped order, (e) no JWT, (f) a customer reads another customer's order, (g) rate limit hit, (h) inventory service timed out, (i) async export started, (j) client sent XML to a JSON-only endpoint.

<details><summary>Solution</summary>

```text
(a) 400 Bad Request            HttpMessageNotReadableException
(b) 400 Bad Request            (or 422 if your API standard says so) with field errors
(c) 404 Not Found
(d) 409 Conflict               invalid state transition, with a problem code
(e) 401 Unauthorized           plus WWW-Authenticate: Bearer
(f) 403 Forbidden              (or 404 to hide existence)
(g) 429 Too Many Requests      plus Retry-After
(h) 503 Service Unavailable    (or 504 Gateway Timeout) plus Retry-After
(i) 202 Accepted               plus Location of the job resource
(j) 415 Unsupported Media Type
```
The rule of thumb: 4xx means the client can fix something, 5xx means the server or an upstream failed. Never return 200 with an error flag.
</details>

## 3. Validate a nested request and report all field errors
Write `CreateOrderRequest` (customer id positive, 1 to 20 lines, each line with SKU pattern `[A-Z]{3}-\d{5}` and quantity 1 to 99) and a handler that returns a `ProblemDetail` with an `errors` array of `{field, message}`.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/order/dto/CreateOrderRequest.java
package com.orderhub.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CreateOrderRequest(
        @NotNull @Positive Long customerId,
        @NotEmpty @Size(max = 20) List<@Valid Line> lines) {

    public record Line(@NotBlank @Pattern(regexp = "[A-Z]{3}-\\d{5}") String sku,
                       @Min(1) @Max(99) int quantity) { }
}

// File: src/main/java/com/orderhub/common/error/GlobalExceptionHandler.java (validation part)
package com.orderhub.common.error;

import java.util.Comparator;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail pd = ex.getBody();
        pd.setTitle("Validation failed");
        pd.setProperty("errors", ex.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .map(fe -> Map.of("field", fe.getField(), "message", String.valueOf(fe.getDefaultMessage())))
                .toList());
        return handleExceptionInternal(ex, pd, headers, status, request);
    }
}
```
`List<@Valid Line>` cascades into each line, producing field paths like `lines[2].sku`. The controller parameter is `@Valid @RequestBody CreateOrderRequest req`.
</details>

## 4. Map domain exceptions globally
Services throw `OrderNotFoundException` and `InvalidOrderStateException(orderId, current, attempted)`. Map them to 404 and 409 `ProblemDetail` responses; the 409 must contain `currentStatus` and `attemptedAction`. Unknown exceptions must return 500 without internals.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/common/error/DomainErrorHandler.java
package com.orderhub.common.error;

import com.orderhub.order.InvalidOrderStateException;
import com.orderhub.order.OrderNotFoundException;
import java.net.URI;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DomainErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(DomainErrorHandler.class);

    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail notFound(OrderNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("https://api.orderhub.example/problems/not-found"));
        return pd;
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    ProblemDetail invalidState(InvalidOrderStateException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setType(URI.create("https://api.orderhub.example/problems/invalid-order-state"));
        pd.setProperty("currentStatus", ex.current().name());
        pd.setProperty("attemptedAction", ex.attempted());
        return pd;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail unexpected(Exception ex) {
        String ref = UUID.randomUUID().toString();
        log.error("Unexpected error ref={}", ref, ex);           // full details only in logs
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Reference " + ref);
        pd.setProperty("reference", ref);
        return pd;
    }
}
```
Response for cancelling a shipped order:
```json
{"type":"https://api.orderhub.example/problems/invalid-order-state","title":"Conflict","status":409,
 "detail":"Order 42 cannot be cancelled in status SHIPPED","instance":"/api/v1/orders/42/cancel",
 "currentStatus":"SHIPPED","attemptedAction":"cancel"}
```
Careful: a catch-all `Exception` handler in a high-precedence advice also catches Spring MVC exceptions (like `HttpMessageNotReadableException`) before `ResponseEntityExceptionHandler` sees them. Either keep everything in one advice that extends `ResponseEntityExceptionHandler` (the base class's specific handlers win over `Exception` by type distance) or put the catch-all in the lowest-precedence advice.
</details>

## 5. Paginated, filtered search with safe sorting
`GET /api/v1/products?category=books&minPrice=10&page=0&size=20&sort=price,asc`: bind filters into a record, accept a `Pageable`, cap size at 100, allow sorting only by `name`, `price` and `createdAt`, and always add `id` as a tie-breaker.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/product/ProductSearchController.java
package com.orderhub.product;

import java.math.BigDecimal;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductSearchController {
    public record ProductFilter(String category, BigDecimal minPrice, BigDecimal maxPrice) { }

    private static final Set<String> SORTABLE = Set.of("name", "price", "createdAt");
    private final ProductService service;

    public ProductSearchController(ProductService service) { this.service = service; }

    @GetMapping
    public PageResponse<ProductResponse> search(ProductFilter filter, Pageable pageable) {
        pageable.getSort().forEach(o -> {
            if (!SORTABLE.contains(o.getProperty())) throw new InvalidSortException(o.getProperty());
        });
        Sort sort = pageable.getSort().and(Sort.by(Sort.Direction.ASC, "id"));
        Pageable safe = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100), sort);
        return PageResponse.of(service.search(filter, safe));
    }
}
```
`InvalidSortException` is mapped to 400 by the global handler. In Phase 12 `service.search` becomes a JPA Specification or query method taking the `Pageable` directly. The tie-breaker prevents duplicates across pages when many products share a price.
</details>

## 6. Make POST /payments idempotent
Implement the core of an idempotent `POST /api/v1/payments` using a shared store abstraction. Concurrent duplicates must not both charge.

**Hint:** atomic reserve, fingerprint, replay.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/payment/PaymentController.java
package com.orderhub.payment;

import com.orderhub.common.idempotency.IdempotencyStore;
import com.orderhub.common.idempotency.IdempotencyStore.StoredResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService payments;
    private final IdempotencyStore store;

    public PaymentController(PaymentService payments, IdempotencyStore store) {
        this.payments = payments;
        this.store = store;
    }

    @PostMapping
    public ResponseEntity<Object> pay(@RequestHeader("Idempotency-Key") String key,
                                      @Valid @RequestBody PaymentRequest req) {
        String fingerprint = req.orderId() + ":" + req.amount() + ":" + req.method();
        if (!store.reserve(key, fingerprint)) {                       // atomic: SET NX or unique insert
            StoredResponse prev = store.find(key).orElseThrow();
            if (!prev.fingerprint().equals(fingerprint))
                return ResponseEntity.unprocessableEntity().body("Key reused with different payload");
            if (!prev.completed())
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Payment in progress");
            return ResponseEntity.status(prev.status()).body(prev.body());   // replay, no second charge
        }
        try {
            PaymentResponse res = payments.charge(req, key);          // pass key on to the provider too
            store.complete(key, new StoredResponse(fingerprint, 201, res, true));
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (RuntimeException e) {
            store.release(key);
            throw e;
        }
    }
}
```
Passing the same key to the payment provider makes the chain idempotent end to end: even if OrderHub crashes after charging but before `complete`, the provider deduplicates the retry.
</details>

## 7. Return 201, ETag and handle conditional updates
Write `PUT /api/v1/products/{id}` that requires `If-Match`, returns 428 if missing, 412 if stale, and 200 with a new `ETag` on success.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/product/ProductUpdateController.java
package com.orderhub.product;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductUpdateController {
    private final ProductService service;
    public ProductUpdateController(ProductService service) { this.service = service; }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable long id,
                                                  @RequestHeader(name = "If-Match", required = false) String ifMatch,
                                                  @Valid @RequestBody UpdateProductRequest req) {
        if (ifMatch == null) return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();
        long expectedVersion = Long.parseLong(ifMatch.replace("\"", "").replace("W/", ""));
        try {
            ProductResponse updated = service.update(id, expectedVersion, req);   // compares versions atomically
            return ResponseEntity.ok().eTag(Long.toString(updated.version())).body(updated);
        } catch (StaleVersionException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
    }
}
```
The service must compare and update in one atomic step (in Phase 12: a JPA `@Version` field, where a stale version raises `ObjectOptimisticLockingFailureException`). This prevents the lost-update problem when two admins edit the same product.
</details>

## 8. Secure file upload
Write the validation for `POST /api/v1/products/{id}/image`: at most 2 MB, PNG or JPEG by magic bytes, stored under a generated name. Explain why each check exists.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/storage/UploadValidator.java
package com.orderhub.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadValidator {
    private static final long MAX = 2L * 1024 * 1024;
    private static final byte[] PNG = {(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final byte[] JPG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

    public record Checked(String storageKey, String contentType) { }

    public Checked check(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("Empty file");
        if (file.getSize() > MAX) throw new IllegalArgumentException("Max 2 MB");          // memory and disk abuse
        byte[] head;
        try (InputStream in = file.getInputStream()) { head = in.readNBytes(4); }
        String type;
        if (head.length >= 4 && Arrays.equals(head, PNG)) type = "image/png";                // real content, not header
        else if (head.length >= 3 && Arrays.equals(Arrays.copyOf(head, 3), JPG)) type = "image/jpeg";
        else throw new IllegalArgumentException("Only PNG or JPEG");
        String ext = type.equals("image/png") ? ".png" : ".jpg";
        return new Checked(UUID.randomUUID() + ext, type);                                 // never the client name
    }
}
```
Size limits protect resources (also set `spring.servlet.multipart.max-file-size`); magic bytes stop renamed executables or HTML files; generated names prevent path traversal (`../../`) and overwriting; serving later with the stored content type and `nosniff` stops browsers from executing uploaded content.
</details>

## 9. Configure CORS for a SPA with Spring Security
The Vue app on `http://localhost:5173` calls the API with `Authorization: Bearer` and reads the `Location` header. Write the configuration.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/config/ApiSecurityConfig.java
package com.orderhub.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class ApiSecurityConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:5173"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type", "Idempotency-Key"));
        c.setExposedHeaders(List.of("Location", "ETag"));
        c.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", c);
        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())                 // CorsFilter before authentication
            .csrf(csrf -> csrf.disable())                    // bearer tokens, no cookies
            .authorizeHttpRequests(a -> a.anyRequest().permitAll());   // real rules in Phase 14
        return http.build();
    }
}
```
Bearer tokens in a header do not need `allowCredentials(true)` (that is for cookies). Without `http.cors(...)`, preflights hit authentication and fail with 401, which the browser reports as a CORS error.
</details>

## 10. Robust outbound call with an HTTP interface
Create a `PaymentProviderClient` HTTP interface with `charge(key, request)`, backed by `RestClient` with a 2 s connect and 5 s read timeout; map 4xx from the provider to `PaymentDeclinedException` and 5xx or timeouts to `PaymentProviderUnavailableException`.

<details><summary>Solution</summary>

```java
// File: src/main/java/com/orderhub/payment/provider/PaymentProviderClient.java
package com.orderhub.payment.provider;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/v1")
public interface PaymentProviderClient {
    record ChargeRequest(String reference, long amountCents, String currency, String cardToken) { }
    record ChargeResponse(String chargeId, String status) { }

    @PostExchange("/charges")
    ChargeResponse charge(@RequestHeader("Idempotency-Key") String key, @RequestBody ChargeRequest request);
}

// File: src/main/java/com/orderhub/payment/provider/PaymentProviderConfig.java
package com.orderhub.payment.provider;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration(proxyBeanMethods = false)
public class PaymentProviderConfig {

    @Bean
    PaymentProviderClient paymentProviderClient(RestClient.Builder builder) {
        var factory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build());
        factory.setReadTimeout(Duration.ofSeconds(5));

        RestClient rest = builder
                .baseUrl("https://api.payments.example")
                .requestFactory(factory)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new PaymentDeclinedException("Provider rejected charge: " + res.getStatusCode());
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new PaymentProviderUnavailableException("Provider error " + res.getStatusCode());
                })
                .build();

        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(rest)).build()
                .createClient(PaymentProviderClient.class);
    }
}

// File: src/main/java/com/orderhub/payment/provider/ProviderExceptions.java
package com.orderhub.payment.provider;

class PaymentDeclinedException extends RuntimeException {
    PaymentDeclinedException(String m) { super(m); }
}

class PaymentProviderUnavailableException extends RuntimeException {
    PaymentProviderUnavailableException(String m) { super(m); }
}
```
Timeouts surface as `ResourceAccessException`; the calling `PaymentService` catches it and throws `PaymentProviderUnavailableException`, which the global handler maps to 503. Declines map to 402 Payment Required or 422 with a problem code. Because the idempotency key travels with the request, a later retry (Resilience4j, Phase 20) cannot double-charge.
</details>

@@ 3 | TOPIC | Practice — Phase 11 review: REST APIs | Cheat sheet, key snippets, top 10 questions and rapid-fire flashcards for building REST APIs with Spring | MUST
# Phase 11 review: REST APIs

## Cheat sheet

| Idea | Remember |
|---|---|
| REST | Resources with URIs, uniform interface, stateless, cacheable |
| URIs | Plural nouns, no verbs, actions as sub-resources (`/orders/42/cancel`) |
| GET | Read; safe and idempotent; never changes state |
| POST | Create or action; not idempotent; 201 + `Location` |
| PUT vs PATCH | Full replace (idempotent) vs partial update (not guaranteed idempotent) |
| DELETE | Idempotent; 204 |
| Key codes | 200, 201, 202, 204, 304, 400, 401, 403, 404, 409, 412, 415, 422, 429, 500, 503 |
| Idempotency-Key | Atomic reserve, fingerprint, replay, shared store, TTL |
| ETag | `If-None-Match` gives 304; `If-Match` gives 412 when stale |
| DispatcherServlet | HandlerMapping, HandlerAdapter, argument resolvers, message converters |
| Mapping conditions | path, method, params, headers, consumes (415), produces (406) |
| Filter vs interceptor | Before the dispatcher vs around a known handler |
| ResponseEntity | Status + headers + body; use for 201, 204, 404, caching, downloads |
| Validation | `@Valid @RequestBody`, nested `@Valid`, `MethodArgumentNotValidException` (400) |
| `@Validated` | Groups and method validation via proxy; throws `ConstraintViolationException` |
| Errors | One `@RestControllerAdvice` extending `ResponseEntityExceptionHandler`, `ProblemDetail` (RFC 9457) |
| Pagination | `Pageable`, `Page` vs `Slice`, max size, id tie-breaker, keyset for deep pages |
| Versioning | URI `/v1` common; additive changes first; `Deprecation` and `Sunset`; native in `Boot 4` |
| OpenAPI | springdoc: `/v3/api-docs`, `/swagger-ui.html`, validation becomes schema |
| Files | `MultipartFile`, limits, magic bytes, generated names, stream downloads |
| CORS | Browser-enforced; preflight `OPTIONS`; `http.cors()` with Security; no `*` with credentials |
| HTTP clients | `RestClient` (sync), `WebClient` (reactive), `@HttpExchange` interfaces; always timeouts |

## Key code snippets

```java
// Create with 201 + Location
@PostMapping
public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req) {
    OrderResponse o = service.create(req);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(o.id()).toUri();
    return ResponseEntity.created(uri).body(o);
}
```

```java
// Snippet: Spring imports (org.springframework.*) and surrounding classes omitted for brevity
// Validated DTO with nested lines
public record CreateOrderRequest(@NotNull @Positive Long customerId,
                                 @NotEmpty @Size(max = 20) List<@Valid Line> lines) {
    public record Line(@NotBlank String sku, @Min(1) @Max(99) int quantity) { }
}
```

```java
// Snippet: Spring imports (org.springframework.*) and surrounding classes omitted for brevity
// Global error handling with ProblemDetail
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail notFound(OrderNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Resource not found");
        return pd;
    }
}
```

```java
// Pagination with a tie-breaker
@GetMapping
public PageResponse<ProductResponse> list(@PageableDefault(size = 20, sort = "name") Pageable p) {
    Pageable safe = PageRequest.of(p.getPageNumber(), Math.min(p.getPageSize(), 100), p.getSort().and(Sort.by("id")));
    return PageResponse.of(service.list(safe));
}
```

```yaml
# Limits, pageable defaults and docs
spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 6MB
  data:
    web:
      pageable:
        max-page-size: 100
springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

```java
// Snippet: Spring imports (org.springframework.*) and surrounding classes omitted for brevity
// CORS with Spring Security and a declarative HTTP client
http.cors(Customizer.withDefaults());

@HttpExchange("/api/stock")
public interface InventoryClient {
    @GetExchange("/{sku}") StockResponse stock(@PathVariable String sku);
}
InventoryClient client = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build()
        .createClient(InventoryClient.class);
```

## The 10 most likely interview questions

1. **PUT vs PATCH vs POST?** POST creates or triggers (not idempotent); PUT replaces at a known URI (idempotent); PATCH partially updates (not guaranteed idempotent).
2. **What is idempotency and how do you make POST idempotent?** Same effect when repeated; use an `Idempotency-Key` with atomic reservation in a shared store and replay the stored response.
3. **Which status codes do you use and when?** 200 read, 201 create with `Location`, 204 delete, 400 invalid input, 401 unauthenticated, 403 forbidden, 404 missing, 409 conflict, 429 rate limit, 500 bug, 503 upstream down.
4. **How does a request reach your controller?** Filters, `DispatcherServlet`, `HandlerMapping` picks the method, `HandlerAdapter` resolves arguments with converters, return value written by a message converter.
5. **How do you validate input?** Constraint annotations on DTO records, `@Valid @RequestBody`, nested `@Valid`, custom constraints; errors become a 400 `ProblemDetail` with field errors.
6. **How do you handle exceptions globally?** `@RestControllerAdvice` extending `ResponseEntityExceptionHandler`, `@ExceptionHandler` per domain exception, `ProblemDetail` bodies, a logged catch-all without internals.
7. **How do you paginate?** `Pageable` in, `Page` or `Slice` out, capped size, whitelisted sort fields, id tie-breaker; keyset pagination for deep or live data.
8. **How do you version an API?** Prefer additive changes; URI versioning for breaking ones; run versions side by side with deprecation headers; Spring 7 adds native support.
9. **What is CORS and how do you configure it?** Browser-enforced cross-origin rules; preflight `OPTIONS`; `CorsConfigurationSource` plus `http.cors()`; explicit origins, exposed headers.
10. **RestTemplate vs RestClient vs WebClient?** Legacy template API vs modern fluent synchronous client vs reactive non-blocking client; HTTP interfaces sit on top; always set timeouts and map errors.

## Rapid-fire flashcards

Q: Which status code and header does a successful resource creation return?
A: 201 Created with a `Location` header.

Q: Is DELETE idempotent if the second call returns 404?
A: Yes; idempotency concerns server state, not the response.

Q: Which exception does a failed `@Valid @RequestBody` raise in Spring MVC?
A: `MethodArgumentNotValidException`.

Q: What content type does an RFC 9457 problem response use?
A: `application/problem+json`.

Q: Which status code means the request's Content-Type is not supported?
A: 415 Unsupported Media Type.

Q: What does `Slice` avoid compared with `Page`?
A: The extra count query for total elements.

Q: Which springdoc URL serves the OpenAPI JSON by default?
A: `/v3/api-docs`.

Q: What are Spring Boot's default multipart limits?
A: 1MB per file and 10MB per request.

Q: Why does a CORS preflight fail with Spring Security unless `http.cors()` is enabled?
A: The preflight carries no credentials and is rejected by authentication before CORS headers are added.

Q: Which exception does `RestClient` throw on a timeout?
A: `ResourceAccessException`.
