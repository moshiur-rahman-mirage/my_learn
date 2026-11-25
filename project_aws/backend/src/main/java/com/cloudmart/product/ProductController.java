package com.cloudmart.product;

import com.cloudmart.product.ProductDtos.CreateProductRequest;
import com.cloudmart.product.ProductDtos.ProductView;
import com.cloudmart.storage.StorageService.UploadTarget;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService products;

    public ProductController(ProductService products) {
        this.products = products;
    }

    @GetMapping
    public List<ProductView> list() {
        return products.list();
    }

    @GetMapping("/{id}")
    public ProductView get(@PathVariable long id) {
        return products.get(id);
    }

    /** Local flow: the image travels through the API (multipart form). */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductView createMultipart(@RequestParam String name,
                                       @RequestParam(required = false) String description,
                                       @RequestParam BigDecimal price,
                                       @RequestParam(required = false) MultipartFile image) {
        return products.create(name, description, price, image);
    }

    /** S3 flow (Phase 4): the browser already PUT the image to S3; only the key arrives here. */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductView createJson(@Valid @RequestBody CreateProductRequest req) {
        return products.create(req.name(), req.description(), req.price(), req.imageKey());
    }

    /** Returns {key, url}: a presigned PUT URL. 501 while cloudmart.storage=local. */
    @PostMapping("/upload-url")
    public UploadTarget uploadUrl(@RequestParam(defaultValue = "image.jpg") String filename,
                                  @RequestParam(defaultValue = "image/jpeg") String contentType) {
        return products.uploadUrl(filename, contentType);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        products.delete(id);
    }
}
