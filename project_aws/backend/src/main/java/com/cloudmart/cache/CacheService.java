package com.cloudmart.cache;

import java.time.Duration;
import java.util.Optional;

/**
 * PORT: a key/value cache with TTL (cache-aside pattern).
 * Local adapter: {@link InMemoryCacheService}.
 * AWS adapter (Phase 5): RedisCacheService for ElastiCache (Valkey/Redis), cloudmart.cache=redis.
 */
public interface CacheService {

    <T> Optional<T> get(String key, Class<T> type);

    void put(String key, Object value, Duration ttl);

    void evict(String key);
}
