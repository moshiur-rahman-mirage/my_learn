# CloudMart: the AWS SAA lab project

One small shop app (Spring Boot + React) used in **every phase** of the AWS Solutions Architect study plan
(`D:\mylearn\Plans\aws-saa-plan.csv`). It runs fully on your laptop now. Each phase swaps one local
piece for an AWS service.

```
project_aws/
  backend/     Spring Boot 3.5, Java 21, Maven   -> target/api.jar
  frontend/    React 18 + Vite                   -> dist/ (static files for S3)
  docker-compose.yml   API + PostgreSQL (rehearsal for RDS)
```

## Run locally

```bash
# terminal 1 - API on :8080  (H2 file DB in backend/data, images in backend/data/images)
cd backend
mvn spring-boot:run            # or: mvn package && java -jar target/api.jar

# terminal 2 - UI on :5173 (proxies /api to :8080)
cd frontend
npm install
npm run dev                    # open http://localhost:5173
```

> Port clash? Run the API on another port with `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8090`
> and start the UI with `API_TARGET=http://localhost:8090 npm run dev` (PowerShell: `$env:API_TARGET="http://localhost:8090"; npm run dev`).

With PostgreSQL instead of H2: `docker compose up --build` (needs Docker Desktop).

Tests: `cd backend && mvn test`.

## Pages

| Page | What it shows | AWS concept later |
|---|---|---|
| `/` Catalog | products, add to cart, sends click events | S3/CloudFront images, Firehose clickstream |
| `/admin` | create product + image, delete | S3 presigned upload (switches automatically when `storage=s3`) |
| `/cart` | cart + checkout (email containing `fail` = failing order) | DynamoDB carts (TTL), SQS |
| `/orders`, `/orders/:id` | order status NEW -> PROCESSED / FAILED | SQS + DLQ, Lambda worker, SNS email |
| `/lab` | live whoami table, Stress CPU, Break/Fix health, Slow request | ALB, Auto Scaling, Multi-AZ failover, timeouts |

## API

| Method | Path | Notes |
|---|---|---|
| GET | `/api/products`, `/api/products/{id}` | `{id}` uses cache-aside, response has `cacheHit` |
| POST | `/api/products` (multipart) | local upload: `name`, `price`, `description`, `image` |
| POST | `/api/products` (JSON) | `{name, price, description, imageKey}` after a presigned upload |
| POST | `/api/products/upload-url?filename=&contentType=` | presigned PUT `{key,url}`. Returns **501** until you write the S3 adapter |
| DELETE | `/api/products/{id}` | |
| GET/DELETE | `/api/cart/{sessionId}` | |
| POST | `/api/cart/{sessionId}/items` | `{productId, quantity}` |
| DELETE | `/api/cart/{sessionId}/items/{productId}` | |
| POST | `/api/orders` | `{sessionId, email}` -> 201 status NEW, processed in background |
| GET | `/api/orders`, `/api/orders/{id}` | |
| POST | `/internal/orders/{id}/processed` | called by the worker (Lambda in Phase 8). Idempotent. Keep it private |
| POST | `/api/track` | `{event, productId, sessionId}` clickstream |
| GET | `/api/lab/whoami` | host, EC2 instance id + AZ (IMDSv2), DB URL, health, stress flag |
| GET | `/api/lab/config` | active adapters + profiles |
| GET | `/api/lab/health` | **use as the ALB health check path**. 503 when broken |
| POST | `/api/lab/health/{up\|down}`, `/api/lab/stress?seconds=` | |
| GET | `/api/lab/slow?seconds=` | timeout experiments |
| GET | `/actuator/health` | Spring health |

## Ports and adapters: what you add in each phase

Every infrastructure need is an interface with a **local** implementation (already done) selected by a
property. In each phase you write the **AWS** implementation with
`@ConditionalOnProperty(name = "cloudmart.<x>", havingValue = "<aws>")` and flip the property.

| Property (env var) | Interface | Local (done) | You add | Phase |
|---|---|---|---|---|
| `cloudmart.storage` (`CLOUDMART_STORAGE`) | `storage/StorageService` | `LocalStorageService` | `S3StorageService` (`s3`) | 4 |
| `spring.datasource.url` / `DB_URL` | JPA | H2 file | RDS PostgreSQL / Aurora (`aws` profile) | 5 |
| `cloudmart.cache` | `cache/CacheService` | `InMemoryCacheService` | `RedisCacheService` (`redis`) | 5 |
| `cloudmart.cart` | `cart/CartRepository` | `InMemoryCartRepository` | `DynamoCartRepository` (`dynamodb`) | 5 |
| `cloudmart.events` | `order/OrderEventPublisher` | `LocalOrderEventPublisher` + `LocalOrderWorker` | `SqsOrderEventPublisher` (`sqs`), then a Lambda worker | 7-8 |
| `cloudmart.notifier` | `order/Notifier` | `LogNotifier` | `SnsNotifier` (`sns`) | 7 |
| `cloudmart.tracking` | `tracking/ClickTracker` | `LogClickTracker` | `FirehoseClickTracker` (`firehose`) | 7 |
| config / secrets | `application.yml` | plain values | Secrets Manager + Parameter Store | 9 |
| auth | none yet | - | Cognito JWT (`spring-boot-starter-oauth2-resource-server`) | 8 |

The AWS SDK v2 BOM is already in `pom.xml`. Add modules without a version, e.g.:

```xml
<dependency><groupId>software.amazon.awssdk</groupId><artifactId>s3</artifactId></dependency>
```

Credentials: **never put keys in code or config.** Locally the SDK uses your `AWS_PROFILE` (e.g. `cloudmart`).
On EC2/ECS/Lambda it uses the attached IAM role automatically.

## Server configuration (EC2 user data / ECS task definition)

```
SPRING_PROFILES_ACTIVE=aws
DB_URL=jdbc:postgresql://<rds-endpoint>:5432/cloudmart
SPRING_DATASOURCE_USERNAME=...      # Phase 9: Secrets Manager instead
SPRING_DATASOURCE_PASSWORD=...
CLOUDMART_STORAGE=s3
CLOUDMART_BUCKET=cloudmart-images-<account-id>
CLOUDMART_CORS_ALLOWED-ORIGINS=http://cloudmart-web-<id>.s3-website-<region>.amazonaws.com
```

## Build artifacts for AWS

| Artifact | Command | Used in |
|---|---|---|
| `backend/target/api.jar` | `mvn package` | Phase 2 (EC2 user data downloads it from S3) |
| Docker image | `docker build -t cloudmart-api ./backend` | Phase 8 (ECR + ECS Fargate) |
| `frontend/dist/` | `npm run build` (set `VITE_API_BASE` first if the API is on another origin) | Phase 4 (S3 website), Phase 6 (CloudFront) |

See `frontend/.env.example` for the `VITE_API_BASE` values per phase.
