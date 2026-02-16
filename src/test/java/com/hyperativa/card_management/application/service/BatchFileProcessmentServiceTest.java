package com.hyperativa.card_management.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.hyperativa.card_management.api.dto.response.BatchInsertResponse;
import com.hyperativa.card_management.api.exception.BatchProcessingException;
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
    
    private MultipartFile fileFromString(String content) {
        return new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content.getBytes(StandardCharsets.UTF_8)
        );
    }
    
    private void mockCrypto() {
        when(cryptoService.sha256(anyString())).thenReturn("hash");
        when(cryptoService.encryptAES(anyString())).thenReturn(new byte[]{1});
    }

    private void mockChunkSize(int size) {
        when(batchProps.chunkSize()).thenReturn(size);
        when(batchProps.bufferSize()).thenReturn(1024);
    }
    
    private String header(int expectedRecords) {
        return String.format("%37s%8s%6s",
                "",
                "00000001",
                String.format("%06d", expectedRecords)
        ) + "\n";
    }

    private String validLine(String lineNum, String order, String card) {
        return lineNum +
                String.format("%6s", order) +
                String.format("%-19s", card) +
                "\n";
    }



    @Test
    void shouldProcessFileSuccessfully() throws Exception {
        when(batchProps.bufferSize()).thenReturn(1024);
        when(batchProps.chunkSize()).thenReturn(10);

        String header = "DESAFIO-HYPERATIVA           20260216LOTE0001000010";
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
    
    @Test
    void shouldThrowBatchProcessingExceptionWhenFileIsEmpty() {
        MultipartFile file = fileFromString("");

        when(batchProps.bufferSize()).thenReturn(1024);

        assertThrows(BatchProcessingException.class,
                () -> service.execute(file));
    }
    
    @Test
    void shouldSkipBlankAndLoteLines() {
        String content =
                header(1) +
                "\n" +
                "LOTE XXXXX\n" +
                validLine("01", "000001", "1234567890123456");

        mockCrypto();
        mockChunkSize(10);

        BatchInsertResponse response = service.execute(fileFromString(content));

        assertEquals(1, response.totalProcessed());
    }

    
    @Test
    void shouldSaveInChunks() {
        when(batchProps.chunkSize()).thenReturn(2);
        when(batchProps.bufferSize()).thenReturn(1024);

        mockCrypto();

        String content =
                header(2) +
                validLine("01", "000001", "9999999999999999") +
                validLine("02", "000002", "999999999998");

        BatchInsertResponse response = service.execute(fileFromString(content));

        verify(repository).saveAll(anyList());
        assertEquals(2, response.inserted());
    }

}

