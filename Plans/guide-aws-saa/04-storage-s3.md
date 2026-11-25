@@ 1 | MODULE | Phase 4 — Amazon S3 & Storage Services | S3 deep dive, security, performance, hybrid storage, data transfer, backup | MUST
# Phase 4: S3 and storage services

S3 is the **single most tested service** on the exam: storage classes, lifecycle, security, encryption, replication and performance.

**CloudMart goal this phase:** product images move from the local folder to **S3**, with browser uploads through **presigned URLs**. The React build is hosted from S3. You add versioning, lifecycle and event notifications.

@@ 2 | MODULE | S3 Fundamentals | Buckets, objects, storage classes, lifecycle, versioning, replication | MUST
Core S3 concepts.

@@ 3 | TOPIC | S3 buckets, objects and consistency | Global names, keys, 5 TB objects, strong consistency, durability | MUST
# S3 basics

**Amazon S3 (Simple Storage Service)** is object storage: you store **objects** (files plus metadata) in **buckets**.

**Analogy:** an infinite valet parking garage. You hand over a car (the object), get a ticket (the key), and you never see where it's parked.

## Key facts
- **Bucket names are globally unique** (across all accounts), but each bucket **lives in one Region**.
- An object is identified by its **key**, the full "path" `products/42/main.jpg`. There are no real folders: the `/` is just part of the key, and the console shows prefixes as folders.
- Max object size **5 TB**. A single PUT can upload up to 5 GB, but use **multipart upload** above 100 MB (it's required above 5 GB).
- **Durability: 11 nines** (99.999999999%). Availability depends on the storage class (99.99% for Standard).
- **Strong read-after-write consistency** for all PUTs, overwrites and deletes, and LIST operations.
- Objects carry metadata (system and user `x-amz-meta-*`), **tags** (up to 10, usable in IAM and lifecycle rules) and an optional version ID.

## Access
- Every bucket is **private by default**. **Block Public Access** is on by default.
- Access with IAM policies, bucket policies or presigned URLs (next topics).

## Lab task (CloudMart)
Create bucket **`cloudmart-images-<account-id>`** in your Region. Keep Block Public Access **ON**. Upload a test image with the CLI and download it:

```bash
aws s3 cp sample.jpg s3://cloudmart-images-123456789012/products/test.jpg
aws s3 ls s3://cloudmart-images-123456789012/products/
```
- `s3 cp` and `s3 ls` are high-level commands. `aws s3api ...` gives raw API access (e.g. `put-bucket-versioning`).

Q: What consistency model does S3 provide?
A: Strong read-after-write consistency for all object operations, including overwrites, deletes and lists.

Q: Maximum S3 object size, and when must you use multipart upload?
A: 5 TB. Multipart is required above 5 GB and recommended above 100 MB.

@@ 3 | TOPIC | S3 storage classes | Standard, Intelligent-Tiering, IA, One Zone-IA, Glacier tiers, Express One Zone | MUST
# S3 storage classes (memorize this table)

| Class | AZs | Min duration | Retrieval | Use case |
|---|---|---|---|---|
| **S3 Standard** | ≥3 | — | ms, no fee | Frequently accessed data (CloudMart product images) |
| **S3 Intelligent-Tiering** | ≥3 | — | ms (archive tiers optional) | **Unknown or changing access patterns.** Moves objects between tiers automatically. Small monitoring fee, no retrieval fees |
| **S3 Standard-IA** | ≥3 | 30 days | ms, per-GB fee | Infrequent but needs fast access (backups, older images) |
| **S3 One Zone-IA** | **1** | 30 days | ms, per-GB fee | Re-creatable infrequent data (thumbnails, secondary backup copies). Lost if the AZ is destroyed |
| **S3 Glacier Instant Retrieval** | ≥3 | 90 days | **ms** | Archive accessed about once a quarter, but needs instant access (medical images) |
| **S3 Glacier Flexible Retrieval** | ≥3 | 90 days | Expedited 1–5 min, Standard 3–5 h, Bulk 5–12 h (bulk free) | Archives, retrieval within hours is OK |
| **S3 Glacier Deep Archive** | ≥3 | 180 days | Standard 12 h, Bulk 48 h | **Cheapest.** Compliance archives kept 7–10 years |
| **S3 Express One Zone** | 1 (directory bucket) | — | **single-digit ms**, highest RPS | Latency-critical analytics/ML in the same AZ as compute |

## Decision rules (exam)
- Access pattern unknown or unpredictable → **Intelligent-Tiering**.
- Infrequent, must be immediate, must survive an AZ loss → **Standard-IA**.
- Infrequent and re-creatable → **One Zone-IA**.
- Archive with millisecond access → **Glacier Instant Retrieval**.
- Archive, hours are OK → **Glacier Flexible**. Cheapest, 12–48 h is OK → **Deep Archive**.
- Watch the **minimum storage duration**: deleting a Deep Archive object after 10 days still bills 180 days.

## Lab task (CloudMart)
Upload one object with `--storage-class STANDARD_IA` and look at its class in the console. In the lifecycle topic you automate class changes for old product images.

Q: Which storage class fits data with unknown or changing access patterns?
A: S3 Intelligent-Tiering.

Q: Cheapest storage for compliance archives retrieved rarely with 12–48 hour retrieval acceptable?
A: S3 Glacier Deep Archive.

Q: Which class gives millisecond access to archive data accessed about once a quarter?
A: S3 Glacier Instant Retrieval.

@@ 3 | TOPIC | Lifecycle rules, versioning and Object Lock | Transitions, expiration, MFA delete, WORM compliance | MUST
# Lifecycle, versioning and Object Lock

## Versioning
- Bucket-level. Each overwrite creates a new **version**. A delete adds a **delete marker**, and the old versions stay (you can recover them).
- States: Unversioned → Enabled → Suspended (it can never return to unversioned).
- Protects against accidental overwrite and delete. It's **required for replication**.
- **MFA Delete:** requires MFA to permanently delete versions or change versioning. Only the root user can enable it, using the CLI.

## Lifecycle rules
Act automatically on objects by prefix or tag:
- **Transition actions:** e.g. Standard → Standard-IA after 30 days → Glacier Flexible after 90 → Deep Archive after 365.
- **Expiration actions:** delete objects after N days. Delete **noncurrent versions** after N days. Clean up **incomplete multipart uploads** (a hidden cost!) and expired delete markers.
- Constraint: an object must be in Standard for ≥30 days before moving to Standard-IA or One Zone-IA.

**Storage Class Analysis** watches access patterns and suggests when to transition (Standard → Standard-IA).

## Object Lock (WORM = write once, read many)
Requires versioning. It prevents deletion or overwrite of a version for a retention period.
- **Compliance mode:** **nobody** can delete it or shorten the retention, *including root*. Used for regulations (SEC 17a-4).
- **Governance mode:** users with the special permission `s3:BypassGovernanceRetention` can override it.
- **Legal hold:** an indefinite lock until removed, independent of the retention period.
- **Glacier Vault Lock** = the same WORM idea for Glacier vaults (legacy), locked by policy.

## Lab task (CloudMart)
1. Enable **versioning** on `cloudmart-images-*`. Upload `test.jpg` twice, then delete it. Use "Show versions" to see the delete marker, and restore it by deleting the marker.
2. Add a lifecycle rule on prefix `products/`: transition to **Standard-IA after 30 days**, delete **noncurrent versions after 30 days**, and **abort incomplete multipart uploads after 7 days**.

Q: How do you make S3 objects undeletable even by the root user for 7 years?
A: S3 Object Lock in compliance mode with a 7-year retention period (versioning required).

Q: What does a DELETE do in a versioned bucket?
A: It adds a delete marker. Previous versions remain and can be restored.

Q: Which lifecycle action removes hidden costs from failed large uploads?
A: AbortIncompleteMultipartUpload.

@@ 3 | TOPIC | S3 Replication (CRR and SRR) | Cross-Region and same-Region replication, RTC, batch replication | SHOULD
# S3 replication

Copies objects **asynchronously** from a source bucket to a destination bucket.
- **CRR (Cross-Region Replication):** compliance, lower latency for distant users, DR, replication to another account.
- **SRR (Same-Region Replication):** aggregate logs, copy prod data to a test account, keep a copy under a different owner.

## Rules to remember
- **Versioning must be enabled on both buckets.**
- Needs an IAM role that S3 assumes to replicate.
- **Only new objects** are replicated after the rule is enabled. Use **S3 Batch Replication** for existing objects and for objects that previously failed.
- Delete markers can optionally be replicated. **Deletes of specific versions are never replicated** (protection against malicious deletes).
- There's no chaining: A→B→C does not copy A's objects to C.
- **S3 Replication Time Control (RTC):** 99.99% of objects replicated within **15 minutes**, with an SLA. It's for compliance.
- You can change the storage class or the owner in the destination (e.g. replicate into Glacier Deep Archive as a cheap DR copy).

## Lab task (CloudMart)
Optional (small cost): create `cloudmart-images-dr-<id>` in a **second Region** with versioning on, then add a CRR rule from the images bucket with destination storage class `STANDARD_IA`. Upload an image and see it appear in the DR bucket. You'll reuse this bucket in the DR phase.

Q: What must be enabled on both buckets for S3 replication?
A: Versioning.

Q: Are existing objects replicated when you create a replication rule?
A: No, only new objects. Use S3 Batch Replication for existing ones.

@@ 2 | MODULE | S3 Security | Bucket policies, Block Public Access, encryption, presigned URLs, CORS | MUST
Who can access the objects, and how they're protected.

@@ 3 | TOPIC | Bucket policies, Block Public Access and Object Ownership | Resource policies, public access settings, ACLs disabled | MUST
# S3 access control

## Ways to control access
1. **IAM identity policies:** what a user or role may do (Phase 1).
2. **Bucket policy:** a resource policy *on the bucket* with a `Principal`. It can grant **cross-account** access, force TLS, force encryption, or restrict to a VPC endpoint or an organization.
3. **Block Public Access (BPA):** account-level and bucket-level guard rails that override any policy or ACL that would make data public. Keep it on unless you truly host public content (and even then, prefer CloudFront + OAC).
4. **ACLs:** legacy. **Object Ownership = "Bucket owner enforced"** (the default for new buckets) disables ACLs entirely. Exam answer: disable ACLs and use policies.

An IAM principal can access an object if **(IAM allows OR the bucket policy allows) AND nothing explicitly denies it**. Cross-account access needs **both** sides to allow.

## Bucket policy examples

```json
{
  "Version": "2012-10-17",
  "Statement": [
    { "Sid": "DenyInsecureTransport", "Effect": "Deny", "Principal": "*",
      "Action": "s3:*",
      "Resource": ["arn:aws:s3:::cloudmart-images-123456789012",
                   "arn:aws:s3:::cloudmart-images-123456789012/*"],
      "Condition": { "Bool": { "aws:SecureTransport": "false" } } },
    { "Sid": "OnlyFromOurVpcEndpoint", "Effect": "Deny", "Principal": "*",
      "Action": "s3:*",
      "Resource": "arn:aws:s3:::cloudmart-images-123456789012/*",
      "Condition": { "StringNotEquals": { "aws:SourceVpce": "vpce-0abc123" } } }
  ]
}
```
- The first statement rejects any plain-HTTP request: "force encryption in transit".
- The second only allows access through a specific VPC endpoint. Add it **only after Phase 6**, or it locks out your laptop too. It's shown here so you can recognize the pattern.

## Other access features
- **S3 Access Points:** named endpoints, each with its own policy (e.g. `analytics-ap` gets read-only access to `logs/`). This simplifies huge bucket policies. They can be restricted to a VPC.
- **Multi-Region Access Points:** one global endpoint that routes to the nearest replicated bucket.
- **S3 Object Lambda:** transform data on GET (e.g. redact PII, resize an image) with a Lambda function.
- **Requester Pays:** the requester pays the transfer costs, not the bucket owner.

## Lab task (CloudMart)
Add the **DenyInsecureTransport** statement to the images bucket policy. Confirm Object Ownership is "Bucket owner enforced". Check `cloudmart-app-policy` grants `s3:PutObject`/`s3:GetObject` on `.../*`, and add `s3:ListBucket` on the bucket ARN.

Q: How do you force all S3 requests to use HTTPS?
A: Add a bucket policy that denies requests where aws:SecureTransport is false.

Q: What setting overrides any policy that would make bucket data public?
A: S3 Block Public Access (account or bucket level).

Q: What is the recommended Object Ownership setting?
A: Bucket owner enforced. ACLs are disabled and access is controlled only by policies.

@@ 3 | TOPIC | S3 encryption | SSE-S3, SSE-KMS, DSSE-KMS, SSE-C, client-side, bucket keys | MUST
# S3 encryption

## At rest
| Option | Who manages keys | Notes |
|---|---|---|
| **SSE-S3** | S3 (AES-256) | **The default for all new objects.** No cost, no setup |
| **SSE-KMS** | KMS key (AWS managed `aws/s3` or **your customer managed key**) | **Audit trail in CloudTrail**, key policies, control over who can decrypt. KMS request quotas apply |
| **DSSE-KMS** | KMS, two layers of encryption | For compliance that requires dual-layer encryption |
| **SSE-C** | **You send the key** with each request (HTTPS required). AWS doesn't store it | You fully control the keys outside AWS |
| **Client-side** | You encrypt before uploading | S3 never sees plaintext |

- **S3 Bucket Keys:** cut KMS API calls (and cost) by up to 99% for SSE-KMS, by using a bucket-level data key.
- **Default encryption** setting per bucket, plus a bucket policy can **deny PutObject without the required encryption header** (e.g. require SSE-KMS).
- To read an SSE-KMS object, the caller needs **s3:GetObject *and* kms:Decrypt** on the key. That's a common "access denied" cause.

## In transit
HTTPS (TLS). Enforce it with `aws:SecureTransport` (previous topic).

## Exam patterns
- "Must audit who used the encryption key" or "must control and rotate our own key" → **SSE-KMS with a customer managed key**.
- "Keys must be managed by the company outside AWS" → **SSE-C** or client-side.
- "High-volume SSE-KMS hitting KMS throttling" → enable **S3 Bucket Keys**.

## Lab task (CloudMart)
For now set the bucket default to **SSE-S3**. In Phase 9 (KMS) you switch it to a customer managed key `cloudmart-key` with Bucket Keys, and grant the app role `kms:Decrypt`/`kms:GenerateDataKey`.

Q: Which S3 encryption option provides an audit trail of key usage?
A: SSE-KMS. Key usage is logged in CloudTrail.

Q: An app can read a bucket with s3:GetObject but gets AccessDenied on SSE-KMS objects. Why?
A: The role lacks kms:Decrypt permission on the KMS key (or the key policy doesn't allow it).

@@ 3 | TOPIC | Presigned URLs and CORS | Time-limited access without AWS credentials; browser cross-origin rules | MUST
# Presigned URLs and CORS

## Presigned URL
A URL signed with the credentials of someone who *has* permission. It grants **temporary GET or PUT access to one object** to anyone holding the URL, until it expires (up to 7 days with IAM user credentials, and no longer than the role session when signed by a role).

**Analogy:** a valet ticket you hand to a friend, valid only until midnight.

Use cases: let browsers **upload directly to S3** (no file bytes pass through your server), and give paying users temporary download links.

## CloudMart S3 adapter (Spring Boot, AWS SDK v2)

```java
@Service
@ConditionalOnProperty(name = "cloudmart.storage", havingValue = "s3")
class S3StorageService implements StorageService {
    private final S3Client s3 = S3Client.create();          // credentials from the default chain
    private final S3Presigner presigner = S3Presigner.create();
    @Value("${cloudmart.bucket}") String bucket;

    public String save(String key, InputStream in, String type) {   // server-side upload (fallback)
        try {
            byte[] bytes = in.readAllBytes();
            s3.putObject(b -> b.bucket(bucket).key(key).contentType(type), RequestBody.fromBytes(bytes));
            return key;
        } catch (IOException e) { throw new UncheckedIOException(e); }
    }

    public String publicUrl(String key) {                  // short-lived download link
        return presigner.presignGetObject(r -> r.signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(g -> g.bucket(bucket).key(key))).url().toString();
    }

    public String uploadUrl(String key, String type) {     // browser PUTs the file directly here
        return presigner.presignPutObject(r -> r.signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(p -> p.bucket(bucket).key(key).contentType(type))).url().toString();
    }
}
```
- `S3Client.create()` uses the default credential chain: your `cloudmart` profile locally, the instance role on EC2. There are no keys in code.
- `uploadUrl` is the scalable pattern. The API only signs, and the browser sends the bytes straight to S3, so the app servers never carry the upload traffic.

```js
// React admin page: 1) ask the API for a URL, 2) PUT the file straight to S3
const { key, url } = await api(`/api/products/upload-url?type=${file.type}`, { method: "POST" });
await fetch(url, { method: "PUT", body: file, headers: { "Content-Type": file.type } });
await api("/api/products", { method: "POST", body: JSON.stringify({ name, price, imageKey: key }) });
```
- The `Content-Type` must match the one used when signing, or S3 rejects the signature.

## CORS (Cross-Origin Resource Sharing)
A browser blocks JavaScript from calling another origin (domain/port) unless that origin allows it. Your React app (`localhost:5173` or the CloudFront domain) PUTting to `bucket.s3.amazonaws.com` is cross-origin, so the bucket needs a CORS rule:

```json
[{ "AllowedOrigins": ["http://localhost:5173", "https://*.cloudfront.net"],
   "AllowedMethods": ["GET", "PUT"], "AllowedHeaders": ["*"], "MaxAgeSeconds": 3000 }]
```
- Exam trap: "a website on bucket A loads fonts or images from bucket B and gets blocked" → **enable CORS on bucket B** (the one being called).

## Lab task (CloudMart)
Add the `software.amazon.awssdk:s3` dependency, implement `S3StorageService` and an endpoint `POST /api/products/upload-url`, set `cloudmart.storage=s3` and `cloudmart.bucket=...`, add the CORS rule, and change the admin page to upload directly. Create a product and confirm the image object appears in S3 and loads through the presigned GET URL.

Q: How can users upload large files to S3 without passing through your application servers and without AWS credentials?
A: The app generates presigned PUT URLs, and the client uploads directly to S3.

Q: Website on bucket A requests resources from bucket B and the browser blocks it. Fix?
A: Configure a CORS rule on bucket B allowing bucket A's origin.

@@ 2 | MODULE | S3 Features & Performance | Static hosting, events, performance, analytics | MUST
Using S3 as a website host, an event source and a fast data store.

@@ 3 | TOPIC | S3 static website hosting | Host the React build, index/error documents, public vs CloudFront | MUST
# Static website hosting

S3 can serve a **static website** (HTML/CSS/JS, no server-side code) from a website endpoint:
`http://<bucket>.s3-website-<region>.amazonaws.com` (or `s3-website.<region>`, depending on the Region).

## Requirements
- Enable "Static website hosting", and set the index document (`index.html`) and error document.
- The objects must be **publicly readable**: turn off BPA for that bucket and add a bucket policy allowing `s3:GetObject` to `"Principal": "*"`. **A 403 error** means the policy or BPA is wrong.
- The website endpoint is **HTTP only**. For HTTPS and a custom domain → put **CloudFront** in front (Phase 6). That's the production-grade answer: a *private* bucket + **Origin Access Control**.

## SPA routing trick
React Router paths like `/cart` don't exist as files. Set the **error document to `index.html`**, so S3 serves the app and React handles the route. (With CloudFront you map 403/404 → `/index.html` with status 200.)

## Lab task (CloudMart)
1. Create bucket `cloudmart-web-<id>`, enable static hosting (index and error document = `index.html`), turn off BPA *for this bucket only*, and add a public-read bucket policy.
2. Build with `VITE_API_BASE=http://<alb-dns>` (if your ALB from Phase 3 is up) and run `aws s3 sync frontend/dist s3://cloudmart-web-<id> --delete`.
3. Open the website endpoint. CloudMart's UI now runs from S3 with no server.
4. In Phase 6 you make this bucket **private again** and serve it through CloudFront + OAC.

Q: Your S3 static website returns 403 Forbidden. Most likely cause?
A: Block Public Access is on, or the bucket policy doesn't allow public s3:GetObject.

Q: How do you serve an S3 static website over HTTPS with a custom domain?
A: Put CloudFront in front (with an ACM certificate) and point Route 53 at the distribution.

@@ 3 | TOPIC | S3 event notifications | Trigger Lambda, SQS, SNS or EventBridge on object events | MUST
# S3 event notifications

S3 can emit an event when something happens to objects (`s3:ObjectCreated:*`, `ObjectRemoved`, `ObjectRestore`, replication events), filtered by prefix or suffix (e.g. `products/` and `.jpg`).

## Destinations
- **SQS**, **SNS**, **Lambda**: direct notifications (the destination's resource policy must allow S3 to send).
- **Amazon EventBridge**: enable it once on the bucket and *all* events go to EventBridge. There you get advanced filtering (metadata, size), **many targets** (18+ services), archive/replay, and cross-account delivery.

Typical use: an image is uploaded → **Lambda creates a thumbnail**, or a file lands → start a processing pipeline (Step Functions, Glue).

## Notes
- Delivery is usually within seconds, but can occasionally take a minute or more. Design for **at-least-once** delivery (handlers must be idempotent).
- Don't write the output to the same prefix that triggers the function, or you create an **infinite loop**. Write thumbnails to `thumbnails/`, and trigger only on `products/`.

## Lab task (CloudMart)
Enable **"Send notifications to Amazon EventBridge"** on the images bucket now. In Phase 8 you create the **thumbnail Lambda** that reacts to `products/*.jpg` uploads and writes `thumbnails/...`. For now, create an EventBridge rule that sends `Object Created` events from this bucket to a CloudWatch Logs group, upload an image, and read the event JSON.

Q: An image upload must trigger processing by several different services with advanced filtering. Which integration?
A: S3 → Amazon EventBridge (rules can route to many targets with rich filtering).

Q: How do you avoid an infinite loop when a Lambda writes back to the bucket that triggers it?
A: Use different prefixes (or buckets) for input and output, and filter the trigger by prefix.

@@ 3 | TOPIC | S3 performance and transfer | Prefix scaling, multipart, Transfer Acceleration, byte-range fetches, S3 Select | MUST
# S3 performance

## Request rates
At least **3,500 PUT/COPY/POST/DELETE** and **5,500 GET/HEAD requests per second per prefix**. More prefixes means more total throughput (e.g. spread keys over `products/a/...`, `products/b/...`).

## Faster uploads
- **Multipart upload:** parallel parts, retries per part. Recommended above 100 MB, required above 5 GB.
- **S3 Transfer Acceleration:** uploads go to the nearest **edge location**, then travel over the AWS backbone to the bucket. For users **far from the bucket's Region**. Uses the `bucket.s3-accelerate.amazonaws.com` endpoint.

## Faster downloads
- **Byte-range fetches:** GET parts of an object in parallel, or only the header of a file.
- **CloudFront** caching for repeated reads worldwide.
- **S3 Select / Glacier Select:** filter rows/columns of CSV/JSON/Parquet *server-side* with SQL, so less data travels. (For new work AWS points to Athena, but the exam may still mention S3 Select.)

## KMS limit
SSE-KMS uploads and downloads call KMS, which has per-Region request quotas. At very high rates this throttles. Fix: **S3 Bucket Keys** or a quota increase.

## Visibility
- **S3 Storage Lens:** org-wide dashboards of usage, activity and cost-saving recommendations.
- **S3 Inventory:** a daily/weekly CSV/Parquet list of all objects and their properties (encryption status, storage class), used for audits and as input to Batch Operations.
- **S3 Batch Operations:** run one action on billions of objects (copy, tag, restore from Glacier, invoke Lambda, encrypt existing objects).

## Exam patterns
- "Users worldwide upload large files to one bucket, slow" → **Transfer Acceleration** (plus multipart).
- "Encrypt all existing unencrypted objects" → **S3 Inventory + Batch Operations** (copy in place with encryption).

## Lab task (CloudMart)
Upload a 200 MB file with the CLI (it uses multipart automatically above a threshold): `aws s3 cp big.bin s3://.../tmp/`. Watch the parts. Then delete it. The lifecycle rule from earlier also cleans up any aborted parts.

Q: What is the S3 request rate per prefix?
A: At least 3,500 write and 5,500 read requests per second per prefix.

Q: Users far from the bucket's Region upload slowly. Which feature helps?
A: S3 Transfer Acceleration (uploads through the nearest edge location).

Q: How do you perform an action (e.g. encrypt or tag) on billions of existing objects?
A: S3 Batch Operations, typically driven by an S3 Inventory report.

@@ 2 | MODULE | Hybrid Storage, Data Transfer & Backup | Storage Gateway, Snow Family, DataSync, Transfer Family, AWS Backup | MUST
Moving data into AWS and protecting it.

@@ 3 | TOPIC | AWS Storage Gateway | File, Volume and Tape gateways for hybrid on-premises access | MUST
# AWS Storage Gateway

**Storage Gateway** is a VM or hardware appliance **on-premises** that lets local servers use **cloud storage** through standard protocols, with a local cache for speed.

**Analogy:** a local branch library. Popular books are on the shelf (the cache), and everything else is fetched from the central warehouse (S3) on demand.

| Gateway type | Protocol on-prem | Backed by | Use |
|---|---|---|---|
| **S3 File Gateway** | NFS / SMB | **S3 objects** (each file = one object) | File shares backed by S3, so you can use lifecycle/Glacier on them. Analytics on-prem files in AWS |
| **FSx File Gateway** | SMB | **FSx for Windows** | Low-latency local cache for Windows file shares in FSx |
| **Volume Gateway – Cached** | iSCSI | S3 (with EBS snapshots) | Primary data in AWS, frequently used data cached locally |
| **Volume Gateway – Stored** | iSCSI | Full copy on-prem, async backup to S3 as EBS snapshots | Low-latency access to the whole dataset, with cloud backup |
| **Tape Gateway** | iSCSI VTL | S3 + Glacier / Deep Archive | **Replace physical tape** backups, keeping existing backup software (Veeam, NetBackup) |

## Exam keywords
- "On-prem apps need NFS/SMB access to S3" → **S3 File Gateway**.
- "Replace the tape library, keep the backup software" → **Tape Gateway**.
- "iSCSI block storage backed by the cloud" → **Volume Gateway**.

## Lab task (CloudMart)
None (it needs an on-prem VM). Write a short note in your README on how a hypothetical CloudMart warehouse office could share product photos from S3 over SMB using an S3 File Gateway.

Q: A company wants to replace its physical tape backup infrastructure without changing backup software. Which service?
A: AWS Storage Gateway – Tape Gateway.

Q: On-premises servers need NFS access to files stored as S3 objects. Which service?
A: S3 File Gateway.

@@ 3 | TOPIC | DataSync, Transfer Family and the Snow Family | Online transfers, SFTP endpoints, offline petabyte transfers | MUST
# Moving data into AWS

## AWS DataSync (online)
An agent on-prem (or agentless for cloud-to-cloud) **copies data** from NFS, SMB, HDFS or object storage → **S3, EFS or FSx**, and also between AWS storage services. It handles scheduling, encryption, integrity checks, **preserves metadata/permissions**, and can throttle bandwidth.
- "Migrate or regularly sync a large on-prem NFS share to EFS/S3 over the network" → **DataSync**.

## AWS Transfer Family
Managed **SFTP, FTPS, FTP and AS2** endpoints in front of S3 or EFS. Partners keep using their SFTP clients, and the files land in S3. Authentication through service-managed users, AD or a custom IdP.
- "Partners upload files by SFTP and we want them in S3 with no servers to manage" → **Transfer Family**.

## Snow Family (offline)
Physical devices that AWS ships to you. You load the data locally and ship the device back, and AWS imports it into S3.
- **Snowball Edge Storage Optimized:** about 80–210 TB usable. For petabyte migrations, use several devices.
- **Snowball Edge Compute Optimized:** more vCPU/GPU for **edge computing** in disconnected places (ships, mines, military).
- Snowcone and Snowmobile have been retired, but older questions may still mention them: Snowcone = tiny, Snowmobile = exabyte truck.

## Online vs offline rule of thumb
Transfer time ≈ data size ÷ usable bandwidth. 100 TB over a 100 Mbps link takes about **3 months**, so ship a Snowball (about 1 week). If the network can do it in days, use **DataSync**, or **Direct Connect** for an ongoing need.

## Other
- **S3 Transfer Acceleration:** faster internet uploads to one bucket (covered earlier).
- **Direct Connect:** a dedicated private link (Phase 6).

## Lab task (CloudMart)
Optional (a few cents): create a DataSync task **S3 → S3** (agentless) that copies `cloudmart-images-<id>/products/` into `cloudmart-artifacts-<id>/image-backup/`. Run it, then open the task report to see the verified files. This is the same workflow you'd use for an on-prem NFS share, only with an agent as the source.

Q: You must move 500 TB to AWS and the internet link would take months. Which service?
A: AWS Snowball Edge Storage Optimized devices (offline transfer).

Q: Partners must upload files over SFTP into S3 without you managing servers. Which service?
A: AWS Transfer Family.

Q: Which service performs scheduled online transfers from on-prem NFS to EFS while preserving metadata?
A: AWS DataSync.

@@ 3 | TOPIC | AWS Backup | Centralized backup plans, vaults, cross-Region/account copies, Vault Lock | MUST
# AWS Backup

**AWS Backup** is a **central place to schedule and manage backups** for EC2, EBS, RDS, Aurora, DynamoDB, EFS, FSx, S3, Storage Gateway, DocumentDB, Neptune and more.

**Analogy:** one backup control room instead of a separate backup schedule in every service's console.

## Concepts
- **Backup plan:** frequency (e.g. daily at 03:00), retention (e.g. 35 days), lifecycle to cold storage, **copy to another Region/account**.
- **Resource assignment** by **tags** (e.g. every resource with `Backup=daily`) or by ARN.
- **Backup vault:** the encrypted container for recovery points, with its own KMS key and access policy.
- **Backup Vault Lock:** WORM for backups. **Compliance mode** makes them undeletable even by root, to protect against ransomware and insiders.
- **Cross-account backups** with AWS Organizations, plus **Backup Audit Manager** to report compliance.
- Point-in-time restore (PITR) for supported services (RDS, Aurora, DynamoDB, S3).

## Exam patterns
- "Centralize and automate backups across many services and accounts" → **AWS Backup**.
- "Backups must be immutable / protected from deletion" → **Vault Lock (compliance mode)**.
- "Copy backups to another Region for DR" → a cross-Region copy rule in the backup plan.

## Lab task (CloudMart)
Create a backup plan `cloudmart-daily` (daily, keep 7 days) with a resource assignment by tag `Project=cloudmart`. In Phase 5 the RDS database and DynamoDB table carry that tag, so they're protected automatically. In Phase 12 you add a cross-Region copy for DR.

Q: Which service centrally manages backups for RDS, EBS, EFS, DynamoDB and more, assigned by tags?
A: AWS Backup.

Q: How do you make backups immutable to protect against ransomware?
A: Enable AWS Backup Vault Lock in compliance mode.

@@ 3 | TOPIC | Lab milestone: CloudMart storage on S3 | Images on S3, presigned uploads, React on S3, versioning + lifecycle | MUST
# Lab milestone: storage on S3

## Checklist
- [ ] `cloudmart-images-<id>`: private, BPA on, versioning on, default SSE-S3, DenyInsecureTransport, CORS for your origins, lifecycle (IA after 30 days, noncurrent cleanup, abort multipart), EventBridge notifications on.
- [ ] `cloudmart-app-policy` has `s3:GetObject`/`s3:PutObject` on `images/*` and `s3:ListBucket` on the bucket.
- [ ] The CloudMart API runs with `cloudmart.storage=s3` (locally with your profile, and on EC2/ASG with the role).
- [ ] The admin page uploads through a **presigned PUT**, and the catalog shows images through **presigned GET** URLs.
- [ ] `cloudmart-web-<id>` hosts the React build (temporarily public, until CloudFront in Phase 6).
- [ ] The AWS Backup plan `cloudmart-daily` exists.

## Verify the role path
Start the ASG from Phase 3 (desired 2) with the updated jar. Upload an image through the S3-hosted UI → it reaches the ALB → the EC2 role signs the URL → the browser PUTs to S3. No access keys anywhere.

## Think like the exam
Why S3 rather than EFS for product images? Cheaper per GB, unlimited scale, presigned URLs, CDN integration, lifecycle to IA, event triggers. EFS is for apps that need a POSIX file system.

## Teardown
The buckets cost cents. Keep them. Scale the ASG to 0 and delete the ALB if they're running.

Q: Why is uploading directly to S3 with presigned URLs better than uploading through the app server?
A: It removes upload traffic and memory load from the app servers, scales automatically with S3, and needs no AWS credentials in the browser.
