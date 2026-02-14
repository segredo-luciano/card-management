package com.hyperativa.card_management.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtServiceTest {

	private JwtService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
    }

    @Test
    void shouldExtractUsernameFromValidToken() {

        String token = jwtService.generateToken("admin", "ADMIN");

        String username = jwtService.extractUsername(token);

        assertEquals("admin", username);
    }
}
