@@ 1 | MODULE | Phase 0 — Orientation | How to use this guide, the roadmap and your setup | MUST
# Phase 0: Orientation

This guide takes you from Java basics to senior-level Java and Spring Boot interview answers. It has 23 phases in three levels: Beginner, Intermediate and Advanced. Spring Boot appears at every level.

## The big analogy: learning to drive, then to race

| Driving | This guide |
|---|---|
| Learning the pedals and the steering wheel | Beginner: Java syntax, OOP, collections, a first Spring Boot API |
| Driving in city traffic with rules and other cars | Intermediate: streams, concurrency, Spring Core, REST, JPA, testing, security |
| Knowing the engine and racing on a track | Advanced: JVM, garbage collection, Spring internals, microservices, production |
| The driving test | Mock interviews and the final cheat sheet |

```text
Beginner           Intermediate                    Advanced
Ph 1-4 Java   -->  Ph 6-9 Java deeper        -->   Ph 15-17 JVM, concurrency, patterns
Ph 5 Spring   -->  Ph 10-14 Spring in depth  -->   Ph 18-22 Spring internals, cloud, prod
                                                   Ph 23 Interview mastery
```

@@ 2 | MODULE | Start here | Read these three notes before Phase 1 | MUST
# Start here

Three short notes: how each topic note is built, the interview roadmap, and how to set up your machine. Read them once, then start Phase 1.

@@ 3 | TOPIC | Practice — Start here: How to use this guide | The seven sections of every note and a study routine | MUST
# How to use this guide

## Every concept note has the same seven sections

| Section | What it gives you | How to use it |
|---|---|---|
| 30-second interview answer | Two or three sentences to say word for word | Say it aloud until it is natural |
| 1. Concept in Simple Words | An analogy, a definition and the reason the thing exists | Read first, slowly |
| 2. How It Works | What happens inside, with a diagram | This is what separates good answers from great ones |
| 3. Code Example | A complete program, a walkthrough and the output | Type it yourself, do not copy and paste |
| 4. Interview Questions | 6 to 10 questions with model answers | Cover the answer, answer aloud, then check |
| 5. Pitfalls & Best Practices | Common mistakes and senior habits | The "tricky" questions come from here |
| 6. Hands-on Exercise | A task with a hidden solution | Try it before opening the solution |
| 7. Recall | Flashcards | Your tracker turns them into spaced-repetition cards |

## A daily routine that works

1. **Read** one or two topics (30 to 45 minutes).
2. **Type and run** the code example. Change one thing and predict the result before running it.
3. **Answer** the interview questions aloud, like a real interview.
4. **Do** the exercise.
5. **Review** yesterday's flashcards.

Like a gym plan: short sessions every day beat one long session a week.

## Priorities

| Tag | Meaning | If you are short on time |
|---|---|---|
| MUST | Asked in most interviews | Never skip |
| SHOULD | Asked often at mid level | Do on the second pass |
| NICE | Senior or "wow" topics | Do for senior roles |

## The running project: OrderHub

From Phase 5 on, every Spring exercise grows one project, **OrderHub**, an online order backend. You start with in-memory CRUD and end with microservices, Kafka, tracing and Docker. In an interview you can then say "In my project I did X, and this is why."

## A first taste of code

Every topic contains code like this. Read the comments: they explain each line.

```java
// File: Hello.java
public class Hello {
    public static void main(String[] args) {
        // args holds anything typed after the program name
        String name = args.length > 0 ? args[0] : "interviewer";
        System.out.println("Hello, " + name + "!");
    }
}
```

Run it with `java Hello.java Sam` and it prints `Hello, Sam!`. Since Java 11 you can run a single source file without compiling it first.

@@ 3 | TOPIC | Practice — Start here: Java and Spring Boot interview roadmap | What interviews test at each level and how this guide maps to it | MUST
# Java and Spring Boot interview roadmap

## What a typical backend interview loop looks like

| Round | What they test | Phases in this guide |
|---|---|---|
| Screening (30 min) | Core Java definitions, OOP, collections | 1 to 4, 6 to 8 |
| Java deep dive | Streams, concurrency, JVM, tricky output questions | 7, 9, 15, 16, 23 |
| Spring Boot round | DI, REST, JPA, transactions, security, testing | 5, 10 to 14, 18 |
| Coding round | DSA (your DSA 300 plan) and Java-specific coding | Drills in every phase |
| Design round | Low-level design, microservices, system design | 17, 19, 20, 21, 23 |
| Behavioral | Your project, conflicts, ownership | 23 |

