package com.hyperativa.card_management.api.exception;

public class BatchFileEmptyException extends RuntimeException {
    private static final long serialVersionUID = -308924031164483839L;

	public BatchFileEmptyException() {
        super("Batch file is empty");
    }
}
