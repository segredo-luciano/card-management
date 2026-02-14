package com.hyperativa.card_management.application.usecase;

import com.hyperativa.card_management.domain.Card;

public interface CreateCardUseCase {

	Card execute(Long batchId, Integer orderNumber, String cardNumberPlain);
}
