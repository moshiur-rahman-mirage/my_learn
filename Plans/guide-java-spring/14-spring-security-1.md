@@ 1 | MODULE | Phase 14 — Spring Security (Intermediate) | Authentication, authorization, the filter chain, password storage, JWT, OAuth2, method security, CSRF and CORS | MUST
# Phase 14: Spring Security (Intermediate)

Spring Security is the framework that decides **who you are** (authentication) and **what you may do** (authorization) for every request that reaches a Spring application. It does this with a chain of servlet filters that run before your controllers. Almost every backend interview has at least one security question, because a developer who cannot explain how a request gets authenticated cannot be trusted with production APIs. Interviewers also use security to test whether you understand Spring internals: filters, proxies, `ThreadLocal` context and auto-configuration all show up here.

In this phase OrderHub gains real security: users with BCrypt passwords stored in PostgreSQL, a `/api/auth/login` endpoint that issues a signed JWT, stateless request authentication with Spring's OAuth2 resource server support, role-based URL rules (`CUSTOMER`, `ADMIN`), method security with `@PreAuthorize` (a customer can only read their own orders), CORS for the frontend and correct CSRF settings for a token-based API. Everything uses the **Spring Security 6 lambda DSL** with a `SecurityFilterChain` bean. `WebSecurityConfigurerAdapter` was deprecated in 5.7 and removed in 6.0; if an interviewer shows you code that extends it, say so.

```mermaid
flowchart TD
    A["Authentication vs Authorization"] --> B["Security Filter Chain Architecture"]
    B --> C["UserDetailsService and Password Encoding"]
    B --> D["SecurityFilterChain Configuration"]
    C --> E["JWT Stateless Authentication"]
    D --> E
    E --> F["OAuth2 and OpenID Connect"]
    D --> G["Method Security with @PreAuthorize"]
    D --> H["CSRF, CORS and Common Vulnerabilities"]
    E --> I["Practice: Secure OrderHub"]
    G --> I
    H --> I
```

**Prerequisites:** Phase 10 (beans, proxies, configuration classes), Phase 11 (REST controllers, global error handling), Phase 12 (JPA entities and repositories for the `users` table), Phase 13 (MockMvc tests, which you will extend with `spring-security-test`). From Phase 9, remember that a `ThreadLocal` is a per-thread variable: the security context lives in one.

**The 5 questions from this phase that come up most often**
1. What is the difference between authentication and authorization, and between HTTP 401 and 403?
2. Walk me through what happens inside Spring Security when a request arrives (DelegatingFilterProxy, FilterChainProxy, filters, SecurityContextHolder).
3. How do you store passwords, and why BCrypt instead of SHA-256?
4. How does JWT authentication work, what is inside a token, and how do you log a user out or revoke a token?
5. Should you disable CSRF for a REST API, and how is CORS different from CSRF?

@@ 2 | MODULE | Architecture | Who you are, what you may do, and the filter machinery that decides it | MUST
# Architecture

**Shared analogy: an office building with a security desk.** At the entrance there is a row of checkpoints (the filter chain). One guard checks your badge or ID card and writes your name on a visitor slip (authentication, the `SecurityContext`). The receptionist looks up who you are in the staff register and compares your signature (the `UserDetailsService` and `PasswordEncoder`). Further in, each floor has a sign saying who may enter (authorization rules in the `SecurityFilterChain` configuration). If you have no badge you are sent back to the front desk (401); if your badge is valid but you try the executive floor, you are turned away (403). This group covers the building plan: the difference between identity and permission, the checkpoints, the staff register, and how you write the rules.

@@ 3 | TOPIC | Authentication vs Authorization | Identity versus permission, 401 versus 403, principals, credentials, authorities and roles | MUST
# Authentication vs Authorization

**Level: Intermediate | Phase 14 | Priority: MUST | Since: Spring Security 1.0 (Acegi, 2004); current model Spring Security 6.x with Boot 3**

> **30-second interview answer:** Authentication answers "who are you?" by verifying credentials such as a password or a signed token, and produces an authenticated principal. Authorization answers "what are you allowed to do?" by checking that principal's authorities against the rules for a URL, method or object. Authentication always comes first. A failed authentication returns HTTP 401 Unauthorized; an authenticated user who lacks permission gets HTTP 403 Forbidden.

## 1. Concept in Simple Words
### Analogy
Boarding a plane.

| Airport | Spring Security |
|---|---|
| Showing your passport at the check-in desk | Authentication: proving identity with credentials |
| The passport itself | Credentials (password, token, certificate) |
| Your name printed on the boarding pass | The principal (the authenticated user) |
| "Business class, gate B12" printed on the pass | Granted authorities (`ROLE_ADMIN`, `SCOPE_orders.read`) |
| The gate agent checking your class before letting you into the lounge | Authorization |
| No passport: you cannot even get a boarding pass | 401 Unauthorized |
| Valid pass, but economy trying to enter the business lounge | 403 Forbidden |

### What it is
- **Authentication (AuthN):** verifying an identity claim. Input: credentials. Output: an authenticated `Authentication` object.
- **Authorization (AuthZ):** deciding whether an authenticated (or anonymous) identity may perform an action on a resource.
- **Principal:** the "who". In Spring it is usually a `UserDetails` object or, for JWT, a `Jwt`.
- **Credentials:** the proof, such as the password. Spring erases the password from the `Authentication` after successful login (`eraseCredentialsAfterAuthentication`).
- **GrantedAuthority:** a string permission attached to the principal, such as `ROLE_ADMIN` or `orders:write`.
- **Role:** by convention an authority with the `ROLE_` prefix. `hasRole("ADMIN")` checks for the authority `ROLE_ADMIN`; `hasAuthority("ROLE_ADMIN")` checks the exact string.
- **`Authentication` interface:** Spring's container for principal, credentials, authorities and an `isAuthenticated()` flag. Before login it holds the raw request (for example `UsernamePasswordAuthenticationToken` with a username and password); after login it holds the loaded user and authorities.
- **`SecurityContextHolder`:** where the current `Authentication` lives for the duration of the request, backed by a `ThreadLocal` by default.

### Why it exists
Without a clear split, applications mix "is this a real user?" with "can this user delete orders?" in every controller, and one forgotten `if` exposes data. Separating the two lets one component verify identity in one place (a filter), and lets declarative rules decide access everywhere else. It also explains the two HTTP codes: clients react differently to "log in again" (401) and "you will never be allowed" (403).

## 2. How It Works
```mermaid
sequenceDiagram
    participant C as "Client"
    participant AF as "Authentication filter"
    participant AM as "AuthenticationManager"
    participant SC as "SecurityContextHolder"
    participant AZ as "AuthorizationFilter"
    participant API as "Controller"
    C->>AF: "Request with credentials"
    AF->>AM: "authenticate(unauthenticated token)"
    AM-->>AF: "authenticated token with authorities"
    AF->>SC: "store Authentication"
    AF->>AZ: "continue chain"
    AZ->>SC: "read Authentication"
    AZ->>AZ: "check rule for URL (hasRole ADMIN?)"
    alt "allowed"
        AZ->>API: "invoke controller"
    else "not authenticated"
        AZ-->>C: "401 via AuthenticationEntryPoint"
    else "authenticated but denied"
        AZ-->>C: "403 via AccessDeniedHandler"
    end
```

