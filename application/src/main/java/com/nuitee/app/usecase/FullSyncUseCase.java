package com.nuitee.app.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nuitee.app.service.FullSyncService;

@Service
public class FullSyncUseCase {
    private final FullSyncService fullSyncService;

    public FullSyncUseCase(FullSyncService fullSyncService) {
        this.fullSyncService = fullSyncService;
    }

    @Transactional
    public void execute() {
        fullSyncService.syncAll();
    }
}
