@@ 3 | TOPIC | Securing Service-to-Service Communication | Zero trust between services with OAuth2 client credentials, JWT resource servers, token relay and mTLS | SHOULD
# Securing Service-to-Service Communication

**Level: Advanced | Phase 20 | Priority: SHOULD | Since: OAuth2 client credentials (RFC 6749, 2012); Spring Security 6 resource server and `OAuth2ClientHttpRequestInterceptor` (6.4); SSL bundles since Boot 3.1**

> **30-second interview answer:** Inside a microservice system I assume the network is hostile (zero trust), so every call is authenticated and authorized, not only calls at the gateway. For user requests the gateway validates the user's JWT and relays it, so downstream services know who the user is. For machine-to-machine calls, like order-service reserving stock, the caller gets its own token with the OAuth2 client credentials grant, and the callee, configured as a resource server, validates signature, issuer, audience and scopes. Transport is protected with TLS, often mutual TLS so both sides prove their identity with certificates, either in the app via Spring Boot SSL bundles or transparently via a service mesh.

## 1. Concept in Simple Words
### Analogy
Staff badges in a hospital.

| Hospital | Service security |
|---|---|
| Visitors show ID at the front desk | Gateway validates the user's JWT |
| A visitor sticker that staff can read on every floor | Token relay: the user's JWT forwarded downstream |
| Staff badges issued by HR, valid for 8 hours | Client credentials tokens issued by the authorization server, short-lived |
| A badge that opens the pharmacy only for pharmacists | Scopes and audience checked by the callee |
| Every door has a badge reader, not just the main entrance | Every service validates tokens (zero trust) |
| Couriers and the hospital both show ID to each other | Mutual TLS |
| Corridors closed to the public | Network policies |

### What it is
- **Zero trust:** never trust a request because it comes from "inside"; authenticate and authorize every hop.
- **JWT (JSON Web Token):** a signed token with claims (`sub`, `iss`, `aud`, `exp`, `scope`). Services validate it locally with the issuer's public keys (JWKS), with no call to the authorization server per request.
- **Authorization server:** issues tokens (Keycloak, Okta, Auth0, Spring Authorization Server).
- **Resource server:** a service that accepts and validates bearer tokens (`spring-boot-starter-oauth2-resource-server`).
- **Client credentials grant:** a service authenticates with its own client id and secret (or private key) and receives an access token representing the service itself, not a user.
- **Token relay:** forwarding the incoming user token on downstream calls. **Token exchange** (RFC 8693) swaps it for a narrower token for a specific downstream audience.
- **Audience (`aud`):** which service a token is meant for. Checking it stops a token issued for notification-service being replayed against inventory-service.
- **mTLS:** TLS where the client also presents a certificate, so both sides are authenticated at the transport level.
- **SSL bundle:** Spring Boot's named configuration of key stores and trust stores (`spring.ssl.bundle.*`), reusable by the server, `RestClient`, Kafka, Redis and others.

### Why it exists
If only the gateway checks tokens, any compromised pod, misconfigured ingress or developer with VPN access can call `POST /api/inventory/reservations` directly and drain stock. Trusting headers like `X-User-Id` set "by the gateway" is worse: anyone who reaches the service can set them. Security breaches regularly start inside the perimeter, so each service must protect itself.

## 2. How It Works
```mermaid
sequenceDiagram
    participant U as "Browser"
    participant G as "Gateway"
    participant O as "order-service"
    participant A as "Authorization server (Keycloak)"
    participant I as "inventory-service"
    U->>G: "POST /api/orders, Bearer user JWT"
    G->>G: "Validate JWT (signature, exp, iss)"
    G->>O: "Forward with the same user JWT (token relay)"
    O->>O: "Resource server: check scope orders.write, read sub as customer"
    O->>A: "client_credentials: client_id=order-service, scope=inventory.write (cached until expiry)"
    A-->>O: "access token, aud=inventory-service, expires in 300 s"
    O->>I: "POST /api/inventory/reservations, Bearer service token (over mTLS)"
    I->>I: "Validate signature with JWKS, iss, aud, scope inventory.write"
    I-->>O: "201 Created"
```

Behind the Spring annotations:
- **Resource server:** Boot creates a `JwtDecoder` from `issuer-uri` (discovering the JWKS URL), and `BearerTokenAuthenticationFilter` turns a valid token into a `JwtAuthenticationToken` whose authorities are `SCOPE_<scope>` by default. Expired, wrongly signed or wrong-audience tokens give `401`; valid tokens without the required authority give `403`.
- **Client side:** `OAuth2AuthorizedClientManager` obtains and caches tokens per registration and refreshes them shortly before expiry. `OAuth2ClientHttpRequestInterceptor` (Spring Security 6.4+) asks the manager for a token on each `RestClient` request and sets the `Authorization` header.
- **mTLS:** the server's `server.ssl.client-auth=need` rejects TLS handshakes without a client certificate signed by a trusted CA; the client presents its certificate from its SSL bundle.

