package com.hyperativa.card_management.api.exception;

public class CardAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = -4264179040997529026L;

	public CardAlreadyExistsException() {
        super("Card already exists!");
    }
}