@@ 3 | TOPIC | SecurityFilterChain Configuration | The Spring Security 6 lambda DSL, rule ordering, session policy, JSON 401/403 handlers and Boot defaults | MUST
# SecurityFilterChain Configuration

**Level: Intermediate | Phase 14 | Priority: MUST | Since: Spring Security 5.4 (SecurityFilterChain bean); lambda DSL 5.2; WebSecurityConfigurerAdapter removed 6.0**

> **30-second interview answer:** In Spring Security 6 you configure security by declaring a `SecurityFilterChain` `@Bean` built from `HttpSecurity` with the lambda DSL: `authorizeHttpRequests` for URL rules (evaluated top to bottom, first match wins), an authentication mechanism such as `oauth2ResourceServer`, `httpBasic` or `formLogin`, `sessionManagement` for stateful or stateless, and `csrf`, `cors`, `headers` and `exceptionHandling` for the rest. The old `WebSecurityConfigurerAdapter` is gone. If you declare no chain, Spring Boot creates a default one that secures everything with form login and HTTP Basic.

## 1. Concept in Simple Words
### Analogy
The rule book at a nightclub door.

| Nightclub door | SecurityFilterChain configuration |
|---|---|
| The rule book the bouncer reads top to bottom, stopping at the first rule that fits | `authorizeHttpRequests` with ordered `requestMatchers` |
| "Bar area: everyone", "VIP lounge: VIP wristband", "Everything else: must have a ticket" | `permitAll()`, `hasRole("VIP")`, `anyRequest().authenticated()` |
| How guests prove they paid: ticket scan or wristband | Authentication mechanism (`oauth2ResourceServer`, `httpBasic`) |
| Hand stamp that lets you come back in without paying again | Session (`JSESSIONID`); stateless means no stamp, show your ticket each time |
| What the bouncer says when he turns you away | `exceptionHandling` (entry point and access denied handler) |
| The club's standard house rules if the owner wrote nothing | Spring Boot's default chain |

### What it is
- **`HttpSecurity`:** a builder, prototype-scoped per chain, that collects configurers and produces a `DefaultSecurityFilterChain`.
- **Lambda DSL:** each feature takes a `Customizer<T>` lambda, for example `.csrf(csrf -> csrf.disable())` or `.httpBasic(Customizer.withDefaults())`. Since 6.1 the non-lambda methods and `.and()` are deprecated; in `Boot 4` (Spring Security 7) they are removed.
- **`authorizeHttpRequests`:** builds the `AuthorizationFilter` and its `RequestMatcherDelegatingAuthorizationManager`. Rules: `permitAll`, `denyAll`, `authenticated`, `anonymous`, `hasRole`, `hasAnyRole`, `hasAuthority`, `hasAnyAuthority`, `access(AuthorizationManager)`.
- **`requestMatchers()`:** chooses requests by path pattern and optionally HTTP method. With Spring MVC on the classpath, patterns are matched the same way MVC matches them.
- **`securityMatcher()`:** decides whether this whole chain applies (different from `requestMatchers`, which choose rules inside a chain).
- **`SessionCreationPolicy`:** `IF_REQUIRED` (default), `ALWAYS`, `NEVER` (use one if it exists), `STATELESS` (never create or use a session for security).
- **`AuthenticationEntryPoint` / `AccessDeniedHandler`:** produce the 401 and 403 responses.

### Why it exists
The adapter style hid configuration in overridden methods and made multiple configurations awkward. A bean-based configuration is plain Spring: you can have several chains, inject other beans, test them, and compose them with `@Order`. The lambda DSL makes the scope of each setting obvious and removes the confusing `.and()` chains where it was unclear which feature a method belonged to.

## 2. How It Works
```mermaid
flowchart TD
    A["@Bean SecurityFilterChain(HttpSecurity http)"] --> B["Configurers registered: authorize, csrf, cors, oauth2ResourceServer"]
    B --> C["http.build()"]
    C --> D["Each configurer init() then configure(): adds its filter"]
    D --> E["Filters sorted by FilterOrderRegistration"]
    E --> F["DefaultSecurityFilterChain(matcher, filters)"]
    F --> G["FilterChainProxy holds all chains"]
```

Rule evaluation inside `AuthorizationFilter`:
```text
Request: DELETE /api/products/7, user alice [ROLE_CUSTOMER]

1. requestMatchers("/api/auth/**").permitAll()                 no match
2. requestMatchers(GET, "/api/products/**").permitAll()        no match (method is DELETE)
3. requestMatchers("/api/products/**").hasRole("ADMIN")         MATCH -> alice lacks ROLE_ADMIN -> DENY
4. requestMatchers("/api/admin/**").hasRole("ADMIN")            never evaluated
5. anyRequest().authenticated()                                 never evaluated

Result: AccessDeniedException -> ExceptionTranslationFilter -> AccessDeniedHandler -> 403
```
First match wins, so put the most specific rules first and the catch-all last. Spring Security 6 throws at startup if `anyRequest()` is not the last rule.

What Spring Boot auto-configures:
- `SecurityAutoConfiguration` imports `SpringBootWebSecurityConfiguration`, which creates a default chain (`anyRequest().authenticated()`, `formLogin()`, `httpBasic()`) **only if you have not declared any `SecurityFilterChain` bean**. Your bean replaces it completely; nothing is merged.
- `SecurityFilterAutoConfiguration` registers `DelegatingFilterProxy` with the servlet container.
- `UserDetailsServiceAutoConfiguration` creates the generated in-memory user only if no `UserDetailsService`, `AuthenticationProvider`, `AuthenticationManager` or OAuth2 resource server configuration exists.

