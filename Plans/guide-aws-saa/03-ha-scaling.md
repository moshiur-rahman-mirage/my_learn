@@ 1 | MODULE | Phase 3 — High Availability & Scalability | Elastic Load Balancing and EC2 Auto Scaling across AZs | MUST
# Phase 3: High availability and scalability

- **Scalability** = handling more load. **Vertical:** a bigger instance. **Horizontal:** more instances.
- **High availability** = surviving failures by running in **at least 2 AZs**.
- **Elasticity** = scaling automatically with demand, so you pay only for what you use.

**CloudMart goal this phase:** put CloudMart behind an **Application Load Balancer** with an **Auto Scaling Group** across 2 AZs. On the Lab page, watch requests alternate between instances, trigger scale-out with "Stress CPU", and watch self-healing with "Break health".

@@ 2 | MODULE | Elastic Load Balancing | ALB, NLB, GWLB, health checks, stickiness, TLS | MUST
Spread traffic over healthy targets.

@@ 3 | TOPIC | Load balancer types: ALB, NLB, GWLB | Layer 7 vs Layer 4 vs Layer 3 appliances | MUST
# Elastic Load Balancing types

A load balancer gives clients **one DNS name**, spreads requests over many targets, sends traffic only to **healthy** targets, terminates TLS, and spans AZs.

| | **ALB** (Application) | **NLB** (Network) | **GWLB** (Gateway) |
|---|---|---|---|
| Layer | 7 (HTTP/HTTPS, gRPC, WebSocket) | 4 (TCP, UDP, TLS) | 3 (IP packets), GENEVE port 6081 |
| Routing | Path (`/api/*`), host (`shop.example.com`), headers, query string, source IP, method | Port-based | Sends all traffic through appliances |
| Targets | Instances, IPs, **Lambda**, ECS tasks | Instances, IPs, **ALB** | Firewall/IDS appliances |
| Static IP | No (DNS name only) | **Yes: one static IP / Elastic IP per AZ** | — |
| Performance | Very good | **Millions of req/s, ultra-low latency** | — |
| Extras | Auth (Cognito/OIDC), fixed responses, redirects (HTTP→HTTPS), WAF | Preserves the client IP, PrivateLink services | Third-party virtual appliances (Palo Alto, Fortinet) |

(The **Classic Load Balancer** is legacy. If it appears, the answer is usually "migrate to ALB/NLB".)

## Which one?
- HTTP microservices, path/host routing, containers → **ALB**.
- Extreme performance, TCP/UDP (gaming, IoT), **static IP or Elastic IP needed**, PrivateLink → **NLB**.
- Inspect all traffic with third-party firewalls → **GWLB**.
- Need a static IP *and* HTTP routing → NLB in front of an ALB (or Global Accelerator in front of the ALB).

## Client IP
The ALB replaces the source IP with its own. The app reads the real client IP from the **`X-Forwarded-For`** header (plus `X-Forwarded-Proto` and `X-Forwarded-Port`). In Spring Boot, set `server.forward-headers-strategy=native`.

## Lab task (CloudMart)
Create `sg-alb` (inbound 80/443 from `0.0.0.0/0`). Change `sg-cloudmart-app` to allow 8080 **only from `sg-alb`**: remove "My IP". Add `server.forward-headers-strategy: native` to application.yml so that logs show real client IPs.

Q: Which load balancer provides static IP addresses per AZ?
A: The Network Load Balancer (it supports Elastic IPs).

Q: How does an app behind an ALB learn the client's IP?
A: From the X-Forwarded-For header.

Q: Which load balancer routes based on URL path or hostname?
A: The Application Load Balancer.

@@ 3 | TOPIC | Target groups, health checks and routing rules | Listeners, rules, health check settings, deregistration delay | MUST
# Target groups, listeners and health checks

## Building blocks
- **Listener:** a port and protocol the LB listens on (e.g. HTTPS:443).
- **Rules:** conditions → actions (forward to a target group, redirect, fixed response, authenticate), checked in priority order.
- **Target group:** a set of targets plus a **health check** configuration.

```text
ALB listener HTTP:80   -> rule: redirect to HTTPS:443
ALB listener HTTPS:443 -> rule: path /api/*   -> tg-cloudmart-api (port 8080)
                       -> default            -> fixed response 404  (later CloudFront/S3 serves the UI)
```

## Health checks
- Protocol, port, **path** (e.g. `/api/lab/health`), interval, timeout, healthy/unhealthy threshold, success codes.
- An unhealthy target gets no traffic. **If it's in an ASG with ELB health checks enabled, the ASG replaces it.**
- A good health endpoint is cheap and checks only what *this* instance needs. Don't make it fail just because a shared DB blips, or every instance gets killed at once.

## Deregistration delay (connection draining)
When a target is removed, the LB stops sending new requests but lets in-flight ones finish for N seconds (default 300 s, set 0–3600). Lower it for short requests, so deployments and scale-in go faster.

