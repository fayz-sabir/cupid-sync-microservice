package com.nuitee.adaptermessaging.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.nuitee.domain.event.DomainEvent;

@Component
class LoggingDomainEventListener {
    private static final Logger log = LoggerFactory.getLogger(LoggingDomainEventListener.class);

    @EventListener
    public void onDomainEvent(DomainEvent event) {
        log.info("DomainEvent received: {}", event);
    }
}