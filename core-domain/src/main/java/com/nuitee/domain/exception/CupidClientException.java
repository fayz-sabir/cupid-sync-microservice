package com.nuitee.domain.exception;

public class CupidClientException extends RuntimeException {
    private final int status;
    private final Object details;

    public CupidClientException(int status, Object details) {
        super("Cupid API error " + status);
        this.status = status;
        this.details = details;
    }

    public int getStatus() { return status; }
    public Object getDetails() { return details; }
}