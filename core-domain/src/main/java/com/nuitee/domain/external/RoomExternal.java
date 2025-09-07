package com.nuitee.domain.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RoomExternal(
    long id,
    @JsonProperty("hotel_id")
    long hotelId,
    @JsonProperty("room_name")
    String roomName,
    String description,
    @JsonProperty("room_size_square")
    double roomSizeSquare,
    @JsonProperty("room_size_unit")
    String roomSizeUnit,
    @JsonProperty("max_adults")
    int maxAdults,
    @JsonProperty("max_children")
    int maxChildren,
    @JsonProperty("max_occupancy")
    int maxOccupancy,
    @JsonProperty("bed_relation")
    String bedRelation,
    @JsonProperty("bed_types")
    List<BedExternal> bedTypes,
    @JsonProperty("room_amenities")
    List<AmenityExternal> roomAmenities,
    List<PhotoExternal> photos
) {
}
