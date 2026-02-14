package com.hyperativa.card_management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch.file")
public record BatchFileProperties(
        int chunkSize,
        int bufferSize,
        int lineMinLength
) {}
