package com.nuitee.adaptercacheredis.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuitee.adaptercacheredis.config.CacheProperties;
import com.nuitee.domain.view.HotelView;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class RedisCacheAdapterTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:8.2.1"))
        .withExposedPorts(6379);

    private static StringRedisTemplate template;
    private static ObjectMapper mapper;
    private static MeterRegistry registry;

    @BeforeAll
    static void init() {
        redis.start();
        var config = new RedisStandaloneConfiguration(redis.getHost(), redis.getMappedPort(6379));
        var factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        template = new StringRedisTemplate(factory);
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        registry = new SimpleMeterRegistry();
    }

    @BeforeEach
    void clear() {
        template.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private RedisCacheAdapter adapter(long hotelTtl, long reviewsTtl) {
        CacheProperties props = new CacheProperties();
        props.setHotelViewTtlSeconds(hotelTtl);
        props.setReviewsTtlSeconds(reviewsTtl);
        return new RedisCacheAdapter(template, mapper, props, registry);
    }

    @Test
    void shouldCacheAndRetrieveHotelView() {
        RedisCacheAdapter adapter = adapter(60, 60);
        HotelView view = new HotelView(1L, "hotel", 0.0, null, null, null, null, null, null,
            List.of(), List.of(), List.of(), List.of(), List.of());
        adapter.putHotelView(view, "en");
        Optional<HotelView> cached = adapter.getHotelView(1L, "en");
        assertTrue(cached.isPresent());
        assertEquals(view, cached.get());
    }

    @Test
    void shouldExpireHotelViewAfterTtl() throws Exception {
        RedisCacheAdapter adapter = adapter(1, 60);
        HotelView view = new HotelView(2L, "hotel", 0.0, null, null, null, null, null, null,
            List.of(), List.of(), List.of(), List.of(), List.of());
        adapter.putHotelView(view, "en");
        Thread.sleep(1500);
        assertTrue(adapter.getHotelView(2L, "en").isEmpty());
    }

    @Test
    void shouldEvictHotelViewsByPattern() {
        RedisCacheAdapter adapter = adapter(60, 60);
        HotelView en = new HotelView(3L, "hotel", 0.0, null, null, null, null, null, null,
            List.of(), List.of(), List.of(), List.of(), List.of());
        HotelView fr = new HotelView(3L, "hotel", 0.0, null, null, null, null, null, null,
            List.of(), List.of(), List.of(), List.of(), List.of());
        adapter.putHotelView(en, "en");
        adapter.putHotelView(fr, "fr");
        assertTrue(adapter.getHotelView(3L, "en").isPresent());
        assertTrue(adapter.getHotelView(3L, "fr").isPresent());
        adapter.evictHotelViews(3L);
        assertTrue(adapter.getHotelView(3L, "en").isEmpty());
        assertTrue(adapter.getHotelView(3L, "fr").isEmpty());
    }
}