Step by step:
1. An authentication filter (form login, HTTP Basic, bearer token) extracts credentials and builds an **unauthenticated** `Authentication`.
2. The `AuthenticationManager` (usually `ProviderManager`) asks its `AuthenticationProvider`s to verify it. Success returns a new, **authenticated** token with authorities. Failure throws an `AuthenticationException`.
3. The filter stores the result in the `SecurityContext`, held by `SecurityContextHolder`.
4. If no filter authenticated the request, `AnonymousAuthenticationFilter` stores an `AnonymousAuthenticationToken` so that rules like `permitAll()` still have something to evaluate.
5. `AuthorizationFilter` (last in the chain) asks an `AuthorizationManager` whether the current `Authentication` may access this request.
6. If access is denied, `ExceptionTranslationFilter` decides the response: anonymous user means "start authentication" through the `AuthenticationEntryPoint` (401 or a redirect to the login page); authenticated user means the `AccessDeniedHandler` (403).

```text
Authentication object before and after
+----------------------------------------+      +-----------------------------------------------+
| UsernamePasswordAuthenticationToken    |      | UsernamePasswordAuthenticationToken           |
| principal   = "alice"   (String)       | ---> | principal   = UserDetails(alice)              |
| credentials = "s3cret"                 |      | credentials = null  (erased)                  |
| authorities = []                       |      | authorities = [ROLE_CUSTOMER]                 |
| authenticated = false                  |      | authenticated = true                          |
+----------------------------------------+      +-----------------------------------------------+
```

## 3. Code Example
### Code
A tiny OrderHub-style app with two in-memory users and HTTP Basic, to see 200, 401 and 403 side by side.

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

```java
package com.orderhub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class DemoSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()   // anyone
                .requestMatchers("/api/admin/**").hasRole("ADMIN")                 // authorization rule
                .anyRequest().authenticated())                                      // must be logged in
            .httpBasic(Customizer.withDefaults())                                   // authentication mechanism
            .csrf(csrf -> csrf.disable());                                          // stateless demo API
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();          // {bcrypt} by default
    }

    @Bean
    UserDetailsService users(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
            User.withUsername("alice").password(encoder.encode("alice-pass")).roles("CUSTOMER").build(),
            User.withUsername("admin").password(encoder.encode("admin-pass")).roles("ADMIN").build());
    }
}
```

```java
package com.orderhub.web;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WhoAmIController {

    @GetMapping("/api/products")
    List<String> products() {
        return List.of("Keyboard", "Mouse");
    }

    @GetMapping("/api/me")
    Map<String, Object> me(Authentication authentication) {        // Spring injects the current Authentication
        return Map.of(
            "name", authentication.getName(),
            "authorities", authentication.getAuthorities().stream()
                                          .map(GrantedAuthority::getAuthority).toList());
    }

    @GetMapping("/api/admin/stats")
    Map<String, Integer> stats() {
        return Map.of("orders", 42);
    }
}
```

### Walkthrough
- `httpBasic()` registers `BasicAuthenticationFilter`, which reads the `Authorization: Basic base64(user:pass)` header. That is the **authentication** part.
- `authorizeHttpRequests()` registers `AuthorizationFilter` with rules evaluated top to bottom; the first match wins. That is the **authorization** part.
- `roles("ADMIN")` stores the authority `ROLE_ADMIN`; `hasRole("ADMIN")` adds the prefix when checking.
- The controller method takes an `Authentication` parameter; Spring MVC resolves it from the `SecurityContextHolder`.
- CSRF is disabled only because this demo is a stateless API called with Basic credentials; a browser app with session cookies must keep it (see [[CSRF, CORS and Common Vulnerabilities]]).

### Output
```http
GET /api/products
-> 200 OK
["Keyboard","Mouse"]

GET /api/me
-> 401 Unauthorized
WWW-Authenticate: Basic realm="Realm"

GET /api/me
Authorization: Basic YWxpY2U6YWxpY2UtcGFzcw==
-> 200 OK
{"name":"alice","authorities":["ROLE_CUSTOMER"]}

GET /api/admin/stats
Authorization: Basic YWxpY2U6YWxpY2UtcGFzcw==
-> 403 Forbidden

GET /api/admin/stats
Authorization: Basic YWRtaW46YWRtaW4tcGFzcw==
-> 200 OK
{"orders":42}
```
(`YWxpY2U6YWxpY2UtcGFzcw==` is Base64 of `alice:alice-pass`; Base64 is encoding, not encryption, which is why Basic auth must only travel over HTTPS.)

### Compare
| Aspect | Authentication | Authorization |
|---|---|---|
| Question | Who are you? | What may you do? |
| Input | Credentials (password, token, certificate) | Authenticated principal plus rule |
| Spring component | `AuthenticationManager`, `AuthenticationProvider` | `AuthorizationManager`, `@PreAuthorize` |
| Failure exception | `AuthenticationException` | `AccessDeniedException` |
| HTTP status | 401 Unauthorized (really "unauthenticated") | 403 Forbidden |
| When | Once per login or once per request (stateless) | On every protected access |

## 4. Interview Questions
**Basic**
1. **What is the difference between authentication and authorization?**
   Authentication verifies identity, for example by checking a password or a token signature. Authorization decides whether that identity may perform an action. Authentication comes first, because you cannot evaluate permissions for an unknown user.
2. **What is the difference between 401 and 403?**
   401 means the request has no valid credentials, so the client should authenticate (the name "Unauthorized" is historically misleading; it means unauthenticated). 403 means the server knows who you are and still refuses. Spring produces 401 through the `AuthenticationEntryPoint` and 403 through the `AccessDeniedHandler`.
3. **What is the difference between `hasRole("ADMIN")` and `hasAuthority("ADMIN")`?**
   `hasRole` prefixes `ROLE_`, so it checks for `ROLE_ADMIN`. `hasAuthority` checks the exact string. If your authorities come from a JWT as `SCOPE_orders.read`, use `hasAuthority("SCOPE_orders.read")`.

**Follow-up**
4. **Where does Spring keep the authenticated user during a request?**
   In the `SecurityContext` inside `SecurityContextHolder`, which uses a `ThreadLocal` by default (`MODE_THREADLOCAL`). Any code on the request thread can call `SecurityContextHolder.getContext().getAuthentication()`. The context is cleared when the request finishes.
5. **What happens to an anonymous request that hits a `permitAll()` endpoint?**
   `AnonymousAuthenticationFilter` puts an `AnonymousAuthenticationToken` with `ROLE_ANONYMOUS` in the context, and `permitAll()` grants access. So `authentication` is not null, but `isAnonymous()` is true; injecting `Authentication` into the controller gives null for anonymous users though, because Spring MVC resolves anonymous tokens to null.
6. **What is a role versus a permission?**
   A role groups permissions by job (`ADMIN`, `CUSTOMER`); a permission (fine-grained authority) is a single action (`orders:refund`). Spring treats both as `GrantedAuthority` strings; the `ROLE_` prefix is the only difference. Large systems map roles to permissions and check permissions in code, so a role change does not require a code change.

