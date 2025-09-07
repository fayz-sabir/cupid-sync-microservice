package com.nuitee.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nuitee.domain.policy.I18nFallbackPolicy;
import com.nuitee.domain.policy.ReviewPolicy;

@Configuration
public class ApplicationConfig {
    @Bean
    public I18nFallbackPolicy fallbackPolicy() {
        return new I18nFallbackPolicy();
    }

    @Bean
    public ReviewPolicy reviewPolicy() {
        return new ReviewPolicy();
    }
}
