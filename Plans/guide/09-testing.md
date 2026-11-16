@@ 1 | MODULE | Phase 9 — Testing and Code Quality | JUnit, Mockito, Spring test slices, Testcontainers, frontend tests, quality tools | MUST
# Phase 9: Testing and code quality

The TOR asks for a public GitHub repo **with tests**. In the viva, expect "How do you test a service / controller / repository?"

**The test pyramid:** many fast **unit** tests at the bottom, fewer **integration** tests in the middle, a few slow **end-to-end** tests at the top.
**Analogy:** building a car. Test each part on the bench (unit), then test assembled systems such as the engine with the gearbox (integration), then take a few full test drives (E2E).

@@ 2 | MODULE | Backend Testing | JUnit 5, Mockito, MockMvc, DataJpaTest, Testcontainers | MUST
How to test each layer of a Spring Boot app.

@@ 3 | TOPIC | JUnit 5 Basics | Test structure, assertions, parameterized tests, AAA pattern | MUST
# JUnit 5 basics

```java
class InterestCalculatorTest {

    private FlatInterest calc;

    @BeforeEach
    void setUp() {                                     // runs before EACH test: fresh object
        calc = new FlatInterest(new BigDecimal("0.12"));
    }

    @Test
    @DisplayName("12% flat on 10,000 for 12 months = 1,200.00")
    void calculatesFlatInterest() {
        // Arrange
        BigDecimal principal = new BigDecimal("10000");
        // Act
        BigDecimal interest = calc.calculate(principal, 12);
        // Assert
        assertEquals(new BigDecimal("1200.00"), interest);
    }

    @Test
    void rejectsNegativePrincipal() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calc.calculate(new BigDecimal("-1"), 12));
        assertTrue(ex.getMessage().contains("positive"));
    }

    @ParameterizedTest
    @CsvSource({ "10000, 6, 600.00", "5000, 12, 600.00", "0.01, 1, 0.00" })
    void manyCases(BigDecimal p, int months, BigDecimal expected) {
        assertEquals(expected, calc.calculate(p, months));
    }
}
```
**Line by line**
- `@Test`: marks a test method. JUnit 5 test classes/methods don't need to be public.
- `@BeforeEach`: setup before every test, so **tests don't share state** and can run in any order. (`@BeforeAll` runs once, on a static method.)
- `@DisplayName`: a readable name in reports.
- **AAA (Arrange-Act-Assert):** a clear 3-part structure for every test. (Also called Given-When-Then.)
- `assertEquals(expected, actual)`: **expected comes first**. Note: BigDecimal `equals` checks the scale too, so `1200.00` ≠ `1200`. Use `compareTo` or AssertJ's `isEqualByComparingTo`.
- `assertThrows`: checks that the code throws, and returns the exception so that you can inspect the message.
- `@ParameterizedTest` + `@CsvSource`: one test method, many input rows. Great for edge cases (zero, tiny amounts, rounding).

**AssertJ (more readable, included in spring-boot-starter-test):**
```java
assertThat(interest).isEqualByComparingTo("1200.00");
assertThat(members).hasSize(3).extracting(Member::getName).contains("Rahim");
```

**A good unit test is FIRST:** Fast, Independent, Repeatable, Self-validating, Timely.

@@ 3 | TOPIC | Mockito for Unit Tests | Mocks, stubbing, verify, ArgumentCaptor | MUST
# Mockito: unit testing a service

A **mock** is a fake object that you control. It lets you test one class **in isolation**, without a real DB, SMS gateway or network.

**Analogy:** a flight simulator. Pilots practise emergencies without risking a real plane, and the instructor decides exactly what "goes wrong".

