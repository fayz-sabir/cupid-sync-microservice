package com.nuitee.domain.event;

import java.time.Instant;
import java.util.UUID;

public record ReviewsSynced(
    UUID eventId,
    Instant occurredAt,
    EventMetadata metadata,
    long hotelId,
    int fetchedCount,
    int appliedLimit
) implements DomainEvent {
    public static ReviewsSynced of(long hotelId, int fetchedCount, int appliedLimit, EventMetadata eventMetadata) {
        return new ReviewsSynced(UUID.randomUUID(), Instant.now(), eventMetadata, hotelId, fetchedCount, appliedLimit);
    }
}
