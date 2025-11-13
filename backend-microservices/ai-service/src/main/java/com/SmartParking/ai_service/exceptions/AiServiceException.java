package com.SmartParking.ai_service.exceptions;

public abstract class AiServiceException extends RuntimeException {

    protected AiServiceException(String message) {
        super(message);
    }

    protected AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

