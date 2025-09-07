package com.nuitee.domain.i18n;

import java.util.List;

import com.nuitee.domain.model.Hotel;

public record HotelI18n(
        long hotelId,
        Hotel hotel,
        Lang lang,
        String parking,
        String description,
        String hotelType,
        String markdownDescription,
        String importantInfo,
        List<PolicyI18n> policies,
        List<FacilityI18n> facilities,
        List<RoomI18n> rooms,
        List<Photo> photos
) {}
