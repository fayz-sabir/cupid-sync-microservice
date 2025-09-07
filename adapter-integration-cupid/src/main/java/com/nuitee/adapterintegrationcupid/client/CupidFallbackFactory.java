package com.nuitee.adapterintegrationcupid.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import com.nuitee.domain.exception.CupidClientException;
import com.nuitee.domain.external.HotelExternal;
import com.nuitee.domain.external.HotelLocalizedExternal;
import com.nuitee.domain.external.ReviewExternal;

@Component
public class CupidFallbackFactory implements FallbackFactory<CupidFeignClient> {
    private static final Logger log = LoggerFactory.getLogger(CupidFallbackFactory.class);

    @Override
    public CupidFeignClient create(Throwable cause) {
        log.warn("CupidFeignClient fallback due to: {}", cause.getMessage());
        return new CupidFeignClient() {
            @Override
            public HotelExternal getHotel(long id) {
                throw new CupidClientException(503, "Cupid fallback for getHotel: " + cause);
            }

            @Override
            public HotelLocalizedExternal getHotelLocalized(long id, String lang) {
                throw new CupidClientException(503, "Cupid fallback for getHotelLocalized: " + cause);
            }

            @Override
            public List<ReviewExternal> getReviews(long id, int limit) {
                throw new CupidClientException(503, "Cupid fallback for getReviews: " + cause);
            }
        };
    }
}
