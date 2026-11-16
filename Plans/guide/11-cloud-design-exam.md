@@ 1 | MODULE | Phase 11 — Cloud (Optional) | Cloud models and the AWS/GCP/Azure services that map to this stack | NICE
# Phase 11: Cloud (optional)

The TOR lists cloud as **optional**. Knowing the basic ideas and which managed service replaces which part of your stack is enough.

@@ 2 | TOPIC | Cloud Fundamentals and Service Mapping | IaaS/PaaS/SaaS, regions, managed services, shared responsibility | NICE
# Cloud fundamentals

## Service models

| Model | You manage | Provider manages | Example |
|---|---|---|---|
| **IaaS** | OS, runtime, app, data | Hardware, network, virtualization | AWS EC2, Azure VM |
| **PaaS** | App + data | Everything below | AWS Elastic Beanstalk, Azure App Service, Cloud Run |
| **SaaS** | Just use it | Everything | Gmail, Office 365 |

**Analogy (pizza):** IaaS = they give you the kitchen and you cook. PaaS = they give you the dough and oven, and you add toppings. SaaS = pizza delivered to your door.

## Key ideas
- **Region / Availability Zone (AZ):** a region is a geographic area. AZs are separate data centres within it. Deploy across **2+ AZs** for high availability.
- **Elasticity:** scale up or down automatically with load, and pay for what you use.
- **Shared responsibility:** the provider secures the cloud (hardware, data centres). **You** secure what you put **in** the cloud (IAM, data, network rules, patches on your VMs).
- **IAM:** users, roles and policies. Follow **least privilege**, and give apps roles instead of hard-coded keys.

## Mapping our stack to managed services

| Our component | AWS | GCP | Azure |
|---|---|---|---|
| VM | EC2 | Compute Engine | Virtual Machines |
| Kubernetes | EKS | GKE | AKS |
| Run containers simply | ECS/Fargate, App Runner | Cloud Run | Container Apps |
| PostgreSQL | RDS / Aurora | Cloud SQL | Azure Database for PostgreSQL |
| Redis | ElastiCache | Memorystore | Azure Cache for Redis |
| File storage | S3 | Cloud Storage | Blob Storage |
| Container registry | ECR | Artifact Registry | ACR |
| Secrets | Secrets Manager | Secret Manager | Key Vault |
| Messaging | SQS/SNS, MSK (Kafka) | Pub/Sub | Service Bus, Event Hubs |
| Monitoring | CloudWatch | Cloud Monitoring | Azure Monitor |
| CDN | CloudFront | Cloud CDN | Front Door |

## Example: a production-style AWS layout
```
Users → CloudFront (React static files from S3)
      → ALB (HTTPS) → EKS pods (loan-api, in private subnets across 2 AZs)
                          ├── RDS PostgreSQL (Multi-AZ, automated backups)
                          ├── ElastiCache Redis
                          └── Secrets Manager (DB password)
CloudWatch for logs/metrics; IAM roles for pods (IRSA)
```
**Why each piece**
- S3 + CloudFront: cheap, fast global delivery of static frontend files.
- ALB: a managed load balancer that terminates HTTPS.
- **Private subnets:** the app and DB are not directly reachable from the internet.
- RDS **Multi-AZ:** a standby copy in another AZ with automatic failover, plus backups and point-in-time restore without you running pg_dump.
- Secrets Manager + IAM roles: no passwords in code or config files.

**Infrastructure as Code (IaC):** define the cloud resources in code (**Terraform**, CloudFormation) so that environments are reproducible and reviewed like application code.

**Data residency note for PKSF:** financial and personal data may need to stay in Bangladesh (a local data centre / government cloud). Mention that you would check the regulations before choosing a region.

@@ 1 | MODULE | Phase 12 — System Design | Scalability concepts, approach and a full PO loan system case study | SHOULD
# Phase 12: System design

The viva may ask you to "design a loan management system for 200+ POs". They're testing **structured thinking**, not a perfect answer.

@@ 2 | TOPIC | System Design Fundamentals | Scaling, load balancing, caching, replication, sharding, CAP, queues | SHOULD
# System design fundamentals

