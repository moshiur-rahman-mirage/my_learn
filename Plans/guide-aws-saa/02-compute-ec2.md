@@ 1 | MODULE | Phase 2 — EC2 & Instance Storage | Instances, pricing, AMIs, EBS, instance store, EFS, FSx | MUST
# Phase 2: EC2 and storage attached to compute

EC2 is the foundation of most exam scenarios: pricing models, storage choice, placement and networking.

**CloudMart goal this phase:** run the CloudMart API jar on a single EC2 instance (built with user data, managed with SSM, using the IAM role from Phase 1), and see `whoami` return a real instance ID and AZ.

@@ 2 | MODULE | EC2 Core | Instances, AMIs, user data, security groups, pricing | MUST
Virtual servers and how to pay for them.

@@ 3 | TOPIC | EC2 basics, instance types and AMIs | Families, sizes, AMI, user data, key pairs | MUST
# EC2 basics

**Amazon EC2** rents virtual servers (instances) by the second. You choose the **AMI** (the OS image), the **instance type** (CPU/RAM), the **storage**, the **network** (VPC/subnet), the **security group**, the **IAM role** and the **user data**.

## Instance type naming: `m7g.large`
- `m` = family, `7` = generation, `g` = processor attribute (g = Graviton/ARM, i = Intel, a = AMD), `large` = size.

| Family | Optimized for | Example use |
|---|---|---|
| **T** (t3, t4g) | Burstable general purpose (CPU credits) | Dev/test, small web apps (CloudMart) |
| **M** | Balanced general purpose | App servers |
| **C** | Compute | Batch, gaming, HPC, video encoding |
| **R / X** | Memory | In-memory caches, big JVM heaps, SAP |
| **I / D** | Storage (fast local NVMe) | NoSQL, data warehousing |
| **P / G / Inf / Trn** | Accelerated (GPU, ML chips) | ML training and inference |

- **Graviton (ARM)** instances give up to about 40% better price-performance. Java runs on them unchanged.
- **Burstable T instances** earn CPU credits while idle and spend them when busy. In *unlimited* mode, extra bursting costs extra.

## AMI (Amazon Machine Image)
A template with the OS plus any pre-installed software. AMIs are **Regional**: copy one to use it in another Region. Types: AWS-provided (Amazon Linux 2023), Marketplace, or **your own "golden AMI"** (faster boot because the software is pre-installed, and good for Auto Scaling).

## User data
A script that runs **once at first boot**, as root. Use it to install software and start the app. **Analogy:** the AMI is the furniture in a flat, and user data is the to-do list you hand to the mover on day one.

