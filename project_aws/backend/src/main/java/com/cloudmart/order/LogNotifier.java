package com.cloudmart.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** LOCAL adapter: writes the "email" to the log. */
@Service
@ConditionalOnProperty(name = "cloudmart.notifier", havingValue = "local", matchIfMissing = true)
public class LogNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(LogNotifier.class);

    @Override
    public void orderProcessed(Order order) {
        log.info("[EMAIL to {}] Your CloudMart order #{} ({}) is confirmed.",
                order.getCustomerEmail(), order.getId(), order.getTotal());
    }
}
