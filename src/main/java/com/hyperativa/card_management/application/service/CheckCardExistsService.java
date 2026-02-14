package com.hyperativa.card_management.application.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyperativa.card_management.api.dto.response.CardResponse;
import com.hyperativa.card_management.api.exception.CardNotFoundException;
import com.hyperativa.card_management.application.usecase.CheckCardExistsUseCase;
import com.hyperativa.card_management.config.BatchFileProperties;
import com.hyperativa.card_management.domain.Card;
import com.hyperativa.card_management.domain.port.CardRepositoryPort;
import com.hyperativa.card_management.security.CryptoService;

@Service
public class CheckCardExistsService implements CheckCardExistsUseCase {

	private final CardRepositoryPort repository;    
    private final CryptoService cryptoService;    
    private static final Logger log = LoggerFactory.getLogger(CheckCardExistsService.class);
  

    public CheckCardExistsService(CardRepositoryPort repository, CryptoService cryptoService,
    		BatchFileProperties batchProps) {
        this.repository = repository;    
        this.cryptoService = cryptoService;        
    }
	
    @Override
    public CardResponse execute(String cardNumber) {
    	return new CardResponse(checkCardExists(cardNumber).map(Card::getId)
    			.orElseThrow(() -> new CardNotFoundException()));
    }
    
	@Transactional(readOnly = true)
	private Optional<Card> checkCardExists(String cardNumberPlain) {
		String hash = cryptoService.sha256(cardNumberPlain);
		log.info("Checking if card exists");
        return repository.findByHash(hash);
	}
	
}
