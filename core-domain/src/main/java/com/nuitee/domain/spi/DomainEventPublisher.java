package com.nuitee.domain.spi;

import com.nuitee.domain.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