```java
@ExtendWith(MockitoExtension.class)
class RepaymentServiceTest {

    @Mock LoanRepository loans;               // fake
    @Mock LedgerRepository ledger;            // fake
    @Mock SmsSender sms;                      // fake
    @InjectMocks RepaymentService service;    // REAL class, with the mocks injected

    @Test
    void repayReducesOutstandingAndWritesLedger() {
        // Arrange
        Loan loan = new Loan(7L, new BigDecimal("5000.00"));
        when(loans.findById(7L)).thenReturn(Optional.of(loan));

        // Act
        service.repay(7L, new BigDecimal("1000.00"));

        // Assert: state
        assertThat(loan.getOutstanding()).isEqualByComparingTo("4000.00");

        // Assert: interactions
        ArgumentCaptor<LedgerEntry> captor = ArgumentCaptor.forClass(LedgerEntry.class);
        verify(ledger).save(captor.capture());
        assertThat(captor.getValue().getAmount()).isEqualByComparingTo("1000.00");
        verify(sms, times(1)).send(anyString(), contains("1000"));
    }

    @Test
    void repayMoreThanOutstandingFails() {
        when(loans.findById(7L)).thenReturn(Optional.of(new Loan(7L, new BigDecimal("500.00"))));

        assertThrows(BusinessRuleException.class,
                () -> service.repay(7L, new BigDecimal("1000.00")));

        verify(ledger, never()).save(any());          // nothing must be written
        verifyNoInteractions(sms);
    }

    @Test
    void unknownLoanThrowsNotFound() {
        when(loans.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.repay(99L, BigDecimal.TEN));
    }
}
```
**Line by line**
- `@ExtendWith(MockitoExtension.class)`: enables the Mockito annotations in JUnit 5. **No Spring context is started**, so the test runs in milliseconds.
- `@Mock`: creates a fake. Unstubbed methods return defaults (null, 0, empty Optional/collections).
- `@InjectMocks`: creates the real `RepaymentService` and passes the mocks into its constructor.
- `when(...).thenReturn(...)`: **stubbing**, i.e. "when asked for loan 7, return this loan".
- `verify(ledger).save(...)`: checks that the method **was called**. `times(1)`, `never()`, `atLeastOnce()`.
- `ArgumentCaptor`: grabs the actual argument passed in, so you can inspect it.
- `anyString()`, `any()`, `contains("1000")`: **argument matchers**. If you use a matcher for one argument, use matchers for all of them.
- `thenThrow(new RuntimeException())`: simulates failures (e.g. the SMS gateway is down).

**Mock vs stub vs spy:** a stub returns canned answers, a mock also verifies interactions, and a spy (`@Spy`) wraps a **real** object and overrides only some methods.

**Don't mock what you don't own** too deeply (e.g. complex library internals). And don't mock value objects (BigDecimal, DTOs): just create real ones.

@@ 3 | TOPIC | Testing Controllers with MockMvc | @WebMvcTest, JSON assertions, validation, security | MUST
# Testing controllers with @WebMvcTest

`@WebMvcTest` starts **only the web layer** (controllers, advice, filters, JSON conversion), not services or the DB. It is fast and focused.

