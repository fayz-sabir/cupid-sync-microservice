package com.nuitee.domain.i18n;

import java.util.List;

public record RoomI18n(long roomId,
                       long hotelId,
                       Lang lang,
                       String roomName,
                       String description,
                       double roomSizeSquare,
                       String roomSizeUnit,
                       int maxAdults,
                       int maxChildren,
                       int maxOccupancy,
                       String bedRelation,
                       List<BedI18n> beds,
                       List<Photo> photos,
                       List<AmenityI18n> amenities) {
}
