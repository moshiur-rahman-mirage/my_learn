@@ 1 | MODULE | Phase 7 — Decoupling & Messaging | SQS, SNS, EventBridge, Kinesis, Amazon MQ | MUST
# Phase 7: Decoupling and messaging

**Decoupling** means components talk through a buffer (queue, topic or event bus) instead of calling each other directly. If the consumer is slow or down, messages wait, and nothing is lost. "Resilient architecture" questions very often want **SQS**.

**Analogy:** a restaurant ticket rail. Waiters (producers) pin orders on the rail (the queue), and cooks (consumers) take them at their own pace. A rush of guests doesn't crash the kitchen.

**CloudMart goal this phase:** checkout publishes an **OrderPlaced** message to **SQS**, a worker processes it, and **SNS** emails the customer. A failed message goes to a **dead-letter queue**. Order events also go on **EventBridge** for other consumers.

@@ 2 | MODULE | Queues and Topics | SQS and SNS | MUST
Point-to-point queues and publish/subscribe fan-out.

@@ 3 | TOPIC | Amazon SQS fundamentals | Standard vs FIFO, visibility timeout, retention, long polling, DLQ | MUST
# Amazon SQS (Simple Queue Service)

A fully managed **message queue**. Producers send messages, consumers **poll** and receive them, then **delete** them after processing.

## Standard vs FIFO

| | **Standard** | **FIFO** (`.fifo` suffix) |
|---|---|---|
| Throughput | **Nearly unlimited** | 300 msg/s per API action, 3,000 with batching (much higher in high-throughput mode) |
| Delivery | **At least once** (occasional duplicates) | **Exactly-once processing** (deduplication ID, 5-minute window) |
| Order | Best effort | **Strict order per message group ID** |

## Key settings
| Setting | Meaning | Default / range |
|---|---|---|
| **Visibility timeout** | After a consumer receives a message, it's hidden from others for this time. If not deleted in time, it **reappears** (and is processed again) | 30 s (0 s – 12 h) |
| **Message retention** | How long unprocessed messages stay | 4 days (1 min – **14 days**) |
| Max message size | | **256 KB**. For bigger payloads, store in S3 and send a pointer (Extended Client Library) |
| **Long polling** (`WaitTimeSeconds` up to 20) | Waits for messages instead of returning empty at once | **Fewer empty responses = lower cost** |
| **Delay queue** | Hide new messages for 0–15 minutes | |
| **Dead-letter queue (DLQ)** | After `maxReceiveCount` failed receives, the message moves to the DLQ for inspection. **Redrive** sends it back later | |

## Exam rules
- Messages processed twice → **visibility timeout is shorter than processing time**. Increase it (or call `ChangeMessageVisibility`).
- "Strict ordering and no duplicates" → **FIFO**.
- "Reduce cost/empty receives" → **long polling**.
- "Messages failing repeatedly block or waste processing" → **DLQ**.
- Scale consumers on **`ApproximateNumberOfMessagesVisible`** (the backlog per instance), e.g. an ASG or ECS service.
- SQS is the buffer for **write-heavy spikes** in front of a database: the queue absorbs the burst, and workers write at a steady rate.

## Lab task (CloudMart)
Create **`cloudmart-orders-dlq`** (Standard, retention 14 days) and **`cloudmart-orders`** (Standard, visibility timeout 60 s, long polling 20 s, redrive policy → DLQ with **maxReceiveCount 3**). Send a test message with the CLI and receive it:

```bash
aws sqs send-message --queue-url $Q --message-body '{"orderId":1}'
aws sqs receive-message --queue-url $Q --wait-time-seconds 20
```
- After receiving, *don't* delete the message. Wait 60 s and receive again: it reappears. That's the visibility timeout. Do this 3 more times and it lands in the DLQ.

Q: A message is processed multiple times by different consumers. What should you change?
A: Increase the visibility timeout to exceed the processing time (and make processing idempotent).

Q: What is the maximum SQS message retention period?
A: 14 days.

Q: Which queue type guarantees ordering and exactly-once processing?
A: SQS FIFO.

@@ 3 | TOPIC | SQS in CloudMart: producer and worker | Spring Boot publisher adapter, consumer loop, idempotency | MUST
# CloudMart order flow with SQS

## Producer: the `OrderEventPublisher` SQS adapter

