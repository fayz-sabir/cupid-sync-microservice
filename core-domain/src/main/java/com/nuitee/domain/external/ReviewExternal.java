package com.nuitee.domain.external;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReviewExternal(
    @JsonProperty("review_id")
    long reviewId,
    @JsonProperty("average_score")
    int averageScore,
    String country,
    String type,
    String name,
    LocalDateTime date,
    String headline,
    String language,
    String pros,
    String cons,
    String source
) {
}
