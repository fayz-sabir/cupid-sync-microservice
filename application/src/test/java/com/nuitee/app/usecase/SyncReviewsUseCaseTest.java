package com.nuitee.app.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.nuitee.app.mapper.ExternalToDomainMapper;
import com.nuitee.domain.event.ReviewsSynced;
import com.nuitee.domain.external.ReviewExternal;
import com.nuitee.domain.i18n.Lang;
import com.nuitee.domain.model.Review;
import com.nuitee.domain.spi.CachePort;
import com.nuitee.domain.spi.CupidClientPort;
import com.nuitee.domain.spi.DomainEventPublisher;
import com.nuitee.domain.spi.HotelRepositoryPort;
import com.nuitee.domain.spi.ReviewRepositoryPort;
import com.nuitee.domain.view.ReviewView;

@ExtendWith(MockitoExtension.class)
class SyncReviewsUseCaseTest {

    @Mock
    private CupidClientPort cupidClient;
    @Mock
    private ReviewRepositoryPort reviewRepo;
    @Mock
    private HotelRepositoryPort hotelRepo;
    @Mock
    private ExternalToDomainMapper mapper;
    @Mock
    private CachePort cache;
    @Mock
    private DomainEventPublisher events;

    private SyncReviewsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SyncReviewsUseCase(cupidClient, reviewRepo, hotelRepo, mapper, cache, events);
    }

    @Test
    void orchestratesReviewSync() {
        long hotelId = 11L;
        int limit = 2;
        List<ReviewExternal> external = List.of(mock(ReviewExternal.class));
        long pkId = 100L;
        Review review = new Review(1L, pkId, hotelId, 9, "src", LocalDateTime.now(), "type", Lang.EN, "p", "c", "a", "co", "h");
        List<Review> mapped = List.of(review);
        when(cupidClient.getReviews(hotelId, limit)).thenReturn(external);
        when(hotelRepo.resolvePkByExternalId(hotelId)).thenReturn(pkId);
        when(mapper.mapReviews(external, pkId, hotelId)).thenReturn(mapped);

        useCase.execute(hotelId, limit);

        verify(cupidClient).getReviews(hotelId, limit);
        verify(hotelRepo).resolvePkByExternalId(hotelId);
        verify(mapper).mapReviews(external, pkId, hotelId);
        verify(reviewRepo).upsertAll(mapped);
        ArgumentCaptor<List<ReviewView>> reviewViewsCaptor = ArgumentCaptor.forClass(List.class);
        verify(cache).putReviews(eq(hotelId), reviewViewsCaptor.capture(), eq(limit));
        assertEquals(1, reviewViewsCaptor.getValue().size());
        ArgumentCaptor<ReviewsSynced> eventCaptor = ArgumentCaptor.forClass(ReviewsSynced.class);
        verify(events).publish(eventCaptor.capture());
        assertEquals(hotelId, eventCaptor.getValue().hotelId());
        assertEquals(1, eventCaptor.getValue().fetchedCount());
        assertEquals(limit, eventCaptor.getValue().appliedLimit());
    }
}
