# Prompt: Java + Spring Boot interview guide (Beginner → Intermediate → Advanced)

> To run: open Claude Code in `D:\mylearn\Plans` and say `Read java-spring-prompt.md and follow it`.
> If a session stops early, say the same thing again. It resumes where it stopped.

## 1. Role and goal

You are a senior Java / Spring Boot engineer who has run hundreds of backend interviews, and a patient teacher. Build a progressive study guide that takes me from Java basics to answering senior-level Java and Spring Boot interview questions with confidence.

- I am preparing for **Java + Spring Boot backend interviews**. Every topic must end with me able to *explain it aloud*, *write the code*, and *survive the follow-up questions*.
- **Every topic gets a full study note with example code.** No topic is a bare title or a list of links. This includes the practice rows (drills, reviews, mock interviews): they must contain code too.
- This guide is my **one-stop place** for Java and Spring Boot. The notes must be complete enough that I never need another website, book or video to understand a topic.
- Three levels, in order: **Beginner → Intermediate → Advanced**. Spring Boot appears at every level, not only at the end.
- I love analogies. Open every concept with an everyday analogy and map its parts to the technical parts, but only where the analogy really helps. Never force a weak one.
- Versions: **Java 21 (LTS)** and **Spring Boot 3.5 / Spring Framework 6.2** are the baseline. Mark newer features clearly: Java 22–25 features as `Java 25+`, Spring Boot 4 / Framework 7 changes as `Boot 4`. Interviewers still ask about Java 8/11/17 changes, so say which version introduced each feature.

The result is a CSV that I import into my Learning Tracker app, in the same format as `plan-example.csv`.

## 2. Deliverables

| Path | Purpose |
|---|---|
| `guide-java-spring/00-orientation.md` | How to use the guide, the full roadmap, interview strategy, setup |
| `guide-java-spring/01-…md` to `guide-java-spring/23-…md` | Source files per phase from section 9; a big phase may be split into parts (`02-oop-1.md`, `02-oop-2.md`, …) that sort in order |
| `guide-java-spring/_sections.txt` | The required headings from section 6, one per line |
| `java-spring-plan.csv` | Built with `python -X utf8 build_plan_csv.py guide-java-spring java-spring-plan.csv` |

Never write the CSV by hand. Do not change `build_plan_csv.py`, `plan-example.csv`, `guide/` (the older PKSF guide) or any other plan. Do not commit.

## 3. How to work

1. Read `plan-example.csv`, `build_plan_csv.py`, and one finished topic from `guide-dsa-300/01-arrays-hashing.md` for the format, depth and analogy style.
2. If `guide-java-spring/` exists, find the last complete topic and continue after it. Do not rewrite finished topics.
3. Otherwise write `00-orientation.md` and `_sections.txt` first, then the phases in order.
4. Write **one phase at a time**. After each phase, run the checks in section 10, fix any failures, and report one line of progress (phase, topics written, build result).
5. Keep the last topic as detailed as the first. Never write "similar to above", "TODO", "see previous" or "...". If you run low on room, finish the current topic cleanly and stop.

## 4. Row format

Each row starts with a marker line. Everything up to the next marker is that row's Markdown note.

```text
@@ level | TYPE | name | description | PRIORITY
```

| Row | level | TYPE | name | PRIORITY |
|---|---|---|---|---|
| Phase | 1 | MODULE | `Phase N — Title (Level)` e.g. `Phase 4 — Collections Framework (Beginner)` | MUST |
| Group | 2 | MODULE | the bold group name from section 9 | MUST |
| Concept topic | 3 | TOPIC | the topic name from section 9 | as given in section 9 |
| Coding drill | 3 | TOPIC | `Practice — Coding drill: …` | MUST |
| Phase review | 3 | TOPIC | `Practice — Phase N review: …` | MUST |
| Mock interview | 3 | TOPIC | `Practice — Mock interview: …` | MUST |
| Orientation | 3 | TOPIC | `Practice — Start here: …` | MUST |

- MUST = asked in most interviews; SHOULD = asked often at mid level; NICE = senior / differentiator.
- Names starting with `Practice` skip the builder's heading check, so use that prefix only for the rows above.
- `description` is one line: what the topic answers in an interview. No `|` in a name or description.
- A TOPIC never has children. Keep rows in section 9 order. Topic names are permanent (note links use them).

## 5. Module notes

