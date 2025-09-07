package com.nuitee.domain.view;

import java.util.List;

import com.nuitee.domain.model.Address;
import com.nuitee.domain.model.CheckinPolicy;
import com.nuitee.domain.model.Contact;

public record HotelView(long hotelId,
                        String name,
                        double rating,
                        Address address,
                        Contact contact,
                        CheckinPolicy checkin,
                        String mainImageTh,
                        Boolean childAllowed,
                        Boolean petsAllowed,
                        List<ViewPhoto> photos,
                        List<ViewFacility> facilities,
                        List<ViewPolicy> policies,
                        List<RoomView> rooms,
                        List<ReviewView> reviews) {

    public HotelView withReviews(List<ReviewView> reviews) {
        return new HotelView(hotelId,
            name,
            rating,
            address,
            contact,
            checkin,
            mainImageTh,
            childAllowed,
            petsAllowed,
            photos,
            facilities,
            policies,
            rooms,
            reviews);
    }
}