**Tricky / scenario**
7. **A user logs in successfully but every request returns 403. What do you check?**
   First, whether the authorities are what you think: a role stored as `ADMIN` but checked with `hasAuthority("ROLE_ADMIN")`, or stored as `ROLE_ADMIN` and created with `.roles("ROLE_ADMIN")` (which throws, because the prefix is added for you). Second, rule order: a broad `requestMatchers("/api/**").hasRole("ADMIN")` above a narrower rule wins first. Third, CSRF: a POST without a CSRF token also returns 403 when CSRF is enabled.
8. **Your async `@Async` method calls `SecurityContextHolder.getContext().getAuthentication()` and gets null. Why?**
   The context is in a `ThreadLocal` on the request thread, and the async method runs on another thread. Wrap the executor with `DelegatingSecurityContextAsyncTaskExecutor` (or `DelegatingSecurityContextExecutorService`), or pass the needed data explicitly. `MODE_INHERITABLETHREADLOCAL` exists but is unsafe with thread pools because pooled threads keep stale contexts.

**What the interviewer is really testing:** whether you know the vocabulary precisely (principal, authority, role, 401 vs 403) and where identity lives at runtime, not just how to paste a config.

## 5. Pitfalls & Best Practices
### Common mistakes
| Mistake | What goes wrong | Fix |
|---|---|---|
| Returning 403 for missing credentials | Clients do not know they should log in again | Let the entry point return 401; reserve 403 for denied access |
| Mixing `hasRole` and `hasAuthority` with or without prefix | Users with the right role are denied | Decide a convention and use `hasRole("X")` for `ROLE_X` |
| Checking permissions with `if` statements in controllers | One forgotten check exposes data | Declarative URL rules plus `@PreAuthorize` |
| Reading the security context on another thread | Null authentication in async code | Delegating security executors or pass the user id |
| Trusting a user id sent in the request body | Users act on other users' data | Take identity from the `Authentication`, never from input |

### Best practices
- Authenticate once at the edge (filter), authorize everywhere it matters (URL rules for coarse access, method security for business rules).
- Deny by default: end your rules with `anyRequest().authenticated()` or `denyAll()`.
- Keep authorities small and meaningful; prefer permissions for fine control and roles for grouping.
- Log authentication failures (with user name, not password) and alert on bursts.

### Compare with
| Mechanism | Authentication style | Typical use |
|---|---|---|
| HTTP Basic | Username and password on every request | Internal tools, quick demos, service accounts over TLS |
| Form login plus session | Password once, then `JSESSIONID` cookie | Server-rendered web apps |
| Bearer JWT | Signed token on every request | SPAs, mobile apps, microservices |
| OAuth2 login (OIDC) | Redirect to an identity provider | "Log in with Google", corporate SSO |
| Mutual TLS (X.509) | Client certificate | Service-to-service in zero-trust networks |

## 6. Hands-on Exercise
Add a first security layer to OrderHub. Create `com.orderhub.security.SecurityConfig` with two in-memory users (`alice` with role `CUSTOMER`, `admin` with role `ADMIN`), HTTP Basic, and rules: `GET /api/products/**` is public, `POST/PUT/DELETE /api/products/**` needs `ADMIN`, everything else needs authentication. Add `GET /api/me` returning the name and authorities.

Acceptance criteria:
- Anonymous `GET /api/products` returns 200; anonymous `GET /api/me` returns 401.
- `alice` calling `POST /api/products` gets 403; `admin` gets 201.
- A MockMvc test proves each of the three outcomes.

<details><summary>Solution</summary>

```java
package com.orderhub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers("/api/products/**").hasRole("ADMIN")   // POST, PUT, DELETE fall here
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService users(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
            User.withUsername("alice").password(encoder.encode("alice-pass")).roles("CUSTOMER").build(),
            User.withUsername("admin").password(encoder.encode("admin-pass")).roles("ADMIN").build());
    }
}
```

```java
package com.orderhub.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityRulesTest {

    @Autowired MockMvc mvc;

    @Test
    void anonymousCanBrowseProducts() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isOk());
    }

    @Test
    void anonymousCannotSeeMe() throws Exception {
        mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void customerCannotCreateProduct() throws Exception {
        mvc.perform(post("/api/products").with(httpBasic("alice", "alice-pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Lamp\",\"price\":20.00}"))
           .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateProduct() throws Exception {
        mvc.perform(post("/api/products").with(httpBasic("admin", "admin-pass"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Lamp\",\"price\":20.00}"))
           .andExpect(status().isCreated());
    }
}
```
Add `spring-security-test` with `test` scope to the pom. The rule order matters: the `GET` rule is more specific, so it sits above the catch-all `ADMIN` rule for `/api/products/**`.
</details>

## 7. Recall
Q: What is the difference between authentication and authorization in Spring Security?
A: Authentication verifies who the caller is and produces an authenticated principal; authorization decides whether that principal may access a resource.

Q: In Spring Security, which HTTP status means "not authenticated" and which means "authenticated but not allowed"?
A: 401 Unauthorized for missing or invalid credentials, 403 Forbidden for denied access.

Q: What does `hasRole("ADMIN")` check in Spring Security?
A: That the user has the authority string `ROLE_ADMIN`; `hasRole` adds the `ROLE_` prefix.

Q: Where does Spring Security store the current Authentication during a request?
A: In the `SecurityContext` held by `SecurityContextHolder`, backed by a `ThreadLocal` by default.

Q: Which Spring Security components turn a denied request into 401 or 403?
A: `ExceptionTranslationFilter` calls the `AuthenticationEntryPoint` for anonymous users (401) and the `AccessDeniedHandler` for authenticated users (403).

**Related:** [[Security Filter Chain Architecture]] · [[UserDetailsService and Password Encoding]] · [[Method Security with @PreAuthorize]]

@@ 3 | TOPIC | Security Filter Chain Architecture | DelegatingFilterProxy, FilterChainProxy, filter order, SecurityContextHolder and exception translation | MUST
# Security Filter Chain Architecture

**Level: Intermediate | Phase 14 | Priority: MUST | Since: Spring Security 3.1 (FilterChainProxy with multiple chains); SecurityContextHolderFilter and AuthorizationFilter Spring Security 5.7/6.0**

> **30-second interview answer:** Spring Security is a chain of servlet filters. The servlet container calls one filter, `DelegatingFilterProxy`, which hands the request to the Spring bean `FilterChainProxy`. That bean picks the first `SecurityFilterChain` whose matcher fits the request and runs its filters in a fixed order: load the security context, CORS, CSRF, logout, an authentication filter, anonymous, exception translation, and finally `AuthorizationFilter`. Only if all of them pass does the request reach the `DispatcherServlet` and your controller.

## 1. Concept in Simple Words
### Analogy
An airport security lane.

| Airport security | Spring Security |
|---|---|
| The airport building has one entrance door for everyone | The servlet container's filter chain |
| A sign at the door: "security handled by the airport authority" | `DelegatingFilterProxy`, a container filter that delegates to a Spring bean |
| The airport authority's control room choosing a lane: crew lane or passenger lane | `FilterChainProxy` choosing a `SecurityFilterChain` by request matcher |
| The checkpoints in the lane, always in the same order: ID check, bag scan, body scanner, gate | Security filters in their fixed order |
| The ID officer stamps your card | An authentication filter populating the `SecurityContext` |
| The supervisor who decides "back to check-in" or "you are banned" | `ExceptionTranslationFilter` (401 or 403) |
| The final gate agent | `AuthorizationFilter` |
| The plane | `DispatcherServlet` and your controller |