```java
@Service
@ConditionalOnProperty(name = "cloudmart.events", havingValue = "sqs")
class SqsOrderEventPublisher implements OrderEventPublisher {
    private final SqsClient sqs = SqsClient.create();
    private final ObjectMapper json;
    @Value("${cloudmart.ordersQueueUrl}") String queueUrl;
    SqsOrderEventPublisher(ObjectMapper json) { this.json = json; }

    public void orderPlaced(Order o) {
        try {
            sqs.sendMessage(b -> b.queueUrl(queueUrl)
                    .messageBody(json.writeValueAsString(Map.of("orderId", o.getId(), "email", o.getCustomerEmail()))));
        } catch (JsonProcessingException e) { throw new IllegalStateException(e); }
    }
}
```
- The HTTP request finishes as soon as the message is safely in SQS. The customer doesn't wait for the processing, and a slow worker can't slow down checkout.

## Consumer (a temporary in-app worker; Phase 8 moves it to Lambda)

```java
@Component
@ConditionalOnProperty(name = "cloudmart.worker", havingValue = "true")
class OrderWorker {
    private final SqsClient sqs = SqsClient.create();
    private final OrderService orders;
    @Value("${cloudmart.ordersQueueUrl}") String queueUrl;
    OrderWorker(OrderService orders) { this.orders = orders; }

    @Scheduled(fixedDelay = 1000)
    void poll() {
        var msgs = sqs.receiveMessage(b -> b.queueUrl(queueUrl).maxNumberOfMessages(10).waitTimeSeconds(20)).messages();
        for (var m : msgs) {
            orders.process(m.body());                              // must be idempotent
            sqs.deleteMessage(b -> b.queueUrl(queueUrl).receiptHandle(m.receiptHandle()));
        }
    }
}
```
- **Delete only after success.** If `process` throws, the message reappears after the visibility timeout and is retried. After 3 failures it goes to the DLQ.
- **Idempotency:** `process` should do nothing if the order is already PROCESSED, because Standard queues can deliver twice.
- Enable scheduling with `@EnableScheduling`.

## IAM
Add `sqs:SendMessage`, `sqs:ReceiveMessage`, `sqs:DeleteMessage` and `sqs:GetQueueAttributes` on the queue ARN to `cloudmart-app-policy`.

## Lab task (CloudMart)
Add `software.amazon.awssdk:sqs`, implement both classes, and set `cloudmart.events=sqs` and `cloudmart.worker=true`. Place an order in the UI and watch the status turn PROCESSED. Then make `process` throw for orders whose email contains `fail`, place such an order, and find it in the DLQ after 3 attempts.

Q: When should an SQS consumer delete a message?
A: Only after it has processed the message successfully. Otherwise it should let it reappear for a retry.

Q: Why must SQS Standard consumers be idempotent?
A: Standard queues deliver at least once, so a message can arrive more than once.

@@ 3 | TOPIC | Amazon SNS and the fan-out pattern | Pub/sub topics, subscriptions, filtering, SNS + SQS fan-out, FIFO topics | MUST
# Amazon SNS (Simple Notification Service)

**SNS** is **publish/subscribe**: a publisher sends a message to a **topic**, and SNS **pushes** a copy to every **subscriber**: SQS, Lambda, HTTP(S), email, SMS, mobile push, Firehose.

**Analogy:** a radio station. It broadcasts once, and everybody tuned in hears it. (SQS is a mailbox: one reader takes each letter.)

## SQS vs SNS

| | SQS | SNS |
|---|---|---|
| Model | Queue (pull) | Topic (push) |
| Consumers per message | **One** | **Many** |
| Persistence | Up to 14 days | No storage (retries, then an optional DLQ) |

## Fan-out pattern (an exam classic)
**SNS topic → several SQS queues** (each consumer service has its own queue).
- Each consumer processes independently, at its own pace, with retries and persistence.
- The SQS queue policy must allow the SNS topic to send.
- Typical: "an order event must trigger billing, shipping and analytics independently" → SNS + SQS fan-out (or EventBridge).
- **S3 event → SNS → multiple SQS queues** solves "one S3 event type, many consumers".

## Message filtering
A **filter policy** on a subscription delivers only matching messages, e.g. `{"eventType": ["OrderPlaced"]}` or `{"amount": [{"numeric": [">", 100]}]}`.

## FIFO topics
SNS FIFO + SQS FIFO subscribers give **ordered, deduplicated** fan-out.

## Lab task (CloudMart)
1. Create topic **`cloudmart-notifications`** and an **email subscription** to your address (confirm the email).
2. Implement `SnsNotifier` behind the `Notifier` port: after an order is processed, publish "Your order #N is confirmed".
3. Stretch: create topic `cloudmart-order-events` with two SQS subscribers, `orders-billing` and `orders-analytics`, to practice fan-out. Add a filter policy so analytics only gets orders with `total > 50`.

