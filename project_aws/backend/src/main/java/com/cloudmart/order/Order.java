package com.cloudmart.order;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")   // "order" is a reserved word in SQL
public class Order {

    public enum Status { NEW, PROCESSED, FAILED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.NEW;

    private int attempts;
    private String lastError;
    private Instant createdAt = Instant.now();
    private Instant processedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_lines", joinColumns = @JoinColumn(name = "order_id"))
    private List<Line> lines = new ArrayList<>();

    @Embeddable
    public static class Line {
        private Long productId;
        private String name;
        @Column(precision = 10, scale = 2)
        private BigDecimal price;
        private int quantity;

        protected Line() {}

        public Line(Long productId, String name, BigDecimal price, int quantity) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public String getName() { return name; }
        public BigDecimal getPrice() { return price; }
        public int getQuantity() { return quantity; }
    }

    protected Order() {}

    public Order(String customerEmail, BigDecimal total, List<Line> lines) {
        this.customerEmail = customerEmail;
        this.total = total;
        this.lines = new ArrayList<>(lines);
    }

    public void markProcessed() {
        this.status = Status.PROCESSED;
        this.processedAt = Instant.now();
        this.lastError = null;
    }

    public void recordFailure(String error, boolean finalFailure) {
        this.attempts++;
        this.lastError = error;
        if (finalFailure) this.status = Status.FAILED;
    }

    public Long getId() { return id; }
    public String getCustomerEmail() { return customerEmail; }
    public BigDecimal getTotal() { return total; }
    public Status getStatus() { return status; }
    public int getAttempts() { return attempts; }
    public String getLastError() { return lastError; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getProcessedAt() { return processedAt; }
    public List<Line> getLines() { return lines; }
}
