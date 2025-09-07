package com.nuitee.domain.spi;

import java.util.List;

import com.nuitee.domain.model.Review;

public interface ReviewRepositoryPort {
    List<Review> findByHotelId(long hotelId, int min);

    void upsertAll(List<Review> mapped);
}
