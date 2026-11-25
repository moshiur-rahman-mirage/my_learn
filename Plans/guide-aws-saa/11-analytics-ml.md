@@ 1 | MODULE | Phase 11 — Data Analytics & Machine Learning Services | Athena, Glue, Lake Formation, Redshift, EMR, OpenSearch, QuickSight, MSK, AI services | SHOULD
# Phase 11: Analytics and ML services

For the Associate exam you need to **pick the right analytics or AI service from a scenario**, not build ML models.

**CloudMart goal this phase:** turn the clickstream (Firehose → S3 from Phase 7) into a **data lake**: catalog it with **Glue**, query it with **Athena**, visualize it in **QuickSight**, and tag product images automatically with **Rekognition**.

@@ 2 | MODULE | Analytics Services | Query, transform, warehouse, search and visualize data | SHOULD
From raw data to insight.

@@ 3 | TOPIC | Amazon Athena | Serverless SQL on S3, pay per TB scanned, partitioning and columnar formats | MUST
# Amazon Athena

**Athena** runs **standard SQL directly on files in S3** (CSV, JSON, **Parquet, ORC**, Avro). It's **serverless**: nothing to provision, and you **pay per TB scanned** (about $5/TB).

**Analogy:** asking questions of a filing cabinet without first moving the files into a database.

## Make it fast and cheap
- **Columnar formats (Parquet/ORC) + compression**: scan only the needed columns (often 90% less data).
- **Partitioning** by date (`s3://.../clicks/year=2026/month=11/day=19/`) with `WHERE` on the partition columns, so it scans only those folders. **Partition projection** avoids partition-maintenance overhead.
- Bigger files (> 128 MB) are better than millions of tiny ones.
- **Federated queries:** query RDS, DynamoDB, Redshift, CloudWatch Logs and others through Lambda connectors.
- Results go to an S3 output location. **Workgroups** set per-team limits and cost controls.

## Common uses (exam)
- Ad-hoc analysis of **logs in S3**: ALB access logs, CloudTrail, VPC Flow Logs, CUR billing data.
- "Query data in S3 with SQL, **serverless**, least overhead" → **Athena**.

## Lab task (CloudMart)
1. Create a Glue database `cloudmart` (next topic: the crawler creates the table automatically). Or create the table manually in the Athena editor:
```sql
CREATE EXTERNAL TABLE cloudmart.clicks (
  event string, productId bigint, sessionId string, ts string)
PARTITIONED BY (dt string)
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
LOCATION 's3://cloudmart-datalake-123456789012/clicks/'
TBLPROPERTIES ('projection.enabled'='true', 'projection.dt.type'='date',
               'projection.dt.format'='yyyy/MM/dd', 'projection.dt.range'='2026/01/01,NOW',
               'storage.location.template'='s3://cloudmart-datalake-123456789012/clicks/${dt}/');
```
- The table only *describes* the JSON files, and nothing is copied. **Partition projection** maps `dt` onto Firehose's default `yyyy/MM/dd/HH` folders (the hour folders sit underneath and are read too), so a `WHERE dt = '2026/11/19'` scans only that day.
2. Query the top products: `SELECT productId, count(*) views FROM cloudmart.clicks WHERE dt >= '2026/11/01' GROUP BY 1 ORDER BY 2 DESC LIMIT 10;`

Q: How do you reduce Athena query cost?
A: Use columnar formats (Parquet/ORC), compression and partitioning, so less data is scanned.

Q: Which service runs serverless SQL queries directly on data in S3?
A: Amazon Athena.

@@ 3 | TOPIC | AWS Glue and Lake Formation | Data Catalog, crawlers, serverless ETL; data lake permissions | MUST
# AWS Glue and Lake Formation

## AWS Glue
A serverless **data integration (ETL)** service.
- **Glue Data Catalog:** a central metadata store (databases, tables, schemas) used by **Athena, Redshift Spectrum, EMR and Lake Formation**. It's Hive metastore compatible.
- **Crawlers:** scan S3, JDBC or DynamoDB sources, infer schemas and create/update catalog tables and partitions automatically.
- **Glue ETL jobs:** Spark (Python/Scala) or Python shell, serverless. Transform data, e.g. **convert JSON/CSV to Parquet**, join and clean it.
- **Glue Studio** (visual ETL), **DataBrew** (no-code data cleaning), **Data Quality**, **job bookmarks** (process only new data), triggers and workflows.
- Exam: "convert CSV in S3 to Parquet on a schedule, serverless" → **Glue ETL job**. "Discover the schema and make the data queryable in Athena" → **Glue crawler**.

## AWS Lake Formation
Builds a **secure data lake** on S3 quickly, on top of the Glue Data Catalog.
- Ingest data with blueprints, and **centrally manage fine-grained permissions**: database, table, **column, row and cell level**, plus **tag-based access control**.
- One place to grant "analysts can read `orders` but not the `email` column" across Athena, Redshift Spectrum, EMR and Glue.
- Cross-account data sharing.
- Exam: "column-level/row-level security on a data lake for multiple analytics services" → **Lake Formation**.

