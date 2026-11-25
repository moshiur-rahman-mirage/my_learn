@@ 1 | MODULE | Phase 9 — Security, Encryption & Governance | KMS, Secrets Manager, ACM, WAF, Shield, threat detection, Organizations | MUST
# Phase 9: Security, encryption and governance

"Design Secure Architectures" is **30% of the exam**. This phase covers encryption keys, secrets, edge protection, threat detection and multi-account governance.

**CloudMart goal this phase:** remove the DB password from user data and task definitions (→ **Secrets Manager** with rotation), encrypt S3/RDS/SQS with a customer managed **KMS key**, protect CloudFront/ALB with **WAF**, and turn on **GuardDuty**, **Security Hub** and **Config**.

@@ 2 | MODULE | Encryption and Secrets | KMS, CloudHSM, Secrets Manager, Parameter Store, ACM | MUST
Protecting data and credentials.

@@ 3 | TOPIC | AWS KMS fundamentals | Key types, envelope encryption, key policies, rotation, multi-Region keys | MUST
# AWS KMS (Key Management Service)

**KMS** creates and controls **encryption keys** used by almost every AWS service. The key material never leaves the KMS hardware security modules (FIPS 140-3 validated). Every use is **logged in CloudTrail**.

## Key types
| Type | Who manages | Cost | Notes |
|---|---|---|---|
| **AWS owned keys** | AWS, invisible to you | Free | e.g. the DynamoDB default |
| **AWS managed keys** (`aws/s3`, `aws/rds`) | AWS, visible in your account | Free storage (usage billed) | Rotated automatically every year. You can't change the policy |
| **Customer managed keys (CMK)** | **You** | $1/month + usage | Your own **key policy**, enable/disable, **optional automatic rotation** (yearly by default, configurable), grants, imported key material |

Symmetric (AES-256, the common case) or asymmetric (RSA/ECC for signing or for encryption outside AWS). There are also HMAC keys.

## Envelope encryption
KMS can encrypt at most **4 KB** directly. For bigger data:
1. Call `GenerateDataKey` → get a plaintext **data key** plus the same key encrypted under the KMS key.
2. Encrypt the data locally with the plaintext data key, then throw the plaintext key away.
3. Store the **encrypted data key alongside the data**.
4. To decrypt: `Decrypt` the data key with KMS, then decrypt the data locally.

**Analogy:** you lock the documents in a box with a cheap key (the data key), then lock that key inside a bank safe (KMS). Only people allowed to open the safe can open the box.

## Key policy (most important rule)
**Every KMS key has a key policy, and without it allowing access, IAM policies alone don't work.** The default key policy gives the account root (and therefore IAM) control. Cross-account use needs the key policy to allow the other account, *and* the other account's IAM policy to allow it.

## Other facts
- KMS keys are **Regional**. **Multi-Region keys** share the same key ID and material across Regions (encrypt in one Region, decrypt in another: global DynamoDB tables, DR).
- **Deleting a key** has a mandatory waiting period of 7–30 days. Disable it instead if unsure.
- **API quotas** (thousands of requests per second per Region) → throttling at high volume. Use S3 Bucket Keys or data key caching.
- Copying an encrypted EBS snapshot or AMI to another Region → **re-encrypt with a key in the destination Region**.

## Lab task (CloudMart)
Create a customer managed symmetric key **`alias/cloudmart`** with automatic rotation on. Key policy: admins = your admin role; key users = `cloudmart-ec2-role`, `cloudmart-task-role` and the Lambda roles. Then:
1. S3 images bucket default encryption → **SSE-KMS with `alias/cloudmart`** + **Bucket Key** on.
2. SQS `cloudmart-orders` → SSE-KMS with `alias/cloudmart` (producers need `kms:GenerateDataKey`, consumers need `kms:Decrypt`).
3. Upload a new image and open the CloudTrail event history: find the `GenerateDataKey` calls.

