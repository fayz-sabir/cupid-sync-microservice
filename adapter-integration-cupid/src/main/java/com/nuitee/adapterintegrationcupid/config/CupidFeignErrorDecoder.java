package com.nuitee.adapterintegrationcupid.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuitee.domain.exception.CupidClientException;
import com.nuitee.domain.exception.CupidNotFoundException;
import com.nuitee.domain.exception.CupidTransientException;

import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CupidFeignErrorDecoder implements ErrorDecoder {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String body = "";
        try {
            if (response.body() != null) {
                body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
        }

        Map<String, Object> details = null;
        try {
            details = mapper.readValue(body, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ignored) {
        }

        int status = response.status();

        if (status == 404) {
            return new CupidNotFoundException(details != null ? details : Map.of("raw", body));
        }

        if (status == 429 || status == 503) {
            return new CupidTransientException(status, details != null ? details : Map.of("raw", body));
        }

        if (status >= 400 && status < 500) {
            return new CupidClientException(status, details != null ? details : Map.of("raw", body));
        }

        return new CupidTransientException(status, details != null ? details : Map.of("raw", body));
    }
}