@@ 1 | MODULE | Phase 12 — Architecture, Disaster Recovery, Migration & Cost | Well-Architected, DR strategies, migration tools, cost optimization | MUST
# Phase 12: Thinking like a solutions architect

This phase ties everything together: the **Well-Architected Framework**, **disaster recovery**, **migration** and **cost optimization**. These are the "big picture" questions.

**CloudMart goal this phase:** run a **DR drill** into a second Region (pilot light), do a **cost review**, and write a final Well-Architected review of CloudMart.

@@ 2 | MODULE | Architecture Principles | Well-Architected Framework and classic patterns | MUST
The lens AWS uses to judge every design.

@@ 3 | TOPIC | AWS Well-Architected Framework | Six pillars and their key design principles | MUST
# The Well-Architected Framework (6 pillars)

| Pillar | Core idea | Typical AWS answers |
|---|---|---|
| **Operational Excellence** | Run and improve systems: automate, make small reversible changes, learn from failures | IaC (CloudFormation), CI/CD, CloudWatch, runbooks (SSM) |
| **Security** | Protect data and systems: identity, traceability, defense in depth, encryption | IAM least privilege, KMS, CloudTrail, GuardDuty, WAF, private subnets |
| **Reliability** | Recover from failure, scale to meet demand, test recovery | Multi-AZ, Auto Scaling, SQS decoupling, backups, DR, health checks |
| **Performance Efficiency** | Use the right resources efficiently, go global in minutes, use serverless, experiment | Right instance/storage type, caching (CloudFront/ElastiCache), read replicas |
| **Cost Optimization** | Pay only for what you need, measure efficiency, stop spending on undifferentiated heavy lifting | Savings Plans, Spot, S3 tiers, rightsizing, serverless, auto scaling |
| **Sustainability** | Minimize environmental impact | Maximize utilization, Graviton, managed services, data lifecycle |

## General design principles
- Stop guessing capacity (auto scale). Test at production scale. Automate. Allow evolutionary architectures. Drive decisions with data. Game days.
- **Design for failure:** "everything fails all the time" (Werner Vogels). Remove single points of failure.
- **Loose coupling** (queues, events) and **stateless** compute.

The **Well-Architected Tool** in the console runs reviews against these pillars, and there are *lenses* for serverless, SaaS, and so on.

## Lab task (CloudMart)
Open the **Well-Architected Tool** and create a workload "CloudMart". Answer the Reliability and Security questions honestly. Note the high-risk issues (e.g. a single NAT Gateway, no DR yet). You fix DR in this phase.

Q: Name the six pillars of the Well-Architected Framework.
A: Operational Excellence, Security, Reliability, Performance Efficiency, Cost Optimization, Sustainability.

Q: Which pillar covers Multi-AZ deployments and backups?
A: Reliability.

@@ 3 | TOPIC | Classic high-availability architecture patterns | Stateless web tier, decoupled workers, caching, serverless, event-driven | MUST
# Reference patterns the exam reuses

## 1. Highly available web application
Route 53 → CloudFront (+WAF) → **ALB across ≥2 AZs** → **ASG / ECS service across ≥2 AZs** (stateless; sessions in ElastiCache/DynamoDB) → **RDS Multi-AZ / Aurora** (+ read replicas) → S3 for static and media files.

## 2. Decoupled worker
Web tier → **SQS** → worker ASG scaled on **queue depth** (or Lambda). DLQ for failures. SNS/EventBridge for fan-out.

## 3. Serverless API
API Gateway → Lambda → DynamoDB (+DAX). Cognito for auth. S3 + CloudFront for the front-end. Step Functions for workflows.

## 4. Event-driven file processing
S3 upload → EventBridge/S3 event → Lambda (small) or Step Functions → Fargate/Batch (large) → results to S3/DynamoDB → SNS notification.

