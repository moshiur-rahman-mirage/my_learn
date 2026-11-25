package com.cloudmart.order;

import com.cloudmart.order.OrderEventPublisher.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * LOCAL worker. It imitates what SQS + Lambda will do in Phases 7-8:
 * up to 3 attempts (like maxReceiveCount=3), then the order is FAILED (like landing in the DLQ).
 * Orders whose email contains "fail" always fail, so you can practice DLQ handling.
 */
@Component
@ConditionalOnProperty(name = "cloudmart.events", havingValue = "local", matchIfMissing = true)
public class LocalOrderWorker {

    private static final Logger log = LoggerFactory.getLogger(LocalOrderWorker.class);
    private static final int MAX_ATTEMPTS = 3;

    private final OrderService orders;

    public LocalOrderWorker(OrderService orders) {
        this.orders = orders;
    }

    @Async
    @TransactionalEventListener(fallbackExecution = true)   // runs after the order row is committed
    public void on(OrderPlacedEvent event) throws InterruptedException {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            Thread.sleep(2000);   // pretend processing takes time
            try {
                orders.process(event.orderId());
                return;
            } catch (RuntimeException e) {
                boolean last = attempt == MAX_ATTEMPTS;
                log.warn("order {} attempt {} failed: {}", event.orderId(), attempt, e.getMessage());
                orders.recordFailure(event.orderId(), e.getMessage(), last);
            }
        }
        log.error("order {} moved to FAILED (this is where SQS would put it in the DLQ)", event.orderId());
    }
}
