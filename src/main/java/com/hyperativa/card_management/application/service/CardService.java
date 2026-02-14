package com.hyperativa.card_management.application.service;

import org.springframework.web.multipart.MultipartFile;

import com.hyperativa.card_management.api.dto.response.BatchInsertResponse;
import com.hyperativa.card_management.api.dto.response.CardResponse;
import com.hyperativa.card_management.domain.Card;

public interface CardService {

	Card createCard(Long batchId, Integer orderNumber, String cardNumberPlain);
	
    CardResponse handleCardExistence(String cardNumberPlain);
    
    BatchInsertResponse processFile(MultipartFile file);
}
