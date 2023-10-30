package com.slukovskyi.bidorama.exceptions;

public class UnableToBidException extends RuntimeException {

    public UnableToBidException(String message) {
        super(message);
    }

    public UnableToBidException(String message, Throwable cause) {
        super(message, cause);
    }
}