## Lab task (CloudMart)
1. Create a **Glue crawler** on `s3://cloudmart-datalake-<id>/clicks/` → database `cloudmart`. Run it and look at the inferred table and partitions.
2. Stretch: in Glue Studio, a visual job JSON clicks → Parquet at `s3://.../clicks-parquet/` partitioned by date. Compare the Athena "data scanned" for the same query on JSON vs Parquet.

Q: What does a Glue crawler do?
A: It scans data stores, infers schemas and creates or updates tables and partitions in the Glue Data Catalog.

Q: Which service provides column- and row-level permissions across a data lake?
A: AWS Lake Formation.

@@ 3 | TOPIC | Amazon Redshift | Columnar MPP data warehouse, Serverless, Spectrum, concurrency scaling, zero-ETL | MUST
# Amazon Redshift

**Redshift** is a petabyte-scale **data warehouse** for **OLAP** (analytics, BI reports, aggregations over huge tables) using SQL. It's based on PostgreSQL, but **columnar** and **massively parallel (MPP)**.

## Key points
- **Provisioned clusters** (leader node + compute nodes, RA3 nodes with managed storage) or **Redshift Serverless** (pay per use, auto-scaling, least overhead).
- **Redshift Spectrum:** query data **in S3** directly from Redshift, without loading it, and join it with warehouse tables.
- **Concurrency scaling:** adds transient capacity automatically for bursts of queries.
- **Workload management (WLM):** queues/priorities for different user groups.
- **Snapshots:** automated and manual to S3, **cross-Region snapshot copy** for DR. Multi-AZ deployments for RA3.
- **Loading data:** `COPY` from S3 (the fastest, parallel), Firehose, DMS, and **zero-ETL integrations** from Aurora/RDS/DynamoDB.
- **Materialized views**, data sharing across clusters, Redshift ML.

## Athena vs Redshift vs RDS
| | Athena | Redshift | RDS/Aurora |
|---|---|---|---|
| Workload | Ad-hoc queries on S3 | **Complex, frequent BI/analytics on large structured data** | **OLTP** transactions |
| Infrastructure | Serverless | Cluster or Serverless | Instances |
| Cost model | Per TB scanned | Per node-hour or RPU-hour | Per instance-hour |

## Exam patterns
- "Run complex analytical queries and BI dashboards on petabytes of structured data" → **Redshift**.
- "Occasional queries on S3 logs" → **Athena** (cheaper).
- "Query S3 data from the warehouse without loading it" → **Redshift Spectrum**.

## Lab task (CloudMart)
Optional (Serverless has free trial credits for new users): create a **Redshift Serverless** workgroup, use **Spectrum** (an external schema pointing at the Glue `cloudmart` database), and query the clicks table from Redshift. Delete the workgroup afterwards.

Q: Which service is a columnar data warehouse for OLAP analytics on petabytes?
A: Amazon Redshift.

Q: How can Redshift query data in S3 without loading it?
A: Redshift Spectrum.

@@ 3 | TOPIC | Amazon EMR, OpenSearch, QuickSight and MSK | Big data clusters, search/log analytics, BI dashboards, managed Kafka | SHOULD
# More analytics services

## Amazon EMR (Elastic MapReduce)
Managed **big data frameworks**: **Hadoop, Spark, Hive, HBase, Presto/Trino, Flink**, on EC2 clusters (or EMR Serverless, or EMR on EKS).
- Clusters: primary node, core nodes (HDFS + tasks), **task nodes (often Spot)**.
- Exam: "migrate existing Hadoop/Spark jobs", "petabyte-scale processing with open-source frameworks" → **EMR**. (Purely serverless ETL with less control → Glue.)

## Amazon OpenSearch Service
A managed **OpenSearch/Elasticsearch** cluster (or OpenSearch Serverless).
- **Full-text search**, log analytics, real-time dashboards (OpenSearch Dashboards).
- Common pattern: DynamoDB/RDS is the source of truth, and OpenSearch is the **search index** (synced through DynamoDB Streams → Lambda, or zero-ETL).
- Logs: CloudWatch Logs subscription / Firehose → OpenSearch.
- Exam: "search products by keywords, fuzzy matching", "analyze logs interactively" → **OpenSearch**.

## Amazon QuickSight
Serverless **BI dashboards** and visualizations, with ML insights, natural-language Q&A (Amazon Q), embedding into apps, and per-user pricing. It connects to Athena, Redshift, RDS, S3 and SaaS sources. SPICE is its in-memory engine.
- Exam: "create interactive dashboards for business users" → **QuickSight**.

