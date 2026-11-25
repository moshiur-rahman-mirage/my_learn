package com.cloudmart.order;

/**
 * PORT: tell the customer something happened.
 * Local adapter: {@link LogNotifier}.
 * AWS adapter (Phase 7): SnsNotifier -> topic cloudmart-notifications (email subscription), cloudmart.notifier=sns.
 */
public interface Notifier {

    void orderProcessed(Order order);
}
