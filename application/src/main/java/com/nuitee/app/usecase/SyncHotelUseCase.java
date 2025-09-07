package com.nuitee.app.usecase;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nuitee.app.mapper.ExternalToDomainMapper;
import com.nuitee.app.service.HotelQueryService;
import com.nuitee.domain.event.EventMetadata;
import com.nuitee.domain.event.HotelSynced;
import com.nuitee.domain.model.Hotel;
import com.nuitee.domain.spi.CachePort;
import com.nuitee.domain.spi.CupidClientPort;
import com.nuitee.domain.spi.DomainEventPublisher;
import com.nuitee.domain.spi.HotelI18nRepositoryPort;
import com.nuitee.domain.spi.HotelRepositoryPort;

import org.springframework.transaction.annotation.Transactional;

@Service
public class SyncHotelUseCase {

    private final CupidClientPort cupidClient;
    private final HotelRepositoryPort hotelRepo;
    private final HotelI18nRepositoryPort hotelI18nRepo;
    private final CachePort cache;
    private final DomainEventPublisher eventPublisher;
    private final ExternalToDomainMapper externalMapper;
    private final HotelQueryService hotelQueryService;

    public SyncHotelUseCase(CupidClientPort cupidClient,
                            HotelRepositoryPort hotelRepo,
                            HotelI18nRepositoryPort hotelI18nRepo,
                            CachePort cache,
                            DomainEventPublisher eventPublisher,
                            ExternalToDomainMapper externalMapper,
                            HotelQueryService hotelQueryService) {
        this.cupidClient = cupidClient;
        this.hotelRepo = hotelRepo;
        this.hotelI18nRepo = hotelI18nRepo;
        this.cache = cache;
        this.eventPublisher = eventPublisher;
        this.externalMapper = externalMapper;
        this.hotelQueryService = hotelQueryService;
    }

    @Transactional
    public void execute(long hotelId, String langForWarmup) {

        var ext = cupidClient.getHotel(hotelId);

        var extLoc = (langForWarmup != null && !langForWarmup.isBlank())
            ? cupidClient.getHotelLocalized(hotelId, langForWarmup)
            : null;

        Hotel hotel = externalMapper.mapHotel(ext);
        var hotelI18n = (extLoc != null) ? externalMapper.mapHotelLocalized(extLoc, hotel, langForWarmup) : null;

        hotelRepo.upsert(hotel);
        if (hotelI18n != null) {
            long hotelPkId = hotelRepo.resolvePkByExternalId(hotelId);
            hotelI18nRepo.upsertHotelI18n(hotelI18n, hotelPkId);
        }

        cache.evictHotelViews(hotelId);

        if (extLoc != null) {
            var view = hotelQueryService.buildHotelView(hotelId, langForWarmup);
            cache.putHotelView(view, langForWarmup);
        }

        String causationId = UUID.randomUUID().toString();

        EventMetadata metadata = new EventMetadata(UUID.randomUUID().toString(), causationId, "cupid-sync");

        eventPublisher.publish(new HotelSynced(UUID.randomUUID(), Instant.now(), metadata, hotelId, "cupid-hotel-synced", Set.of(langForWarmup)));
    }
}
