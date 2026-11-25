@@ 1 | MODULE | Phase 13 — Exam Strategy & Final Review | Question tactics, keyword cheat sheet, scenario drills, exam-day checklist | MUST
# Phase 13: Exam preparation

You've learned the services and built CloudMart. Now train the **exam skill**: read a scenario, spot the requirement and the qualifier, and eliminate the wrong options quickly.

@@ 2 | MODULE | Exam Technique | How to read and answer scenario questions | MUST
Turn knowledge into points.

@@ 3 | TOPIC | How to answer SAA scenario questions | Find requirement + qualifier, eliminate, pick the most AWS-native option | MUST
# Answering technique

## The 4-step method
1. **Read the last sentence first.** It holds the question and the **qualifier**: *most cost-effective*, *least operational overhead*, *highest availability*, *most secure*, *minimal changes*, *lowest latency*.
2. **Underline the hard requirements** in the scenario: numbers (RPO 15 minutes, 10 TB, 300 msg/s), constraints (no internet, on-prem AD, must be encrypted, strict ordering), and the current setup.
3. **Eliminate** options that:
   - break a hard requirement (e.g. Spot for a database, FIFO limits exceeded, Lambda > 15 minutes, CNAME at the apex),
   - add needless custom work when a managed feature exists ("write a script on EC2 to…" is rarely right),
   - are technically wrong (gateway endpoint for SQS, cross-AZ EBS attach).
4. Between the last two: pick the one that best matches the **qualifier**.

## Qualifier → bias
| Qualifier | Lean towards |
|---|---|
| Least operational overhead | Managed/serverless: Lambda, Fargate, DynamoDB, Aurora Serverless, S3, managed features over scripts |
| Most cost-effective | Spot, Savings Plans, S3 IA/Glacier/Intelligent-Tiering, serverless for spiky load, gateway endpoints |
| Highly available / fault tolerant | Multi-AZ, ASG across AZs, ELB health checks, Aurora, DynamoDB, SQS |
| Disaster recovery in another Region | Cross-Region replicas, Aurora Global, DynamoDB Global Tables, AWS Backup copy, Route 53 failover |
| Most secure | Roles, KMS CMK, private subnets, VPC endpoints, least privilege, WAF |
| Minimal code changes | Lift and shift: Amazon MQ, DocumentDB, EKS, RDS same engine, MGN |
| Lowest latency globally | CloudFront, Global Accelerator, Aurora Global/DynamoDB Global Tables, edge |

## Time management
130 minutes for 65 questions: do a first pass in about 90 minutes and **flag** hard ones, then review the flags. Never leave a question blank. For multiple-response questions, the stem says how many to select.

## Lab task (CloudMart)
For the next 10 days: every day, write 3 "exam-style" questions about a CloudMart component you built (e.g. "CloudMart's worker processes orders twice. What should be changed?"), with answers. Writing questions trains the pattern recognition.

Q: In a scenario question, which part usually decides between two technically valid answers?
A: The qualifier (e.g. most cost-effective, least operational overhead, highest availability).

@@ 3 | TOPIC | Keyword-to-service cheat sheet | Fast mapping of scenario phrases to AWS services | MUST
# Keyword → service cheat sheet

## Compute
| Phrase | Answer |
|---|---|
| Interruptible, cheapest, batch | Spot Instances |
| Steady 24/7 for 1–3 years | Savings Plans / Reserved Instances |
| BYOL per socket/core | Dedicated Hosts |
| Event-driven, < 15 minutes, no servers | Lambda |
| Containers without managing servers | ECS/EKS on Fargate |
| Existing Kubernetes | EKS |
| Long batch jobs | AWS Batch |
| Deploy code, AWS provisions the infra | Elastic Beanstalk |
| Lowest latency between HPC nodes | Cluster placement group (+ EFA) |

