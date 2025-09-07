package com.nuitee.domain.policy;

import java.util.Comparator;
import java.util.List;

import com.nuitee.domain.view.ReviewView;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviewPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewPolicy.class);
    private static final Counter APPLY_COUNTER = Metrics.counter("review.policy.apply");

    public List<ReviewView> apply(List<ReviewView> reviews, int limit) {
        LOGGER.info("Applying review policy with limit {}", limit);
        APPLY_COUNTER.increment();
        return reviews.stream()
            .sorted(Comparator.comparing(ReviewView::score))
            .limit(limit)
            .toList();
    }

    public int maxLimit() {
        return 200;
    }
}