## Scaling
- **Vertical (scale up):** a bigger machine (more CPU/RAM). Simple, but it has a ceiling and is a single point of failure.
- **Horizontal (scale out):** more machines behind a load balancer. Needs **stateless** apps (sessions in Redis/JWT, files in object storage).
- **Analogy:** vertical = hiring one super-fast cashier. Horizontal = opening more cash counters.

## Building blocks

| Block | Purpose |
|---|---|
| **Load balancer** | Spreads traffic, removes unhealthy instances (round-robin, least connections) |
| **Cache** (Redis, CDN) | Serves hot data fast and reduces DB load |
| **Read replicas** | Serve reports/reads, and the primary handles writes |
| **Sharding / partitioning** | Split data across DBs/partitions (e.g. by PO or by year) |
| **Message queue** | Absorb spikes, async work, decouple services |
| **Object storage** | Files (documents, photos) instead of DB blobs |
| **CDN** | Static assets close to users |

## Key concepts
- **Latency vs throughput:** the time for one request vs the requests per second.
- **Availability:** 99.9% ≈ 8.7 hours of downtime per year. 99.99% ≈ 52 minutes. Achieve it through redundancy (no single point of failure), health checks and failover.
- **CAP:** during a network partition, choose consistency or availability. **Money data → choose consistency.** A dashboard counter → availability is fine.
- **Consistency models:** strong (read your latest write) vs eventual (replicas catch up within seconds).
- **Idempotency:** retried requests must not double-charge.
- **Back-of-the-envelope estimation:**
  200 POs × 500 officers = 100,000 users. 5 million members, 1 repayment per week each ≈ 5M / (6 working days × 8 h × 3600 s) ≈ **30 writes/s on average**, maybe **10× at peak (≈300/s)** on collection days. That is very manageable for one well-tuned PostgreSQL primary. **Don't over-engineer.**

**Analogy:** system design is like planning a city's roads. First estimate the traffic, then decide how many lanes, which bypasses and where to put traffic lights. You don't build a 12-lane highway to a village.

@@ 2 | TOPIC | Approach to Any Design Question | A step-by-step framework to answer in a viva | SHOULD
# How to answer any design question

**Follow these steps out loud.** Interviewers grade the process.

1. **Clarify requirements (2–3 min)**
   - Functional: what must it do? (register members, disburse loans, collect repayments, PAR reports)
   - Non-functional: how many users? Peak load? Availability? Security/compliance? Offline field use?
2. **Estimate scale:** users, requests per second, data size per year. Decide whether "one DB is enough".
3. **High-level design:** draw the boxes: clients → gateway → services → DB/cache/queue.
4. **Data model:** the main tables/entities and their relationships, plus the key indexes.
5. **API design:** 4–6 key endpoints.
6. **Deep dive** into 1–2 hard parts: consistency of payments, reports at scale, security.
7. **Bottlenecks and trade-offs:** what fails first, and how you would scale it. Admit the trade-offs ("I chose X over Y because...").
8. **Operations:** monitoring, backups, deployment, audit.

**Phrases that score well**
- "Let me first clarify the requirements…"
- "For money, I'd prioritise consistency over availability, so a single PostgreSQL primary with ACID transactions."
- "This is read-heavy, so I'd add a cache / read replica / materialized view for reports."
- "To avoid double posting I'd use an idempotency key with a unique constraint."
- "I'd start as a modular monolith and extract services when needed."

**Analogy:** like a doctor's consultation: ask about the symptoms (requirements), examine (estimate), diagnose (design), prescribe (trade-offs), follow up (monitoring).

@@ 2 | TOPIC | Case Study: PO Loan Management System | End-to-end design for PKSF-style microfinance | SHOULD
# Case study: PO loan management system