Q: What is envelope encryption?
A: Data is encrypted with a data key, and that data key is encrypted with a KMS key. The encrypted data key is stored with the data.

Q: Can IAM policies alone grant access to a KMS key if the key policy doesn't allow it?
A: No. The key policy must allow the account (or principal) for IAM policies to take effect.

Q: How do you use the same KMS key material for encryption in one Region and decryption in another?
A: Use KMS multi-Region keys.

@@ 3 | TOPIC | CloudHSM and encryption choices | Dedicated single-tenant HSM, customer-controlled keys | SHOULD
# AWS CloudHSM

**CloudHSM** gives you **dedicated, single-tenant hardware security modules** in your VPC. **You** manage the keys and users. AWS has **no access** to your keys.

| | KMS | CloudHSM |
|---|---|---|
| Tenancy | Multi-tenant (FIPS 140-3) | **Single-tenant, dedicated** (FIPS 140-3 Level 3) |
| Key control | AWS manages the HSMs, you control use through policies | **You fully control** the keys, AWS can't recover them |
| Integration | Native in most AWS services | Through PKCS#11, JCE, CNG. Can back a **KMS custom key store** |
| HA | Built in | You deploy a cluster across AZs |
| Use | Default choice | Strict compliance ("keys must be in single-tenant HSMs under our exclusive control"), SSL offload, Oracle TDE, PKI |

## Exam pattern
"Regulations require dedicated HSMs and that the cloud provider can't access the keys" → **CloudHSM** (possibly as a KMS custom key store so services can still use it).

## Lab task (CloudMart)
None (CloudHSM costs over $1/hour). Note in your README: "CloudMart uses KMS. CloudHSM only if a regulator demands dedicated HSMs."

Q: Which service provides single-tenant HSMs where AWS has no access to your keys?
A: AWS CloudHSM.

@@ 3 | TOPIC | Secrets Manager vs SSM Parameter Store | Storing credentials, automatic rotation, hierarchical config | MUST
# Secrets Manager vs Parameter Store

| | **AWS Secrets Manager** | **SSM Parameter Store** |
|---|---|---|
| Purpose | **Secrets**: DB passwords, API keys | **Configuration** and secrets (SecureString) |
| **Automatic rotation** | **Yes, built in** (native for RDS/Aurora/Redshift/DocumentDB, a Lambda for anything else) | No (build it yourself) |
| Encryption | Always, with KMS | SecureString uses KMS (String/StringList are plain) |
| Cost | Per secret per month + API calls | **Standard tier free** (advanced tier paid) |
| Extras | Cross-Region replication of secrets, resource policies, random password generation | Hierarchies (`/cloudmart/prod/db/url`), versioning, parameter policies (expiry notifications in the advanced tier), public parameters (e.g. the latest AMI ID) |

## Exam patterns
- "Store DB credentials and **rotate them automatically** every 30 days" → **Secrets Manager**.
- "Store configuration values and some secrets cheaply, no rotation needed" → **Parameter Store**.
- RDS can **manage the master password in Secrets Manager** for you (one checkbox).
- ECS task definitions and Lambda can reference secrets and parameters directly. They're injected as environment variables at startup.

## CloudMart integration
Turn on "Manage master credentials in AWS Secrets Manager" on the RDS instance (or create a secret `cloudmart/db`). Then:

```yaml
# application-aws.yml  (Spring Cloud AWS 3.x: spring-cloud-aws-starter-secrets-manager, -parameter-store)
spring:
  config:
    import:
      - aws-secretsmanager:cloudmart/db          # keys "username"/"password" become properties
      - aws-parameterstore:/cloudmart/prod/       # e.g. /cloudmart/prod/cloudmart.bucket
  datasource:
    username: ${username}
    password: ${password}
```
- At startup Spring calls Secrets Manager with the **instance/task role** and loads the secret into the environment. The password is never in code, user data or task definitions.
- On ECS you can alternatively map the secret in the task definition `secrets` block → env var `SPRING_DATASOURCE_PASSWORD` (the **execution role** needs `secretsmanager:GetSecretValue`).

