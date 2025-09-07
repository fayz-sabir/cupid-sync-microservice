package com.nuitee.adaptermessaging.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.nuitee.domain.event.DomainEvent;
import com.nuitee.domain.event.EventMetadata;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@SpringJUnitConfig(SpringEventPublisherAdapterTest.Config.class)
class SpringEventPublisherAdapterTest {

    @Configuration
    static class Config {
        @Bean
        MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }

        @Bean
        SpringEventPublisherAdapter springEventPublisherAdapter(ApplicationEventPublisher publisher, MeterRegistry registry) {
            return new SpringEventPublisherAdapter(publisher, registry);
        }

        @Bean
        LoggingDomainEventListener loggingDomainEventListener() {
            return Mockito.spy(new LoggingDomainEventListener());
        }
    }

    @Autowired
    SpringEventPublisherAdapter adapter;

    @Autowired
    MeterRegistry meterRegistry;

    @Autowired
    LoggingDomainEventListener listener;

    @Test
    void publishesEventAndIncrementsCounter() {
        DomainEvent event = new TestDomainEvent();

        adapter.publish(event);

        Mockito.verify(listener).onDomainEvent(event);
        assertThat(meterRegistry.counter("domain.events.published").count()).isEqualTo(1.0);
    }

    static class TestDomainEvent implements DomainEvent {
        private final UUID id = UUID.randomUUID();
        private final Instant at = Instant.now();
        private final EventMetadata metadata = new EventMetadata("corr", "cause", "test");

        @Override
        public UUID eventId() {
            return id;
        }

        @Override
        public Instant occurredAt() {
            return at;
        }

        @Override
        public EventMetadata metadata() {
            return metadata;
        }
    }
}
