package com.hyperativa.card_management.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateCardRequest(Long batchId, Integer orderNumber, @NotNull(message = "cardNumber is required") String cardNumber) { }
