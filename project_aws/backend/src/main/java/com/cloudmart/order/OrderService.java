package com.cloudmart.order;

import com.cloudmart.cart.Cart;
import com.cloudmart.cart.CartService;
import com.cloudmart.order.OrderEventPublisher.OrderPlacedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final CartService carts;
    private final OrderEventPublisher publisher;
    private final Notifier notifier;

    public OrderService(OrderRepository orders, CartService carts, OrderEventPublisher publisher, Notifier notifier) {
        this.orders = orders;
        this.carts = carts;
        this.publisher = publisher;
        this.notifier = notifier;
    }

    /** Checkout: save the order as NEW, empty the cart, hand processing to the background. Returns fast. */
    @Transactional
    public Order place(String sessionId, String email) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("valid email required");
        Cart cart = carts.get(sessionId);
        if (cart.items().isEmpty()) throw new IllegalStateException("cart is empty");
        List<Order.Line> lines = cart.items().stream()
                .map(i -> new Order.Line(i.productId(), i.name(), i.price(), i.quantity()))
                .toList();
        Order order = orders.save(new Order(email, cart.total(), lines));
        carts.clear(sessionId);
        publisher.orderPlaced(new OrderPlacedEvent(order.getId(), email));
        return order;
    }

    @Transactional(readOnly = true)
    public Order get(long id) {
        return orders.findById(id).orElseThrow(() -> new NoSuchElementException("order " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Order> recent() {
        return orders.findTop20ByOrderByIdDesc();
    }

    /**
     * The actual "processing". IDEMPOTENT on purpose: SQS standard queues may deliver a message twice,
     * so processing an already-PROCESSED order must do nothing.
     */
    @Transactional
    public Order process(long id) {
        Order order = get(id);
        if (order.getStatus() == Order.Status.PROCESSED) return order;
        if (order.getCustomerEmail().contains("fail")) {
            throw new IllegalStateException("simulated failure for " + order.getCustomerEmail());
        }
        order.markProcessed();
        notifier.orderProcessed(order);
        return order;
    }

    @Transactional
    public void recordFailure(long id, String error, boolean finalFailure) {
        get(id).recordFailure(error, finalFailure);
    }
}
