package com.nuitee.adapterintegrationcupid.config;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.RequestInterceptor;

@Configuration
public class CupidFeignConfig {

    @Value("${cupid.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor cupidHeaders(@Value("${cupid.api.key}") String apiKey) {
        return template -> {
            template.header("X-API-KEY", apiKey);

            String correlationId = MDC.get("correlationId");
            if (correlationId == null) {
                correlationId = UUID.randomUUID().toString();
                MDC.put("correlationId", correlationId);
            }
            template.header("X-Correlation-Id", correlationId);
        };
    }

    @Bean
    public Request.Options feignOptions(
        @Value("${cupid.feign.connect-timeout-ms:3000}") int connectTimeout,
        @Value("${cupid.feign.read-timeout-ms:5000}") int readTimeout) {
        return new Request.Options(connectTimeout, readTimeout);
    }

    @Bean
    public Retryer retryer(
        @Value("${cupid.feign.retry.period-ms:100}") long period,
        @Value("${cupid.feign.retry.max-period-ms:1000}") long maxPeriod,
        @Value("${cupid.feign.retry.max-attempts:3}") int maxAttempts) {
        return new Retryer.Default(period, maxPeriod, maxAttempts);
    }

    @Bean
    public Logger.Level feignLoggerLevel(
        @Value("${cupid.feign.logger-level:BASIC}") Logger.Level level) {
        return level;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CupidFeignErrorDecoder();
    }
}
