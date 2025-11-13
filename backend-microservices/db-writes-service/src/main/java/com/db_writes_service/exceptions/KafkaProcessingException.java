package com.db_writes_service.exceptions;

public class KafkaProcessingException extends RuntimeException {
    public KafkaProcessingException(String message) {
        super(message);
    }

    public KafkaProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

