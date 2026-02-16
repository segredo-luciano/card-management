package com.hyperativa.card_management.domain;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CardTest {

    @Test
    void shouldGenerateIdCreatedAtAndActiveOnPrePersistWhenIdIsNull() {
        Card card = Card.builder()
                .cardHash("hash")
                .cardNumber(new byte[]{1, 2, 3})
                .build();

        card.prePersist();

        assertNotNull(card.getId());
        assertNotNull(card.getCreatedAt());
        assertTrue(card.getActive());
    }

    @Test
    void shouldNotOverrideExistingIdOnPrePersist() {
        String existingId = UUID.randomUUID().toString();

        Card card = Card.builder()
                .id(existingId)
                .cardHash("hash")
                .cardNumber(new byte[]{1})
                .build();

        card.prePersist();

        assertEquals(existingId, card.getId());
        assertNotNull(card.getCreatedAt());
        assertTrue(card.getActive());
    }

    @Test
    void shouldSetCreatedAtEveryTimePrePersistIsCalled() {
        Card card = Card.builder()
                .cardHash("hash")
                .cardNumber(new byte[]{1})
                .build();

        card.prePersist();

        LocalDateTime firstCreatedAt = card.getCreatedAt();

        assertNotNull(firstCreatedAt);
    }

    @Test
    void shouldCreateCardUsingBuilder() {
        byte[] number = {1, 2, 3};

        Card card = Card.builder()
                .id("123")
                .active(false)
                .batchId(10L)
                .orderNumber(1)
                .cardNumber(number)
                .cardHash("hash")
                .createdAt(LocalDateTime.now())
                .build();

        assertEquals("123", card.getId());
        assertFalse(card.getActive());
        assertEquals(10L, card.getBatchId());
        assertEquals(1, card.getOrderNumber());
        assertArrayEquals(number, card.getCardNumber());
        assertEquals("hash", card.getCardHash());
    }
}

