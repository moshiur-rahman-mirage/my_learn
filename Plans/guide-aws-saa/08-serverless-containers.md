@@ 1 | MODULE | Phase 8 — Serverless & Containers | Lambda, API Gateway, Step Functions, Cognito, ECS/Fargate, EKS, Beanstalk | MUST
# Phase 8: Serverless and containers

"Least operational overhead" questions usually end here: **Lambda, API Gateway, DynamoDB, Fargate, Step Functions, Cognito**.

**CloudMart goal this phase:** move the order worker into **Lambda** (SQS-triggered), add a **thumbnail Lambda** on S3 uploads, add **Cognito** login to React, and run the API as a container on **ECS Fargate** instead of EC2.

@@ 2 | MODULE | Serverless Compute | Lambda, API Gateway, Step Functions | MUST
Run code without servers.

@@ 3 | TOPIC | AWS Lambda fundamentals | Event-driven functions, limits, pricing, concurrency, cold starts | MUST
# AWS Lambda

**Lambda** runs your function code **in response to events**. There are no servers to manage, it scales automatically, and you **pay per request and per GB-second**.

**Analogy:** a taxi instead of owning a car. You pay only for the ride, and a new taxi arrives for every passenger at the same time.

## Key limits (memorize)
| Limit | Value |
|---|---|
| **Max timeout** | **15 minutes** |
| Memory | 128 MB – **10 GB** (CPU scales with memory) |
| Ephemeral `/tmp` | 512 MB – 10 GB |
| Deployment package | 50 MB zipped / 250 MB unzipped. **Container image up to 10 GB** |
| Env variables | 4 KB |
| **Concurrency** | 1,000 per Region by default (soft limit) |
| Sync payload | 6 MB request/response |

Anything longer than 15 minutes → ECS/Fargate, AWS Batch or Step Functions.

