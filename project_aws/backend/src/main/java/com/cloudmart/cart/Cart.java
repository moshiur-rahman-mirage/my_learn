package com.cloudmart.cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * A shopping cart keyed by a browser session id.
 * expiresAt (epoch seconds) is exactly the attribute DynamoDB TTL uses in Phase 5.
 */
public record Cart(String sessionId, List<CartItem> items, long expiresAt) {

    public record CartItem(Long productId, String name, BigDecimal price, int quantity) {
        public BigDecimal lineTotal() {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal total() {
        return items.stream().map(CartItem::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Cart empty(String sessionId) {
        return new Cart(sessionId, List.of(), 0);
    }
}
