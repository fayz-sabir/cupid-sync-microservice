package com.nuitee.app.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.nuitee.app.service.ReviewService;
import com.nuitee.domain.view.ReviewView;

@ExtendWith(MockitoExtension.class)
class GetReviewsUseCaseTest {

    @Mock
    private ReviewService reviewService;

    private GetReviewsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetReviewsUseCase(reviewService);
    }

    @Test
    void delegatesToService() {
        long hotelId = 7L;
        int limit = 5;
        List<ReviewView> reviews = List.of();
        when(reviewService.getReviews(hotelId, limit)).thenReturn(reviews);

        List<ReviewView> result = useCase.execute(hotelId, limit);

        assertEquals(reviews, result);
        verify(reviewService).getReviews(hotelId, limit);
    }
}