## What each level is expected to know

| Level | Interviewer expects |
|---|---|
| Junior (0 to 2 years) | OOP, collections, exceptions, a REST API with JPA, basic tests |
| Mid (2 to 5 years) | Streams, concurrency basics, transactions and N+1, security with JWT, testing slices |
| Senior (5+ years) | JVM and GC, Spring internals, microservices patterns, production debugging, trade-offs |

## The top 15 questions you will almost certainly hear

1. Difference between JDK, JRE and JVM. (Phase 1)
2. Is Java pass-by-value or pass-by-reference? (Phase 1)
3. Why are strings immutable? (Phase 3)
4. The equals and hashCode contract. (Phase 2)
5. How does HashMap work internally? (Phase 8)
6. ArrayList vs LinkedList. (Phase 4)
7. Checked vs unchecked exceptions. (Phase 3)
8. What is a functional interface? (Phase 7)
9. synchronized vs volatile. (Phase 9)
10. What is dependency injection and why use constructor injection? (Phase 10)
11. What does @SpringBootApplication do? (Phase 5)
12. How does @Transactional work and when does it not? (Phases 12 and 18)
13. What is the N+1 problem and how do you fix it? (Phase 12)
14. How does JWT authentication work in Spring Security? (Phase 14)
15. Monolith vs microservices, and how services talk. (Phase 20)

## How to answer any concept question: the D-W-E-T formula

1. **Define** it in one sentence.
2. **Why** it exists: the problem it solves.
3. **Example** from code or your project.
4. **Trade-off** or pitfall: shows real experience.

Example for "What is a HashMap?":

```text
Define:    A key-value store with average O(1) get and put.
Why:       Lookup by key without scanning a list.
Example:   In OrderHub I cache products by id in a Map<Long, Product>.
Trade-off: Keys need correct equals/hashCode; not thread-safe, so
           I use ConcurrentHashMap when threads share it.
```

```java
import java.util.HashMap;
import java.util.Map;

public class RoadmapDemo {
    public static void main(String[] args) {
        Map<Long, String> products = new HashMap<>();
        products.put(1L, "Keyboard");          // put: average O(1)
        System.out.println(products.get(1L));  // get: average O(1) -> Keyboard
    }
}
```

@@ 3 | TOPIC | Practice — Start here: Setup (JDK 21, IntelliJ, Maven, Docker) | Install the tools and check them with one command each | MUST
# Setup: JDK 21, IntelliJ, Maven and Docker

## What to install

| Tool | Why | Check it with |
|---|---|---|
| JDK 21 or newer (Temurin) | Compiles and runs Java | `java -version` |
| IntelliJ IDEA (Community is fine) | Editor with Spring support | open it |
| Maven 3.9+ (or the `mvnw` wrapper in each project) | Builds Spring Boot projects | `mvn -v` |
| Docker Desktop | PostgreSQL, Redis and Kafka for later phases | `docker version` |
| Git | Version control | `git --version` |
| curl or Postman | Calling your REST APIs | `curl --version` |

Your machine already has JDK 25. That is fine: compile with `--release 21` to make sure the code also works on Java 21, the version most companies run.

```bash
java -version                       # shows the runtime
javac --release 21 Hello.java       # compile targeting Java 21
java Hello                          # run the compiled class
```

## The project you will create in Phase 5

```bash
# Generates a Spring Boot project from the command line
curl https://start.spring.io/starter.zip -d dependencies=web,validation,actuator \
     -d javaVersion=21 -d type=maven-project -d name=orderhub -o orderhub.zip
```

## Databases for later phases

```yaml
# docker-compose.yml: start with `docker compose up -d`
services:
  postgres:
    image: postgres:17
    environment:
      POSTGRES_DB: orderhub
      POSTGRES_USER: orderhub
      POSTGRES_PASSWORD: orderhub
    ports: ["5432:5432"]
  redis:
    image: redis:7
    ports: ["6379:6379"]
```

## A check program

```java
// File: SetupCheck.java  ->  run: java SetupCheck.java
public class SetupCheck {
    public static void main(String[] args) {
        System.out.println("Java version: " + Runtime.version());
        System.out.println("CPUs: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Max heap MB: " + Runtime.getRuntime().maxMemory() / 1024 / 1024);
    }
}
```

If it prints a version of 21 or higher, you are ready for Phase 1.
