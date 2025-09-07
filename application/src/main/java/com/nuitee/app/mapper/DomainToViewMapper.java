package com.nuitee.app.mapper;

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuitee.domain.i18n.AmenityI18n;
import com.nuitee.domain.i18n.BedI18n;
import com.nuitee.domain.i18n.FacilityI18n;
import com.nuitee.domain.i18n.HotelI18n;
import com.nuitee.domain.i18n.PolicyI18n;
import com.nuitee.domain.i18n.RoomI18n;
import com.nuitee.domain.model.Hotel;
import com.nuitee.domain.model.Review;
import com.nuitee.domain.i18n.Photo;
import com.nuitee.domain.view.HotelView;
import com.nuitee.domain.view.ReviewView;
import com.nuitee.domain.view.RoomView;
import com.nuitee.domain.view.ViewAmenity;
import com.nuitee.domain.view.ViewBed;
import com.nuitee.domain.view.ViewFacility;
import com.nuitee.domain.view.ViewPhoto;
import com.nuitee.domain.view.ViewPolicy;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DomainToViewMapper {

    public HotelView toHotelView(HotelI18n hotelI18n) {

        Hotel hotel = hotelI18n.hotel();
        List<ViewFacility> viewFacilities = hotelI18n.facilities().stream().map(this::mapFacility).toList();
        List<ViewPolicy> viewPolicies = hotelI18n.policies().stream().map(this::mapPolicy).toList();
        List<RoomView> roomViews = hotelI18n.rooms().stream().map(this::mapRoom).toList();

        List<ViewPhoto> hotelPhotos = hotelI18n.photos().stream().map(this::mapPhoto).toList();

        return new HotelView(
            hotel.id(),
            hotel.name(),
            hotel.rating(),
            hotel.address(),
            hotel.contact(),
            hotel.checkinPolicy(),
            hotel.mainImageTh(),
            hotel.childAllowed(),
            hotel.petsAllowed(),
            hotelPhotos,
            viewFacilities,
            viewPolicies,
            roomViews,
            emptyList()
        );
    }

    private RoomView mapRoom(RoomI18n roomI18n) {
        List<ViewPhoto> roomPhotos = roomI18n.photos().stream().map(this::mapPhoto).toList();
        List<ViewBed> roomBeds = roomI18n.beds().stream().map(this::mapBed).toList();
        List<ViewAmenity> roomAmenities = roomI18n.amenities().stream().map(this::mapAmenity).toList();

        return new RoomView(
            roomI18n.roomId(),
            roomI18n.roomName(),
            roomI18n.description(),
            roomI18n.roomSizeSquare(),
            roomI18n.roomSizeUnit(),
            roomI18n.maxAdults(),
            roomI18n.maxChildren(),
            roomI18n.maxOccupancy(),
            roomI18n.bedRelation(),
            roomBeds,
            roomAmenities,
            roomPhotos
        );
    }

    private ViewAmenity mapAmenity(AmenityI18n amenityI18n) {
        return new ViewAmenity(amenityI18n.amenityId(), amenityI18n.name());
    }

    private ViewBed mapBed(BedI18n bedI18n) {
        return new ViewBed(
            bedI18n.bedId(),
            bedI18n.quantity(),
            bedI18n.type(),
            bedI18n.size());
    }

    private ViewFacility mapFacility(FacilityI18n facilityI18n) {
        return new ViewFacility(
            facilityI18n.facilityId(),
            facilityI18n.name()
        );
    }

    private ViewPolicy mapPolicy(PolicyI18n policyI18n) {
        return new ViewPolicy(
            policyI18n.policyId(),
            policyI18n.name(),
            policyI18n.description()
        );
    }

    public HotelView withReviews(HotelView base, List<ReviewView> reviews) {
        return base.withReviews(reviews);
    }

    private ViewPhoto mapPhoto(Photo photo) {
        return new ViewPhoto(photo.url(), photo.hdUrl(), photo.description(), photo.classId(), photo.isMain(), photo.score());
    }
}
