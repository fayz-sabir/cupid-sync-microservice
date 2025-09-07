package com.nuitee.app.usecase;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.nuitee.app.service.FullSyncService;

@ExtendWith(MockitoExtension.class)
class FullSyncUseCaseTest {

    @Mock
    private FullSyncService service;

    private FullSyncUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FullSyncUseCase(service);
    }

    @Test
    void delegatesToService() {
        useCase.execute();
        verify(service).syncAll();
    }
}
