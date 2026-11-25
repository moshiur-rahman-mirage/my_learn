package com.cloudmart.lab;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reads EC2 instance metadata with IMDSv2 (token first, then the value).
 * Returns "local" when not on EC2 (laptop, Fargate). Values are cached: they never change for an instance.
 */
public final class Metadata {

    private static final String BASE = "http://169.254.169.254/latest";
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(300)).build();
    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();
    private static volatile boolean notOnEc2;

    private Metadata() {}

    public static String get(String path) {
        if (notOnEc2) return "local";
        return CACHE.computeIfAbsent(path, Metadata::fetch);
    }

    private static String fetch(String path) {
        try {
            HttpRequest tokenReq = HttpRequest.newBuilder(URI.create(BASE + "/api/token"))
                    .timeout(Duration.ofMillis(500))
                    .header("X-aws-ec2-metadata-token-ttl-seconds", "60")
                    .PUT(HttpRequest.BodyPublishers.noBody()).build();
            String token = HTTP.send(tokenReq, HttpResponse.BodyHandlers.ofString()).body();
            HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + "/meta-data/" + path))
                    .timeout(Duration.ofMillis(500))
                    .header("X-aws-ec2-metadata-token", token).build();
            HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            return res.statusCode() == 200 ? res.body() : "unknown";
        } catch (Exception e) {
            notOnEc2 = true;       // don't pay the timeout on every request
            return "local";
        }
    }
}