## Lab task (CloudMart)
1. Move the DB password into Secrets Manager (managed by RDS), with rotation on.
2. Create parameters `/cloudmart/prod/cloudmart.bucket`, `.../cloudmart.ordersQueueUrl` and the others as Parameter Store Strings.
3. Remove all plain values from user data and the task definition. Grant `secretsmanager:GetSecretValue` + `kms:Decrypt` + `ssm:GetParametersByPath`.
4. Restart the service. CloudMart must still work. Trigger "Rotate secret immediately" and confirm the app reconnects. (Spring's Hikari pool may need a restart. That's a real-world caveat.)

Q: Which service rotates RDS database credentials automatically?
A: AWS Secrets Manager.

Q: Which is cheaper for storing many non-rotating configuration values?
A: SSM Parameter Store (standard parameters are free).

@@ 3 | TOPIC | AWS Certificate Manager (ACM) | Free public TLS certificates, auto-renewal, where they can be used | SHOULD
# AWS Certificate Manager (ACM)

**ACM** provisions, manages and **automatically renews** public and private **TLS certificates**.

- **Public certificates are free.** Validation by **DNS** (recommended: auto-renews while the CNAME exists) or email.
- They can be used on: **ELB (ALB/NLB), CloudFront, API Gateway**, App Runner, and others. You **can't download** ACM public certs for use on your own EC2 web server (use exportable certs or a third-party cert there).
- **CloudFront requires the certificate in `us-east-1`.** ALB/API Gateway Regional endpoints need it in their own Region.
- Imported certificates: ACM does **not** renew them. It can alert on expiry (through EventBridge/Config).
- **AWS Private CA:** issues private certificates for internal services (mTLS).

## Exam pattern
"Enable HTTPS on an ALB with the least operational overhead, and certificates must auto-renew" → **ACM public certificate** with DNS validation.

## Lab task (CloudMart)
If you have a domain: request `shop.<domain>` in **us-east-1** for CloudFront, and the same name in your Region for the ALB. Attach both, then set CloudFront's alternate domain name and the Route 53 alias. If not, skip it: the `*.cloudfront.net` default certificate is enough.

Q: In which Region must an ACM certificate be for CloudFront?
A: us-east-1.

Q: Does ACM renew imported certificates?
A: No. Only certificates issued by ACM are renewed automatically.

@@ 2 | MODULE | Edge Protection and Threat Detection | WAF, Shield, Firewall Manager, GuardDuty, Inspector, Macie, Security Hub, Detective | MUST
Blocking attacks and detecting threats.

@@ 3 | TOPIC | AWS WAF, Shield and Firewall Manager | Layer-7 rules, DDoS protection, central policy management | MUST
# WAF, Shield and Firewall Manager

## AWS WAF (Web Application Firewall)
Filters **HTTP(S) requests (layer 7)** on **CloudFront, ALB, API Gateway, AppSync, Cognito user pools, App Runner, Verified Access**. It does *not* attach to NLBs or EC2 directly.
- A **Web ACL** with rules: IP sets (allow/block), **geo match**, **rate-based rules** (e.g. block an IP above 1,000 requests per 5 minutes), size constraints, **SQL injection and XSS** match, regex, labels.
- **Managed rule groups:** AWS (Core rule set, Known bad inputs, SQLi, IP reputation, **Bot Control**, Account Takeover Prevention) and Marketplace vendors.
- Logs → CloudWatch Logs, S3 or Firehose.

