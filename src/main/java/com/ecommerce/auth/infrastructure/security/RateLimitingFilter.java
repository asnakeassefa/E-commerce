package com.ecommerce.auth.infrastructure.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting filter using the token bucket algorithm (Bucket4j).
 *
 * Each unique IP + endpoint combination gets its own bucket.
 * When a bucket runs out of tokens the request is rejected with 429.
 *
 * Limits (per IP, per minute):
 *   /login               → 5 requests
 *   /forgot-password     → 3 requests   (prevents email bombing)
 *   /resend-verification → 3 requests   (prevents email bombing)
 *   /register            → 5 requests
 *   /reset-password      → 5 requests
 *   everything else      → 20 requests
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String ip       = extractClientIp(request);
        String uri      = request.getRequestURI();
        String bucketKey = ip + ":" + uri;

        Bucket bucket = buckets.computeIfAbsent(bucketKey, key -> createBucket(uri));

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(buildJson());
        }
    }

    private Bucket createBucket(String uri) {
        int capacity = resolveCapacity(uri);
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private int resolveCapacity(String uri) {
        if (uri.endsWith("/forgot-password") || uri.endsWith("/resend-verification")) {
            return 3;
        }
        if (uri.endsWith("/login") || uri.endsWith("/register") || uri.endsWith("/reset-password")) {
            return 5;
        }
        return 20;
    }

    /**
     * Respects X-Forwarded-For so rate limiting works correctly behind a proxy or load balancer.
     */
    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String buildJson() {
        return String.format(
                "{\"status\":429,\"error\":\"Too Many Requests\"," +
                "\"message\":\"Too many requests. Please slow down and try again later.\"," +
                "\"timestamp\":\"%s\"}",
                LocalDateTime.now()
        );
    }
}
