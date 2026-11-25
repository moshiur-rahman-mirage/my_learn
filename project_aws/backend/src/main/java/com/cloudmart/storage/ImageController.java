package com.cloudmart.storage;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/** Serves images only while the LOCAL storage adapter is active. With S3 the browser loads from S3/CloudFront. */
@RestController
@ConditionalOnProperty(name = "cloudmart.storage", havingValue = "local", matchIfMissing = true)
public class ImageController {

    private final LocalStorageService storage;

    public ImageController(LocalStorageService storage) {
        this.storage = storage;
    }

    @GetMapping("/api/images/**")
    public ResponseEntity<Resource> image(HttpServletRequest request) {
        String key = request.getRequestURI().substring(request.getContextPath().length() + "/api/images/".length());
        Path file = storage.resolve(key);
        if (!Files.isRegularFile(file)) return ResponseEntity.notFound().build();
        MediaType type = MediaTypeFactory.getMediaType(file.getFileName().toString())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(type)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)))
                .body(new FileSystemResource(file));
    }
}
