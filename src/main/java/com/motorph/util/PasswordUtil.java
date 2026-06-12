

package com.motorph.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    
    public static String hashPassword(String password) throws Exception {
        
        // Generate salt
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        
        // Hash password
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        
        byte[] hash = factory.generateSecret(spec).getEncoded();
        
        // Encode for storage
        String saltEncoded = Base64.getEncoder().encodeToString(salt);
        String hashEncoded = Base64.getEncoder().encodeToString(hash);
        
        return saltEncoded + ":" + hashEncoded;
    }
    
    public static boolean verifyPassword(String password, String storedHash) throws Exception {
        
        String[] parts = storedHash.split(":");
        
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] originalHash = Base64.getDecoder().decode(parts[1]);
        
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
           
        byte[] testHash = factory.generateSecret(spec).getEncoded();
        
        return java.util.Arrays.equals(originalHash, testHash);
        
    }
}