## 1. Requirements
- **Functional:** manage POs, branches, samities, members; loan products; loan application → approval → disbursement; installment schedules; record repayments (including from field officers' phones); savings; overdue tracking; PAR and portfolio reports for PKSF; audit history.
- **Non-functional:** 100k users; correctness of money (no lost or double payments); role- and PO-based data isolation; 99.9% availability; reports over millions of rows; partly **offline** field collection; full audit trail.

## 2. High-level architecture
```
 Field app (React PWA / mobile)   PO web (React)    PKSF HQ dashboards
            \                         |                  /
             └──────── HTTPS ── API Gateway / Nginx ────┘
                              │   (JWT from Keycloak)
          ┌───────────────────┼──────────────────────────┐
          ▼                   ▼                          ▼
   member module        loan module               collection module
   (members, KYC)   (products, schedule,      (repayments, ledger,
                     approval workflow)          receipts, idempotency)
          └──────────── PostgreSQL primary (ACID) ─────────┘
                              │ streaming replication
                              ▼
                     read replica → reporting module (PAR, MIS, materialized views)
  Redis: cache (POs, products), rate limits      Kafka/outbox: events → SMS, analytics
  Object storage: KYC documents                  Prometheus/Grafana, ELK, backups (PITR)
```
- Start as a **modular monolith** (one deployable, modules with clear boundaries). Extract reporting or notification services later if needed.

## 3. Data model (core)
```sql
po(id, code, name)
branch(id, po_id, name)
samity(id, branch_id, name, meeting_day)
member(id, samity_id, national_id UNIQUE, name, phone, status)
loan_product(id, name, rate, method, max_months)
loan(id, member_id, product_id, principal, outstanding, status, disbursed_on, version)
installment(id, loan_id, seq_no, due_date, amount, paid_amount, UNIQUE(loan_id, seq_no))
ledger_entry(id, loan_id, type, amount, reference UNIQUE, reverses_id, posted_at, posted_by)
audit_log(id, entity, entity_id, action, old_value JSONB, new_value JSONB, user_id, at)
```
- **Indexes:** `member(samity_id)`, `loan(member_id, status)`, partial `installment(due_date) WHERE paid_amount < amount`, `ledger_entry(loan_id, posted_at)`.
- **Multi-tenancy:** every query is scoped by the user's `po_id` (taken from the JWT claim). Optionally use PostgreSQL **Row-Level Security** as a second safety net.

## 4. Key APIs
```http
POST /api/v1/loans                         # apply
POST /api/v1/loans/{id}/approve            # MANAGER only
POST /api/v1/loans/{id}/disburse
POST /api/v1/repayments                    # header: Idempotency-Key
GET  /api/v1/loans/{id}/schedule
GET  /api/v1/reports/par?poId=7&asOf=2025-11-30
```

## 5. Deep dive: recording a repayment safely
```java
@Transactional
public Receipt repay(String idemKey, RepaymentRequest r) {
    Loan loan = loans.findByIdForUpdate(r.loanId()).orElseThrow();      // 1. row lock
    checkPoAccess(loan);                                                 // 2. authorization
    if (r.amount().compareTo(loan.getOutstanding()) > 0) throw new BusinessRuleException("Overpayment");
    ledger.save(LedgerEntry.repayment(loan, r.amount(), idemKey));       // 3. UNIQUE(reference) blocks duplicates
    allocateToInstallments(loan, r.amount());                            // 4. oldest due first
    loan.reduceOutstanding(r.amount());
    outbox.save(new OutboxEvent("RepaymentRecorded", loan.getId()));     // 5. SMS receipt, async
    return Receipt.of(loan, r);
}
```
**Why each step**
1. `SELECT ... FOR UPDATE`: two officers paying the same loan at the same moment can't lose an update.
2. An officer can only touch loans in their own PO (prevents broken access control).
3. A retry with the same idempotency key hits the unique constraint, so the original receipt is returned instead of double-posting.
4. The payment clears the oldest installments first (standard microfinance practice).
5. The outbox guarantees the SMS event is published if, and only if, the payment committed.
- Everything happens in **one ACID transaction**: either all of it or none.

## 6. Offline field collection
- The PWA stores repayments locally (IndexedDB) with a client-generated **UUID idempotency key**, and syncs when back online. Duplicate syncs are harmless because of step 3.

## 7. Reports at scale
- Heavy PAR/portfolio queries run on a **read replica** or **materialized views** refreshed nightly, plus a Redis cache for dashboards. They never slow down the collection workload on the primary.
- Partition `ledger_entry` by year as the data grows.

## 8. Security and operations
- Keycloak (SSO, MFA for managers), roles plus PO scoping, TLS everywhere, encrypted NIDs, immutable ledger, Envers audit.
- Kubernetes with 3+ replicas across zones, HPA, readiness probes, rolling deploys.
- Monitoring: request latency, error rate, DB connections, failed logins. Daily backups plus WAL archiving (PITR), with restores tested.

**Trade-offs to mention:** a monolith first (simpler, ACID) instead of microservices; pessimistic locking on the loan row (correctness over raw throughput, which is fine at about 300 writes/s); eventual consistency for reports (minutes old is acceptable).

@@ 2 | TOPIC | Phase 12 — Interview Questions | Quick Q&A revision for system design | SHOULD
# Phase 12 interview questions

**Q. Vertical vs horizontal scaling?**
A. Vertical means a bigger machine (simple, limited, single point of failure). Horizontal means more machines behind a load balancer (needs stateless services, scales further).

**Q. How do you make a service stateless?**
A. Keep no user session in memory: use JWT or Redis-backed sessions, store files in object storage, and keep all state in the DB/cache.

**Q. How would you design for high availability?**
A. Redundancy at every layer (multiple app instances across zones, DB with a standby/failover), health checks, load balancers, backups, and no single point of failure.

**Q. When do you add a cache?**
A. For read-heavy data that tolerates slight staleness, when the DB is a bottleneck. Plan invalidation and TTLs.

**Q. How do you scale reads vs writes in PostgreSQL?**
A. Reads: replicas, caching, materialized views. Writes: tune queries and indexes, batch, partition, and as a last resort shard by tenant (PO).

**Q. How do you ensure a payment is not processed twice?**
A. An idempotency key with a unique constraint, done in a transaction with row locking, and idempotent consumers for events.

**Q. How do you keep one PO from seeing another PO's data?**
A. A PO id claim in the token, every query filtered by it in the service layer, method-level security checks, and optionally PostgreSQL row-level security.

**Q. What would you monitor in production?**
A. Latency (p95/p99), error rate, throughput, saturation (CPU, memory, DB pool, disk), business metrics (repayments per hour), and security events (failed logins).

@@ 1 | MODULE | Phase 13 — Portfolio Project | Build and publish a PO microfinance system | MUST
# Phase 13: Portfolio project

The TOR **prefers a public GitHub repo** with clean structure, tests, a README and docker-compose. Building one project that uses everything in this guide is the best preparation, and it gives you stories for the viva.

@@ 2 | TOPIC | Project Scope: PO Microfinance Management System | Features, tech stack and repository layout | MUST
# Project scope: PO microfinance management system

## Features (MVP first, then extras)
**MVP**
1. Login with Keycloak (roles: ADMIN, MANAGER, OFFICER), with officers scoped to their PO.
2. CRUD for POs, samities and members (validation, pagination, search).
3. Loan products and loan applications → approval (MANAGER) → disbursement.
4. Installment schedule generation (flat/declining interest via the Strategy pattern).
5. Record repayments with an idempotency key, a ledger, and optimistic/pessimistic locking.
6. Dashboard: total outstanding, overdue, PAR per PO.

**Extras**
- Redis cache for products/POs, a rate limit on login.
- A nightly overdue job with ShedLock.
- Kafka/outbox → notification service (SMS mock).
- A SOAP endpoint for a "bank balance check" integration.
- Audit history with Envers.
- Kubernetes manifests + GitHub Actions CI/CD.

## Tech stack
Java 21 · Spring Boot 3 · Spring Security (resource server) · Spring Data JPA · Flyway · PostgreSQL 16 · Redis · Keycloak · React 18 + TypeScript + Vite · TanStack Query · React Hook Form + Zod · JUnit 5 · Mockito · Testcontainers · Vitest + RTL · Docker · docker-compose · GitHub Actions · (Kubernetes)

## Repository layout
```
po-microfinance/
├── backend/
│   ├── src/main/java/bd/org/pksf/mf/
│   │   ├── common/        (config, security, exceptions, audit)
│   │   ├── member/        (controller, service, repository, entity, dto)
│   │   ├── loan/
│   │   ├── collection/
│   │   └── report/
│   ├── src/main/resources/db/migration/   (Flyway V1__...sql)
│   ├── src/test/java/...                  (unit + Testcontainers tests)
│   └── Dockerfile
├── frontend/
│   ├── src/{api,components,pages,hooks,types}
│   └── Dockerfile
├── keycloak/realm-pksf.json
├── k8s/                     (deployment, service, ingress, hpa)
├── docker-compose.yml
├── .github/workflows/ci.yml
└── README.md
```
**Why this layout**
- **Package by feature** in the backend: each business area is self-contained, which is easy to navigate and easy to split later.
- Migrations live with the code, so the schema is versioned.
- `realm-pksf.json`: a reviewer gets working users and roles with zero manual setup.
- `docker-compose.yml` at the root: **one command runs everything**.

@@ 2 | TOPIC | Milestones | A week-by-week build plan | MUST
# Milestones

| Week | Deliverable | Skills practised |
|---|---|---|
| 1 | Backend skeleton, Flyway schema, Member CRUD with validation and global errors | Spring Boot, JPA, REST, Flyway |
| 2 | Loan products, applications, schedule generation; unit tests with Mockito | OOP/Strategy, BigDecimal, JUnit |
| 3 | Repayments with ledger, idempotency, locking; Testcontainers tests | Transactions, locking, SQL |
| 4 | Keycloak + Spring Security, role and PO scoping; security tests | OAuth2/JWT, method security |
| 5 | React app: login, member list/form, loan screens | React, TS, TanStack Query, RHF+Zod |
| 6 | Dashboard (PAR), Redis caching, overdue job | SQL window functions, Redis, scheduling |
| 7 | Dockerfiles, docker-compose, GitHub Actions CI | Docker, CI/CD |
| 8 | K8s manifests, README polish, demo video, mock interview on your own project | Kubernetes, communication |

**Rules for yourself**
- Commit small and often, with clear messages (it shows your process to reviewers).
- Every feature comes with at least one test.
- After each week, write 5 viva questions about what you built and answer them out loud.

**Analogy:** building a house: foundation (schema, CRUD) → walls (business logic) → locks (security) → paint (UI) → moving trucks (Docker/CI).

@@ 2 | TOPIC | README Checklist | What a reviewer expects to see | MUST
# README checklist

A reviewer gives your repo **2 minutes**. The README must sell it quickly.

```markdown
# PO Microfinance Management System

Loan and collection management for microfinance Partner Organizations.
Spring Boot 3 · React + TypeScript · PostgreSQL · Redis · Keycloak · Docker

![CI](https://github.com/you/po-microfinance/actions/workflows/ci.yml/badge.svg)

## Features
- Member, samity and PO management with validation and search
- Loan lifecycle: apply → approve → disburse → repay, with installment schedules
- Idempotent repayments, immutable ledger, optimistic and pessimistic locking
- Role-based and PO-scoped access with Keycloak (OAuth2/OIDC, JWT)
- PAR dashboard with SQL window functions and Redis caching

## Architecture
(diagram image: React → Nginx → Spring Boot → PostgreSQL/Redis, Keycloak)

## Quick start
    git clone https://github.com/you/po-microfinance.git
    cd po-microfinance
    cp .env.example .env
    docker compose up -d --build
- App: http://localhost  ·  API docs: http://localhost:8080/swagger-ui.html
- Demo users: manager / manager123, officer / officer123

## Running tests
    cd backend && mvn verify       # unit + Testcontainers integration tests
    cd frontend && npm test

## Design decisions
- NUMERIC(14,2) + BigDecimal for money; reversal entries instead of updates
- Modular monolith, packaged by feature
- ...

## Screenshots
```
**Why each section**
- **One-line pitch + stack:** the reviewer knows instantly what it is.
- **CI badge:** proves the tests actually run and pass.
- **Features in business language:** shows you understand the domain.
- **Architecture diagram:** shows system thinking.
- **Quick start in 3 commands + demo users:** a reviewer can actually run it. Most candidates' repos can't be run.
- **Tests section:** the TOR explicitly mentions tests.
- **Design decisions:** your best viva talking points, written down.
- `.env.example`: shows which config is needed **without committing real secrets**.

@@ 1 | MODULE | Phase 14 — Exam and Interview Preparation | Written test strategy, output prediction, viva prep, rapid revision | MUST
# Phase 14: Exam and interview preparation

The final stretch: practise under exam conditions, predict code output, and rehearse your viva answers.

@@ 2 | TOPIC | Written Test Strategy | Time management, MCQ tactics, writing code by hand | MUST
# Written test strategy

## Before the exam
- Do **timed** MCQ sets (e.g. 50 questions in 40 minutes).
- Write 2–3 programs **on paper** every day: string reversal, palindrome, duplicates, binary search, sorting, linked list reverse, second largest, an SQL query with a JOIN + GROUP BY.
- Revise the "Interview Questions" topic of every phase in this guide.

## During the exam
1. **Read every question to the end.** Watch for "NOT", "EXCEPT", "always".
2. **Easy questions first.** Mark the hard ones and come back.
3. MCQs: eliminate the clearly wrong options, then choose. Don't leave blanks unless there's negative marking.
4. **Handwritten code:**
   - Write the method signature first, then handle the **edge cases** (null, empty, a single element).
   - Use clear variable names and consistent indentation.
   - Add one line of comment for the idea ("two pointers from both ends").
   - Dry-run with a small example in the margin.
   - State the time/space complexity at the end; it earns extra credit.
5. **SQL:** write the FROM/JOIN first, then WHERE, GROUP BY, HAVING, SELECT, ORDER BY. Check that every non-aggregated column is in GROUP BY.
6. **Theory answers:** definition → short example → one advantage/disadvantage. Short and structured beats long and vague.

## Example of a strong handwritten answer
```java
// Returns the first non-repeating character, or '_' if none. O(n) time, O(1) space (26 letters).
char firstUnique(String s) {
    if (s == null || s.isEmpty()) return '_';
    int[] count = new int[26];
    for (char c : s.toCharArray()) count[c - 'a']++;     // pass 1: count
    for (char c : s.toCharArray())
        if (count[c - 'a'] == 1) return c;               // pass 2: first with count 1
    return '_';
}
```
**Why it scores well**
- An edge-case check first (null/empty).
- A comment states the purpose and the complexity.
- Two simple passes, easy for the examiner to follow.
- `c - 'a'` maps a letter to an index 0–25 (assuming lowercase a–z, which you should say explicitly).

@@ 2 | TOPIC | Java Output Prediction Practice | Tricky snippets with explained answers | MUST
# Java output prediction practice

Cover the answers, predict the output, then read the explanation.

**1. String comparison**
```java
String a = "java";
String b = "ja" + "va";
String c = new String("java");
System.out.println((a == b) + " " + (a == c) + " " + a.equals(c));
```
**Answer:** `true false true`
- `"ja" + "va"` is concatenated **at compile time** into the literal "java", so it comes from the pool and `a == b`.
- `new String` creates a new object, so `==` is false while `equals` is true.

**2. Integer caching**
```java
Integer x = 127, y = 127, p = 128, q = 128;
System.out.println((x == y) + " " + (p == q));
```
**Answer:** `true false`. Integers from -128 to 127 are cached and 128 is not. Use `equals`.

**3. Pre/post increment**
```java
int i = 5;
int j = i++ + ++i;
System.out.println(i + " " + j);
```
**Answer:** `7 12`
- `i++` uses 5, then i becomes 6. `++i` makes i 7 and uses 7. j = 5 + 7 = 12.

**4. finally with return**
```java
static int test() {
    try { return 1; }
    finally { System.out.print("F "); }
}
System.out.println(test());
```
**Answer:** `F 1`. `finally` runs **before** the method actually returns, but the return value 1 was already decided.

**5. Static vs instance (method hiding)**
```java
class A { static String who() { return "A"; } String me() { return "A"; } }
class B extends A { static String who() { return "B"; } String me() { return "B"; } }
A obj = new B();
System.out.println(obj.who() + obj.me());
```
**Answer:** `AB`
- Static methods are **not overridden**; they are chosen by the **reference type** (A).
- Instance methods are overridden; they are chosen by the **actual object** (B).

**6. Char arithmetic**
```java
char ch = 'A';
System.out.println(ch + 1);
System.out.println((char) (ch + 1));
System.out.println("" + ch + 1);
```
**Answer:** `66`, `B`, `A1`
- `ch + 1` promotes the char to an int (65 + 1).
- The cast turns 66 back into the char 'B'.
- Starting with `""` makes it string concatenation, left to right.

**7. String concatenation order**
```java
System.out.println(1 + 2 + "3" + 4 + 5);
```
**Answer:** `3345`. Left to right: 1 + 2 = 3 (numbers), then "3" + "3" = "33", then "334", then "3345".

**8. Integer division and modulo**
```java
System.out.println(7 / 2 + " " + 7 % 3 + " " + -7 / 2 + " " + -7 % 3 + " " + 7.0 / 2);
```
**Answer:** `3 1 -3 -1 3.5`. Integer division truncates toward zero, and the remainder takes the sign of the dividend.

**9. Overloading resolution**
```java
static void f(Object o) { System.out.print("Object "); }
static void f(String s) { System.out.print("String "); }
f(null);
```
**Answer:** `String`. Java picks the **most specific** applicable overload, and String is more specific than Object.

**10. Exceptions in a loop**
```java
List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
for (Integer n : list) if (n == 2) list.remove(n);
System.out.println(list);
```
**Answer:** in this exact case it prints `[1, 3]` without an exception, because removing the second-to-last element makes `hasNext()` return false before the check runs. In general, modifying a list during for-each throws `ConcurrentModificationException`. Use `removeIf`.

**11. Short-circuit**
```java
int k = 0;
if (k != 0 && 10 / k > 1) System.out.println("big");
System.out.println("safe");
```
**Answer:** `safe`. `&&` stops at the first false, so `10 / k` never runs (no ArithmeticException). With `&` it would throw.

**12. Ternary type promotion**
```java
Object o = true ? 1 : "one";
System.out.println(o.getClass().getSimpleName());
Object n = true ? 1 : 2.0;
System.out.println(n);
```
**Answer:** `Integer`, then `1.0`. In the second, both branches are numeric, so the int is promoted to double.

@@ 2 | TOPIC | Viva Preparation | Project stories, common HR and technical questions, STAR answers | MUST
# Viva preparation

## 1. Your project walkthrough (practise until smooth)
A 2-minute script:
1. **Problem:** "POs need to track loans and repayments accurately…"
2. **Architecture:** draw the boxes: React → Spring Boot → PostgreSQL/Redis, Keycloak.
3. **One hard problem you solved:** "Preventing double repayments: I used an idempotency key with a unique constraint plus row locking…"
4. **Testing and deployment:** "Unit tests with Mockito, integration tests with Testcontainers, CI on GitHub Actions, docker-compose."
5. **What you'd improve:** shows maturity ("I'd add a read replica for reports").

## 2. STAR format for experience questions
**S**ituation → **T**ask → **A**ction → **R**esult (with numbers if possible).

> "Tell me about a performance problem you fixed."
> **S:** The loan list page took 8 seconds. **T:** I had to make it fast without changing the UI. **A:** SQL logs showed an N+1 problem: 1 + 200 queries. I replaced it with a DTO projection using a join, and added an index on `loan(member_id, status)`. **R:** The page loaded in 300 ms and DB CPU dropped by 40%.

## 3. Likely questions and how to answer

**"Why PKSF?"**
Link your skills to their mission: "PKSF's systems support financial inclusion for millions through 200+ POs. I want to build secure, reliable systems where correctness really matters, and my Spring Boot/React/PostgreSQL experience fits directly."

**"Tell me about yourself."** (60–90 seconds)
Present (current role and stack) → past (key projects/achievements) → future (why this job).

**"What is your biggest weakness?"**
A real but non-critical weakness plus what you're doing about it: "I used to over-engineer early; now I start simple and refactor when the need is proven."

**"How do you keep learning?"** Official docs, building side projects (like your portfolio), reading engineering blogs.

**"A loan balance is wrong in production. What do you do?"**
1. Stop further damage (if needed, block that operation).
2. Check the **audit logs and ledger** to see exactly what happened.
3. Reproduce it in a test environment and write a failing test.
4. Fix the bug, and correct the data with **reversal/adjustment entries** (never silent edits), with approval.
5. Root-cause analysis plus prevention (a constraint, a test, monitoring).

**"How do you handle disagreement with a colleague?"**
Listen, discuss with data/prototypes, agree on criteria, escalate respectfully if needed, and commit to the team decision.

## 4. Technical "explain this" checklist (practise out loud, 1 minute each)
- How a request flows: browser → Nginx → Spring Security filter → controller → service → repository → DB.
- How JWT/Keycloak login works.
- @Transactional and why it sometimes fails.
- The N+1 problem and its fixes.
- Optimistic vs pessimistic locking.
- How an index works and when it isn't used.
- Docker vs VM; Kubernetes liveness vs readiness.
- useEffect and its dependency array.

**Tip:** if you don't know something, say "I haven't used that directly, but here's how I'd approach it…" Honesty plus reasoning beats bluffing.

@@ 2 | TOPIC | Rapid-Fire Revision List | One-line answers to revise the night before | MUST
# Rapid-fire revision list

Cover the right side and test yourself.

## CS and Java
| Question | Answer |
|---|---|
| Binary search complexity | O(log n), sorted input |
| Stable O(n log n) sort | Merge sort / TimSort |
| HashMap default capacity / load factor | 16 / 0.75 |
| Bucket becomes a tree at | > 8 entries (capacity ≥ 64) |
| String immutable why | Security, thread safety, hash caching, pool |
| `==` vs `equals` | Reference vs content |
| equals/hashCode contract | Equal objects → equal hash codes |
| Checked exception example | IOException |
| `volatile` gives | Visibility, not atomicity |
| `start()` vs `run()` | New thread vs same thread |
| Heap vs stack | Objects (shared) vs frames (per thread) |
| Default GC (Java 9+) | G1 |
| Money type | BigDecimal + NUMERIC |
| SOLID | SRP, OCP, LSP, ISP, DIP |
| Deadlock conditions | Mutual exclusion, hold and wait, no preemption, circular wait |
| TCP handshake | SYN, SYN-ACK, ACK |
| 401 vs 403 | Not authenticated vs not authorized |
| Idempotent methods | GET, PUT, DELETE, HEAD, OPTIONS |

## Spring
| Question | Answer |
|---|---|
| @SpringBootApplication = | @SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan |
| Default bean scope | Singleton |
| Preferred injection | Constructor |
| Default propagation | REQUIRED |
| Rollback by default on | Unchecked exceptions |
| @Transactional fails when | Self-invocation, private method, checked/swallowed exception |
| N+1 fix | JOIN FETCH, @EntityGraph, DTO projection, batch size |
| Optimistic lock | @Version → 409 on conflict |
| Pessimistic lock | SELECT ... FOR UPDATE |
| @ManyToOne default fetch | EAGER (set LAZY) |
| ddl-auto in prod | validate / none + Flyway |
| Validation trigger | @Valid on @RequestBody |
| Global errors | @RestControllerAdvice + ProblemDetail |

## Database
| Question | Answer |
|---|---|
| WHERE vs HAVING | Rows before grouping vs groups after |
| SQL logical order | FROM, WHERE, GROUP BY, HAVING, SELECT, ORDER BY, LIMIT |
| RANK vs DENSE_RANK (100,100,90) | 1,1,3 vs 1,1,2 |
| Postgres default isolation | Read Committed |
| 3NF | Non-key columns depend only on the key |
| Composite index rule | Leftmost prefix |
| Index ignored when | Function on column, leading %, type cast, low selectivity |
| Read a plan | EXPLAIN (ANALYZE, BUFFERS) |
| MVCC cleanup | VACUUM (autovacuum) |
| Deep pagination fix | Keyset (WHERE id > last) |

## Security, Redis, microservices
| Question | Answer |
|---|---|
| Password storage | BCrypt / Argon2 (salted, slow hash) |
| JWT parts | Header . Payload . Signature (payload not encrypted) |
| SPA login flow | Authorization Code + PKCE |
| Service-to-service flow | Client Credentials |
| OIDC adds | ID token (authentication) |
| Keycloak roles claim | realm_access.roles → map to ROLE_ |
| CSRF can be disabled for | Stateless APIs that use Authorization headers |
| Most common cache pattern | Cache-aside |
| Cache invalidation on write | Evict (delete), plus a TTL |
| Distributed lock | SET key val NX PX ttl / Redisson / ShedLock |
| Circuit breaker states | Closed → Open → Half-open |
| Cross-service transaction | Saga + outbox |
| Kafka ordering | Within a partition (same key) |

## Frontend and DevOps
| Question | Answer |
|---|---|
| Event loop order | Sync → microtasks (promises) → macrotasks (timers) |
| `[]` deps in useEffect | Run once after mount |
| useMemo vs useCallback | Value vs function |
| List keys | Stable unique IDs, not indexes |
| Server state lib | TanStack Query |
| Image vs container | Template vs running instance |
| Multi-stage build | Small, secure runtime image |
| Readiness vs liveness | Receive traffic? vs restart? |
| Zero-downtime deploy | Rolling update + readiness probe |
| Safe undo on a shared branch | git revert |

**Last night before the exam:** revise this list once, sleep well, and arrive early. Good luck!