## 5. Real-time analytics
Producers → Kinesis Data Streams → Lambda/Flink (real-time) + Firehose → S3 → Glue → Athena/Redshift → QuickSight.

## 6. Hybrid
On-prem ↔ Site-to-Site VPN / Direct Connect ↔ Transit Gateway ↔ VPCs. Storage Gateway for files/backups. DataSync for migration.

## Anti-patterns → fixes
| Anti-pattern | Fix |
|---|---|
| Single EC2 instance with an Elastic IP | ALB + ASG across AZs |
| Sessions stored on the instance | ElastiCache / DynamoDB, a stateless app |
| App calls a slow downstream service synchronously | SQS / async processing |
| DB overloaded by reads | Read replicas / ElastiCache / DAX |
| Cron on one EC2 instance | EventBridge Scheduler + Lambda / ECS task |
| Access keys on instances | IAM roles |
| Manual console builds | CloudFormation/CDK |

## Lab task (CloudMart)
Map CloudMart onto patterns 1–5 (you have built all five!). Draw the final diagram in your README, with every service you used labeled.

Q: An application stores sessions in instance memory and loses them during scale-in. Fix?
A: Store sessions externally in ElastiCache (Redis) or DynamoDB and make the instances stateless.

Q: How should a worker tier processing SQS messages scale?
A: Based on the queue depth / backlog per instance (target tracking on a custom metric), or use Lambda with SQS.

@@ 2 | MODULE | Disaster Recovery | RPO/RTO and the four DR strategies | MUST
Surviving a Region-level disaster.

@@ 3 | TOPIC | DR strategies: backup/restore, pilot light, warm standby, multi-site | RPO vs RTO trade-offs and which AWS services implement each | MUST
# Disaster recovery strategies

- **RPO (Recovery Point Objective):** how much **data** you can lose, measured in time ("we can lose at most 1 hour of orders").
- **RTO (Recovery Time Objective):** how long you can be **down** ("back up within 15 minutes").

**Analogy:** RPO = how old your last saved game is. RTO = how long it takes to load it and play again.

## The four strategies (cheapest/slowest → most expensive/fastest)

| Strategy | What runs in the DR Region | RPO / RTO | AWS tools |
|---|---|---|---|
| **Backup & Restore** | Nothing, only backups | Hours / **hours** | AWS Backup cross-Region copy, S3 CRR, snapshots, AMIs copied, IaC to rebuild |
| **Pilot Light** | **Core data live** (DB replica), servers **off** or not created | Minutes / **tens of minutes** | RDS cross-Region read replica / Aurora Global DB, DynamoDB Global Tables, AMIs + IaC ready, scale up on disaster |
| **Warm Standby** | A **scaled-down but fully working** copy of the whole stack | Seconds–minutes / **minutes** | Same, plus a small ASG/ECS service running. On disaster: scale out + Route 53 failover |
| **Multi-Site Active/Active** | **Full production in 2+ Regions** serving traffic | Near zero / **near zero** | Route 53 latency/weighted routing, Aurora Global DB (write forwarding) / DynamoDB Global Tables, Global Accelerator |

**Analogy:** backup & restore = blueprints in a safe. Pilot light = the gas pilot flame is lit, so you can turn the heating up quickly. Warm standby = a small heater already running. Multi-site = two fully heated houses.

## Supporting services
- **AWS Elastic Disaster Recovery (DRS):** continuous block-level replication of servers (on-prem or EC2) into a low-cost staging area. Launch recovery instances in minutes (RPO seconds, RTO minutes). The "lift and shift" DR answer.
- **Route 53 health checks + failover routing**, or Global Accelerator, for traffic switching.
- **Aurora Global Database:** cross-Region RPO ~1 s, RTO < 1 minute.
- **AWS Backup** with cross-Region and cross-account copy and Vault Lock.

