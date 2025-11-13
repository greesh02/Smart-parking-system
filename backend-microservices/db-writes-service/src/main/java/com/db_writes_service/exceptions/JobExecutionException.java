package com.db_writes_service.exceptions;

public class JobExecutionException extends RuntimeException {
    public JobExecutionException(String message) {
        super(message);
    }

    public JobExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}

