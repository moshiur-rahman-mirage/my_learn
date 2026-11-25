@@ 1 | MODULE | Phase 1 — Global Infrastructure & IAM | Regions/AZs, shared responsibility, identities, policies, roles, federation | MUST
# Phase 1: Global infrastructure and IAM

IAM shows up in **about a third** of exam questions, directly or indirectly. Almost every "secure" answer involves a **role** and a **least-privilege policy**.

**CloudMart goal this phase:** create the IAM roles and policies that CloudMart will use on AWS, and make sure no access key will ever live in the app.

@@ 2 | MODULE | Global Infrastructure | Where AWS runs and who is responsible for what | MUST
The physical map of AWS and the shared responsibility split.

@@ 3 | TOPIC | Regions, Availability Zones and Edge Locations | Region = geographic area, AZ = isolated data centers, Edge = CDN points | MUST
# Regions, AZs and edge locations

| Concept | What it is | Analogy |
|---|---|---|
| **Region** | A geographic area (e.g. `eu-west-1` Ireland) with 3+ AZs | A city |
| **Availability Zone (AZ)** | One or more data centers with independent power, cooling and network, a few km apart, linked by low-latency fiber | Separate buildings in the city, on different power grids |
| **Edge location / PoP** | 400+ points used by CloudFront, Route 53, Global Accelerator and Shield | Small kiosks in every neighbourhood |
| **Local Zone** | AWS compute/storage placed in a metro area close to users (single-digit ms) | A branch office in a suburb |
| **Wavelength Zone** | AWS compute inside 5G telecom networks | A kiosk inside the phone network |
| **Outposts** | AWS racks installed in *your* data center | AWS moves into your building |

## How to choose a Region (exam favourite)
1. **Compliance / data residency:** e.g. data must stay in the EU. This comes first.
2. **Latency** to your users.
3. **Service availability:** not every service is in every Region.
4. **Price** differs per Region.

## Scope of services
- **Global:** IAM, Route 53, CloudFront, WAF (for CloudFront), Organizations.
- **Regional:** most services (S3 buckets *live* in one Region even though the console lists them all, Lambda, DynamoDB, RDS).
- **AZ-scoped:** EC2 instances, EBS volumes, subnets.

