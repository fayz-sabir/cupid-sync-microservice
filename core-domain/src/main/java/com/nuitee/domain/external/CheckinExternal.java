package com.nuitee.domain.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckinExternal(
    @JsonProperty("checkin_start")
    String checkinStart,
    @JsonProperty("checkin_end")
    String checkinEnd,
    @JsonProperty("checkout")
    String checkout,
    @JsonProperty("instructions")
    List<String> instructions,
    @JsonProperty("special_instructions")
    String specialInstructions
) {
}
