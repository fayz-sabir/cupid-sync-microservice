package com.nuitee.domain.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AmenityExternal(
    @JsonProperty("amenities_id")
    long amenitiesId,
    String name,
    int sort
) {
}