## 3. Code Example
### Code
The OrderHub base configuration that the JWT topic will complete: URL rules, stateless sessions, JSON error bodies in RFC 9457 Problem Details format (Phase 11), security headers, and a custom `AuthorizationManager`.

```java
package com.orderhub.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/** Writes 401 and 403 as application/problem+json, matching the API's other errors. */
@Component
public class ProblemDetailsSecurityHandlers {

    private final ObjectMapper mapper;

    public ProblemDetailsSecurityHandlers(ObjectMapper mapper) { this.mapper = mapper; }

    public AuthenticationEntryPoint entryPoint() {
        return (request, response, ex) ->
            write(response, HttpStatus.UNAUTHORIZED, "Authentication required", request.getRequestURI());
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) ->
            write(response, HttpStatus.FORBIDDEN, "You do not have permission for this action", request.getRequestURI());
    }

    private void write(HttpServletResponse response, HttpStatus status, String detail, String path) throws IOException {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setInstance(URI.create(path));
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), pd);
    }
}
```

```java
package com.orderhub.security;

import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http, ProblemDetailsSecurityHandlers handlers) throws Exception {
        http
            .securityMatcher("/api/**", "/error")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()                                   // let error responses through
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers("/api/products/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/customers/{id}/**").access(sameCustomerOrAdmin())
                .anyRequest().authenticated())                                           // deny by default
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())                  // no cookies are used for auth in this API
            .cors(Customizer.withDefaults())               // uses the CorsConfigurationSource bean
            .headers(h -> h
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'; frame-ancestors 'none'"))
                .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)))
            .exceptionHandling(e -> e
                .authenticationEntryPoint(handlers.entryPoint())
                .accessDeniedHandler(handlers.accessDeniedHandler()))
            .httpBasic(Customizer.withDefaults());         // replaced by oauth2ResourceServer in the JWT topic
        return http.build();
    }

    /** /api/customers/{id}/** is allowed for ADMIN or for the customer whose id is in the path. */
    private AuthorizationManager<RequestAuthorizationContext> sameCustomerOrAdmin() {
        return (Supplier<Authentication> authentication, RequestAuthorizationContext ctx) -> {
            Authentication auth = authentication.get();
            boolean admin = auth.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (admin) return new AuthorizationDecision(true);
            String pathId = ctx.getVariables().get("id");
            boolean owner = auth.getPrincipal() instanceof com.orderhub.user.OrderHubUserDetails u
                            && u.customerId() != null
                            && u.customerId().toString().equals(pathId);
            return new AuthorizationDecision(owner);
        };
    }
}
```

```yaml
# application.yml
logging:
  level:
    org.springframework.security.web.access: DEBUG   # logs every authorization decision
```

### Walkthrough
- `securityMatcher("/api/**", "/error")` scopes the chain; a separate chain could serve `/actuator/**`.
- Rules are ordered from specific to general. `GET /api/products/**` is public, but any other method on products falls to the next line and needs `ADMIN`. If the `hasRole("ADMIN")` line came first, browsing products would require admin.
- `"/api/customers/{id}/**"` uses a path variable; `RequestAuthorizationContext.getVariables()` exposes it to the custom `AuthorizationManager`. This is URL-level ownership; [[Method Security with @PreAuthorize]] shows the more common method-level way.
- `STATELESS` stops Spring Security from creating or reading an `HttpSession`, so every request must carry credentials.
- CSRF is disabled because authentication will use the `Authorization` header, which a malicious site cannot make a browser send automatically.
- Custom headers add a strict Content Security Policy and no referrer. Default headers (`X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Cache-Control: no-cache, no-store`) stay on.
- The handlers return Problem Details JSON instead of Spring's default empty 401 or HTML pages, which frontends can parse.

### Output
```http
DELETE /api/products/7
Authorization: Basic YWxpY2U6YWxpY2UtcGFzcw==

HTTP/1.1 403 Forbidden
Content-Type: application/problem+json
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Security-Policy: default-src 'none'; frame-ancestors 'none'
Referrer-Policy: no-referrer

{"type":"about:blank","title":"Forbidden","status":403,"detail":"You do not have permission for this action","instance":"/api/products/7"}
```
```http
GET /api/orders

HTTP/1.1 401 Unauthorized
Content-Type: application/problem+json

{"type":"about:blank","title":"Unauthorized","status":401,"detail":"Authentication required","instance":"/api/orders"}
```
DEBUG log for the 403:
```text
o.s.s.w.a.AuthorizationFilter : Authorizing DELETE /api/products/7
o.s.s.w.a.i.RequestMatcherDelegatingAuthorizationManager : Checking authorization on DELETE /api/products/7 using AuthorityAuthorizationManager[authorities=[ROLE_ADMIN]]
o.s.s.w.a.ExceptionTranslationFilter : Sending UsernamePasswordAuthenticationToken [Principal=alice] to access denied handler since access is denied
```

### Compare
```java
// OLD: Spring Security 5.x, removed in 6.0. Do not write this in an interview.
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class OldConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/products/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf().disable();
    }
}
```
| Old | New |
|---|---|
| Extend `WebSecurityConfigurerAdapter` | Declare `SecurityFilterChain` `@Bean` |
| Override `configure(AuthenticationManagerBuilder)` | Declare `UserDetailsService` and `PasswordEncoder` beans |
| `authorizeRequests` + `antMatchers` / `mvcMatchers` | `authorizeHttpRequests` + `requestMatchers` |
| `.and()` between features | One lambda per feature |
| `web.ignoring()` to skip static files | `permitAll()` rules (ignoring also drops headers and context) |