## Storage
| Phrase | Answer |
|---|---|
| Shared file system for Linux across AZs | EFS |
| Windows SMB + AD | FSx for Windows |
| HPC parallel file system, S3-linked | FSx for Lustre |
| Unknown access pattern | S3 Intelligent-Tiering |
| Archive, 12–48 h retrieval, cheapest | Glacier Deep Archive |
| WORM / undeletable | S3 Object Lock (compliance), Backup Vault Lock |
| On-prem NFS/SMB backed by S3 | Storage Gateway – File |
| Replace tape | Tape Gateway |
| Petabytes, slow network | Snowball Edge |
| Ongoing online sync on-prem → AWS | DataSync |
| SFTP into S3 | Transfer Family |
| Temporary upload/download access | S3 presigned URL |
| Faster long-distance uploads | S3 Transfer Acceleration |

## Database
| Phrase | Answer |
|---|---|
| Automatic failover in another AZ | RDS Multi-AZ |
| Scale reads | Read replicas / Aurora replicas |
| Global DB, < 1 s replication, Region DR | Aurora Global Database |
| Unpredictable relational load, least ops | Aurora Serverless v2 |
| Key-value, ms latency at any scale | DynamoDB |
| Microsecond reads on DynamoDB | DAX |
| Multi-Region active-active NoSQL | DynamoDB Global Tables |
| Too many DB connections from Lambda | RDS Proxy |
| Cache, sessions, leaderboard | ElastiCache (Redis/Valkey) |
| MongoDB / Cassandra / graph / time-series | DocumentDB / Keyspaces / Neptune / Timestream |
| Data warehouse | Redshift |

## Networking
| Phrase | Answer |
|---|---|
| Private instances need outbound internet | NAT Gateway (one per AZ for HA) |
| Private access to S3/DynamoDB, free | Gateway endpoint |
| Private access to other AWS services | Interface endpoint (PrivateLink) |
| Expose a service to many VPCs privately | PrivateLink (NLB endpoint service) |
| Many VPCs + on-prem, transitive | Transit Gateway |
| Quick encrypted on-prem link | Site-to-Site VPN |
| Dedicated, consistent bandwidth | Direct Connect (+VPN for encryption/backup) |
| Block a specific IP range at the subnet | NACL deny |
| Static IPs, TCP/UDP global acceleration | Global Accelerator |
| Cache content globally | CloudFront |
| Point the apex domain at an ALB | Route 53 alias record |
| Lowest-latency Region routing | Route 53 latency policy |
| Country-based routing | Route 53 geolocation |
| Active-passive DNS failover | Route 53 failover + health check |

## Integration
| Phrase | Answer |
|---|---|
| Decouple, buffer, durable | SQS |
| Order + exactly-once | SQS FIFO |
| Fan-out to many consumers | SNS → SQS |
| SaaS events, schedules, content routing | EventBridge |
| Real-time stream, replay, many consumers | Kinesis Data Streams |
| Load streaming data into S3/Redshift | Data Firehose |
| JMS/AMQP/MQTT legacy broker | Amazon MQ |
| Orchestrate steps with retries/approvals | Step Functions |
| API keys and per-client throttling | API Gateway REST + usage plans |

## Security
| Phrase | Answer |
|---|---|
| App credentials on EC2/ECS/Lambda | IAM role (instance profile/task role/execution role) |
| Rotate DB credentials automatically | Secrets Manager |
| Audit key usage / own the key | KMS customer managed key |
| Dedicated HSM, AWS has no access | CloudHSM |
| SQLi/XSS/rate limiting | WAF |
| DDoS response team + cost protection | Shield Advanced |
| Threat detection from logs | GuardDuty |
| Vulnerability scanning (EC2/ECR/Lambda) | Inspector |
| PII in S3 | Macie |
| Aggregate findings + compliance | Security Hub |
| Who made this API call | CloudTrail |
| Configuration compliance/history | Config |
| Max permissions for accounts | SCPs (Organizations) |
| Multi-account landing zone | Control Tower |
| Workforce SSO to many accounts | IAM Identity Center |
| App user sign-up/sign-in | Cognito user pool |
| Temporary AWS creds for app users | Cognito identity pool |

