package com.nuitee.adapterintegrationcupid.cupid;

import com.nuitee.adapterintegrationcupid.client.CupidFeignClient;
import com.nuitee.domain.external.HotelExternal;
import com.nuitee.domain.external.HotelLocalizedExternal;
import com.nuitee.domain.external.ReviewExternal;
import com.nuitee.domain.spi.CupidClientPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CupidClientAdapter implements CupidClientPort {

    private final CupidFeignClient client;
    private final Counter getHotelCounter;
    private final Counter getHotelLocalizedCounter;
    private final Counter getReviewsCounter;
    private static final Logger LOGGER = LoggerFactory.getLogger(CupidClientAdapter.class);

    public CupidClientAdapter(CupidFeignClient client, MeterRegistry registry) {
        this.client = client;
        this.getHotelCounter = registry.counter("cupid.client.getHotel.calls");
        this.getHotelLocalizedCounter = registry.counter("cupid.client.getHotelLocalized.calls");
        this.getReviewsCounter = registry.counter("cupid.client.getReviews.calls");
    }

    @Timed(value = "cupid.client.getHotel")
    @CircuitBreaker(name = "cupid")
    @Retry(name = "cupid")
    @Override
    public HotelExternal getHotel(long hotelId) {
        LOGGER.info("Calling Cupid getHotel for hotel {}", hotelId);
        getHotelCounter.increment();
        return client.getHotel(hotelId);
    }

    @Timed(value = "cupid.client.getHotelLocalized")
    @CircuitBreaker(name = "cupid")
    @Retry(name = "cupid")
    @Override
    public HotelLocalizedExternal getHotelLocalized(long hotelId, String lang) {
        LOGGER.info("Calling Cupid getHotelLocalized for hotel {} lang {}", hotelId, lang);
        getHotelLocalizedCounter.increment();
        return client.getHotelLocalized(hotelId, lang);
    }

    @Timed(value = "cupid.client.getReviews")
    @CircuitBreaker(name = "cupid")
    @Retry(name = "cupid")
    @Override
    public List<ReviewExternal> getReviews(long hotelId, int limit) {
        LOGGER.info("Calling Cupid getReviews for hotel {} limit {}", hotelId, limit);
        getReviewsCounter.increment();
        return client.getReviews(hotelId, limit);
    }
}
