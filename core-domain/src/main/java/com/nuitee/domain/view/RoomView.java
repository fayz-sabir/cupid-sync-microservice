package com.nuitee.domain.view;

import java.util.List;

public record RoomView(long roomId,
                       String name,
                       String description,
                       double sizeSqm,
                       String sizeUnit,
                       int maxAdults,
                       int maxChildren,
                       int maxOccupancy,
                       String bedRelation,
                       List<ViewBed> bedTypes,
                       List<ViewAmenity> amenities,
                       List<ViewPhoto> photos) {
}