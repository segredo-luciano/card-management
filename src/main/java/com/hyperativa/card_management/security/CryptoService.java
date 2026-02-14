package com.hyperativa.card_management.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {

    private final SecretKeySpec secretKey;
    private static final Logger log = LoggerFactory.getLogger(CryptoService.class);
    
    public CryptoService(@Value("${crypto.aes-secret}") String aesSecret) {
        
        byte[] decodedKey = Base64.getDecoder().decode(aesSecret);
        this.secretKey = new SecretKeySpec(decodedKey, "AES");
    }
    
    public String sha256(String value) {
	    try {
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

	        StringBuilder hex = new StringBuilder();
	        for (byte b : hash) {
	            hex.append(String.format("%02x", b));
	        }

	        return hex.toString();

	    } catch (NoSuchAlgorithmException e) {
	    	log.error("SHA-256 algorithm not available");
	        throw new IllegalStateException("SHA-256 algorithm not available", e);
	    }
	}

    public byte[] encrypt(String value) throws Exception {

        SecretKeySpec key = this.secretKey;

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
    }
    
    public byte[] encryptAES(String value) {

	    try {

	        byte[] iv = new byte[12];
	        SecureRandom random = new SecureRandom();
	        random.nextBytes(iv);

	        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

	        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

	        byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

	        ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
	        buffer.put(iv);
	        buffer.put(encrypted);

	        return buffer.array();

	    } catch (GeneralSecurityException e) {
	        throw new IllegalStateException("Error encrypting card data", e);
	    }
	}
}