## High availability rule
Something that runs in **one AZ** survives an instance failure but not an AZ outage. **Multi-AZ** survives an AZ outage. **Multi-Region** survives a Region outage (that's DR, Phase 12).

## Lab task (CloudMart)
In the console, look at the Region selector. Then run `aws ec2 describe-availability-zones --region <your-region>` and write the AZ names you will use (e.g. `ap-south-1a`, `ap-south-1b`) in your README. CloudMart will always span **two AZs**.

Q: What is the first factor when choosing a Region?
A: Compliance / data residency requirements. Then latency, service availability and price.

Q: Is an EBS volume Regional or AZ-scoped?
A: AZ-scoped. It can only attach to instances in the same AZ (use a snapshot to move it).

@@ 3 | TOPIC | Shared Responsibility Model | AWS secures the cloud, you secure what you put in it | MUST
# Shared responsibility model

- **AWS: security OF the cloud:** physical data centers, hardware, the network, the hypervisor, and the managed-service software.
- **You: security IN the cloud:** your data, IAM, OS patching on EC2, security groups, encryption choices and app code.

**Analogy:** renting an apartment. The landlord (AWS) maintains the building, locks the main entrance and fixes the plumbing. You lock your own door, choose who gets a key, and keep your valuables in a safe.

## It shifts with the service type

| Service | AWS manages | You manage |
|---|---|---|
| EC2 (IaaS) | Hardware, hypervisor | Guest OS patches, runtime, app, SGs, data |
| RDS (managed) | OS + DB engine patching, backups (automated) | DB users, SGs, encryption setting, parameters |
| Lambda / S3 / DynamoDB (serverless) | Almost everything below your code | Code, IAM permissions, data, encryption choices |

## Exam traps
- "Who patches the OS of an EC2 instance?" → **You** (AWS Systems Manager Patch Manager can help).
- "Who patches the RDS database engine?" → **AWS** (during the maintenance window you choose).
- Data encryption *configuration* is always the customer's responsibility. AWS provides the tools.

## Lab task (CloudMart)
In your README, write one line per CloudMart component (EC2 app, RDS, S3, Lambda) saying what *you* must secure. Revisit it at the end of the course.

Q: Who is responsible for patching the guest OS on EC2?
A: The customer.

Q: Who is responsible for physical security of data centers?
A: AWS.

@@ 2 | MODULE | IAM Fundamentals | Users, groups, roles, policies and how AWS evaluates them | MUST
Identity and Access Management: who can do what on which resource.

@@ 3 | TOPIC | IAM users, groups and roles | Long-term identities vs assumable roles with temporary credentials | MUST
# IAM users, groups and roles

| Identity | What it is | Credentials | Use for |
|---|---|---|---|
| **User** | A person or app with a fixed identity | Password and/or access keys (long-term) | Humans (prefer Identity Center instead), legacy apps outside AWS |
| **Group** | A collection of users. You attach policies to it | None | Give permissions to many users at once (e.g. `Developers`) |
| **Role** | An identity with permissions that *someone assumes* | **Temporary** credentials from STS | EC2/Lambda/ECS apps, cross-account access, federation |

**Analogy:** a user is an employee badge with your photo. A role is a **visitor badge from reception**: anyone allowed to pick it up gets those permissions for a limited time, then it expires.

## Key facts
- Groups **cannot contain other groups**, and a group is **not** an identity you can put in a resource policy's `Principal`.
- A user can be in up to 10 groups.
- A role has two policies: a **trust policy** (who may assume it) and **permission policies** (what it can do).
- An **instance profile** is the container that attaches a role to an EC2 instance.

## Trust policy example (EC2 may assume the role)

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": { "Service": "ec2.amazonaws.com" },
    "Action": "sts:AssumeRole"
  }]
}
```
- `Principal` names *who* may pick up the visitor badge: here, the EC2 service on behalf of your instances. For Lambda it would be `lambda.amazonaws.com`, and for ECS tasks `ecs-tasks.amazonaws.com`.

## Lab task (CloudMart)
1. Create a group `cloudmart-devs` with `ReadOnlyAccess`, and a test IAM user in it with console access. Log in as that user in a private browser window and confirm you can view but not create things.
2. Create the role **`cloudmart-ec2-role`** with the EC2 trust policy above, and attach `AmazonSSMManagedInstanceCore` (it lets you connect to instances without SSH in Phase 2). You add the S3/SQS permissions later.

Q: What are the two policy types attached to an IAM role?
A: A trust policy (who can assume the role) and permission policies (what the role can do).

Q: Can IAM groups be nested?
A: No. Groups contain only users.

@@ 3 | TOPIC | IAM policies and evaluation logic | JSON structure, explicit deny wins, least privilege, conditions | MUST
# IAM policies and how AWS decides

## Policy structure

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "CloudMartImagesRW",
    "Effect": "Allow",
    "Action": ["s3:GetObject", "s3:PutObject"],
    "Resource": "arn:aws:s3:::cloudmart-images-123456/*",
    "Condition": { "Bool": { "aws:SecureTransport": "true" } }
  }]
}
```
- **Effect** is Allow or Deny. **Action** is the API calls, e.g. `s3:PutObject`. **Resource** is the ARNs the statement applies to. **Condition** is extra checks: source IP, MFA present, tags, VPC endpoint, TLS.
- `/*` after the bucket ARN means *objects inside* the bucket. The bucket itself (`arn:aws:s3:::name`) is needed for `s3:ListBucket`. Mixing these up is a classic bug and a classic exam trick.

## Evaluation logic (memorize)
1. Everything starts as **implicit deny**.
2. If any applicable policy has an **explicit Deny** → **DENY**. Nothing can override it.
3. Otherwise, if there is an **Allow** (and no SCP, permissions boundary or session policy blocks it) → **ALLOW**.
4. Otherwise → implicit **DENY**.

**Analogy:** a nightclub. You need your name on the guest list (an Allow), and if you're on the banned list (an explicit Deny), the guest list doesn't matter.

## Policy types
| Type | Attached to | Purpose |
|---|---|---|
| Identity-based (managed / inline) | User, group, role | What this identity can do |
| Resource-based | Resource (S3 bucket, SQS queue, KMS key, Lambda) | Who can access this resource. Has a `Principal`. Enables cross-account access without assuming a role |
| Permissions boundary | User or role | The **maximum** permissions that identity policies can grant (delegation safety) |
| SCP (Organizations) | Account / OU | The maximum permissions for a whole account. Applies even to the account's root user |
| Session policy | Passed with AssumeRole | Narrows a single session further |

