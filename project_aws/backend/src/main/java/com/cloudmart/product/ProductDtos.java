package com.cloudmart.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;

public final class ProductDtos {
    private ProductDtos() {}

    /** What the API returns. imageUrl is resolved by the active StorageService. */
    public record ProductView(Long id, String name, String description, BigDecimal price,
                              String imageKey, String imageUrl, boolean cacheHit) {}

    /** Cached form (no URL: presigned URLs expire, so URLs are built fresh on every read). */
    public record CachedProduct(Long id, String name, String description, BigDecimal price,
                                String imageKey) implements Serializable {}

    /** JSON create, used by the presigned-upload flow (image already uploaded, only the key is sent). */
    public record CreateProductRequest(@NotBlank String name, String description,
                                       @NotNull @Positive BigDecimal price, String imageKey) {}
}
