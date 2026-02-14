package com.hyperativa.card_management.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class CryptoServiceTest {

    private CryptoService cryptoService;

    @BeforeEach
    void setup() {

        byte[] key = new byte[32];
        for (int i = 0; i < key.length; i++) key[i] = 1;

        String base64Key = Base64.getEncoder().encodeToString(key);

        cryptoService = new CryptoService(base64Key);
    }
    
    @Test
    void shouldGenerateSameHashForSameValue() {

        String hash1 = cryptoService.sha256("123");
        String hash2 = cryptoService.sha256("123");

        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length());
    }

    @Test
    void shouldGenerateDifferentHashForDifferentValues() {

        String hash1 = cryptoService.sha256("123");
        String hash2 = cryptoService.sha256("456");

        assertNotEquals(hash1, hash2);
    }

    @Test
    void shouldEncryptDeterministically() throws Exception {

        byte[] encrypted1 = cryptoService.encrypt("card");
        byte[] encrypted2 = cryptoService.encrypt("card");

        assertArrayEquals(encrypted1, encrypted2);
        assertNotEquals("card", new String(encrypted1));
    }

    @Test
    void shouldEncryptWithRandomIV() {

        byte[] encrypted1 = cryptoService.encryptAES("card");
        byte[] encrypted2 = cryptoService.encryptAES("card");

        assertNotNull(encrypted1);
        assertNotNull(encrypted2);

        assertNotEquals(
                new String(encrypted1),
                new String(encrypted2)
        );

        assertTrue(encrypted1.length > "card".length());
    }

}