package com.nuitee.domain.view;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nuitee.domain.i18n.Lang;
import com.nuitee.domain.model.Review;

public record ReviewView(
    long reviewId,
    long hotelId,
    long externalHotelId,
    int score,
    String source,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime date,
    String type,
    Lang language,
    String pros,
    String cons,
    String authorName,
    String country,
    String headline
) {
    public static ReviewView fromReview(Review review) {
        return new ReviewView(review.id(),
            review.hotelId(),
            review.externalHotelId(),
            review.score(),
            review.source(),
            review.date(),
            review.type(),
            review.language(),
            review.pros(),
            review.cons(),
            review.authorName(),
            review.country(),
            review.headline());
    }
}
