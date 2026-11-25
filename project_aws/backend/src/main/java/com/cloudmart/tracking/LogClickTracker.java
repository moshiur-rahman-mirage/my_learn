package com.cloudmart.tracking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** LOCAL adapter: logs each click as one JSON-ish line. */
@Service
@ConditionalOnProperty(name = "cloudmart.tracking", havingValue = "local", matchIfMissing = true)
public class LogClickTracker implements ClickTracker {

    private static final Logger log = LoggerFactory.getLogger(LogClickTracker.class);

    @Override
    public void track(ClickEvent e) {
        log.info("click event={} productId={} sessionId={} ts={}", e.event(), e.productId(), e.sessionId(), e.ts());
    }
}