## Exam decision
Read the RPO/RTO numbers and the word "cost-effective":
- RTO in hours, lowest cost → **backup & restore**.
- RTO about 10–30 minutes, low cost → **pilot light**.
- RTO in minutes, business-critical → **warm standby**.
- Zero downtime, global users → **multi-site active/active**.

## Lab task (CloudMart)
Decide CloudMart's targets: **RPO 5 minutes, RTO 30 minutes** → **pilot light**. The next topic implements it.

Q: What is RPO?
A: The maximum acceptable amount of data loss, measured in time (how far back the last recoverable point is).

Q: Which DR strategy keeps only the core data replicated and starts compute on disaster?
A: Pilot light.

Q: Which DR strategy runs a scaled-down full copy of production in another Region?
A: Warm standby.

Q: Which service continuously replicates servers for low-cost DR with minutes of RTO?
A: AWS Elastic Disaster Recovery (DRS).

@@ 3 | TOPIC | Lab milestone: CloudMart pilot-light DR drill | Cross-Region data replication, IaC-ready compute, Route 53 failover, measure RPO/RTO | MUST
# Lab milestone: DR drill

## Prepare the DR Region (the pilot light)
1. **Database:** create a **cross-Region read replica** of CloudMart RDS in the DR Region (or, if you use Aurora, add a Global Database secondary). It must use a KMS key in that Region.
2. **Images:** S3 **CRR** to `cloudmart-images-dr-<id>` (set up in Phase 4).
3. **Carts:** convert `cloudmart-carts` to a **DynamoDB Global Table** with a replica in the DR Region (optional, cheap on-demand).
4. **Container image:** enable **ECR cross-Region replication** for `cloudmart-api`.
5. **Secrets:** **replicate the secret** `cloudmart/db` to the DR Region (Secrets Manager replication).
6. **Backups:** add a **cross-Region copy** rule to the AWS Backup plan `cloudmart-daily`.
7. **Compute:** *nothing running.* Your IaC stacks from Phase 10 (with Env=dr and Region parameters) are the "pilot light igniter".

## Run the drill (simulate a primary Region failure)
1. Start a stopwatch. Write down the latest order ID created in the primary.
2. **Promote** the DR read replica to a standalone DB (RDS → Actions → Promote).
3. Deploy the `cloudmart-network` and `cloudmart-app` stacks in the DR Region, pointing at the promoted DB, the DR bucket and the replicated secret.
4. Switch traffic: with Route 53, change the failover record (or the CloudFront origin) to the DR ALB.
5. Stop the stopwatch when `/lab` shows the DR Region's AZs. That's your **RTO**.
6. Check the last order ID in the DR database. The missing orders = your **RPO**.

## Write a post-drill report (Operational Excellence!)
Measured RPO/RTO vs the targets, what was manual, what to automate (e.g. a Step Functions/SSM Automation runbook for the failover), and the cost of the pilot light per month.

## Teardown
Delete the DR stacks and the promoted DB. Keep the cheap pieces (CRR bucket, ECR replica) only if you want them.

Q: In a pilot-light drill for RDS, what step makes the DR database writable?
A: Promoting the cross-Region read replica to a standalone primary instance.

@@ 2 | MODULE | Migration | The 7 Rs and AWS migration services | SHOULD
Moving existing workloads to AWS.

@@ 3 | TOPIC | Migration strategies and services | 7 Rs, Application Migration Service, DMS + SCT, Discovery, Migration Hub | SHOULD
# Migration to AWS

## The 7 Rs
| Strategy | Meaning | Example |
|---|---|---|
| **Retire** | Turn it off | An unused legacy app |
| **Retain** | Keep it on-prem for now | Mainframe with recent investment |
| **Rehost** ("lift and shift") | Move the VM as is to EC2 | **AWS Application Migration Service (MGN)** |
| **Relocate** | Move the hypervisor platform | VMware Cloud on AWS |
| **Replatform** ("lift, tinker and shift") | Small optimizations | Self-managed MySQL → **RDS**, Tomcat → Beanstalk |
| **Repurchase** | Move to SaaS | CRM → Salesforce |
| **Refactor / re-architect** | Redesign cloud-native | Monolith → microservices on Lambda/ECS |

