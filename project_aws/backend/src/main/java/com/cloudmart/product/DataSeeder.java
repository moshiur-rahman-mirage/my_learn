package com.cloudmart.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** Seeds a few products on an empty database so the catalog is never blank (also on a fresh RDS). */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final ProductRepository repo;

    public DataSeeder(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        repo.saveAll(List.of(
                new Product("Cloud Hoodie", "Warm hoodie for long lab nights", new BigDecimal("39.90"), null),
                new Product("Serverless Mug", "Holds coffee, scales to zero", new BigDecimal("12.50"), null),
                new Product("Multi-AZ Backpack", "Two compartments, no single point of failure", new BigDecimal("59.00"), null),
                new Product("S3 Notebook", "11 nines of durability for your notes", new BigDecimal("8.99"), null),
                new Product("Lambda Sticker Pack", "Cold start not included", new BigDecimal("4.50"), null),
                new Product("VPC Cable Organizer", "Keeps your subnets tidy", new BigDecimal("15.00"), null)));
        log.info("Seeded {} demo products", repo.count());
    }
}