## Slow start
New targets get gradually more traffic. Useful for a JVM warming up its JIT.

## Lab task (CloudMart)
Create target group `tg-cloudmart-api`: target type *instance*, HTTP 8080, health check path `/api/lab/health`, healthy threshold 2, interval 10 s, deregistration delay **30 s**. You attach it to the ASG in the milestone topic.

Q: What does deregistration delay do?
A: It stops new requests to a deregistering target while letting in-flight requests complete for a configured time.

Q: What happens to an ASG instance that fails the ELB health check (with ELB health checks enabled on the ASG)?
A: The ASG terminates it and launches a replacement.

@@ 3 | TOPIC | Sticky sessions, cross-zone load balancing, TLS and SNI | Stickiness cookies, cross-zone defaults, ACM certs, multiple domains | MUST
# Stickiness, cross-zone and TLS

## Sticky sessions (session affinity)
The LB sends the same client to the same target, using a cookie (`AWSALB`, or your app's own cookie).
- Use it when the app keeps session state in memory. **Better design:** make the app **stateless** and keep sessions in ElastiCache or DynamoDB. Then stickiness isn't needed, and scale-in loses no sessions.
- Downside: load can become uneven.

## Cross-zone load balancing
Spreads traffic evenly across **all** targets in all AZs, instead of evenly per AZ.
- **ALB:** on by default, no charge for inter-AZ traffic.
- **NLB / GWLB:** off by default. Charged for inter-AZ data when on.

Example: AZ-a has 2 instances, AZ-b has 8. Without cross-zone, each AZ-a instance gets 25% of the traffic. With cross-zone, every instance gets 10%.

## TLS / SSL
- Certificates come from **AWS Certificate Manager (ACM)**: free public certs with auto-renewal. You can also import certs.
- **SNI (Server Name Indication):** **one ALB/NLB listener serves many domains, each with its own certificate.** The client says which hostname it wants during the TLS handshake. (The Classic LB can't do this.)
- **Security policy:** chooses the TLS versions and ciphers (e.g. TLS 1.2+ only).
- **End-to-end encryption:** re-encrypt from the ALB to the targets (HTTPS target group) when compliance requires it.

## Lab task (CloudMart)
If you own a domain, request an ACM certificate for `shop.<yourdomain>` with DNS validation, add an HTTPS:443 listener, and redirect 80 → 443. If you don't have a domain, keep HTTP:80 for now. CloudFront in Phase 6 gives you HTTPS on a `*.cloudfront.net` name for free.

Q: How can one ALB serve HTTPS for several domains with different certificates?
A: With SNI. Attach multiple ACM certificates to the HTTPS listener.

Q: Is cross-zone load balancing enabled by default on an ALB?
A: Yes, at no extra charge. On an NLB it's off by default and inter-AZ data is charged.

@@ 2 | MODULE | EC2 Auto Scaling | Launch templates, scaling policies, lifecycle hooks | MUST
Add and remove instances automatically, and heal failures.

@@ 3 | TOPIC | Auto Scaling Groups and launch templates | Min/desired/max, multi-AZ, health checks, mixed instances | MUST
# Auto Scaling Groups (ASG)

An **ASG** keeps a group of EC2 instances at the right size: at least **min**, at most **max**, currently **desired**. It replaces unhealthy instances and balances them across AZs.

**Analogy:** a supermarket manager who opens more checkout lanes when queues grow, closes them when it's quiet, and replaces a cashier who falls ill.

## Launch template (what to launch)
The AMI, instance type, key pair (none for us), security groups, IAM instance profile, user data, IMDSv2 setting, EBS. It's versioned. (Launch *configurations* are legacy. The answer is always a launch template.)

## ASG settings
- **Subnets in 2+ AZs** → HA. If an AZ fails, the ASG launches the replacements in the healthy AZs.
- **Health check type:** EC2 (status checks only) or **ELB** (also uses the target group health check, which the exam prefers when there's a load balancer).
- **Health check grace period:** time to boot before checks count (e.g. 120 s for Spring Boot).
- **Mixed instances policy:** several instance types, plus On-Demand base capacity + a percentage of Spot.
- **Instance refresh:** rolling replacement when the launch template changes (a new AMI = a deployment).
- **Termination policy (default):** pick the AZ with the most instances, then the oldest launch template, then the instance closest to the next billing hour.

## Lab task (CloudMart)
Create launch template `lt-cloudmart`: AMI `cloudmart-ami-v1` (from Phase 2), `t3.micro`, `sg-cloudmart-app`, role `cloudmart-ec2-role`, IMDSv2 required. Add a small user-data line that restarts the service, so the latest jar is pulled from S3 on boot.

Q: Should you use launch configurations or launch templates for a new ASG?
A: Launch templates. Launch configurations are legacy and don't support newer features like mixed instances.

Q: How does an ASG provide high availability?
A: By spreading instances across multiple AZs and replacing unhealthy or failed instances automatically.

@@ 3 | TOPIC | Scaling policies | Target tracking, step, simple, scheduled, predictive; cooldown, warm pools | MUST
# Scaling policies

| Policy | How it works | Use when |
|---|---|---|
| **Target tracking** | "Keep average CPU at 50%." The ASG creates the CloudWatch alarms itself | The default, simplest choice |
| **Step scaling** | CloudWatch alarm bands: CPU > 70% add 2, > 90% add 4 | Different responses for different severity |
| **Simple scaling** | One alarm → one action, then wait for the cooldown | Legacy. Prefer step or target tracking |
| **Scheduled** | "Mon–Fri 08:00 set min=4" | Known, predictable patterns |
| **Predictive** | ML forecasts load from history and scales **ahead of time** | Recurring daily/weekly cycles |

## Good metrics to track
- `CPUUtilization` (average)
- **`ALBRequestCountPerTarget`**: often better for web apps
- Network in/out
- A custom CloudWatch metric, e.g. **SQS queue depth per instance** (the backlog-per-instance pattern for workers)

## Timing helpers
- **Cooldown / instance warmup:** time after a scaling activity before the next one is considered, so it doesn't overreact.
- **Warm pools:** pre-initialized, stopped instances that join quickly. They help apps with long boot times.
- **Lifecycle hooks:** pause an instance in `Pending:Wait` (to run setup or register somewhere) or `Terminating:Wait` (to copy logs or drain work) before it continues.

## Exam patterns
- "Traffic spikes every morning at 9" → **scheduled** (or predictive).
- "Scale workers according to the SQS backlog" → target tracking on a custom metric (backlog per instance).
- "Download logs before an instance terminates" → **lifecycle hook**.

## Lab task (CloudMart)
In the milestone topic you add a **target tracking policy: average CPU 50%**. You also add a **scheduled action** that sets desired=0 at night, so you can't forget to stop it.

Q: Which scaling policy keeps a metric such as CPU at a set value?
A: A target tracking scaling policy.

Q: How can you run a script on an instance before the ASG terminates it?
A: Use a lifecycle hook (Terminating:Wait), usually with EventBridge/SNS triggering a script or SSM Run Command.

Q: The workload has a predictable daily peak. What is the most efficient scaling approach?
A: Scheduled scaling or predictive scaling, so capacity is ready before the peak.

@@ 3 | TOPIC | Lab milestone: CloudMart behind ALB + ASG | Build, watch load balancing, trigger scale-out and self-healing | MUST
# Lab milestone: highly available CloudMart

## Build
1. **ALB** `alb-cloudmart`: internet-facing, **2 public subnets in 2 AZs**, `sg-alb`. Listener HTTP:80 → forward to `tg-cloudmart-api`.
2. **ASG** `asg-cloudmart`: launch template `lt-cloudmart`, the same 2 AZs, **min 2 / desired 2 / max 4**. Attach `tg-cloudmart-api`. Health check type **ELB**, grace period 120 s.
3. **Scaling policy:** target tracking, average CPU **50%**.
4. Optional: mixed instances (1 On-Demand base, the rest Spot with `t3.micro`, `t3a.micro`, `t3.small`).

## Experiments (use the CloudMart Lab page)
Run `VITE_API_BASE=http://<alb-dns-name> npm run dev`, then open `/lab`.

| Experiment | Do | Expect to see |
|---|---|---|
| Load balancing | Just watch | `instanceId` and `az` alternate between 2 instances |
| Scale-out | Click **Stress CPU** a few times (each click hits one instance) | After 3–5 minutes CPU > 50%, a CloudWatch alarm fires, and the ASG launches a 3rd/4th instance. Its id appears in the table |
| Scale-in | Wait about 15 minutes after the stress ends | The ASG terminates the extra instances. Deregistration delay drains them first |
| Self-healing | Click **Break health** | The target turns unhealthy, the ALB stops routing to it, and the ASG terminates and replaces it |
| AZ failure (simulated) | Terminate all instances in one AZ manually | The ASG rebalances by launching in the healthy AZ. The site stays up |

Look at **EC2 → Auto Scaling Groups → Activity** and **CloudWatch → Alarms** to read what happened and why.

## Deployment practice
Upload a new jar to S3, create a new AMI (or rely on the user-data pull), make a new launch template version, then start an **Instance refresh** (min healthy 50%). You've done a rolling deployment with zero downtime.

## Teardown (important: an ALB costs money every hour)
Set the ASG desired/min/max to 0, delete the ALB. Keep the launch template, target group and AMI (free or cheap) to recreate quickly in later phases.

Q: In an ASG with min 2 across 2 AZs, what happens if one AZ fails?
A: The ASG launches replacement instances in the remaining healthy AZ to keep the desired capacity, and the ALB sends traffic only to healthy targets.

Q: What is a zero-downtime way to roll out a new AMI to an ASG?
A: Create a new launch template version and start an instance refresh (rolling replacement with a minimum healthy percentage).