**Phase note:** what this phase covers and why interviewers care; a small `text` or Mermaid map of how its topics connect; prerequisites from earlier phases; the 5 questions from this phase that come up most often.

**Group note:** a 5–10 line overview that ties the group's topics together with one shared analogy.

## 6. Concept topic note

Use exactly this structure:

````markdown
# HashMap Internals

**Level: Intermediate | Phase 8 | Priority: MUST | Since: Java 1.2 (treeification Java 8)**

> **30-second interview answer:** two or three sentences I could say word for word when asked "What is X?"

## 1. Concept in Simple Words
### Analogy
An everyday analogy, then a table mapping each part of the analogy to the technical part.
### What it is
Plain-English definition. Define every new term the first time it appears.
### Why it exists
The problem it solves, and what life looked like without it.

## 2. How It Works
The internals, step by step: memory layout, call flow, lifecycle, algorithm.
At least one diagram: Mermaid (sequence, flow, class) or a `text` diagram.
For Spring topics: what Spring does behind the annotation (proxies, bean post-processors, filters, auto-config).
For JVM / concurrency topics: what happens in memory and between threads.

## 3. Code Example
### Code
A complete, compilable Java 21 example (imports and class included), with comments.
For Spring: the controller / service / config / `application.yml` pieces needed, nothing left out.
### Walkthrough
Explain the code block by block: what each part does and why.
### Output
The exact console output or HTTP request and response.
### Compare
Where useful, the wrong or old way next to the right or modern way, and why it matters.

## 4. Interview Questions
6–10 questions, from basic to tricky, each with a model answer of 2–6 sentences:
- **Basic** — the definition questions
- **Follow-up** — "why?", "what happens if…?", "how does it work inside?"
- **Tricky / scenario** — the trap questions, output-prediction snippets, "in production we saw X, why?"
End with **What the interviewer is really testing** in one or two lines.

## 5. Pitfalls & Best Practices
### Common mistakes
A table: mistake, what goes wrong, fix. At least three.
### Best practices
What a senior engineer does in production.
### Compare with
A table against the closest alternatives (e.g. HashMap vs Hashtable vs ConcurrentHashMap).

## 6. Hands-on Exercise
A small task for me to do, with acceptance criteria.
From Phase 5 on, Spring tasks extend the single running project **OrderHub** (section 7).
Put the solution in a `<details><summary>Solution</summary> … </details>` block.

## 7. Recall
Q: …
A: …
````

`_sections.txt` contains exactly:

```text
## 1. Concept in Simple Words
## 2. How It Works
## 3. Code Example
## 4. Interview Questions
## 5. Pitfalls & Best Practices
## 6. Hands-on Exercise
## 7. Recall
```

- 4–6 Q/A pairs per topic. They become flashcards, so each question must make sense on its own and name the topic.
- End each note with a **Related** line linking 2–4 other topics, like `[[equals() and hashCode() Contract]]`.

Deepen the notes as the levels progress:

- **Beginner:** define every term, trace every step, explain each Java API on first use. Interview questions focus on definitions and differences.
- **Intermediate:** shorter basics; more on internals, trade-offs and "what happens if". Interview questions add follow-ups and output prediction.
- **Advanced:** explain like a senior: design decisions, production failures, performance numbers, tuning, and how to reason about a scenario you have never seen. Interview questions are mostly scenario-based.

## 7. Running project and practice notes

**OrderHub** is one Spring Boot project that grows through the whole guide (an online order management backend: products, customers, orders, payments, notifications).

| Phase | What OrderHub gains |
|---|---|
| 5 | In-memory REST CRUD for products, layered structure |
| 10–11 | Proper DI, config properties, validation, global errors, OpenAPI |
| 12 | PostgreSQL with JPA, relationships, Flyway, transactions |
| 13 | Unit, slice and Testcontainers tests |
| 14 | JWT login, roles, method security |
| 18–19 | A custom starter, caching with Redis |
| 20 | Split into order, inventory and notification services behind a gateway, with Kafka events and Resilience4j |
| 21 | Logs, metrics, tracing, Docker image, Kubernetes manifests |

Each lab task names the files to create or change. Lab code lives in the notes; do not create the project on disk.

**Coding drill** (`Practice — Coding drill: …`): 8–12 short interview coding tasks on the phase (e.g. "group employees by department with streams", "write a thread-safe singleton", "fix this N+1"). Each has the task, a hint, then the solution and explanation in a `<details>` block.

