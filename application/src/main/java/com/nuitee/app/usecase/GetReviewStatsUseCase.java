package com.nuitee.app.usecase;

import com.nuitee.app.service.ReviewService;
import com.nuitee.domain.view.ReviewStatsView;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import org.springframework.stereotype.Service;

@Service
public class GetReviewStatsUseCase {

    private final ReviewService reviewService;

    public GetReviewStatsUseCase(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public ReviewStatsView execute(@Positive long hotelId,
                                   @Min(1) @Max(200) int limit) {
        return reviewService.getReviewStats(hotelId, limit);
    }
}

