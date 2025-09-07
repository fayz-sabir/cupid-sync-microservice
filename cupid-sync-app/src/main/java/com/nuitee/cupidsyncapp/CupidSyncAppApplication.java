package com.nuitee.cupidsyncapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.nuitee")

public class CupidSyncAppApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(CupidSyncAppApplication.class);
    private static final Counter START_COUNTER = Metrics.counter("application.startups");

    public static void main(String[] args) {
        LOGGER.info("Starting Cupid Sync App");
        START_COUNTER.increment();
        SpringApplication.run(CupidSyncAppApplication.class, args);
    }
}