### What it is
- **Servlet filter:** a `jakarta.servlet.Filter` that sees every request before the servlet and can stop it or pass it on with `chain.doFilter(request, response)`.
- **`DelegatingFilterProxy`:** a plain servlet filter registered with the container (Spring Boot registers it as `springSecurityFilterChain`). The container knows nothing about Spring beans, so this proxy looks up the bean lazily and delegates.
- **`FilterChainProxy`:** the Spring bean that holds a list of `SecurityFilterChain`s, picks one per request, runs its filters, and clears the security context afterwards. It is also the place to put a breakpoint when debugging.
- **`SecurityFilterChain`:** a request matcher plus an ordered list of filters. You declare it as a `@Bean` built with `HttpSecurity`.
- **`SecurityContextHolder`:** the static holder of the current `SecurityContext` (by default a `ThreadLocal`).
- **`SecurityContextRepository`:** where the context is saved between requests: `HttpSessionSecurityContextRepository` for session apps, nothing for stateless JWT apps.

### Why it exists
Security must run before any controller, apply uniformly to every URL (including static files and error pages), and stay out of business code. Servlet filters are the natural place. But filters registered with the container cannot use dependency injection, so Spring adds one proxy filter that bridges into the Spring world, where filters are beans that can use services, encoders and repositories. Multiple chains let one app treat `/api/**` (stateless, JWT) differently from `/admin/**` (form login, session) or `/actuator/**`.

## 2. How It Works
```mermaid
flowchart LR
    R["HTTP request"] --> T["Tomcat filter chain"]
    T --> DFP["DelegatingFilterProxy"]
    DFP --> FCP["FilterChainProxy (bean)"]
    FCP -->|"matches /api/**"| C1["SecurityFilterChain 1"]
    FCP -->|"matches everything else"| C2["SecurityFilterChain 2"]
    C1 --> DS["DispatcherServlet"]
    C2 --> DS
    DS --> CTRL["@RestController"]
```

The important filters of one chain, in order (Spring Security 6.x; you only see the ones you enable):

```text
 #  Filter                                  Job
 1  DisableEncodeUrlFilter                  stop session ids leaking into URLs
 2  WebAsyncManagerIntegrationFilter        carry the context into async MVC
 3  SecurityContextHolderFilter             load SecurityContext from the repository
 4  HeaderWriterFilter                      X-Content-Type-Options, X-Frame-Options, HSTS, Cache-Control
 5  CorsFilter                              answer preflight, add CORS headers
 6  CsrfFilter                              reject state-changing requests without a valid token
 7  LogoutFilter                            handle POST /logout
 8  UsernamePasswordAuthenticationFilter    form login (POST /login)
 9  BearerTokenAuthenticationFilter         Authorization: Bearer <jwt> (resource server)
10  BasicAuthenticationFilter               Authorization: Basic
11  RequestCacheAwareFilter                 resume the saved request after login
12  SecurityContextHolderAwareRequestFilter request.isUserInRole(), getRemoteUser()
13  AnonymousAuthenticationFilter           put AnonymousAuthenticationToken if still empty
14  SessionManagementFilter / session stuff session fixation, concurrency control
15  ExceptionTranslationFilter              turn security exceptions into 401 or 403
16  AuthorizationFilter                     final access decision for the URL
```

What happens in memory and between threads:
1. Tomcat takes a worker thread from its pool and calls the filter chain.
2. `SecurityContextHolderFilter` asks the `SecurityContextRepository` for a context (from the HTTP session, or an empty one) and sets a deferred supplier in the `ThreadLocal`.
3. An authentication filter, if it recognizes credentials, calls the `AuthenticationManager` and on success creates a new `SecurityContext` containing the `Authentication`.
4. `ExceptionTranslationFilter` wraps the rest of the chain in a try/catch. When `AuthorizationFilter` throws `AccessDeniedException`, it catches it: anonymous user goes to the `AuthenticationEntryPoint`, otherwise to the `AccessDeniedHandler`.
5. After the response, `FilterChainProxy` clears the `ThreadLocal`. This is essential because the same Tomcat thread will serve another user next.

Since Spring Security 6, the context is **not** saved automatically to the session after authentication: authentication filters explicitly call `securityContextRepository.saveContext()`. That is why a custom login endpoint must save the context itself if you want a session, and why stateless JWT apps simply skip it.

Exception path:
```mermaid
sequenceDiagram
    participant ETF as "ExceptionTranslationFilter"
    participant AZF as "AuthorizationFilter"
    participant EP as "AuthenticationEntryPoint"
    participant ADH as "AccessDeniedHandler"
    ETF->>AZF: "chain.doFilter()"
    AZF-->>ETF: "throws AccessDeniedException"
    alt "Authentication is anonymous"
        ETF->>EP: "commence() returns 401 or redirect to /login"
    else "Authentication is real"
        ETF->>ADH: "handle() returns 403"
    end
```

## 3. Code Example
### Code
Two chains in OrderHub (actuator health public with its own chain, API secured), a custom logging filter inserted in the right place, and a debug switch to print the chain.

```java
package com.orderhub.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/** Logs who made each request. Runs after authentication so the user is known. */
public class AuditLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuditLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (auth == null) ? "none" : auth.getName();
        chain.doFilter(request, response);                       // let the rest of the chain run
        log.info("{} {} user={} status={}", request.getMethod(), request.getRequestURI(),
                 user, response.getStatus());
    }
}
```

```java
package com.orderhub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@EnableWebSecurity(debug = false)      // true prints every request and the filter list (never in prod)
public class ChainsConfig {

    @Bean
    @Order(1)                                         // checked first
    SecurityFilterChain actuatorChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/actuator/**")             // this chain only handles /actuator/**
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                .anyRequest().hasRole("OPS"))
            .httpBasic(Customizer.withDefaults())
            .build();
    }

    @Bean
    @Order(2)                                         // fallback chain: no securityMatcher = any request
    SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(new AuditLoggingFilter(), AuthorizationFilter.class)  // after auth, before access decision
            .build();
    }
}
```

```yaml
# application.yml: see the chain decisions in the log while learning
logging:
  level:
    org.springframework.security: TRACE
```

### Walkthrough
- `@Order(1)` with `securityMatcher("/actuator/**")` makes `FilterChainProxy` try the actuator chain first. Only one chain handles a request: the first whose matcher fits.
- The API chain has no `securityMatcher`, so it matches everything; it must have the highest order number. If you put a catch-all chain first, the second chain is never used (Spring Security 6 fails fast at startup when a chain matching any request is followed by another chain).
- `addFilterBefore(new AuditLoggingFilter(), AuthorizationFilter.class)` places the custom filter after authentication filters, so `auth.getName()` already has the user. Placing it before `SecurityContextHolderFilter` would always log `none`.
- The filter is created with `new`, not as a `@Component`. A `Filter` `@Component` is also auto-registered by Spring Boot in the **servlet container** chain, so it would run twice (once outside Spring Security, once inside). If you need it as a bean, add a `FilterRegistrationBean` with `setEnabled(false)`.
- `OncePerRequestFilter` guarantees one execution per request even with forwards and error dispatches.
- TRACE logging shows lines such as which chain was chosen and every filter invoked, the single best debugging tool for "why do I get 403?".