## Invocation types
- **Synchronous:** API Gateway, ALB, SDK `invoke`. The caller waits, and the caller handles retries.
- **Asynchronous:** S3, SNS, EventBridge. Lambda queues the event, **retries twice**, then sends it to a **DLQ or an on-failure destination** (SQS, SNS, Lambda, EventBridge).
- **Event source mapping (polling):** **SQS, Kinesis, DynamoDB Streams, MSK**. Lambda polls and invokes in batches. A failed batch is retried (SQS → the queue's DLQ).

## Concurrency
- **Reserved concurrency:** guarantees, *and caps*, concurrency for one function (protects others and the downstream DB).
- **Provisioned concurrency:** pre-initialized environments → **no cold starts** (costs extra).
- **Cold start:** the first invocation in a new environment loads the runtime and your code. Java is the slowest. **Lambda SnapStart** (Java, Python, .NET) restores a snapshot of the initialized environment and cuts cold starts dramatically.

## Other features
- **Layers:** share libraries between functions. **Versions and aliases** (e.g. `prod` → v5, with weighted traffic shifting for canary deploys).
- **VPC access:** a function attached to private subnets can reach RDS/ElastiCache. To reach the internet it then needs NAT (or VPC endpoints for AWS services).
- **Execution role:** the IAM role the function uses (like an instance profile).
- **Lambda function URLs:** a simple HTTPS endpoint without API Gateway.

## Lab task (CloudMart)
None yet. The next topics build two functions. Read the limits table twice.

Q: What is the maximum execution time of a Lambda function?
A: 15 minutes.

Q: How do you eliminate Lambda cold starts for a latency-sensitive function?
A: Provisioned concurrency (or SnapStart for Java/Python/.NET to greatly reduce them).

Q: What happens when an asynchronously invoked Lambda fails?
A: Lambda retries twice, then sends the event to a configured DLQ or on-failure destination.

@@ 3 | TOPIC | CloudMart Lambda: SQS order worker | Java Lambda handler, SQS event source mapping, partial batch failures | MUST
# Order worker as a Lambda function

Moving the worker out of the API means the API servers only serve HTTP, and processing scales with the queue automatically.

```java
// lambda/order-worker  (Maven: aws-lambda-java-core, aws-lambda-java-events, AWS SDK sns/eventbridge)
public class OrderWorkerHandler implements RequestHandler<SQSEvent, SQSBatchResponse> {
    private final SnsClient sns = SnsClient.create();                 // created once per environment (reused)
    private final String topicArn = System.getenv("TOPIC_ARN");
    private final String apiBase = System.getenv("API_BASE");         // internal API to mark the order processed

    @Override
    public SQSBatchResponse handleRequest(SQSEvent event, Context ctx) {
        List<SQSBatchResponse.BatchItemFailure> failures = new ArrayList<>();
        for (SQSEvent.SQSMessage m : event.getRecords()) {
            try {
                OrderMsg o = OrderMsg.parse(m.getBody());
                Http.post(apiBase + "/internal/orders/" + o.orderId() + "/processed");   // idempotent
                sns.publish(b -> b.topicArn(topicArn).message("Order #" + o.orderId() + " confirmed")
                        .messageAttributes(Map.of("email", MessageAttributeValue.builder()
                                .dataType("String").stringValue(o.email()).build())));
            } catch (Exception e) {
                ctx.getLogger().log("failed " + m.getMessageId() + ": " + e);
                failures.add(new SQSBatchResponse.BatchItemFailure(m.getMessageId()));
            }
        }
        return new SQSBatchResponse(failures);   // only the failed messages return to the queue
    }
}
```
- Clients are created **outside** the handler method, so they're reused across invocations in the same environment. That's faster and cheaper.
- **Partial batch response** (`ReportBatchItemFailures` turned on in the event source mapping): only the failed messages are retried, not the whole batch of 10.
- The worker calls an internal API to update RDS. The alternative is to connect to RDS directly from a VPC-attached Lambda through **RDS Proxy**. Both are valid exam designs.

## Configure
1. Build a shaded/fat jar (`maven-shade-plugin`). Create function `cloudmart-order-worker`: Java 21, 512 MB, timeout 30 s, **SnapStart on** (publish a version).
2. Execution role: `AWSLambdaBasicExecutionRole` + `sqs:ReceiveMessage/DeleteMessage/GetQueueAttributes` on the orders queue + `sns:Publish`.
3. **Trigger:** SQS `cloudmart-orders`, batch size 10, report batch item failures. Set the **queue's visibility timeout ≥ 6 × the function timeout** (AWS guidance) → 180 s.
4. Turn off the in-app worker: `cloudmart.worker=false`.

## Lab task (CloudMart)
Deploy it and place an order. Check the CloudWatch log group `/aws/lambda/cloudmart-order-worker`, the email, and the order status. Then send 500 messages and watch the Lambda **concurrency** metric scale up.

Q: What setting lets a Lambda SQS consumer retry only the failed messages in a batch?
A: ReportBatchItemFailures (partial batch response) on the event source mapping.

Q: How should the SQS visibility timeout relate to a Lambda consumer's timeout?
A: It should be at least about six times the function timeout, so messages aren't redelivered while still being processed.

@@ 3 | TOPIC | CloudMart Lambda: S3 thumbnail generator | S3/EventBridge trigger, write to another prefix, avoid recursion | SHOULD
# Thumbnail function (event-driven image processing)

A product image upload to `products/` triggers a function that writes a 300 px thumbnail to `thumbnails/`.

```python
# lambda/thumbnail/app.py  (Python: quick to write; package Pillow as a layer)
import boto3, io, os
from PIL import Image

s3 = boto3.client("s3")

def handler(event, context):
    detail = event["detail"]                      # EventBridge "Object Created" event
    bucket = detail["bucket"]["name"]
    key = detail["object"]["key"]                 # e.g. products/42/main.jpg
    if not key.startswith("products/"):
        return                                    # safety net against recursion
    img = Image.open(io.BytesIO(s3.get_object(Bucket=bucket, Key=key)["Body"].read()))
    img.thumbnail((300, 300))
    out = io.BytesIO(); img.save(out, format="JPEG"); out.seek(0)
    s3.put_object(Bucket=bucket, Key=key.replace("products/", "thumbnails/", 1),
                  Body=out, ContentType="image/jpeg")
```
- Output goes to `thumbnails/`, and the rule only matches `products/`, so writing the thumbnail doesn't trigger the function again (the infinite-loop trap from Phase 4).
- A Java version works too. Python is used here because image libraries make it 15 lines, and the exam doesn't care about the language.

## Configure
- EventBridge rule on the default bus: `source = aws.s3`, `detail-type = Object Created`, `detail.bucket.name = cloudmart-images-<id>`, `detail.object.key prefix = products/` → target the Lambda.
- Execution role: `s3:GetObject` on `products/*`, `s3:PutObject` on `thumbnails/*`.
- Memory 1024 MB (faster image processing), timeout 30 s.

## Lab task (CloudMart)
Deploy it, upload a product image through the admin page, and confirm `thumbnails/...` appears. Change the catalog grid to use the thumbnail key (with a fallback to the original).

Q: An S3-triggered Lambda writes back to the same bucket and runs endlessly. How do you fix it?
A: Write output to a different prefix or bucket and filter the trigger to the input prefix only.

@@ 3 | TOPIC | Amazon API Gateway | REST vs HTTP vs WebSocket APIs, stages, throttling, caching, authorizers | MUST
# Amazon API Gateway

A fully managed service to **create, publish, secure and scale APIs** in front of Lambda, HTTP backends (ALB, ECS) or AWS services.

## API types
| Type | Use |
|---|---|
| **HTTP API** | Cheaper (~70% less), faster, simpler. JWT/OIDC authorizers, Lambda/HTTP integrations. The default choice |
| **REST API** | Full feature set: **API keys + usage plans**, request validation, **response caching**, WAF, private APIs, request/response transforms |
| **WebSocket API** | Two-way real-time (chat, live dashboards) |

## Endpoint types
- **Edge-optimized** (through CloudFront, for global clients), **Regional** (clients in the same Region), **Private** (only from the VPC through an interface endpoint).

## Important features
- **Stages** (`dev`, `prod`) with stage variables. **Canary** deployments.
- **Throttling:** account default of 10,000 req/s steady and 5,000 burst. You can set limits per stage, method or client (usage plan). Excess → **429 Too Many Requests**.
- **Caching** (REST): cache responses for a TTL, which reduces backend calls.
- **Authorization:** IAM (SigV4), **Cognito user pool authorizer**, **Lambda authorizer** (custom tokens), JWT authorizer (HTTP API).
- **Timeout: 29 seconds** for integrations (the default limit). Longer work → async (SQS/Step Functions).
- Direct **AWS service integrations** (e.g. API Gateway → SQS PutMessage) with no Lambda in between.

## Typical serverless stack
`Client → API Gateway → Lambda → DynamoDB` = fully serverless, scales to zero, least operational overhead.

## Exam patterns
- "Third-party developers need API keys with per-client quotas" → **REST API + usage plans**.
- "Protect the backend from traffic bursts" → throttling (+ caching).
- "API returns 504 for long jobs" → the 29 s integration timeout. Make it asynchronous.

## Lab task (CloudMart)
Create an **HTTP API** `cloudmart-public-api` with route `GET /products` → **HTTP integration** to the ALB `/api/products` (or a small Lambda that reads products). Add a **JWT authorizer** with Cognito after the next topic, and protect `POST /orders`. This shows the API Gateway alternative to the ALB for the same app.

Q: Which API Gateway type supports API keys, usage plans and response caching?
A: REST API.

Q: What HTTP error does API Gateway return when throttling a client?
A: 429 Too Many Requests.

Q: What is the default maximum integration timeout in API Gateway?
A: 29 seconds.

@@ 3 | TOPIC | AWS Step Functions | Serverless workflows, Standard vs Express, error handling, human approval | MUST
# AWS Step Functions

**Step Functions** orchestrates multiple steps (Lambda, ECS tasks, DynamoDB, SQS, SNS, Glue, SageMaker and 200+ services) as a **visual state machine** defined in JSON (Amazon States Language).

**Analogy:** a recipe card that a chef follows exactly: step order, what to do if a step fails, waiting for the oven, and doing tasks in parallel.

## States
`Task`, `Choice` (if/else), `Parallel`, `Map` (for each item, also *distributed map* over millions of S3 objects), `Wait`, `Pass`, `Succeed`, `Fail`. Each has **Retry** (with backoff) and **Catch** (fallback) built in.

## Workflow types
| | **Standard** | **Express** |
|---|---|---|
| Duration | Up to **1 year** | Up to 5 minutes |
| Semantics | Exactly-once | At least once |
| Price | Per state transition | Per execution + duration |
| Use | Long business processes, human approval | High-volume, short (IoT, streaming transforms) |

## Patterns
- **Wait for callback (task token):** pause until a human approves or an external system answers.
- **Saga / compensation:** on failure, run the undo steps (refund payment, release stock).
- Replace "one giant Lambda calling other Lambdas" with clear orchestration and error handling.

## Exam patterns
- "Coordinate several Lambda functions with retries, branching and a manual approval step" → **Step Functions**.
- "A workflow longer than 15 minutes" → Step Functions (Standard) orchestrating short tasks.

## Lab task (CloudMart)
Build a Standard state machine `cloudmart-order-flow`: **ReserveStock** (Pass state for now) → **Choice** `total > 500` → **WaitForApproval** (SNS email with a task token) → **ChargePayment** (Pass) → **MarkProcessed** (a Lambda or API call) with Catch → **NotifyFailure**. Trigger it from the order worker for big orders (`states:StartExecution`). Watch the visual execution graph.

Q: Which service orchestrates multi-step workflows with retries, branching and human approval?
A: AWS Step Functions.

Q: Maximum duration of a Step Functions Standard workflow?
A: One year.

@@ 3 | TOPIC | Amazon Cognito | User pools (sign-up/sign-in, JWT) vs identity pools (temporary AWS credentials) | MUST
# Amazon Cognito

Identity for **your app's end users** (customers), not employees.

| | **User pool** | **Identity pool** (federated identities) |
|---|---|---|
| Answers | "**Who** is this user?" (authentication) | "What **AWS resources** can they access?" (authorization to AWS) |
| Gives | **JWT tokens** (ID, access, refresh) | **Temporary AWS credentials** (through STS + an IAM role) |
| Features | Sign-up, sign-in, MFA, password reset, hosted UI, social/SAML/OIDC login, groups, Lambda triggers | Maps authenticated (or guest) users to IAM roles, fine-grained policies with `${cognito-identity.amazonaws.com:sub}` |
| Integrates | ALB authentication, API Gateway authorizers, your backend validating JWTs | Direct browser/mobile access to S3, DynamoDB |

**Analogy:** the user pool is the **ID card office** that verifies who you are. The identity pool is the **key desk** that swaps your ID card for a temporary room key (AWS credentials).

## Exam patterns
- "Mobile app users sign in with Google/Facebook or email" → **Cognito user pool** (with social IdPs).
- "Mobile users must upload directly to their own S3 folder" → **identity pool** with an IAM policy using the identity ID in the resource path.
- "Add login to an ALB-fronted web app without code" → **ALB authenticate action with Cognito**.

## Lab task (CloudMart)
1. Create user pool **`cloudmart-users`**: email sign-in, an app client (no secret, for the SPA), the managed login / hosted UI, and the group **`admins`**.
2. In React, use `oidc-client-ts` or AWS Amplify Auth to sign in and keep the ID/access token. Send `Authorization: Bearer <token>` on API calls.
3. In Spring Boot, add `spring-boot-starter-oauth2-resource-server` with `spring.security.oauth2.resourceserver.jwt.issuer-uri=https://cognito-idp.<region>.amazonaws.com/<poolId>`. Protect `POST /api/products` so it requires the `cognito:groups` claim to contain `admins`, and protect `/api/orders` so any logged-in user can use it.
4. Add yourself to `admins` and test that a non-admin gets 403 on the admin page.

Q: Which Cognito component issues JWTs after sign-in?
A: The user pool.

Q: Which Cognito component gives end users temporary AWS credentials to access S3 directly?
A: The identity pool.

@@ 2 | MODULE | Containers and PaaS | ECS, Fargate, ECR, EKS, App Runner, Beanstalk, Batch | MUST
Run containers and applications with less server management.

@@ 3 | TOPIC | Amazon ECS, Fargate and ECR | Clusters, task definitions, services, EC2 vs Fargate launch type, task roles | MUST
# Amazon ECS and AWS Fargate

**ECS (Elastic Container Service)** = AWS's own container orchestrator. **ECR (Elastic Container Registry)** = a private Docker image registry (with image scanning and lifecycle policies).

## Concepts
- **Task definition:** a blueprint with image, CPU/memory, ports, environment/secrets, logging, the **task role** and the execution role.
- **Task:** a running instance of a task definition (one or more containers).
- **Service:** keeps N tasks running, integrates with the **ALB** (dynamic ports, target type `ip`), replaces failed tasks, and supports rolling or blue/green (CodeDeploy) deployments.
- **Cluster:** a logical group of capacity.

## Launch types
| | **EC2 launch type** | **Fargate** |
|---|---|---|
| Servers | You manage the EC2 container instances (AMI, patching, scaling) | **Serverless**: no instances |
| Pricing | EC2 instances (Spot/RI possible) | Per task vCPU/GB-second (**Fargate Spot** up to 70% off) |
| Choose when | GPU, special instance types, maximum cost control at a big steady scale | **Least operational overhead** |

## Two IAM roles (exam trap)
- **Task execution role:** used by the *ECS agent* to **pull the image from ECR** and **write logs** to CloudWatch, and to fetch secrets for injection.
- **Task role:** used by **your application code** inside the container (S3, SQS, DynamoDB). It replaces the EC2 instance profile.

## Scaling
**Service Auto Scaling** (target tracking on CPU, memory, ALB requests per target, or SQS backlog). With the EC2 launch type, **capacity providers** scale the instances too.

## Storage
**EFS** can be mounted by tasks for persistent shared storage (Fargate supports EFS). The ephemeral storage is 20–200 GB.

## Lab task (CloudMart)
1. Create an ECR repo `cloudmart-api` and push the image from Phase 0:
```bash
aws ecr get-login-password | docker login --username AWS --password-stdin <acct>.dkr.ecr.<region>.amazonaws.com
docker build -t cloudmart-api ./backend
docker tag cloudmart-api:latest <acct>.dkr.ecr.<region>.amazonaws.com/cloudmart-api:v1
docker push <acct>.dkr.ecr.<region>.amazonaws.com/cloudmart-api:v1
```
- The login command uses your IAM identity to get a 12-hour Docker password for ECR. There are no static registry passwords.
2. Create cluster `cloudmart` (Fargate), and task definition `cloudmart-api` (0.5 vCPU, 1 GB, port 8080, **task role = a new `cloudmart-task-role` with `cloudmart-app-policy`**, logs to `/ecs/cloudmart-api`, env `SPRING_PROFILES_ACTIVE=aws`, etc.).
3. Create a service: 2 tasks in **app-a/app-b**, security group `sg-app`, attached to the ALB with a **new target group of type `ip`** (port 8080, health `/api/lab/health`). Switch the ALB rule to it.
4. The Lab page now shows container hostnames instead of instance IDs (IMDS returns nothing on Fargate, so `instanceId: local`. You can read `ECS_CONTAINER_METADATA_URI_V4` instead as an exercise).
5. Scale the old ASG to 0.

Q: Which ECS IAM role does application code in the container use to call AWS APIs?
A: The task role. The task execution role is for pulling images and writing logs.

Q: Which ECS launch type removes the need to manage EC2 instances?
A: Fargate.

@@ 3 | TOPIC | Amazon EKS | Managed Kubernetes, node groups, Fargate profiles, when to choose it | SHOULD
# Amazon EKS (Elastic Kubernetes Service)

**EKS** runs a managed **Kubernetes** control plane (highly available across AZs). You run workloads on:
- **Managed node groups** (EC2, AWS manages provisioning and updates), self-managed nodes, **Fargate profiles** (serverless pods), or **EKS Auto Mode** (AWS manages the nodes completely).
- Pods get AWS permissions through **IRSA / EKS Pod Identity** (IAM roles for service accounts).
- Storage through CSI drivers: EBS, EFS, FSx.
- The **AWS Load Balancer Controller** creates ALBs/NLBs from Kubernetes Ingress/Service objects.

## ECS vs EKS
| Choose ECS when | Choose EKS when |
|---|---|
| AWS-native, simplest, least overhead | The company **already uses Kubernetes**, wants portability across clouds/on-prem, needs the K8s ecosystem (Helm, operators) |

**EKS Anywhere / EKS Hybrid Nodes:** run Kubernetes on-prem with the same tooling.

## Exam pattern
"Migrate existing Kubernetes workloads to AWS with minimal changes / use open-source K8s tooling" → **EKS**.

## Lab task (CloudMart)
Optional only (the EKS control plane bills hourly): write in your README the Kubernetes objects CloudMart would need: Deployment (2 replicas), Service, Ingress (ALB controller), ServiceAccount with IRSA, ConfigMap/Secret.

Q: A company runs Kubernetes on-prem and wants a managed AWS equivalent with minimal changes. Which service?
A: Amazon EKS.

@@ 3 | TOPIC | Elastic Beanstalk, App Runner, AWS Batch and Lightsail | PaaS and specialized compute choices | SHOULD
# Other compute options

| Service | What it is | Exam trigger |
|---|---|---|
| **Elastic Beanstalk** | PaaS: upload code (Java, .NET, Node, Python, Docker…) and it creates EC2, ASG, ELB, CloudWatch and RDS (optional) for you. You keep control of the resources | "Developers want to deploy a web app quickly without managing infrastructure, but still on EC2" |
| **AWS App Runner** | Point it at a container image or source repo and get an HTTPS web service with autoscaling, no cluster | "Simplest way to run a containerized web app/API" |
| **AWS Batch** | Runs **batch jobs** (containers) at any scale on EC2/Spot/Fargate. Manages queues and compute environments | "Long-running (>15 min) batch processing", "HPC jobs", "nightly jobs on Spot" |
| **Amazon Lightsail** | Simple VPS bundles (VM + storage + data transfer) for a fixed monthly price | "Simple website or blog with predictable price, little AWS knowledge" |
| **AWS Outposts / Local Zones** | Run AWS on-prem or close to users | Data residency or ultra-low latency |

## Beanstalk details
- Environments: **web server tier** (ELB + ASG) and **worker tier** (pulls from an SQS queue).
- Deployment policies: all at once, rolling, rolling with additional batch, **immutable**, traffic splitting, and blue/green (swap environment URLs).

## Lambda vs Batch vs Fargate
- Short event-driven tasks (< 15 minutes) → Lambda.
- Long batch jobs with queues and job dependencies → **Batch**.
- Long-running services → ECS/Fargate.

## Lab task (CloudMart)
Optional: deploy `api.jar` to **Elastic Beanstalk** (Java SE platform, single instance), open the URL, and look at the EC2/ASG/CloudWatch resources it created for you. Then terminate the environment. You'll understand what "PaaS" means.

Q: Which service runs batch jobs longer than 15 minutes on managed compute, including Spot?
A: AWS Batch.

Q: Which service deploys web apps by uploading code, while provisioning EC2, ELB and Auto Scaling automatically?
A: AWS Elastic Beanstalk.

@@ 3 | TOPIC | Lab milestone: serverless and containerized CloudMart | Lambda worker + thumbnails, Cognito, API on Fargate, optional API Gateway | MUST
# Lab milestone: serverless and containers

## Architecture now

```text
CloudFront --> S3 (React)          Cognito user pool (login, admins group)
          \--> ALB --> ECS Fargate service cloudmart-api (2 tasks, private subnets, task role)
                            |--> RDS / ElastiCache / DynamoDB (via gateway endpoint)
                            |--> SQS cloudmart-orders --> Lambda order-worker --> SNS email
                                                                       \--> Step Functions (big orders)
S3 products/* --EventBridge--> Lambda thumbnail --> S3 thumbnails/*
```

## Checklist
- [ ] The ECR image is versioned (`v1`, `v2`, …). A new deployment = register a new task definition revision and update the service.
- [ ] ECS service autoscaling: target tracking on **ALBRequestCountPerTarget** or CPU 60%, min 2, max 4.
- [ ] Lambda order-worker with SnapStart, partial batch failures, DLQ via the queue redrive.
- [ ] Cognito-protected admin endpoints.
- [ ] Optional: the HTTP API Gateway route with a JWT authorizer.

## Experiment
Stop one Fargate task manually. The ECS service starts a replacement within seconds. Compare that with the EC2 ASG replacement time from Phase 3.

## Teardown
Set the ECS service desired count to 0. Lambda, SQS and Cognito cost nothing when idle. Delete the ALB/NAT if you're pausing for days.

Q: Compared with EC2 + ASG, what operational work does ECS on Fargate remove?
A: Managing, patching and scaling EC2 instances. AWS runs the underlying compute, and you manage only task definitions and services.
