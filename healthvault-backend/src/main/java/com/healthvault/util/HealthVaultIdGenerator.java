package com.healthvault.util;

import java.util.Random;

public class HealthVaultIdGenerator {
    
    private static final String PREFIX = "HV-";
    private static final int LENGTH = 6;
    private static final Random random = new Random();

    public static String generateId() {
        StringBuilder sb = new StringBuilder(PREFIX);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
