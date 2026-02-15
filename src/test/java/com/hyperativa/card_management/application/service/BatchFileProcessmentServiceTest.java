package com.hyperativa.card_management.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.hyperativa.card_management.api.dto.response.BatchInsertResponse;
import com.hyperativa.card_management.config.BatchFileProperties;
import com.hyperativa.card_management.domain.port.CardRepositoryPort;
import com.hyperativa.card_management.security.CryptoService;

@ExtendWith(MockitoExtension.class)
class BatchFileProcessmentServiceTest {

    @Mock
    private CardRepositoryPort repository;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private BatchFileProperties batchProps;

    @InjectMocks
    private BatchFileProcessmentService service;

    @Mock
    private MultipartFile file;

    @Test
    void shouldProcessFileSuccessfully() throws Exception {

        when(batchProps.bufferSize()).thenReturn(1024);
        when(batchProps.chunkSize()).thenReturn(10);

        String header = "DESAFIO-HYPERATIVA           20180524LOTE0001000010";
        String line1  = "C1     0000012222222222";
        String line2  = "C2     0000029999999999999999";

        String content = header + "\n" + line1 + "\n" + line2;

        when(file.getInputStream())
                .thenReturn(new ByteArrayInputStream(content.getBytes()));

        when(cryptoService.sha256(anyString())).thenReturn("hash");
        when(cryptoService.encryptAES(anyString())).thenReturn(new byte[]{1});

        BatchInsertResponse response = service.execute(file);

        assertEquals(2, response.totalProcessed());
        assertEquals(2, response.inserted());
        assertEquals(0, response.failed());

        verify(repository).saveAll(anyList());
    }
}

