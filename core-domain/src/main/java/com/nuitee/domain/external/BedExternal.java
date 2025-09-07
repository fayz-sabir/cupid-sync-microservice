package com.nuitee.domain.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BedExternal(
    long id,
    int quantity,
    @JsonProperty("bed_type")
    String type,
    @JsonProperty("bed_size")
    String size
) {
}
