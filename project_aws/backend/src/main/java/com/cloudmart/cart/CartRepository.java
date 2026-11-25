package com.cloudmart.cart;

import java.util.Optional;

/**
 * PORT: temporary cart storage.
 * Local adapter: {@link InMemoryCartRepository}.
 * AWS adapter (Phase 5): DynamoCartRepository (table cloudmart-carts, partition key sessionId,
 * TTL attribute expiresAt), cloudmart.cart=dynamodb.
 */
public interface CartRepository {

    Optional<Cart> find(String sessionId);

    void save(Cart cart);

    void delete(String sessionId);
}
