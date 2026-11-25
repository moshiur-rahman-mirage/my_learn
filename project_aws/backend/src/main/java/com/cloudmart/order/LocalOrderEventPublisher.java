package com.cloudmart.order;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/** LOCAL adapter: an in-process Spring event. Lost if the JVM dies -> that's why Phase 7 uses SQS. */
@Service
@ConditionalOnProperty(name = "cloudmart.events", havingValue = "local", matchIfMissing = true)
public class LocalOrderEventPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher events;

    public LocalOrderEventPublisher(ApplicationEventPublisher events) {
        this.events = events;
    }

    @Override
    public void orderPlaced(OrderPlacedEvent event) {
        events.publishEvent(event);
    }
}
