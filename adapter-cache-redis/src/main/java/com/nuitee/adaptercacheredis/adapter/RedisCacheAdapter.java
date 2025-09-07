package com.nuitee.adaptercacheredis.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuitee.adaptercacheredis.config.CacheProperties;
import com.nuitee.domain.spi.CachePort;
import com.nuitee.domain.view.HotelView;
import com.nuitee.domain.view.ReviewView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCacheAdapter implements CachePort {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheAdapter.class);
    private static final String HOTEL_VIEW_KEY = "hotelView:%d:%s";
    private static final String REVIEWS_KEY = "reviews:%d:%d";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final CacheProperties props;
    private final Counter hotelViewHit;
    private final Counter hotelViewMiss;
    private final Counter reviewsHit;
    private final Counter reviewsMiss;

    public RedisCacheAdapter(StringRedisTemplate redis,
                             ObjectMapper cacheObjectMapper,
                             CacheProperties props,
                             MeterRegistry registry) {
        this.redis = redis;
        this.objectMapper = cacheObjectMapper;
        this.props = props;
        this.hotelViewHit = registry.counter("cache.hotelView.hit");
        this.hotelViewMiss = registry.counter("cache.hotelView.miss");
        this.reviewsHit = registry.counter("cache.reviews.hit");
        this.reviewsMiss = registry.counter("cache.reviews.miss");
    }


    @Override
    public Optional<HotelView> getHotelView(long hotelId, String lang) {
        LOGGER.info("Getting hotel view from cache: {}", hotelViewKey(hotelId, lang));
        String key = hotelViewKey(hotelId, lang);
        String json = redis.opsForValue().get(key);
        if (json == null) {
            hotelViewMiss.increment();
            LOGGER.info("Hotel view not found in cache: {}", key);
            return Optional.empty();
        }
        try {
            HotelView view = objectMapper.readValue(json, HotelView.class);
            LOGGER.info("Hotel view found in cache: {}", key);
            hotelViewHit.increment();
            return Optional.of(view);
        } catch (Exception e) {
            redis.delete(key);
            LOGGER.error("Failed to get hotel view from cache", e);
            return Optional.empty();
        }
    }

    @Override
    public void putHotelView(HotelView view, String lang) {
        LOGGER.info("Putting hotel view in cache: {}", hotelViewKey(view.hotelId(), lang));
        String key = hotelViewKey(view.hotelId(), lang);
        try {
            String json = objectMapper.writeValueAsString(view);
            redis.opsForValue().set(key, json, props.getHotelViewTtlSeconds(), TimeUnit.SECONDS);
            LOGGER.info("Hotel view put in cache: {}", key);
        } catch (Exception e) {
            LOGGER.error("Failed to put hotel view in cache", e);
        }
    }

    @Override
    public void evictHotelViews(long hotelId) {
        String pattern = "hotelView:" + hotelId + ":*";
        scanAndDelete(pattern);
    }


    @Override
    public Optional<List<ReviewView>> getReviews(long hotelId, int limit) {
        LOGGER.info("Getting reviews from cache: {}", reviewsKey(hotelId, limit));
        String key = reviewsKey(hotelId, limit);
        String json = redis.opsForValue().get(key);
        if (json == null) {
            reviewsMiss.increment();
            LOGGER.info("Reviews not found in cache: {}", key);
            return Optional.empty();
        }
        try {
            List<ReviewView> list = objectMapper.readValue(json, new TypeReference<List<ReviewView>>() {
            });
            LOGGER.info("Reviews found in cache: {}", key);
            reviewsHit.increment();
            return Optional.of(list);
        } catch (Exception e) {
            redis.delete(key);
            return Optional.empty();
        }
    }

    @Override
    public void putReviews(long hotelId, List<ReviewView> reviews, int limit) {
        String key = reviewsKey(hotelId, limit);
        try {
            String json = objectMapper.writeValueAsString(reviews);
            redis.opsForValue().set(key, json, props.getReviewsTtlSeconds(), TimeUnit.SECONDS);
            LOGGER.info("Reviews put in cache: {}", key);
        } catch (Exception e) {
            LOGGER.error("Failed to put reviews in cache", e);
        }
    }

    private static String hotelViewKey(long hotelId, String lang) {
        return String.format(Locale.ROOT, HOTEL_VIEW_KEY, hotelId, lang.toLowerCase(Locale.ROOT));
    }

    private static String reviewsKey(long hotelId, int limit) {
        return String.format(Locale.ROOT, REVIEWS_KEY, hotelId, limit);
    }

    private void scanAndDelete(String pattern) {
        redis.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(
                ScanOptions.scanOptions().match(pattern).count(1000).build())) {
                List<byte[]> toDel = new ArrayList<>();
                while (cursor.hasNext()) {
                    toDel.add(cursor.next());
                    if (toDel.size() >= 500) {
                        connection.keyCommands().del(toDel.toArray(new byte[0][]));
                        toDel.clear();
                    }
                }
                if (!toDel.isEmpty()) {
                    connection.keyCommands().del(toDel.toArray(new byte[0][]));
                }
            } catch (Exception exception) {
                LOGGER.error("Failed to scan and delete keys", exception);
            }
            return null;
        });
    }
}