Q: How do you deliver one message to multiple independent consumers that each need durable processing?
A: Use the SNS-to-SQS fan-out pattern: publish to an SNS topic subscribed by multiple SQS queues.

Q: How do you make a subscriber receive only certain messages from an SNS topic?
A: Attach a subscription filter policy.

@@ 2 | MODULE | Events and Streaming | EventBridge, Kinesis, Amazon MQ | MUST
Event routing and real-time data streams.

@@ 3 | TOPIC | Amazon EventBridge | Event buses, rules, schedules, SaaS sources, archive/replay, Pipes | MUST
# Amazon EventBridge

**EventBridge** is a serverless **event bus**. Events (JSON) come from AWS services, your apps or SaaS partners, **rules** match them by content, and matched events go to **targets** (Lambda, SQS, SNS, Step Functions, ECS tasks, API destinations, another bus or account…).

## Buses
- **Default bus:** AWS service events (EC2 state change, S3 Object Created, GuardDuty finding, CodePipeline, Health events…).
- **Custom bus:** your application events, e.g. `cloudmart-bus`.
- **Partner bus:** SaaS events (Zendesk, Datadog, Shopify…).

## Features
- **Content-based filtering** on any field (prefix, numeric, exists, anything-but…).
- **EventBridge Scheduler:** cron/rate schedules with time zones, one-time schedules, millions of schedules. It replaces "cron on EC2".
- **Archive and replay** past events (e.g. after fixing a bug).
- **Schema registry:** discovers event structure and generates code bindings.
- **Cross-account / cross-Region** event delivery (a central security account).
- **Pipes:** point-to-point source → filter → enrich → target (e.g. SQS → Step Functions) without glue code.
- **API destinations:** call any HTTP API as a target.

## EventBridge vs SNS
- SNS: very high throughput fan-out with simple attribute filtering. Mobile, SMS and email.
- EventBridge: rich content filtering, many AWS targets, SaaS integration, schedules, archive/replay. Lower throughput limits than SNS.

## Exam patterns
- "Run a Lambda every night at 2 AM" → **EventBridge Scheduler** (rule).
- "React when an EC2 instance stops / a GuardDuty finding appears" → an EventBridge rule on the default bus.
- "Integrate events from a SaaS application" → **partner event source**.

## Lab task (CloudMart)
1. Create a custom bus **`cloudmart-bus`**. After processing, the worker also puts an event `{"source":"cloudmart.orders","detail-type":"OrderProcessed","detail":{...}}` on it (`events:PutEvents` permission).
2. Add a rule matching `detail.total > 100` → target CloudWatch Logs `/cloudmart/big-orders`.
3. Create an **EventBridge Scheduler** schedule that runs every day at 02:00 and sends a message to SQS (later a Lambda) to "clean up old carts report".

Q: Which service replaces cron jobs on EC2 for scheduled serverless tasks?
A: Amazon EventBridge Scheduler (or scheduled rules).

Q: Which service can receive events from SaaS partners like Zendesk or Datadog?
A: Amazon EventBridge (partner event buses).

@@ 3 | TOPIC | Kinesis Data Streams and Amazon Data Firehose | Real-time streaming: shards, retention, consumers; delivery to S3/Redshift/OpenSearch | MUST
# Kinesis Data Streams and Data Firehose

## Kinesis Data Streams (KDS)
A **real-time data stream** for high-volume records (clickstreams, logs, IoT telemetry, metrics).
- Records are ordered **per partition key** within a **shard**.
- Capacity: provisioned shards (1 MB/s or 1,000 records/s in, 2 MB/s out per shard) or **on-demand** mode.
- **Retention 24 h by default, up to 365 days.** Data can be **replayed**, and many consumers read the same data.
- Consumers: Lambda, the KCL apps, Managed Service for Apache Flink, Firehose. **Enhanced fan-out** gives each consumer a dedicated 2 MB/s per shard.
- You manage scaling (shards) in provisioned mode, and you write consumer code.

## Amazon Data Firehose (formerly Kinesis Data Firehose)
A fully managed **delivery service**: it ingests streaming data and **loads it into destinations**: **S3, Redshift (through S3), OpenSearch, Splunk**, HTTP endpoints, Datadog and others.
- **Near-real-time** (buffering by size or time, e.g. 60 s or 5 MB). Not real-time.
- Automatic scaling, no shards to manage, **no data storage/replay**.
- Optional transformation with Lambda, **format conversion to Parquet/ORC**, compression, dynamic partitioning in S3.

