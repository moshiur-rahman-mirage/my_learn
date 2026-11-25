package com.cloudmart.cart;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart/{sessionId}")
public class CartController {

    private final CartService carts;

    public CartController(CartService carts) {
        this.carts = carts;
    }

    public record AddItemRequest(long productId, Integer quantity) {}

    @GetMapping
    public Cart get(@PathVariable String sessionId) {
        return carts.get(sessionId);
    }

    @PostMapping("/items")
    public Cart add(@PathVariable String sessionId, @RequestBody AddItemRequest req) {
        return carts.add(sessionId, req.productId(), req.quantity() == null ? 1 : req.quantity());
    }

    @DeleteMapping("/items/{productId}")
    public Cart remove(@PathVariable String sessionId, @PathVariable long productId) {
        return carts.remove(sessionId, productId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(@PathVariable String sessionId) {
        carts.clear(sessionId);
    }
}
