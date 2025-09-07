package com.nuitee.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.nuitee.app.mapper.DomainToViewMapper;
import com.nuitee.domain.exception.CupidNotFoundException;
import com.nuitee.domain.i18n.HotelI18n;
import com.nuitee.domain.i18n.Lang;
import com.nuitee.domain.model.Address;
import com.nuitee.domain.model.CheckinPolicy;
import com.nuitee.domain.model.Contact;
import com.nuitee.domain.model.Coordinates;
import com.nuitee.domain.model.Hotel;
import com.nuitee.domain.policy.I18nFallbackPolicy;
import com.nuitee.domain.spi.HotelI18nRepositoryPort;
import com.nuitee.domain.spi.HotelRepositoryPort;
import com.nuitee.domain.view.HotelView;
import com.nuitee.domain.view.ReviewView;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class HotelQueryServiceTest {

    @Mock
    private HotelRepositoryPort hotelRepo;
    @Mock
    private HotelI18nRepositoryPort hotelI18nRepo;
    @Mock
    private DomainToViewMapper viewMapper;
    @Mock
    private ReviewService reviewService;

    private HotelQueryService service;
    private SimpleMeterRegistry registry;
    private final I18nFallbackPolicy policy = new I18nFallbackPolicy();

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        service = new HotelQueryService(hotelRepo, hotelI18nRepo, viewMapper, policy, reviewService, registry);
    }

    private HotelI18n sampleHotelI18n() {
        Hotel hotel = new Hotel(1L, 1L, "H", 4.0, 5,
            new Coordinates(0, 0), new Contact("", "", ""), new Address("", "", "", "", ""),
            new CheckinPolicy("", "", "", List.of(), ""), "", true, false);
        return new HotelI18n(1L, hotel, Lang.EN, "", "", "", "", "", List.of(), List.of(), List.of(), List.of());
    }

    @Test
    void buildHotelViewReturnsView() {
        long hotelId = 10L;
        long pkId = 5L;
        String lang = "en";
        HotelI18n i18n = sampleHotelI18n();
        HotelView view = new HotelView(1L, "H", 4.0, i18n.hotel().address(), i18n.hotel().contact(), i18n.hotel().checkinPolicy(), "", true, false, List.of(), List.of(), List.of(), List.of(), List.of());

        when(hotelRepo.resolvePkByExternalId(hotelId)).thenReturn(pkId);
        when(hotelI18nRepo.findHotelI18n(pkId, lang, policy)).thenReturn(Optional.of(i18n));
        when(viewMapper.toHotelView(i18n)).thenReturn(view);

        HotelView result = service.buildHotelView(hotelId, lang);

        assertEquals(view, result);
        verify(hotelRepo).resolvePkByExternalId(hotelId);
        verify(hotelI18nRepo).findHotelI18n(pkId, lang, policy);
        verify(viewMapper).toHotelView(i18n);
        assertEquals(1.0, registry.get("hotel.view.build").counter().count());
    }

    @Test
    void buildHotelViewThrowsWhenNotFound() {
        long hotelId = 10L;
        when(hotelRepo.resolvePkByExternalId(hotelId)).thenReturn(5L);
        when(hotelI18nRepo.findHotelI18n(anyLong(), anyString(), any())).thenReturn(Optional.empty());

        assertThrows(CupidNotFoundException.class, () -> service.buildHotelView(hotelId, "en"));
    }

    @Test
    void withReviewsAddsReviewsViaMapper() {
        long hotelId = 1L;
        int limit = 2;
        HotelView base = new HotelView(hotelId, "H", 4.0, null, null, null, "", true, false, List.of(), List.of(), List.of(), List.of(), List.of());
        List<ReviewView> reviews = List.of();
        HotelView updated = base.withReviews(reviews);

        when(reviewService.getReviews(hotelId, limit)).thenReturn(reviews);
        when(viewMapper.withReviews(base, reviews)).thenReturn(updated);

        HotelView result = service.withReviews(base, hotelId, limit);

        assertEquals(updated, result);
        verify(reviewService).getReviews(hotelId, limit);
        verify(viewMapper).withReviews(base, reviews);
    }
}
