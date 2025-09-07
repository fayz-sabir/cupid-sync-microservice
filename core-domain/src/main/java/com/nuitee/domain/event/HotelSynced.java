package com.nuitee.domain.event;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record HotelSynced(
    UUID eventId,
    Instant occurredAt,
    EventMetadata metadata,
    long hotelId,
    String sourceSystem,
    Set<String> langsUpdated
) implements DomainEvent {
    public static HotelSynced of(long hotelId, String sourceSystem, Set<String> langsUpdated, EventMetadata eventMetadata) {
        return new HotelSynced(UUID.randomUUID(), Instant.now(), eventMetadata, hotelId, sourceSystem, Set.copyOf(langsUpdated));
    }
}