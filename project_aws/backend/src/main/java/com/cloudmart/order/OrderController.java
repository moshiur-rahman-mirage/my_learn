package com.cloudmart.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService orders;

    public OrderController(OrderService orders) {
        this.orders = orders;
    }

    public record PlaceOrderRequest(String sessionId, String email) {}

    @PostMapping("/api/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order place(@RequestBody PlaceOrderRequest req) {
        return orders.place(req.sessionId(), req.email());
    }

    @GetMapping("/api/orders/{id}")
    public Order get(@PathVariable long id) {
        return orders.get(id);
    }

    @GetMapping("/api/orders")
    public List<Order> recent() {
        return orders.recent();
    }

    /**
     * Called by the background worker (the SQS-triggered Lambda in Phase 8).
     * It is NOT under /api/** so CloudFront's /api/* behavior never exposes it publicly;
     * in AWS, allow it only from inside the VPC (security group / ALB rule).
     */
    @PostMapping("/internal/orders/{id}/processed")
    public Order process(@PathVariable long id) {
        return orders.process(id);
    }
}
