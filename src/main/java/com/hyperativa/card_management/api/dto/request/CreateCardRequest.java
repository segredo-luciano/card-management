package com.hyperativa.card_management.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCardRequest(Long batchId, Integer orderNumber, @NotBlank(message = "cardNumber is required") String cardNumber) { }
