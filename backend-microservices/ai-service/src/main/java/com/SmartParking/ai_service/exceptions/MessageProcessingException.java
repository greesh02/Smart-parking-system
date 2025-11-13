package com.SmartParking.ai_service.exceptions;

public class MessageProcessingException extends AiServiceException {

    public MessageProcessingException(String message) {
        super(message);
    }

    public MessageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

