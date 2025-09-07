package com.nuitee.domain.event;

import java.util.Optional;

public record EventMetadata(
    String correlationId,
    String causationId,
    String producer
) {
    public Optional<String> correlationIdOpt() {
        return Optional.ofNullable(correlationId);
    }

    public Optional<String> causationIdOpt() {
        return Optional.ofNullable(causationId);
    }

    public Optional<String> producerOpt() {
        return Optional.ofNullable(producer);
    }
}