## 4. Interview Questions
**Basic**
1. **How do you configure Spring Security in Spring Boot 3?**
   Declare a `SecurityFilterChain` bean that takes `HttpSecurity`, configure it with the lambda DSL and return `http.build()`. Users come from a `UserDetailsService` bean and passwords from a `PasswordEncoder` bean. There is no adapter class to extend.
2. **What happens if you add the security starter and configure nothing?**
   Boot creates a default chain: every request requires authentication, with form login and HTTP Basic, plus a generated in-memory user `user` whose password is printed at startup. CSRF and security headers are enabled.
3. **In what order are `requestMatchers` rules evaluated?**
   Top to bottom, and the first matching rule decides. Put specific rules before general ones and finish with `anyRequest()`, which must be last or startup fails.

**Follow-up**
4. **What is the difference between `securityMatcher` and `requestMatchers`?**
   `securityMatcher` decides whether the whole chain applies to a request; it is how multiple chains split the URL space. `requestMatchers` inside `authorizeHttpRequests` choose which authorization rule applies within that chain.
5. **What does `SessionCreationPolicy.STATELESS` do and when do you use it?**
   Spring Security neither creates nor uses an `HttpSession` to store the security context, so each request must authenticate itself, typically with a bearer token. Use it for token-based REST APIs; use the default for browser apps with form login.
6. **How do you return JSON instead of the default 401 and 403 responses?**
   Configure `exceptionHandling` with a custom `AuthenticationEntryPoint` and `AccessDeniedHandler` that write a body, ideally a `ProblemDetail`. A `@ControllerAdvice` will not catch these, because the exceptions happen in filters before the `DispatcherServlet`.

**Tricky / scenario**
7. **Why does `@ControllerAdvice` not handle `AccessDeniedException` from URL rules, but does from `@PreAuthorize`?**
   URL rules are enforced by `AuthorizationFilter`, outside Spring MVC, so only `ExceptionTranslationFilter` sees the exception. `@PreAuthorize` throws inside the controller or service call, which is within MVC, so an `@ExceptionHandler(AccessDeniedException.class)` would catch it first. If you handle it there, rethrow or return 403 deliberately, or let it propagate so Spring Security's handler is used consistently.
8. **Someone uses `web.ignoring().requestMatchers("/api/public/**")`. What is the risk?**
   Ignored paths bypass the entire security filter chain: no security headers, no CSRF, no `SecurityContext`, no firewall. Spring logs a warning recommending `permitAll()` instead. Use ignoring only for truly static resources, if at all.
9. **After adding your own `SecurityFilterChain`, the H2 console and Swagger UI stop working. Why?**
   Your bean replaced Boot's default chain, so those paths are now covered by your `anyRequest().authenticated()`. Add explicit `permitAll()` rules for `/swagger-ui/**` and `/v3/api-docs/**` (and frame options `sameOrigin` plus CSRF ignore for the H2 console in development only).

**What the interviewer is really testing:** whether you write modern configuration, understand ordering and statelessness, and know which parts happen in filters versus MVC.

## 5. Pitfalls & Best Practices
### Common mistakes
| Mistake | What goes wrong | Fix |
|---|---|---|
| Catch-all rule before specific rules | Specific rules never apply | Specific first, `anyRequest()` last |
| Forgetting HTTP method in a public rule | `POST /api/products` becomes public too | Use `requestMatchers(HttpMethod.GET, "/api/products/**")` |
| Expecting `@ControllerAdvice` to format filter errors | Default empty 401 or 403 | Custom entry point and access denied handler |
| Using `web.ignoring()` for API paths | No headers or context on those paths | `permitAll()` |
| Ending with `anyRequest().permitAll()` | New endpoints are public by accident | Deny by default with `authenticated()` |
| Copying old `antMatchers` and adapter code | Compilation errors on Boot 3 | Lambda DSL and `requestMatchers` |

### Best practices
- One `SecurityConfig` per chain with a clear `securityMatcher`; keep rules readable and grouped by resource.
- Deny by default and write a MockMvc test per rule; security rules are code and regress like code.
- Keep URL rules coarse (public, authenticated, admin area) and push business rules ("own orders only") to method security.
- Return consistent error bodies; never leak stack traces or which rule failed.

### Compare with
| Configuration style | Status | Note |
|---|---|---|
| `WebSecurityConfigurerAdapter` | Removed in 6.0 | Interview red flag if written today |
| `SecurityFilterChain` bean, non-lambda | Deprecated since 6.1 | Works on Boot 3, removed in `Boot 4` |
| `SecurityFilterChain` bean, lambda DSL | Current | Use this |
| Kotlin DSL `http { }` | Current | Same features for Kotlin projects |
| XML namespace `<http>` | Legacy but supported | Old enterprise apps |

## 6. Hands-on Exercise
Write OrderHub's rules for these requirements and a test class that proves them:
- `POST /api/auth/**` is public, `GET /api/products/**` is public.
- Creating, updating and deleting products requires `ADMIN`.
- `GET /api/admin/reports/**` requires `ADMIN` or `REPORTING`.
- `/actuator/health` is public, other actuator endpoints need `ADMIN`.
- Anything else requires authentication; 401 and 403 are Problem Details JSON.

