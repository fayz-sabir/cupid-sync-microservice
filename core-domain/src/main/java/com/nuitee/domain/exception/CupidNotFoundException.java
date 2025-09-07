package com.nuitee.domain.exception;

public class CupidNotFoundException extends CupidClientException {
    public CupidNotFoundException(Object details) {
        super(404, details);
    }
}