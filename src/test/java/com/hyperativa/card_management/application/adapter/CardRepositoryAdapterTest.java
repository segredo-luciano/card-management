package com.hyperativa.card_management.application.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hyperativa.card_management.domain.Card;
import com.hyperativa.card_management.infrastructure.repository.CardRepository;

@ExtendWith(MockitoExtension.class)
class CardRepositoryAdapterTest {

    @Mock
    private CardRepository repository;

    @InjectMocks
    private CardRepositoryAdapter adapter;

    @Test
    void shouldFindByHash() {

        Card card = Card.builder().id("12df34gh").build();

        when(repository.findByCardHashAndActiveTrue("hash"))
                .thenReturn(Optional.of(card));

        Optional<Card> result = adapter.findByHash("hash");

        assertTrue(result.isPresent());
        assertEquals("12df34gh", result.get().getId());

        verify(repository).findByCardHashAndActiveTrue("hash");
    }

    @Test
    void shouldReturnEmptyWhenCardNotFound() {

        when(repository.findByCardHashAndActiveTrue("hash"))
                .thenReturn(Optional.empty());

        Optional<Card> result = adapter.findByHash("hash");

        assertTrue(result.isEmpty());

        verify(repository).findByCardHashAndActiveTrue("hash");
    }

    @Test
    void shouldSaveCard() {

        Card card = Card.builder().id("12df34gh").build();

        when(repository.save(card)).thenReturn(card);

        Card result = adapter.save(card);

        assertEquals(card, result);

        verify(repository).save(card);
    }

    @Test
    void shouldSaveAllCards() {

        List<Card> cards = List.of(
                Card.builder().id("12df34gh").build(),
                Card.builder().id("19la28ks").build()
        );

        when(repository.saveAll(cards)).thenReturn(cards);

        List<Card> result = adapter.saveAll(cards);

        assertEquals(2, result.size());

        verify(repository).saveAll(cards);
    }
}

