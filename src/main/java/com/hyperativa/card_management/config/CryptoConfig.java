package com.hyperativa.card_management.config;

import java.security.Key;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class CryptoConfig {

    @Value("${crypto.aes-secret}")
    private String aesSecret;

    @Value("${crypto.jwt-secret}")
    private String jwtSecret;

    private SecretKey aesKey;
    private Key jwtKey;

    @PostConstruct
    public void init() {

        byte[] aesDecoded = Base64.getDecoder().decode(aesSecret);
        this.aesKey = new SecretKeySpec(aesDecoded, "AES");

        byte[] jwtDecoded = Base64.getDecoder().decode(jwtSecret);
        this.jwtKey = Keys.hmacShaKeyFor(jwtDecoded);
    }

    public SecretKey getAesKey() {
        return aesKey;
    }

    public Key getJwtKey() {
        return jwtKey;
    }
}
