package com.nuitee.domain.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HotelLocalizedExternal(
    @JsonProperty("hotel_id") long hotelId,
    @JsonProperty("hotel_type")
    String type,
    String parking,
    @JsonProperty("description") String description,
    @JsonProperty("address") AddressExternal address,
    @JsonProperty("important_info") String importantInfo,
    @JsonProperty("markdown_description") String markdownDescription,
    List<FacilityExternal> facilities,
    List<PolicyExternal> policies,
    List<RoomExternal> rooms,
    List<PhotoExternal> photos
) {
}
