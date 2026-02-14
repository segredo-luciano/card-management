package com.hyperativa.card_management.api.dto.request;

public record AuthRequest(
        String login,
        String password
) {}