Q: Which service do you choose when the scenario says "fan-out the same message to multiple queues"?
A: Amazon SNS with multiple SQS subscriptions.

Q: "Workforce SSO to many AWS accounts". Which service?
A: AWS IAM Identity Center.

@@ 3 | TOPIC | Commonly confused pairs | Side-by-side differences that the exam exploits | MUST
# Commonly confused pairs

| Pair | The difference |
|---|---|
| **Multi-AZ vs read replica** | HA with synchronous standby and automatic failover, vs async read scaling with manual promote |
| **SG vs NACL** | Stateful, allow-only, instance level, vs stateless, allow+deny, subnet level, ordered rules |
| **Gateway vs interface endpoint** | Route table, S3/DynamoDB only, free, vs ENI, most services, paid, reachable from on-prem |
| **CloudFront vs Global Accelerator** | Caching HTTP CDN, vs static anycast IPs for TCP/UDP with no caching |
| **SQS vs SNS vs EventBridge vs Kinesis** | Queue (1 consumer, pull) / topic (push fan-out) / event bus (rules, SaaS, schedules) / stream (ordered, replay, many readers) |
| **Kinesis Data Streams vs Firehose** | Real-time with custom consumers and replay, vs managed near-real-time delivery to S3/Redshift/OpenSearch |
| **EBS vs EFS vs instance store** | One-instance AZ block disk / multi-AZ shared NFS / ephemeral local disk |
| **Secrets Manager vs Parameter Store** | Rotation, paid, vs config, free tier, no native rotation |
| **KMS vs CloudHSM** | Multi-tenant managed keys, vs single-tenant HSM under your full control |
| **CloudTrail vs CloudWatch vs Config** | Who did what (API audit) / metrics, logs, alarms / resource configuration and compliance |
| **GuardDuty vs Inspector vs Macie** | Threats from logs / software vulnerabilities / PII in S3 |
| **WAF vs Shield vs Network Firewall** | Layer-7 HTTP rules / DDoS protection / VPC-level stateful network filtering |
| **ALB vs NLB** | HTTP layer 7, path/host routing, vs TCP/UDP layer 4, static IP, extreme performance |
| **Savings Plan vs Reserved Instance** | $/hour commitment, flexible (Compute SP covers Fargate/Lambda), vs a specific instance attribute commitment |
| **Cognito user pool vs identity pool** | Authentication with JWTs, vs AWS credentials for users |
| **Route 53 latency vs geolocation vs geoproximity** | Best network latency / the user's location (compliance) / distance with bias |
| **Aurora Global DB vs DynamoDB Global Tables** | Relational, single writer Region (+ write forwarding), vs NoSQL multi-active writes |
| **Storage Gateway vs DataSync** | Ongoing hybrid access with a local cache, vs a data transfer/migration job |
| **Backup & restore vs pilot light vs warm standby vs multi-site** | Increasing cost, decreasing RTO/RPO |
| **ECS task role vs task execution role** | App permissions, vs pulling images, writing logs and fetching secrets |
| **S3 presigned URL vs CloudFront signed URL** | Direct temporary S3 object access, vs access to content through the CDN with key groups |

Q: Task role vs task execution role in ECS?
A: The task role is used by the application code. The execution role is used by ECS to pull images, write logs and fetch injected secrets.

Q: Storage Gateway vs DataSync?
A: Storage Gateway gives ongoing hybrid access to cloud storage with local caching. DataSync moves or synchronizes data as transfer jobs.

@@ 2 | MODULE | Scenario Drills | Practice questions with explanations | MUST
Exam-style questions. Answer before reading the explanation.

@@ 3 | TOPIC | Scenario drill set 1: resilient and high-performing | 10 scenarios on HA, scaling, decoupling, caching, storage | MUST
# Drill set 1

