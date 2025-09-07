package com.nuitee.app.service;

import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.nuitee.app.support.IngestionSyncProperties;
import com.nuitee.app.usecase.SyncHotelUseCase;
import com.nuitee.app.usecase.SyncReviewsUseCase;

@ExtendWith(MockitoExtension.class)
class FullSyncServiceTest {

    @Mock
    private SyncHotelUseCase syncHotel;
    @Mock
    private SyncReviewsUseCase syncReviews;

    private FullSyncService service;

    @BeforeEach
    void setUp() {
        IngestionSyncProperties props = new IngestionSyncProperties();
        props.setHotelIds(List.of(1L, 2L));
        Executor executor = Runnable::run; // synchronous
        service = new FullSyncService(syncHotel, syncReviews, props, executor);
    }

    @Test
    void syncAllInvokesUseCasesForEachHotelAndLang() {
        service.syncAll();

        verify(syncHotel).execute(1L, "fr");
        verify(syncHotel).execute(1L, "es");
        verify(syncHotel).execute(2L, "fr");
        verify(syncHotel).execute(2L, "es");
        verify(syncReviews).execute(1L, 10);
        verify(syncReviews).execute(2L, 10);
    }
}
