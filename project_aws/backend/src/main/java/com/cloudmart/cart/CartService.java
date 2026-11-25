package com.cloudmart.cart;

import com.cloudmart.cart.Cart.CartItem;
import com.cloudmart.product.ProductDtos.ProductView;
import com.cloudmart.product.ProductService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    static final Duration CART_TTL = Duration.ofHours(24);

    private final CartRepository carts;
    private final ProductService products;

    public CartService(CartRepository carts, ProductService products) {
        this.carts = carts;
        this.products = products;
    }

    public Cart get(String sessionId) {
        return carts.find(sessionId).orElse(Cart.empty(sessionId));
    }

    public Cart add(String sessionId, long productId, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("quantity must be >= 1");
        ProductView p = products.get(productId);
        List<CartItem> items = new ArrayList<>(get(sessionId).items());
        int idx = indexOf(items, productId);
        if (idx >= 0) {
            CartItem old = items.get(idx);
            items.set(idx, new CartItem(old.productId(), old.name(), old.price(), old.quantity() + quantity));
        } else {
            items.add(new CartItem(p.id(), p.name(), p.price(), quantity));
        }
        return save(sessionId, items);
    }

    public Cart remove(String sessionId, long productId) {
        List<CartItem> items = new ArrayList<>(get(sessionId).items());
        items.removeIf(i -> i.productId() == productId);
        return save(sessionId, items);
    }

    public void clear(String sessionId) {
        carts.delete(sessionId);
    }

    private Cart save(String sessionId, List<CartItem> items) {
        long expiresAt = Instant.now().plus(CART_TTL).getEpochSecond();   // sliding 24h expiry
        Cart cart = new Cart(sessionId, List.copyOf(items), expiresAt);
        carts.save(cart);
        return cart;
    }

    private static int indexOf(List<CartItem> items, long productId) {
        for (int i = 0; i < items.size(); i++) if (items.get(i).productId() == productId) return i;
        return -1;
    }
}