## Key pairs vs Session Manager
SSH needs a key pair and port 22 open. **SSM Session Manager** needs neither: the SSM agent on the instance plus the `AmazonSSMManagedInstanceCore` role is enough. You get audited shell access with no inbound ports open (the exam's preferred answer).

## Lab task (CloudMart)
Launch **one** `t3.micro` or `t4g.micro` instance with Amazon Linux 2023 in your default VPC, a public subnet, the IAM role `cloudmart-ec2-role`, and **no key pair**. Connect with **Session Manager**. Run `curl` against the metadata endpoint with a token (see Phase 0), and read the instance ID.

Q: What runs a script once at first boot of an EC2 instance?
A: User data (cloud-init).

Q: How can you get shell access to an EC2 instance with no SSH port open and no key pair?
A: AWS Systems Manager Session Manager, using the SSM agent and an instance role with AmazonSSMManagedInstanceCore.

Q: Are AMIs global or Regional?
A: Regional. Copy the AMI to another Region to use it there.

@@ 3 | TOPIC | Security Groups | Stateful instance firewall, allow rules only, SG referencing | MUST
# Security groups (SG)

A **security group** is a **stateful virtual firewall** attached to an ENI (instance, RDS, ALB, Lambda in a VPC and so on).

- **Allow rules only.** There's no deny. Anything not allowed is blocked.
- **Stateful:** if inbound traffic is allowed, the response goes out automatically (and the other way round).
- Default: all inbound denied, all outbound allowed.
- A rule's source can be a CIDR, a prefix list or **another security group**. That last one is the powerful pattern.
- Several SGs on one instance combine as the union of their rules. Changes apply immediately.

## SG referencing (the 3-tier pattern)

```text
sg-alb  : inbound 443 from 0.0.0.0/0
sg-app  : inbound 8080 from sg-alb          (only the load balancer can reach the app)
sg-db   : inbound 5432 from sg-app          (only app servers can reach the database)
```
- You don't need to know any IPs. When Auto Scaling adds instances that carry `sg-app`, they're allowed into the DB automatically. Exam answers prefer SG references over IP ranges.

## Troubleshooting hints
- **Timeout** when connecting → usually the SG or NACL is blocking (nothing answers).
- **Connection refused** → the network allowed you, but no app listens on that port.

## Lab task (CloudMart)
Create `sg-cloudmart-app` with inbound **8080 from your own IP only** (choose "My IP"). Attach it to the instance. You create `sg-alb` and `sg-db` in Phases 3 and 5.

Q: Are security groups stateful or stateless?
A: Stateful. Return traffic for allowed connections is automatically allowed.

Q: Can a security group contain deny rules?
A: No. Only allow rules. Use NACLs (or AWS Network Firewall/WAF) for explicit denies.

@@ 3 | TOPIC | EC2 purchasing options | On-Demand, Savings Plans, Reserved, Spot, Dedicated, Capacity Reservations | MUST
# EC2 purchasing options (a top exam topic)

| Option | Discount | Commitment | Best for |
|---|---|---|---|
| **On-Demand** | 0% | None, pay per second | Short, unpredictable workloads, testing |
| **Reserved Instances (Standard)** | up to ~72% | 1 or 3 years, a specific instance family and Region | Steady 24/7 workloads (databases) |
| **Convertible RI** | up to ~66% | 1 or 3 years, can change the family | Steady, but needs may change |
| **Compute Savings Plan** | up to ~66% | A $/hour commitment for 1 or 3 years, **any** family, Region, OS, plus Fargate and Lambda | Flexible steady usage (the modern default) |
| **EC2 Instance Savings Plan** | up to ~72% | $/hour for one instance family in one Region | Like a Standard RI, but simpler |
| **Spot Instances** | up to ~90% | None, but AWS can **reclaim with a 2-minute warning** | Fault-tolerant work: batch, CI, big data, stateless workers |
| **Dedicated Host** | — | A physical server reserved for you, with visibility of sockets/cores | **BYOL** licensing (per-socket/core), compliance |
| **Dedicated Instance** | — | Hardware not shared with other customers (but no host control) | Compliance isolation |
| **On-Demand Capacity Reservation** | 0% (combine with SP for a discount) | Reserve capacity in a **specific AZ**, any duration | Guarantee capacity for an event or DR |

**Analogy (hotel):** On-Demand = walk in tonight at full price. Reserved/Savings Plan = sign a 1-year lease, cheaper. Spot = last-minute empty rooms, very cheap, but you can be asked to leave with 2 minutes' notice. Dedicated Host = rent the whole building.

## Spot details
- A **Spot Fleet / EC2 Fleet** mixes Spot and On-Demand across many instance types. Allocation strategies: `price-capacity-optimized` (recommended), `lowest-price`, `diversified`.
- Interruption behaviour: terminate (default), stop or hibernate.
- Spot is **not** for databases or anything that can't handle interruption.

## Exam decision rules
- "Steady state 24/7, 1+ years" → **Savings Plans / Reserved**.
- "Can tolerate interruption, cheapest" → **Spot**.
- "License per core/socket, BYOL" → **Dedicated Host**.
- "Must guarantee capacity in an AZ for a short event, no long commitment" → **Capacity Reservation**.
- "Mix: baseline steady + peaks" → Savings Plan for the baseline + On-Demand/Spot for the peaks (an ASG with a mixed instances policy).

## Lab task (CloudMart)
In the console, open **EC2 → Spot Requests → Pricing history** and compare the Spot price of `t3.micro` with On-Demand. In Phase 3 the CloudMart ASG will use a **mixed instances policy** (1 On-Demand base + Spot), so note which types are cheapest.

Q: Which purchase option suits per-socket BYOL licensing?
A: Dedicated Hosts.

Q: How much warning does AWS give before reclaiming a Spot Instance?
A: Two minutes (an interruption notice through instance metadata and EventBridge).

Q: Which Savings Plan also covers Fargate and Lambda?
A: The Compute Savings Plan.

@@ 3 | TOPIC | EC2 networking: ENI, Elastic IP, placement groups | ENIs, public/private/elastic IPs, cluster/spread/partition | MUST
# EC2 networking features

## IP addresses
- **Private IP:** stays for the life of the instance (inside the VPC).
- **Public IPv4:** auto-assigned, **changes on stop/start**. AWS charges per hour for every public IPv4.
- **Elastic IP (EIP):** a static public IPv4 you own until you release it. You can remap it to another instance for quick failover. It's billed whether attached or not, so prefer DNS names or a load balancer instead.

## ENI (Elastic Network Interface)
A virtual network card: private IP(s), an EIP, a MAC address and security groups. It is **AZ-bound**. You can detach a secondary ENI and move it to another instance in the same AZ for failover (the IP and MAC move with it).
- **ENA (Elastic Network Adapter):** enhanced networking, up to 100+ Gbps. **EFA (Elastic Fabric Adapter):** for HPC/MPI with OS bypass on Linux.

## Placement groups

| Strategy | Layout | Pros | Cons | Use |
|---|---|---|---|---|
| **Cluster** | Packed close together in **one AZ** | Lowest latency, 10–100 Gbps between nodes | One rack/AZ failure hits all | HPC, tightly coupled big data |
| **Spread** | Each instance on **different hardware**, can span AZs | Max isolation. **Limit: 7 instances per AZ** per group | Small scale | A few critical instances (e.g. DB primary + standby, Kafka controllers) |
| **Partition** | Groups of instances (partitions) on separate racks, up to 7 partitions per AZ | Hundreds of instances, the topology is visible to the app | — | Hadoop, Cassandra, Kafka (rack-aware) |

## Hibernate
Saves RAM to the (encrypted) root EBS volume, so the instance resumes fast with the app already warm. Useful for slow-starting apps. RAM must be under 150 GB, and the root volume must be EBS and encrypted.

## Lab task (CloudMart)
Stop and start your instance and notice that the public IP changes. (Don't keep an EIP: it costs money while idle.) This is why CloudMart will use a load balancer's DNS name in Phase 3 instead of instance IPs.

Q: Which placement group gives the lowest network latency between instances?
A: A cluster placement group (single AZ).

Q: What is the limit of a spread placement group?
A: 7 running instances per AZ per group.

Q: Does an auto-assigned public IP survive a stop/start?
A: No. Use an Elastic IP (or a load balancer/DNS) for a stable address.

@@ 3 | TOPIC | Deploy CloudMart API on EC2 with user data | Lab milestone: jar from S3, systemd service, health check | MUST
# Lab milestone: CloudMart on one EC2 instance

## Step 1: put the jar somewhere the instance can download it
For now, create a temporary bucket `cloudmart-artifacts-<account-id>` and upload `backend/target/api.jar` (from `D:mylearnproject_aws`) to it. Add `s3:GetObject` on `arn:aws:s3:::cloudmart-artifacts-<id>/*` to `cloudmart-app-policy`. (S3 is covered properly in Phase 4. Here it is just a file store.)

```bash
aws s3 mb s3://cloudmart-artifacts-123456789012
aws s3 cp backend/target/api.jar s3://cloudmart-artifacts-123456789012/api.jar
```

## Step 2: user data script

```bash
#!/bin/bash
dnf install -y java-21-amazon-corretto-headless
mkdir -p /opt/cloudmart && cd /opt/cloudmart
aws s3 cp s3://cloudmart-artifacts-123456789012/api.jar app.jar   # uses the instance role, no keys

cat > /etc/systemd/system/cloudmart.service <<'EOF'
[Unit]
Description=CloudMart API
After=network.target
[Service]
WorkingDirectory=/opt/cloudmart
ExecStart=/usr/bin/java -jar /opt/cloudmart/app.jar
Restart=always
[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload && systemctl enable --now cloudmart
```
- The `aws s3 cp` works with **no credentials in the script**. The CLI gets temporary credentials from the instance role through IMDS. This is the exam's "use roles, not keys" rule in practice.
- `systemd` with `Restart=always` brings the app back if it crashes or the instance reboots.

## Step 3: launch and verify
1. Launch a new instance (Amazon Linux 2023, `t3.micro`, the role `cloudmart-ec2-role`, `sg-cloudmart-app`), and paste the script into **Advanced → User data**.
2. Set **Metadata version → V2 only (token required)**.
3. After about 2 minutes, run: `curl http://<public-ip>:8080/api/lab/whoami`. You should see a real `instanceId` and `az`.
4. Point the React dev server at it (`VITE_API_BASE=http://<ip>:8080`) and open the Lab page.

## Troubleshooting
- No response → check the SG (8080 from your IP?), and that the instance is in a public subnet with a public IP.
- The app didn't start → in Session Manager, run `sudo journalctl -u cloudmart -f` and `cat /var/log/cloud-init-output.log`.

## Create a golden AMI (for Phase 3)
Once it works: EC2 → Actions → Image and templates → **Create image** → `cloudmart-ami-v1`. Auto Scaling will launch from this AMI, so boot is faster because Java and the jar are already inside.

## Teardown
Keep the AMI. Terminate the instance when you finish for the day.

Q: Where do you look when a user data script fails on Amazon Linux?
A: /var/log/cloud-init-output.log (and the service logs through journalctl).

Q: Why create a golden AMI for an Auto Scaling group?
A: New instances boot faster and more consistently because the software is pre-installed, so the group scales out quicker.

@@ 2 | MODULE | EC2 Storage | EBS, instance store, EFS, FSx | MUST
Block, file and local storage choices for compute.

@@ 3 | TOPIC | EBS volumes and snapshots | Volume types, AZ-bound, snapshots, encryption, Multi-Attach | MUST
# Amazon EBS (Elastic Block Store)

EBS is a **network-attached block disk** for an EC2 instance. **Analogy:** a USB drive plugged in through the network. It survives instance stop/termination (unless "delete on termination" is set, which is the default for the root volume), and it's locked to **one AZ**.

## Volume types

| Type | Kind | Performance | Use |
|---|---|---|---|
| **gp3** | General SSD | 3,000 IOPS / 125 MB/s baseline, **IOPS and throughput settable independently** (up to 16,000 IOPS) | Default for almost everything |
| gp2 | General SSD (older) | IOPS tied to size (3 IOPS/GB, burst) | Legacy. gp3 is cheaper |
| **io2 Block Express / io1** | Provisioned IOPS SSD | Up to 256,000 IOPS, sub-ms latency, 99.999% durability (io2) | Critical databases with sustained high IOPS |
| **st1** | Throughput HDD | High MB/s, cheap | Big sequential: logs, data warehouse, Kafka |
| **sc1** | Cold HDD | Cheapest | Rarely accessed data |

- **Only SSD types (gp2/gp3/io1/io2) can be boot volumes.**
- **Multi-Attach:** io1/io2 only. Attach one volume to up to 16 Nitro instances **in the same AZ** (the app must handle concurrent writes, e.g. a cluster file system).

## Snapshots
- Point-in-time **incremental** backups stored (invisibly) in S3. They're **Regional**, so you can create a volume from one in any AZ and copy it to other Regions or accounts.
- **Move a volume to another AZ:** snapshot → create a volume in the new AZ.
- **Snapshot Archive** tier: about 75% cheaper, restore takes 24–72 h. **Recycle Bin:** protects against accidental deletion.
- **Fast Snapshot Restore (FSR):** a volume is fully performant immediately, with no lazy-loading "first-touch" latency. It costs extra.
- **Data Lifecycle Manager (DLM)** or **AWS Backup** schedule snapshots automatically.

## Encryption
- Uses KMS keys. Encrypts data at rest, data in transit between instance and volume, snapshots, and volumes made from those snapshots. Almost no performance impact.
- **Encrypt an existing unencrypted volume:** snapshot → copy the snapshot with encryption on → create a volume from it.
- Turn on **"EBS encryption by default"** per Region (the exam's best-practice answer).

## Lab task (CloudMart)
On the CloudMart instance: create a 1 GB **gp3** volume in the *same AZ*, attach it, format it and mount it at `/opt/cloudmart/data` (where the local image folder and H2 file live). Take a snapshot. Then enable **EBS encryption by default** in EC2 → Settings.

Q: Can an EBS volume be attached to an instance in a different AZ?
A: No. Snapshot it and create a new volume in the target AZ.

Q: Which EBS type lets you set IOPS and throughput independently of size at low cost?
A: gp3.

Q: How do you encrypt an existing unencrypted EBS volume?
A: Create a snapshot, copy it with encryption enabled, then create a new volume from the encrypted snapshot.

@@ 3 | TOPIC | Instance Store | Ephemeral, very fast local NVMe disk | SHOULD
# Instance store

**Instance store** = disks *physically attached* to the host machine. Extremely high IOPS and low latency, but **ephemeral**: data is lost when the instance **stops, hibernates or terminates**, or when the hardware fails. (It survives a reboot.)

**Analogy:** a whiteboard in a meeting room. It's fast to write on, but it's wiped when you leave the room.

## Use for
- Buffers, caches, scratch data, temporary files.
- Data replicated elsewhere (e.g. a Cassandra or Kafka cluster with replication).

## Exam traps
- "Highest possible IOPS for a temporary cache" → instance store (I-family instances).
- "Data must persist after stop" → **not** instance store. Use EBS.
- You can't detach it or snapshot it.

## Lab task (CloudMart)
None (micro instances have no instance store). Note in your README: "the CloudMart local image folder must not live on instance store. Phase 4 moves it to S3."

Q: Is data on an instance store volume kept after the instance is stopped?
A: No. It's lost on stop, hibernate, terminate or hardware failure. It survives only a reboot.

@@ 3 | TOPIC | Amazon EFS | Managed NFS shared across many instances and AZs | MUST
# Amazon EFS (Elastic File System)

EFS is a **managed NFS v4 file system** that **many Linux instances in many AZs mount at the same time**. It grows and shrinks automatically, and you pay per GB used.

**Analogy:** a shared network drive in an office. Every computer sees the same files.

## Key facts
- **Linux only** (POSIX). For Windows use FSx for Windows.
- Regional (multi-AZ) by default, with mount targets in each AZ. A cheaper **One Zone** option exists.
- Works with EC2, ECS, EKS and Lambda.
- Security: security groups on the mount targets, IAM authorization, **EFS Access Points** (enforce a user and a root directory per app), and encryption at rest (KMS) and in transit (TLS).

## Performance and throughput
- Performance modes: **General Purpose** (default, low latency) and Max I/O (legacy, for highly parallel work).
- Throughput modes: **Elastic** (recommended, scales automatically, pay per use), Provisioned, Bursting.

## Storage classes and lifecycle
Standard → **Infrequent Access (IA)** → **Archive**, moved automatically by lifecycle policies (e.g. after 30 days without access). This saves a lot on cold files.

## EBS vs EFS vs instance store

| | EBS | EFS | Instance store |
|---|---|---|---|
| Attach | 1 instance (Multi-Attach io2 only) | **Thousands, across AZs** | 1 instance |
| Scope | AZ | Region | Host |
| Persistence | Yes | Yes | No |
| Price | Low | Higher per GB | Included |

## Lab task (CloudMart)
Optional (it costs very little): create an EFS file system and mount it at `/mnt/efs` on the instance, using the `amazon-efs-utils` package. Write a file. Think about why sharing `data/images` over EFS would work for two instances, and why S3 (Phase 4) is still the better choice for user uploads: cheaper, CDN-friendly and presigned URLs.

Q: Which storage can be mounted by hundreds of Linux EC2 instances across multiple AZs simultaneously?
A: Amazon EFS.

Q: Can Windows instances mount EFS?
A: No. EFS is NFS/Linux only. Use FSx for Windows File Server.

@@ 3 | TOPIC | Amazon FSx family | Windows File Server, Lustre, NetApp ONTAP, OpenZFS | SHOULD
# Amazon FSx

FSx gives you **fully managed third-party file systems**.

| FSx flavour | What it is | Choose when |
|---|---|---|
| **FSx for Windows File Server** | SMB shares, NTFS, **Active Directory** integration, DFS | Windows apps, home directories, SharePoint, SQL Server file shares. Multi-AZ option |
| **FSx for Lustre** | Parallel file system for **HPC/ML**, hundreds of GB/s | Machine learning training, genomics, financial modelling. **Links to S3**: reads S3 objects as files and writes results back |
| **FSx for NetApp ONTAP** | NetApp features: NFS + SMB + iSCSI, snapshots, cloning, dedup | Lift-and-shift NetApp workloads, multi-protocol access |
| **FSx for OpenZFS** | ZFS, NFS, snapshots, very low latency | Migrating ZFS/Linux NFS workloads |

## Lustre deployment types
- **Scratch:** temporary, no replication, cheapest and fastest. For short processing jobs.
- **Persistent:** data replicated within the AZ. For long-running workloads.

## Exam keywords
- "Windows", "SMB", "Active Directory", "NTFS" → **FSx for Windows**.
- "HPC", "high-performance parallel file system", "ML training data in S3" → **FSx for Lustre**.
- "NetApp", "multi-protocol NFS + SMB + iSCSI" → **FSx for ONTAP**.

## Lab task (CloudMart)
None. These are expensive and Windows/HPC specific. Make sure you can answer the flashcards.

Q: A Windows application needs a shared file system integrated with Active Directory. Which service?
A: Amazon FSx for Windows File Server.

Q: Which file system suits HPC jobs that read training data from S3?
A: Amazon FSx for Lustre (linked to an S3 bucket).
