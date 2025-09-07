package com.nuitee.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.nuitee.domain.i18n.AmenityI18n;
import com.nuitee.domain.i18n.BedI18n;
import com.nuitee.domain.i18n.FacilityI18n;
import com.nuitee.domain.i18n.HotelI18n;
import com.nuitee.domain.i18n.Lang;
import com.nuitee.domain.i18n.Photo;
import com.nuitee.domain.i18n.PolicyI18n;
import com.nuitee.domain.i18n.RoomI18n;
import com.nuitee.domain.model.Address;
import com.nuitee.domain.model.CheckinPolicy;
import com.nuitee.domain.model.Contact;
import com.nuitee.domain.model.Coordinates;
import com.nuitee.domain.model.Hotel;
import com.nuitee.domain.view.HotelView;
import com.nuitee.domain.view.ReviewView;

public class DomainToViewMapperTest {

    private final DomainToViewMapper mapper = new DomainToViewMapper();

    private HotelI18n sampleHotel() {
        Hotel hotel = new Hotel(1L, 1L, "Hotel", 4.2, 5,
            new Coordinates(1.0, 2.0), new Contact("p", "f", "e"), new Address("l", "c", "s", "co", "p"),
            new CheckinPolicy("14:00", "12:00", "10:00", List.of(), "special"), "img", true, false);
        List<FacilityI18n> facilities = List.of(new FacilityI18n(10L, "Wifi"));
        List<PolicyI18n> policies = List.of(new PolicyI18n(20L, Lang.EN, "policy", "desc"));
        List<Photo> photos = List.of(new Photo("u", "hd", "d", true, 4.5, 1, 99L));
        List<BedI18n> beds = List.of(new BedI18n(30L, "king", "large", 1));
        List<AmenityI18n> amenities = List.of(new AmenityI18n(40L, "TV"));
        List<RoomI18n> rooms = List.of(new RoomI18n(50L, 1L, Lang.EN, "room", "desc", 20.0, "sqm", 2, 1, 3, "relation", beds, photos, amenities));
        return new HotelI18n(1L, hotel, Lang.EN, "parking", "description", "type", "md", "info", policies, facilities, rooms, photos);
    }

    @Test
    void mapsDomainToView() {
        HotelI18n i18n = sampleHotel();
        HotelView view = mapper.toHotelView(i18n);

        assertEquals(i18n.hotel().id(), view.hotelId());
        assertEquals(1, view.facilities().size());
        assertEquals(1, view.policies().size());
        assertEquals(1, view.rooms().size());
        assertEquals(1, view.photos().size());
    }

    @Test
    void withReviewsAddsReviews() {
        HotelView base = mapper.toHotelView(sampleHotel());
        List<ReviewView> reviews = List.of(new ReviewView(1, base.hotelId(), base.hotelId(), 8, "src", null, "t", Lang.EN, "p", "c", "a", "co", "h"));
        HotelView result = mapper.withReviews(base, reviews);
        assertEquals(reviews, result.reviews());
    }
}
