package com.hyperativa.card_management.application.usecase;

import com.hyperativa.card_management.api.dto.response.CardResponse;

public interface CheckCardExistsUseCase {

    CardResponse execute(String cardNumber);
}