**Phase review** (`Practice — Phase N review: …`): a one-page cheat sheet (tables, key rules, the answers to memorize, and a short code snippet for each key idea), the 10 most likely interview questions with short answers, and 10 rapid-fire Q/A flashcards.

**Mock interview:** a 45-minute script: warm-up, concept questions, a coding task, a scenario question and a design question, each with the model answer and a scoring rubric (what earns weak, good or strong).

## 8. Rendering rules for my app

- Each note starts with a `#` heading. No line inside a note starts with `@@`.
- Tag every code fence (`java`, `yaml`, `properties`, `xml`, `sql`, `bash`, `http`, `text`, `mermaid`) and close it.
- A single newline renders as a line break, so do not hard-wrap paragraphs.
- Put generics, arrays and annotations in backticks in prose: `List<String>` loses its type as HTML, and `[[x]]` becomes a note link.
- No `|` inside table cells, even in backticks. Write "or" instead.
- Do not use `$...$`; it renders as KaTeX math.
- Mermaid labels in quotes. Use `text` diagrams for memory layouts and thread timelines.
- `Q:` and `A:` start a line only for flashcards. Leave a blank line after each answer, and none inside it.

## 9. Fixed outline

Follow it exactly. Do not add, drop, rename or reorder topics. Format: topic name · priority. Bold lines are level-2 groups.

### Phase 0 — Orientation
**Start here**
- Practice — Start here: How to use this guide · MUST
- Practice — Start here: Java and Spring Boot interview roadmap · MUST
- Practice — Start here: Setup (JDK 21, IntelliJ, Maven, Docker) · MUST

### BEGINNER

### Phase 1 — Java Foundations (Beginner)
**How Java runs**
- JDK, JRE and JVM · MUST
- Compiling and Running a Program · MUST
**Language basics**
- Primitive Types and Variables · MUST
- Wrapper Classes, Autoboxing and Caching · MUST
- Operators, Casting and Type Promotion · MUST
- Control Flow and Loops · MUST
- Arrays · MUST
- Methods and Pass-by-Value · MUST
- Practice — Coding drill: Java basics
- Practice — Phase 1 review: Java Foundations

### Phase 2 — Object-Oriented Programming (Beginner)
**Classes and objects**
- Classes, Objects and Constructors · MUST
- this, static and Initialization Order · MUST
- Packages and Access Modifiers · MUST
**The four pillars**
- Encapsulation · MUST
- Inheritance and super · MUST
- Polymorphism: Overloading vs Overriding · MUST
- Abstraction: Abstract Classes vs Interfaces · MUST
- Interface Default, Static and Private Methods · SHOULD
**More building blocks**
- The final Keyword · MUST
- The Object Class Methods · MUST
- equals() and hashCode() Contract · MUST
- Composition vs Inheritance · MUST
- Nested, Inner and Anonymous Classes · SHOULD
- Enums · MUST
- Records · MUST
- Immutable Class Design · MUST
- Practice — Coding drill: OOP design
- Practice — Phase 2 review: OOP

### Phase 3 — Exceptions, Strings and Core APIs (Beginner)
**Exceptions**
- Exception Hierarchy: Checked vs Unchecked · MUST
- try, catch, finally and try-with-resources · MUST
- Custom Exceptions and Best Practices · MUST
**Core APIs**
- String Immutability and the String Pool · MUST
- StringBuilder and StringBuffer · MUST
- BigDecimal and Money · SHOULD
- Date and Time API (java.time) · SHOULD
- File I/O with java.nio.file · SHOULD
- Practice — Coding drill: Strings and exceptions
- Practice — Phase 3 review: Exceptions and Core APIs

### Phase 4 — Collections Framework (Beginner)
**Overview**
- Collections Hierarchy and Choosing a Collection · MUST
**Lists, sets and maps**
- ArrayList vs LinkedList · MUST
- HashSet, LinkedHashSet and TreeSet · MUST
- HashMap, LinkedHashMap and TreeMap · MUST
- Queue, Deque and PriorityQueue · MUST
**Working with collections**
- Iterator and Fail-Fast vs Fail-Safe · MUST
- Comparable vs Comparator · MUST
- Collections and Arrays Utility Classes · SHOULD
- Immutable and Unmodifiable Collections · MUST
- Practice — Coding drill: Collections
- Practice — Phase 4 review: Collections

