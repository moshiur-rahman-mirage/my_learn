@@ 1 | MODULE | Phase 6 — Authentication and Security | OWASP, Spring Security, JWT, OAuth2/OIDC, Keycloak, auditing | MUST
# Phase 6: Authentication and security

PKSF handles **financial and personal data**, so security questions are guaranteed. Know the difference between **authentication** and **authorization**, how **JWT** works, **OAuth2 flows** and **Keycloak** integration.

@@ 2 | TOPIC | Security Fundamentals | AuthN vs AuthZ, hashing, OWASP Top 10, SQLi, XSS, CSRF | MUST
# Security fundamentals

## Authentication vs authorization
- **Authentication (AuthN):** *who are you?* (login with a password, OTP, fingerprint) → 401 if it fails.
- **Authorization (AuthZ):** *what are you allowed to do?* (roles, permissions) → 403 if it fails.

**Analogy:** at an airport, showing your passport is authentication. Your boarding pass deciding which plane (and which lounge) you can enter is authorization.

## Store passwords with hashing, never encryption

```java
PasswordEncoder encoder = new BCryptPasswordEncoder(12);    // strength 12
String hash = encoder.encode("MyS3cret!");                  // $2a$12$Kx... (different every time)
boolean ok = encoder.matches("MyS3cret!", hash);           // true
```
**Line by line**
- **Hashing** is one-way: you can't get the password back, only check a guess. **Encryption** is two-way (it can be decrypted). Passwords must be hashed.
- **BCrypt** adds a random **salt** automatically, so two users with the same password get different hashes, and pre-computed "rainbow tables" are useless.
- `12` is the cost factor: each +1 doubles the time. A deliberately **slow** hash makes brute force expensive. Argon2 is another good choice.
- `matches` re-hashes the input using the salt stored inside the hash, then compares.
- **Never** use MD5/SHA-1/plain SHA-256 for passwords: they are far too fast.

**Analogy:** hashing is like making juice from fruit. You can check whether juice came from an orange, but you can't turn the juice back into an orange.

## OWASP Top 10 (the most important web risks)

| Risk | Example | Defence |
|---|---|---|
| Broken access control | `/loans/8` shows another PO's loan | Check ownership on **every** request |
| Cryptographic failures | Plain-text NIDs, HTTP | TLS everywhere, encrypt sensitive columns |
| **Injection** (SQL, command) | `' OR 1=1 --` | Parameterized queries |
| Insecure design | No rate limit on OTP | Threat modelling |
| Security misconfiguration | Default passwords, open actuator | Hardening, least privilege |
| Vulnerable components | An old Log4j | Dependency scanning (OWASP DC, Snyk, Dependabot) |
| Auth failures | No lockout, weak passwords | MFA, lockout, strong hashing |
| Integrity failures | Unsigned updates/deserialization | Signed artifacts, safe deserialization |
| Logging failures | No audit log of who changed a balance | Audit logs + alerts |
| SSRF | The server fetches a URL from user input | Allow-list destinations |

## SQL injection

```java
// ✗ VULNERABLE: user input glued into SQL
String sql = "SELECT * FROM member WHERE phone = '" + phone + "'";
// phone = "' OR '1'='1"  -> returns ALL members

// ✓ SAFE: parameter binding
jdbcTemplate.query("SELECT * FROM member WHERE phone = ?", mapper, phone);
// JPA: @Query("select m from Member m where m.phone = :phone") with @Param
```
- With a `?` placeholder, the input is sent as **data**, never as SQL code, so the quotes inside it have no power.
- **Analogy:** filling in a form box vs letting a stranger write directly on the instruction sheet.

## XSS (Cross-Site Scripting)
An attacker stores `<script>steal(document.cookie)</script>` as a member name, and other users' browsers run it.
Defence: **escape output** (React escapes `{value}` by default; avoid `dangerouslySetInnerHTML`), validate input, use a Content-Security-Policy header, and use HttpOnly cookies.