Acceptance criteria: tests with `@WithMockUser` cover at least one allowed and one denied case per rule.

<details><summary>Solution</summary>

```java
package com.orderhub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class OrderHubRules {

    @Bean
    SecurityFilterChain rules(HttpSecurity http, ProblemDetailsSecurityHandlers handlers) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers("/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/reports/**").hasAnyRole("ADMIN", "REPORTING")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(e -> e.authenticationEntryPoint(handlers.entryPoint())
                                     .accessDeniedHandler(handlers.accessDeniedHandler()))
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
```

```java
package com.orderhub.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderHubRulesTest {

    @Autowired MockMvc mvc;

    @Test void productsArePublic() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isOk());
    }

    @Test @WithMockUser(roles = "CUSTOMER")
    void customerCannotDeleteProduct() throws Exception {
        mvc.perform(delete("/api/products/1"))
           .andExpect(status().isForbidden())
           .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test @WithMockUser(roles = "ADMIN")
    void adminCanDeleteProduct() throws Exception {
        mvc.perform(delete("/api/products/1")).andExpect(status().isNoContent());
    }

    @Test @WithMockUser(roles = "REPORTING")
    void reportingCanReadReports() throws Exception {
        mvc.perform(get("/api/admin/reports/daily")).andExpect(status().isOk());
    }

    @Test @WithMockUser(roles = "REPORTING")
    void reportingCannotUseOtherAdminEndpoints() throws Exception {
        mvc.perform(get("/api/admin/users")).andExpect(status().isForbidden());
    }

    @Test void healthIsPublic() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test @WithMockUser(roles = "CUSTOMER")
    void metricsNeedAdmin() throws Exception {
        mvc.perform(get("/actuator/metrics")).andExpect(status().isForbidden());
    }

    @Test void anonymousGetsProblemJson401() throws Exception {
        mvc.perform(get("/api/orders"))
           .andExpect(status().isUnauthorized())
           .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
}
```
`@WithMockUser(roles = "ADMIN")` puts an authenticated user with `ROLE_ADMIN` into the test's security context without any login. The order of the `/api/admin/reports/**` rule before `/api/admin/**` is what lets `REPORTING` in.
</details>

## 7. Recall
Q: How is Spring Security configured in Spring Boot 3 without WebSecurityConfigurerAdapter?
A: By declaring a `SecurityFilterChain` `@Bean` built from `HttpSecurity` with the lambda DSL and returning `http.build()`.

Q: In what order does authorizeHttpRequests evaluate requestMatchers rules?
A: Top to bottom; the first matching rule decides, and `anyRequest()` must be last.

Q: What is the difference between securityMatcher and requestMatchers in Spring Security?
A: `securityMatcher` selects whether a whole chain applies; `requestMatchers` select an authorization rule within the chain.

Q: What does SessionCreationPolicy.STATELESS do in Spring Security?
A: It stops Spring Security from creating or using an HTTP session for the security context, so each request must carry credentials.

Q: Why can't @ControllerAdvice format 401 responses from Spring Security URL rules?
A: They are produced in the filter chain before the `DispatcherServlet`; use a custom `AuthenticationEntryPoint` instead.

Q: What does Spring Boot do when you declare your own SecurityFilterChain bean?
A: It backs off its default chain entirely; your chain is the only configuration.

**Related:** [[Security Filter Chain Architecture]] · [[JWT Stateless Authentication]] · [[CSRF, CORS and Common Vulnerabilities]]

@@ 2 | MODULE | Modern auth | Tokens, delegated login, method-level rules and browser attacks | MUST
# Modern auth

**Shared analogy: a festival wristband system.** At a big festival you show your ticket once at the gate and get a tamper-proof wristband (a JWT): every stage checks the wristband instead of calling the ticket office. Some people buy through a ticket partner instead of the festival itself, and the festival trusts the partner's stamp (OAuth2 and OpenID Connect). Inside, some areas check more than the wristband colour: the artist lounge checks that your name is on that night's list (method security with `@PreAuthorize`). And the festival must stop tricksters who make you unknowingly sign things (CSRF) and decide which partner sites may sell its tickets (CORS). This group turns OrderHub into a token-based API that can also trust an external identity provider.

@@ 3 | TOPIC | JWT Stateless Authentication | Token structure, signing, issuing on login, validating with the resource server, expiry, refresh and revocation | MUST
# JWT Stateless Authentication

**Level: Intermediate | Phase 14 | Priority: MUST | Since: JWT RFC 7519 (2015); Spring Security OAuth2 resource server 5.1, JwtEncoder 5.6**

> **30-second interview answer:** A JWT is a signed JSON token made of three Base64URL parts: header, payload with claims (subject, roles, expiry) and signature. On login the server verifies the password once and issues a short-lived token; the client sends it as `Authorization: Bearer <token>` on every request, and the server validates the signature and expiry without any session or database lookup. That makes the API stateless and easy to scale, at the cost of hard revocation, so you keep access tokens short (5 to 15 minutes) and use refresh tokens. In Spring Boot 3 you validate tokens with `oauth2ResourceServer().jwt()` instead of writing your own filter.

## 1. Concept in Simple Words
### Analogy
A concert wristband with a hologram.

| Wristband | JWT |
|---|---|
| You show your ID and ticket once at the gate | Login with username and password |
| You get a wristband printed with your name and area access | Token with claims `sub`, `roles` |
| The hologram only the organiser can make | Signature with the server's secret or private key |
| Any guard can check the hologram without phoning the box office | Signature validation without a database lookup |
| The wristband's colour changes each day, yesterday's is void | `exp` claim (expiry) |
| Anyone can read what is printed on it | The payload is only Base64URL-encoded, not encrypted |
| Confiscating a wristband before it expires is hard: every guard would need a list | Revocation needs a denylist or short expiry |

