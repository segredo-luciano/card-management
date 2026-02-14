package com.hyperativa.card_management.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperativa.card_management.application.service.impl.CardServiceImpl;

public class CardNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2412453673671906382L;
	private static final Logger log = LoggerFactory.getLogger(CardServiceImpl.class);

	public CardNotFoundException() {
        super("Card not found");
    	log.info("The card you are searching were not found");
    }
}
