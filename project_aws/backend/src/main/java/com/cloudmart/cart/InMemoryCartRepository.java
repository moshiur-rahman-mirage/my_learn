package com.cloudmart.cart;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LOCAL adapter. AWS lesson: behind a load balancer with 2 instances, a cart stored here
 * "disappears" when the next request hits the other instance -> stateless app + DynamoDB (Phase 5).
 */
@Repository
@ConditionalOnProperty(name = "cloudmart.cart", havingValue = "local", matchIfMissing = true)
public class InMemoryCartRepository implements CartRepository {

    private final Map<String, Cart> carts = new ConcurrentHashMap<>();

    @Override
    public Optional<Cart> find(String sessionId) {
        Cart c = carts.get(sessionId);
        if (c != null && c.expiresAt() < Instant.now().getEpochSecond()) {   // emulate TTL
            carts.remove(sessionId);
            return Optional.empty();
        }
        return Optional.ofNullable(c);
    }

    @Override
    public void save(Cart cart) {
        carts.put(cart.sessionId(), cart);
    }

    @Override
    public void delete(String sessionId) {
        carts.remove(sessionId);
    }
}