### What it is
- **JWT (JSON Web Token):** `base64url(header).base64url(payload).base64url(signature)`.
- **Header:** `{"alg":"HS256","typ":"JWT"}` or `{"alg":"RS256","kid":"key-1"}`. `kid` identifies which key signed it.
- **Claims:** registered claims `iss` (issuer), `sub` (subject, the user), `aud` (audience, who the token is for), `exp` (expiry, seconds since epoch), `iat` (issued at), `nbf` (not before), `jti` (unique id); plus custom claims like `roles` or `cid`.
- **JWS:** a signed JWT (what everyone means by "JWT"). **JWE** is an encrypted JWT, rarely needed.
- **HS256:** HMAC with SHA-256 and one shared secret (at least 256 bits). The same key signs and verifies, so every verifier can also forge tokens.
- **RS256 / ES256:** asymmetric. The auth server signs with a private key; services verify with the public key, published as a **JWKS** (JSON Web Key Set) endpoint.
- **Bearer token:** whoever holds it can use it, like cash. Always over HTTPS.
- **Access token vs refresh token:** short-lived token sent to APIs vs long-lived token sent only to the auth endpoint to get new access tokens.
- **Resource server:** Spring Security's term for an API that accepts bearer tokens.

### Why it exists
Server-side sessions store state in server memory or a shared store; with ten instances behind a load balancer you need sticky sessions or Redis, and mobile clients and other services do not handle cookies well. A self-contained signed token moves the state to the client: any instance can verify it with only a key. That suits microservices, where a gateway and several services must all recognise the user.

## 2. How It Works
```mermaid
sequenceDiagram
    participant C as "Client (SPA or mobile)"
    participant L as "POST /api/auth/login"
    participant AM as "AuthenticationManager"
    participant TS as "TokenService (JwtEncoder)"
    participant BF as "BearerTokenAuthenticationFilter"
    participant JD as "JwtDecoder"
    participant API as "OrderController"
    C->>L: "username and password"
    L->>AM: "authenticate()"
    AM-->>L: "Authentication with ROLE_CUSTOMER"
    L->>TS: "issue token"
    TS-->>C: "access token (15 min)"
    C->>BF: "GET /api/orders with Authorization: Bearer token"
    BF->>JD: "decode(token): verify signature, exp, iss"
    JD-->>BF: "Jwt claims"
    BF->>BF: "JwtAuthenticationConverter: roles claim to ROLE_ authorities"
    BF->>API: "SecurityContext holds JwtAuthenticationToken"
    API-->>C: "200 orders"
```

Validation steps inside `NimbusJwtDecoder` (in order):
1. Split into three parts and parse the header. Reject `alg: none` and any algorithm other than the configured one (prevents algorithm confusion attacks).
2. Recompute the signature over `header.payload` with the key and compare.
3. Run `OAuth2TokenValidator`s: `exp` and `nbf` with 60 seconds of clock skew by default, `iss` if configured, `aud` if you add a validator.
4. On any failure, throw `JwtException`, which `BearerTokenAuthenticationFilter` turns into 401 with `WWW-Authenticate: Bearer error="invalid_token"`.

What the token physically looks like, built with only the JDK (to understand that there is no magic):

```java
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class JwtByHand {

    static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();

    static String sign(String data, byte[] key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return B64.encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public static void main(String[] args) throws Exception {
        byte[] key = "orderhub-demo-secret-key-32-bytes!".getBytes(StandardCharsets.UTF_8);
        String header = B64.encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = B64.encodeToString(
            "{\"sub\":\"alice\",\"roles\":[\"CUSTOMER\"],\"exp\":1767225600}".getBytes(StandardCharsets.UTF_8));
        String token = header + "." + payload + "." + sign(header + "." + payload, key);
        System.out.println("token parts: " + token.split("\\.").length);
        System.out.println("header:  " + header);
        System.out.println("payload decoded: " + new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8));

        // Verification = recompute the signature and compare
        String[] p = token.split("\\.");
        System.out.println("valid signature: " + sign(p[0] + "." + p[1], key).equals(p[2]));

        // Tampering: change the role in the payload, keep the old signature
        String forged = B64.encodeToString(
            "{\"sub\":\"alice\",\"roles\":[\"ADMIN\"],\"exp\":1767225600}".getBytes(StandardCharsets.UTF_8));
        System.out.println("forged accepted: " + sign(p[0] + "." + forged, key).equals(p[2]));
    }
}
```
```text
token parts: 3
header:  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
payload decoded: {"sub":"alice","roles":["CUSTOMER"],"exp":1767225600}
valid signature: true
forged accepted: false
```
Anyone can decode the payload, but nobody without the key can change it. (Real code compares signatures with `MessageDigest.isEqual` for constant time; Spring's decoder does.)

## 3. Code Example
### Code
OrderHub's complete JWT setup using Spring's own OAuth2 resource server support with an HS256 key: login endpoint, token issuing, validation and role mapping. No third-party JWT library and no hand-written filter.

```xml
<!-- pom.xml: brings BearerTokenAuthenticationFilter, NimbusJwtDecoder and NimbusJwtEncoder -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

```yaml
# application.yml
orderhub:
  jwt:
    secret: ${JWT_SECRET}          # Base64 of at least 32 random bytes: openssl rand -base64 32
    issuer: orderhub
    access-token-ttl: 15m
```

```java
package com.orderhub.security;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "orderhub.jwt")
public record JwtProperties(String secret, String issuer, Duration accessTokenTtl) { }
```

```java
package com.orderhub.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    SecretKey jwtKey(JwtProperties props) {
        byte[] bytes = Base64.getDecoder().decode(props.secret());
        if (bytes.length < 32) throw new IllegalStateException("JWT secret must be at least 256 bits");
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey key) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey key, JwtProperties props) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer(props.issuer())));     // exp, nbf, iss
        return decoder;
    }

    /** Maps the "roles" claim ["CUSTOMER"] to authorities [ROLE_CUSTOMER]. */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }

    /** Exposes the AuthenticationManager built from JpaUserDetailsService and the PasswordEncoder. */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

