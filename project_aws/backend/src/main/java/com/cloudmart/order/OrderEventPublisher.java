package com.cloudmart.order;

/**
 * PORT: "an order was placed, process it in the background".
 * Local adapter: {@link LocalOrderEventPublisher} (Spring in-process event + async worker).
 * AWS adapter (Phase 7): SqsOrderEventPublisher -> queue cloudmart-orders (+ DLQ), cloudmart.events=sqs.
 * The worker then becomes a Lambda (Phase 8) that calls POST /internal/orders/{id}/processed.
 */
public interface OrderEventPublisher {

    void orderPlaced(OrderPlacedEvent event);

    /** Message body. Keep it small: the worker loads details by id (SQS max message = 256 KB). */
    record OrderPlacedEvent(long orderId, String email) {}
}
