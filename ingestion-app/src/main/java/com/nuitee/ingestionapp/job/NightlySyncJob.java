package com.nuitee.ingestionapp.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nuitee.app.support.IngestionSyncProperties;
import com.nuitee.app.usecase.FullSyncUseCase;

@Component
class NightlySyncJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(NightlySyncJob.class);

    private final IngestionSyncProperties props;
    private final FullSyncUseCase fullSyncUseCase;

    NightlySyncJob(FullSyncUseCase fullSyncUseCase, IngestionSyncProperties props) {
        this.fullSyncUseCase = fullSyncUseCase;

        this.props = props;
    }

    @Scheduled(cron = "00 00 00 * * *", zone = "Africa/Casablanca")
    public void run() {
        LOGGER.info("Starting nightly sync job with {} hotel IDs", props.getHotelIds().size());
        fullSyncUseCase.execute();
    }
}
