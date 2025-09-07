package com.nuitee.app.usecase;

import jakarta.validation.constraints.Positive;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import com.nuitee.app.service.HotelQueryService;
import com.nuitee.app.validation.ValidLang;
import com.nuitee.domain.spi.CachePort;
import com.nuitee.domain.view.HotelView;

@Service
public class GetHotelViewUseCase {

    private final CachePort cache;
    private final HotelQueryService hotelQueryService;

    public GetHotelViewUseCase(CachePort cache, HotelQueryService hotelQueryService) {
        this.cache = cache;
        this.hotelQueryService = hotelQueryService;
    }

    @Transactional(readOnly = true)
    public HotelView execute(@Positive long hotelId,
                             @ValidLang String lang,
                             boolean includeReviews,
                             int reviewLimit) {

        Optional<HotelView> cached = cache.getHotelView(hotelId, lang);
        if (cached.isPresent()) {
            return includeReviews
                ? hotelQueryService.withReviews(cached.get(), hotelId, reviewLimit)
                : cached.get();
        }

        HotelView view = hotelQueryService.buildHotelView(hotelId, lang);
        cache.putHotelView(view, lang);

        return includeReviews
            ? hotelQueryService.withReviews(view, hotelId, reviewLimit)
            : view;
    }
}
