package com.nuitee.app.usecase;

import com.nuitee.app.service.ReviewService;
import com.nuitee.domain.view.ReviewView;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetReviewsUseCase {

    private final ReviewService reviewService;

    public GetReviewsUseCase(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public List<ReviewView> execute(@Positive long hotelId,
                                    @Min(1) @Max(200) int limit) {
        return reviewService.getReviews(hotelId, limit);
    }
}