## 3. Code Example
### Code
inventory-service as a resource server, order-service as an OAuth2 client with mTLS, gateway relaying user tokens.

```xml
<!-- inventory-service -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>

<!-- order-service: it is both a resource server (for users) and a client (to inventory) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

```yaml
# inventory-service application.yml
server:
  port: 8443
  ssl:
    bundle: inventory-server
    client-auth: need                       # mutual TLS: callers must present a certificate
spring:
  ssl:
    bundle:
      pem:
        inventory-server:
          keystore:
            certificate: file:/etc/certs/inventory.crt
            private-key: file:/etc/certs/inventory.key
          truststore:
            certificate: file:/etc/certs/orderhub-ca.crt   # trust certs signed by our internal CA
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://keycloak:8180/realms/orderhub
          audiences: inventory-service      # reject tokens meant for other services
```

```java
// inventory-service: authorization rules
package com.orderhub.inventory.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class InventorySecurityConfig {

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/inventory/reservations").hasAuthority("SCOPE_inventory.write")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventory/reservations/**").hasAuthority("SCOPE_inventory.write")
                        .requestMatchers(HttpMethod.GET, "/api/inventory/**").hasAuthority("SCOPE_inventory.read")
                        .anyRequest().denyAll())                         // default deny
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())                            // stateless API, no cookies
                .build();
    }
}
```

```yaml
# order-service application.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://keycloak:8180/realms/orderhub
          audiences: order-service
      client:
        registration:
          inventory:
            provider: keycloak
            client-id: order-service
            client-secret: ${ORDER_SERVICE_CLIENT_SECRET}    # from a Secret, never in Git
            authorization-grant-type: client_credentials
            scope: inventory.read,inventory.write
        provider:
          keycloak:
            issuer-uri: http://keycloak:8180/realms/orderhub
  ssl:
    bundle:
      pem:
        order-client:
          keystore:
            certificate: file:/etc/certs/order.crt
            private-key: file:/etc/certs/order.key
          truststore:
            certificate: file:/etc/certs/orderhub-ca.crt
inventory:
  base-url: https://inventory-service:8443
```

```java
// order-service: RestClient that adds a service token and presents a client certificate
package com.orderhub.order.security;

import com.orderhub.order.client.InventoryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
class SecureInventoryClientConfig {

    // Works outside HTTP requests too (Kafka listeners, schedulers): no servlet request needed
    @Bean
    OAuth2AuthorizedClientManager serviceTokenManager(ClientRegistrationRepository registrations,
                                                      OAuth2AuthorizedClientService clients) {
        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, clients);
        manager.setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build());
        return manager;                                   // caches tokens until shortly before expiry
    }

    @Bean
    InventoryClient secureInventoryClient(RestClient.Builder builder, RestClientSsl ssl,
                                          OAuth2AuthorizedClientManager manager,
                                          @Value("${inventory.base-url}") String baseUrl) {
        var tokenInterceptor = new OAuth2ClientHttpRequestInterceptor(manager);
        tokenInterceptor.setClientRegistrationIdResolver(request -> "inventory");

        RestClient restClient = builder
                .baseUrl(baseUrl)
                .apply(ssl.fromBundle("order-client"))   // client certificate + trusted CA (mTLS)
                .requestInterceptor(tokenInterceptor)    // Authorization: Bearer <service token>
                .build();

        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build().createClient(InventoryClient.class);
    }
}
```

```yaml
# gateway: forward the logged-in user's token downstream
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
                - TokenRelay=                     # needs spring-boot-starter-oauth2-client in the gateway
                - RemoveRequestHeader=X-User-Id   # never let clients inject identity headers
```

### Walkthrough
- inventory-service accepts only HTTPS connections that present a certificate signed by the OrderHub CA (`client-auth: need`). That stops anything without a cluster certificate before HTTP even starts.
- Its resource server validates every JWT against Keycloak's keys, checks the issuer and that `aud` contains `inventory-service`. Scopes become `SCOPE_` authorities; reservation endpoints need `inventory.write`, reads need `inventory.read`, everything else is denied by default.
- order-service registers itself as an OAuth2 client with the client credentials grant. The secret comes from an environment variable injected from a Kubernetes Secret or Vault.
- `AuthorizedClientServiceOAuth2AuthorizedClientManager` fetches tokens without needing a current HTTP request, which matters because the saga calls inventory from Kafka listeners too. Tokens are cached and reused until near expiry, so Keycloak sees one token request every few minutes, not one per call.
- `RestClientSsl.fromBundle` applies the client certificate and trust store; the interceptor adds the bearer token. Both protections apply to every call made through `InventoryClient`.
- At the gateway, `TokenRelay` forwards the user's access token, and `RemoveRequestHeader` strips spoofable identity headers.

### Output
order-service obtains a token (logged at DEBUG):

```http
POST http://keycloak:8180/realms/orderhub/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded
Authorization: Basic b3JkZXItc2VydmljZTpzM2NyZXQ=

