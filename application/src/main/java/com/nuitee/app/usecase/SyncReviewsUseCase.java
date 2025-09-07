package com.nuitee.app.usecase;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nuitee.app.mapper.ExternalToDomainMapper;
import com.nuitee.domain.event.EventMetadata;
import com.nuitee.domain.event.ReviewsSynced;
import com.nuitee.domain.spi.CachePort;
import com.nuitee.domain.spi.CupidClientPort;
import com.nuitee.domain.spi.DomainEventPublisher;
import com.nuitee.domain.spi.HotelRepositoryPort;
import com.nuitee.domain.spi.ReviewRepositoryPort;
import com.nuitee.domain.view.ReviewView;

@Service
public class SyncReviewsUseCase {
    private final CupidClientPort cupidClient;
    private final ReviewRepositoryPort reviewRepo;
    private final HotelRepositoryPort hotelRepo;
    private final ExternalToDomainMapper mapper;
    private final CachePort cache;
    private final DomainEventPublisher events;

    public SyncReviewsUseCase(CupidClientPort cupidClient,
                              ReviewRepositoryPort reviewRepo,
                              HotelRepositoryPort hotelRepo,
                              ExternalToDomainMapper mapper,
                              CachePort cache,
                              DomainEventPublisher events) {
        this.cupidClient = cupidClient;
        this.reviewRepo = reviewRepo;
        this.hotelRepo = hotelRepo;
        this.mapper = mapper;
        this.cache = cache;
        this.events = events;
    }

    @Transactional
    public void execute(long hotelId, int limit) {
        var external = cupidClient.getReviews(hotelId, limit);
        long hotelPkId = hotelRepo.resolvePkByExternalId(hotelId);
        var mapped = mapper.mapReviews(external, hotelPkId, hotelId);

        reviewRepo.upsertAll(mapped);
        List<ReviewView> reviewViews = mapped.stream().map(ReviewView::fromReview).toList();
        cache.putReviews(hotelId, reviewViews, limit);

        EventMetadata metadata = new EventMetadata(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            "cupid-sync-reviews"
        );

        events.publish(ReviewsSynced.of(
            hotelId,
            mapped.size(),
            limit,
            metadata
        ));
    }

    public static ReviewsSynced create(long hotelId, int fetchedCount, int appliedLimit, EventMetadata metadata) {
        return new ReviewsSynced(UUID.randomUUID(), Instant.now(), metadata, hotelId, fetchedCount, appliedLimit);
    }
}
