package com.nuitee.domain.event;

import java.time.Instant;
import java.util.UUID;

public record HotelViewCached(
    UUID eventId,
    Instant occurredAt,
    EventMetadata metadata,
    long hotelId,
    String lang
) implements DomainEvent {
    public static HotelViewCached of(long hotelId, String lang, EventMetadata md) {
        return new HotelViewCached(UUID.randomUUID(), Instant.now(), md, hotelId, lang);
    }
}