grant_type=client_credentials&scope=inventory.read%20inventory.write

HTTP/1.1 200 OK
{"access_token":"eyJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJpbnZlbnRvcnktc2VydmljZSJ9.c2lnbmF0dXJl","expires_in":300,"token_type":"Bearer","scope":"inventory.read inventory.write"}
```

Calls to inventory-service:

```http
POST https://inventory-service:8443/api/inventory/reservations
Authorization: Bearer <token with scope inventory.write, aud inventory-service>
HTTP/1.1 201 Created

POST https://inventory-service:8443/api/inventory/reservations
Authorization: Bearer <token for notification-service, aud notification-service>
HTTP/1.1 401 Unauthorized
WWW-Authenticate: Bearer error="invalid_token", error_description="The required audience is missing"

POST https://inventory-service:8443/api/inventory/reservations
Authorization: Bearer <token with only inventory.read>
HTTP/1.1 403 Forbidden
WWW-Authenticate: Bearer error="insufficient_scope"
```

A caller without a client certificate:

```text
curl: (56) OpenSSL SSL_read: error:0A00045C:SSL routines::tlsv13 alert certificate required
```

### Compare
| Approach | Who is authenticated | Strength | Weakness |
|---|---|---|---|
| Trust the internal network | Nobody | Simple | One breach exposes everything |
| Gateway-only JWT check | User at the edge | Simple for services | Soft centre, spoofable headers |
| Token relay | User on every hop | Services know the user | Long chains carry broad tokens |
| Client credentials | Calling service | Least privilege per service | Token management |
| mTLS | Both endpoints (transport) | Strong identity, encryption | Certificate lifecycle |
| Service mesh (Istio mTLS + policies) | Workloads automatically | No app code | Mesh complexity |

## 4. Interview Questions
**Basic**
1. **How do you authenticate one microservice to another?**
   With the OAuth2 client credentials grant: the calling service gets an access token for itself from the authorization server and sends it as a bearer token; the callee validates it as a resource server and checks scopes and audience. mTLS can add transport-level identity.
2. **What does a resource server validate in a JWT?**
   The signature against the issuer's public keys (JWKS), expiry and not-before times, the issuer, and ideally the audience. Then the application authorizes based on scopes, roles or other claims.
3. **What is token relay?**
   Forwarding the user's access token from the gateway (or a service) to downstream services so they can make decisions based on the end user. Spring Cloud Gateway has a `TokenRelay` filter for this.

**Follow-up**
4. **When would you use the user's token versus a service token for a downstream call?**
   Use the user's token when the downstream must enforce user-level rules (can this customer see this order). Use a service token for system operations the user does not directly own, such as reserving stock from a saga or batch job. Token exchange can produce a narrower user token for a specific downstream.
5. **Why check the audience claim?**
   Without it, any valid token from the same issuer works everywhere: a token stolen from a low-privilege service could be replayed against inventory. The audience binds a token to its intended recipients.
6. **What does mTLS add over bearer tokens?**
   Encryption in transit plus cryptographic identity of both peers at connection time, so a stolen token alone is not enough without the matching certificate. It also blocks unauthenticated network clients before any HTTP processing. The cost is certificate issuance and rotation, which a mesh or cert-manager can automate.

**Tricky / scenario**
7. **Services read `X-User-Id` set by the gateway to identify the customer. What is wrong?**
   Anyone who reaches a service directly, or a client if the gateway does not strip the header, can impersonate any user. Identity must come from a signed token the service validates itself. If a header is used, the gateway must strip incoming copies and the network must make the gateway the only path, which is still weaker.
8. **Keycloak goes down. What happens to service-to-service calls?**
   Resource servers keep validating tokens with cached JWKS keys, and clients keep using cached tokens until they expire (for example 5 minutes). After expiry, new tokens cannot be obtained and calls start failing. Run the authorization server highly available and alert on token errors; do not make tokens long-lived as a workaround.
9. **Where do you store the client secret for order-service?**
   In a secrets manager or Kubernetes Secret injected as an environment variable or file, never in Git or the image. Better still, use private-key JWT client authentication or workload identity so there is no shared secret, and rotate credentials regularly.

**What the interviewer is really testing:** whether you apply zero trust inside the system and can distinguish user identity from service identity, with concrete Spring Security configuration.

## 5. Pitfalls & Best Practices
### Common mistakes
| Mistake | What goes wrong | Fix |
|---|---|---|
| Only the gateway checks tokens | Internal callers bypass security | Every service is a resource server |
| Trusting identity headers | User impersonation | Derive identity from validated JWTs |
| No audience validation | Token replay across services | `jwt.audiences` per service |
| Requesting a new token on every call | Authorization server overload, latency | `OAuth2AuthorizedClientManager` caching |
| Long-lived service tokens | Stolen token works for days | Short expiry, automatic renewal |
| Client secrets in `application.yml` in Git | Credential leak | Secrets manager, env vars, private-key JWT |

### Best practices
- Default deny: `anyRequest().denyAll()` and explicit rules per endpoint.
- One client registration per service with least-privilege scopes.
- Keep actuator endpoints on a separate management port, not exposed outside the cluster.
- Use Kubernetes NetworkPolicies so only expected callers can reach each service.
- Automate certificates (cert-manager, mesh CA) and rotate them often.

### Compare with
| Mechanism | Layer | Identifies |
|---|---|---|
| API keys | Application | A client application, weakly |
| JWT bearer tokens (OAuth2) | Application | User or service, with scopes |
| mTLS | Transport | Workload or host |
| NetworkPolicy and firewalls | Network | IP ranges and pods |
| Service mesh authorization policies | Proxy | Workload identity (SPIFFE) |

## 6. Hands-on Exercise
Secure notification-service's admin endpoint `POST /api/notifications/resend/{orderId}`: only callers with scope `notifications.admin` may use it, the token audience must be `notification-service`, and the customer id in the URL query must match the token's `customer_id` claim unless the caller has the admin scope. Write a `@WebMvcTest` using `jwt()` request post-processors to prove the rules.

Acceptance criteria: admin scope gives 202; a customer token with matching `customer_id` gives 202; a mismatching customer gives 403; no token gives 401.

<details><summary>Solution</summary>

```java
package com.orderhub.notification.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ResendController {

    @PostMapping("/api/notifications/resend/{orderId}")
    @PreAuthorize("hasAuthority('SCOPE_notifications.admin') or "
            + "(hasAuthority('SCOPE_notifications.self') and #customerId == authentication.token.claims['customer_id'])")
    ResponseEntity<Void> resend(@PathVariable long orderId, @RequestParam Long customerId) {
        return ResponseEntity.accepted().build();
    }
}
```

```java
package com.orderhub.notification.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    SecurityFilterChain chain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(a -> a.anyRequest().authenticated())
                   .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
                   .csrf(c -> c.disable())
                   .build();
    }
}
```

```java
package com.orderhub.notification.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ResendController.class)
@Import(SecurityConfig.class)
class ResendControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean JwtDecoder jwtDecoder;     // not called: jwt() bypasses decoding

    @Test void adminCanResend() throws Exception {
        mvc.perform(post("/api/notifications/resend/42").param("customerId", "7")
                .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_notifications.admin"))))
           .andExpect(status().isAccepted());
    }

    @Test void customerCanResendOwnOrder() throws Exception {
        mvc.perform(post("/api/notifications/resend/42").param("customerId", "7")
                .with(jwt().jwt(j -> j.claim("customer_id", 7L))
                           .authorities(new SimpleGrantedAuthority("SCOPE_notifications.self"))))
           .andExpect(status().isAccepted());
    }

    @Test void otherCustomerIsForbidden() throws Exception {
        mvc.perform(post("/api/notifications/resend/42").param("customerId", "8")
                .with(jwt().jwt(j -> j.claim("customer_id", 7L))
                           .authorities(new SimpleGrantedAuthority("SCOPE_notifications.self"))))
           .andExpect(status().isForbidden());
    }

    @Test void anonymousIsUnauthorized() throws Exception {
        mvc.perform(post("/api/notifications/resend/42").param("customerId", "7"))
           .andExpect(status().isUnauthorized());
    }
}
```
In production add `spring.security.oauth2.resourceserver.jwt.audiences: notification-service` so tokens for other services are rejected before the method security check runs.
</details>

## 7. Recall
Q: Which OAuth2 grant does a microservice use to call another service on its own behalf?
A: The client credentials grant: the service authenticates with its own credentials and receives an access token representing itself.

Q: Why should each microservice validate JWTs even behind an API gateway?
A: Zero trust: internal callers or compromised pods could bypass the gateway, so every service must authenticate and authorize requests itself.

Q: What does checking the JWT audience claim prevent between microservices?
A: Replaying a token issued for one service against another service that trusts the same issuer.

Q: What does mutual TLS add to service-to-service communication?
A: Both client and server present certificates, giving encryption plus cryptographic identity of both peers at the transport level.

Q: What does Spring Security's `OAuth2ClientHttpRequestInterceptor` do on a `RestClient`?
A: It obtains (and reuses cached) OAuth2 access tokens from an `OAuth2AuthorizedClientManager` and adds them as bearer tokens to outgoing requests.

**Related:** [[API Gateway with Spring Cloud Gateway]] · [[Service-to-Service Calls: OpenFeign and HTTP Interfaces]] · [[Kubernetes Basics for Spring Developers]]

@@ 3 | TOPIC | Practice — Coding drill: Split OrderHub into services | Ten hands-on tasks that turn the OrderHub monolith into gateway, order, inventory and notification services | MUST
# Coding drill: Split OrderHub into services

Ten tasks that take OrderHub from one application to four services plus infrastructure. They mirror what interviewers ask in a take-home or a "walk me through how you would split this" round. Do them in order; each solution is self-contained.

## 1. Multi-module Maven build with the Spring Cloud BOM
Create a parent POM `orderhub-parent` with modules `gateway`, `discovery-server`, `order-service`, `inventory-service`, `notification-service`. All modules must use Boot 3.5 and the Spring Cloud 2025.0 BOM without repeating versions.

Hint: inherit from `spring-boot-starter-parent` in the parent and import `spring-cloud-dependencies` in `dependencyManagement`.

<details><summary>Solution</summary>

```xml
<!-- orderhub/pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.7</version>
        <relativePath/>
    </parent>
    <groupId>com.orderhub</groupId>
    <artifactId>orderhub-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>discovery-server</module>
        <module>gateway</module>
        <module>order-service</module>
        <module>inventory-service</module>
        <module>notification-service</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2025.0.0</spring-cloud.version>
        <resilience4j.version>2.3.0</resilience4j.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>io.github.resilience4j</groupId>
                <artifactId>resilience4j-spring-boot3</artifactId>
                <version>${resilience4j.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

```xml
<!-- inventory-service/pom.xml -->
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.orderhub</groupId>
        <artifactId>orderhub-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>inventory-service</artifactId>
    <dependencies>
        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-actuator</artifactId></dependency>
        <dependency><groupId>org.springframework.cloud</groupId><artifactId>spring-cloud-starter-netflix-eureka-client</artifactId></dependency>
        <dependency><groupId>org.springframework.kafka</groupId><artifactId>spring-kafka</artifactId></dependency>
        <dependency><groupId>org.flywaydb</groupId><artifactId>flyway-database-postgresql</artifactId></dependency>
        <dependency><groupId>org.postgresql</groupId><artifactId>postgresql</artifactId><scope>runtime</scope></dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin><groupId>org.springframework.boot</groupId><artifactId>spring-boot-maven-plugin</artifactId></plugin>
        </plugins>
    </build>
</project>
```
The BOM guarantees that gateway, Eureka, LoadBalancer and OpenFeign versions are tested together. Each service is still its own deployable JAR; the parent only shares build settings, not code.
</details>

## 2. Local infrastructure with Docker Compose
Write a `docker-compose.yml` with one PostgreSQL per service (orders and inventory), Kafka in KRaft mode and Redis for the gateway rate limiter.

Hint: separate databases make the "database per service" rule physical.

<details><summary>Solution</summary>

```yaml
services:
  orders-db:
    image: postgres:17
    environment:
      POSTGRES_DB: orders
      POSTGRES_USER: orders
      POSTGRES_PASSWORD: orders
    ports: ["5433:5432"]
  inventory-db:
    image: postgres:17
    environment:
      POSTGRES_DB: inventory
      POSTGRES_USER: inventory
      POSTGRES_PASSWORD: inventory
    ports: ["5434:5432"]
  kafka:
    image: apache/kafka:3.9.0
    ports: ["9092:9092"]
  redis:
    image: redis:7.4
    ports: ["6379:6379"]
```
Two database containers stop anyone from writing a cross-service join. Notification-service can start with only a small table for processed messages in its own schema or database.
</details>

## 3. Move inventory data out of the shared database
In the monolith, `product` has a `stock` column used by orders. Design the Flyway migrations: inventory-service owns `stock_item` and `reservation`; order-service stops using `product.stock`. Write inventory's migration and a one-off data copy.

Hint: expand and contract: add the new store, copy, switch reads and writes, then drop the old column in a later release.

<details><summary>Solution</summary>

```sql
-- inventory-service: V1__inventory_schema.sql
CREATE TABLE stock_item (
    sku       VARCHAR(40) PRIMARY KEY,
    on_hand   INT NOT NULL CHECK (on_hand >= 0),
    version   BIGINT NOT NULL DEFAULT 0          -- optimistic locking
);

CREATE TABLE reservation (
    order_id  BIGINT      NOT NULL,
    sku       VARCHAR(40) NOT NULL REFERENCES stock_item (sku),
    quantity  INT         NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (order_id, sku)                   -- natural idempotency per order and sku
);

CREATE TABLE processed_message (
    consumer   VARCHAR(100) NOT NULL,
    message_id UUID         NOT NULL,
    PRIMARY KEY (consumer, message_id)
);
```

```sql
-- one-off copy, run once during the migration window (export from orders DB, import to inventory DB)
-- orders DB:
COPY (SELECT sku, stock FROM product) TO '/tmp/stock.csv' WITH CSV;
-- inventory DB:
COPY stock_item (sku, on_hand) FROM '/tmp/stock.csv' WITH CSV;
```

```sql
-- order-service: V9__drop_product_stock.sql (only in a LATER release, after reads switched)
ALTER TABLE product DROP COLUMN stock;
```
Release plan: release 1 deploys inventory-service and copies data; release 2 switches order-service to call inventory; release 3 drops the old column. Each step can be rolled back independently.
</details>

## 4. Idempotent reservation endpoint in inventory-service
Implement `POST /api/inventory/reservations` taking `{orderId, lines}` and an `Idempotency-Key` header. Return 201 with a reservation id; the same order reserved twice returns the same result; insufficient stock returns 409.

Hint: the `(order_id, sku)` primary key and a check for existing reservations make it idempotent.

<details><summary>Solution</summary>

```java
package com.orderhub.inventory.web;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ReservationController {
    record Line(String sku, int quantity) { }
    record ReserveRequest(long orderId, List<Line> lines) { }
    record ReservationResponse(String reservationId, long orderId, String status) { }

    static class InsufficientStockException extends RuntimeException {
        InsufficientStockException(String msg) { super(msg); }
    }

    private final ReservationService service;

    ReservationController(ReservationService service) { this.service = service; }

    @PostMapping("/api/inventory/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    ReservationResponse reserve(@RequestHeader("Idempotency-Key") String key, @RequestBody ReserveRequest req) {
        service.reserve(req);
        return new ReservationResponse("res-" + req.orderId(), req.orderId(), "RESERVED");
    }

    @ExceptionHandler(InsufficientStockException.class)
    ProblemDetail insufficient(InsufficientStockException e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        pd.setTitle("Insufficient stock");
        return pd;
    }

    @Service
    static class ReservationService {
        private final JdbcClient jdbc;

        ReservationService(JdbcClient jdbc) { this.jdbc = jdbc; }

        @Transactional
        public void reserve(ReserveRequest req) {
            int existing = jdbc.sql("select count(*) from reservation where order_id = ?")
                               .param(req.orderId()).query(Integer.class).single();
            if (existing > 0) return;                                   // duplicate: same result

            for (Line line : req.lines()) {
                int updated = jdbc.sql("""
                        update stock_item s set on_hand = on_hand - :qty, version = version + 1
                        where s.sku = :sku and s.on_hand - :qty >= 0
                        """)
                        .param("qty", line.quantity()).param("sku", line.sku())
                        .update();
                if (updated == 0) {
                    throw new InsufficientStockException("sku " + line.sku() + ": wanted " + line.quantity());
                }
                jdbc.sql("insert into reservation(order_id, sku, quantity) values (?, ?, ?)")
                    .params(req.orderId(), line.sku(), line.quantity()).update();
            }
        }
    }
}
```
The conditional `UPDATE` with `WHERE on_hand - qty >= 0` is atomic in the database, so two concurrent orders cannot oversell. The exception rolls back earlier lines of the same request. (This simplified version decrements `on_hand` on reservation; a real system would keep `on_hand` and `reserved` separately.)
</details>

## 5. Gateway routes for the three services
Configure the gateway so `/api/orders/**` and `/api/products/**` go to order-service, `/api/inventory/**` to inventory-service and `/api/notifications/**` to notification-service, all through discovery, with a correlation id response header and a 3-second response timeout.

Hint: global HTTP client timeout properties live under `spring.cloud.gateway.server.webflux.httpclient`.

<details><summary>Solution</summary>

```yaml
spring:
  application:
    name: gateway
  cloud:
    gateway:
      server:
        webflux:
          httpclient:
            connect-timeout: 1000          # ms
            response-timeout: 3s
          routes:
            - id: orders
              uri: lb://order-service
              predicates:
                - Path=/api/orders/**,/api/products/**
            - id: inventory
              uri: lb://inventory-service
              predicates:
                - Path=/api/inventory/**
            - id: notifications
              uri: lb://notification-service
              predicates:
                - Path=/api/notifications/**
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

```java
package com.orderhub.gateway;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
class CorrelationConfig {
    @Bean
    @Order(-1)
    GlobalFilter correlationId() {
        return (exchange, chain) -> {
            String id = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");
            String value = id != null ? id : UUID.randomUUID().toString();
            exchange.getResponse().getHeaders().set("X-Correlation-Id", value);
            return chain.filter(exchange.mutate()
                    .request(r -> r.header("X-Correlation-Id", value)).build());
        };
    }
}
```
A request to `/api/orders/1` that takes longer than 3 seconds returns `504 Gateway Timeout` instead of hanging.
</details>

## 6. Call inventory from order-service with timeouts and a circuit breaker
Write the `InventoryClient` HTTP Interface plus configuration, and a `ResilientInventory` wrapper with a circuit breaker and a fallback that throws `InventoryUnavailableException`.

Hint: build the `RestClient` from a `@LoadBalanced` builder and set connect and read timeouts on the request factory.

<details><summary>Solution</summary>

```java
package com.orderhub.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public interface InventoryClient {
    record Line(String sku, int quantity) { }
    record ReserveRequest(long orderId, List<Line> lines) { }
    record ReservationResponse(String reservationId, long orderId, String status) { }

    @PostExchange("/api/inventory/reservations")
    ReservationResponse reserve(@RequestHeader("Idempotency-Key") String key, @RequestBody ReserveRequest req);

    class InventoryUnavailableException extends RuntimeException {
        public InventoryUnavailableException(Throwable cause) { super("inventory unavailable", cause); }
    }

    @Configuration
    class Config {
        @Bean @LoadBalanced
        RestClient.Builder lbBuilder(RestClientBuilderConfigurer configurer) {
            return configurer.configure(RestClient.builder());
        }

        @Bean
        InventoryClient inventoryClient(@LoadBalanced RestClient.Builder builder) {
            var factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(Duration.ofSeconds(1));
            factory.setReadTimeout(Duration.ofSeconds(2));
            RestClient rc = builder.baseUrl("http://inventory-service").requestFactory(factory).build();
            return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(rc)).build()
                    .createClient(InventoryClient.class);
        }
    }

    @Component
    class ResilientInventory {
        private final InventoryClient client;

        ResilientInventory(InventoryClient client) { this.client = client; }

        @CircuitBreaker(name = "inventory", fallbackMethod = "unavailable")
        public ReservationResponse reserve(long orderId, List<Line> lines) {
            return client.reserve("order-" + orderId, new ReserveRequest(orderId, lines));
        }

        private ReservationResponse unavailable(long orderId, List<Line> lines, Throwable t) {
            throw new InventoryUnavailableException(t);
        }
    }
}
```
Everything is nested in one file for the drill; in a real project each type gets its own file. Without retry here, the fallback can sit on the circuit breaker itself.
</details>

## 7. Publish `OrderPlaced` through the outbox
In order-service, write the `place` method that saves the order and an outbox row in one transaction, plus a relay that publishes unpublished rows to `order-events` keyed by order id.

Hint: never call `KafkaTemplate` inside the business transaction.

<details><summary>Solution</summary>

```java
package com.orderhub.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderPlacement {
    private final JdbcClient jdbc;
    private final ObjectMapper json;
    private final KafkaTemplate<String, String> kafka;

    public OrderPlacement(JdbcClient jdbc, ObjectMapper json, KafkaTemplate<String, String> kafka) {
        this.jdbc = jdbc;
        this.json = json;
        this.kafka = kafka;
    }

    @Transactional
    public long place(long customerId, List<Map<String, Object>> lines) throws Exception {
        long orderId = jdbc.sql("insert into orders(customer_id, status) values (?, 'PENDING') returning id")
                           .param(customerId).query(Long.class).single();
        String payload = json.writeValueAsString(Map.of(
                "eventId", UUID.randomUUID().toString(), "orderId", orderId,
                "customerId", customerId, "lines", lines));
        jdbc.sql("insert into outbox_event(id, aggregate_type, aggregate_id, event_type, payload) "
                 + "values (?, 'Order', ?, 'OrderPlaced', cast(? as jsonb))")
            .params(UUID.randomUUID(), String.valueOf(orderId), payload).update();
        return orderId;                                   // both rows commit together
    }

    record Pending(UUID id, String aggregateId, String eventType, String payload) { }

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void relay() throws Exception {
        List<Pending> batch = jdbc.sql("""
                select id, aggregate_id, event_type, payload::text as payload from outbox_event
                where published_at is null order by created_at limit 100 for update skip locked
                """).query(Pending.class).list();
        for (Pending p : batch) {
            var record = new org.apache.kafka.clients.producer.ProducerRecord<>("order-events", p.aggregateId(), p.payload());
            record.headers().add("eventType", p.eventType().getBytes());
            kafka.send(record).get(5, TimeUnit.SECONDS);
            jdbc.sql("update outbox_event set published_at = now() where id = ?").param(p.id()).update();
        }
    }
}
```
If the application crashes after `send` but before the `update`, the row is published again on restart. That is the accepted at-least-once trade-off; consumers deduplicate by `eventId`.
</details>

## 8. Idempotent notification consumer with a Testcontainers test
notification-service consumes `order-events`, sends an email for `OrderConfirmed`, and ignores duplicates. Write an integration test with a real Kafka container that publishes the same event twice and verifies one email.

Hint: `@ServiceConnection` on a `KafkaContainer` wires `spring.kafka.bootstrap-servers` automatically (Boot 3.1+).

<details><summary>Solution</summary>

```java
package com.orderhub.notification;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class ConfirmedListener {
    private final Set<String> processed = ConcurrentHashMap.newKeySet();   // a DB table in production
    private final EmailSender email;

    ConfirmedListener(EmailSender email) { this.email = email; }

    @KafkaListener(topics = "order-events", groupId = "notification-service")
    void on(ConsumerRecord<String, String> record) {
        String eventId = new String(record.headers().lastHeader("eventId").value());
        if (!processed.add(eventId)) return;                                // duplicate
        email.send(Long.parseLong(record.key()), "Order " + record.key() + " confirmed");
    }
}
```

```java
package com.orderhub.notification;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

