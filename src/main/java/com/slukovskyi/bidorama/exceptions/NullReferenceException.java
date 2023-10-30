package com.slukovskyi.bidorama.exceptions;

public class NullReferenceException extends RuntimeException {

    public NullReferenceException(String message) {
        super(message);
    }

    public NullReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
