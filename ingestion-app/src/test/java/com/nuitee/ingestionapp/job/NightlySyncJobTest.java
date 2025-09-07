package com.nuitee.ingestionapp.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.nuitee.app.support.IngestionSyncProperties;
import com.nuitee.app.usecase.FullSyncUseCase;

@SpringJUnitConfig(NightlySyncJobTest.Config.class)
class NightlySyncJobTest {

    @Autowired
    FullSyncUseCase fullSyncUseCase;

    @Autowired
    ScheduledAnnotationBeanPostProcessor postProcessor;

    @Test
    void nightlyJobExecutesFullSyncAndHasCorrectSchedule() {
        Set<ScheduledTask> tasks = postProcessor.getScheduledTasks();
        assertThat(tasks).hasSize(1);

        ScheduledTask task = tasks.iterator().next();
        CronTask cronTask = (CronTask) task.getTask();
        assertThat(cronTask.getExpression()).isEqualTo("00 00 00 * * *");

        cronTask.getRunnable().run();
        verify(fullSyncUseCase, times(1)).execute();
    }

    @Configuration
    @EnableScheduling
    static class Config {
        @Bean
        FullSyncUseCase fullSyncUseCase() {
            return mock(FullSyncUseCase.class);
        }

        @Bean
        IngestionSyncProperties ingestionSyncProperties() {
            IngestionSyncProperties props = new IngestionSyncProperties();
            props.setHotelIds(List.of(1L, 2L));
            return props;
        }

        @Bean
        NightlySyncJob nightlySyncJob(FullSyncUseCase fullSyncUseCase, IngestionSyncProperties props) {
            return new NightlySyncJob(fullSyncUseCase, props);
        }
    }
}
