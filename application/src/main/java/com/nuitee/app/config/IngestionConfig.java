package com.nuitee.app.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.nuitee.app.support.IngestionSyncProperties;

@Configuration
public class IngestionConfig {

    @Bean("props")
    public IngestionSyncProperties ingestionSyncProperties() throws Exception {
        ClassPathResource resource = new ClassPathResource("hotel-ids.txt");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            List<Long> ids = reader.lines()
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .map(Long::valueOf)
                    .distinct()
                    .toList();

            IngestionSyncProperties props = new IngestionSyncProperties();
            props.setHotelIds(ids);
            return props;
        }
    }
}
