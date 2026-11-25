package com.cloudmart.lab;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.sql.Connection;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Endpoints that exist only to SEE AWS behaviour:
 *  whoami  -> which instance / AZ / DB served me   (load balancing, failover)
 *  stress  -> burn CPU                             (Auto Scaling scale-out)
 *  health  -> togglable health check               (ALB unhealthy target -> ASG replaces it)
 *  slow    -> sleep N seconds                      (ALB / API Gateway / CloudFront timeouts)
 *  config  -> which adapters are active            (which AWS services this instance uses)
 */
@RestController
@RequestMapping("/api/lab")
public class LabController {

    private final AtomicBoolean healthy = new AtomicBoolean(true);
    private final AtomicLong stressUntil = new AtomicLong();
    private final DataSource dataSource;
    private final Environment env;

    @Value("${cloudmart.storage}") String storage;
    @Value("${cloudmart.cache}") String cache;
    @Value("${cloudmart.cart}") String cart;
    @Value("${cloudmart.events}") String events;
    @Value("${cloudmart.notifier}") String notifier;
    @Value("${cloudmart.tracking}") String tracking;

    public LabController(DataSource dataSource, Environment env) {
        this.dataSource = dataSource;
        this.env = env;
    }

    @GetMapping("/whoami")
    public Map<String, Object> whoami() throws Exception {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("time", Instant.now().toString());
        m.put("host", InetAddress.getLocalHost().getHostName());
        m.put("instanceId", Metadata.get("instance-id"));
        m.put("az", Metadata.get("placement/availability-zone"));
        m.put("db", dbUrl());
        m.put("healthy", healthy.get());
        m.put("stressing", System.currentTimeMillis() < stressUntil.get());
        return m;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("profiles", Arrays.asList(env.getActiveProfiles()));
        m.put("storage", storage);
        m.put("cache", cache);
        m.put("cart", cart);
        m.put("events", events);
        m.put("notifier", notifier);
        m.put("tracking", tracking);
        m.put("javaVersion", System.getProperty("java.version"));
        m.put("cpus", Runtime.getRuntime().availableProcessors());
        return m;
    }

    @PostMapping("/stress")
    public Map<String, Object> stress(@RequestParam(defaultValue = "120") int seconds) {
        int s = Math.max(1, Math.min(seconds, 900));
        int cores = Runtime.getRuntime().availableProcessors();
        long end = System.currentTimeMillis() + s * 1000L;
        stressUntil.accumulateAndGet(end, Math::max);
        for (int i = 0; i < cores; i++) {
            Thread t = new Thread(() -> {
                double x = 0;
                while (System.currentTimeMillis() < end) x += Math.sqrt(Math.random());
            }, "stress-" + i);
            t.setDaemon(true);
            t.start();
        }
        return Map.of("cores", cores, "seconds", s, "instanceId", Metadata.get("instance-id"));
    }

    @PostMapping("/health/{state}")
    public Map<String, Object> setHealth(@PathVariable String state) {
        healthy.set(!"down".equalsIgnoreCase(state));
        return Map.of("healthy", healthy.get(), "instanceId", Metadata.get("instance-id"));
    }

    /** Point the ALB / target group health check here. */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return healthy.get() ? ResponseEntity.ok("UP") : ResponseEntity.status(503).body("DOWN");
    }

    @GetMapping("/slow")
    public Map<String, Object> slow(@RequestParam(defaultValue = "5") int seconds) throws InterruptedException {
        int s = Math.max(0, Math.min(seconds, 120));
        Thread.sleep(s * 1000L);
        return Map.of("slept", s);
    }

    private String dbUrl() {
        try (Connection c = dataSource.getConnection()) {
            return c.getMetaData().getURL();
        } catch (Exception e) {
            return "DB ERROR: " + e.getMessage();   // visible on the Lab page during an RDS failover
        }
    }
}