- **AWS managed** policies are maintained by AWS. **Customer managed** policies are reusable and versioned (the best practice for your own policies). **Inline** policies are embedded in one identity (strict 1:1, deleted with it).

## Useful condition keys
`aws:SourceIp`, `aws:MultiFactorAuthPresent`, `aws:RequestedRegion`, `aws:PrincipalTag/...`, `aws:ResourceTag/...` (ABAC), `aws:SourceVpce` (only via a VPC endpoint), `aws:PrincipalOrgID` (any account in my organization).

## Tools
- **IAM Policy Simulator:** test whether an action is allowed.
- **IAM Access Analyzer:** finds resources shared outside your account, validates policies, and can generate least-privilege policies from CloudTrail activity.
- **Access Advisor / last accessed:** shows unused permissions so you can remove them.

## Lab task (CloudMart)
Create the customer-managed policy **`cloudmart-app-policy`** with the S3 statement above. Use your future bucket name: `cloudmart-images-<account-id>`. Attach it to `cloudmart-ec2-role`. Test it with the Policy Simulator: `s3:PutObject` on that bucket should be allowed, and `s3:DeleteBucket` should be denied.

Q: An identity policy allows s3:* and an SCP denies s3:DeleteBucket. Can the user delete a bucket?
A: No. An explicit deny (or the absence of an allow in the SCP) always wins.

Q: What is a permissions boundary?
A: A managed policy that sets the maximum permissions an IAM user or role can have. The effective permissions are the intersection of the boundary and the identity policies.

Q: Which condition key restricts access to principals from your AWS Organization?
A: aws:PrincipalOrgID

@@ 3 | TOPIC | STS, AssumeRole and cross-account access | Temporary credentials, role chaining, external ID | MUST
# STS and cross-account roles

**AWS STS (Security Token Service)** issues **temporary credentials**: an access key, a secret and a session token, valid for 15 minutes to 12 hours. Roles work entirely through STS.

## Main STS APIs
| API | Used when |
|---|---|
| `AssumeRole` | A user or service in your account, or in **another account**, takes a role |
| `AssumeRoleWithWebIdentity` | A Google/Facebook/OIDC token is exchanged for AWS credentials (Cognito Identity Pools is the preferred way) |
| `AssumeRoleWithSAML` | Corporate SAML IdP (AD FS, Okta) → AWS |
| `GetSessionToken` | MFA-protected API access for an IAM user |
| `GetCallerIdentity` | "Who am I?" |

## Cross-account access pattern
Account A (dev) needs to read a bucket in Account B (prod):
1. In **B**, create role `ReadReports` with a trust policy `Principal: {"AWS": "arn:aws:iam::<A>:root"}` and S3 read permissions.
2. In **A**, allow the user or role `sts:AssumeRole` on that role's ARN.
3. A calls `AssumeRole` and gets temporary credentials for B.

```bash
aws sts assume-role --role-arn arn:aws:iam::222222222222:role/ReadReports \
    --role-session-name test
# returns AccessKeyId, SecretAccessKey, SessionToken, Expiration
```
- Both sides must agree: B trusts A in the trust policy, *and* A grants its own principal permission to assume. That's the "two keys" rule of cross-account access.

## Role vs resource-based policy for cross-account
- **Role:** the caller *gives up* its original permissions while using the role.
- **Resource-based policy** (S3 bucket policy, SQS, SNS, KMS, Lambda): the caller keeps its own permissions and also gets access. That's simpler when one service needs one resource.

## External ID
When you let a **third-party vendor** assume a role in your account, require an `sts:ExternalId` condition. This prevents the **confused deputy** problem, where another customer of the same vendor tricks the vendor into using *your* role.

## Lab task (CloudMart)
From your admin profile, assume `cloudmart-ec2-role`. First add your own account root as a second trusted principal, then remove it after the test. Use the returned credentials to run `aws s3 ls` and see what the app will be allowed to do.

Q: What does STS return?
A: Temporary security credentials: access key ID, secret access key, session token and expiration.

Q: How do you prevent the confused deputy problem with third-party role access?
A: Require an External ID condition in the role's trust policy.

