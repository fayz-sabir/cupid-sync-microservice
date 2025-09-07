package com.nuitee.app.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.nuitee.domain.model.Review;
import com.nuitee.domain.policy.ReviewPolicy;
import com.nuitee.domain.spi.CachePort;
import com.nuitee.domain.spi.ReviewRepositoryPort;
import com.nuitee.domain.view.ReviewView;
import com.nuitee.domain.view.ReviewStatsView;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepositoryPort reviewRepo;
    private final CachePort cache;
    private final ReviewPolicy policy;
    private final Counter cacheHit;
    private final Counter cacheMiss;
    private final Counter repoCalls;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewService.class);

    public ReviewService(ReviewRepositoryPort reviewRepo, CachePort cache, ReviewPolicy policy, MeterRegistry registry) {
        this.reviewRepo = reviewRepo;
        this.cache = cache;
        this.policy = policy;
        this.cacheHit = registry.counter("reviews.cache.hit");
        this.cacheMiss = registry.counter("reviews.cache.miss");
        this.repoCalls = registry.counter("reviews.repo.calls");
    }

    @Transactional(readOnly = true)
    public List<ReviewView> getReviews(long hotelId, int limit) {
        LOGGER.info("Fetching reviews for hotel {} with limit {}", hotelId, limit);
        Optional<List<ReviewView>> cached = cache.getReviews(hotelId, limit);
        if (cached.isPresent()) {
            cacheHit.increment();
            return applyPolicy(cached.get(), limit);
        }

        cacheMiss.increment();
        repoCalls.increment();
        List<Review> fromRepo = reviewRepo.findByHotelId(hotelId, Math.min(limit, policy.maxLimit()));
        List<ReviewView> reviewViews = fromRepo.stream().map(ReviewView::fromReview).toList();
        List<ReviewView> filteredReviewViews = applyPolicy(reviewViews, limit);

        cache.putReviews(hotelId, filteredReviewViews, limit);
        return filteredReviewViews;
    }

    @Transactional(readOnly = true)
    public ReviewStatsView getReviewStats(long hotelId, int limit) {
        List<ReviewView> reviews = getReviews(hotelId, limit);
        double average = reviews.stream().mapToInt(ReviewView::score).average().orElse(0.0);
        return new ReviewStatsView(average, reviews.size());
    }

    private List<ReviewView> applyPolicy(List<ReviewView> reviews, int limit) {
        return policy.apply(reviews, limit);
    }
}
