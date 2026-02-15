package com.hyperativa.card_management.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.hyperativa.card_management.api.exception.CardAlreadyExistsException;
import com.hyperativa.card_management.domain.Card;
import com.hyperativa.card_management.domain.port.CardRepositoryPort;
import com.hyperativa.card_management.security.CryptoService;

@ExtendWith(MockitoExtension.class)
class CreateCardServiceTest {

    @Mock
    private CardRepositoryPort repository;

    @Mock
    private CryptoService cryptoService;

    @InjectMocks
    private CreateCardService service;

    @Test
    void shouldCreateCardSuccessfully() {
        Long batchId = 1L;
        Integer orderNumber = 10;
        String cardNumber = "1234567890123456";

        String hash = "hash123";
        byte[] encrypted = "encrypted".getBytes();

        Card savedCard = Card.builder()
                .id("123g5678j")
                .batchId(batchId)
                .orderNumber(orderNumber)
                .cardHash(hash)
                .cardNumber(encrypted)
                .build();

        when(cryptoService.sha256(cardNumber)).thenReturn(hash);
        when(cryptoService.encryptAES(cardNumber)).thenReturn(encrypted);
        when(repository.save(any(Card.class))).thenReturn(savedCard);

        Card result = service.execute(batchId, orderNumber, cardNumber);

        assertNotNull(result);
        assertEquals("123g5678j", result.getId());

        verify(cryptoService).sha256(cardNumber);
        verify(cryptoService).encryptAES(cardNumber);
        verify(repository).save(any(Card.class));
    }

    @Test
    void shouldThrowExceptionWhenCardNumberIsNotNumeric() {

        String invalidCard = "123ABC";

        assertThrows(IllegalArgumentException.class,
                () -> service.execute(1L, 1, invalidCard));

        verifyNoInteractions(repository);
        verifyNoInteractions(cryptoService);
    }

    @Test
    void shouldThrowCardAlreadyExistsException() {

        String cardNumber = "1234567890123456";

        when(cryptoService.sha256(cardNumber)).thenReturn("hash");
        when(cryptoService.encryptAES(cardNumber)).thenReturn("enc".getBytes());
        when(repository.save(any(Card.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(CardAlreadyExistsException.class,
                () -> service.execute(1L, 1, cardNumber));
    }
}

