package com.motorph.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_ATTEMPTS = 3;

    private String currentOtp;
    private LocalDateTime expiresAt;
    private int attempts;

    public String generateOtp() {
        int code = RANDOM.nextInt(900000) + 100000;
        currentOtp = String.valueOf(code);
        expiresAt = LocalDateTime.now().plusMinutes(5);
        attempts = 0;
        return currentOtp;
    }

    public boolean verifyOtp(String input) {
        if (currentOtp == null || expiresAt == null) return false;
        if (LocalDateTime.now().isAfter(expiresAt)) return false;
        if (attempts >= MAX_ATTEMPTS) return false;

        attempts++;

        boolean valid = currentOtp.equals(input == null ? "" : input.trim());

        if (valid) {
            clear();
        }

        return valid;
    }

    public boolean isExpired() {
        return expiresAt == null || LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean hasAttemptsLeft() {
        return attempts < MAX_ATTEMPTS;
    }

    public int getRemainingAttempts() {
        return MAX_ATTEMPTS - attempts;
    }

    public void clear() {
        currentOtp = null;
        expiresAt = null;
        attempts = 0;
    }
}