### Output
```text
GET /actuator/health   (no credentials)
-> 200 OK  {"status":"UP"}

GET /api/orders        (no credentials)
-> 401 Unauthorized

GET /api/orders        Authorization: Basic YWxpY2U6YWxpY2UtcGFzcw==
-> 200 OK
log: INFO  c.o.security.AuditLoggingFilter : GET /api/orders user=alice status=200

TRACE log excerpt for the authenticated request:
o.s.security.web.FilterChainProxy : Trying to match request against DefaultSecurityFilterChain [RequestMatcher=Or [Mvc [pattern='/actuator/**']], Filters=[...]] (1/2)
o.s.security.web.FilterChainProxy : Securing GET /api/orders
o.s.security.web.FilterChainProxy : Invoking SecurityContextHolderFilter (3/12)
o.s.security.web.FilterChainProxy : Invoking BasicAuthenticationFilter (6/12)
o.s.s.w.a.www.BasicAuthenticationFilter : Set SecurityContextHolder to UsernamePasswordAuthenticationToken [Principal=org.springframework.security.core.userdetails.User [Username=alice, ...]]
o.s.security.web.FilterChainProxy : Invoking AuthorizationFilter (12/12)
o.s.security.web.FilterChainProxy : Secured GET /api/orders
```
(Exact filter numbers depend on which features are enabled; the bracketed filter list in Spring's log line is abbreviated here.)

### Compare
| Old style (Spring Security 5.6 and earlier) | Modern style (6.x) |
|---|---|
| `class SecurityConfig extends WebSecurityConfigurerAdapter` | One or more `SecurityFilterChain` `@Bean`s |
| `antMatchers("/api/**")` | `requestMatchers("/api/**")` |
| `authorizeRequests()` with `FilterSecurityInterceptor` | `authorizeHttpRequests()` with `AuthorizationFilter` |
| `SecurityContextPersistenceFilter` saves automatically | `SecurityContextHolderFilter`; saving is explicit |
| `.and()` chaining | Lambda DSL, each feature in its own lambda (`.and()` removed in `Boot 4` / Security 7) |

## 4. Interview Questions
**Basic**
1. **What is `DelegatingFilterProxy` and why is it needed?**
   It is a standard servlet filter registered with the container that delegates to a Spring bean named `springSecurityFilterChain`. The container instantiates filters itself and cannot inject Spring beans, so the proxy bridges the two worlds and allows lazy lookup after the application context starts.
2. **What does `FilterChainProxy` do?**
   It holds all `SecurityFilterChain`s, selects the first one whose request matcher fits, runs that chain's filters, and clears the `SecurityContextHolder` at the end. It also applies the HTTP firewall (`StrictHttpFirewall`) that rejects suspicious URLs such as encoded slashes or path traversal.
3. **Name the main filters in order.**
   `SecurityContextHolderFilter`, `HeaderWriterFilter`, `CorsFilter`, `CsrfFilter`, `LogoutFilter`, the authentication filters (form, bearer, basic), `AnonymousAuthenticationFilter`, `ExceptionTranslationFilter`, and `AuthorizationFilter` last. CORS is before CSRF and authentication so preflight requests are answered without credentials.

**Follow-up**
4. **How do you add a custom filter and where?**
   Use `addFilterBefore`, `addFilterAfter` or `addFilterAt` relative to a known filter class. A filter that needs the authenticated user goes after the authentication filters; a filter that performs authentication (a custom API-key filter) goes before `UsernamePasswordAuthenticationFilter` or `AuthorizationFilter` and sets the context itself.
5. **Why can declaring a custom filter as a `@Component` be a problem?**
   Spring Boot registers every `Filter` bean with the servlet container, so it runs in the container chain and again inside the security chain where you added it. Either create it with `new` inside the config or disable container registration with a `FilterRegistrationBean`.
6. **How do multiple `SecurityFilterChain`s work?**
   Each chain has a `securityMatcher`; `FilterChainProxy` picks the first matching chain by `@Order`. Exactly one chain handles a request, so rules in a later chain are invisible for requests that an earlier chain matched.

**Tricky / scenario**
7. **Users intermittently see another user's data in a custom thread pool. What could be the cause?**
   Someone set the security context on a pooled thread (for example with `MODE_INHERITABLETHREADLOCAL` or a manual `setContext`) and never cleared it, so the next task on that thread runs as the previous user. `FilterChainProxy` only clears the request thread. Use `DelegatingSecurityContextExecutor`, which sets and clears around each task.
8. **A `POST /api/orders` returns 403 even for an admin, while `GET` works. Why?**
   Most likely `CsrfFilter`: CSRF protection is on by default and rejects state-changing requests without a token, with a 403 that looks like an authorization failure. TRACE logging shows "Invalid CSRF token found". For a stateless bearer-token API, disable CSRF; for a cookie session app, send the token.
9. **Does Spring Security protect error pages and `/error`?**
   Yes. Since 6.0 the filter chain also applies to `ERROR` and `FORWARD` dispatches. That is why an unauthenticated exception in a controller can turn into a 401 instead of the expected 500 body if `/error` is not permitted; many apps add `requestMatchers("/error").permitAll()`.

**What the interviewer is really testing:** whether you can trace a request through the container, the proxy and the filters, and debug 401/403 problems instead of guessing.

## 5. Pitfalls & Best Practices
### Common mistakes
| Mistake | What goes wrong | Fix |
|---|---|---|
| Custom filter as `@Component` and also added to the chain | Runs twice, once without security context | Instantiate in config or disable container registration |
| Catch-all chain with lower order than specific chains | Specific chain never runs | Specific chains get `securityMatcher` and lower `@Order` |
| Custom auth filter placed after `AuthorizationFilter` | Requests are rejected before your filter runs | Place it before the authorization step |
| Forgetting to clear a manually set context on pooled threads | User data leaks between requests | Use delegating executors or `try/finally` with `clearContext()` |
| Debugging 403 by changing rules randomly | Rules become insecure | Enable TRACE logging for `org.springframework.security` |

### Best practices
- Keep one chain per security style (stateless API, session web UI, actuator), each with an explicit `securityMatcher`.
- Use TRACE security logging in development; it prints the chosen chain and each filter.
- Extend `OncePerRequestFilter` for custom filters and keep them free of business logic.
- Remember that headers (`X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Cache-Control: no-store`) come for free from `HeaderWriterFilter`; do not disable them.

### Compare with
| Concept | Layer | Sees | Typical use |
|---|---|---|---|
| Servlet filter (Spring Security) | Servlet container, before `DispatcherServlet` | Every request, including static files and errors | Authentication, CORS, CSRF, headers |
| `HandlerInterceptor` | Spring MVC, around handler methods | Only requests mapped to handlers | Logging, locale, request timing |
| `@PreAuthorize` (AOP) | Bean method call | Method arguments and return values | Business-level authorization |
| API gateway filter | Network edge | Requests to all services | Token validation, rate limiting |

## 6. Hands-on Exercise
In OrderHub, add an `ApiKeyAuthenticationFilter` for a reporting client that calls `GET /api/reports/**` with header `X-API-Key`. A valid key (from `orderhub.reporting.api-key` in `application.yml`) authenticates a principal `reporting-client` with authority `ROLE_REPORTING`. Put reports in their own `SecurityFilterChain` with `@Order(1)`.

Acceptance criteria:
- `GET /api/reports/daily` with the right key returns 200; with a wrong key or no key returns 401.
- The filter is not a `@Component`.
- The rest of the API is unaffected.

<details><summary>Solution</summary>

```java
package com.orderhub.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final byte[] expectedKey;

    public ApiKeyAuthenticationFilter(String expectedKey) {
        this.expectedKey = expectedKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String key = request.getHeader("X-API-Key");
        // constant-time comparison avoids timing attacks
        if (key != null && MessageDigest.isEqual(expectedKey, key.getBytes(StandardCharsets.UTF_8))) {
            var auth = UsernamePasswordAuthenticationToken.authenticated(
                "reporting-client", null, List.of(new SimpleGrantedAuthority("ROLE_REPORTING")));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
        }
        chain.doFilter(request, response);   // no key: stays anonymous, AuthorizationFilter returns 401
    }
}
```

```java
package com.orderhub.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class ReportingSecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain reportingChain(HttpSecurity http,
                                       @Value("${orderhub.reporting.api-key}") String apiKey) throws Exception {
        return http
            .securityMatcher("/api/reports/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("REPORTING"))
            .addFilterBefore(new ApiKeyAuthenticationFilter(apiKey), AuthorizationFilter.class)
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .build();
    }
}
```

```yaml
orderhub:
  reporting:
    api-key: ${REPORTING_API_KEY}   # injected from the environment, never committed
```
The `HttpStatusEntryPoint` returns a bare 401 for anonymous requests. The main API chain keeps a higher `@Order` number, so it handles everything that is not `/api/reports/**`.
</details>

## 7. Recall
Q: What is the role of DelegatingFilterProxy in Spring Security?
A: It is a servlet-container filter that delegates to the Spring bean `springSecurityFilterChain` (the `FilterChainProxy`), bridging the container and the Spring context.

Q: How does FilterChainProxy choose which SecurityFilterChain to run?
A: It tries the chains in `@Order` and runs only the first whose `securityMatcher` matches the request.

Q: Which Spring Security filter turns an AccessDeniedException into a 401 or 403?
A: `ExceptionTranslationFilter`: entry point (401) for anonymous users, access denied handler (403) for authenticated users.

Q: Why is CorsFilter placed before CsrfFilter and the authentication filters?
A: So CORS preflight `OPTIONS` requests, which carry no credentials or CSRF token, can be answered before they are rejected.

Q: Why should a custom Spring Security filter usually not be a @Component?
A: Spring Boot also registers `Filter` beans with the servlet container, so the filter would run twice.

Q: Which filter is last in a Spring Security 6 filter chain and what does it do?
A: `AuthorizationFilter`, which asks the `AuthorizationManager` whether the current authentication may access the request.

**Related:** [[Authentication vs Authorization]] · [[SecurityFilterChain Configuration]] · [[JWT Stateless Authentication]]

@@ 3 | TOPIC | UserDetailsService and Password Encoding | Loading users from the database, DaoAuthenticationProvider, BCrypt, DelegatingPasswordEncoder and password upgrades | MUST
# UserDetailsService and Password Encoding

**Level: Intermediate | Phase 14 | Priority: MUST | Since: Spring Security 2.0 (UserDetailsService); PasswordEncoder interface 3.1; DelegatingPasswordEncoder 5.0**

> **30-second interview answer:** `UserDetailsService` is a one-method interface, `loadUserByUsername`, that Spring calls to fetch a user's stored password hash and authorities, usually from a database. `DaoAuthenticationProvider` uses it, then calls `PasswordEncoder.matches(raw, hash)` to check the password. Passwords are stored with a slow, salted, adaptive hash such as BCrypt, never in plain text or with a fast hash like SHA-256. `DelegatingPasswordEncoder` prefixes each hash with its algorithm id, like `{bcrypt}`, so you can migrate algorithms without resetting everyone's password.

## 1. Concept in Simple Words
### Analogy
A bank that verifies signatures.

| Bank | Spring Security |
|---|---|
| The customer gives their name and signs a slip | Login request with username and raw password |
| The clerk pulls the customer's card from the filing cabinet | `UserDetailsService.loadUserByUsername` |
| The card holds a sample signature and account permissions | `UserDetails`: password hash and authorities |
| The clerk compares the signatures using a trained method | `PasswordEncoder.matches(raw, encoded)` |
| The bank never keeps a copy of your PIN, only a way to check it | One-way hashing |
| A note on each card saying which verification method was used | `{bcrypt}` prefix in `DelegatingPasswordEncoder` |
| The clerk who coordinates the whole thing | `DaoAuthenticationProvider` |

### What it is
- **`UserDetailsService`:** `UserDetails loadUserByUsername(String username) throws UsernameNotFoundException`. It only loads; it does not check passwords.
- **`UserDetails`:** username, encoded password, authorities, and four flags: `isEnabled`, `isAccountNonLocked`, `isAccountNonExpired`, `isCredentialsNonExpired`.
- **`DaoAuthenticationProvider`:** an `AuthenticationProvider` that calls the `UserDetailsService`, checks the flags, and compares passwords with the `PasswordEncoder`.
- **Hash:** a one-way function; you cannot get the password back from it.
- **Salt:** random bytes added to each password before hashing, stored with the hash, so equal passwords produce different hashes and precomputed rainbow tables are useless.
- **Adaptive (work factor):** the algorithm can be made deliberately slower over time. BCrypt's strength (log rounds) defaults to 10 in Spring, meaning 2^10 iterations of its key setup.
- **`DelegatingPasswordEncoder`:** encodes with a default algorithm and stores `{id}hash`; for matching it reads the id and delegates to the right encoder.
- **`UserDetailsPasswordService`:** an optional interface that lets Spring re-hash and save the password with the current algorithm after a successful login.

### Why it exists
Databases leak. If passwords are stored in plain text, a leak is an immediate disaster across every site where users reused the password. Fast hashes (MD5, SHA-1, SHA-256) are not enough: a modern GPU computes billions of SHA-256 hashes per second, so most real passwords are cracked within hours. Slow, salted hashes like BCrypt cost about 50 to 100 ms each on a server, which is invisible at login but makes brute force millions of times more expensive. The separation into `UserDetailsService` and `PasswordEncoder` lets you plug in any user store and any algorithm.

## 2. How It Works
```mermaid
sequenceDiagram
    participant F as "Auth filter or login endpoint"
    participant PM as "ProviderManager"
    participant DAO as "DaoAuthenticationProvider"
    participant UDS as "UserDetailsService"
    participant DB as "users table"
    participant PE as "PasswordEncoder"
    F->>PM: "authenticate(alice, raw password)"
    PM->>DAO: "supports(UsernamePasswordAuthenticationToken)? yes"
    DAO->>UDS: "loadUserByUsername(alice)"
    UDS->>DB: "SELECT by username"
    DB-->>UDS: "row with {bcrypt} hash and roles"
    UDS-->>DAO: "UserDetails"
    DAO->>DAO: "check enabled, locked, expired"
    DAO->>PE: "matches(raw, {bcrypt}$2a$10$...)"
    PE-->>DAO: "true"
    DAO->>DAO: "upgradeEncoding? re-hash and save"
    DAO-->>PM: "authenticated token, credentials erased"
```

Anatomy of a stored BCrypt hash:
```text
{bcrypt}$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
   |     |  |  |                     |
   |     |  |  +- 22 chars: salt      +- 31 chars: hash
   |     |  +---- cost 10 = 2^10 rounds
   |     +------- BCrypt version
   +------------- DelegatingPasswordEncoder id
```
The salt is inside the string, so `matches` can re-hash the raw password with the same salt and cost and compare. Calling `encode` twice on the same password gives two different strings; that is expected.

What Spring Boot does automatically:
- If you define exactly one `UserDetailsService` bean and one `PasswordEncoder` bean, Spring Security builds a `DaoAuthenticationProvider` and an `AuthenticationManager` with them.
- If you define nothing, Boot's `UserDetailsServiceAutoConfiguration` creates an in-memory user `user` with a random password printed at startup (a line starting "Using generated security password:" followed by a UUID), which you should never see in production.
- If there is no `PasswordEncoder` bean, a `DelegatingPasswordEncoder` is used for matching, so stored passwords must carry an `{id}` prefix, otherwise you get "There is no PasswordEncoder mapped for the id null".
- `UsernameNotFoundException` is hidden as `BadCredentialsException` by default (`hideUserNotFoundExceptions`), so attackers cannot tell whether the user exists. `DaoAuthenticationProvider` also runs a dummy password check for unknown users to keep timing similar.

## 3. Code Example
### Code
OrderHub users stored in PostgreSQL with JPA (Phase 12), loaded by a custom `UserDetailsService`, with automatic password upgrade.

```sql
-- src/main/resources/db/migration/V7__users.sql (Flyway)
CREATE TABLE app_user (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    role          VARCHAR(30)  NOT NULL,       -- CUSTOMER or ADMIN
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    customer_id   BIGINT REFERENCES customer(id)
);
```

```java
package com.orderhub.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public enum Role { CUSTOMER, ADMIN }

@Entity
@Table(name = "app_user")
class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @Column(nullable = false, unique = true) String username;
    @Column(name = "password_hash", nullable = false) String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false) Role role;
    boolean enabled = true;
    @Column(name = "customer_id") Long customerId;

    protected AppUser() { }                       // for JPA
    AppUser(String username, String passwordHash, Role role, Long customerId) {
        this.username = username; this.passwordHash = passwordHash;
        this.role = role; this.customerId = customerId;
    }
}

interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
```
(In a real project `Role`, `AppUser` and `AppUserRepository` are three files in `com.orderhub.user`; they are shown together here.)

```java
package com.orderhub.user;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Adapter from the JPA entity to Spring Security's UserDetails. Carries customerId for ownership checks. */
public record OrderHubUserDetails(Long id, String username, String password, Role role,
                                  boolean enabled, Long customerId) implements UserDetails {
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isEnabled() { return enabled; }
    // isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired default to true since Spring Security 6.3
}
```

```java
package com.orderhub.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaUserDetailsService implements UserDetailsService, UserDetailsPasswordService {

    private final AppUserRepository repo;

    public JpaUserDetailsService(AppUserRepository repo) { this.repo = repo; }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return repo.findByUsername(username)
                   .map(JpaUserDetailsService::toDetails)
                   .orElseThrow(() -> new UsernameNotFoundException("No user " + username));
    }

    /** Called by DaoAuthenticationProvider after a successful login when the hash is outdated. */
    @Override
    @Transactional
    public UserDetails updatePassword(UserDetails user, String newEncodedPassword) {
        AppUser entity = repo.findByUsername(user.getUsername()).orElseThrow();
        entity.passwordHash = newEncodedPassword;          // dirty checking saves it at commit
        return toDetails(entity);
    }

    private static OrderHubUserDetails toDetails(AppUser u) {
        return new OrderHubUserDetails(u.id, u.username, u.passwordHash, u.role, u.enabled, u.customerId);
    }
}
```

```java
package com.orderhub.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        // encodes new passwords as {bcrypt}, can still match {noop}, {pbkdf2}, {scrypt}, {argon2}, {sha256}
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CommandLineRunner showHashes(PasswordEncoder encoder) {
        return args -> {
            String h1 = encoder.encode("alice-pass");
            String h2 = encoder.encode("alice-pass");
            System.out.println("hash 1: " + h1);
            System.out.println("hash 2: " + h2);
            System.out.println("same string? " + h1.equals(h2));
            System.out.println("matches? " + encoder.matches("alice-pass", h1));
            System.out.println("legacy noop matches? " + encoder.matches("old", "{noop}old"));
            System.out.println("needs upgrade? " + encoder.upgradeEncoding("{noop}old"));
        };
    }
}
```

### Walkthrough
- The SQL and entity store only `password_hash` and one role. `customer_id` links a login to an OrderHub customer; method security will use it for "own orders only".
- `OrderHubUserDetails` is a record implementing `UserDetails`. Carrying `id` and `customerId` in the principal avoids a database lookup later. Authorities get the `ROLE_` prefix here, in one place.
- `JpaUserDetailsService` throws `UsernameNotFoundException` for unknown users; Spring turns it into a generic `BadCredentialsException` so the API never reveals whether the account exists.
- Implementing `UserDetailsPasswordService` makes `DaoAuthenticationProvider` call `updatePassword` when `upgradeEncoding` says the stored hash is weak (for example `{noop}` or BCrypt with a lower cost). Users migrate silently as they log in.
- Because there is exactly one `UserDetailsService` and one `PasswordEncoder` bean, Spring wires the `DaoAuthenticationProvider` itself; no extra config is needed.
- The runner shows that encoding the same password twice yields different strings (random salt) but both match.

### Output
```text
hash 1: {bcrypt}$2a$10$5Wb1yQd4m6Hn0PZq2d9mKe2bD2xJzv2nH8m0B8b0K8fJmXl3P7B9S
hash 2: {bcrypt}$2a$10$Qk7cS1o0bPZr3Y9oRrH3Jei4Ff0zv7kz0Wb1bM0l1pY6dX0lq4Zt2
same string? false
matches? true
legacy noop matches? true
needs upgrade? true
```
(The hash characters are random on every run; the shape `{bcrypt}$2a$10$` plus 53 characters is always the same.)

### Compare
| Storage | Example | Verdict |
|---|---|---|
| Plain text | `alice-pass` | Never; one leak exposes everyone |
| Fast hash | `SHA-256(alice-pass)` | Broken: no salt, billions of guesses per second |
| Salted fast hash | `SHA-256(salt + pass)` | Better, still far too fast |
| Encryption (AES) | reversible with a key | Wrong tool: the key leaks with the DB |
| Adaptive hash | BCrypt (`{bcrypt}` prefix) or Argon2id | Correct |

## 4. Interview Questions
**Basic**
1. **What does `UserDetailsService` do?**
   It loads a user by username and returns `UserDetails` with the encoded password, authorities and account flags. It does not verify the password; `DaoAuthenticationProvider` does that with a `PasswordEncoder`. You implement it to connect Spring Security to your own user table.
2. **Why BCrypt instead of SHA-256?**
   SHA-256 is designed to be fast, so attackers can test billions of guesses per second. BCrypt is deliberately slow, salted per password, and has a tunable cost factor, so each guess costs tens of milliseconds. Argon2id (memory-hard) and PBKDF2 are also acceptable choices.
3. **What is a salt and where is it stored?**
   Random bytes mixed into the password before hashing so identical passwords get different hashes and rainbow tables do not work. For BCrypt it is stored inside the hash string itself, right after the cost.

**Follow-up**
4. **Why does `encoder.encode("x")` return a different value every time, and how does `matches` still work?**
   Each call generates a new random salt. `matches` parses the salt and cost from the stored hash, hashes the raw password with them, and compares the results in constant time.
5. **What is `DelegatingPasswordEncoder` for?**
   It stores the algorithm id in front of the hash (`{bcrypt}`, `{argon2}`), encodes new passwords with the current default, and matches old ones with their original algorithm. Combined with `UserDetailsPasswordService`, it lets you migrate a whole user base to a stronger algorithm without forced resets.
6. **How do you choose the BCrypt strength?**
   Benchmark on production hardware and target roughly 100 ms or less per hash; strength 10 to 12 is typical. Each increment doubles the time. Too high and login becomes a CPU denial-of-service vector.

**Tricky / scenario**
7. **After an upgrade you see "There is no PasswordEncoder mapped for the id null". Why?**
   Stored hashes have no `{id}` prefix, but the app now matches with a `DelegatingPasswordEncoder`. Either migrate the stored values by prefixing them (`{bcrypt}` for existing BCrypt hashes) or set `setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder())` on the delegating encoder.
8. **Login responses are 2 ms for unknown users and 80 ms for known users. What is the risk and the fix?**
   That timing difference enables user enumeration. `DaoAuthenticationProvider` already prevents it by encoding a dummy password when the user is not found, so check that a custom provider or login endpoint does the same, and always return the same error message.
9. **Your login endpoint handles 50 requests per second and CPU is pinned. Why?**
   BCrypt is intentionally expensive, so password checks per request are costly. That is why you authenticate once and then use a session or token for later requests, and why login endpoints need rate limiting. Never use Basic auth with BCrypt on high-traffic APIs, because every request re-hashes the password.

**What the interviewer is really testing:** whether you understand password storage as a security engineering decision (slow hashes, salts, migration, enumeration) and know how Spring plugs a user store in.

## 5. Pitfalls & Best Practices
### Common mistakes
| Mistake | What goes wrong | Fix |
|---|---|---|
| `NoOpPasswordEncoder` or plain text in production | Leak exposes every password | `createDelegatingPasswordEncoder()` (BCrypt) |
| Encoding with SHA-256 or MD5 | Hashes cracked quickly | BCrypt, Argon2id or PBKDF2 |
| Comparing `encode(raw).equals(stored)` | Always false because of random salt | `encoder.matches(raw, stored)` |
| Revealing "user not found" vs "wrong password" | User enumeration | Same message for both; keep `hideUserNotFoundExceptions` |
| Returning JPA entities as `UserDetails` with lazy relations | `LazyInitializationException` later in the request | Map to a small immutable `UserDetails` record |
| BCrypt cost 16 "to be safe" | Each login takes seconds, easy DoS | Benchmark; 10 to 12 is typical |

### Best practices
- Use `PasswordEncoderFactories.createDelegatingPasswordEncoder()` and implement `UserDetailsPasswordService` for silent upgrades.
- Keep the `UserDetails` small and immutable; include ids you need for authorization.
- Lock accounts or add exponential delays after repeated failures; rate limit the login endpoint.
- Never log raw passwords, even in debug; check request logging filters.
- Validate password strength at registration (length over 12 beats complexity rules) and check against breached-password lists.

### Compare with
| Algorithm | Spring class | Strength | Note |
|---|---|---|---|
| BCrypt | `BCryptPasswordEncoder` | Slow, salted, adaptive | Default; input limited to 72 bytes |
| Argon2id | `Argon2PasswordEncoder` | Memory-hard, best against GPUs | Needs Bouncy Castle; OWASP first choice |
| SCrypt | `SCryptPasswordEncoder` | Memory-hard | Needs Bouncy Castle |
| PBKDF2 | `Pbkdf2PasswordEncoder` | Slow if many iterations | FIPS-compliant environments |
| SHA-256 / MD5 | `MessageDigestPasswordEncoder` (deprecated) | Fast | Legacy only, migrate away |

## 6. Hands-on Exercise
Add registration to OrderHub: `POST /api/auth/register` with `{ "username", "password" }` creates an `AppUser` with role `CUSTOMER` and a hashed password. Reject usernames that already exist with 409 and passwords shorter than 12 characters with 400.

Acceptance criteria:
- The stored `password_hash` starts with `{bcrypt}`.
- Registering the same username twice returns 409.
- The response never contains the password or hash.

<details><summary>Solution</summary>

```java
package com.orderhub.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

record RegisterRequest(@NotBlank @Size(max = 100) String username,
                       @NotBlank @Size(min = 12, max = 72) String password) { }   // 72 = BCrypt input limit

record UserResponse(Long id, String username, String role) { }

@Service
class RegistrationService {
    private final AppUserRepository repo;
    private final PasswordEncoder encoder;

    RegistrationService(AppUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo; this.encoder = encoder;
    }

    @Transactional
    UserResponse register(RegisterRequest req) {
        if (repo.findByUsername(req.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username taken");
        }
        AppUser saved = repo.save(new AppUser(req.username(), encoder.encode(req.password()), Role.CUSTOMER, null));
        return new UserResponse(saved.id, saved.username, saved.role.name());
    }
}

@RestController
class RegistrationController {
    private final RegistrationService service;
    RegistrationController(RegistrationService service) { this.service = service; }

    @PostMapping("/api/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse register(@Valid @RequestBody RegisterRequest req) {
        return service.register(req);
    }
}
```
In the security config, add `.requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()`. A unique constraint on `username` also protects against two simultaneous registrations; map `DataIntegrityViolationException` to 409 in the global error handler from Phase 11. `@Valid` failures already return 400 through that handler.
</details>

## 7. Recall
Q: What does a UserDetailsService do in Spring Security?
A: It loads a user's `UserDetails` (encoded password, authorities, account flags) by username; it does not check the password itself.

Q: Which Spring Security class checks the password using a UserDetailsService and a PasswordEncoder?
A: `DaoAuthenticationProvider`.

Q: Why is BCrypt preferred over SHA-256 for password storage?
A: BCrypt is slow, salted and has an adjustable cost, so brute force is millions of times more expensive than with a fast hash.

Q: What does the {bcrypt} prefix in a stored password mean?
A: It is the algorithm id used by `DelegatingPasswordEncoder` to pick the right encoder for matching.

Q: How can Spring Security upgrade old password hashes without forcing resets?
A: Implement `UserDetailsPasswordService`; after a successful login, if `upgradeEncoding` is true, Spring re-hashes the raw password and calls `updatePassword`.

Q: Why does Spring Security hide UsernameNotFoundException as BadCredentialsException?
A: To prevent attackers from discovering which usernames exist (user enumeration).

**Related:** [[Authentication vs Authorization]] · [[JWT Stateless Authentication]] · [[SecurityFilterChain Configuration]]
