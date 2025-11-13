package com.db_writes_service.exceptions;

public class MongoOperationException extends RuntimeException {
    public MongoOperationException(String message) {
        super(message);
    }

    public MongoOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

