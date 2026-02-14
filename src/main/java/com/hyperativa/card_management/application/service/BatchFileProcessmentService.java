package com.hyperativa.card_management.application.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hyperativa.card_management.api.dto.response.BatchInsertResponse;
import com.hyperativa.card_management.api.exception.BatchProcessingException;
import com.hyperativa.card_management.application.usecase.BatchFileProcessmentUseCase;
import com.hyperativa.card_management.config.BatchFileProperties;
import com.hyperativa.card_management.domain.Card;
import com.hyperativa.card_management.domain.port.CardRepositoryPort;
import com.hyperativa.card_management.security.CryptoService;

@Service
public class BatchFileProcessmentService implements BatchFileProcessmentUseCase {

	private final CardRepositoryPort repository;    
    private final CryptoService cryptoService;
    private final BatchFileProperties batchProps;
    private static final Logger log = LoggerFactory.getLogger(BatchFileProcessmentService.class);
  

    public BatchFileProcessmentService(CardRepositoryPort repository, CryptoService cryptoService,
    		BatchFileProperties batchProps) {
        this.repository = repository;    
        this.cryptoService = cryptoService;
        this.batchProps = batchProps;
    }
	
	@Override
    public BatchInsertResponse execute(MultipartFile file) {
    	log.info("Starting batch file processing");

        int processed = 0;
        int inserted = 0;
        int failed = 0;

        long start = System.currentTimeMillis();
        
        List<Card> batchCards = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()),
                batchProps.bufferSize())) {

            String header = reader.readLine();

            if (header == null)
                throw new RuntimeException("File is empty");           

            String batch = header.substring(37, 45).trim();
            Long batchId = Long.parseLong(batch.replaceAll("\\D", ""));

            int expectedRecords =
                    Integer.parseInt(header.substring(45, 51).trim());

            log.info("Batch detected: {}", batchId);
            log.info("Expected records: {}", expectedRecords);

            String line;
            String lineNum = "";

            while ((line = reader.readLine()) != null) {

                try {

                    if (line.isBlank() || line.startsWith("LOTE")) {
                        continue;
                    }

                    processed++;

                    String paddedLine = line.length() < 26
                            ? line + " ".repeat(26 - line.length())
                            : line;

                    lineNum = line.substring(0,2);
                    Integer orderNumber =
                            Integer.parseInt(paddedLine.substring(1, 7).trim());

                    String cardNumber =
                            paddedLine.substring(7, 26).trim();
                    
                    if (!cardNumber.matches("\\d+"))
                        throw new IllegalArgumentException("cardNumber must contain only digits");                    

                    String hash = cryptoService.sha256(cardNumber);

                    batchCards.add(Card.builder()
                            .batchId(batchId)
                            .orderNumber(orderNumber)
                            .cardHash(hash)
                            .cardNumber(cryptoService.encryptAES(cardNumber))
                            .build());

                    if (batchCards.size() >= batchProps.chunkSize()) {
                        inserted += saveBatch(batchCards);                        
                        batchCards.clear();
                    }

                } catch (Exception e) {
                    log.error("line {} failed to be processed! reason: {}", lineNum, e.getMessage());
                    failed++;
                }
            }

            if (!batchCards.isEmpty()) {
                inserted += saveBatch(batchCards);
            }

            if (processed != expectedRecords) {
                log.error("Record count mismatch. Expected={}, Found={}",
                        expectedRecords, processed);
            }
        } catch (Exception e) {
            throw new BatchProcessingException("Error processing batch file", e);
        }

        long time = System.currentTimeMillis() - start;

        BatchInsertResponse batchResponse = new BatchInsertResponse(processed, inserted, (processed-inserted-failed), failed, time);
        log.info("Batch file processment status: \n"+ batchResponse.toString());
        return batchResponse;
    }

    private int saveBatch(List<Card> cards) {

        int inserted = 0;

        try {
            repository.saveAll(cards);
            inserted = cards.size();

        } catch (DataIntegrityViolationException e) {

            for (Card card : cards) {
                try {
                    repository.save(card);
                    inserted++;
                } catch (DataIntegrityViolationException ex) {
                	
                }
            }
        }

        return inserted;
    }
}
