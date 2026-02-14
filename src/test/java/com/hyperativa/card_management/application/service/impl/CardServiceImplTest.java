package com.hyperativa.card_management.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;

import com.hyperativa.card_management.api.dto.response.BatchInsertResponse;
import com.hyperativa.card_management.api.dto.response.CardResponse;
import com.hyperativa.card_management.api.exception.BatchProcessingException;
import com.hyperativa.card_management.api.exception.CardAlreadyExistsException;
import com.hyperativa.card_management.api.exception.CardNotFoundException;
import com.hyperativa.card_management.config.BatchFileProperties;
import com.hyperativa.card_management.domain.Card;
import com.hyperativa.card_management.infrastructure.repository.CardRepository;
import com.hyperativa.card_management.security.CryptoService;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository repository;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private BatchFileProperties batchProps;

    @InjectMocks
    private CardServiceImpl service;

    @Test
    void shouldCreateCardSuccessfully() {

        String cardNumber = "123456";

        when(cryptoService.sha256(cardNumber)).thenReturn("hash");
        when(cryptoService.encryptAES(cardNumber)).thenReturn(new byte[]{1, 2});

        Card savedCard = Card.builder()
                .id("123f456g")
                .batchId(10L)
                .orderNumber(1)
                .cardHash("hash")
                .cardNumber(new byte[]{1, 2})
                .build();

        when(repository.save(any(Card.class))).thenReturn(savedCard);

        Card result = service.createCard(10L, 1, cardNumber);

        assertNotNull(result);
        assertEquals("123f456g", result.getId());

        verify(cryptoService).sha256(cardNumber);
        verify(cryptoService).encryptAES(cardNumber);
        verify(repository).save(any(Card.class));
    }

    @Test
    void shouldThrowExceptionWhenCardNumberIsNotNumeric() {

        String invalidCard = "ABC123";

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createCard(10L, 1, invalidCard)
        );

        assertEquals("cardNumber must contain only digits", ex.getMessage());

        verifyNoInteractions(cryptoService);
        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowCardAlreadyExistsException() {

        String cardNumber = "123456";

        when(cryptoService.sha256(cardNumber)).thenReturn("hash");
        when(cryptoService.encryptAES(cardNumber)).thenReturn(new byte[]{1});

        when(repository.save(any(Card.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(CardAlreadyExistsException.class,
                () -> service.createCard(10L, 1, cardNumber));

        verify(repository).save(any(Card.class));
    }
    
    @Test
    void shouldReturnCardResponseWhenCardExists() {

        String cardNumber = "123456";
        String hash = "hashed";

        Card card = Card.builder()
                .id("123f456g")
                .build();

        when(cryptoService.sha256(cardNumber)).thenReturn(hash);
        when(repository.findByCardHashAndActiveTrue(hash))
                .thenReturn(Optional.of(card));

        CardResponse response = service.handleCardExistence(cardNumber);

        assertNotNull(response);
        assertEquals("123f456g", response.cardId());

        verify(cryptoService).sha256(cardNumber);
        verify(repository).findByCardHashAndActiveTrue(hash);
    }

    @Test
    void shouldThrowExceptionWhenCardDoesNotExist() {

        String cardNumber = "123456";
        String hash = "hashed";

        when(cryptoService.sha256(cardNumber)).thenReturn(hash);
        when(repository.findByCardHashAndActiveTrue(hash))
                .thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> service.handleCardExistence(cardNumber));

        verify(repository).findByCardHashAndActiveTrue(hash);
    }

    @Test
    void shouldProcessFileSuccessfully() throws Exception {

        String header = " ".repeat(37) + "00000001" + "000002";

        String line1 = "C0000011234567890123456";
        String line2 = "C0000029999999999999999";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cards.txt",
                "text/plain",
                (header + "\n" + line1 + "\n" + line2).getBytes()
        );

        when(batchProps.bufferSize()).thenReturn(1024);
        when(batchProps.chunkSize()).thenReturn(10);

        when(cryptoService.sha256(anyString())).thenReturn("hash");
        when(cryptoService.encryptAES(anyString())).thenReturn(new byte[]{1});

        when(repository.saveAll(anyList()))
                .thenAnswer(i -> i.getArgument(0));

        BatchInsertResponse response = service.processFile(file);

        assertEquals(2, response.totalProcessed());
        assertEquals(2, response.inserted());
        assertEquals(0, response.failed());

        verify(repository).saveAll(anyList());
    }


    @Test
    void shouldCountFailedLines() throws Exception {

        String header =
                " ".repeat(37) + "00000001" + "000001";

        String invalidLine = "C000001ABC";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cards.txt",
                "text/plain",
                (header + "\n" + invalidLine).getBytes()
        );

        when(batchProps.bufferSize()).thenReturn(1024);

        BatchInsertResponse response = service.processFile(file);

        assertEquals(1, response.totalProcessed());
        assertEquals(0, response.inserted());
        assertEquals(1, response.failed());
    }

    @Test
    void shouldThrowExceptionWhenFileIsEmpty() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[]{}
        );

        when(batchProps.bufferSize()).thenReturn(1024);

        assertThrows(BatchProcessingException.class,
                () -> service.processFile(file));
    }
}