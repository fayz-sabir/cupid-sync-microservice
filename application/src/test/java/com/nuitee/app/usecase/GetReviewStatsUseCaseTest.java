package com.nuitee.app.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.nuitee.app.service.ReviewService;
import com.nuitee.domain.view.ReviewStatsView;

@ExtendWith(MockitoExtension.class)
class GetReviewStatsUseCaseTest {

    @Mock
    private ReviewService reviewService;

    private GetReviewStatsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetReviewStatsUseCase(reviewService);
    }

    @Test
    void delegatesToService() {
        long hotelId = 8L;
        int limit = 4;
        ReviewStatsView stats = new ReviewStatsView(4.5, 2);
        when(reviewService.getReviewStats(hotelId, limit)).thenReturn(stats);

        ReviewStatsView result = useCase.execute(hotelId, limit);

        assertEquals(stats, result);
        verify(reviewService).getReviewStats(hotelId, limit);
    }
}
