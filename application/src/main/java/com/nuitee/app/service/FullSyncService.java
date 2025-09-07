package com.nuitee.app.service;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nuitee.app.support.IngestionSyncProperties;
import com.nuitee.app.usecase.SyncHotelUseCase;
import com.nuitee.app.usecase.SyncReviewsUseCase;

@Service
public class FullSyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FullSyncService.class);
    private static final int DEFAULT_LIMIT = 10;
    private static final List<String> DEFAULT_LANGS = List.of("fr", "es");

    private final SyncHotelUseCase syncHotel;
    private final SyncReviewsUseCase syncReviews;
    private final IngestionSyncProperties props;
    private final Executor executor;

    public FullSyncService(SyncHotelUseCase syncHotel,
                           SyncReviewsUseCase syncReviews,
                           IngestionSyncProperties props,
                           Executor ingestionExecutor) {
        this.syncHotel = syncHotel;
        this.syncReviews = syncReviews;
        this.props = props;
        this.executor = ingestionExecutor;
    }

    public void syncAll() {
        List<Long> hotelIds = props.getHotelIds();
        LOGGER.info("Starting full sync with {} hotel IDs", hotelIds.size());

        List<CompletableFuture<Void>> allIdsFutures = new ArrayList<>(hotelIds.size());

        for (long hotelId : hotelIds) {
            CompletableFuture<?>[] langTasks = DEFAULT_LANGS.stream()
                .map(lang -> CompletableFuture.runAsync(() -> {
                    try {
                        LOGGER.info("Syncing hotel {} lang {}", hotelId, lang);
                        syncHotel.execute(hotelId, lang);
                        LOGGER.info("Synced hotel {} lang {}", hotelId, lang);
                    } catch (Exception ex) {
                        LOGGER.warn("Hotel {} lang {} failed: {}", hotelId, lang, ex.toString());
                    }
                }, executor))
                .toArray(CompletableFuture[]::new);

            CompletableFuture<Void> oneIdFlow = CompletableFuture
                .allOf(langTasks)
                .thenRunAsync(() -> {
                    try {
                        LOGGER.info("Syncing reviews for hotel {}", hotelId);
                        syncReviews.execute(hotelId, DEFAULT_LIMIT);
                        LOGGER.info("Synced reviews for hotel {}", hotelId);
                    } catch (Exception ex) {
                        LOGGER.warn("Reviews sync failed for hotel {}: {}", hotelId, ex.toString());
                    }
                }, executor)
                .exceptionally(ex -> {
                    LOGGER.warn("Flow failed for hotel {}: {}", hotelId, ex.toString());
                    return null;
                });

            allIdsFutures.add(oneIdFlow);
        }

        CompletableFuture.allOf(allIdsFutures.toArray(new CompletableFuture[0])).join();
        LOGGER.info("Full sync completed for {} hotel IDs", hotelIds.size());
    }
}
