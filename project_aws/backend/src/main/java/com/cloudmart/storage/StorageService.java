package com.cloudmart.storage;

import java.io.InputStream;
import java.util.Optional;

/**
 * PORT: where product images live.
 * Local adapter: {@link LocalStorageService} (folder on disk).
 * AWS adapter (you write it in Phase 4): S3StorageService with
 *   {@code @ConditionalOnProperty(name = "cloudmart.storage", havingValue = "s3")}
 *   - save()       -> s3.putObject
 *   - publicUrl()  -> presigned GET URL (later: CloudFront URL)
 *   - uploadUrl()  -> presigned PUT URL so the browser uploads straight to S3
 */
public interface StorageService {

    /** Server-side upload. Returns the key that was stored. */
    String save(String key, InputStream data, String contentType);

    /** A URL the browser can load the image from. */
    String publicUrl(String key);

    /** Direct browser upload target. Empty = not supported, the UI falls back to multipart upload. */
    default Optional<UploadTarget> uploadUrl(String key, String contentType) {
        return Optional.empty();
    }

    void delete(String key);

    record UploadTarget(String key, String url) {}
}
