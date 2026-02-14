package com.hyperativa.card_management.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.hyperativa.card_management.api.exception.CardAlreadyExistsException;
import com.hyperativa.card_management.application.usecase.CreateCardUseCase;
import com.hyperativa.card_management.domain.Card;
import com.hyperativa.card_management.domain.port.CardRepositoryPort;
import com.hyperativa.card_management.security.CryptoService;

@Service
public class CreateCardService implements CreateCardUseCase {

	private final CardRepositoryPort repository;    
    private final CryptoService cryptoService;
    private static final Logger log = LoggerFactory.getLogger(CreateCardService.class);
  

    public CreateCardService(CardRepositoryPort repository, CryptoService cryptoService) {
        this.repository = repository;    
        this.cryptoService = cryptoService;        
    }

    @Override
	public Card execute(Long batchId, Integer orderNumber, String cardNumber) {	
		
		if (!cardNumber.matches("\\d+")) {	
			log.error("Failed to insert card! cardNumber must contain only digits");
			throw new IllegalArgumentException("cardNumber must contain only digits");
		}
				
		String hash = cryptoService.sha256(cardNumber);
		
		
		try {		
			log.info("Tryng to save card!");
			byte[] encrypted = cryptoService.encryptAES(cardNumber);
			
			Card card = Card.builder()
					.batchId(batchId)
					.orderNumber(orderNumber)
					.cardHash(hash)
					.cardNumber(encrypted)
					.build();			
			
			Card saved = repository.save(card);
			
			log.info("Card created. id={}, batchId={}, orderNumber={}",
					saved.getId(), batchId, orderNumber);
			
			return saved;		
		} catch(DataIntegrityViolationException dive) {			
			log.error("Card already exists, throwing exception!");
			throw new CardAlreadyExistsException();		
		}
	}	
}