```java
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired MockMvc mvc;                         // a simulated HTTP client, no real server
    @Autowired ObjectMapper json;
    @MockitoBean MemberService service;             // Spring Boot 3.4+ (older: @MockBean)

    @Test
    @WithMockUser(roles = "OFFICER")
    void getReturnsMember() throws Exception {
        when(service.get(1L)).thenReturn(new MemberResponse(1L, "Rahim", "01712345678", "Samity A"));

        mvc.perform(get("/api/v1/members/1"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name").value("Rahim"))
           .andExpect(jsonPath("$.phone").value("01712345678"));
    }

    @Test
    @WithMockUser(roles = "OFFICER")
    void createValidatesInput() throws Exception {
        var bad = new MemberRequest("", "123", "abc", LocalDate.now().plusDays(1), null);

        mvc.perform(post("/api/v1/members")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsString(bad)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.errors.name").exists())
           .andExpect(jsonPath("$.errors.phone").exists());

        verifyNoInteractions(service);              // the service must not be called for invalid input
    }

    @Test
    void anonymousGets401() throws Exception {
        mvc.perform(get("/api/v1/members/1")).andExpect(status().isUnauthorized());
    }
}
```
**Line by line**
- `@WebMvcTest(MemberController.class)`: loads only this controller plus the MVC infrastructure.
- `MockMvc`: sends fake HTTP requests through the full Spring MVC pipeline (filters, validation, exception handlers), with no network.
- `@MockitoBean`: puts a Mockito mock **into the Spring context** in place of the real service.
- `@WithMockUser(roles = "OFFICER")`: pretends a user with that role is logged in (spring-security-test).
- `jsonPath("$.name")`: reads a field from the JSON response. `$` is the root.
- `.with(csrf())`: adds a CSRF token. It is needed if CSRF is enabled in the security config the test loads.
- The validation test proves that `@Valid` + `@RestControllerAdvice` return 400 with field errors, **and** that the service is never reached.
- The 401 test proves security is actually on. Always test the "not allowed" paths too.

@@ 3 | TOPIC | Repository and Integration Tests with Testcontainers | @DataJpaTest, real PostgreSQL in Docker, @SpringBootTest | MUST
# Repository and integration tests with Testcontainers

**Problem:** testing against H2 (an in-memory DB) differs from PostgreSQL: different SQL, types and behaviour. Tests pass, and production fails.
**Testcontainers** starts a **real PostgreSQL in Docker** for your tests and throws it away afterwards.

**Analogy:** rehearsing a play on the real stage instead of in your living room.

## @DataJpaTest with real Postgres

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)   // don't swap in H2
@Testcontainers
class LoanRepositoryTest {

    @Container
    @ServiceConnection                                   // Boot 3.1+: auto-wires the datasource URL/user/password
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired LoanRepository repo;
    @Autowired TestEntityManager em;

    @Test
    void findsOnlyActiveLoansOfSamity() {
        Samity s = em.persist(new Samity("Samity A"));
        Member m = em.persist(new Member("Rahim", s));
        em.persist(new Loan(m, new BigDecimal("5000"), LoanStatus.ACTIVE));
        em.persist(new Loan(m, new BigDecimal("3000"), LoanStatus.CLOSED));
        em.flush(); em.clear();                          // force SQL, then read fresh from the DB

        List<Loan> result = repo.activeInSamity(s.getId());

        assertThat(result).hasSize(1)
                          .first().extracting(Loan::getStatus).isEqualTo(LoanStatus.ACTIVE);
    }
}
```
**Line by line**
- `@DataJpaTest`: starts only JPA (entities, repositories, Flyway). **Each test runs in a transaction that is rolled back**, so the DB is clean for the next test.
- `Replace.NONE`: keep our real Postgres instead of the embedded default.
- `@Container static`: one container shared by all tests in the class (started once, which is faster).
- `@ServiceConnection`: Spring Boot reads the container's random port and credentials and configures the DataSource. No manual properties needed.
- `TestEntityManager.persist`: inserts test data directly.
- `flush(); clear();`: make sure the query really hits the DB and not Hibernate's first-level cache.
- Flyway migrations run against the container, so your **migrations are tested too**.

## Full integration test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class RepaymentFlowIT {

    @Container @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired TestRestTemplate http;

    @Test
    void repaymentUpdatesOutstanding() {
        ResponseEntity<LoanResponse> before = http.getForEntity("/api/v1/loans/1", LoanResponse.class);
        http.postForEntity("/api/v1/loans/1/repayments", new RepaymentRequest(new BigDecimal("500")), Void.class);
        ResponseEntity<LoanResponse> after = http.getForEntity("/api/v1/loans/1", LoanResponse.class);

        assertThat(after.getBody().outstanding())
            .isEqualByComparingTo(before.getBody().outstanding().subtract(new BigDecimal("500")));
    }
}
```
- `@SpringBootTest(RANDOM_PORT)`: starts the **whole app** on a random port. It is slow, so keep these tests few and use them for critical flows.
- `TestRestTemplate`: a real HTTP client against the running app.
- Name integration tests `*IT` and run them with the Maven **Failsafe** plugin (`mvn verify`). Unit tests (`*Test`) run with Surefire (`mvn test`).

