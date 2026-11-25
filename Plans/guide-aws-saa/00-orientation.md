@@ 1 | MODULE | Phase 0 — Exam Orientation & Lab Project Setup | Understand the exam, secure your AWS account, build the CloudMart lab app | MUST
# Phase 0: Orientation and lab project

This guide prepares you for **AWS Certified Solutions Architect – Associate (SAA-C03)**. Every topic has three parts:

1. **Study notes:** what the service is, an analogy, the key facts, and the exam traps.
2. **CloudMart lab task:** one small step on the *same* project. There is no new project per service.
3. **Flashcards:** `Q:`/`A:` lines for spaced repetition.

**The single project: CloudMart.** CloudMart is a small online shop: a product catalog, image upload, a cart, orders and email notifications. You build it **once**, in Phase 0, with Spring Boot + React, and it runs fully on your laptop. In each later phase you swap one local piece for an AWS service. Local disk becomes S3, H2 becomes RDS, in-memory events become SQS, and so on. At the end, the whole shop runs on a production-style AWS architecture that you built yourself.

@@ 2 | MODULE | The Exam | Format, domains and how to study | MUST
What the exam looks like and how to use this guide.

@@ 3 | TOPIC | SAA-C03 exam format and domains | 65 questions, 130 min, pass 720/1000, four domains | MUST
# SAA-C03 exam format

