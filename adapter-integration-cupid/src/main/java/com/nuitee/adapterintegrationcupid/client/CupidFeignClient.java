package com.nuitee.adapterintegrationcupid.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nuitee.adapterintegrationcupid.config.CupidFeignConfig;
import com.nuitee.domain.external.HotelExternal;
import com.nuitee.domain.external.HotelLocalizedExternal;
import com.nuitee.domain.external.ReviewExternal;

@FeignClient(
    name = "cupid",
    url = "${cupid.api.base-url}",
    configuration = CupidFeignConfig.class,
    fallbackFactory = CupidFallbackFactory.class
)
public interface CupidFeignClient {

    @GetMapping("/v3.0/property/{id}")
    HotelExternal getHotel(@PathVariable("id") long id);

    @GetMapping("/v3.0/property/{id}/lang/{lang}")
    HotelLocalizedExternal getHotelLocalized(@PathVariable("id") long id, @PathVariable("lang") String lang);

    @GetMapping("/v3.0/property/reviews/{id}/{limit}")
    List<ReviewExternal> getReviews(@PathVariable("id") long id, @PathVariable("limit") int limit);
}