## Compare (exam staple)
| | Kinesis Data Streams | Data Firehose | SQS |
|---|---|---|---|
| Purpose | Real-time stream, custom consumers | **Load streaming data into storage** | Decouple tasks / jobs |
| Latency | ~70–200 ms | Seconds to minutes (buffered) | ms |
| Replay | **Yes** (retention) | No | No (a message is deleted after processing) |
| Multiple consumers of the same data | **Yes** | One destination per stream | No (one consumer per message) |
| Ordering | Per shard | — | FIFO only |
| Ops | Shards (or on-demand) | Fully managed | Fully managed |

**Managed Service for Apache Flink** (formerly Kinesis Data Analytics) = real-time SQL/Java/Python analytics on streams.
**Amazon MSK** = managed Apache Kafka (when you need the Kafka API). Covered in Phase 11.

## Exam patterns
- "Ingest clickstream and store in S3 for analytics with the least operational overhead" → **Firehose → S3**.
- "Real-time processing, multiple apps read the same stream, replay needed" → **Kinesis Data Streams**.
- "Need ordering per user ID at very high throughput" → KDS with partition key = userId.

## Lab task (CloudMart)
Create a **Firehose stream `cloudmart-clicks`** (source: Direct PUT, destination: a new bucket `cloudmart-datalake-<id>`, prefix `clicks/`, buffer 60 s). Add an endpoint `POST /api/track` that calls `firehose:PutRecord` with `{event, productId, sessionId, ts}`, and call it from React on product views. After a minute, see the files in S3. Phase 11 queries them with Athena.

Q: Which service loads streaming data into S3, Redshift or OpenSearch with no administration?
A: Amazon Data Firehose.

Q: Which streaming service allows data replay and multiple consumers reading the same records?
A: Kinesis Data Streams.

Q: Default and maximum retention for Kinesis Data Streams?
A: 24 hours by default, up to 365 days.

@@ 3 | TOPIC | Amazon MQ | Managed ActiveMQ/RabbitMQ for lift-and-shift of existing messaging | SHOULD
# Amazon MQ

**Amazon MQ** is a managed message broker for **Apache ActiveMQ** and **RabbitMQ**, with industry-standard protocols: **JMS, AMQP, MQTT, OpenWire, STOMP**.

## When to choose it
- You're **migrating existing on-prem apps** that already use these brokers and protocols, and you don't want to rewrite them for SQS/SNS.
- New cloud-native apps → **SQS/SNS/EventBridge** (more scalable, serverless).

## HA
Active/standby brokers across **2 AZs**, with shared storage (EFS for ActiveMQ). It runs on brokers (instances), so it doesn't scale like SQS.

## Exam pattern
"A company is migrating an application that uses JMS/AMQP/MQTT and wants minimal code changes" → **Amazon MQ**.

## Lab task (CloudMart)
None. If CloudMart had a legacy warehouse system speaking JMS, Amazon MQ is the bridge you would use.

Q: An on-prem app uses RabbitMQ with AMQP. Which managed AWS service needs the fewest code changes?
A: Amazon MQ (for RabbitMQ).

@@ 3 | TOPIC | Lab milestone: CloudMart decoupled order flow | SQS + DLQ, SNS email, EventBridge bus, Firehose clickstream | MUST
# Lab milestone: decoupled CloudMart

## Flow you've built

```text
React checkout -> API (ALB/ASG) -> SQS cloudmart-orders --(3 failures)--> cloudmart-orders-dlq
                                        |
                                   worker (in-app for now; Lambda in Phase 8)
                                        |-> RDS: status PROCESSED
                                        |-> SNS cloudmart-notifications -> email
                                        |-> EventBridge cloudmart-bus -> rule (total>100) -> logs
React product views -> API /api/track -> Firehose -> S3 data lake (clicks/)
```

## Resilience experiments
1. **Consumer down:** set `cloudmart.worker=false` (or stop the worker), place 10 orders → the queue depth is 10 (CloudWatch metric `ApproximateNumberOfMessagesVisible`). Start the worker → it drains. No orders are lost.
2. **Poison message:** an order with "fail" in the email ends in the DLQ. Fix the code, then use **"Start DLQ redrive"** in the console to reprocess it.
3. **Spike:** send 1,000 messages with a loop. The API stays fast because it only enqueues.

## Exam takeaway
Replacing a synchronous call with SQS gives: **loose coupling, durability, buffering for spikes, independent scaling and retries**. That's what "make it resilient" usually means.

## Teardown
SQS, SNS, EventBridge and Firehose cost almost nothing when idle. Keep them for Phase 8.

Q: Orders are lost when the processing service crashes during peaks. What architectural change fixes this?
A: Put an SQS queue between the API and the processor (with a DLQ) so messages persist and are processed when capacity is available.
