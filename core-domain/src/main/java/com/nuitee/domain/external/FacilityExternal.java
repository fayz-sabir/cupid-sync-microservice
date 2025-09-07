package com.nuitee.domain.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FacilityExternal(
    @JsonProperty("facility_id")
    long facilityId,
    String name
) {

}