**1.** A web app on a single EC2 instance with an Elastic IP must survive an AZ failure with minimal changes.
→ **AMI + launch template, an ASG across 2 AZs, behind an ALB; move state off the instance.** An EIP on one instance is a single point of failure.

**2.** An order system loses orders when the processing service is overwhelmed during flash sales.
→ **SQS queue between the front end and the processors**, processors scaled on queue depth, plus a DLQ.

**3.** A reporting dashboard runs heavy read queries that slow down the production RDS MySQL database.
→ **Create a read replica** and point the reporting at it. (Multi-AZ doesn't help reads.)

**4.** The same product pages are read millions of times, and database CPU is high.
→ **ElastiCache (Redis) with cache-aside**, plus CloudFront for static assets.

**5.** A DynamoDB-backed game leaderboard needs microsecond reads with minimal code changes.
→ **DAX.**

**6.** Messages must be processed exactly once and in order per customer, at 200 msg/s.
→ **SQS FIFO** with message group ID = customerId.

**7.** Hundreds of Linux instances across AZs need shared access to the same files.
→ **Amazon EFS.**

**8.** Users worldwide upload large videos to one S3 bucket in us-east-1 and complain about slow uploads.
→ **S3 Transfer Acceleration + multipart upload.**

**9.** A stateless app has a traffic spike every weekday at 9:00, and instances take 5 minutes to boot.
→ **Scheduled scaling (or predictive scaling)** before 9:00, plus warm pools or a golden AMI.

**10.** A Lambda function connecting to RDS fails at peak with "too many connections".
→ **RDS Proxy.**

## Lab task (CloudMart)
For each scenario, name the CloudMart lab where you built that exact fix (e.g. #2 = Phase 7 SQS order flow).

Q: An app on one EC2 instance must survive an AZ failure. What's the standard design?
A: An Auto Scaling group across at least two AZs behind an Application Load Balancer, with state stored outside the instances.

@@ 3 | TOPIC | Scenario drill set 2: secure and cost-optimized | 10 scenarios on IAM, encryption, network security, cost | MUST
# Drill set 2

**1.** An application on EC2 uses access keys in a config file to write to S3.
→ **Attach an IAM role (instance profile)** with a least-privilege policy, and remove and rotate the keys.

**2.** EC2 instances in a private subnet must read from S3 without internet access and without NAT costs.
→ **S3 gateway VPC endpoint**, plus optionally a bucket policy with `aws:SourceVpce`.

**3.** A company must audit every use of the key that encrypts sensitive S3 data, and rotate it yearly.
→ **SSE-KMS with a customer managed key**, automatic rotation, and CloudTrail.

**4.** Database credentials must be rotated every 30 days automatically.
→ **AWS Secrets Manager** with rotation.

**5.** A website is hit by SQL injection attempts and by bots flooding the login page.
→ **AWS WAF** on CloudFront/ALB with the SQLi managed rules + a rate-based rule (+ Bot Control).

**6.** Developers in member accounts must never be able to use Regions outside the EU, even as admins.
→ **SCP** in AWS Organizations denying other Regions (`aws:RequestedRegion`).

**7.** A batch job runs 4 hours nightly, can be restarted if interrupted, and must be cheapest.
→ **Spot Instances** (or AWS Batch on Spot).

**8.** Log files are read frequently for 30 days, rarely for a year, then kept for 7 years for compliance.
→ **S3 lifecycle:** Standard → Standard-IA (30 d) → Glacier Flexible/Deep Archive (365 d) → expire after 7 years. Object Lock if they must be immutable.

**9.** A steady workload runs 24/7 on a mix of EC2 instance families and Fargate, and will keep changing instance types.
→ **Compute Savings Plan.**

**10.** Large monthly NAT Gateway data charges come mostly from instances downloading from S3 and DynamoDB.
→ **Gateway endpoints for S3 and DynamoDB.**

## Lab task (CloudMart)
For #2, #3, #4 and #5, open the CloudMart resource where you implemented it and screenshot the setting into your notes.

Q: Private instances generate large NAT data charges accessing S3. Cheapest fix?
A: Add an S3 gateway VPC endpoint to the private route tables.

@@ 3 | TOPIC | Scenario drill set 3: hybrid, DR, migration and analytics | 10 scenarios on connectivity, DR, migration, data | MUST
# Drill set 3

**1.** On-prem needs a private, consistent 10 Gbps connection to AWS, encrypted, with a backup path.
→ **Direct Connect + IPsec VPN over DX (or MACsec), with a Site-to-Site VPN as backup.**

**2.** 40 VPCs across accounts plus 3 data centers need any-to-any connectivity with central control.
→ **Transit Gateway** (shared via RAM), with VPN/DX attachments.

**3.** RTO 4 hours, RPO 24 hours, minimize cost.
→ **Backup & restore** (AWS Backup cross-Region copies + IaC).

**4.** RTO 10 minutes for a critical app, and a scaled-down environment is acceptable during normal operation.
→ **Warm standby** in a second Region + Route 53 failover.

**5.** A global relational app needs sub-second cross-Region replication and fast Region failover.
→ **Aurora Global Database.**

**6.** Migrate an on-prem Oracle DB to Aurora PostgreSQL with minimal downtime.
→ **SCT for the schema + DMS full load and CDC.**

**7.** Lift and shift 200 VMware VMs quickly with minimal downtime.
→ **AWS Application Migration Service (MGN).**

**8.** Analysts want to run SQL on CSV logs in S3 occasionally, with no infrastructure.
→ **Athena** (convert to Parquet with Glue for cost and speed).

**9.** Clickstream data from millions of users must be ingested and stored in S3 with no administration.
→ **Amazon Data Firehose → S3.**

**10.** Analysts must see only certain columns of data lake tables in Athena and Redshift Spectrum.
→ **Lake Formation** fine-grained permissions.

## Lab task (CloudMart)
Re-read your DR drill report from Phase 12 and decide which DR strategy CloudMart would need if the RTO became 2 minutes. (Answer: warm standby with automated failover, or multi-site.)

Q: RTO of hours and lowest cost: which DR strategy?
A: Backup and restore.

Q: Which combination migrates Oracle to Aurora PostgreSQL with minimal downtime?
A: AWS SCT (schema conversion) plus AWS DMS (full load + change data capture).

@@ 3 | TOPIC | Final week checklist and exam day | Revision plan, flashcard review, booking and test-day tips | MUST
# Final week and exam day

## 7 days before
- [ ] Review **all flashcards** in this plan marked MUST (the learning tracker review queue).
- [ ] Re-read these topics: storage classes, Multi-AZ vs replicas, DR strategies, routing policies, purchasing options, SQS settings, VPC endpoints, KMS key policies, SCPs, Commonly confused pairs.
- [ ] Do the 3 drill sets again, timed: about 2 minutes per question.
- [ ] Redraw the CloudMart final architecture from memory and explain each choice out loud.

## Booking
- Book through the AWS Certification portal (Pearson VUE): a **test center** or **online proctored** exam.
- Online: a clean desk, a webcam, a quiet room, and a stable connection. Run the system test the day before. No notes, no second monitor.
- If English isn't your first language, you can request the **ESL +30 minutes** accommodation *before* booking (it applies once to your account).

## Exam day
- Sleep well. Read each question's last line first. Flag and move on after 2–3 minutes.
- Use every remaining minute to review the flagged questions. Don't change answers without a clear reason.
- The result appears within a few days (sometimes pass/fail immediately), along with a score report per domain.

## After passing
- Add the badge (Credly) to your profile.
- Keep CloudMart's `infra/` in your portfolio. It's proof of real hands-on skills (Spring Boot + React + 30 AWS services).
- Next steps: AWS Developer Associate (you already know a lot of it), then Solutions Architect Professional.

Q: How many minutes on average do you have per SAA-C03 question?
A: About 2 minutes (130 minutes for 65 questions).
