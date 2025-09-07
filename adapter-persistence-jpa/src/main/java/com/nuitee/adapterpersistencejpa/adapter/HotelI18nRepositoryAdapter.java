package com.nuitee.adapterpersistencejpa.adapter;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nuitee.adapterpersistencejpa.mapper.DomainToJpaMapper;
import com.nuitee.adapterpersistencejpa.mapper.JpaToDomainMapper;
import com.nuitee.adapterpersistencejpa.repository.HotelI18nJpaRepository;
import com.nuitee.domain.i18n.HotelI18n;
import com.nuitee.domain.policy.I18nFallbackPolicy;
import com.nuitee.domain.spi.HotelI18nRepositoryPort;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class HotelI18nRepositoryAdapter implements HotelI18nRepositoryPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelI18nRepositoryAdapter.class);

    private final HotelI18nJpaRepository hotelRepo;
    private final JpaToDomainMapper toDomain;
    private final DomainToJpaMapper toJpa;
    private Counter upsertCounter;
    private Counter updateCounter;

    public HotelI18nRepositoryAdapter(HotelI18nJpaRepository hotelRepo,
                                      JpaToDomainMapper toDomain,
                                      DomainToJpaMapper toJpa,
                                      MeterRegistry meterRegistry) {
        this.hotelRepo = hotelRepo;
        this.toDomain = toDomain;
        this.toJpa = toJpa;
        this.upsertCounter = meterRegistry.counter("hotel.i18n.upsert");
        this.updateCounter = meterRegistry.counter("hotel.i18n.update");
    }

    @Override
    public Optional<HotelI18n> findHotelI18n(long hotelId, String lang, I18nFallbackPolicy policy) {
        return hotelRepo.findByHotelIdAndLang(hotelId, lang)
            .map(toDomain::hotelI18n);
    }

    @Override
    public void upsertHotelI18n(HotelI18n hotelI18n, long hotelPkId) {
        var optionalHotelEntity = hotelRepo.findByHotelIdAndLang(
            hotelPkId,
            hotelI18n.lang().code().toLowerCase()
        );
        if (optionalHotelEntity.isEmpty()) {
            LOGGER.info("Upserting hotelI18n {}", hotelI18n.hotelId());
            upsertCounter.increment();
            hotelRepo.save(toJpa.hotelI18n(hotelI18n, hotelPkId));
            LOGGER.info("Upserted hotelI18n {}", hotelI18n.hotelId());
            return;
        }

        LOGGER.info("Updating hotel {}", hotelI18n.hotelId());
        updateCounter.increment();
        var managed = optionalHotelEntity.get();
        hotelRepo.save(toJpa.updateHotelI18nEntity(managed, hotelI18n));
    }
}