| Item | Value |
|---|---|
| Questions | 65 (50 scored + 15 unscored, and you can't tell which are which) |
| Time | 130 minutes (about 2 minutes per question) |
| Types | Multiple choice (1 correct of 4) and multiple response (2+ correct of 5+) |
| Score | 100–1000, **pass = 720**. Wrong answers carry no penalty, so never leave a question blank |
| Cost | 150 USD, valid for 3 years |

## The four domains

| Domain | Weight | What it really tests |
|---|---|---|
| 1. Design Secure Architectures | 30% | IAM, encryption (KMS), VPC security, S3 policies, Cognito, WAF |
| 2. Design Resilient Architectures | 26% | Multi-AZ, Auto Scaling, decoupling (SQS/SNS), DR strategies |
| 3. Design High-Performing Architectures | 24% | Choosing the right storage, DB, caching, network and compute |
| 4. Design Cost-Optimized Architectures | 20% | Purchase options, S3 storage classes, right-sizing, serverless |

## How the questions look
Almost every question is a **scenario**: "A company has X. They need Y with *the least operational overhead* / *most cost-effective* / *highest availability*. What should a solutions architect do?"

The **qualifier at the end decides the answer**. Two options are often both technically possible:
- "Least operational overhead" → managed/serverless (Lambda, Fargate, Aurora Serverless, DynamoDB).
- "Most cost-effective" → the cheapest option that still meets the requirement (Spot, S3 IA/Glacier, Savings Plans).
- "Highest availability" → Multi-AZ, multi-Region, health checks, Auto Scaling.

## Note
AWS updates exam versions from time to time. Before booking, check that the exam code is still SAA-C03. The services and patterns in this guide stay valid either way.

## Lab task (CloudMart)
None yet. Read the next topic.

Q: What is the passing score for SAA-C03?
A: 720 out of 1000.

Q: Which domain has the highest weight?
A: Design Secure Architectures (30%).

Q: What does "least operational overhead" usually point to?
A: Fully managed or serverless services (Lambda, Fargate, DynamoDB, Aurora Serverless, S3).

@@ 3 | TOPIC | How to use this guide and study schedule | Read, lab, flashcard, review: a 10–12 week plan | MUST
# How to use this guide

## Daily loop (60–90 minutes)
1. **Read** one or two topics. Each one is written so that you don't need any other website.
2. **Do the CloudMart lab task** for those topics. Seeing the console and the CLI fixes the concepts in memory.
3. **Mark complete.** Then review the flashcards the next day, after 7 days and after 30 days.
4. **Tear down** expensive resources (NAT Gateway, ALB, RDS) when you stop for the day. Each phase has a teardown note.

## Suggested schedule (about 10–12 weeks)

| Week | Phase |
|---|---|
| 1 | Phase 0 (setup + build CloudMart locally) and Phase 1 (IAM) |
| 2 | Phase 2 (EC2, EBS, EFS) |
| 3 | Phase 3 (ELB + Auto Scaling) |
| 4 | Phase 4 (S3 and storage services) |
| 5 | Phase 5 (databases and caching) |
| 6 | Phase 6 (VPC, Route 53, CloudFront) |
| 7 | Phase 7 (decoupling) and Phase 8 (serverless + containers) |
| 8 | Phase 9 (security) and Phase 10 (monitoring + IaC) |
| 9 | Phase 11 (analytics + ML) and Phase 12 (architecture, DR, migration, cost) |
| 10–12 | Phase 13: exam strategy, scenario drills, full review of flashcards |

## Priorities
- **MUST:** shows up often on the exam. Know it deeply.
- **SHOULD:** shows up regularly. Know what it is and when to pick it.
- **NICE:** shows up rarely. Recognize the name and its one-line use case.

## Lab task (CloudMart)
The CloudMart project is already created for you at **`D:\mylearn\project_aws`** (`backend/` + `frontend/`, see its README). Run `git init` there. Commit after every lab step with a message like `phase4: product images on S3`. Your Git history becomes your study log.

Q: What should you do with NAT Gateways, ALBs and RDS instances at the end of a lab session?
A: Delete or stop them. They are billed per hour even when idle.

@@ 2 | MODULE | AWS Account Setup & Cost Safety | Create the account safely before anything else | MUST
Secure the account and protect your wallet first.

@@ 3 | TOPIC | Create and secure your AWS account | Root user MFA, admin identity, region choice | MUST
# Create and secure your AWS account

## Root user vs everyday identity
When you sign up, you get the **root user** (your email address plus a password). Root can do *everything*, including closing the account and changing billing. **Analogy:** the root user is the master key to the building. You lock it in a safe and carry a normal office key (an IAM identity) every day.

## Steps
1. Sign up at the AWS console with an email address you control. Pick the **Free plan** if offered. New accounts get free-tier credits, and the console shows the current offer.
2. **Enable MFA on root** (authenticator app or passkey/security key): Account menu → Security credentials → Assign MFA.
3. **Never create access keys for root.**
4. Create your daily admin identity. Either:
   - **IAM Identity Center** (recommended, it's what AWS suggests): enable it, create a user, and give it the `AdministratorAccess` permission set. You sign in through the access portal URL. Or:
   - **an IAM user** `admin` with the `AdministratorAccess` policy, console access and MFA. This is simpler for a single personal account.
5. Sign out of root and sign in with the admin identity from now on.

## Choose one Region for the lab
Pick one Region close to you and use it for everything. Examples: `ap-south-1` (Mumbai), `ap-southeast-1` (Singapore), `us-east-1` (N. Virginia, where every service exists first). Resources are Region-scoped, so if you "lose" something, check the Region selector first.

## Exam traps
- Some tasks *require* root: changing the account's root email or support plan, closing the account, restoring IAM permissions when the only admin is locked out, and some billing settings.
- "Secure the root account" → MFA + no access keys + don't use it daily.

## Lab task (CloudMart)
Do steps 1–5. Write your admin sign-in URL and your Region in `cloudmart/README.md`.

Q: Which tasks require the root user?
A: Changing the account email/root password, changing the support plan, closing the account, and recovering when all IAM admins are locked out (plus a few billing settings).

Q: Should the root user have access keys?
A: No. Delete them if they exist, and enable MFA on root.

@@ 3 | TOPIC | Billing alarms, AWS Budgets and Free Tier | Never get a surprise bill | MUST
# Cost safety: Budgets and alerts

**AWS Budgets** lets you set a cost or usage limit and get emailed when actual or *forecasted* spend crosses it. **Analogy:** a fuel gauge with a warning light. It doesn't stop the car, but it warns you before you run dry.

## Set up (5 minutes)
1. Billing and Cost Management → **Budgets** → Create budget → "Zero spend budget" (alerts on the first cent) **and** a "Monthly cost budget" of e.g. 10 USD.
2. Alert at 50%, 80% and 100% of actual spend, and at 100% of forecasted spend.
3. Turn on **Free Tier usage alerts** in Billing preferences.
4. Enable **Cost Explorer**. It shows spend per service per day, so you find the culprit fast.

## The usual bill surprises in labs

| Resource | Why it costs | Tip |
|---|---|---|
| NAT Gateway | Hourly charge + per-GB charge | Create it only for the lab, then delete it |
| Public IPv4 addresses | Charged per hour for every public IPv4, attached or not | Release unused Elastic IPs |
| ALB / NLB | Hourly charge + capacity units | Delete after the lab |
| RDS (Multi-AZ, large class) | Hourly charge, doubled for Multi-AZ | Use `db.t4g.micro`, stop or delete after the lab |
| EBS volumes and snapshots | Billed per GB-month even when the instance is stopped | Delete orphan volumes |
| Interface VPC endpoints | Hourly charge per AZ | Delete after the lab |

## Exam angle
- **AWS Budgets** = alerts (and optional *budget actions*, e.g. apply an IAM policy that blocks launching instances).
- **Cost Explorer** = visualize and forecast spend, and get Savings Plan / RI recommendations.
- **Cost and Usage Report (CUR / Data Exports)** = the most detailed line-item billing data, sent to S3 and queried with Athena.

## Lab task (CloudMart)
Create the zero-spend budget and a 10 USD monthly budget. Add **tags** to every lab resource from now on: `Project=cloudmart`, `Phase=<n>`. Activate `Project` as a **cost allocation tag** in Billing so Cost Explorer can group by it.

Q: Which service sends an alert when forecasted spend will exceed a threshold?
A: AWS Budgets.

Q: Which service gives the most granular, line-by-line billing data for analysis in Athena?
A: The AWS Cost and Usage Report (Data Exports), delivered to S3.

@@ 3 | TOPIC | Install AWS CLI and configure credentials | CLI v2, SSO/profile login, credential chain | MUST
# AWS CLI setup

The **AWS CLI** controls AWS from your terminal. The **SDKs** (Java, JavaScript, Python and others) do the same from code. Both find credentials through the same **default credential provider chain**.

## Install and log in
- Windows: install the AWS CLI v2 MSI, then check it with `aws --version`.
- If you use IAM Identity Center: run `aws configure sso`, follow the browser login, and name the profile `cloudmart`.
- If you use an IAM user: create an access key for the `admin` user (only for your laptop), then run `aws configure --profile cloudmart`.

```bash
aws configure sso --profile cloudmart      # Identity Center login (short-lived credentials)
aws sts get-caller-identity --profile cloudmart   # "who am I?" - prints account + ARN
export AWS_PROFILE=cloudmart               # PowerShell: $env:AWS_PROFILE="cloudmart"
aws s3 ls                                  # lists buckets: proves the credentials work
```
- `sts get-caller-identity` is the first command to run whenever a permission problem appears. It tells you *which* identity you really are.
- The `AWS_PROFILE` variable makes every later command, and your Spring Boot app, use this profile.

## The credential chain (the order the SDK looks in)
1. Environment variables (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`)
2. Java system properties
3. Web identity token (EKS, IRSA)
4. The shared `~/.aws/credentials` and `~/.aws/config` files (profiles, SSO)
5. Container credentials (ECS task role)
6. The **EC2 instance profile** (instance metadata)

**Why this matters:** the same CloudMart code works on your laptop (profile) and on EC2/ECS/Lambda (role) **with no keys in code**. The exam loves this: "never store access keys on EC2, use an IAM role".

## Lab task (CloudMart)
Install the CLI, create the `cloudmart` profile and run `aws sts get-caller-identity`. Paste the output ARN into your README.

Q: How should an application on EC2 get AWS credentials?
A: Through an IAM role attached as an instance profile. The SDK reads temporary credentials from instance metadata. Never store access keys on the instance.

Q: Which CLI command shows which identity you are using?
A: aws sts get-caller-identity

@@ 2 | MODULE | CloudMart Lab Project | Build the one app you will move to AWS piece by piece | MUST
Build the app before learning AWS, so that every later lab is only "swap one adapter".

@@ 3 | TOPIC | CloudMart overview and target AWS architecture | Features, local design, and which AWS service each phase adds | MUST
# CloudMart: the one project for the whole course

## Features (business view)
- **Catalog:** list and view products (name, price, image).
- **Admin:** create products and upload product images.
- **Cart:** add items. The cart is temporary data.
- **Orders:** checkout creates an order, a background worker "processes" it, and the user gets a notification.
- **Lab dashboard:** a special page that shows *which server, AZ and database* served your request, with buttons to stress the CPU and to break the health check. This makes load balancing, Auto Scaling and failover *visible*.

## The key design idea: ports and adapters
Each infrastructure need is a Java **interface** with a **local** implementation and an **AWS** implementation. A Spring property chooses which one runs. This is why one project can practice every service.

| Port (interface) | Local adapter (Phase 0) | AWS adapter (later phase) |
|---|---|---|
| `StorageService` (images) | Local folder | **S3** + presigned URLs (Phase 4) |
| Database (JPA) | H2 file DB | **RDS PostgreSQL / Aurora** (Phase 5) |
| `CacheService` | In-memory map | **ElastiCache (Redis/Valkey)** (Phase 5) |
| `CartRepository` | In-memory map | **DynamoDB** with TTL (Phase 5) |
| `OrderEventPublisher` | Spring in-process event | **SQS** → Lambda worker (Phase 7) |
| `Notifier` | Log line | **SNS** email (Phase 7) |
| Auth | Simple dev login | **Cognito** user pool (Phase 8) |
| Config/secrets | `application.yml` | **Secrets Manager / Parameter Store** (Phase 9) |

## Target architecture by the end

```text
Users -> Route 53 -> CloudFront --+--> S3 (React build, product images)
                                  +--> ALB (public subnets, 2 AZ) + WAF
                                         -> ECS Fargate / EC2 ASG (private subnets): cloudmart-api
                                              -> RDS/Aurora (Multi-AZ)   -> ElastiCache
                                              -> DynamoDB (carts)        -> SQS -> Lambda worker -> SNS
           Cognito (login)    CloudWatch + X-Ray (observability)    KMS + Secrets Manager (security)
           Kinesis Firehose -> S3 data lake -> Glue -> Athena -> QuickSight (analytics)
           Everything defined in CloudFormation/CDK, backed up by AWS Backup, DR in a 2nd Region
```
- Read this diagram again at the end of each phase and tick off what you have built.

## Repository layout

```text
D:\mylearn\project_aws\        (already created; see README.md there)
  backend/    Spring Boot 3.5 (Java 21), Maven  -> target/api.jar
  frontend/   React (Vite)                      -> dist/
  lambda/     order-worker and thumbnail functions (added in Phases 7-8)
  infra/      CloudFormation / CDK (added in Phase 10)
  docker-compose.yml
```

Q: Why does CloudMart use an interface with local and AWS implementations for each infrastructure need?
A: So the same app runs locally and on AWS, and each phase only swaps one adapter by changing a property. You practice one service at a time without rewriting the app.

@@ 3 | TOPIC | Build the Spring Boot API skeleton | Entities, ports/adapters, profiles, lab endpoints | MUST
# Build the CloudMart API (Spring Boot)

Create the project at start.spring.io: **Java 21, Spring Boot 3.5.x, Maven**, group `com.cloudmart`, artifact `api`. Dependencies: Spring Web, Spring Data JPA, H2 Database, PostgreSQL Driver, Validation, Actuator, Lombok (optional).

Add the AWS SDK v2 BOM now. You will use its modules later (s3, dynamodb, sqs, sns, secretsmanager).

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>software.amazon.awssdk</groupId>
      <artifactId>bom</artifactId>
      <version>2.31.0</version>  <!-- any recent 2.x -->
      <type>pom</type><scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```
- A BOM ("bill of materials") pins all AWS SDK module versions together, so you can add `<artifactId>s3</artifactId>` later without a version number.

## Domain: Product and Order

```java
@Entity
public class Product {
    @Id @GeneratedValue Long id;
    @NotBlank String name;
    @Positive BigDecimal price;
    String imageKey;          // the file name/key in storage (local folder now, S3 later)
    // getters/setters
}

@Entity @Table(name = "orders")
public class Order {
    @Id @GeneratedValue Long id;
    String customerEmail;
    BigDecimal total;
    String status;            // NEW -> PROCESSED (set by the worker)
    Instant createdAt = Instant.now();
}
```
- `imageKey` stores only a *key*, not a full URL. Later, the S3 adapter turns the key into a presigned or CloudFront URL. This is a common real-world pattern.

## A port with two adapters

```java
public interface StorageService {
    String save(String key, InputStream data, String contentType);
    String publicUrl(String key);          // URL the browser can load
}

@Service
@ConditionalOnProperty(name = "cloudmart.storage", havingValue = "local", matchIfMissing = true)
class LocalStorageService implements StorageService {
    private final Path root = Path.of("data/images");
    public String save(String key, InputStream in, String type) {
        try { Files.createDirectories(root); Files.copy(in, root.resolve(key), REPLACE_EXISTING); }
        catch (IOException e) { throw new UncheckedIOException(e); }
        return key;
    }
    public String publicUrl(String key) { return "/api/images/" + key; }
}
```
- `@ConditionalOnProperty` creates the bean only when `cloudmart.storage=local` (or when the property is missing). In Phase 4 you add `S3StorageService` with `havingValue = "s3"` and switch by changing one line of config.
- Create the same pattern now for `OrderEventPublisher` (local: `ApplicationEventPublisher` plus an `@EventListener` that marks the order PROCESSED after 2 seconds), `Notifier` (local: log line), `CartRepository` (local: `ConcurrentHashMap`) and `CacheService` (local: `ConcurrentHashMap`).

## application.yml with profiles

```yaml
cloudmart:
  storage: local        # local | s3
  events: local         # local | sqs
  cart: local           # local | dynamodb
  cache: local          # local | redis
spring:
  datasource:
    url: jdbc:h2:file:./data/cloudmart
  jpa.hibernate.ddl-auto: update
management.endpoints.web.exposure.include: health,info
---
spring.config.activate.on-profile: aws
spring.datasource.url: ${DB_URL}          # filled in Phase 5 (RDS)
```
- The `aws` profile is turned on with `SPRING_PROFILES_ACTIVE=aws` on servers. Values like `${DB_URL}` come from environment variables, and later from Secrets Manager.

## REST endpoints to implement
- `GET /api/products`, `GET /api/products/{id}`, `POST /api/products` (multipart: JSON + image)
- `GET/POST/DELETE /api/cart/{sessionId}`
- `POST /api/orders` (publishes an event), `GET /api/orders/{id}` (shows the status)
- `GET /actuator/health` (used by load balancer health checks in Phase 3)

## Lab task (CloudMart)
The API is already built in `D:\mylearn\project_aws\backend`, so read the code instead of typing it: start with `storage/StorageService.java` + `LocalStorageService.java`, then `order/OrderService.java` + `LocalOrderWorker.java`. Run `mvn spring-boot:run` and test with curl: create a product with an image, add it to the cart, place an order, and watch the status change to PROCESSED.

Q: Which Spring annotation lets CloudMart pick the local or the AWS implementation by config?
A: @ConditionalOnProperty (or @Profile). The bean is created only when the property has the given value.

@@ 3 | TOPIC | Add the Lab Dashboard endpoints | whoami (instance, AZ), CPU stress, health toggle, slow endpoint | MUST
# Lab endpoints: make AWS behaviour visible

These endpoints exist only to *see* AWS concepts working: load balancing, Auto Scaling, health checks and timeouts.

```java
@RestController
@RequestMapping("/api/lab")
class LabController {
    private final AtomicBoolean healthy = new AtomicBoolean(true);
    private final DataSource dataSource;
    LabController(DataSource ds) { this.dataSource = ds; }

    @GetMapping("/whoami")
    Map<String, Object> whoami() throws Exception {
        String dbUrl;
        try (var c = dataSource.getConnection()) { dbUrl = c.getMetaData().getURL(); }
        return Map.of(
            "host", InetAddress.getLocalHost().getHostName(),
            "instanceId", Metadata.get("instance-id"),        // EC2 only; "local" elsewhere
            "az", Metadata.get("placement/availability-zone"),
            "db", dbUrl,
            "time", Instant.now().toString());
    }

    @PostMapping("/stress")      // burn CPU for N seconds -> triggers Auto Scaling (Phase 3)
    String stress(@RequestParam(defaultValue = "120") int seconds) {
        int cores = Runtime.getRuntime().availableProcessors();
        long end = System.currentTimeMillis() + seconds * 1000L;
        for (int i = 0; i < cores; i++)
            new Thread(() -> { while (System.currentTimeMillis() < end) Math.sqrt(Math.random()); }).start();
        return "stressing " + cores + " cores for " + seconds + "s";
    }

    @PostMapping("/health/{state}")   // "down" makes the ALB health check fail
    String setHealth(@PathVariable String state) { healthy.set(!"down".equals(state)); return state; }

    @GetMapping("/health")
    ResponseEntity<String> health() {
        return healthy.get() ? ResponseEntity.ok("UP") : ResponseEntity.status(503).body("DOWN");
    }

    @GetMapping("/slow")              // test ALB/API Gateway timeouts
    String slow(@RequestParam(defaultValue = "5") int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000L); return "slept " + seconds;
    }
}
```
- **whoami:** when you refresh it behind a load balancer, `instanceId` and `az` change. That's load balancing you can see. The `db` field shows whether you hit the primary or a read replica.
- **stress:** pushes CPU to 100%, so a target-tracking Auto Scaling policy launches new instances.
- **health down:** the ALB marks the target unhealthy, and the Auto Scaling Group replaces it.

## Reading EC2 instance metadata (IMDSv2)

```java
final class Metadata {
    private static final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(300)).build();
    static String get(String path) {
        try {
            var tokenReq = HttpRequest.newBuilder(URI.create("http://169.254.169.254/latest/api/token"))
                    .header("X-aws-ec2-metadata-token-ttl-seconds", "60")
                    .PUT(HttpRequest.BodyPublishers.noBody()).build();
            String token = http.send(tokenReq, HttpResponse.BodyHandlers.ofString()).body();
            var req = HttpRequest.newBuilder(URI.create("http://169.254.169.254/latest/meta-data/" + path))
                    .header("X-aws-ec2-metadata-token", token).build();
            return http.send(req, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) { return "local"; }   // not on EC2
    }
}
```
- **IMDSv2** first gets a session token with a PUT, then sends that token on each read. This blocks SSRF attacks that could steal role credentials. AWS recommends requiring IMDSv2 (you will see this on the exam).
- Use a short timeout so that this code returns `"local"` quickly on your laptop.

## Lab task (CloudMart)
`lab/LabController.java` and `lab/Metadata.java` already exist in the backend. Read them, then open `http://localhost:8080/api/lab/whoami`: locally it must return `instanceId: local`.

Q: Why does IMDSv2 require a PUT for a token first?
A: The session token defends against SSRF and open-proxy attacks that could read instance role credentials through simple GET requests.

@@ 3 | TOPIC | Build the React frontend | Vite app: catalog, admin upload, cart, orders, lab dashboard | MUST
# Build the CloudMart web app (React)

```bash
npm create vite@latest web -- --template react
cd web && npm install && npm install react-router-dom
```

## Pages
| Route | What it does | AWS concept it will show later |
|---|---|---|
| `/` | Product grid (image, name, price) | S3 + CloudFront image delivery |
| `/admin` | Form to create a product with an image | S3 presigned upload, Cognito admin group |
| `/cart` | Cart with checkout | DynamoDB cart, SQS order flow |
| `/orders/:id` | Order status (polls every 2 s) | Async processing with SQS + Lambda |
| `/lab` | Lab dashboard | ALB, ASG, Multi-AZ, failover |

## API base URL from an environment variable

```js
// src/api.js
const BASE = import.meta.env.VITE_API_BASE ?? "";   // "" = same origin (dev proxy)
export async function api(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    ...options,
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
  });
  if (!res.ok) throw new Error(`${res.status} ${await res.text()}`);
  return res.status === 204 ? null : res.json();
}
```
- `VITE_API_BASE` lets the same build talk to `localhost:8080` in dev and, later, to the ALB or CloudFront domain. In Phase 6 CloudFront serves both the site and `/api/*`, so it stays `""`.

## The Lab dashboard page

```jsx
export default function Lab() {
  const [rows, setRows] = useState([]);
  useEffect(() => {
    const t = setInterval(async () => {
      const r = await api("/api/lab/whoami");
      setRows(prev => [r, ...prev].slice(0, 20));     // keep the last 20 responses
    }, 1000);
    return () => clearInterval(t);
  }, []);
  return (
    <div>
      <button onClick={() => api("/api/lab/stress?seconds=180", { method: "POST" })}>Stress CPU</button>
      <button onClick={() => api("/api/lab/health/down", { method: "POST" })}>Break health</button>
      <table>{rows.map((r, i) =>
        <tr key={i}><td>{r.time}</td><td>{r.instanceId}</td><td>{r.az}</td><td>{r.db}</td></tr>)}
      </table>
    </div>
  );
}
```
- It calls `whoami` once per second and shows the last 20 answers. Behind a load balancer you see the instance and AZ columns alternate. During an RDS failover you see the requests fail for a moment and then continue.

## Dev proxy
In `vite.config.js` add `server: { proxy: { "/api": "http://localhost:8080" } }`, so the browser calls the same origin and you need no CORS setup locally.

## Lab task (CloudMart)
The pages already exist in `D:\mylearn\project_aws\frontend\src\pages`. Run `npm install` and `npm run dev`, then check the full flow in the browser (catalog → cart → order → status, Admin upload, Lab page). Then run `npm run build`: the `dist/` folder is what you host on S3 in Phase 4.

Q: Which folder of a Vite React build do you upload to S3 for static hosting?
A: The dist/ folder produced by npm run build. It contains only static HTML, JS and CSS.

@@ 3 | TOPIC | Run everything locally with Docker | Dockerfile for the API, docker-compose with Postgres | SHOULD
# Containerize CloudMart

A container image is needed later for **ECR + ECS Fargate** (Phase 8), and a Postgres container lets you test the "real database" profile before using RDS.

```dockerfile
# api/Dockerfile  (multi-stage: build with Maven, run with a small JRE)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
```
- Multi-stage builds keep Maven out of the final image, which makes it smaller and safer. `MaxRAMPercentage` makes the JVM respect the container memory limit, which matters for Fargate task sizes.

```yaml
# docker-compose.yml
services:
  db:
    image: postgres:16
    environment: { POSTGRES_DB: cloudmart, POSTGRES_USER: cm, POSTGRES_PASSWORD: cm }
    ports: ["5432:5432"]
  api:
    build: ./backend
    environment:
      SPRING_PROFILES_ACTIVE: aws
      DB_URL: jdbc:postgresql://db:5432/cloudmart
      SPRING_DATASOURCE_USERNAME: cm
      SPRING_DATASOURCE_PASSWORD: cm
    ports: ["8080:8080"]
    depends_on: [db]
```
- This proves that the `aws` profile works with PostgreSQL. In Phase 5 only `DB_URL` and the credentials change, to point at RDS.

## Lab task (CloudMart)
Run `docker compose up --build`, then repeat the curl flow. If Docker isn't available, skip this for now. It is needed again in Phase 8.

Q: Why use a multi-stage Dockerfile for a Spring Boot app?
A: The build tools (Maven, JDK) stay in the build stage. The final image has only the JRE and the jar, so it is smaller and has less attack surface.
