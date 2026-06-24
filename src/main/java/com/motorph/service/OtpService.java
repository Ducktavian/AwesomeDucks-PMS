/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private String currentOtp;
    private LocalDateTime expiresAt;

    public String generateOtp() {
        int code = RANDOM.nextInt(900000) + 100000;
        currentOtp = String.valueOf(code);
        expiresAt = LocalDateTime.now().plusMinutes(5);
        return currentOtp;
    }

    public boolean verifyOtp(String input) {
        if (currentOtp == null || expiresAt == null) return false;
        if (LocalDateTime.now().isAfter(expiresAt)) return false;
        return currentOtp.equals(input);
    }

    public void clear() {
        currentOtp = null;
        expiresAt = null;
    }
}