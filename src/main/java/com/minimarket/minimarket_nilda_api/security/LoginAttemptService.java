package com.minimarket.minimarket_nilda_api.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
private static final long LOCK_DURATION_SECONDS = 60;

    private record AttemptInfo(int failedAttempts, Instant lockedUntil) {}

    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        AttemptInfo info = attempts.get(key);
        if (info == null || info.lockedUntil() == null) {
            return false;
        }
        if (Instant.now().isAfter(info.lockedUntil())) {
            attempts.remove(key);
            return false;
        }
        return true;
    }

    public long getRemainingSeconds(String key) {
        AttemptInfo info = attempts.get(key);
        if (info == null || info.lockedUntil() == null) {
            return 0;
        }
        long seconds = Instant.now().until(info.lockedUntil(), java.time.temporal.ChronoUnit.SECONDS);
        return Math.max(seconds, 1);
    }

    public void recordFailedAttempt(String key) {
        Instant now = Instant.now();
        attempts.compute(key, (k, current) -> {
            if (current == null) {
                int count = 1;
                Instant lockUntil = count >= MAX_ATTEMPTS ? now.plusSeconds(LOCK_DURATION_SECONDS) : null;
                return new AttemptInfo(count, lockUntil);
            }
            if (current.lockedUntil() != null && now.isBefore(current.lockedUntil())) {
                return current;
            }
            int nextCount = current.failedAttempts() + 1;
            Instant lockUntil = nextCount >= MAX_ATTEMPTS ? now.plusSeconds(LOCK_DURATION_SECONDS) : null;
            return new AttemptInfo(nextCount, lockUntil);
        });
    }

    public void resetAttempts(String key) {
        attempts.remove(key);
    }
}