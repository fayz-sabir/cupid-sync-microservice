package com.nuitee.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nuitee.app.mapper.DomainToViewMapper;
import com.nuitee.domain.exception.CupidNotFoundException;
import com.nuitee.domain.policy.I18nFallbackPolicy;
import com.nuitee.domain.spi.HotelRepositoryPort;
import com.nuitee.domain.spi.HotelI18nRepositoryPort;
import com.nuitee.domain.view.HotelView;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class HotelQueryService {

    private final HotelRepositoryPort hotelRepo;

    private final HotelI18nRepositoryPort hotelI18nRepo;
    private final DomainToViewMapper viewMapper;
    private final I18nFallbackPolicy fallbackPolicy;
    private final ReviewService reviewService;
    private final Counter hotelViewCounter;
    private static final Logger LOGGER = LoggerFactory.getLogger(HotelQueryService.class);

    public HotelQueryService(HotelRepositoryPort hotelRepo,
                             HotelI18nRepositoryPort hotelI18nRepo,
                             DomainToViewMapper viewMapper,
                             I18nFallbackPolicy fallbackPolicy,
                             ReviewService reviewService,
                             MeterRegistry registry) {
        this.hotelRepo = hotelRepo;

        this.hotelI18nRepo = hotelI18nRepo;
        this.viewMapper = viewMapper;
        this.fallbackPolicy = fallbackPolicy;
        this.reviewService = reviewService;
        this.hotelViewCounter = registry.counter("hotel.view.build");
    }

    @Transactional(readOnly = true)
    public HotelView buildHotelView(long hotelId, String lang) {
        LOGGER.info("Building hotel view for id={} lang={}", hotelId, lang);
        hotelViewCounter.increment();

        long hotelPkId = hotelRepo.resolvePkByExternalId(hotelId);
        var hotelI18n = hotelI18nRepo.findHotelI18n(hotelPkId, lang, fallbackPolicy)
            .orElseThrow(() -> new CupidNotFoundException(
                "No i18n found for hotel " + hotelId + " with lang '" + lang + "' (policy=" + fallbackPolicy + ")"
            ));

        return viewMapper.toHotelView(hotelI18n);
    }

    @Transactional(readOnly = true)
    public HotelView withReviews(HotelView base, long hotelId, int limit) {
        LOGGER.info("Adding reviews to hotel view id={} limit={}", hotelId, limit);
        var reviews = reviewService.getReviews(hotelId, limit);
        return viewMapper.withReviews(base, reviews);
    }
}