### Phase 5 — Spring Boot Foundations (Beginner)
**First steps**
- What Spring and Spring Boot Are · MUST
- Maven Basics: pom.xml, Lifecycle and Scopes · MUST
- Spring Initializr and Project Structure · MUST
- @SpringBootApplication Explained · MUST
**Your first API**
- First REST Controller · MUST
- Path Variables, Request Params and Request Body · MUST
- application.properties and application.yml · MUST
- Layered Architecture: Controller, Service, Repository · MUST
- DTOs and Mapping · MUST
- Lombok: Uses and Risks · NICE
- Practice — Coding drill: Build OrderHub v1 (in-memory CRUD)
- Practice — Phase 5 review: Spring Boot Foundations
- Practice — Mock interview: Beginner round

### INTERMEDIATE

### Phase 6 — Generics and Modern Language Features (Intermediate)
**Generics**
- Generics, Bounded Types and Generic Methods · MUST
- Wildcards and PECS · SHOULD
- Type Erasure · SHOULD
**Metaprogramming**
- Annotations and Custom Annotations · MUST
- Reflection · SHOULD
**Modern Java**
- var, Switch Expressions and Text Blocks · MUST
- Sealed Classes and Pattern Matching · MUST
- Java Version Features: 8 to 25 · MUST
- Practice — Coding drill: Generics and modern Java
- Practice — Phase 6 review: Generics and Modern Java

### Phase 7 — Functional Java and Streams (Intermediate)
**Functional basics**
- Lambdas and Functional Interfaces · MUST
- Built-in Functional Interfaces · MUST
- Method References · MUST
**Streams**
- Stream Pipeline: Lazy Evaluation · MUST
- Intermediate and Terminal Operations · MUST
- Collectors: groupingBy, partitioningBy, toMap · MUST
- flatMap and Reduce · MUST
- Optional Done Right · MUST
- Parallel Streams and Their Traps · SHOULD
- Practice — Coding drill: Stream interview problems (25 tasks)
- Practice — Phase 7 review: Functional Java and Streams

### Phase 8 — Collections Internals (Intermediate)
**Inside the maps**
- HashMap Internals · MUST
- LinkedHashMap and LRU Cache · SHOULD
- TreeMap and Red-Black Trees · SHOULD
- ConcurrentHashMap Internals · MUST
**Edge cases**
- ConcurrentModificationException · MUST
- EnumMap, WeakHashMap and IdentityHashMap · NICE
- Practice — Coding drill: Collections internals
- Practice — Phase 8 review: Collections Internals

### Phase 9 — Concurrency Fundamentals (Intermediate)
**Threads**
- Processes, Threads and Thread Lifecycle · MUST
- Runnable vs Callable · MUST
**Shared state**
- Race Conditions and synchronized · MUST
- volatile and Visibility · MUST
- wait, notify and notifyAll · SHOULD
- Deadlock, Livelock and Starvation · MUST
- Atomic Classes and CAS · MUST
**Executors**
- ExecutorService and Thread Pools · MUST
- Future and CompletableFuture · MUST
- Concurrent Collections and BlockingQueue · MUST
- Practice — Coding drill: Concurrency problems
- Practice — Phase 9 review: Concurrency Fundamentals

### Phase 10 — Spring Core in Depth (Intermediate)
**The container**
- IoC and Dependency Injection · MUST
- ApplicationContext and Bean Definitions · MUST
- Constructor vs Setter vs Field Injection · MUST
- @Qualifier, @Primary and Multiple Beans · MUST
**Beans**
- Stereotype Annotations · MUST
- @Configuration and @Bean · MUST
- Bean Scopes · MUST
- Bean Lifecycle Callbacks · MUST
**Configuration**
- Profiles and Environment-Specific Config · MUST
- @Value vs @ConfigurationProperties · MUST
- Conditional Beans · SHOULD
- Spring Application Events · SHOULD
- Practice — Coding drill: Spring Core
- Practice — Phase 10 review: Spring Core

### Phase 11 — Building REST APIs (Intermediate)
**REST design**
- REST Principles, HTTP Methods and Status Codes · MUST
- Idempotency and Safe Methods · MUST
**Spring MVC**
- @RestController and Request Mapping Deep Dive · MUST
- ResponseEntity and Headers · MUST
- Bean Validation · MUST
- Global Exception Handling and ProblemDetail · MUST
- Pagination and Sorting · MUST
- API Versioning · SHOULD
- OpenAPI with springdoc · SHOULD
- File Upload and Download · SHOULD
- CORS · MUST
**Calling other APIs**
- RestClient, WebClient and HTTP Interfaces · MUST
- Practice — Coding drill: REST API scenarios
- Practice — Phase 11 review: REST APIs

