package com.cloudmart.product;

import com.cloudmart.cache.CacheService;
import com.cloudmart.product.ProductDtos.CachedProduct;
import com.cloudmart.product.ProductDtos.ProductView;
import com.cloudmart.storage.StorageService;
import com.cloudmart.storage.StorageService.UploadTarget;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final ProductRepository repo;
    private final StorageService storage;
    private final CacheService cache;

    public ProductService(ProductRepository repo, StorageService storage, CacheService cache) {
        this.repo = repo;
        this.storage = storage;
        this.cache = cache;
    }

    @Transactional(readOnly = true)
    public List<ProductView> list() {
        return repo.findAll().stream()
                .map(p -> view(toCached(p), false))
                .toList();
    }

    /** Cache-aside (lazy loading): cache -> miss -> DB -> put in cache with TTL. */
    @Transactional(readOnly = true)
    public ProductView get(long id) {
        String key = "product:" + id;
        Optional<CachedProduct> hit = cache.get(key, CachedProduct.class);
        if (hit.isPresent()) return view(hit.get(), true);
        CachedProduct p = repo.findById(id).map(this::toCached)
                .orElseThrow(() -> new NoSuchElementException("product " + id + " not found"));
        cache.put(key, p, CACHE_TTL);
        return view(p, false);
    }

    @Transactional
    public ProductView create(String name, String description, BigDecimal price, MultipartFile image) {
        String imageKey = null;
        if (image != null && !image.isEmpty()) {
            imageKey = newImageKey(image.getOriginalFilename());
            try {
                storage.save(imageKey, image.getInputStream(), image.getContentType());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return create(name, description, price, imageKey);
    }

    @Transactional
    public ProductView create(String name, String description, BigDecimal price, String imageKey) {
        if (price == null || price.signum() <= 0) throw new IllegalArgumentException("price must be positive");
        Product saved = repo.save(new Product(name, description, price, imageKey));
        return view(toCached(saved), false);
    }

    @Transactional
    public void delete(long id) {
        Product p = repo.findById(id).orElseThrow(() -> new NoSuchElementException("product " + id + " not found"));
        if (p.getImageKey() != null) storage.delete(p.getImageKey());
        repo.delete(p);
        cache.evict("product:" + id);   // never serve a deleted product from cache
    }

    /** Presigned upload target for the browser (S3 adapter only, Phase 4). */
    public UploadTarget uploadUrl(String filename, String contentType) {
        String key = newImageKey(filename);
        return storage.uploadUrl(key, contentType)
                .orElseThrow(() -> new UnsupportedOperationException(
                        "direct upload not supported by the active storage adapter (switch cloudmart.storage=s3)"));
    }

    private String newImageKey(String filename) {
        String ext = "";
        if (filename != null && filename.lastIndexOf('.') > 0) {
            ext = filename.substring(filename.lastIndexOf('.')).toLowerCase().replaceAll("[^a-z0-9.]", "");
        }
        return "products/" + UUID.randomUUID() + ext;
    }

    private CachedProduct toCached(Product p) {
        return new CachedProduct(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getImageKey());
    }

    private ProductView view(CachedProduct p, boolean cacheHit) {
        String url = p.imageKey() == null ? null : storage.publicUrl(p.imageKey());
        return new ProductView(p.id(), p.name(), p.description(), p.price(), p.imageKey(), url, cacheHit);
    }
}
