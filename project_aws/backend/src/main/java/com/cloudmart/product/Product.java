package com.cloudmart.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Storage KEY only (e.g. products/uuid.jpg), never a full URL. The StorageService turns it into a URL. */
    private String imageKey;

    private Instant createdAt = Instant.now();

    protected Product() {}

    public Product(String name, String description, BigDecimal price, String imageKey) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageKey = imageKey;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getImageKey() { return imageKey; }
    public Instant getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setImageKey(String imageKey) { this.imageKey = imageKey; }
}
