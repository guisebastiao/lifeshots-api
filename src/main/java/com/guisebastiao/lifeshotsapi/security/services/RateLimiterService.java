package com.guisebastiao.lifeshotsapi.security.services;

import com.guisebastiao.lifeshotsapi.util.IpExtractor;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    private static final int SENSITIVE_LIMIT = 100;
    private static final int GENERAL_LIMIT = 200;
    private static final Duration WINDOW = Duration.ofMinutes(1);
    private static final List<String> SENSITIVE_PATHS = List.of("/auth/", "/recover-password/");

    private final ConcurrentHashMap<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(HttpServletRequest request) {
        String ip = IpExtractor.extract(request);
        boolean sensitive = isSensitivePath(request.getRequestURI());
        String key = (sensitive ? "sensitive:" : "general:") + ip;

        return bucketCache.computeIfAbsent(key, k -> buildBucket(sensitive));
    }

    private Bucket buildBucket(boolean sensitive) {
        int capacity  = sensitive ? SENSITIVE_LIMIT : GENERAL_LIMIT;
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(capacity, WINDOW));
        return Bucket.builder().addLimit(limit).build();
    }

    private boolean isSensitivePath(String path) {
        return SENSITIVE_PATHS.stream().anyMatch(path::startsWith);
    }
}