## AWS Shield
| | **Shield Standard** | **Shield Advanced** |
|---|---|---|
| Cost | **Free, automatic** for everyone | About $3,000/month per organization + data |
| Protects | Layer 3/4 DDoS (SYN floods, UDP reflection) | Layer 3/4/7 on CloudFront, Route 53, Global Accelerator, ELB, EC2 EIPs |
| Extras | — | **24/7 Shield Response Team (SRT)**, **DDoS cost protection** (refund of scaling charges during an attack), advanced metrics, automatic layer-7 mitigation with WAF |

## AWS Firewall Manager
Centrally configures **WAF rules, Shield Advanced, security groups, Network Firewall and Route 53 Resolver DNS Firewall** across **all accounts in an AWS Organization**, and auto-applies them to new resources.

## DDoS-resilient architecture (exam)
CloudFront + Route 53 (edge, absorbs the attack) → WAF with rate-based rules → ALB → Auto Scaling, and Shield Advanced for critical apps.

## Exam patterns
- "Block SQL injection / XSS" → **WAF**.
- "Block a country" → WAF geo match (or CloudFront geo restriction).
- "Limit requests per IP" → WAF **rate-based rule**.
- "24/7 DDoS response team and cost protection" → **Shield Advanced**.
- "Enforce WAF rules across all accounts" → **Firewall Manager**.