## Amazon MSK (Managed Streaming for Apache Kafka)
Managed **Apache Kafka** (provisioned or **MSK Serverless**). For apps that already use the Kafka API and its ecosystem (Kafka Connect, Streams).
- Kinesis vs MSK: Kinesis is AWS-native and simpler. MSK is for Kafka compatibility, larger messages (configurable beyond 1 MB) and Kafka tooling.

## Typical analytics pipeline (memorize the shape)

```text
Ingest            Store           Catalog/Transform     Query/Analyze         Visualize
Kinesis/Firehose -> S3 data lake -> Glue crawler/ETL  -> Athena / Redshift  -> QuickSight
DMS (databases)                     Lake Formation       EMR (Spark)
```

## Lab task (CloudMart)
1. **QuickSight:** sign up for the trial (Standard edition), add an Athena data source, create a dataset from `cloudmart.clicks`, and build a bar chart "views per product" and a line chart "views per hour". Cancel the subscription afterwards if you don't want to pay.
2. Write in your README how you would add product **search** with OpenSearch: products in RDS → change events → Lambda → OpenSearch index → `GET /api/search?q=`.

Q: Which service runs managed Hadoop and Spark clusters?
A: Amazon EMR.

Q: Which service provides full-text search and log analytics dashboards?
A: Amazon OpenSearch Service.

Q: Which service creates BI dashboards for business users from Athena or Redshift data?
A: Amazon QuickSight.

@@ 2 | MODULE | AI/ML Services | Pre-trained AI APIs and SageMaker | SHOULD
Recognize which AI service fits the scenario.

@@ 3 | TOPIC | AWS AI services overview | Rekognition, Comprehend, Textract, Transcribe, Polly, Translate, Lex, Kendra, Personalize, SageMaker, Bedrock | SHOULD
# AI/ML services: match the keyword

| Keyword in the question | Service |
|---|---|
| Images/video: detect objects, faces, labels, **content moderation**, celebrity, text in images | **Amazon Rekognition** |
| Text: **sentiment**, entities, key phrases, language, PII detection (NLP) | **Amazon Comprehend** (Comprehend Medical for clinical text) |
| Extract text, **forms and tables from scanned documents** (OCR) | **Amazon Textract** |
| **Speech → text** (call transcripts, subtitles) | **Amazon Transcribe** |
| **Text → speech** (lifelike voices) | **Amazon Polly** |
| Language **translation** | **Amazon Translate** |
| **Chatbots** with voice/text (same tech as Alexa) | **Amazon Lex** |
| Contact center in the cloud | **Amazon Connect** |
| Intelligent **enterprise search** over documents | **Amazon Kendra** |
| Real-time **recommendations** ("customers also bought") | **Amazon Personalize** |
| Time-series **forecasting** | Amazon Forecast (closed to new customers; SageMaker Canvas is the successor) |
| Online **fraud** detection | Amazon Fraud Detector (closed to new customers; use SageMaker) |
| Build, train and deploy **custom ML models** | **Amazon SageMaker AI** |
| **Generative AI** with foundation models (Claude, Llama, Titan…) through an API, RAG with knowledge bases, agents | **Amazon Bedrock** |
| Human review of ML predictions | Amazon Augmented AI (A2I) |

## Exam tips
- "Least development effort" for a common AI task → the **pre-trained AI service**, not SageMaker.
- "Data scientists need to build and train their own model" → **SageMaker**.

## Lab task (CloudMart)
Extend the **thumbnail Lambda** (or add a second rule target): call `rekognition:DetectLabels` on each new product image and `rekognition:DetectModerationLabels` to block inappropriate uploads. Store the top 5 labels as product tags through the internal API, and show them in the UI. Stretch: call **Comprehend** `DetectSentiment` on product reviews if you add a review feature.

```python
rek = boto3.client("rekognition")
labels = rek.detect_labels(Image={"S3Object": {"Bucket": bucket, "Name": key}}, MaxLabels=5, MinConfidence=80)
tags = [l["Name"] for l in labels["Labels"]]          # e.g. ["Shoe", "Footwear", "Sneaker"]
mod = rek.detect_moderation_labels(Image={"S3Object": {"Bucket": bucket, "Name": key}}, MinConfidence=80)
flagged = len(mod["ModerationLabels"]) > 0            # True -> mark the product hidden for admin review
```
- Rekognition reads the image straight from S3 (the Lambda role needs `s3:GetObject` and `rekognition:DetectLabels`). No ML knowledge is needed, which is exactly the "pre-trained service" point the exam makes.

Q: Which service detects inappropriate content in user-uploaded images?
A: Amazon Rekognition (content moderation).

Q: Which service extracts text and tables from scanned forms?
A: Amazon Textract.

Q: Which service analyzes customer review sentiment?
A: Amazon Comprehend.

Q: Which service provides API access to foundation models for generative AI?
A: Amazon Bedrock.