| Test type | Annotation | Starts | Speed |
|---|---|---|---|
| Unit | none / `@ExtendWith(MockitoExtension)` | Nothing | ms |
| Web slice | `@WebMvcTest` | MVC only | ~1 s |
| JPA slice | `@DataJpaTest` | JPA + DB | seconds |
| Full | `@SpringBootTest` | Everything | slowest |

@@ 2 | TOPIC | Frontend Testing | Vitest, React Testing Library, MSW, Playwright | SHOULD
# Frontend testing

| Tool | Job |
|---|---|
| **Vitest** (or Jest) | Test runner + assertions |
| **React Testing Library (RTL)** | Render components and interact like a **user** |
| **MSW (Mock Service Worker)** | Fake API responses at the network level |
| **Playwright / Cypress** | End-to-end tests in a real browser |

**RTL philosophy:** "Test what the user sees and does, not the implementation details." Find elements by **role, label or text**, not by CSS class.

```tsx
// MemberForm.test.tsx
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi } from "vitest";

describe("MemberForm", () => {
  it("shows a validation error for an invalid phone", async () => {
    const user = userEvent.setup();
    render(<MemberForm onSave={vi.fn()} />);

    await user.type(screen.getByLabelText(/phone/i), "123");
    await user.click(screen.getByRole("button", { name: /save/i }));

    expect(await screen.findByText(/invalid bd mobile/i)).toBeInTheDocument();
  });

  it("calls onSave with the form values", async () => {
    const user = userEvent.setup();
    const onSave = vi.fn();
    render(<MemberForm onSave={onSave} />);

    await user.type(screen.getByLabelText(/name/i), "Rahim");
    await user.type(screen.getByLabelText(/phone/i), "01712345678");
    await user.click(screen.getByRole("button", { name: /save/i }));

    expect(onSave).toHaveBeenCalledWith(expect.objectContaining({ name: "Rahim", phone: "01712345678" }));
  });
});
```
**Line by line**
- `render(<MemberForm />)`: mounts the component in a simulated DOM (jsdom).
- `userEvent.setup()` + `user.type` / `user.click`: realistic typing and clicking (focus, key events).
- `getByLabelText(/phone/i)`: finds the input by its **label**. If this fails, your form isn't accessible either, which is a nice side benefit.
- `getByRole("button", { name: /save/i })`: finds the button the way a screen reader would.
- `findByText`: **async**, it waits for the element to appear (validation runs asynchronously).
- `vi.fn()`: a mock function that records its calls. `toHaveBeenCalledWith` checks the arguments.
- `getBy...` throws if the element is missing, `queryBy...` returns null (use it to assert something is absent), `findBy...` waits.

## MSW: mock the API
```ts
import { http, HttpResponse } from "msw";
import { setupServer } from "msw/node";

const server = setupServer(
  http.get("/api/v1/members/:id", ({ params }) =>
    HttpResponse.json({ id: Number(params.id), name: "Rahim" }))
);
beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());
```
- The real axios/fetch code runs unchanged. MSW intercepts the request and returns fake data, so you test the actual data-fetching logic without a backend.

@@ 2 | TOPIC | Code Quality | Clean code, SonarQube, JaCoCo, linters, code review | SHOULD
# Code quality

