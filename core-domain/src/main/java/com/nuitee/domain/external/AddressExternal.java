package com.nuitee.domain.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressExternal(String address, String city, String state, String country,
                              @JsonProperty("postal_code") String postalCode) {
}
