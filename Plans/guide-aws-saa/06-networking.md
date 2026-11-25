@@ 1 | MODULE | Phase 6 — Networking: VPC, Route 53, CloudFront | Private networks, hybrid connectivity, DNS and global content delivery | MUST
# Phase 6: Networking

Networking questions are everywhere: "the instance in a private subnet can't reach the internet", "connect on-prem to AWS", "access S3 without the internet", "serve users globally with low latency".

**CloudMart goal this phase:** build a custom **3-tier VPC** (public/app/data subnets in 2 AZs), move the ALB, the app and the DB into it, reach S3 and DynamoDB through **gateway endpoints**, and serve everything through **CloudFront** (with an optional Route 53 domain).

@@ 2 | MODULE | VPC Fundamentals | CIDR, subnets, route tables, gateways, SGs vs NACLs | MUST
Your private network in AWS.

@@ 3 | TOPIC | VPC, CIDR and subnets | IP ranges, public vs private subnets, reserved addresses | MUST
# VPC, CIDR and subnets

A **VPC (Virtual Private Cloud)** is your own isolated network in a Region. **Analogy:** a gated housing estate. The VPC is the estate wall, subnets are streets, route tables are the road signs, and gateways are the estate gates.

## CIDR
A CIDR block defines an IP range: `10.0.0.0/16` = 65,536 addresses (the `/16` means the first 16 bits are fixed). `/24` = 256 addresses. Smaller suffix = bigger network.
- VPC size: from `/16` (largest) to `/28` (smallest). You can add secondary CIDRs later.
- Use **private ranges** (RFC 1918): `10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`.
- **Don't overlap** with on-prem or other VPCs you may connect to later. Overlap blocks peering and VPN routing.

## Subnets
- A subnet lives in **exactly one AZ**.
- **AWS reserves 5 IPs in every subnet**: network address, VPC router (.1), DNS (.2), future use (.3), broadcast (last). A `/24` gives 251 usable addresses (an exam question!).
- **Public subnet** = its route table has `0.0.0.0/0 → Internet Gateway`. **Private subnet** = no route to an IGW.

## CloudMart VPC plan

```text
VPC cloudmart-vpc 10.0.0.0/16
  AZ-a                         AZ-b
  public-a   10.0.0.0/24       public-b   10.0.1.0/24    ALB, NAT Gateway
  app-a      10.0.10.0/24      app-b      10.0.11.0/24   EC2/ECS (private)
  data-a     10.0.20.0/24      data-b     10.0.21.0/24   RDS, ElastiCache (private, no internet)
```
- Three tiers × two AZs is the classic exam architecture. Each tier gets its own route table and security group.

## Default VPC
Every Region has a default VPC (`172.31.0.0/16`) with public subnets in each AZ. It's handy for quick tests, but production uses custom VPCs.

## Lab task (CloudMart)
Use **VPC → Create VPC → "VPC and more"**: name `cloudmart`, `10.0.0.0/16`, 2 AZs, 2 public + 4 private subnets (you can adjust the names), **NAT gateways: none for now**, **VPC endpoints: S3 gateway**. Then look at the generated route tables and work out why each subnet is public or private.

Q: How many IP addresses does AWS reserve in each subnet?
A: Five (the first four and the last one).

Q: What makes a subnet public?
A: Its route table has a route to an Internet Gateway (0.0.0.0/0 → igw).

Q: Why must VPC CIDRs not overlap with on-prem networks?
A: Overlapping ranges can't be routed over peering, VPN or Direct Connect.

@@ 3 | TOPIC | Internet Gateway, NAT Gateway and route tables | Inbound/outbound internet, NAT for private subnets, egress-only IGW | MUST
# Internet access in a VPC

## Internet Gateway (IGW)
Lets resources with a **public IP** in public subnets talk to the internet in both directions. One per VPC, horizontally scaled and highly available.

