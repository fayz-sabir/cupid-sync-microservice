package com.nuitee.domain.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HotelExternal(
    @JsonProperty("hotel_id")
    long hotelId,
    @JsonProperty("chain_id")
    int chainId,
    @JsonProperty("main_image_th")
    String mainImageTh,
    int stars,
    double rating,
    double latitude,
    double longitude,
    String phone,
    String email,
    String fax,
    @JsonProperty("hotel_name")
    String name,
    AddressExternal address,
    CheckinExternal checkin,
    @JsonProperty("child_allowed")
    Boolean childAllowed,
    @JsonProperty("pets_allowed")
    Boolean petsAllowed,
    @JsonProperty("review_count")
    Integer reviewCount

) {
}
