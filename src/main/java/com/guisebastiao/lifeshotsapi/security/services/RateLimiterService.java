package com.guisebastiao.lifeshotsapi.security.services;

import com.guisebastiao.lifeshotsapi.util.IpExtractor;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private static final int LIMIT = 200;
    private static final Duration WINDOW = Duration.ofMinutes(1);
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean allowRequest(HttpServletRequest request) {
        String ip = IpExtractor.extract(request);
        Bucket bucket = buckets.computeIfAbsent(ip, this::createBucket);
        return bucket.tryConsume(1);
    }


    private Bucket createBucket(String key) {
        Bandwidth limit = Bandwidth.classic(LIMIT, Refill.intervally(LIMIT, WINDOW));
        return Bucket.builder().addLimit(limit).build();
    }
}