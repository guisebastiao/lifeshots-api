package com.lifeshots.lifeshotsapi.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenGenerator {

    private int byteLength = 32;

    public String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokennBytes = new byte[this.byteLength];
        secureRandom.nextBytes(tokennBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokennBytes);
    }

}
