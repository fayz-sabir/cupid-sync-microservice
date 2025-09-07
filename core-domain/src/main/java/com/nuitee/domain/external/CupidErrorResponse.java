package com.nuitee.domain.external;

public record CupidErrorResponse(
    int status,
    String message,
    String path
) {}
