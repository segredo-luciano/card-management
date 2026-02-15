package com.hyperativa.card_management.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hyperativa.card_management.api.dto.response.CardResponse;
import com.hyperativa.card_management.api.exception.CardNotFoundException;
import com.hyperativa.card_management.domain.Card;
import com.hyperativa.card_management.domain.port.CardRepositoryPort;
import com.hyperativa.card_management.security.CryptoService;

@ExtendWith(MockitoExtension.class)
class CheckCardExistsServiceTest {

    @Mock
    private CardRepositoryPort repository;

    @Mock
    private CryptoService cryptoService;

    @InjectMocks
    private CheckCardExistsService service;

    @Test
    void shouldReturnCardResponseWhenCardExists() {

        String cardNumber = "123456789";
        String hash = "hashed";

        Card card = Card.builder()
                .id("12fg34")
                .build();

        when(cryptoService.sha256(cardNumber)).thenReturn(hash);
        when(repository.findByHash(hash)).thenReturn(Optional.of(card));

        CardResponse response = service.execute(cardNumber);

        assertEquals("12fg34", response.cardId());

        verify(cryptoService).sha256(cardNumber);
        verify(repository).findByHash(hash);
    }

    @Test
    void shouldThrowExceptionWhenCardNotFound() {

        String cardNumber = "123456789";
        String hash = "hashed";

        when(cryptoService.sha256(cardNumber)).thenReturn(hash);
        when(repository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> service.execute(cardNumber));

        verify(cryptoService).sha256(cardNumber);
        verify(repository).findByHash(hash);
    }
}