### Phase 12 — Data Access with Spring Data JPA (Intermediate)
**Foundations**
- JDBC, JdbcTemplate and JdbcClient · SHOULD
- JPA vs Hibernate vs Spring Data JPA · MUST
- Entity Mapping · MUST
**Relationships**
- One-to-One, One-to-Many and Many-to-Many · MUST
- Owning Side, Cascade and orphanRemoval · MUST
- Fetch Types and the N+1 Problem · MUST
**Repositories**
- Repositories and Derived Queries · MUST
- @Query, JPQL and Native Queries · MUST
- Projections and DTO Queries · SHOULD
**Transactions and state**
- @Transactional and Propagation · MUST
- Isolation Levels and Read Phenomena · MUST
- Persistence Context and Entity States · MUST
- Optimistic vs Pessimistic Locking · MUST
- Schema Migrations with Flyway · MUST
- Auditing · SHOULD
- Practice — Coding drill: JPA scenarios
- Practice — Phase 12 review: Spring Data JPA

### Phase 13 — Testing (Intermediate)
**Unit tests**
- JUnit 5 Essentials · MUST
- Mockito: Mocks, Stubs and Verification · MUST
**Spring tests**
- @SpringBootTest vs Test Slices · MUST
- MockMvc for Controllers · MUST
- @DataJpaTest for Repositories · MUST
- Testcontainers · SHOULD
- Test Pyramid and TDD · SHOULD
- Practice — Coding drill: Write the tests
- Practice — Phase 13 review: Testing

### Phase 14 — Spring Security (Intermediate)
**Architecture**
- Authentication vs Authorization · MUST
- Security Filter Chain Architecture · MUST
- UserDetailsService and Password Encoding · MUST
- SecurityFilterChain Configuration · MUST
**Modern auth**
- JWT Stateless Authentication · MUST
- OAuth2 and OpenID Connect · MUST
- Method Security with @PreAuthorize · SHOULD
- CSRF, CORS and Common Vulnerabilities · MUST
- Practice — Coding drill: Secure OrderHub
- Practice — Phase 14 review: Spring Security
- Practice — Mock interview: Intermediate round

### ADVANCED

### Phase 15 — JVM Internals and Performance (Advanced)
**The JVM**
- Class Loading and ClassLoaders · MUST
- JVM Memory Areas · MUST
- JIT Compilation and Escape Analysis · NICE
- Java Memory Model and Happens-Before · MUST
**Garbage collection**
- Garbage Collection Basics and Generations · MUST
- G1, ZGC, Shenandoah and Parallel GC · SHOULD
- GC Tuning and JVM Flags · SHOULD
**Diagnosing problems**
- Memory Leaks and OutOfMemoryError · MUST
- Heap Dumps, Thread Dumps and Profiling (JFR, jcmd) · SHOULD
- Practice — Coding drill: Diagnose the JVM
- Practice — Phase 15 review: JVM Internals

### Phase 16 — Advanced Concurrency (Advanced)
**Locks and coordination**
- ReentrantLock, ReadWriteLock and StampedLock · MUST
- CountDownLatch, CyclicBarrier, Semaphore and Phaser · SHOULD
- ForkJoinPool and Work Stealing · SHOULD
**Modern concurrency**
- CompletableFuture Composition and Error Handling · MUST
- Virtual Threads · MUST
- Structured Concurrency and Scoped Values · NICE
- ThreadLocal and Its Leaks · SHOULD
- Designing Thread-Safe Classes · MUST
- Practice — Coding drill: Classic concurrency problems
- Practice — Phase 16 review: Advanced Concurrency

### Phase 17 — Design Principles and Patterns (Advanced)
**Principles**
- SOLID Principles · MUST
- DRY, KISS, YAGNI and Clean Code · SHOULD
**Patterns**
- Singleton Variants · MUST
- Factory and Abstract Factory · MUST
- Builder · MUST
- Adapter, Decorator and Facade · SHOULD
- Proxy · MUST
- Strategy, Template Method and Observer · MUST
- Chain of Responsibility · SHOULD
- Design Patterns Inside Spring · MUST
- Practice — Coding drill: Low-level design problems
- Practice — Phase 17 review: Design Patterns

