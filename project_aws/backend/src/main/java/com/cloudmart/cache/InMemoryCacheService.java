package com.cloudmart.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LOCAL adapter: a per-JVM map. Note the AWS lesson: with 2+ instances behind an ALB each instance
 * has its OWN cache -> Phase 5 moves it to ElastiCache so all instances share one cache.
 */
@Service
@ConditionalOnProperty(name = "cloudmart.cache", havingValue = "local", matchIfMissing = true)
public class InMemoryCacheService implements CacheService {

    private record Entry(Object value, Instant expiresAt) {}

    private final Map<String, Entry> map = new ConcurrentHashMap<>();

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        Entry e = map.get(key);
        if (e == null) return Optional.empty();
        if (Instant.now().isAfter(e.expiresAt())) {
            map.remove(key);
            return Optional.empty();
        }
        return Optional.of(type.cast(e.value()));
    }

    @Override
    public void put(String key, Object value, Duration ttl) {
        map.put(key, new Entry(value, Instant.now().plus(ttl)));
    }

    @Override
    public void evict(String key) {
        map.remove(key);
    }
}
