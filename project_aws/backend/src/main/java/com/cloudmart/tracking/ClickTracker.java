package com.cloudmart.tracking;

/**
 * PORT: clickstream events (product views, add-to-cart).
 * Local adapter: {@link LogClickTracker}.
 * AWS adapter (Phase 7): FirehoseClickTracker -> Firehose stream cloudmart-clicks -> S3 data lake,
 * queried with Glue + Athena in Phase 11. cloudmart.tracking=firehose.
 */
public interface ClickTracker {

    void track(ClickEvent event);

    record ClickEvent(String event, Long productId, String sessionId, String ts) {}
}
