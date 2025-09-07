package com.nuitee.app.mapper;

import com.nuitee.domain.external.AddressExternal;
import com.nuitee.domain.external.AmenityExternal;
import com.nuitee.domain.external.BedExternal;
import com.nuitee.domain.external.FacilityExternal;
import com.nuitee.domain.external.HotelExternal;
import com.nuitee.domain.external.HotelLocalizedExternal;
import com.nuitee.domain.external.PhotoExternal;
import com.nuitee.domain.external.PolicyExternal;
import com.nuitee.domain.external.ReviewExternal;
import com.nuitee.domain.external.RoomExternal;
import com.nuitee.domain.i18n.AmenityI18n;
import com.nuitee.domain.i18n.BedI18n;
import com.nuitee.domain.i18n.FacilityI18n;
import com.nuitee.domain.i18n.HotelI18n;
import com.nuitee.domain.i18n.Lang;
import com.nuitee.domain.i18n.PolicyI18n;
import com.nuitee.domain.i18n.RoomI18n;
import com.nuitee.domain.model.Address;
import com.nuitee.domain.model.CheckinPolicy;
import com.nuitee.domain.model.Contact;
import com.nuitee.domain.model.Coordinates;
import com.nuitee.domain.model.Hotel;
import com.nuitee.domain.i18n.Photo;
import com.nuitee.domain.model.Review;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExternalToDomainMapper {

    public Hotel mapHotel(HotelExternal hotelExternal) {
        var coordinates = new Coordinates(hotelExternal.latitude(), hotelExternal.longitude());
        var contact = new Contact(hotelExternal.phone(), hotelExternal.fax(), hotelExternal.email());
        var checkin = new CheckinPolicy(hotelExternal.checkin().checkinStart(),
            hotelExternal.checkin().checkinEnd(),
            hotelExternal.checkin().checkout(),
            hotelExternal.checkin().instructions(),
            hotelExternal.checkin().specialInstructions());
        var address = mapAddress(hotelExternal.address());

        return new Hotel(
            hotelExternal.hotelId(),
            hotelExternal.chainId(),
            hotelExternal.name(),
            hotelExternal.rating(),
            hotelExternal.stars(),
            coordinates,
            contact,
            address,
            checkin,
            hotelExternal.mainImageTh(),
            hotelExternal.childAllowed(),
            hotelExternal.petsAllowed()
        );
    }

    public List<Review> mapReviews(List<ReviewExternal> reviewExternals, long hotelPkId, long externalHotelId) {
        return reviewExternals.stream()
            .map(reviewExternal -> new Review(
                reviewExternal.reviewId(),
                hotelPkId,
                externalHotelId,
                reviewExternal.averageScore(),
                reviewExternal.source(),
                reviewExternal.date(),
                reviewExternal.type(),
                Lang.fromCode(reviewExternal.language()),
                reviewExternal.pros(),
                reviewExternal.cons(),
                reviewExternal.name(),
                reviewExternal.country(),
                reviewExternal.headline()))
            .toList();
    }

    public HotelI18n mapHotelLocalized(HotelLocalizedExternal hotelLocalizedExternal, Hotel hotel, String language) {
        var lang = Lang.fromCode(language);

        var policies = mapPolicyI18n(hotelLocalizedExternal.policies(), language);
        var facilities = mapFacilityI18n(hotelLocalizedExternal.facilities());
        var rooms = mapRoomI18n(hotelLocalizedExternal.rooms(), hotelLocalizedExternal.hotelId(), language);
        var photos = mapPhotos(hotelLocalizedExternal.photos());

        return new HotelI18n(hotelLocalizedExternal.hotelId(),
            hotel,
            lang,
            hotelLocalizedExternal.parking(),
            hotelLocalizedExternal.description(),
            hotelLocalizedExternal.type(),
            hotelLocalizedExternal.markdownDescription(),
            hotelLocalizedExternal.importantInfo(),
            policies,
            facilities,
            rooms,
            photos);
    }

    private List<RoomI18n> mapRoomI18n(List<RoomExternal> roomsExternal, long hotelId, String language) {
        return roomsExternal.stream().map(roomExternal -> {
            var beds = mapBedI18n(roomExternal.bedTypes());
            return new RoomI18n(roomExternal.id(),
                hotelId,
                Lang.fromCode(language),
                roomExternal.roomName(),
                roomExternal.description(),
                roomExternal.roomSizeSquare(),
                roomExternal.roomSizeUnit(),
                roomExternal.maxAdults(),
                roomExternal.maxChildren(),
                roomExternal.maxOccupancy(),
                roomExternal.bedRelation(),
                beds,
                mapPhotos(roomExternal.photos()),
                mapAmenityI18n(roomExternal.roomAmenities())
            );
        }).toList();
    }

    private List<BedI18n> mapBedI18n(List<BedExternal> bedExternals) {
        return bedExternals.stream().map(bedExternal ->
            new BedI18n(bedExternal.id(),
                bedExternal.type(),
                bedExternal.size(),
                bedExternal.quantity()
            )).toList();
    }

    private List<FacilityI18n> mapFacilityI18n(List<FacilityExternal> facilityExternals) {
        return facilityExternals.stream().map(facilityExternal ->
            new FacilityI18n(facilityExternal.facilityId(),
                facilityExternal.name())).toList();
    }

    private List<PolicyI18n> mapPolicyI18n(List<PolicyExternal> policyExternals, String language) {
        return policyExternals.stream().map(policyExternal ->
            new PolicyI18n(policyExternal.id(),
                Lang.fromCode(language),
                policyExternal.name(),
                policyExternal.description())).toList();
    }

    private List<AmenityI18n> mapAmenityI18n(List<AmenityExternal> amenityExternals) {
        return amenityExternals.stream().map(amenityExternal ->
            new AmenityI18n(amenityExternal.amenitiesId(),
                amenityExternal.name())).toList();
    }

    private List<Photo> mapPhotos(List<PhotoExternal> photoExternals) {
        return photoExternals.stream().map(photoExternal ->
            new Photo(photoExternal.url(),
                photoExternal.hdUrl(),
                photoExternal.description(),
                photoExternal.mainPhoto(),
                photoExternal.score(), photoExternal.classOrder(),
                photoExternal.classId())).toList();
    }

    private Address mapAddress(AddressExternal address) {
        return new Address(address.address(), address.city(), address.state(), address.postalCode(), address.country());
    }
}
