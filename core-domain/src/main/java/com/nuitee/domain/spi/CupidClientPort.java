package com.nuitee.domain.spi;

import java.util.List;

import com.nuitee.domain.external.HotelExternal;
import com.nuitee.domain.external.HotelLocalizedExternal;
import com.nuitee.domain.external.ReviewExternal;

public interface CupidClientPort {
    HotelExternal getHotel(long hotelId);

    HotelLocalizedExternal getHotelLocalized(long hotelId, String lang);

    List<ReviewExternal> getReviews(long hotelId, int limit);
}