## Clean code habits (say these in a viva)
- **Meaningful names:** `calculateOverdueAmount()`, not `calc2()`.
- **Small functions** that do one thing.
- **No magic numbers:** `MAX_LOAN_AMOUNT`, not `500000` scattered around.
- **DRY** (Don't Repeat Yourself), but don't over-abstract too early.
- **Fail fast:** validate inputs at the boundary.
- **Comments explain why**, not what.

```java
// ✗ Before
public double c(List<Loan> l) {
    double t = 0;
    for (Loan x : l) if (x.s == 2 && x.d.isBefore(LocalDate.now())) t += x.a * 0.02;
    return t;
}

// ✓ After
private static final BigDecimal LATE_FEE_RATE = new BigDecimal("0.02");

public BigDecimal totalLateFees(List<Loan> loans) {
    return loans.stream()
            .filter(Loan::isOverdue)
            .map(loan -> loan.getOutstanding().multiply(LATE_FEE_RATE))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}
```
**Why the second is better**
- The name says what it does. `isOverdue()` hides the status/date details inside the Loan class (encapsulation).
- The magic number `0.02` becomes a named constant.
- `BigDecimal` instead of `double` for money.
- No cryptic one-letter variables.

## Tools

| Tool | Checks |
|---|---|
| **JaCoCo** | Test coverage % (lines/branches) |
| **SonarQube / SonarCloud** | Bugs, code smells, duplication, security hotspots, a quality gate |
| **Checkstyle / Spotless** | Code formatting and style |
| **SpotBugs / PMD** | Common bug patterns |
| **ESLint + Prettier** | JS/TS linting and formatting |
| **OWASP Dependency-Check / Dependabot** | Vulnerable libraries |

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.12</version>
  <executions>
    <execution><goals><goal>prepare-agent</goal></goals></execution>
    <execution><id>report</id><phase>verify</phase><goals><goal>report</goal></goals></execution>
  </executions>
</plugin>
```
- `prepare-agent`: attaches the coverage agent while the tests run. `report` writes an HTML report to `target/site/jacoco`.
- **Coverage is a guide, not a goal.** 80% coverage with weak assertions is worse than 60% with meaningful tests. Focus on business rules (interest, overdue, balance).

## Code review checklist
Correctness and edge cases → tests included → security (input validation, authorization) → performance (N+1, missing indexes) → readability → no secrets committed.

@@ 2 | TOPIC | Phase 9 — Interview Questions | Quick Q&A revision for testing | MUST
# Phase 9 interview questions

**Q. Unit vs integration test?**
A. A unit test checks one class in isolation with dependencies mocked (fast). An integration test checks several parts working together with real infrastructure such as the DB or HTTP (slower).

**Q. What is the test pyramid?**
A. Many unit tests, fewer integration tests, very few E2E tests, because higher-level tests are slower and more brittle.

**Q. Mock vs stub vs spy?**
A. A stub returns canned data. A mock also verifies interactions. A spy wraps a real object and lets you override parts of it.

**Q. @Mock vs @MockitoBean (@MockBean)?**
A. @Mock is a plain Mockito mock for unit tests without Spring. @MockitoBean replaces a bean inside the Spring test context.

**Q. @WebMvcTest vs @SpringBootTest?**
A. @WebMvcTest loads only the web layer (fast, services mocked). @SpringBootTest loads the full application context.

**Q. Why Testcontainers instead of H2?**
A. It tests against the same database engine as production (SQL dialect, types, constraints, migrations), so the tests are trustworthy.

**Q. How do you test a method that throws?**
A. `assertThrows(Expected.class, () -> call())`, then assert on the message or state, and verify that no side effects happened.

**Q. What is TDD?**
A. Test-Driven Development: write a failing test (red), write the minimum code to pass (green), then refactor. Repeat.

**Q. What does good code coverage mean?**
A. Coverage shows which code ran during tests, not whether behaviour was verified. Aim for meaningful tests of critical logic rather than a number.

**Q. How do you test a React component?**
A. Render it with React Testing Library, interact as a user (type, click), assert on visible output, and mock the API with MSW.
