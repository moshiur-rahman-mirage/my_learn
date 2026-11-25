@@ 1 | MODULE | Phase 10 — Monitoring, Auditing & Infrastructure as Code | CloudWatch, X-Ray, CloudTrail, Config, Systems Manager, CloudFormation/CDK | MUST
# Phase 10: Observe, audit and automate

You can't run what you can't see, and you can't repeat what you built by clicking.

**CloudMart goal this phase:** a **CloudWatch dashboard and alarms** (errors, latency, queue backlog, DLQ), **X-Ray** traces across API → SQS → Lambda, a **CloudTrail** trail and **Config** rules. Finally, **rebuild CloudMart's infrastructure as code** with CloudFormation or CDK.

@@ 2 | MODULE | Monitoring and Observability | CloudWatch metrics, logs, alarms, X-Ray | MUST
Metrics, logs and traces.

@@ 3 | TOPIC | CloudWatch metrics and alarms | Namespaces, dimensions, resolution, custom metrics, alarm actions, composite alarms | MUST
# CloudWatch metrics and alarms

## Metrics
- A **metric** is a time series in a **namespace** (`AWS/EC2`, `AWS/SQS`, `CloudMart`) with **dimensions** (InstanceId, QueueName).
- **Standard resolution:** 1 minute (EC2 *basic monitoring* is 5 minutes, **detailed monitoring** is 1 minute and paid). **High-resolution custom metrics:** down to 1 second.
- **EC2 default metrics:** CPU, network, disk (instance store), status checks. **Not included: memory and disk-space usage**. They need the **CloudWatch agent** (exam favourite!).
- **Custom metrics:** `PutMetricData`, or the **Embedded Metric Format** (structured log lines turned into metrics automatically).
- Retention: 1-minute data for 15 days, 5-minute data for 63 days, 1-hour data for 455 days (15 months).

## Alarms
- States: `OK`, `ALARM`, `INSUFFICIENT_DATA`. Evaluate N of M datapoints over a period.
- **Actions:** SNS notification, **EC2 actions** (stop, terminate, reboot, **recover** onto healthy hardware), **Auto Scaling** policies, Systems Manager OpsItems/incidents.
- **Composite alarms:** combine alarms with AND/OR to reduce noise.
- Anomaly detection bands (ML-based expected range).

## Other CloudWatch features
- **Dashboards:** cross-account and cross-Region.
- **Synthetics canaries:** scripts that test your endpoints/UI every N minutes.
- **RUM:** real user monitoring in the browser.
- **Container Insights / Lambda Insights / Application Signals:** curated performance views.
- **Metric streams:** stream metrics to Firehose (→ third-party tools).

## Exam patterns
- "Monitor EC2 memory usage" → install the **CloudWatch agent** (custom metrics).
- "Automatically recover an instance when the underlying hardware fails" → a CloudWatch alarm on `StatusCheckFailed_System` with the **recover** action.
- "Alert when the queue backlog grows" → an alarm on `ApproximateNumberOfMessagesVisible` or `ApproximateAgeOfOldestMessage`.

