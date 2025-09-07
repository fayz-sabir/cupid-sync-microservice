package com.nuitee.domain.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.nuitee.domain.i18n.Lang;
import com.nuitee.domain.view.ReviewView;

class ReviewPolicyTest {
    private final ReviewPolicy policy = new ReviewPolicy();

    private ReviewView review(long id, int score) {
        return new ReviewView(
            id,
            1L,
            1L,
            score,
            "src",
            LocalDateTime.now(),
            "type",
            Lang.EN,
            "pros",
            "cons",
            "author",
            "country",
            "headline"
        );
    }

    @Test
    void sortsByScoreAscendingAndAppliesLimit() {
        List<ReviewView> reviews = List.of(
            review(1L, 5),
            review(2L, 8),
            review(3L, 1)
        );
        List<ReviewView> result = policy.apply(reviews, 2);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).score());
        assertEquals(5, result.get(1).score());
    }

    @Test
    void maxLimitIs200() {
        assertEquals(200, policy.maxLimit());
    }
}
