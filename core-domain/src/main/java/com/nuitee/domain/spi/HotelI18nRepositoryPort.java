package com.nuitee.domain.spi;

import java.util.Optional;

import com.nuitee.domain.i18n.HotelI18n;
import com.nuitee.domain.policy.I18nFallbackPolicy;

public interface HotelI18nRepositoryPort {

    Optional<HotelI18n> findHotelI18n(long hotelId, String lang, I18nFallbackPolicy fallbackPolicy);

    void upsertHotelI18n(HotelI18n hotelI18n, long hotelPkId);
}
