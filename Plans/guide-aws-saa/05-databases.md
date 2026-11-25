@@ 1 | MODULE | Phase 5 — Databases & Caching | RDS, Aurora, ElastiCache, DynamoDB and choosing the right database | MUST
# Phase 5: Databases and caching

The exam constantly asks you to **pick the right database** and make it **highly available, scalable and fast**.

**CloudMart goal this phase:** move from H2 to **RDS PostgreSQL (Multi-AZ)** with a **read replica** for catalog reads, add **ElastiCache** for product caching, and move carts to **DynamoDB** with TTL.

@@ 2 | MODULE | Relational: RDS and Aurora | Managed SQL, Multi-AZ, replicas, backups, Aurora features | MUST
Managed relational databases.

@@ 3 | TOPIC | Amazon RDS basics | Managed engines, what AWS manages, storage autoscaling, parameter groups | MUST
# Amazon RDS (Relational Database Service)

**RDS** runs a relational database for you. Engines: **PostgreSQL, MySQL, MariaDB, Oracle, SQL Server, Db2**, plus Aurora (next topics).

**AWS manages:** provisioning, OS and engine patching (in your maintenance window), automated backups, Multi-AZ failover, monitoring and storage scaling.
**You manage:** schema, queries, DB users, instance size, security groups, parameters and encryption choices.
**You can't:** SSH into the host. **RDS Custom** (Oracle and SQL Server only) gives OS access when you need custom agents or patches.

## Important features
- **Storage autoscaling:** storage grows automatically when it runs low (you set a maximum).
- **Parameter groups:** engine settings (e.g. `max_connections`, `rds.force_ssl=1`). **Option groups:** engine add-ons (Oracle TDE, SQL Server features).
- **Instance classes:** `db.t4g` (burstable, cheap), `db.m` (general), `db.r` (memory).
- **Storage:** gp3 (default) or io1/io2 for high IOPS.
- **Performance Insights:** shows which SQL statements cause load. **Enhanced Monitoring:** OS-level metrics.
- **Stopping an instance** lasts at most 7 days, then RDS starts it again automatically.

## Connecting securely
- Put RDS in **private subnets** (a DB subnet group with 2+ AZs). It should not be publicly accessible.
- `sg-db` allows the port **only from `sg-app`**.
- **TLS in transit:** `rds.force_ssl` / `require_secure_transport`.
- **IAM database authentication** (MySQL/PostgreSQL): log in with a short-lived token generated from an IAM role instead of a password.

## Lab task (CloudMart)
Create `sg-db` (5432 from `sg-cloudmart-app`). Create a DB subnet group over 2 private subnets. In the default VPC, use two subnets and keep **"Publicly accessible: No"**. Create **RDS PostgreSQL 16, `db.t4g.micro`, 20 GB gp3, single-AZ first** (Free Tier eligible), database name `cloudmart`, tag `Project=cloudmart`. Store the master password safely. Phase 9 moves it to Secrets Manager.

Q: Can you SSH into an RDS instance?
A: No (except with RDS Custom for Oracle/SQL Server). AWS manages the OS.

Q: Which feature lets an app connect to RDS MySQL/PostgreSQL using IAM instead of a password?
A: IAM database authentication (short-lived auth tokens).

@@ 3 | TOPIC | RDS Multi-AZ vs Read Replicas | Synchronous standby for HA vs asynchronous replicas for read scaling | MUST
# Multi-AZ vs read replicas (a top-5 exam topic)

| | **Multi-AZ (instance)** | **Multi-AZ DB cluster** | **Read replica** |
|---|---|---|---|
| Purpose | **High availability / DR** | HA **and** readable standbys | **Read scaling** (and cross-Region DR) |
| Replication | **Synchronous** | Semi-synchronous | **Asynchronous** (replica lag) |
| Standby readable? | **No** | Yes (2 readable standbys) | Yes (read-only) |
| Failover | **Automatic** (about 60–120 s), DNS endpoint stays the same | Automatic, about 35 s | **Manual promote** (it becomes a standalone DB) |
| Location | Another AZ, same Region | 3 AZs | Same AZ, cross-AZ or **cross-Region** |
| Count | 1 standby | 2 readers | Up to 15 (MySQL/PG/MariaDB) |

