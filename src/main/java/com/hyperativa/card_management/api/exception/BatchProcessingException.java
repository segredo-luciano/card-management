package com.hyperativa.card_management.api.exception;

public class BatchProcessingException extends RuntimeException {
    private static final long serialVersionUID = 7365049256993331370L;

	public BatchProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