## Services
- **AWS Application Migration Service (MGN):** continuous block replication of servers → automated **rehost** to EC2 with minimal downtime cutover. (It replaces the old Server Migration Service.)
- **AWS Database Migration Service (DMS):** migrate databases **with minimal downtime** (the source stays online). **Full load + CDC** (change data capture, ongoing replication). Homogeneous (Oracle → Oracle on RDS) or heterogeneous (Oracle → Aurora PostgreSQL).
- **Schema Conversion Tool (SCT) / DMS Schema Conversion:** converts schemas, stored procedures and code **between different engines** (needed for heterogeneous migrations).
- **AWS Application Discovery Service:** collects on-prem server inventory, performance and dependencies (agent-based or agentless through vCenter) to plan the migration.
- **AWS Migration Hub:** a single place to track migration progress across tools.
- **Migration Evaluator:** a business case and cost projection.
- Data: **DataSync, Snow Family, Transfer Family** (Phase 4).

## Exam patterns
- "Migrate Oracle to Aurora PostgreSQL with minimal downtime" → **SCT (schema) + DMS (data, CDC)**.
- "Lift and shift hundreds of VMs quickly" → **Application Migration Service**.
- "Understand server dependencies before migrating" → **Application Discovery Service**.

## Lab task (CloudMart)
Optional practice of DMS: create a DMS replication (serverless) from the docker-compose-style Postgres (or a small EC2 Postgres) into your RDS, using full load + CDC. Insert a row in the source and watch it appear in RDS. Otherwise, write the CloudMart "migration story": if CloudMart had been running on-prem on VMware with Oracle, which R and which service would you choose for each component?

Q: Which service migrates databases with minimal downtime using ongoing replication (CDC)?
A: AWS Database Migration Service (DMS).

Q: Which tool converts an Oracle schema to PostgreSQL for a heterogeneous migration?
A: AWS Schema Conversion Tool (or DMS Schema Conversion).

Q: Which service rehosts on-prem servers to EC2 with continuous replication?
A: AWS Application Migration Service (MGN).

@@ 2 | MODULE | Cost Optimization | Pricing levers, cost tools, data transfer costs | MUST
20% of the exam: cheapest design that still meets the requirements.

@@ 3 | TOPIC | Cost optimization strategies | Compute, storage, database and network cost levers | MUST
# Cost optimization levers (exam cheat sheet)

## Compute
- **Rightsize** (Compute Optimizer), then commit: **Savings Plans / RIs** for the baseline, **Spot** for fault-tolerant work, On-Demand for the rest.
- **Auto Scaling** and schedules (turn off dev at night: EventBridge Scheduler / Instance Scheduler).
- **Graviton** instances (better price/performance). **Serverless** for spiky or low traffic (Lambda, Fargate, Aurora Serverless v2, DynamoDB on-demand).

## Storage
- **S3 lifecycle** to IA/Glacier, **Intelligent-Tiering** for unknown patterns, delete incomplete multipart uploads and old versions.
- **EBS:** gp2 → **gp3** (about 20% cheaper), delete unattached volumes and old snapshots, archive snapshots.
- **EFS IA/Archive** lifecycle.

## Database
- Rightsize, use reserved instances for steady DBs, **Aurora Serverless v2** or **DynamoDB on-demand** for variable load, **caching** to use a smaller DB, stop dev DBs.

## Network / data transfer (often overlooked)
| Traffic | Cost |
|---|---|
| Inbound from the internet | Free |
| Same AZ, using private IPs | Free |
| **Cross-AZ** | Charged per GB each way |
| **Cross-Region** | Charged per GB |
| Out to the internet | Charged per GB (cheaper via **CloudFront**) |
| **NAT Gateway** processing | Per GB, on top of the transfer charge |