```java
package com.orderhub.security;

import java.time.Instant;
import java.util.List;
import com.orderhub.user.OrderHubUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtProperties props;

    public TokenService(JwtEncoder encoder, JwtProperties props) {
        this.encoder = encoder; this.props = props;
    }

    public String issue(Authentication auth) {
        Instant now = Instant.now();
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring("ROLE_".length()))
                .toList();
        OrderHubUserDetails user = (OrderHubUserDetails) auth.getPrincipal();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(props.issuer())
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(props.accessTokenTtl()))
                .claim("roles", roles);
        if (user.customerId() != null) claims.claim("cid", user.customerId());   // used for ownership checks
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }
}
```

```java
package com.orderhub.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    record LoginRequest(@NotBlank String username, @NotBlank String password) { }
    record TokenResponse(String accessToken, String tokenType, long expiresIn) { }

    private final AuthenticationManager authenticationManager;
    private final TokenService tokens;
    private final JwtProperties props;

    public AuthController(AuthenticationManager am, TokenService tokens, JwtProperties props) {
        this.authenticationManager = am; this.tokens = tokens; this.props = props;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        // Throws BadCredentialsException (mapped to 401) if the password is wrong
        Authentication auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(req.username(), req.password()));
        return new TokenResponse(tokens.issue(auth), "Bearer", props.accessTokenTtl().toSeconds());
    }
}
```

```java
package com.orderhub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain api(HttpSecurity http, JwtAuthenticationConverter converter,
                            ProblemDetailsSecurityHandlers handlers) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers("/api/products/**", "/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(rs -> rs
                .jwt(jwt -> jwt.jwtAuthenticationConverter(converter))
                .authenticationEntryPoint(handlers.entryPoint()))
            .exceptionHandling(e -> e.accessDeniedHandler(handlers.accessDeniedHandler()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .build();
    }
}
```

```java
package com.orderhub.order;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyOrdersController {

    private final OrderService orders;

    public MyOrdersController(OrderService orders) { this.orders = orders; }

    @GetMapping("/api/orders/mine")
    public List<OrderSummary> mine(@AuthenticationPrincipal Jwt jwt) {
        Long customerId = jwt.getClaim("cid");          // identity comes from the token, never from the request
        return orders.findByCustomer(customerId);
    }
}
```

### Walkthrough
- **Dependency:** the resource server starter adds `BearerTokenAuthenticationFilter` and the Nimbus JOSE library that does the cryptography.
- **`JwtConfig`:** one `SecretKey` built from a Base64 environment variable; `NimbusJwtEncoder` signs, `NimbusJwtDecoder` verifies and validates `exp`, `nbf` and `iss`. The converter maps the `roles` claim to `ROLE_` authorities (by default Spring reads `scope` or `scp` and prefixes `SCOPE_`).
- **`AuthenticationManager` bean:** exposes the manager that Spring already builds from `JpaUserDetailsService` and the `PasswordEncoder`, so the login endpoint reuses `DaoAuthenticationProvider` with BCrypt and user-enumeration protection.
- **`TokenService`:** builds claims (issuer, subject, issued at, expiry, roles, customer id) and signs with HS256. The header must name the algorithm because the encoder supports several.
- **`AuthController`:** checks the password once and returns the token. A `BadCredentialsException` becomes 401 via the global handler.
- **`SecurityConfig`:** `oauth2ResourceServer(rs -> rs.jwt())` switches authentication to bearer tokens. Sessions are stateless and CSRF is off because nothing relies on cookies.
- **Controller:** `@AuthenticationPrincipal Jwt jwt` injects the validated token; the customer id comes from the signed `cid` claim.

### Output
```http
POST /api/auth/login
Content-Type: application/json

{"username":"alice","password":"alice-pass-123"}

HTTP/1.1 200 OK
Content-Type: application/json

{"accessToken":"eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJvcmRlcmh1YiIsInN1YiI6ImFsaWNlIiwiZXhwIjoxNzY0MDAwOTAwLCJpYXQiOjE3NjQwMDAwMDAsInJvbGVzIjpbIkNVU1RPTUVSIl0sImNpZCI6N30.kq2y8Zg3R2bPZ1ZsVq4V0fLmQ9pGd8xYb1nW0aT3c5E","tokenType":"Bearer","expiresIn":900}
```
Decoded payload of that token:
```json
{"iss":"orderhub","sub":"alice","exp":1764000900,"iat":1764000000,"roles":["CUSTOMER"],"cid":7}
```
```http
GET /api/orders/mine
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3Mi(rest of token)

HTTP/1.1 200 OK
[{"id":101,"status":"PAID","total":59.90}]
```
```http
GET /api/orders/mine
Authorization: Bearer (same token, 16 minutes later)

HTTP/1.1 401 Unauthorized
WWW-Authenticate: Bearer error="invalid_token", error_description="An error occurred while attempting to decode the Jwt: Jwt expired at 2025-11-24T16:15:00Z", error_uri="https://tools.ietf.org/html/rfc6750#section-3.1"
```
```http
DELETE /api/products/3
Authorization: Bearer (alice's valid token)

HTTP/1.1 403 Forbidden
Content-Type: application/problem+json
{"type":"about:blank","title":"Forbidden","status":403,"detail":"You do not have permission for this action","instance":"/api/products/3"}
```