@SpringBootTest(properties = "spring.kafka.consumer.auto-offset-reset=earliest")
@Testcontainers
class ConfirmedListenerIT {

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer("apache/kafka:3.9.0");

    @Autowired KafkaTemplate<String, String> template;
    @MockitoBean EmailSender email;

    @Test
    void duplicateEventSendsOneEmail() {
        for (int i = 0; i < 2; i++) {
            var record = new ProducerRecord<>("order-events", "42", "{}");
            record.headers().add("eventId", "evt-1".getBytes());
            template.send(record);
        }
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() ->
                verify(email, times(1)).send(eq(42L), anyString()));
        await().during(Duration.ofSeconds(2)).untilAsserted(() ->
                verify(email, times(1)).send(eq(42L), anyString()));   // still one after a pause
    }
}
```
The second `await().during(Duration.ofSeconds(2))` proves the duplicate did not cause a second email, not merely that the first one arrived.
</details>

## 9. Saga reply handler with compensation
order-service listens to `inventory-events`. On `InventoryReserved` it confirms the order; on `InventoryRejected` it cancels it. Both must be idempotent and ignore events for orders not in `PENDING`.

Hint: the status check is the idempotency guard; a conditional update makes it atomic.

<details><summary>Solution</summary>

```java
package com.orderhub.order.saga;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class InventoryReplyHandler {
    private static final Logger log = LoggerFactory.getLogger(InventoryReplyHandler.class);
    private final JdbcClient jdbc;
    private final ObjectMapper json;

    InventoryReplyHandler(JdbcClient jdbc, ObjectMapper json) {
        this.jdbc = jdbc;
        this.json = json;
    }

    @KafkaListener(topics = "inventory-events", groupId = "order-service")
    @Transactional
    void on(ConsumerRecord<String, String> record) throws Exception {
        String type = new String(record.headers().lastHeader("eventType").value());
        JsonNode event = json.readTree(record.value());
        long orderId = event.get("orderId").asLong();

        String newStatus = switch (type) {
            case "InventoryReserved" -> "CONFIRMED";
            case "InventoryRejected" -> "CANCELLED";
            default -> null;
        };
        if (newStatus == null) return;

        int changed = jdbc.sql("update orders set status = ? where id = ? and status = 'PENDING'")
                          .params(newStatus, orderId).update();          // atomic guard
        if (changed == 0) {
            log.info("Ignoring {} for order {}: not pending", type, orderId);
            return;
        }
        String outType = newStatus.equals("CONFIRMED") ? "OrderConfirmed" : "OrderCancelled";
        jdbc.sql("insert into outbox_event(id, aggregate_type, aggregate_id, event_type, payload) "
                 + "values (gen_random_uuid(), 'Order', ?, ?, cast(? as jsonb))")
            .params(String.valueOf(orderId), outType, record.value()).update();
        log.info("Order {} -> {}", orderId, newStatus);
    }
}
```
The `WHERE status = 'PENDING'` makes the transition happen once even if two replies race or a reply is redelivered; the outbox row for the next saga step is written in the same transaction.
</details>

## 10. Fix: remote call inside a database transaction
This order-service code holds a database connection while waiting for inventory, and under load the Hikari pool runs dry. Refactor it.

```java
@Transactional
public Order place(PlaceOrderCommand cmd) {
    Order order = orders.save(Order.pending(cmd));
    inventory.reserve(order.getId(), cmd.lines());   // HTTP call, up to 2 s
    order.confirm();
    return order;
}
```

Hint: keep transactions short and do network I/O outside them, or switch to the asynchronous saga.

<details><summary>Solution</summary>

```java
package com.orderhub.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class PlaceOrderUseCase {
    private final OrderRepository orders;
    private final ResilientInventory inventory;
    private final TransactionTemplate tx;

    public PlaceOrderUseCase(OrderRepository orders, ResilientInventory inventory, TransactionTemplate tx) {
        this.orders = orders;
        this.inventory = inventory;
        this.tx = tx;
    }

    public Order place(PlaceOrderCommand cmd) {
        // Tx 1: short, only the insert
        Order order = tx.execute(status -> orders.save(Order.pending(cmd)));

        try {
            inventory.reserve(order.getId(), cmd.lines());          // no DB connection held here
        } catch (RuntimeException e) {
            tx.executeWithoutResult(status -> orders.findById(order.getId()).orElseThrow().cancel("INVENTORY_FAILED"));
            throw e;
        }

        // Tx 2: short, only the status change
        return tx.execute(status -> {
            Order o = orders.findById(order.getId()).orElseThrow();
            o.confirm();
            return o;
        });
    }
}
```
With 20 pool connections and 2-second inventory calls, the old version could serve at most 10 orders per second before every request queued for a connection. The new version holds a connection for milliseconds. If the process crashes between the two transactions, the order stays `PENDING`; a scheduled job (or the asynchronous saga from task 7 and 9) resolves stuck orders.
</details>