## Lab task (CloudMart)
1. Publish a custom metric `CloudMart/OrdersPlaced` from the API (`cloudwatch:PutMetricData`, or Micrometer's `micrometer-registry-cloudwatch2` with Spring Boot Actuator).
2. Alarms → SNS `cloudmart-notifications`: **DLQ `ApproximateNumberOfMessagesVisible` > 0**, **ALB `HTTPCode_Target_5XX_Count` > 5 in 5 minutes**, **ALB `TargetResponseTime` p95 > 1 s**, **Lambda order-worker `Errors` > 0**.
3. Build dashboard **`CloudMart`**: orders/minute, ALB requests and 5xx, target response time, ECS CPU, RDS CPU and connections, SQS depth, Lambda duration and concurrency.

Q: Which EC2 metrics require the CloudWatch agent?
A: Memory utilization and disk-space utilization (OS-level metrics).

Q: How can a CloudWatch alarm automatically move an instance to healthy hardware?
A: Use the EC2 recover action on a StatusCheckFailed_System alarm.

@@ 3 | TOPIC | CloudWatch Logs | Log groups, retention, agent, Logs Insights, metric filters, subscriptions | MUST
# CloudWatch Logs

- **Log group** (e.g. `/ecs/cloudmart-api`) → **log streams** (one per container, instance or Lambda environment) → events.
- **Retention:** never expire by default (cost!). Set 1 day to 10 years per group.
- Sources: Lambda (automatic), ECS (`awslogs` driver), the **CloudWatch agent** on EC2/on-prem, API Gateway, VPC Flow Logs, Route 53, CloudTrail.
- Encryption with KMS per log group.

## Getting value from logs
- **Logs Insights:** an interactive query language over logs.
- **Metric filters:** turn log patterns into metrics (e.g. count `ERROR` lines), then alarm on them.
- **Subscription filters:** stream logs in real time to **Lambda, Kinesis Data Streams, Firehose or OpenSearch** (central log accounts, SIEM).
- **Export to S3:** batch export (not real time, up to 12 h). For near-real time use a subscription + Firehose.
- **Live Tail:** watch logs as they arrive.
- **Log anomaly detection** and pattern analysis.

```text
fields @timestamp, @message
| filter @message like /ERROR/
| stats count(*) as errors by bin(5m)
| sort errors desc
```
- This Logs Insights query counts ERROR lines in 5-minute buckets. It's handy for finding when a deployment started failing.

## Exam patterns
- "Centralize logs from many accounts in near real time" → subscription filters → Kinesis/Firehose → a central S3/OpenSearch.
- "Create an alarm when the word 'Exception' appears" → a **metric filter** + alarm.

## Lab task (CloudMart)
1. Set retention to **14 days** on all CloudMart log groups.
2. Make Spring Boot log JSON (`logging.structured.format.console=ecs` in Boot 3.4+).
3. Create a metric filter `?ERROR ?Exception` on `/ecs/cloudmart-api` → metric `CloudMart/AppErrors`, with an alarm > 3 in 5 minutes.
4. Run the Logs Insights query above after placing a few "fail" orders.

Q: What is the default retention of CloudWatch Logs?
A: Never expire (indefinite). Set a retention policy to control cost.

Q: How do you trigger an alarm when a specific error string appears in logs?
A: Create a metric filter on the log group and an alarm on the resulting metric.

@@ 3 | TOPIC | AWS X-Ray and distributed tracing | Traces, segments, service map, OpenTelemetry | SHOULD
# AWS X-Ray

**X-Ray** traces requests **as they travel through distributed components** (API → SQS → Lambda → DynamoDB) and shows a **service map** with latency, errors and bottlenecks per hop.

- **Trace** = one request end to end. **Segments/subsegments** = the work in each service or downstream call. **Annotations** = indexed key/values you can search by (e.g. `orderId`).
- **Sampling rules** control the cost (e.g. 5% of requests).
- Instrument with the **AWS Distro for OpenTelemetry (ADOT)** (recommended) or the X-Ray SDK. Turn on **active tracing** on Lambda, API Gateway and Step Functions with one checkbox.
- On EC2/ECS, run the X-Ray daemon or the ADOT collector as a sidecar. The task role needs `xray:PutTraceSegments`.
- **CloudWatch Application Signals** builds service-level dashboards (SLOs) on top of this tracing data.

## Exam pattern
"Identify which microservice causes latency in a distributed application" → **X-Ray**.

## Lab task (CloudMart)
1. Enable **active tracing** on the `cloudmart-order-worker` Lambda and on the Step Functions state machine.
2. For the API: run the **ADOT Java agent** (`-javaagent:aws-opentelemetry-agent.jar` in the Dockerfile) with an ADOT collector sidecar in the ECS task definition.
3. Place an order and open **X-Ray → Trace map**. You should see CloudFront/ALB → cloudmart-api → SQS → order-worker → SNS, with the time spent at each step.

Q: Which service shows a service map and per-hop latency for a request across microservices?
A: AWS X-Ray.

@@ 2 | MODULE | Auditing and Configuration Management | CloudTrail, Config, Systems Manager, Trusted Advisor | MUST
Who did what, is it compliant, and how to manage fleets.

@@ 3 | TOPIC | AWS CloudTrail | Management, data and Insights events; trails; log integrity; Lake | MUST
# AWS CloudTrail

**CloudTrail** records **API calls** made in your account (console, CLI, SDK, services): **who** (identity), **what** (action), **when**, **from where** (IP) and **on which resource**.

**Analogy:** the CCTV recording of everything anyone does in your AWS building.

## Event types
- **Management events** (control plane): CreateBucket, RunInstances, AttachRolePolicy. **Logged by default.**
- **Data events** (data plane): S3 GetObject/PutObject, Lambda Invoke, DynamoDB item-level. **Off by default** (high volume, extra cost).
- **Insights events:** detect **unusual activity** (e.g. a burst of TerminateInstances or IAM calls).

## Storage
- **Event history:** the last **90 days** of management events, free, searchable in the console.
- **Trail:** delivers events continuously to **S3** (and optionally CloudWatch Logs) for long-term retention. It can be an **organization trail** for all accounts and all Regions.
- **Log file integrity validation:** digest files prove the logs weren't modified.
- Protect the bucket: separate log account, Object Lock, MFA delete, SSE-KMS.
- **CloudTrail Lake:** a managed data store with SQL queries over events (years of retention).

## CloudTrail vs CloudWatch vs Config
| Question | Service |
|---|---|
| **Who** deleted the security group yesterday? | **CloudTrail** |
| **How** is the CPU doing / alert on errors? | **CloudWatch** |
| **What did the resource configuration look like** last week, and is it compliant? | **Config** |

## Exam patterns
- "Audit who made API changes" → **CloudTrail**.
- "Keep audit logs for 7 years, tamper-proof" → a trail to S3 with Object Lock + log file validation + lifecycle to Glacier.
- "React in real time to a specific API call (e.g. root login)" → CloudTrail → **EventBridge** rule → SNS/Lambda.

## Lab task (CloudMart)
1. Create trail **`cloudmart-trail`**: all Regions, management events, **data events for the `cloudmart-images` bucket (write only)**, log file validation on, a new bucket `cloudmart-trail-<id>`, SSE-KMS, and also send to CloudWatch Logs.
2. Delete a test product image and find the `DeleteObject` event with your identity.
3. EventBridge rule: `ConsoleLogin` by the root user → SNS alert.

Q: How long does CloudTrail event history keep management events by default?
A: 90 days. Create a trail to keep them longer in S3.

Q: Are S3 object-level API calls logged by CloudTrail by default?
A: No. They're data events and must be enabled explicitly.

Q: Which service answers "who terminated this EC2 instance"?
A: AWS CloudTrail.

@@ 3 | TOPIC | AWS Config | Configuration history, managed rules, remediation, conformance packs, aggregators | MUST
# AWS Config

**AWS Config** continuously **records the configuration of your resources** and their changes over time, and **evaluates them against rules**.

## Features
- **Configuration timeline** per resource: what changed, when, linked to the CloudTrail event that changed it.
- **Config rules:** **managed rules** (e.g. `s3-bucket-public-read-prohibited`, `encrypted-volumes`, `restricted-ssh`, `rds-multi-az-support`, `required-tags`) or custom rules (Lambda or Guard). They're evaluated on change or periodically.
- **Remediation:** automatic or manual fixes through **SSM Automation documents** (e.g. re-enable bucket encryption, remove 0.0.0.0/0 SSH).
- **Conformance packs:** a bundle of rules + remediations (e.g. "Operational best practices for PCI DSS").
- **Aggregator:** a multi-account, multi-Region compliance view.
- Config **does not prevent** actions. It **detects and remediates**. (Prevent = SCPs / IAM.)

## Exam patterns
- "Check that all EBS volumes are encrypted and see historical compliance" → **Config rule**.
- "Automatically fix non-compliant security groups" → Config rule + **SSM Automation remediation**.
- "Track configuration changes for audits" → Config.

## Lab task (CloudMart)
1. Enable Config recording for all resource types (it costs per configuration item, so it's cheap for a small lab).
2. Add managed rules: `s3-bucket-public-read-prohibited`, `s3-bucket-ssl-requests-only`, `rds-storage-encrypted`, `required-tags` (tag `Project`), `vpc-sg-open-only-to-authorized-ports`.
3. Create a test SG that allows 22 from 0.0.0.0/0, watch it turn **NONCOMPLIANT**, and add the remediation `AWS-DisablePublicAccessForSecurityGroup`.

Q: Does AWS Config prevent non-compliant changes?
A: No. It records and evaluates configurations and can trigger remediation. Use SCPs or IAM to prevent.

Q: Which service gives a timeline of a resource's configuration changes?
A: AWS Config.

@@ 3 | TOPIC | AWS Systems Manager | Session Manager, Run Command, Patch Manager, Automation, Parameter Store, Fleet Manager | SHOULD
# AWS Systems Manager (SSM)

A suite for **operating EC2 and on-prem servers at scale**. It needs the **SSM agent** plus an instance role with `AmazonSSMManagedInstanceCore`.

| Capability | What it does |
|---|---|
| **Session Manager** | Browser/CLI shell with no SSH, no bastion and no open ports. Sessions are logged to S3/CloudWatch |
| **Run Command** | Run a script on many instances at once (selected by tag), without SSH |
| **Patch Manager** | Scan and install OS patches on a schedule (maintenance windows, patch baselines) |
| **Automation** | Runbooks for common tasks (create an AMI, restart, remediate Config findings) |
| **Parameter Store** | Configuration and secrets (Phase 9) |
| **State Manager** | Keep instances in a desired state (e.g. agent installed) |
| **Inventory / Fleet Manager** | Software inventory and remote management |
| **OpsCenter / Incident Manager** | Operational issues and incident response |
| **Distributor** | Package and install software |

## Exam patterns
- "Patch hundreds of EC2 instances and on-prem servers automatically" → **SSM Patch Manager**.
- "Run a command on all instances tagged Env=prod" → **Run Command**.
- "Secure shell access with auditing, no port 22" → **Session Manager**.
- Hybrid: register on-prem servers with SSM through **hybrid activations**.

## Lab task (CloudMart)
If any CloudMart EC2 instances are left (or launch one temporary instance from `lt-cloudmart`): use **Run Command** with `AWS-RunShellScript` to execute `java -version` on all instances tagged `Project=cloudmart`, then do a Patch Manager **Scan** of them.

Q: Which Systems Manager capability patches EC2 and on-prem servers on a schedule?
A: Patch Manager.

Q: How can you run a script on many instances selected by tag without SSH?
A: SSM Run Command.

@@ 3 | TOPIC | Trusted Advisor, Health Dashboard and Compute Optimizer | Best-practice checks, service health, rightsizing | SHOULD
# Advisory services

## AWS Trusted Advisor
Checks your account against best practices in **cost optimization, performance, security, fault tolerance, service limits (quotas) and operational excellence**.
- **Basic/Developer support:** core security checks + service quotas only.
- **Business, Enterprise On-Ramp and Enterprise support:** **all checks**, plus API and EventBridge integration.
- Examples: idle load balancers, underused EC2, open SG ports, MFA on root, S3 bucket permissions, EBS snapshots missing, service quota usage over 80%.

## AWS Health Dashboard
- **Service health:** the public status of all AWS services.
- **Your account health:** events that **affect your resources** (scheduled EC2 retirement, maintenance, outages in your AZ). Integrates with **EventBridge** for automated responses.

## AWS Compute Optimizer
ML-based **rightsizing recommendations** for EC2, ASGs, EBS, Lambda memory, ECS on Fargate, and RDS, using CloudWatch metrics. E.g. "this m5.xlarge averages 5% CPU, move to t3.large and save 60%."

## Service Quotas
View and request increases for limits (e.g. Lambda concurrency, EC2 vCPUs). You can create CloudWatch alarms on quota usage.

## Exam patterns
- "Be notified when AWS schedules maintenance that affects our instances" → **AWS Health** events → EventBridge → SNS.
- "Recommend the optimal Lambda memory / EC2 size" → **Compute Optimizer**.
- "Check for service limits approaching" → **Trusted Advisor / Service Quotas**.

## Lab task (CloudMart)
Open Trusted Advisor and read the free checks (root MFA, open security groups). Opt in to **Compute Optimizer**. It needs about 14 days of data, so check its recommendations for the Lambda functions at the end of the course.

Q: Which service provides rightsizing recommendations for EC2, Lambda and EBS?
A: AWS Compute Optimizer.

Q: Which service notifies you about AWS events affecting your specific resources?
A: AWS Health Dashboard (your account health), with EventBridge integration.

@@ 2 | MODULE | Infrastructure as Code and Deployment | CloudFormation, CDK, StackSets, CI/CD | MUST
Rebuild everything with code.

@@ 3 | TOPIC | AWS CloudFormation | Templates, stacks, change sets, drift, StackSets, rollback | MUST
# AWS CloudFormation

**CloudFormation** creates and manages AWS resources from **templates** (YAML/JSON). You describe *what* you want, and CloudFormation works out the order, creates it, and can update or delete it as one unit (a **stack**).

**Analogy:** an architectural blueprint. Give the same blueprint to any builder (account or Region) and get an identical building.

## Template sections
`Parameters` (inputs), `Mappings` (lookups, e.g. AMI per Region), `Conditions`, **`Resources`** (the only required section), `Outputs` (values for other stacks: **exports** and `Fn::ImportValue`).

```yaml
AWSTemplateFormatVersion: "2010-09-09"
Description: CloudMart orders queue with DLQ
Parameters:
  Env: { Type: String, AllowedValues: [dev, prod], Default: dev }
Resources:
  OrdersDLQ:
    Type: AWS::SQS::Queue
    Properties:
      QueueName: !Sub cloudmart-orders-dlq-${Env}
      MessageRetentionPeriod: 1209600          # 14 days
  OrdersQueue:
    Type: AWS::SQS::Queue
    Properties:
      QueueName: !Sub cloudmart-orders-${Env}
      VisibilityTimeout: 180
      RedrivePolicy:
        deadLetterTargetArn: !GetAtt OrdersDLQ.Arn
        maxReceiveCount: 3
Outputs:
  OrdersQueueUrl:
    Value: !Ref OrdersQueue
    Export: { Name: !Sub cloudmart-${Env}-orders-url }
```
- `!Sub` builds strings with parameters, `!GetAtt` reads an attribute of another resource (the DLQ ARN), and `!Ref` on a queue returns its URL. CloudFormation creates the DLQ first because the main queue references it.
- The export lets another stack (e.g. the ECS service stack) import the queue URL without hard-coding it.

## Key features
- **Change sets:** preview what an update will change or replace before executing it.
- **Rollback** on failure (automatic). **Stack policies** protect critical resources from updates.
- **DeletionPolicy: Retain / Snapshot** (keep the RDS snapshot or S3 bucket when the stack is deleted).
- **Drift detection:** finds manual changes made outside CloudFormation.
- **Nested stacks** (reuse components) vs **cross-stack references** (exports).
- **StackSets:** deploy a stack to **many accounts and Regions** at once (e.g. a baseline Config/GuardDuty setup across the Organization).
- **Helper scripts** (`cfn-init`, `cfn-signal`) and **CreationPolicy/WaitCondition**: wait until the EC2 app reports it's ready.
- **AWS Infrastructure Composer:** a visual designer.

## Exam patterns
- "Replicate the same environment in multiple Regions/accounts quickly and consistently" → **CloudFormation (StackSets)**.
- "Keep the DB snapshot when the stack is deleted" → `DeletionPolicy: Snapshot`.
- "Detect manual changes" → **drift detection**.

## Lab task (CloudMart)
Write `infra/messaging.yaml` (the template above + the SNS topic + EventBridge bus) and deploy it as stack `cloudmart-messaging-dev`:
```bash
aws cloudformation deploy --template-file infra/messaging.yaml --stack-name cloudmart-messaging-dev --parameter-overrides Env=dev
```
- `deploy` creates a change set and executes it, and it's idempotent. Run it again after editing `VisibilityTimeout` to see an update. Change the queue in the console, then run **drift detection**.

Q: Which CloudFormation feature previews the impact of a stack update?
A: Change sets.

Q: How do you deploy one CloudFormation template to many accounts and Regions?
A: CloudFormation StackSets.

Q: How do you keep an RDS snapshot when deleting a stack?
A: Set DeletionPolicy: Snapshot on the DB resource.

@@ 3 | TOPIC | AWS CDK, SAM and CI/CD services | Code-defined infrastructure, serverless templates, CodePipeline/CodeBuild/CodeDeploy | SHOULD
# CDK, SAM and CI/CD

## AWS CDK (Cloud Development Kit)
Define infrastructure in **TypeScript, Python, Java, C# or Go**. It **synthesizes to CloudFormation**. High-level constructs create many resources with secure defaults.

```java
// infra/src/main/java/CloudMartStack.java  (CDK v2, Java)
public class CloudMartStack extends Stack {
    public CloudMartStack(Construct scope, String id, StackProps props) {
        super(scope, id, props);
        Vpc vpc = Vpc.Builder.create(this, "Vpc").maxAzs(2).natGateways(1).build();
        Cluster cluster = Cluster.Builder.create(this, "Cluster").vpc(vpc).build();
        ApplicationLoadBalancedFargateService.Builder.create(this, "Api")
            .cluster(cluster).desiredCount(2).cpu(512).memoryLimitMiB(1024)
            .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                .image(ContainerImage.fromAsset("../backend"))       // builds and pushes the Docker image to ECR
                .containerPort(8080).build())
            .build();
    }
}
```
- About 15 lines create a VPC (subnets, NAT, route tables), an ECS cluster, a task definition, an ALB, target groups, security groups and the service. That's hundreds of lines of CloudFormation. `cdk diff` previews it and `cdk deploy` applies it.

## AWS SAM (Serverless Application Model)
A CloudFormation extension with short syntax for **Lambda, API Gateway, DynamoDB and Step Functions**, plus `sam local invoke` to test Lambdas locally. Exam: "define and deploy serverless apps" → SAM.

## CI/CD services
| Service | Role |
|---|---|
| **CodePipeline** | Orchestrates source → build → test → deploy stages |
| **CodeBuild** | Managed build/test containers (runs `mvn package`, `docker build`) |
| **CodeDeploy** | Deploys to EC2/on-prem, **Lambda** (canary/linear traffic shifting) and **ECS** (blue/green) |
| Source | GitHub, Bitbucket, GitLab (through CodeConnections), S3, ECR, or CodeCommit |

## Deployment strategies (exam)
- **All at once:** fastest, downtime risk.
- **Rolling:** batch by batch. **Rolling with extra batch:** keeps full capacity.
- **Immutable:** a new ASG, then swap. Safest rollback.
- **Blue/green:** two full environments, switch traffic (ALB target groups, Route 53 weighted, CodeDeploy for ECS).
- **Canary:** shift 10% first, then the rest (Lambda aliases, API Gateway canary, CodeDeploy).

## Lab task (CloudMart)
Choose **one** IaC path for the milestone: either CloudFormation templates per layer (network, data, messaging, app), or one CDK app in Java like the example. Optionally add a CodePipeline: GitHub → CodeBuild (build the image, push to ECR) → ECS deploy.

Q: What does the AWS CDK produce from your code?
A: CloudFormation templates (synthesis), which are then deployed as stacks.

Q: Which service performs blue/green deployments for ECS and canary deployments for Lambda?
A: AWS CodeDeploy.

@@ 3 | TOPIC | Lab milestone: CloudMart observable and fully in code | Dashboards, alarms, tracing, audit, and an IaC rebuild in a fresh environment | MUST
# Lab milestone: observable and reproducible

## Part A: observability checklist
- [ ] CloudWatch dashboard `CloudMart` with business, app and infrastructure metrics.
- [ ] Alarms (5xx, latency, DLQ > 0, Lambda errors, app error metric filter) → SNS email.
- [ ] Log retention 14 days, structured JSON logs.
- [ ] X-Ray trace map covers API → SQS → Lambda.
- [ ] CloudTrail trail with data events on the images bucket; root-login alert.
- [ ] Config rules for encryption, public access, tags and SSH.

## Part B: infrastructure as code
Codify CloudMart in `infra/` with layered stacks:

| Stack | Contains |
|---|---|
| `cloudmart-network` | VPC, subnets, route tables, NAT (parameter: on/off), gateway endpoints, SGs |
| `cloudmart-data` | RDS (+ secret), ElastiCache, DynamoDB carts, S3 buckets (images/web/datalake) |
| `cloudmart-messaging` | SQS + DLQ, SNS, EventBridge bus/rules, Firehose |
| `cloudmart-app` | ECR, ECS cluster/service, ALB, CloudFront + OAC, WAF, Lambdas, alarms, dashboard |

## The ultimate test
Deploy all stacks with `Env=staging` into the **second Region** (or a second account). If CloudMart works there without any console clicks, you've mastered IaC. Then **delete the staging stacks**. One command removes everything (S3 buckets must be emptied first, or use a custom resource / CDK `autoDeleteObjects`).

From now on, tear down at night with `cdk destroy` / stack deletion and rebuild in the morning. That's the cheapest way to keep labbing.

Q: What is the main benefit of rebuilding an environment from CloudFormation/CDK instead of the console?
A: Repeatable, consistent and version-controlled environments that can be created in any Region/account and deleted completely with one action.