- Use **VPC gateway endpoints** for S3/DynamoDB (free, no NAT charges).
- Serve content via **CloudFront** (reduces origin egress and S3 GET requests).
- Keep chatty components in the same AZ only when HA isn't impacted. Read replicas in the same AZ save transfer costs.
- Direct Connect lowers egress rates for large hybrid traffic.

## Cost tools
| Tool | Use |
|---|---|
| **Cost Explorer** | Visualize and forecast, RI/SP recommendations |
| **AWS Budgets** | Alerts and actions on thresholds |
| **Cost and Usage Report / Data Exports** | Detailed line items → Athena/QuickSight |
| **Cost allocation tags** | Show cost per project/team |
| **Cost Anomaly Detection** | ML alerts on unusual spend |
| **Compute Optimizer / Trusted Advisor** | Rightsizing and idle resources |
| **Pricing Calculator** | Estimate before building |

## Lab task (CloudMart)
**Cost review:** open Cost Explorer, filter by the tag `Project=cloudmart` and group by service. Identify your top 3 cost drivers (probably NAT Gateway, ALB and RDS). Write 3 concrete changes: e.g. (1) remove NAT and add SSM interface endpoints, (2) Fargate Spot for one of the two tasks, (3) Aurora Serverless v2 or a stopped dev DB at night. Enable **Cost Anomaly Detection**.

Q: How can EC2 instances in private subnets access S3 without NAT Gateway data processing charges?
A: Use an S3 gateway VPC endpoint (free).

Q: What is the cheapest way to deliver static content to global users while reducing S3 egress?
A: Serve it through CloudFront.

Q: Is data transfer between AZs free?
A: No. Cross-AZ traffic is charged per GB in each direction (same-AZ traffic over private IPs is free).

@@ 3 | TOPIC | Final CloudMart review and teardown | Well-Architected self-review, cost cleanup, keep the IaC | MUST
# Final review and teardown

## Well-Architected self-review of CloudMart
| Pillar | What you built | Possible improvement |
|---|---|---|
| Operational Excellence | IaC stacks, dashboards, alarms, DR runbook | CodePipeline, automated failover runbook |
| Security | Roles only, KMS, Secrets Manager, WAF, GuardDuty, private subnets | Organizations + SCPs, Inspector findings fixed |
| Reliability | Multi-AZ ALB/ECS/RDS, SQS + DLQ, backups, pilot-light DR | NAT per AZ, warm standby for a lower RTO |
| Performance | CloudFront, ElastiCache, read replica, Lambda SnapStart | DAX for carts if hot, Graviton tasks |
| Cost | Serverless pieces, gateway endpoints, lifecycle rules, Spot option | Savings Plan if always on, delete idle ALB/NAT |
| Sustainability | Managed services, lifecycle, right-sized tasks | Graviton (ARM) images |

## Teardown checklist (do it when you finish labbing)
1. Delete the CloudFormation/CDK stacks (app → messaging → data → network).
2. Empty and delete buckets you don't need (images, web, datalake, trail, artifacts). Keep the trail bucket if you want the audit history.
3. Delete RDS snapshots you don't need, ECR images, CloudWatch log groups, the QuickSight subscription, any Redshift workgroup, OpenSearch domains, and the Global Accelerator.
4. Check **EC2 → Elastic IPs, Volumes, Snapshots, Load balancers, NAT gateways** in **every Region you used**.
5. Keep: IAM roles/policies (free), your budget, and your Git repo with `infra/`. You can rebuild CloudMart in about 30 minutes.

Q: After a lab, which commonly forgotten resources keep billing?
A: NAT Gateways, Elastic IPs/public IPv4, load balancers, EBS volumes and snapshots, RDS instances and snapshots, interface endpoints, and idle Redshift/OpenSearch clusters.
