package com.nuitee.domain.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicyExternal(
    long id,
    @JsonProperty("policy_type")
    String policyType,
    String name,
    String description,
    @JsonProperty("child_allowed")
    String childAllowed,
    @JsonProperty("pets_allowed")
    String petsAllowed,
    String parking

) {
}
