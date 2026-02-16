package com.hyperativa.card_management.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void shouldGenerateIdCreatedAtAndActiveOnPrePersistWhenIdIsNull() {
        User user = User.builder()
                .login("luciano")
                .passwordHash("hash")
                .role("ADMIN")
                .build();

        user.prePersist();

        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertTrue(user.isActive());
    }

    @Test
    void shouldNotOverrideExistingIdOnPrePersist() {
        String existingId = UUID.randomUUID().toString();

        User user = User.builder()
                .id(existingId)
                .login("luciano")
                .passwordHash("hash")
                .role("ADMIN")
                .build();

        user.prePersist();

        assertEquals(existingId, user.getId());
        assertNotNull(user.getCreatedAt());
        assertTrue(user.isActive());
    }

    @Test
    void shouldCreateUserUsingBuilder() {
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id("123")
                .active(false)
                .createdAt(now)
                .login("luciano")
                .passwordHash("hash")
                .role("ADMIN")
                .build();

        assertEquals("123", user.getId());
        assertFalse(user.isActive());
        assertEquals(now, user.getCreatedAt());
        assertEquals("luciano", user.getLogin());
        assertEquals("hash", user.getPasswordHash());
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void shouldGenerateValidUUID() {
        User user = User.builder()
                .login("luciano")
                .passwordHash("hash")
                .role("ADMIN")
                .build();

        user.prePersist();

        assertDoesNotThrow(() -> UUID.fromString(user.getId()));
    }
}