## Lab task (CloudMart)
Create a Web ACL `cloudmart-waf` (scope **CloudFront**, so it's created in us-east-1) with: AWS managed **Core rule set**, **SQL database** rule set, **Known bad inputs**, and a **rate-based rule: 300 requests per 5 minutes per IP → block**. Associate it with the CloudFront distribution. Test it: `curl "https://<cf>/api/products?q=1' OR '1'='1"` should return **403**. A loop of 400 fast requests should start getting 403.

Q: Which service protects against SQL injection and cross-site scripting at layer 7?
A: AWS WAF.

Q: Which Shield tier offers a 24/7 response team and DDoS cost protection?
A: AWS Shield Advanced.

Q: Can AWS WAF be attached to a Network Load Balancer?
A: No. WAF supports CloudFront, ALB, API Gateway, AppSync, Cognito and App Runner.

@@ 3 | TOPIC | GuardDuty, Inspector, Macie, Security Hub and Detective | Threat detection, vulnerability scanning, PII discovery, aggregation, investigation | MUST
# Security detection services (don't mix them up)

| Service | One-liner | Data sources / scope |
|---|---|---|
| **Amazon GuardDuty** | **Intelligent threat detection** with ML: crypto-mining, compromised credentials, port scans, calls from malicious IPs | **CloudTrail events, VPC Flow Logs, DNS logs** (no setup needed), plus S3, EKS, RDS login, Lambda network and malware protection |
| **Amazon Inspector** | **Automated vulnerability scanning**: CVEs and network exposure | **EC2** (through the SSM agent), **ECR container images**, **Lambda** functions |
| **Amazon Macie** | Discovers **sensitive data (PII)** in **S3** using ML | S3 buckets: finds credit card numbers, passports, and so on |
| **AWS Security Hub** | **Aggregates findings** from GuardDuty, Inspector, Macie, Config, Firewall Manager and partners. **Compliance checks** (CIS, AWS Foundational Security Best Practices, PCI DSS) | Multi-account / Region |
| **Amazon Detective** | **Investigates** the root cause of findings with graphs of the related activity | Uses GuardDuty findings, CloudTrail, VPC Flow Logs, EKS logs |
| **AWS Config** (Phase 10) | Records resource **configuration** and evaluates compliance rules | All resource configurations |
| **IAM Access Analyzer** | Resources shared outside your trust zone | IAM / resource policies |

**Analogy:** GuardDuty is the burglar alarm, Inspector is the building inspector checking for weak locks, Macie is the auditor searching filing cabinets for sensitive documents, Security Hub is the security control room showing every screen, and Detective is the investigator reviewing the footage afterwards.

## Automated response pattern
GuardDuty finding → **EventBridge rule** → Lambda (e.g. isolate the instance by changing its security group, or disable access keys) and an SNS alert.

## Lab task (CloudMart)
1. Enable **GuardDuty** (30-day free trial). Generate **sample findings** (Settings → Generate sample findings) and read a few.
2. Enable **Inspector** for EC2/ECR/Lambda. Look at the vulnerabilities found in the `cloudmart-api` ECR image (the base JRE image CVEs).
3. Enable **Security Hub** with the AWS Foundational Security Best Practices standard. Fix 2–3 failed controls on CloudMart resources (e.g. S3 should block public access, RDS should be encrypted).
4. Optional: run a one-time **Macie** job on the images bucket (it will find nothing sensitive in images, which is the point).
5. Create an EventBridge rule for GuardDuty findings with severity ≥ 7 → SNS `cloudmart-notifications`.

Q: Which service analyzes CloudTrail, VPC Flow Logs and DNS logs to detect threats?
A: Amazon GuardDuty.

Q: Which service finds PII in S3 buckets?
A: Amazon Macie.

Q: Which service scans EC2 instances, ECR images and Lambda functions for software vulnerabilities?
A: Amazon Inspector.

Q: Which service aggregates security findings across accounts and runs compliance checks?
A: AWS Security Hub.

@@ 2 | MODULE | Multi-Account Governance | Organizations, SCPs, Control Tower, RAM | MUST
Managing many accounts securely.

@@ 3 | TOPIC | AWS Organizations and SCPs | OUs, consolidated billing, service control policies, tag/backup policies | MUST
# AWS Organizations

**Organizations** groups many AWS accounts under one **management account**, arranged in **Organizational Units (OUs)**.

## Benefits
- **Consolidated billing:** one bill, and **volume discounts and Savings Plans/RIs shared across accounts**.
- **Service Control Policies (SCPs):** guard rails on accounts and OUs.
- Policies for **tags** (enforce tag standards), **backups**, **AI opt-out**, and declarative policies.
- Account creation by API, and integration with Identity Center, CloudTrail organization trails, Config aggregators, GuardDuty/Security Hub delegated admin, and RAM sharing.

## SCPs (must know)
- They set the **maximum available permissions** for the member accounts' IAM users and roles, **including the member account's root user**.
- They **don't grant** anything. IAM policies must still allow the action.
- They **don't affect the management account**. (Keep workloads out of it.)
- They don't affect service-linked roles.
- Strategies: deny list (start with `FullAWSAccess`, add Deny statements) or allow list.

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "DenyOutsideApprovedRegions",
    "Effect": "Deny",
    "NotAction": ["iam:*", "organizations:*", "sts:*", "cloudfront:*", "route53:*", "support:*"],
    "Resource": "*",
    "Condition": { "StringNotEquals": { "aws:RequestedRegion": ["ap-south-1", "us-east-1"] } }
  }]
}
```
- This common SCP blocks every Regional service outside the approved Regions (data residency). Global services are excluded with `NotAction`, otherwise they'd break.
- Other classic SCPs: deny leaving the organization, deny disabling CloudTrail/GuardDuty, deny launching instances without a tag, deny the root user.

## Lab task (CloudMart)
Optional, but valuable if you're comfortable (it's free): create an Organization from your account (it becomes the management account), create OUs `Workloads` and `Sandbox`, create a new member account `cloudmart-dev` (a new email alias such as `you+cmdev@...`), and attach the Region-restriction SCP to `Sandbox`. In the member account, try launching an EC2 instance in a blocked Region and see the explicit deny.

Q: Do SCPs grant permissions?
A: No. They only set the maximum permissions. IAM policies must still allow the actions.

Q: Do SCPs apply to the member account's root user?
A: Yes. (They don't apply to the management account.)

Q: How can multiple accounts share Reserved Instance and Savings Plan discounts?
A: Through consolidated billing in AWS Organizations.

@@ 3 | TOPIC | AWS Control Tower and Resource Access Manager | Landing zones with guardrails; sharing resources across accounts | SHOULD
# Control Tower and RAM

## AWS Control Tower
Sets up and governs a secure **multi-account landing zone** automatically, using best practices.
- Creates the Organization with OUs, a **Log Archive** account and an **Audit** account, Identity Center, and centralized CloudTrail and Config.
- **Controls (guardrails):** *preventive* (SCPs, e.g. disallow public S3), *detective* (Config rules, e.g. detect unencrypted EBS) and *proactive* (CloudFormation hooks).
- **Account Factory:** standardized, pre-configured new accounts.
- A dashboard shows compliance across accounts.
- Exam: "Set up and govern a multi-account environment quickly with best practices" → **Control Tower**.

## AWS Resource Access Manager (RAM)
**Share resources with other accounts** in your Organization (or specific accounts) instead of duplicating them:
- **VPC subnets** (shared VPC: other accounts launch into a central network team's subnets)
- Transit Gateway, Route 53 Resolver rules, License Manager configurations, Aurora DB clusters (cross-account cloning), prefix lists, and more.

Exam: "Many accounts should use a centrally managed VPC/Transit Gateway" → **RAM**.

## Lab task (CloudMart)
If you made an Organization, use RAM to **share the `cloudmart-vpc` app subnets** with `cloudmart-dev`, and launch a micro instance there from the member account (it appears in your VPC). Delete it afterwards. Skip Control Tower in a personal account. Just remember its purpose.

Q: Which service quickly sets up a governed multi-account landing zone with guardrails?
A: AWS Control Tower.

Q: How can other accounts launch resources into a centrally owned VPC's subnets?
A: Share the subnets with AWS Resource Access Manager (a shared VPC).

@@ 3 | TOPIC | Lab milestone: secure CloudMart | No plaintext secrets, KMS everywhere, WAF, threat detection on | MUST
# Lab milestone: security hardening

## Security checklist (also a great exam revision list)
- [ ] **Identity:** root MFA, no root keys; the app uses roles only (`cloudmart-task-role`, Lambda roles); least-privilege `cloudmart-app-policy`, checked with IAM Access Analyzer's unused-access findings.
- [ ] **Secrets:** the DB password is in Secrets Manager (rotating); config in Parameter Store; nothing secret in user data, env files, the task definition or Git.
- [ ] **Encryption at rest:** S3 (SSE-KMS + Bucket Key), RDS (KMS), DynamoDB (KMS), SQS (KMS), EBS default encryption, ElastiCache at-rest encryption.
- [ ] **Encryption in transit:** CloudFront HTTPS only (redirect HTTP), ALB HTTPS (if you have a domain), `aws:SecureTransport` on S3, RDS `rds.force_ssl=1`, ElastiCache TLS.
- [ ] **Network:** private app and data subnets, SGs referencing SGs, ALB reachable only from the CloudFront prefix list, VPC Flow Logs on.
- [ ] **Edge:** WAF on CloudFront (managed rules + rate limit); Shield Standard (automatic).
- [ ] **Detection:** GuardDuty, Inspector, Security Hub; EventBridge → SNS for high-severity findings.
- [ ] **Audit:** CloudTrail (next phase: a trail to S3 with log file validation).

## Exercise: threat-model CloudMart
For each item, write which control stops it: (1) a leaked GitHub repo, (2) an SQL injection on `/api/products?q=`, (3) an attacker who steals a developer laptop, (4) a scraper sending 10,000 req/min, (5) a compromised container trying to read another bucket.
Answers: (1) no secrets in code + Secrets Manager, (2) WAF SQLi rules + parameterized JPA queries, (3) MFA + short-lived SSO credentials, (4) a WAF rate-based rule, (5) a least-privilege task role + bucket policy.

Q: Name three places where secrets must never be stored in an AWS workload.
A: Source code/Git, EC2 user data, and plain environment variables in task definitions or launch templates. Use Secrets Manager or Parameter Store SecureString instead.
