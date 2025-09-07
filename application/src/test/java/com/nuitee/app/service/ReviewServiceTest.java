package com.nuitee.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.nuitee.domain.i18n.Lang;
import com.nuitee.domain.model.Review;
import com.nuitee.domain.policy.ReviewPolicy;
import com.nuitee.domain.spi.CachePort;
import com.nuitee.domain.spi.ReviewRepositoryPort;
import com.nuitee.domain.view.ReviewStatsView;
import com.nuitee.domain.view.ReviewView;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepositoryPort reviewRepo;
    @Mock
    private CachePort cache;
    @Mock
    private ReviewPolicy policy;

    private ReviewService service;
    private SimpleMeterRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        service = new ReviewService(reviewRepo, cache, policy, registry);
    }

    @Test
    void getReviewsReturnsCachedAndCountsHit() {
        long hotelId = 1L;
        int limit = 5;
        List<ReviewView> cached = List.of(new ReviewView(1, hotelId, hotelId, 10, "source", LocalDateTime.now(), "type", Lang.EN, "pros", "cons", "author", "country", "head"));
        when(cache.getReviews(hotelId, limit)).thenReturn(Optional.of(cached));
        when(policy.apply(cached, limit)).thenReturn(cached);

        List<ReviewView> result = service.getReviews(hotelId, limit);

        assertEquals(cached, result);
        verify(cache).getReviews(hotelId, limit);
        verify(policy).apply(cached, limit);
        verifyNoInteractions(reviewRepo);
        verify(cache, never()).putReviews(anyLong(), anyList(), anyInt());
        assertEquals(1.0, registry.get("reviews.cache.hit").counter().count());
        assertEquals(0.0, registry.get("reviews.cache.miss").counter().count());
        assertEquals(0.0, registry.get("reviews.repo.calls").counter().count());
    }

    @Test
    void getReviewsFetchesFromRepoWhenCacheMiss() {
        long hotelId = 2L;
        int limit = 3;
        when(cache.getReviews(hotelId, limit)).thenReturn(Optional.empty());
        when(policy.maxLimit()).thenReturn(10);
        Review review = new Review(1, hotelId, hotelId, 8, "src", LocalDateTime.now(), "type", Lang.EN, "p", "c", "a", "co", "h");
        when(reviewRepo.findByHotelId(hotelId, limit)).thenReturn(List.of(review));
        List<ReviewView> mapped = List.of(ReviewView.fromReview(review));
        when(policy.apply(mapped, limit)).thenReturn(mapped);

        List<ReviewView> result = service.getReviews(hotelId, limit);

        assertEquals(mapped, result);
        verify(cache).getReviews(hotelId, limit);
        verify(reviewRepo).findByHotelId(hotelId, limit);
        verify(cache).putReviews(hotelId, mapped, limit);
        verify(policy).apply(mapped, limit);
        assertEquals(0.0, registry.get("reviews.cache.hit").counter().count());
        assertEquals(1.0, registry.get("reviews.cache.miss").counter().count());
        assertEquals(1.0, registry.get("reviews.repo.calls").counter().count());
    }

    @Test
    void getReviewStatsDelegatesToGetReviewsAndCalculatesAverage() {
        long hotelId = 3L;
        int limit = 4;
        List<ReviewView> cached = List.of(
            new ReviewView(1, hotelId, hotelId, 8, "source", LocalDateTime.now(), "type", Lang.EN, "p", "c", "a", "co", "h"),
            new ReviewView(2, hotelId, hotelId, 6, "source", LocalDateTime.now(), "type", Lang.EN, "p", "c", "a", "co", "h")
        );
        when(cache.getReviews(hotelId, limit)).thenReturn(Optional.of(cached));
        when(policy.apply(cached, limit)).thenReturn(cached);

        ReviewStatsView stats = service.getReviewStats(hotelId, limit);

        assertEquals(7.0, stats.averageScore());
        assertEquals(2, stats.reviewCount());
        assertEquals(1.0, registry.get("reviews.cache.hit").counter().count());
    }
}
