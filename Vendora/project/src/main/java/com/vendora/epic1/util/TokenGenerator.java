package com.vendora.epic1.util;

import java.util.Base64;
import java.util.UUID;

public class TokenGenerator {

    public static String generateToken() {
        String uuid = UUID.randomUUID().toString() + System.currentTimeMillis();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(uuid.getBytes());
    }
}