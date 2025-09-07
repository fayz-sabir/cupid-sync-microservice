package com.nuitee.adaptercacheredis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cupid.cache")
public class CacheProperties {
    private long hotelViewTtlSeconds = 3600;
    private long reviewsTtlSeconds = 1800;

    public long getHotelViewTtlSeconds() {
        return hotelViewTtlSeconds;
    }

    public void setHotelViewTtlSeconds(long v) {
        this.hotelViewTtlSeconds = v;
    }

    public long getReviewsTtlSeconds() {
        return reviewsTtlSeconds;
    }

    public void setReviewsTtlSeconds(long v) {
        this.reviewsTtlSeconds = v;
    }
}
