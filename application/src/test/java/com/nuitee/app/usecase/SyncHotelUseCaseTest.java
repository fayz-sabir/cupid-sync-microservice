package com.nuitee.app.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.nuitee.app.mapper.ExternalToDomainMapper;
import com.nuitee.app.service.HotelQueryService;
import com.nuitee.domain.event.HotelSynced;
import com.nuitee.domain.model.Hotel;
import com.nuitee.domain.i18n.HotelI18n;
import com.nuitee.domain.spi.CachePort;
import com.nuitee.domain.spi.CupidClientPort;
import com.nuitee.domain.spi.DomainEventPublisher;
import com.nuitee.domain.spi.HotelI18nRepositoryPort;
import com.nuitee.domain.spi.HotelRepositoryPort;
import com.nuitee.domain.view.HotelView;
import com.nuitee.domain.external.HotelExternal;
import com.nuitee.domain.external.HotelLocalizedExternal;

@ExtendWith(MockitoExtension.class)
class SyncHotelUseCaseTest {

    @Mock
    private CupidClientPort cupidClient;
    @Mock
    private HotelRepositoryPort hotelRepo;
    @Mock
    private HotelI18nRepositoryPort hotelI18nRepo;
    @Mock
    private CachePort cache;
    @Mock
    private DomainEventPublisher events;
    @Mock
    private ExternalToDomainMapper mapper;
    @Mock
    private HotelQueryService hotelQueryService;

    private SyncHotelUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SyncHotelUseCase(cupidClient, hotelRepo, hotelI18nRepo, cache, events, mapper, hotelQueryService);
    }

    @Test
    void orchestratesSyncFlow() {
        long hotelId = 9L;
        String lang = "fr";
        HotelExternal extHotel = mock(HotelExternal.class);
        HotelLocalizedExternal extLoc = mock(HotelLocalizedExternal.class);
        Hotel domainHotel = mock(Hotel.class);
        HotelI18n i18n = mock(HotelI18n.class);
        HotelView view = mock(HotelView.class);

        when(cupidClient.getHotel(hotelId)).thenReturn(extHotel);
        when(cupidClient.getHotelLocalized(hotelId, lang)).thenReturn(extLoc);
        when(mapper.mapHotel(extHotel)).thenReturn(domainHotel);
        when(mapper.mapHotelLocalized(extLoc, domainHotel, lang)).thenReturn(i18n);
        when(hotelRepo.resolvePkByExternalId(hotelId)).thenReturn(55L);
        when(hotelQueryService.buildHotelView(hotelId, lang)).thenReturn(view);

        useCase.execute(hotelId, lang);

        verify(cupidClient).getHotel(hotelId);
        verify(cupidClient).getHotelLocalized(hotelId, lang);
        verify(mapper).mapHotel(extHotel);
        verify(mapper).mapHotelLocalized(extLoc, domainHotel, lang);
        verify(hotelRepo).upsert(domainHotel);
        verify(hotelRepo).resolvePkByExternalId(hotelId);
        verify(hotelI18nRepo).upsertHotelI18n(i18n, 55L);
        verify(cache).evictHotelViews(hotelId);
        verify(hotelQueryService).buildHotelView(hotelId, lang);
        verify(cache).putHotelView(view, lang);

        ArgumentCaptor<HotelSynced> captor = ArgumentCaptor.forClass(HotelSynced.class);
        verify(events).publish(captor.capture());
        assertEquals(hotelId, captor.getValue().hotelId());
        assertEquals(Set.of(lang), captor.getValue().langsUpdated());
    }
}