### Compare
| Hand-written `OncePerRequestFilter` with a JWT library | Spring `oauth2ResourceServer().jwt()` |
|---|---|
| You parse the header, catch every exception type, build the token | Built-in `BearerTokenAuthenticationFilter` |
| Easy to forget `alg` checks, clock skew, issuer, audience | Validators for `exp`, `nbf`, `iss`; algorithm pinned |
| Custom 401 format, often missing `WWW-Authenticate` | RFC 6750 compliant errors |
| Switching to RS256 or Keycloak means rewriting | Change one property: `issuer-uri` or `jwk-set-uri` |
| Very common in tutorials and interviews | Recommended by the Spring team |

## 4. Interview Questions
**Basic**
1. **What are the three parts of a JWT?**
   Header (algorithm and key id), payload (claims such as `sub`, `exp`, `roles`) and signature, each Base64URL-encoded and joined with dots. The signature covers the header and payload, so changing either invalidates it.
2. **Is the payload of a JWT encrypted?**
   No, it is only encoded; anyone can decode it. Never put secrets, passwords or sensitive personal data in it. If you need confidentiality, use JWE or keep the data server-side.
3. **Why is JWT authentication called stateless?**
   The server stores nothing per user: every request carries a self-contained token that any instance can verify with a key. There is no session store, so horizontal scaling needs no sticky sessions or shared session cache.

**Follow-up**
4. **HS256 or RS256?**
   HS256 uses one shared secret for signing and verifying, fine when the same service issues and checks tokens. RS256 or ES256 sign with a private key and verify with a public key, so many services can verify without being able to forge tokens; that is the standard for an auth server plus microservices, with public keys served via JWKS.
5. **How do you log out or revoke a JWT?**
   You cannot "delete" a self-contained token, so: keep access tokens short-lived (5 to 15 minutes), revoke the refresh token on logout, and if instant revocation is required keep a denylist of `jti` values (in Redis, until their `exp`) or a per-user token version checked on each request. The latter reintroduces some state, which is the trade-off.
6. **Where should a browser app store the token?**
   `localStorage` is readable by any script, so an XSS bug steals tokens. Safer options are keeping the access token in memory and the refresh token in an `HttpOnly`, `Secure`, `SameSite` cookie, or using a backend-for-frontend that keeps tokens server-side and gives the browser a session cookie. Cookie-based storage brings back the need for CSRF protection.

**Tricky / scenario**
7. **A user's role was changed from ADMIN to CUSTOMER, but they can still delete products. Why?**
   Their existing access token still contains `roles: ["ADMIN"]` and stays valid until it expires, because the server does not look up roles on each request. Short expiry limits the window; for critical changes, revoke refresh tokens and use a token-version check or denylist.
8. **What is the `alg: none` attack and how does Spring prevent it?**
   An attacker sends a token whose header says no signature is used, hoping the library accepts it unsigned. Another variant switches RS256 to HS256 and signs with the public key as the HMAC secret. Spring's `NimbusJwtDecoder` is configured with one expected algorithm and key, so tokens with any other `alg` are rejected.
9. **Tokens fail with "Jwt expired" only on one server, seconds after issue. What do you check?**
   Clock drift: the server's clock is ahead of the issuer's. Fix NTP synchronisation; Spring's default validator already allows 60 seconds of skew, which you can adjust with `JwtTimestampValidator`.

**What the interviewer is really testing:** whether you understand what a JWT really guarantees (integrity, not secrecy), the revocation trade-off, and that you would use the framework's validated decoder instead of fragile custom parsing.

## 5. Pitfalls & Best Practices
### Common mistakes
| Mistake | What goes wrong | Fix |
|---|---|---|
| Weak or hard-coded HS256 secret like "secret" | Tokens can be brute-forced and forged | At least 256 random bits from a secret store |
| Access tokens valid for days | Stolen or outdated tokens work for days | 5 to 15 minutes plus refresh tokens |
| Sensitive data in claims | Anyone with the token reads it | Only ids and roles |
| Not validating `iss` and `aud` | Tokens for another app are accepted | Add issuer and audience validators |
| Custom filter that swallows exceptions and continues | Invalid tokens treated as anonymous, confusing 403s | Use the resource server or return 401 explicitly |
| Storing tokens in `localStorage` | XSS steals them | Memory plus `HttpOnly` refresh cookie, or a BFF |

### Best practices
- Prefer an identity provider (Keycloak, Auth0, Okta, Cognito) issuing RS256 tokens; let services only validate (see [[OAuth2 and OpenID Connect]]).
- If you issue tokens yourself, reuse `AuthenticationManager` for the password check and Spring's `JwtEncoder` for signing.
- Rotate keys using `kid` headers and a JWKS with old and new keys during the overlap.
- Rotate refresh tokens on every use and detect reuse (a reused refresh token signals theft: revoke the family).
- Rate-limit `/api/auth/login` and log failures.

