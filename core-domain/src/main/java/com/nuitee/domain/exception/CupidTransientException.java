package com.nuitee.domain.exception;

public class CupidTransientException extends RuntimeException {
    private final int status;
    private final Object details;

    public CupidTransientException(int status, Object details) {
        super("Cupid transient error " + status);
        this.status = status;
        this.details = details;
    }

    public int getStatus() {
        return status;
    }

    public Object getDetails() {
        return details;
    }
}