## CSRF (Cross-Site Request Forgery)
A malicious site makes your logged-in browser send a request (with your cookies) to the bank.
Defence: CSRF tokens (Spring Security's default for cookie/session apps), `SameSite` cookies. **Stateless JWT in an `Authorization` header isn't sent automatically**, so CSRF protection is usually disabled for pure token APIs.

@@ 2 | MODULE | Spring Security | Filter chain, configuration, method security, users | MUST
Spring Security protects your app with a chain of **filters** that run before your controller.

@@ 3 | TOPIC | Spring Security Architecture | Filter chain, SecurityContext, AuthenticationManager | MUST
# Spring Security architecture

```
HTTP request
   │
   ▼
DelegatingFilterProxy → FilterChainProxy → SecurityFilterChain
   ├── CorsFilter
   ├── CsrfFilter
   ├── BearerTokenAuthenticationFilter / UsernamePasswordAuthenticationFilter   (who are you?)
   ├── ExceptionTranslationFilter   (turns security exceptions into 401/403)
   └── AuthorizationFilter          (are you allowed?)
   │
   ▼
DispatcherServlet → your @RestController
```

**Analogy:** a series of checkpoints at a government building: a metal detector, an ID check, a visitor-badge check, then the floor-access check. Fail any one and you don't reach the office.

## Key pieces
- **Authentication:** an object holding the principal (user), credentials and authorities (roles).
- **AuthenticationManager / AuthenticationProvider:** verifies credentials (e.g. `DaoAuthenticationProvider` checks the username/password using a `UserDetailsService` + `PasswordEncoder`).
- **SecurityContextHolder:** stores the current user's Authentication for this request (in a ThreadLocal).
- **GrantedAuthority:** a permission string like `ROLE_ADMIN` or `loan:approve`.

```java
// Get the logged-in user anywhere
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();

// Or inject it into a controller
@GetMapping("/me")
public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
    return Map.of("user", jwt.getSubject(), "po", jwt.getClaimAsString("po_code"));
}
```
**Line by line**
- `SecurityContextHolder`: like a name badge pinned on the current request thread. Any code in that request can read it.
- `@AuthenticationPrincipal Jwt jwt`: Spring injects the validated token, so you can read its claims (user id, PO code).

@@ 3 | TOPIC | SecurityFilterChain Configuration | Configuring URL rules, stateless APIs, CORS, method security | MUST
# Configuring SecurityFilterChain

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity                                   // enables @PreAuthorize
public class SecurityConfig {

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())                                    // stateless token API
            .cors(Customizer.withDefaults())                                 // use the CorsConfigurationSource bean
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/loans/**").hasAnyRole("OFFICER", "MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/v1/loans/*/approve").hasRole("MANAGER")
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()));    // validate JWT bearer tokens
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("https://app.pksf.org.bd"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/api/**", c);
        return src;
    }
}
```

**Line by line**
- `csrf.disable()`: OK **only** for stateless APIs that use `Authorization` headers. Keep CSRF on if you use session cookies.
- `STATELESS`: never create an HTTP session. Every request must carry its token.
- `requestMatchers(...).permitAll()`: public endpoints (health, docs).
- `hasRole("MANAGER")`: checks for the authority `ROLE_MANAGER` (Spring adds the `ROLE_` prefix). `hasAuthority("loan:approve")` checks an exact string.
- **Rules are checked top to bottom, and the first match wins.** Put specific rules first and `anyRequest()` last.
- `oauth2ResourceServer(...jwt...)`: adds the filter that reads `Authorization: Bearer <token>`, verifies the signature and expiry, and builds the Authentication.
- CORS: only our frontend's origin may call the API from a browser. **Never use `*` together with credentials.**

## Method-level security

```java
@Service
public class LoanService {

    @PreAuthorize("hasRole('MANAGER')")
    public void approve(Long loanId) { ... }

    @PreAuthorize("hasRole('OFFICER') and @poGuard.canAccess(#poId, authentication)")
    public List<LoanDto> loansOfPo(Long poId) { ... }

    @PostAuthorize("returnObject.poCode == authentication.token.claims['po_code']")
    public LoanDto get(Long id) { ... }
}
```
- `@PreAuthorize`: checked **before** the method runs. `#poId` refers to the method parameter, and `@poGuard` calls a bean.
- `@PostAuthorize`: checked **after**, using the return value. It is useful for ownership rules ("officers see only their own PO's loans").
- This defends against **broken access control**, OWASP's #1 risk. The URL rule says "officers can read loans", and the method rule says "**only loans of their own PO**".

@@ 2 | MODULE | Tokens: JWT, OAuth2 and OpenID Connect | Token structure, validation, flows | MUST
How modern apps prove identity without server sessions.

@@ 3 | TOPIC | JWT Explained | Structure, signing, validation, storage, refresh tokens | MUST
# JWT (JSON Web Token)

A **JWT** is a signed token that carries claims (facts) about the user. The server can verify it **without a database lookup**.

**Analogy:** a festival wristband. Security checks the official stamp (the signature) and the date printed on it (the expiry). They don't need to phone the ticket office for every person.

## Structure: `header.payload.signature` (each part Base64URL-encoded)

```
eyJhbGciOiJSUzI1NiJ9 . eyJzdWIiOiJyYWhpbSIsInJvbGVzIjpbIk9GRklDRVIiXSwiZXhwIjoxNzMxNjQwMDAwfQ . kX9a...signature
```
```json
// Header
{ "alg": "RS256", "typ": "JWT", "kid": "key-2025-11" }
// Payload (claims)
{ "sub": "rahim", "iss": "https://auth.pksf.org.bd/realms/pksf",
  "aud": "loan-api", "exp": 1731640000, "iat": 1731639700,
  "roles": ["OFFICER"], "po_code": "PO-07" }
```
- `sub` = subject (the user), `iss` = issuer (who created it), `aud` = audience (who it is for), `exp` = expiry time, `iat` = issued at.
- **The payload is only encoded, NOT encrypted.** Anyone can read it (try jwt.io). **Never put passwords or sensitive data in it.**
- The **signature** proves nobody changed it. Change one character of the payload and the signature check fails.

## Signing algorithms
- **HS256 (symmetric):** the same secret signs and verifies. Every service that verifies must know the secret.
- **RS256 / ES256 (asymmetric):** the auth server signs with its **private key**, and services verify with the **public key** (published at a JWKS URL). This is the better choice for microservices and Keycloak.

## Validating in Spring (resource server)

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.pksf.org.bd/realms/pksf
```
- With just this, Spring downloads the public keys (JWKS) from the issuer and checks **the signature, `exp`, `nbf` and `iss`** on every request.

## Hand-made JWT filter (to understand the idea; prefer the built-in resource server)

```java
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims c = jwt.parseAndVerify(token);                    // checks signature + expiry
                List<SimpleGrantedAuthority> auths = ((List<String>) c.get("roles")).stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
                var auth = new UsernamePasswordAuthenticationToken(c.getSubject(), null, auths);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException e) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);       // invalid/expired -> 401
                return;
            }
        }
        chain.doFilter(req, res);                                        // continue to the next filter
    }
}
```
**Line by line**
- `OncePerRequestFilter`: guaranteed to run exactly once per request.
- `substring(7)`: removes `"Bearer "` (7 characters).
- `parseAndVerify`: if the signature is wrong or the token has expired, it throws, and we return 401.
- We turn the roles claim into `GrantedAuthority` objects and store the Authentication in the SecurityContext, so `hasRole` checks work later.
- `chain.doFilter`: **always** pass the request on (unless rejecting it), or the request stops here.

## Access token + refresh token
- **Access token:** short-lived (5–15 min), sent with every API call.
- **Refresh token:** long-lived, used **only** to get a new access token from the auth server. It can be revoked.
- Why? A JWT **can't be revoked easily** before it expires (the server doesn't track it). Short lifetimes limit the damage if one is stolen.
- **Analogy:** the access token is a day pass. The refresh token is your membership card, which you show at the desk each morning to get a new day pass. If the card is cancelled, no more day passes.

## Where should the browser store tokens?
- `localStorage`: easy, but **readable by XSS** scripts.
- **HttpOnly Secure SameSite cookie** (or the BFF pattern, where the server keeps the tokens): JavaScript can't read it. This is the safer choice.
- In memory (a JS variable) for the access token plus silent refresh is a common SPA compromise.

@@ 3 | TOPIC | OAuth2 and OpenID Connect | Roles, grant types, Authorization Code + PKCE, OIDC | MUST
# OAuth2 and OpenID Connect

**OAuth2** is an **authorization** framework: it lets an app get **limited access** to a resource on behalf of a user, **without the app seeing the user's password**.
**OpenID Connect (OIDC)** is a layer on top of OAuth2 that adds **authentication** (who the user is) through an **ID token**.

**Analogy:** a hotel key card. Reception (the authorization server) checks your ID once and gives you a card (the access token) that opens only your room (the scope) until checkout (the expiry). The room door (the resource server) only checks the card, never your passport.

## Four roles
| Role | In our system |
|---|---|
| Resource owner | The user (a PO officer) |
| Client | The React app / a mobile app / another service |
| Authorization server | **Keycloak** |
| Resource server | Our Spring Boot API |

## Grant types (flows)

| Flow | Use for |
|---|---|
| **Authorization Code + PKCE** | Web SPAs and mobile apps where a **user logs in** (recommended) |
| **Client Credentials** | **Service-to-service**, no user (e.g. a reporting job calling loan-service) |
| Refresh Token | Get a new access token |
| ~~Implicit~~, ~~Password~~ | **Deprecated**: don't use |

## Authorization Code + PKCE, step by step

```
1. User clicks "Login" in the React app.
2. The app creates a random code_verifier and its hash, the code_challenge.
3. Browser → Keycloak /authorize?client_id=web&response_type=code&scope=openid
             &redirect_uri=https://app/callback&code_challenge=XYZ&code_challenge_method=S256
4. The user logs in on KEYCLOAK's page (the app never sees the password).
5. Keycloak → redirect to https://app/callback?code=ABC   (a short-lived one-time code)
6. App → Keycloak /token  with code=ABC + code_verifier
7. Keycloak checks that hash(code_verifier) == code_challenge → returns access_token, id_token, refresh_token
8. App → API with  Authorization: Bearer <access_token>
9. The API validates the JWT signature/expiry/issuer → 200
```
**Why each step**
- The **code** travels through the browser URL, which is less safe, so it is useless on its own and short-lived.
- **PKCE** (Proof Key for Code Exchange): only the app that started the login knows the `code_verifier`, so a stolen code can't be exchanged for tokens.
- Tokens are fetched by a direct back-channel request, not left exposed in the URL.

## Client Credentials (machine to machine)
```http
POST /realms/pksf/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&client_id=reporting-job&client_secret=****
```
- No user is involved. The service authenticates as itself and gets a token with the service's roles.

## Tokens in OIDC
- **Access token:** for calling APIs (the API checks it).
- **ID token:** information *about the user* for the **client** app (name, email). **Don't** send it to APIs as an access token.
- **Refresh token:** to get new tokens.
- **Scopes:** what access is requested (`openid profile email loans.read`).

@@ 2 | TOPIC | Keycloak Integration | Realm, clients, roles, Spring resource server, role mapping, React login | MUST
# Keycloak

**Keycloak** is an open-source **identity and access management** server: login pages, users, roles, MFA, social login and LDAP/AD integration, using OAuth2/OIDC standards.
**Analogy:** Keycloak is the building's central security office. Every office (app) trusts the badges it issues, so no office needs its own guard and visitor book.

## Core concepts
| Concept | Meaning |
|---|---|
| **Realm** | An isolated space of users/clients/roles (e.g. `pksf`) |
| **Client** | An app that uses Keycloak (`loan-web` public SPA, `loan-api` bearer-only, `reporting-job` confidential) |
| **Realm role / client role** | `ADMIN`, `MANAGER`, `OFFICER` |
| **Group** | A set of users with shared roles (e.g. "PO-07 Officers") |
| **User attribute / mapper** | Extra claims in the token, like `po_code` |

## Run it locally
```bash
docker run -p 8180:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:26.0 start-dev
```
- Then open the admin console → create realm `pksf` → create client `loan-web` (public, standard flow, PKCE S256, redirect `http://localhost:5173/*`) → create roles → create users and assign roles.

## Spring Boot as a resource server

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8180/realms/pksf
```
Keycloak puts roles in a nested claim: `"realm_access": { "roles": ["OFFICER"] }`. Spring doesn't know this by default, so we map it:

```java
@Bean
SecurityFilterChain api(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(a -> a
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(keycloakConverter())))
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(c -> c.disable());
    return http.build();
}

private Converter<Jwt, AbstractAuthenticationToken> keycloakConverter() {
    return jwt -> {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Collection<String> roles = realmAccess == null ? List.of()
                : (Collection<String>) realmAccess.getOrDefault("roles", List.of());
        List<GrantedAuthority> authorities = roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());
        return new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString("preferred_username"));
    };
}
```
**Line by line**
- `issuer-uri`: Spring fetches Keycloak's discovery document and **public keys** and validates every token's signature, expiry and issuer.
- `getClaimAsMap("realm_access")`: reads the nested JSON object from the token.
- `"ROLE_" + r`: Spring's `hasRole("ADMIN")` looks for `ROLE_ADMIN`.
- `JwtAuthenticationToken(jwt, authorities, name)`: the Authentication object that Spring stores, with our mapped roles and the username as its name.
- Null checks: a token without roles should mean "no roles", not a crash.

## React login with keycloak-js

```ts
import Keycloak from "keycloak-js";

const keycloak = new Keycloak({ url: "http://localhost:8180", realm: "pksf", clientId: "loan-web" });

await keycloak.init({ onLoad: "login-required", pkceMethod: "S256" });

api.interceptors.request.use(async (config) => {
  await keycloak.updateToken(30);                              // refresh if it expires within 30s
  config.headers.Authorization = `Bearer ${keycloak.token}`;
  return config;
});
```
- `onLoad: "login-required"`: redirects to Keycloak's login page if the user isn't logged in.
- `pkceMethod: "S256"`: the Authorization Code + PKCE flow.
- `updateToken(30)`: refreshes the access token silently with the refresh token before it expires.
- The interceptor attaches the token to **every** API call.

## Service-to-service
A confidential client (`reporting-job`) uses **client credentials**. In Spring, use `spring-boot-starter-oauth2-client` with `OAuth2AuthorizedClientManager` so that `RestClient` calls attach the token automatically.

**Interview points:** Keycloak gives SSO across all PKSF apps, central user management, MFA, password policies, brute-force protection, and LDAP federation with the existing staff directory.

@@ 2 | TOPIC | Data Protection and Auditing | Encryption, masking, audit trails, Hibernate Envers | MUST
# Data protection and auditing

## Protect data in transit and at rest
- **In transit:** HTTPS/TLS everywhere, including between services and to the DB (`sslmode=require`).
- **At rest:** disk/DB encryption, plus **column encryption** for very sensitive fields (NID, bank accounts).
- **Secrets:** never in Git. Use environment variables, Kubernetes Secrets, Vault.

## Column encryption with a JPA AttributeConverter

```java
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {
    private final AesGcmCipher cipher;                     // key loaded from a secret store
    public EncryptedStringConverter(AesGcmCipher cipher) { this.cipher = cipher; }

    @Override public String convertToDatabaseColumn(String plain) {
        return plain == null ? null : cipher.encrypt(plain);     // Java -> DB
    }
    @Override public String convertToEntityAttribute(String encrypted) {
        return encrypted == null ? null : cipher.decrypt(encrypted);  // DB -> Java
    }
}

@Entity
public class Member {
    @Convert(converter = EncryptedStringConverter.class)
    private String nationalId;
}
```
**Line by line**
- `AttributeConverter<EntityType, DbType>`: JPA calls it automatically on every save and load.
- The DB only ever stores ciphertext. A stolen backup doesn't reveal NIDs.
- **AES-GCM** is authenticated encryption: it detects tampering. Keep the key outside the DB.
- Trade-off: you can't search encrypted columns with LIKE. Store a separate **hash** column for exact lookups.

## Masking in responses and logs
```java
public static String maskPhone(String p) {           // 01712345678 -> 017*****678
    return p.substring(0, 3) + "*****" + p.substring(p.length() - 3);
}
```
- Show only what the user needs. Logs must **never** contain passwords, tokens or full NIDs.

## Audit trail: who changed what, and when

```java
@Entity
@Audited                                   // Hibernate Envers: keeps every version of the row
public class Loan { ... }
```
```java
AuditReader reader = AuditReaderFactory.get(entityManager);
List<Number> revisions = reader.getRevisions(Loan.class, loanId);
Loan oldVersion = reader.find(Loan.class, loanId, revisions.get(0));
```
**Line by line**
- `@Audited`: Envers creates a `loan_aud` table plus a `revinfo` table. Every insert/update/delete stores a snapshot with a revision number and timestamp.
- Add a custom `RevisionEntity` to record the **username** (from the SecurityContext).
- `AuditReader`: query history, e.g. "what was this loan's outstanding on 1 March?"
- Combine it with Spring Data `@CreatedBy/@LastModifiedBy` (see Entity Mapping) and an **immutable ledger** for money movements.
- **Analogy:** CCTV for your data. You can rewind and see who did what.

**Also mention in a viva:** least-privilege DB users, MFA for admins, rate limiting on login/OTP, account lockout, regular dependency scanning, backups with tested restores, and following Bangladesh's data-protection rules.

@@ 2 | TOPIC | Phase 6 — Interview Questions | Quick Q&A revision for security | MUST
# Phase 6 interview questions

**Q. Authentication vs authorization?**
A. Authentication verifies identity (who you are → 401 if it fails). Authorization checks permissions (what you may do → 403).

**Q. How should passwords be stored?**
A. Hashed with a slow, salted algorithm (BCrypt/Argon2) via `PasswordEncoder`. Never plain text, encrypted or a fast hash.

**Q. What is a JWT and what are its parts?**
A. A signed token with three Base64URL parts: header (algorithm), payload (claims such as sub, exp, roles) and signature. The payload is readable, not encrypted.

**Q. How do you revoke a JWT?**
A. You can't easily before it expires. Use short-lived access tokens with revocable refresh tokens, or keep a denylist of token ids (jti) in Redis for emergencies.

**Q. HS256 vs RS256?**
A. HS256 uses a shared secret for signing and verifying. RS256 signs with a private key and verifies with a public key, which is better for distributed services.

**Q. Explain the OAuth2 Authorization Code flow with PKCE.**
A. The app redirects to the auth server with a code challenge, the user logs in there, the app gets a one-time code, and exchanges code + verifier for tokens. PKCE stops stolen codes from being used.

**Q. OAuth2 vs OpenID Connect?**
A. OAuth2 handles authorization (access tokens). OIDC adds authentication on top (ID token, userinfo endpoint).

**Q. Which flow for service-to-service calls?**
A. Client Credentials.

**Q. What is CSRF and when can you disable protection?**
A. A forged cross-site request that uses the victim's cookies. Protection can be disabled for stateless APIs that authenticate with an Authorization header, not with cookies.

**Q. How do you prevent SQL injection?**
A. Parameterized queries / prepared statements (JPA parameters, JdbcTemplate `?`), never string concatenation. Also validate input and use least-privilege DB users.

**Q. What is CORS?**
A. A browser rule that blocks cross-origin requests unless the server allows the origin through Access-Control-Allow-* headers. Preflight OPTIONS requests are sent for non-simple requests.

**Q. How does Spring Security work internally?**
A. A chain of servlet filters (SecurityFilterChain) authenticates the request, stores an Authentication in the SecurityContextHolder, and authorization filters or method security check the authorities.

**Q. How do Keycloak roles reach Spring?**
A. They arrive in the JWT under realm_access.roles. A custom JwtAuthenticationConverter maps them to ROLE_ authorities.

**Q. How do you audit changes to financial records?**
A. Spring Data auditing (createdBy/At), Hibernate Envers for full history, an immutable ledger with reversal entries, and centralized logs of security events.
