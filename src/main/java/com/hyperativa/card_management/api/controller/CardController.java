package com.hyperativa.card_management.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hyperativa.card_management.api.dto.request.CreateCardRequest;
import com.hyperativa.card_management.api.dto.response.BatchInsertResponse;
import com.hyperativa.card_management.api.dto.response.CardResponse;
import com.hyperativa.card_management.application.usecase.BatchFileProcessmentUseCase;
import com.hyperativa.card_management.application.usecase.CheckCardExistsUseCase;
import com.hyperativa.card_management.application.usecase.CreateCardUseCase;
import com.hyperativa.card_management.domain.Card;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cards")
public class CardController {

    private static final Logger log = LoggerFactory.getLogger(CardController.class);
    private final CreateCardUseCase createCardUseCase;
    private final CheckCardExistsUseCase checkCardExistsUseCase;
    private final BatchFileProcessmentUseCase batchFileProcessmentUseCase;

    public CardController(CreateCardUseCase createCardUseCase,
    		CheckCardExistsUseCase checkCardExistsUseCase,
    		BatchFileProcessmentUseCase batchFileProcessmentUseCase) {
        this.createCardUseCase = createCardUseCase;
        this.checkCardExistsUseCase = checkCardExistsUseCase;
        this.batchFileProcessmentUseCase = batchFileProcessmentUseCase;
    }

    @GetMapping
    public ResponseEntity<CardResponse> check(@RequestParam String cardNumber) {    	
    	CardResponse card = checkCardExistsUseCase.execute(cardNumber);
    	log.info("Card found successfully. id={}", card.cardId());
    	return ResponseEntity.ok(card);
    }

    @PostMapping
    public ResponseEntity<CardResponse> create(@Valid @RequestBody CreateCardRequest request) {
        Card card = createCardUseCase.execute(
                request.batchId(),
                request.orderNumber(),
                request.cardNumber()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new CardResponse(card.getId()));
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchInsertResponse> upload(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(batchFileProcessmentUseCase.execute(file));
    }
}

