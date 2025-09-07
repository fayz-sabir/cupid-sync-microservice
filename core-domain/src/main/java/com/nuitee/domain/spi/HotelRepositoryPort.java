package com.nuitee.domain.spi;

import java.util.Optional;

import com.nuitee.domain.model.Hotel;

public interface HotelRepositoryPort {
    Optional<Hotel> findById(long id);

    void upsert(Hotel hotel);

    long resolvePkByExternalId(long externalHotelId);
}