### Compare with
| Approach | State | Revocation | Best for |
|---|---|---|---|
| Server session (`JSESSIONID`) | Server-side, needs sticky sessions or Spring Session | Instant (invalidate session) | Server-rendered web apps, BFFs |
| JWT access token | None on server | Hard; short expiry | APIs, mobile, microservices |
| Opaque token plus introspection | Auth server stores tokens | Instant | High-security APIs, needs a call per request (cache it) |
| API key | Key table | Instant | Machine clients, partner integrations |

## 6. Hands-on Exercise
Add refresh tokens to OrderHub. `POST /api/auth/login` returns an access token (15 minutes) and a refresh token (an opaque random string stored hashed in a `refresh_token` table with user id and expiry of 7 days). `POST /api/auth/refresh` with a valid refresh token returns a new access token and a **new** refresh token, and deletes the old one. `POST /api/auth/logout` deletes the refresh token.

Acceptance criteria:
- Using a refresh token twice fails the second time with 401.
- Refresh tokens are stored as SHA-256 hashes, never raw (they are long random values, so a fast hash is fine here, unlike passwords).
- A logged-out refresh token cannot be used.

<details><summary>Solution</summary>

```sql
-- V8__refresh_tokens.sql
CREATE TABLE refresh_token (
    token_hash  VARCHAR(64) PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES app_user(id),
    expires_at  TIMESTAMPTZ NOT NULL
);
```

```java
package com.orderhub.security;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Entity(name = "refresh_token")
class RefreshToken {
    @Id String tokenHash;
    Long userId;
    Instant expiresAt;
    protected RefreshToken() { }
    RefreshToken(String tokenHash, Long userId, Instant expiresAt) {
        this.tokenHash = tokenHash; this.userId = userId; this.expiresAt = expiresAt;
    }
}

interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> { }

@Service
public class RefreshTokenService {
    private static final Duration TTL = Duration.ofDays(7);
    private final SecureRandom random = new SecureRandom();
    private final RefreshTokenRepository repo;

    public RefreshTokenService(RefreshTokenRepository repo) { this.repo = repo; }

    @Transactional
    public String create(Long userId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        repo.save(new RefreshToken(hash(raw), userId, Instant.now().plus(TTL)));
        return raw;                                   // only the client ever sees the raw value
    }

    /** Consumes the token (one-time use) and returns the user id. */
    @Transactional
    public Long consume(String raw) {
        RefreshToken token = repo.findById(hash(raw))
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        repo.delete(token);                           // rotation: old token is gone
        if (token.expiresAt.isBefore(Instant.now())) throw new BadCredentialsException("Refresh token expired");
        return token.userId;
    }

    @Transactional
    public void revoke(String raw) { repo.deleteById(hash(raw)); }

    static String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
```

```java
package com.orderhub.security;

import com.orderhub.user.JpaUserDetailsService;
import com.orderhub.user.OrderHubUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
class RefreshController {
    record RefreshRequest(String refreshToken) { }
    record Tokens(String accessToken, String refreshToken) { }

    private final RefreshTokenService refreshTokens;
    private final TokenService tokens;
    private final JpaUserDetailsService users;

    RefreshController(RefreshTokenService r, TokenService t, JpaUserDetailsService u) {
        this.refreshTokens = r; this.tokens = t; this.users = u;
    }

    @PostMapping("/refresh")
    Tokens refresh(@RequestBody RefreshRequest req) {
        Long userId = refreshTokens.consume(req.refreshToken());
        OrderHubUserDetails user = users.loadById(userId);          // fresh roles from the database
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
        return new Tokens(tokens.issue(auth), refreshTokens.create(userId));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void logout(@RequestBody RefreshRequest req) {
        refreshTokens.revoke(req.refreshToken());
    }
}
```
Add `loadById(Long id)` to `JpaUserDetailsService` (same mapping as `loadUserByUsername`), permit `POST /api/auth/refresh` in the security config, and make the login endpoint also return `refreshTokens.create(user.id())`. Because roles are reloaded on refresh, a demoted admin loses admin rights within one access-token lifetime. `BadCredentialsException` maps to 401 in the global error handler.
</details>

## 7. Recall
Q: What are the three parts of a JWT and how are they joined?
A: Header, payload (claims) and signature, each Base64URL-encoded and joined with dots.

Q: Is a JWT payload encrypted?
A: No, a signed JWT is only encoded; anyone can read it, but only the key holder can change it without breaking the signature.

Q: How does Spring Boot 3 validate incoming JWTs without a custom filter?
A: With `oauth2ResourceServer(rs -> rs.jwt())`, which adds `BearerTokenAuthenticationFilter` and a `JwtDecoder` that checks signature, expiry and issuer.

Q: What is the difference between HS256 and RS256 for JWT signing?
A: HS256 uses one shared secret for signing and verifying; RS256 signs with a private key and verifies with a public key, so verifiers cannot forge tokens.

Q: How do you handle JWT logout or revocation?
A: Short-lived access tokens, revocable rotating refresh tokens, and if needed a denylist of token ids or a token-version check.

Q: Which claim does Spring's JwtGrantedAuthoritiesConverter read by default and what prefix does it add?
A: The `scope` or `scp` claim, with the `SCOPE_` prefix.

**Related:** [[SecurityFilterChain Configuration]] · [[OAuth2 and OpenID Connect]] · [[UserDetailsService and Password Encoding]]
