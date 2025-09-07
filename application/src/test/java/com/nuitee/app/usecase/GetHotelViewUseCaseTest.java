package com.nuitee.app.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.nuitee.app.service.HotelQueryService;
import com.nuitee.domain.spi.CachePort;
import com.nuitee.domain.view.HotelView;

@ExtendWith(MockitoExtension.class)
class GetHotelViewUseCaseTest {

    @Mock
    private CachePort cache;
    @Mock
    private HotelQueryService hotelQueryService;

    private GetHotelViewUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetHotelViewUseCase(cache, hotelQueryService);
    }

    @Test
    void returnsCachedViewWithoutFetching() {
        long hotelId = 5L;
        String lang = "en";
        HotelView view = mock(HotelView.class);
        when(cache.getHotelView(hotelId, lang)).thenReturn(Optional.of(view));

        HotelView result = useCase.execute(hotelId, lang, false, 0);

        assertEquals(view, result);
        verify(cache).getHotelView(hotelId, lang);
        verifyNoInteractions(hotelQueryService);
        verify(cache, never()).putHotelView(any(), anyString());
    }

    @Test
    void buildsViewAndCachesWhenMissing() {
        long hotelId = 6L;
        String lang = "fr";
        int limit = 3;
        HotelView base = mock(HotelView.class);
        HotelView withReviews = mock(HotelView.class);

        when(cache.getHotelView(hotelId, lang)).thenReturn(Optional.empty());
        when(hotelQueryService.buildHotelView(hotelId, lang)).thenReturn(base);
        when(hotelQueryService.withReviews(base, hotelId, limit)).thenReturn(withReviews);

        HotelView result = useCase.execute(hotelId, lang, true, limit);

        assertEquals(withReviews, result);
        verify(cache).getHotelView(hotelId, lang);
        verify(hotelQueryService).buildHotelView(hotelId, lang);
        verify(cache).putHotelView(base, lang);
        verify(hotelQueryService).withReviews(base, hotelId, limit);
    }
}
