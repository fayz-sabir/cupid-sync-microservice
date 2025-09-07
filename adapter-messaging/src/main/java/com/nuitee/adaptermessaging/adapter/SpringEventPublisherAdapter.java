package com.nuitee.adaptermessaging.adapter;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.nuitee.domain.event.DomainEvent;
import com.nuitee.domain.spi.DomainEventPublisher;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SpringEventPublisherAdapter implements DomainEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final Counter publishedCounter;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringEventPublisherAdapter.class);

    public SpringEventPublisherAdapter(ApplicationEventPublisher publisher, MeterRegistry registry) {
        this.publisher = publisher;
        this.publishedCounter = registry.counter("domain.events.published");
    }

    @Override
    public void publish(DomainEvent event) {
        LOGGER.info("Publishing domain event {}", event);
        publisher.publishEvent(event);
        publishedCounter.increment();
    }
}