### Phase 18 — Spring Internals (Advanced)
**Startup**
- ApplicationContext Startup Lifecycle · MUST
- BeanPostProcessor and BeanFactoryPostProcessor · SHOULD
- Circular Dependencies · MUST
**Proxies and AOP**
- Spring AOP Concepts · MUST
- JDK Dynamic Proxy vs CGLIB · MUST
- How @Transactional Really Works · MUST
**Boot internals**
- Auto-Configuration Internals · MUST
- Writing a Custom Starter · SHOULD
- DispatcherServlet Request Lifecycle · MUST
- Startup Performance, AOT and GraalVM Native Images · NICE
- Practice — Coding drill: Spring internals puzzles
- Practice — Phase 18 review: Spring Internals

### Phase 19 — Advanced Data and Caching (Advanced)
**Performance**
- Connection Pooling with HikariCP · MUST
- Query Performance and Indexes · MUST
- Batch Inserts and Updates · SHOULD
**Caching**
- Spring Cache Abstraction · MUST
- Redis with Spring Boot · MUST
- Hibernate Second-Level Cache · NICE
**Scale**
- Multiple Datasources and Read Replicas · NICE
- Spring Batch · NICE
- Practice — Coding drill: Data performance scenarios
- Practice — Phase 19 review: Advanced Data and Caching

### Phase 20 — Microservices with Spring Cloud (Advanced)
**Design**
- Monolith vs Microservices · MUST
- Service Decomposition and DDD Basics · SHOULD
**Infrastructure**
- API Gateway with Spring Cloud Gateway · MUST
- Service Discovery · MUST
- Centralized Configuration · SHOULD
- Service-to-Service Calls: OpenFeign and HTTP Interfaces · MUST
**Reliability**
- Resilience4j: Circuit Breaker, Retry, Rate Limiter, Bulkhead · MUST
- Distributed Transactions, Saga and Outbox · MUST
**Messaging**
- Event-Driven Architecture with Kafka · MUST
- RabbitMQ Basics · NICE
- Securing Service-to-Service Communication · SHOULD
- Practice — Coding drill: Split OrderHub into services
- Practice — Phase 20 review: Microservices

### Phase 21 — Observability and Production (Advanced)
**Observability**
- Spring Boot Actuator · MUST
- Logging with SLF4J, Logback and MDC · MUST
- Metrics with Micrometer and Prometheus · SHOULD
- Distributed Tracing with OpenTelemetry · SHOULD
**Deployment**
- Graceful Shutdown and Health Probes · SHOULD
- Docker Images for Spring Boot · MUST
- Kubernetes Basics for Spring Developers · SHOULD
- CI/CD Pipeline for a Spring App · NICE
- Practice — Coding drill: Production incidents
- Practice — Phase 21 review: Observability and Production

### Phase 22 — Reactive and What's New (Advanced)
**Reactive**
- Reactive Programming and Project Reactor · SHOULD
- Spring WebFlux vs Spring MVC · SHOULD
- Backpressure and R2DBC · NICE
- Virtual Threads vs Reactive · SHOULD
**What's new**
- Spring Boot 4 and Spring Framework 7 Changes · SHOULD
- Practice — Phase 22 review: Reactive and What's New

### Phase 23 — Interview Mastery (Advanced)
**Rounds**
- Practice — Coding drill: Java tricky output questions (30)
- Practice — Coding drill: Spring Boot scenario questions (30)
- Practice — Coding drill: System design with Spring Boot (URL shortener, order service, notification service)
- Practice — Coding drill: Explain your project (STAR and architecture walkthrough)
- Practice — Mock interview: Advanced round
- Practice — Mock interview: Full loop (Java, Spring, coding, design)
- Practice — Phase 23 review: Last 48 hours cheat sheet

## 10. Checks after each phase

1. `python -X utf8 build_plan_csv.py guide-java-spring java-spring-plan.csv` succeeds.
2. Every topic in the phase matches section 9 by name and order.
3. Every pure-Java example (no Spring) is copied to the scratchpad and compiled with `javac --release 21`; fix any error. Spring examples are not compiled, so re-read them for missing imports, annotations and config.
4. No `TODO`, `...` placeholders, `similar to above`, or `|` inside table cells.
5. Each concept topic has 6–10 interview questions, at least 3 mistakes, a hands-on exercise with a solution, 4–6 Q/A pairs and a Related line.