## NAT Gateway
Lets instances in **private subnets** start **outbound** connections (OS updates, external APIs, downloading the jar from the internet) while **blocking inbound** connections from the internet.
- Lives in a **public subnet**, has an **Elastic IP**, and is managed by AWS (scales to 100 Gbps).
- It's **AZ-scoped**: for HA, create **one NAT Gateway per AZ** and point each private route table at the NAT in its own AZ.
- Costs: hourly + per GB processed. **The biggest lab cost trap.**
- A **NAT instance** (self-managed EC2) is legacy: you manage HA, disable source/destination check, and use security groups. The exam answer is usually NAT Gateway.

## Egress-only Internet Gateway
The **IPv6** equivalent of NAT: outbound-only IPv6. (IPv6 addresses are all public, so NAT doesn't apply.)

## Route tables
Each subnet is associated with one route table. The most specific route wins. The `local` route (VPC CIDR) always exists.

```text
public-rt:   10.0.0.0/16 -> local      0.0.0.0/0 -> igw-123
app-rt-a:    10.0.0.0/16 -> local      0.0.0.0/0 -> nat-a      pl-s3 -> vpce-s3 (gateway endpoint)
data-rt:     10.0.0.0/16 -> local                                (no internet at all)
```
- The app subnets reach the internet only *outbound* through NAT, and S3 privately through the endpoint. The data subnets have no internet route.

## Troubleshooting checklist: "private instance can't reach the internet"
1. Is there a NAT Gateway in a **public** subnet?
2. Does the private route table have `0.0.0.0/0 → nat`?
3. Does the public subnet's route table have `0.0.0.0/0 → igw`?
4. Do the SG/NACL allow outbound traffic (and NACL return ports)?

## Lab task (CloudMart)
Create **one** NAT Gateway in `public-a` (to save cost; note that production needs one per AZ), and add `0.0.0.0/0 → nat` to both app route tables. **Delete it at the end of the session.**

Q: A NAT Gateway must be placed in which kind of subnet?
A: A public subnet (with a route to the Internet Gateway).

Q: How do you make NAT highly available across AZs?
A: Create one NAT Gateway per AZ, and route each private subnet to the NAT in its own AZ.

Q: What provides outbound-only internet access for IPv6?
A: An egress-only Internet Gateway.

@@ 3 | TOPIC | Security Groups vs Network ACLs | Stateful instance-level vs stateless subnet-level filtering | MUST
# Security groups vs NACLs

| | **Security Group** | **Network ACL** |
|---|---|---|
| Level | ENI / instance | **Subnet** |
| State | **Stateful** (return traffic allowed automatically) | **Stateless** (you must allow return traffic, i.e. ephemeral ports **1024–65535**) |
| Rules | **Allow only** | **Allow and Deny** |
| Evaluation | All rules together | **In number order, first match wins** |
| Default | Inbound deny, outbound allow | The default NACL allows all. A **custom NACL denies all** until you add rules |

**Analogy:** the NACL is the guard at the **street entrance** checking every car in both directions. The security group is the **doorman of each building**, who remembers who went in and lets them out.

## When the NACL matters on the exam
- **Block a specific malicious IP range** → a NACL deny rule (SGs can't deny). For HTTP traffic, AWS WAF on the ALB/CloudFront is another answer.
- A custom NACL breaks the app → usually because the **ephemeral return ports** are missing.

## Lab task (CloudMart)
Create SGs in the new VPC: `sg-alb` (80/443 from anywhere), `sg-app` (8080 from `sg-alb`), `sg-db` (5432 from `sg-app`), `sg-cache` (6379 from `sg-app`). Optional experiment: add a NACL deny rule for your own IP on the public subnets, see the site time out, then remove it.

Q: Which supports explicit deny rules: security groups or NACLs?
A: Network ACLs.

Q: Why must a NACL allow ephemeral ports outbound for a web server?
A: NACLs are stateless, so response traffic to clients (on ports 1024–65535) must be explicitly allowed.

@@ 2 | MODULE | Private Access and VPC Connectivity | Endpoints, PrivateLink, peering, Transit Gateway, hybrid links | MUST
Connecting VPCs to AWS services, other VPCs and on-premises networks.

@@ 3 | TOPIC | VPC Endpoints and PrivateLink | Gateway endpoints (S3/DynamoDB) vs interface endpoints, exposing services | MUST
# VPC endpoints

Endpoints let resources in private subnets reach AWS services **without an IGW, NAT or public IPs**. Traffic stays on the AWS network.

| | **Gateway endpoint** | **Interface endpoint (PrivateLink)** |
|---|---|---|
| Services | **Only S3 and DynamoDB** | Most AWS services (SQS, SNS, Secrets Manager, KMS, ECR, CloudWatch, STS…) and S3 too |
| How | An entry in the **route table** (prefix list) | An **ENI with a private IP** in your subnet. DNS resolves the service name to it |
| Cost | **Free** | Per hour per AZ + per GB |
| On-prem access | No | **Yes** (through VPN/Direct Connect) |
| Security | Endpoint policy | Endpoint policy + security group |

## Exam patterns
- "EC2 in a private subnet must access S3 cheaply without the internet" → **S3 gateway endpoint** (it also removes NAT data charges).
- "Access SQS/Secrets Manager privately" → **interface endpoint**.
- "Restrict the bucket to traffic from our VPC" → bucket policy condition `aws:SourceVpce`.

## PrivateLink for your own services
You can **expose your own service** (behind an **NLB**) to other VPCs and accounts as an *endpoint service*. Consumers create an interface endpoint. There's no peering, no overlapping-CIDR problem, and it's one-way. This is the classic **SaaS provider** pattern.

## Lab task (CloudMart)
1. Make sure the **S3 gateway endpoint** is in the app route tables, and add a **DynamoDB gateway endpoint** too.
2. Once the app runs in the private subnets (milestone), CloudMart reaches S3 and DynamoDB *without NAT*. Check the endpoint's route entry `pl-xxxx → vpce-xxxx`.

Q: Which AWS services support gateway VPC endpoints?
A: Only Amazon S3 and DynamoDB.

Q: How can a SaaS provider expose a service to thousands of customer VPCs without VPC peering?
A: AWS PrivateLink: an endpoint service behind an NLB that customers consume through interface endpoints.

@@ 3 | TOPIC | VPC Peering and Transit Gateway | One-to-one non-transitive links vs a hub-and-spoke router | MUST
# Connecting VPCs

## VPC peering
A private connection between **two** VPCs (same or different account, same or different Region).
- **Not transitive:** if A↔B and B↔C, A **can't** reach C through B. You need A↔C as well (a full mesh: n(n-1)/2 links).
- **CIDRs must not overlap.**
- Update the route tables on both sides. SGs can reference peered SGs (same Region).
- No bandwidth bottleneck, cheap. Good for a few VPCs.

## AWS Transit Gateway (TGW)
A Regional **hub router**: attach VPCs, VPNs and Direct Connect gateways, and route between them through **route tables** on the TGW.
- **Transitive** routing, hub-and-spoke. It scales to thousands of VPCs.
- Supports **IP multicast** (unique to TGW), inter-Region peering, and sharing across accounts through **RAM**.
- **ECMP over multiple VPN tunnels** to increase VPN bandwidth.

**Analogy:** peering = a private road between two houses. Transit Gateway = a roundabout that every road connects to.

## Exam patterns
- "Connect 2–3 VPCs simply and cheaply" → **peering**.
- "Connect hundreds of VPCs and on-prem with central routing" → **Transit Gateway**.
- "A can't reach C even though A–B and B–C are peered" → peering isn't transitive. Add a peering or use TGW.
- "Multicast" → TGW.

## Lab task (CloudMart)
Optional: create a second small VPC `analytics-vpc` (`10.1.0.0/16`), peer it with `cloudmart-vpc`, add routes on both sides, and test a ping/curl from an instance there to the app's private IP (allowed in `sg-app`). Delete it afterwards.

Q: Is VPC peering transitive?
A: No.

Q: Which service provides transitive hub-and-spoke routing among many VPCs and on-prem networks?
A: AWS Transit Gateway.

Q: Which AWS networking service supports IP multicast?
A: Transit Gateway.

@@ 3 | TOPIC | Hybrid connectivity: Site-to-Site VPN, Direct Connect, Client VPN | Encrypted internet tunnels vs dedicated private lines | MUST
# Hybrid networking

## AWS Site-to-Site VPN
An **IPsec-encrypted tunnel over the internet** between your data center (a **Customer Gateway** device) and AWS (a **Virtual Private Gateway** on the VPC, or a Transit Gateway).
- Two tunnels per connection for redundancy. Up to about 1.25 Gbps per tunnel.
- **Quick to set up (minutes to hours)** and cheap, but internet latency varies.
- **VPN CloudHub:** several branch offices connect to one VGW and can talk to each other (hub-and-spoke over VPN).

## AWS Direct Connect (DX)
A **dedicated private physical connection** from your network to AWS through a DX location.
- Dedicated connections of 1/10/100 Gbps, or hosted connections from 50 Mbps upward through partners.
- **Consistent low latency, high bandwidth, lower data-transfer cost.**
- **Takes weeks to months to provision.**
- **Not encrypted by default.** For encryption, run a **VPN over DX** (or MACsec on supported links).
- Virtual interfaces: **private VIF** (to a VPC through VGW/DX Gateway), **public VIF** (AWS public services such as S3), **transit VIF** (to a Transit Gateway).
- **Direct Connect Gateway:** one DX connection to VPCs in **many Regions**.

## Resilience patterns
| Need | Design |
|---|---|
| Cheap backup for DX | **DX primary + Site-to-Site VPN backup** |
| Maximum resiliency | **Two DX connections at two different DX locations** (each with redundant devices) |

## AWS Client VPN
OpenVPN-based **remote access for individual users** (laptops) into a VPC. It's not site-to-site.

## Exam patterns
- "Connect on-prem quickly, encrypted, low cost" → **Site-to-Site VPN**.
- "Consistent performance, large ongoing data transfer, private" → **Direct Connect**.
- "Need DX but must be encrypted" → **VPN over Direct Connect** (or MACsec).
- "Need connectivity *now* while DX is being provisioned" → VPN first, DX later.

## Lab task (CloudMart)
None (it needs on-prem hardware). Draw in your README how a CloudMart warehouse office would connect: VPN first, then DX + VPN backup when traffic grows.

Q: Is Direct Connect encrypted by default?
A: No. Use an IPsec VPN over DX or MACsec for encryption.

Q: What is a cost-effective backup for a Direct Connect connection?
A: A Site-to-Site VPN connection.

Q: Which service connects one Direct Connect link to VPCs in multiple Regions?
A: Direct Connect Gateway.

@@ 3 | TOPIC | Bastion hosts, Session Manager, Flow Logs and Network Firewall | Admin access, traffic logging, deep inspection | SHOULD
# Admin access and network monitoring

## Reaching private instances
- **Bastion host:** an EC2 instance in a public subnet that you SSH into, then hop to private instances. Its SG allows 22 only from your IP. It's legacy.
- **SSM Session Manager:** **no bastion, no inbound ports, no keys**, IAM-controlled, with sessions logged to S3/CloudWatch. The preferred exam answer. (Private subnets need NAT *or* interface endpoints for `ssm`, `ssmmessages` and `ec2messages`.)
- **EC2 Instance Connect Endpoint:** SSH/RDP to private instances without public IPs.

## VPC Flow Logs
Capture **IP traffic metadata** (source/destination IP, port, protocol, ACCEPT/REJECT, bytes) for a VPC, subnet or ENI → **CloudWatch Logs, S3 or Firehose**. They don't capture packet contents (use **Traffic Mirroring** for that).
Use: troubleshoot SG/NACL denies, detect unusual traffic, feed security analysis (GuardDuty uses flow logs internally).

## AWS Network Firewall
A managed, stateful **network firewall for the whole VPC**: domain allow/deny lists, IPS (Suricata rules) and deep packet inspection. Deployed in a firewall subnet, with routing changed to pass through it. Exam: "inspect and filter all traffic entering/leaving the VPC, including domain filtering" → **Network Firewall**.

## Reachability Analyzer
Tests whether two endpoints can connect and shows *which* component blocks it (SG, NACL, route) without sending packets.

## Lab task (CloudMart)
Enable **VPC Flow Logs** for `cloudmart-vpc` → CloudWatch Logs (group `/cloudmart/vpc-flow`). Try to reach the app's private IP from the internet and find the REJECT lines. Use **Reachability Analyzer** to test ALB → app instance on 8080.

Q: What captures IP traffic metadata (accept/reject) in a VPC?
A: VPC Flow Logs.

Q: What is the most secure way to give admins shell access to private instances without a bastion?
A: AWS Systems Manager Session Manager.

@@ 2 | MODULE | DNS and Edge Services | Route 53, CloudFront, Global Accelerator | MUST
Routing users to the right place, fast.

@@ 3 | TOPIC | Route 53 basics | Hosted zones, record types, alias records, TTL | MUST
# Amazon Route 53

**Route 53** is AWS's highly available **DNS** service (100% SLA), domain registrar and health-checking service.

**Analogy:** the phone book of the internet. People look up `shop.example.com` and get the number (IP) to call.

## Hosted zones
- **Public hosted zone:** answers queries from the internet.
- **Private hosted zone:** answers only inside associated VPCs (e.g. `db.cloudmart.internal`).

## Record types
| Type | Maps |
|---|---|
| A / AAAA | Name → IPv4 / IPv6 |
| CNAME | Name → another name (**not allowed at the zone apex**, e.g. `example.com`) |
| **Alias** (Route 53 extension) | Name → **AWS resource** (ALB, CloudFront, S3 website, API Gateway, another record). **Works at the apex**, free queries, follows the resource's IP changes |
| MX, TXT, NS, SOA, CAA, SRV | Mail, verification, delegation… |

## Alias vs CNAME (exam favourite)
- "Point `example.com` (the apex) at an ALB/CloudFront" → **Alias A record**. A CNAME can't be used at the apex.
- An alias can't point at an EC2 DNS name.

## TTL
How long resolvers cache the answer. High TTL = fewer queries, slower changes. Lower it before a migration.

## Lab task (CloudMart)
If you have a domain (or buy a cheap one in Route 53): create a public hosted zone and an **alias A record** `shop.<domain>` → your CloudFront distribution (after the CloudFront topic). Without a domain: create a **private hosted zone** `cloudmart.internal` in `cloudmart-vpc` with a CNAME `db.cloudmart.internal` → the RDS endpoint, and use that name in `DB_URL`.

Q: Can a CNAME be used at the zone apex (example.com)?
A: No. Use a Route 53 alias record.

Q: What is a Route 53 private hosted zone?
A: A DNS zone that resolves only within the associated VPCs.

@@ 3 | TOPIC | Route 53 routing policies and health checks | Simple, weighted, latency, failover, geolocation, geoproximity, multivalue, IP-based | MUST
# Routing policies (memorize)

| Policy | Behaviour | Use |
|---|---|---|
| **Simple** | One record, possibly several IPs returned in random order. **No health checks** | A single resource |
| **Weighted** | Splits traffic by weight (e.g. 90/10) | **Canary / blue-green**, A/B tests |
| **Latency-based** | Sends users to the Region with the **lowest latency** for them | Multi-Region apps, best performance |
| **Failover** | Primary/secondary. Goes to the secondary when the primary's **health check fails** | **Active-passive DR** |
| **Geolocation** | By the user's **continent/country/state** (plus a default record) | Localized content, **compliance/licensing restrictions** |
| **Geoproximity** | By geographic distance, with a **bias** to grow or shrink a Region's area (Traffic Flow) | Shift traffic between Regions gradually |
| **Multivalue answer** | Up to 8 **healthy** records returned | Simple client-side load balancing with health checks (not a substitute for an ELB) |
| **IP-based** | By the client's IP CIDR | Route a specific ISP or network to specific endpoints |

## Health checks
- Check an endpoint (HTTP/HTTPS/TCP, with an optional string match) from multiple global locations.
- **Calculated health checks** combine others with AND/OR.
- **Check a CloudWatch alarm**, useful for **private resources** (the health checkers can't reach inside a VPC).
- Health checks + failover/latency/weighted policies = automatic DNS failover.

## Exam traps
- Latency ≠ geolocation. "Lowest latency" → latency-based. "Users in Germany must see the German site" or "restrict by country" → geolocation.
- DNS failover depends on TTL and client caching. It's not instant.

## Lab task (CloudMart)
With a domain: create a **failover** record pair, primary = CloudFront/ALB with a health check on `/api/lab/health`, secondary = an S3 static "We'll be right back" page. Click **Break health** on all instances (or scale the ASG to 0) and watch DNS fail over. You'll reuse this in the DR phase.

Q: Which routing policy routes users to the Region with the lowest network latency?
A: Latency-based routing.

Q: Which routing policy implements active-passive disaster recovery?
A: Failover routing with health checks.

Q: Content must be restricted by user country. Which routing policy?
A: Geolocation routing.

@@ 3 | TOPIC | Amazon CloudFront | CDN: origins, OAC, cache behaviors, TTL, signed URLs, edge functions | MUST
# Amazon CloudFront

**CloudFront** is AWS's **CDN**: it caches content at **400+ edge locations** close to users. It cuts latency and origin load, and adds security (Shield Standard, WAF, TLS).

**Analogy:** instead of every customer travelling to the main warehouse, popular products are stocked in small local shops.

## Origins
- **S3 bucket**: kept **private** with **Origin Access Control (OAC)**. Only CloudFront may read it (the bucket policy allows the `cloudfront.amazonaws.com` principal with a `SourceArn` condition). OAI is the legacy version.
- **Custom origin:** ALB, EC2, API Gateway, any HTTP server. For an ALB origin, restrict it to CloudFront with the **CloudFront managed prefix list** in `sg-alb` and/or a secret custom header.
- **Origin groups:** primary + secondary origin for **origin failover**.

## Cache behaviors
Path patterns → origin + cache settings, e.g. `/api/*` → ALB (no caching, forward headers/cookies/query), default `*` → S3 (cache for 1 day).
- **Cache policy:** what's in the cache key (headers, cookies, query strings) and the TTLs. Fewer key parts = better hit ratio.
- **Origin request policy:** what's forwarded to the origin without affecting the key.
- **Invalidation:** `/*` or specific paths, to drop cached content (or use versioned file names, as Vite does with hashed assets).

## Security features
- **Signed URLs** (one file) / **signed cookies** (many files) for private content, e.g. paid videos. Different from S3 presigned URLs: these are signed with a CloudFront key pair or trusted key group.
- **Geo restriction:** allow-list or block-list countries.
- **Field-level encryption:** encrypt specific form fields at the edge.
- **HTTPS:** viewer → CloudFront with an ACM certificate (**must be in `us-east-1`** for CloudFront).
- **AWS WAF** attaches to the distribution.

## Edge compute
- **CloudFront Functions:** lightweight JS, sub-ms, viewer request/response only. Header rewrites, redirects, URL normalization, simple auth.
- **Lambda@Edge:** Node/Python, more time and memory, network access, all four triggers (viewer and origin request/response).

## CloudFront vs S3 Cross-Region Replication
- CloudFront: cache static content globally (TTL-based), the same content everywhere.
- CRR: full bucket copies in other Regions, for dynamic content that needs low latency in a few Regions, or for DR.

## Lab task (CloudMart)
Create a distribution:
1. Origin 1: `cloudmart-web-<id>` (S3 REST endpoint, not the website endpoint) with **OAC**. Let CloudFront update the bucket policy, then **turn Block Public Access back ON** and remove the public policy from Phase 4.
2. Origin 2: the ALB. Behavior **`/api/*` → ALB** with the managed policies `CachingDisabled` + `AllViewer`.
3. Default root object `index.html`. Custom error responses 403/404 → `/index.html` with 200 (SPA routing).
4. Rebuild the React app with `VITE_API_BASE=""` (same origin!) and `aws s3 sync`. Run an invalidation `/*`.
5. Open `https://dxxxx.cloudfront.net`. CloudMart now runs over HTTPS on one domain, with no CORS needed for the API.

Q: How do you keep an S3 origin private so that only CloudFront can read it?
A: Use Origin Access Control (OAC) and a bucket policy allowing only the CloudFront distribution.

Q: In which Region must the ACM certificate for CloudFront be created?
A: us-east-1 (N. Virginia).

Q: Signed URLs vs signed cookies in CloudFront?
A: Signed URLs grant access to an individual file; signed cookies grant access to multiple files without changing URLs.

@@ 3 | TOPIC | AWS Global Accelerator | Anycast static IPs over the AWS backbone, fast regional failover | SHOULD
# AWS Global Accelerator

**Global Accelerator** gives you **2 static anycast IPv4 addresses**. Users connect to the nearest edge location, and traffic then rides the **AWS global backbone** to your endpoints (ALB, NLB, EC2, Elastic IP) in one or more Regions.

## Features
- **Static IPs** that never change. Good for allow-lists in client firewalls.
- **Fast regional failover** (< 1 minute) through health checks, with no DNS caching problems.
- Traffic dials and endpoint weights per Region.
- Works for **TCP and UDP**, so it suits non-HTTP traffic: gaming, IoT, VoIP.

## CloudFront vs Global Accelerator

| | CloudFront | Global Accelerator |
|---|---|---|
| Caches content | **Yes** | No |
| Protocols | HTTP/HTTPS (WebSocket) | **TCP/UDP** (any) |
| Static IPs | No | **Yes, 2 anycast IPs** |
| Best for | Static/dynamic web content, caching | Non-HTTP apps, static IP need, fast multi-Region failover |

## Exam patterns
- "Global users, UDP game servers, need consistent performance" → **Global Accelerator**.
- "Customers must allow-list fixed IPs for a multi-Region app" → **Global Accelerator**.
- "Cache images and videos close to users" → **CloudFront**.

## Lab task (CloudMart)
Optional (it bills hourly): create an accelerator with the ALB as endpoint, call `http://<static-ip>/api/lab/whoami`, then delete it.

Q: Which service provides two static anycast IPs and routes TCP/UDP traffic over the AWS backbone?
A: AWS Global Accelerator.

Q: Does Global Accelerator cache content?
A: No. It optimizes the network path. CloudFront caches.

@@ 3 | TOPIC | Lab milestone: CloudMart in a custom 3-tier VPC behind CloudFront | Move ALB, app and data tiers; private endpoints; HTTPS via CloudFront | MUST
# Lab milestone: production-style network

## Build steps
1. **VPC:** `cloudmart-vpc` with public, app and data subnets in 2 AZs (from the first topic).
2. **NAT Gateway** in `public-a` (temporary), plus app route tables → NAT. **Gateway endpoints** for S3 and DynamoDB on the app route tables.
3. **Data tier:** create a new DB subnet group on `data-a`/`data-b`. Restore the CloudMart RDS snapshot into it (or create a fresh DB) with `sg-db`. Create ElastiCache in the data subnets with `sg-cache`.
4. **App tier:** update `lt-cloudmart` to the new `sg-app` and point the ASG at **app-a/app-b** (private subnets). Instances get **no public IPs**.
5. **ALB:** create `alb-cloudmart` in **public-a/public-b** with `sg-alb`. Restrict `sg-alb` inbound to the **CloudFront origin-facing managed prefix list**.
6. **CloudFront:** origins = S3 web bucket (OAC) + ALB. Behaviors `/api/*` → ALB, default → S3.
7. **Flow logs** on.

## Verify
- `https://<cloudfront-domain>/lab`: whoami shows private instance IPs in 2 AZs.
- Direct ALB access from your browser fails (only CloudFront can reach it).
- Session Manager still works on the private instances (through NAT, or add SSM interface endpoints).
- `aws s3` calls from the instance go through the gateway endpoint. Check with Flow Logs (no NAT bytes for S3).

## Cost-saving variant
After the jar has been downloaded and the instances are up, the app needs NAT **only** for internet APIs. With S3/DynamoDB gateway endpoints plus interface endpoints for SSM, you could remove NAT entirely. That's a classic cost-optimization exam idea.

## Teardown
Delete the NAT Gateway and release its Elastic IP. Scale the ASG to 0 and delete the ALB. Disable CloudFront (or keep it: it's free when idle). Stop or delete RDS.

Q: Which three-tier design keeps the database unreachable from the internet?
A: Put the DB in private data subnets with no internet route, and allow its SG only from the app tier's SG. The app tier is private behind a public ALB.