**Analogy:** Multi-AZ is a **spare tyre**. You can't drive on it until the main one fails, but swapping is automatic. Read replicas are **extra delivery vans** that only take read orders, so the main van isn't overloaded.

## Key facts
- Multi-AZ failover is triggered by AZ outage, primary failure, instance type change or patching. The app reconnects to the **same endpoint**.
- Converting single-AZ → Multi-AZ is **zero downtime** (a snapshot is restored as the standby, then sync begins).
- Read replicas need the app to send reads to the **reader endpoint**, a separate connection string.
- Replication traffic **within the same Region is free**. Cross-Region replication costs per GB.
- A read replica can itself be Multi-AZ (for DR of the replica).

## Exam traps
- "Improve read performance for reporting queries" → **read replica** (not Multi-AZ).
- "Survive an AZ failure with automatic failover" → **Multi-AZ**.
- "DR in another Region for RDS" → **cross-Region read replica** (promote on disaster) or cross-Region automated backups.

## Lab task (CloudMart)
1. Modify the DB → enable **Multi-AZ** (it's not free, so do it for the lab and turn it off afterwards).
2. Create a **read replica** `cloudmart-replica`.
3. In Spring Boot, add a second DataSource for reads, or simply a `cloudmart.readUrl` used by `ProductQueryService` with `@Transactional(readOnly=true)`. The Lab page's `db` column shows which endpoint served the request.
4. **Failover test:** RDS → Actions → **Reboot with failover**. Watch the Lab page: requests fail for about a minute and then continue on the same endpoint.

Q: Is the RDS Multi-AZ standby readable (classic Multi-AZ instance deployment)?
A: No. It exists only for failover. Use read replicas (or a Multi-AZ DB cluster) for reads.

Q: Is read replica replication synchronous or asynchronous?
A: Asynchronous, so there can be replication lag.

Q: Reporting queries slow down the primary DB. Best fix?
A: Create a read replica and point the reporting queries at it.

@@ 3 | TOPIC | RDS backups, snapshots, encryption and RDS Proxy | PITR, manual snapshots, encrypting existing DBs, connection pooling | MUST
# RDS backups, encryption and RDS Proxy

## Backups
- **Automated backups:** daily snapshot + transaction logs → **point-in-time restore (PITR)** to any second within the retention period (1–35 days, 0 = disabled).
- **Manual snapshots:** kept until you delete them. Use them for long-term retention, or before risky changes.
- **A restore always creates a NEW DB instance** with a new endpoint.
- Copy snapshots across Regions and share them with other accounts (an encrypted snapshot needs its KMS key shared too).
- Cheap trick for an unused DB: snapshot → delete → restore later (stopping lasts only 7 days).

## Encryption
- **At rest:** KMS, chosen **at creation**. It covers storage, backups, snapshots and replicas.
- **You can't encrypt an existing unencrypted DB in place.** Snapshot → **copy the snapshot with encryption** → restore from the encrypted copy.
- Read replicas of an encrypted DB are encrypted. An unencrypted primary can't have an encrypted replica.
- **In transit:** TLS (force it with a parameter).

## RDS Proxy
A fully managed **connection pool** in front of RDS/Aurora.
- Lambda functions or bursty apps can open thousands of connections and exhaust `max_connections`. **RDS Proxy pools and reuses them.**
- Cuts failover time by up to **66%** (the proxy keeps client connections open while the DB fails over).
- Can enforce **IAM authentication** and read credentials from **Secrets Manager**.
- It's never publicly accessible (VPC only).

## Exam patterns
- "Lambda functions overwhelm the database with connections" → **RDS Proxy**.
- "Encrypt an existing RDS database" → snapshot → encrypted copy → restore.
- "Restore the DB to how it was 10 minutes ago" → **PITR**.

## Lab task (CloudMart)
Take a manual snapshot `cloudmart-before-phase5`. Insert some products, then do a **PITR** to 5 minutes earlier, creating a new instance `cloudmart-pitr`. Confirm the new products are missing there, then delete `cloudmart-pitr`. Note that RDS Proxy is what you'd add in Phase 8 if the Lambda order-worker wrote to RDS.

Q: How do you encrypt an existing unencrypted RDS instance?
A: Snapshot it, copy the snapshot with encryption enabled, and restore a new instance from the encrypted copy.

Q: Many Lambda invocations exhaust database connections. Solution?
A: Use Amazon RDS Proxy to pool and share connections.

Q: What does restoring an RDS backup create?
A: A new DB instance with a new endpoint.

@@ 3 | TOPIC | Amazon Aurora | Cloud-native MySQL/PostgreSQL: shared storage, endpoints, Serverless v2, Global Database | MUST
# Amazon Aurora

**Aurora** is AWS's cloud-native relational engine, **MySQL- and PostgreSQL-compatible**, with up to 5x MySQL / 3x PostgreSQL throughput.

## Architecture
- Compute (instances) is separate from a **shared, distributed storage volume**.
- Storage keeps **6 copies across 3 AZs**. It tolerates losing 2 copies for writes and 3 for reads, self-heals, and **auto-grows in 10 GB steps up to 128 TB**.
- **1 writer + up to 15 Aurora Replicas** that read the *same* storage, so replica lag is usually < 10 ms.
- **Failover:** a replica is promoted automatically in about 30 s (you set the promotion priority **tier 0–15**).

## Endpoints
| Endpoint | Points to |
|---|---|
| **Cluster (writer) endpoint** | The current primary, for writes |
| **Reader endpoint** | **Load-balances across replicas** |
| **Custom endpoint** | A subset of instances (e.g. the large ones for analytics) |
| Instance endpoint | One specific instance (rarely used) |

## Special features
- **Aurora Serverless v2:** capacity scales in fine steps (ACUs) within seconds. For variable or unpredictable workloads, dev/test, multi-tenant SaaS. "Least operational overhead relational DB with unpredictable load" → this.
- **Aurora Global Database:** 1 primary Region + up to 10 secondary read-only Regions, storage-level replication **lag < 1 s**, **promote a secondary in < 1 minute (RTO)**, RPO about 1 s. The exam's answer for **global low-latency reads + Region DR**.
- **Backtrack** (MySQL): "rewind" the DB in place to an earlier time without a restore.
- **Cloning:** a fast copy-on-write clone for testing on production data.
- **Aurora I/O-Optimized:** no per-I/O charges, for I/O-heavy workloads.
- **Machine learning integration:** call SageMaker or Comprehend from SQL. **Zero-ETL** integration to Redshift.

## RDS vs Aurora
Pick Aurora for higher performance, faster failover, more replicas, Global Database and Serverless. RDS can be cheaper for small, steady workloads, and is required for Oracle/SQL Server/Db2.

## Lab task (CloudMart)
Optional (it costs more than RDS micro): create an **Aurora PostgreSQL Serverless v2** cluster (0.5–1 ACU), point `DB_URL` at the **cluster endpoint** and the read datasource at the **reader endpoint**, and repeat the failover test (Actions → Failover) while watching the Lab page. Delete it afterwards and keep RDS as CloudMart's main database.

Q: How many copies of data does Aurora keep, across how many AZs?
A: 6 copies across 3 AZs.

Q: Which Aurora feature provides cross-Region DR with replication lag under 1 second?
A: Aurora Global Database.

Q: Which Aurora endpoint load-balances read traffic across replicas?
A: The reader endpoint.

@@ 2 | MODULE | Caching and NoSQL | ElastiCache, DynamoDB, DAX | MUST
Fast in-memory and key-value data.

@@ 3 | TOPIC | Amazon ElastiCache | Redis/Valkey vs Memcached, caching strategies, session store | MUST
# Amazon ElastiCache

**ElastiCache** = managed in-memory cache: **Redis OSS / Valkey** or **Memcached**. It gives sub-millisecond reads, takes load off databases and stores sessions so apps can be stateless.

**Analogy:** the sticky notes on your monitor. You write down the answers you look up most, so you don't walk to the archive (the database) every time.

## Redis/Valkey vs Memcached

| | Redis / Valkey | Memcached |
|---|---|---|
| Data types | Strings, hashes, lists, sets, **sorted sets** (leaderboards), streams | Simple key/value |
| HA | **Replication, Multi-AZ automatic failover** | None (data lost if a node dies) |
| Persistence / backup | Snapshots, restore | No |
| Scaling | Cluster mode (sharding) + replicas | Multi-node sharding, **multi-threaded** |
| Pick when | Almost always: HA, sessions, leaderboards, pub/sub | Simplest cache, multi-threaded, data loss is OK |

**ElastiCache Serverless** removes capacity planning. **Amazon MemoryDB** is a *durable* Redis-compatible primary database (Multi-AZ transaction log), for when the in-memory store must be the source of truth.

## Caching strategies
- **Lazy loading (cache-aside):** read the cache; on a miss, read the DB and write the result to the cache. Only requested data gets cached, but the first read is slow and the data can be stale.
- **Write-through:** write to the DB and the cache together. Data is fresh, but you cache things nobody reads.
- **TTL:** always set an expiry to limit staleness and memory use.

```java
@Service
class ProductQueryService {
    private final ProductRepository repo;
    private final RedisTemplate<String, Product> redis;

    Product get(long id) {
        String key = "product:" + id;
        Product cached = redis.opsForValue().get(key);
        if (cached != null) return cached;                       // cache hit: sub-ms
        Product p = repo.findById(id).orElseThrow();             // cache miss: go to RDS
        redis.opsForValue().set(key, p, Duration.ofMinutes(10)); // lazy-load with a 10-minute TTL
        return p;
    }
}
```
- This is cache-aside. On product update, **delete** `product:<id>` so the next read reloads fresh data. (Or use Spring's `@Cacheable`/`@CacheEvict` with `spring-boot-starter-data-redis`.)

## Security
Runs in a VPC (security group `sg-cache` with 6379 from `sg-app`), with in-transit TLS, at-rest encryption, and Redis AUTH or IAM authentication.

## Exam patterns
- "Reduce read load and latency on RDS for repeated queries" → **ElastiCache** (or a read replica if the queries are diverse and need fresh data).
- "Store user sessions so the app is stateless" → **ElastiCache Redis** or DynamoDB.
- "Real-time leaderboard" → **Redis sorted sets**.

## Lab task (CloudMart)
Create an ElastiCache **Valkey/Redis serverless cache** (or a `cache.t4g.micro` node) in the CloudMart VPC with `sg-cache`. Add `spring-boot-starter-data-redis`, implement `RedisCacheService` behind the `CacheService` port (`cloudmart.cache=redis`), and add a `cacheHit` flag to the product API response. Refresh a product page twice and see `false` then `true`.

Q: Which ElastiCache engine supports replication, Multi-AZ failover and persistence?
A: Redis OSS / Valkey (Memcached has none of these).

Q: What is lazy loading (cache-aside)?
A: The app checks the cache first; on a miss it reads the DB and populates the cache.

@@ 3 | TOPIC | Amazon DynamoDB core | Serverless NoSQL: keys, capacity modes, GSI/LSI, consistency | MUST
# Amazon DynamoDB

**DynamoDB** is a **fully managed, serverless key-value and document NoSQL database** with single-digit-millisecond latency at any scale. Data is replicated across 3 AZs automatically.

## Data model
- **Table** → **items** (rows, up to **400 KB** each) → **attributes**.
- **Primary key:** either a **partition key** only (e.g. `sessionId`), or a **partition key + sort key** (e.g. `customerId` + `orderDate`) to query ranges within a partition.
- No fixed schema apart from the key. Choose a partition key with **high cardinality** to avoid "hot partitions".

## Capacity modes
| Mode | How you pay | Use |
|---|---|---|
| **On-demand** | Per request | **Unpredictable / new / spiky** workloads. No planning |
| **Provisioned** (+ auto scaling) | RCU/WCU per hour | Predictable traffic, cheaper at steady load. Reserved capacity available |

1 WCU = one 1 KB write/s. 1 RCU = one strongly consistent 4 KB read/s, or two eventually consistent ones.

## Reads
- **Eventually consistent** (the default, half the cost) or **strongly consistent** (on request).
- **Query** = by key (efficient). **Scan** = read the whole table (expensive, avoid).

## Secondary indexes
| | **GSI** (Global) | **LSI** (Local) |
|---|---|---|
| Key | **Any** partition + sort key | **Same partition key**, different sort key |
| When | Add **any time** | **Only at table creation** |
| Capacity | Its own | Shares the table's |
| Consistency | Eventual only | Strong possible |

## Security
IAM policies (fine-grained, down to item or attribute level with `dynamodb:LeadingKeys`), encryption at rest always on (AWS owned or KMS key), and a **VPC gateway endpoint** for private access.

## Lab task (CloudMart)
Create table **`cloudmart-carts`**: partition key `sessionId` (String), **on-demand** capacity, tag `Project=cloudmart`. Add `software.amazon.awssdk:dynamodb-enhanced`, implement `DynamoCartRepository` (`cloudmart.cart=dynamodb`), and add `dynamodb:GetItem/PutItem/UpdateItem/DeleteItem` on the table ARN to `cloudmart-app-policy`.

Q: What is the maximum DynamoDB item size?
A: 400 KB.

Q: Which DynamoDB index can be added after table creation?
A: A Global Secondary Index (LSIs only at creation).

Q: Which capacity mode suits an unpredictable, spiky new workload?
A: On-demand.

@@ 3 | TOPIC | DynamoDB advanced features | TTL, Streams, DAX, Global Tables, PITR, export to S3 | MUST
# DynamoDB advanced features

| Feature | What it does | Exam trigger |
|---|---|---|
| **TTL** | Deletes items automatically after an epoch timestamp attribute passes (no cost) | "Expire sessions/carts automatically" |
| **DynamoDB Streams** | An ordered, 24-hour log of item changes (insert/modify/remove). Triggers **Lambda** | "React to table changes", replicate, audit, send notifications |
| **DAX** (DynamoDB Accelerator) | An in-memory cache *in front of DynamoDB*. **Microseconds**, API-compatible (minimal code change) | "Read-heavy DynamoDB needs microsecond latency" |
| **Global Tables** | **Multi-Region, multi-active** replication (read and write in every Region), lag ~1 s | "Low-latency global writes", multi-Region DR for NoSQL |
| **PITR** | Continuous backups, restore to any second in the last 35 days | "Protect against accidental writes/deletes" |
| **On-demand backups** | Full backups kept until deleted (also through AWS Backup) | Long-term retention |
| **Export to S3** | Export from PITR data without using read capacity → query with Athena | Analytics on table data |
| **Transactions** | ACID across multiple items/tables | "All-or-nothing writes" |
| **Kinesis Data Streams for DynamoDB** | Change data → Kinesis (longer retention, more consumers) | Streams beyond 24 h / many consumers |

## DAX vs ElastiCache
- **DAX:** only for DynamoDB, and transparent (it caches GetItem/Query results).
- **ElastiCache:** any data, any computed result. You write the cache logic.

## Lab task (CloudMart)
1. Enable **TTL** on `cloudmart-carts` with attribute `expiresAt`. Make the cart repository set `expiresAt = now + 24 h` (epoch seconds), so abandoned carts disappear for free.
2. Enable **PITR** on the table.
3. Optional: enable **Streams** (NEW_IMAGE). In Phase 8 a Lambda could react to carts (e.g. an "abandoned cart" email) as a stretch goal.

Q: How do you automatically delete DynamoDB items after a certain time at no cost?
A: Enable Time To Live (TTL) on an attribute holding an expiry epoch timestamp.

Q: Which feature provides microsecond read latency for DynamoDB with minimal code changes?
A: DynamoDB Accelerator (DAX).

Q: An app needs active-active writes in multiple Regions on a NoSQL database. Which feature?
A: DynamoDB Global Tables.

@@ 3 | TOPIC | Other databases and choosing the right one | DocumentDB, Neptune, Keyspaces, Timestream, Redshift, OpenSearch, MemoryDB | MUST
# Purpose-built databases and how to choose

| Need | Service |
|---|---|
| Relational, transactions, joins, existing SQL app | **RDS** / **Aurora** |
| Key-value at any scale, serverless, single-digit ms | **DynamoDB** |
| In-memory cache / sessions | **ElastiCache** |
| Durable in-memory primary DB (Redis API) | **MemoryDB** |
| **MongoDB-compatible** document database | **DocumentDB** |
| **Graph** relationships (social networks, fraud rings, recommendations) | **Neptune** |
| **Apache Cassandra**-compatible wide-column | **Keyspaces** |
| **Time-series** (IoT sensors, metrics) | **Timestream** |
| **Data warehouse**, OLAP analytics with SQL on petabytes | **Redshift** (Phase 11) |
| Full-text **search**, log analytics | **OpenSearch Service** (Phase 11) |
| Immutable, cryptographically verifiable ledger | QLDB is being retired. Use Aurora PostgreSQL with audit tables, or blockchain (Managed Blockchain) |

## Choosing questions to ask
1. **Data model:** relational, key-value, document, graph, time-series?
2. **Access pattern:** OLTP (many small transactions) or OLAP (big scans and aggregations)?
3. **Scale and latency:** millions of requests/s at ms latency → DynamoDB.
4. **Operations:** "least operational overhead" → serverless options (DynamoDB, Aurora Serverless v2, ElastiCache Serverless).
5. **Compatibility:** "migrate MongoDB with minimal changes" → DocumentDB. "Cassandra" → Keyspaces.

## Lab task (CloudMart)
In your README, write a "database decision record" for CloudMart: products/orders → RDS (relational, transactions), carts → DynamoDB (key-value, TTL, scale), product cache → ElastiCache. Then name which service you'd add for (a) product search, (b) "customers who bought X also bought Y", (c) clickstream analytics. Answers: OpenSearch, Neptune, Redshift/Athena.

Q: Which database suits highly connected data such as social networks or fraud detection graphs?
A: Amazon Neptune.

Q: A company wants to move its MongoDB workload to a managed AWS service with minimal changes. Which?
A: Amazon DocumentDB (MongoDB-compatible).

Q: Which service is a petabyte-scale data warehouse for OLAP analytics?
A: Amazon Redshift.

@@ 3 | TOPIC | Lab milestone: CloudMart data tier on AWS | RDS Multi-AZ + replica, ElastiCache, DynamoDB carts, failover test | MUST
# Lab milestone: the data tier

## Checklist
- [ ] RDS PostgreSQL in private subnets, `sg-db` only from `sg-cloudmart-app`, encrypted, automated backups 7 days, tag `Project=cloudmart`.
- [ ] CloudMart `aws` profile: `DB_URL=jdbc:postgresql://<writer-endpoint>:5432/cloudmart`, plus `cloudmart.readUrl` pointing at the read replica.
- [ ] `cloudmart.cache=redis` with ElastiCache; the product endpoint shows `cacheHit`.
- [ ] `cloudmart.cart=dynamodb` with TTL `expiresAt`; PITR on.
- [ ] Orders are still processed locally (in-process events). SQS replaces this in Phase 7.

## Pass the config to the ASG instances
Put the environment variables in the launch template user data for now:

```bash
cat > /opt/cloudmart/env <<'EOF'
SPRING_PROFILES_ACTIVE=aws
DB_URL=jdbc:postgresql://cloudmart.xxxx.ap-south-1.rds.amazonaws.com:5432/cloudmart
SPRING_DATASOURCE_USERNAME=cloudmart
SPRING_DATASOURCE_PASSWORD=change-me-phase9-moves-this-to-secrets-manager
CLOUDMART_CART=dynamodb
CLOUDMART_CACHE=redis
EOF
# and add to the systemd unit:  EnvironmentFile=/opt/cloudmart/env
```
- Spring Boot maps `CLOUDMART_CART` to the `cloudmart.cart` property (relaxed binding).
- A password in user data is **visible** to anyone who can read the launch template. This is deliberately "bad" so that in Phase 9 you fix it with Secrets Manager and see why.

## Experiments
1. **RDS failover:** "Reboot with failover" while the Lab page runs. Note the downtime in seconds.
2. **Cache effect:** hit the same product 100 times (`for i in $(seq 100); do curl -s .../api/products/1 >/dev/null; done`) and compare the RDS `ReadIOPS`/connections in CloudWatch with and without the cache.
3. **TTL:** set a cart's `expiresAt` to the past and watch it vanish (within minutes to hours; TTL deletion is a background process).

## Teardown
RDS Multi-AZ and the replica are the most expensive things so far. Delete the replica, turn off Multi-AZ (or snapshot and delete the DB), and delete the ElastiCache node. DynamoDB on-demand costs nothing when idle.

Q: Why should database passwords not be in EC2 user data?
A: Anyone with permission to view the launch template or instance attributes can read user data. Use Secrets Manager or SSM Parameter Store SecureString, fetched at runtime with the instance role.