@@ 3 | TOPIC | IAM Identity Center, federation and directory integration | SSO for many accounts, SAML/OIDC, AD | SHOULD
# Federation and IAM Identity Center

**Federation** = users log in with an identity that lives *outside* IAM (corporate AD, Okta, Google). They get a role, and you don't create IAM users for them.

| Option | Use when |
|---|---|
| **IAM Identity Center** (formerly AWS SSO) | Workforce users need access to **many accounts** in an Organization and to business apps. Built-in directory, or connects to AD / an external IdP (Okta, Entra ID) through SAML 2.0 + SCIM |
| **SAML 2.0 federation directly to IAM** | Legacy setup: corporate IdP → `AssumeRoleWithSAML` |
| **Amazon Cognito** | **Customers** of your app (web/mobile users), not employees. Covered in Phase 8 |
| **AWS Directory Service** | You need Microsoft AD in AWS. See below |

## Directory Service flavours
- **AWS Managed Microsoft AD:** a real AD in AWS. It can have a **trust** with your on-prem AD. Needed for FSx for Windows, RDS SQL Server Windows auth and AD-aware apps.
- **AD Connector:** a proxy that forwards authentication to your **on-prem AD**. Nothing is stored in AWS.
- **Simple AD:** a cheap Samba-based AD-compatible directory, without trusts.

## Identity Center concepts
- **Permission set:** a template of policies (e.g. `ReadOnly`, `Admin`) that is assigned to users or groups **per account**. Identity Center creates the roles inside each account for you.
- **ABAC:** use user attributes (department, cost center) as `aws:PrincipalTag` in policies.

## Exam keywords
- "Employees, multiple AWS accounts, single sign-on" → **IAM Identity Center**.
- "App users sign up with email or Google" → **Cognito**.
- "Use existing on-prem AD credentials, store nothing in the cloud" → **AD Connector**.

## Lab task (CloudMart)
If you used an IAM user in Phase 0, optionally enable Identity Center now, create a `ReadOnly` permission set, assign it to a new user, and log in through the access portal to see the role-based console.

Q: Employees need SSO into 30 AWS accounts using Okta. Which service?
A: AWS IAM Identity Center, federated with Okta through SAML 2.0 (with SCIM for user sync).

Q: What is the difference between AD Connector and AWS Managed Microsoft AD?
A: AD Connector proxies authentication to an on-prem AD and stores nothing in AWS. Managed Microsoft AD is a full AD running in AWS that can trust on-prem AD.

@@ 3 | TOPIC | IAM best practices and security tools | Least privilege, MFA, roles over keys, credential report | MUST
# IAM best practices (exam checklist)

1. **Lock down root:** MFA, no access keys, don't use it daily.
2. **Use roles for applications.** Never embed access keys in code, AMIs or env files.
3. **Least privilege:** start small, then widen based on real need. Use Access Analyzer's policy generation and last-accessed data.
4. **Groups (or Identity Center) for humans.** Don't attach policies to individual users.
5. **Require MFA** for privileged actions (condition `aws:MultiFactorAuthPresent`).
6. **Strong password policy** and **rotate** any remaining access keys.
7. **Use conditions:** source IP, VPC endpoint, `aws:SecureTransport`.
8. **Audit:** CloudTrail (who did what), the **IAM credential report** (all users and the status of their passwords, keys and MFA), Access Advisor, Access Analyzer.
9. **Multi-account with Organizations + SCPs** for strong isolation (Phase 9).

## Tools summary
| Tool | Scope | Answers |
|---|---|---|
| Credential report | Account | Which users have old keys or no MFA? |
| Access Advisor | User / role | Which services did this identity actually use? |
| Access Analyzer | Account / Org | Which resources are shared externally? Is this policy valid? Generate least privilege |
| Policy Simulator | Policy | Would this action be allowed? |

## Lab task (CloudMart)
Download the **credential report** (IAM → Credential report). Check that root has MFA and no keys, and that your test user has MFA. Delete the test IAM user from earlier if you no longer need it.

Q: Which IAM feature lists all users with the status of their passwords, access keys and MFA?
A: The IAM credential report.

Q: A developer hard-coded access keys in an app running on EC2. What is the fix?
A: Remove the keys, deactivate and rotate them, and attach an IAM role (instance profile) to the instance.
