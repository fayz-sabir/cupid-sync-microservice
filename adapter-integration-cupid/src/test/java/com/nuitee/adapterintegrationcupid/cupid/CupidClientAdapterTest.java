package com.nuitee.adapterintegrationcupid.cupid;

import com.nuitee.domain.exception.CupidNotFoundException;
import com.nuitee.domain.exception.CupidTransientException;
import com.nuitee.domain.external.HotelExternal;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = CupidClientAdapterTest.TestApplication.class)
class CupidClientAdapterTest {

    private static int port;
    private static String baseUrl;
    private static MockWebServer server;

    @BeforeAll
    void boot() throws IOException {
        server = new MockWebServer();
        server.start();
        port = server.getPort();
        baseUrl = "http://127.0.0.1:" + port + "/";
    }

    @AfterAll
    void shutdown() throws IOException {
        server.shutdown();
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("cupid.api.base-url", () -> baseUrl);
        r.add("spring.cloud.openfeign.client.config.cupid.url", () -> baseUrl);
    }

    @BeforeEach
    void clear(@Autowired CircuitBreakerRegistry cbr) throws IOException {
        server.shutdown();
        server = new MockWebServer();
        server.start(port);
        cbr.circuitBreaker("cupid").reset();
    }

    @Autowired
    CupidClientAdapter adapter;

    @Test
    void getHotel_success() {
        server.enqueue(new MockResponse()
            .setBody(HOTEL_JSON)
            .addHeader("Content-Type", "application/json"));

        HotelExternal hotel = adapter.getHotel(1L);
        assertEquals("Test Hotel", hotel.name());
    }

    @Test
    void getHotel_notFound() {
        server.enqueue(new MockResponse().setResponseCode(404));
        server.enqueue(new MockResponse().setResponseCode(404));

        assertThrows(CupidNotFoundException.class, () -> adapter.getHotel(1L));
    }

    @Test
    void circuitBreakerOpens_afterFailures() {
        server.enqueue(new MockResponse().setResponseCode(500));
        server.enqueue(new MockResponse().setResponseCode(500));
        assertThrows(CupidTransientException.class, () -> adapter.getHotel(1L));

        server.enqueue(new MockResponse().setResponseCode(500));
        server.enqueue(new MockResponse().setResponseCode(500));
        assertThrows(CupidTransientException.class, () -> adapter.getHotel(1L));

        assertThrows(CupidTransientException.class, () -> adapter.getHotel(1L));
    }

    private static final String HOTEL_JSON = """
            {
              "hotel_id": 1,
              "chain_id": 10,
              "main_image_th": "img.jpg",
              "stars": 5,
              "rating": 4.5,
              "latitude": 40.0,
              "longitude": -3.7,
              "phone": "123",
              "email": "test@example.com",
              "fax": "456",
              "hotel_name": "Test Hotel",
              "address": null,
              "checkin": null,
              "child_allowed": true,
              "pets_allowed": false,
              "review_count": 20
            }
        """;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableFeignClients(clients = com.nuitee.adapterintegrationcupid.client.CupidFeignClient.class)
    @ComponentScan(basePackageClasses = com.nuitee.adapterintegrationcupid.cupid.CupidClientAdapter.class)
    static class TestApplication {
        @Bean
        MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }
}
