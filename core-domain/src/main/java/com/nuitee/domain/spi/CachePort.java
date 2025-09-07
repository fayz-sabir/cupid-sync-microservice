package com.nuitee.domain.spi;

import java.util.List;
import java.util.Optional;

import com.nuitee.domain.model.Review;
import com.nuitee.domain.view.HotelView;
import com.nuitee.domain.view.ReviewView;

public interface CachePort {
    Optional<HotelView> getHotelView(long hotelId, String lang);

    void putHotelView(HotelView view, String lang);

    void evictHotelViews(long hotelId);

    Optional<List<ReviewView>> getReviews(long hotelId, int limit);

    void putReviews(long hotelId, List<ReviewView> reviews, int limit);
}
