package com.nuitee.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.nuitee.domain.external.AddressExternal;
import com.nuitee.domain.external.AmenityExternal;
import com.nuitee.domain.external.BedExternal;
import com.nuitee.domain.external.CheckinExternal;
import com.nuitee.domain.external.FacilityExternal;
import com.nuitee.domain.external.HotelExternal;
import com.nuitee.domain.external.HotelLocalizedExternal;
import com.nuitee.domain.external.PhotoExternal;
import com.nuitee.domain.external.PolicyExternal;
import com.nuitee.domain.external.ReviewExternal;
import com.nuitee.domain.external.RoomExternal;
import com.nuitee.domain.i18n.HotelI18n;
import com.nuitee.domain.i18n.Lang;
import com.nuitee.domain.model.Address;
import com.nuitee.domain.model.CheckinPolicy;
import com.nuitee.domain.model.Contact;
import com.nuitee.domain.model.Coordinates;
import com.nuitee.domain.model.Hotel;
import com.nuitee.domain.model.Review;

public class ExternalToDomainMapperTest {

    private final ExternalToDomainMapper mapper = new ExternalToDomainMapper();

    @Test
    void mapsHotelExternalToDomain() {
        AddressExternal addr = new AddressExternal("line", "city", "state", "country", "pc");
        CheckinExternal checkin = new CheckinExternal("14:00", "20:00", "10:00", List.of("inst"), "special");
        HotelExternal ext = new HotelExternal(1L, 2, "img", 4, 4.5, 1.0, 2.0, "p", "e", "f", "Name", addr, checkin, true, false, 10);

        Hotel hotel = mapper.mapHotel(ext);
        assertEquals(ext.hotelId(), hotel.id());
        assertEquals("Name", hotel.name());
        assertEquals("city", hotel.address().city());
    }

    @Test
    void mapsReviewsExternalToDomain() {
        ReviewExternal re = new ReviewExternal(5L, 9, "FR", "type", "name", LocalDateTime.now(), "head", "fr", "pros", "cons", "src");
        List<Review> reviews = mapper.mapReviews(List.of(re), 100L, 1L);
        assertEquals(1, reviews.size());
        Review r = reviews.get(0);
        assertEquals(5L, r.id());
        assertEquals(100L, r.hotelId());
        assertEquals(Lang.FR, r.language());
    }

    @Test
    void mapsHotelLocalized() {
        AddressExternal addr = new AddressExternal("line", "city", "state", "country", "pc");
        List<FacilityExternal> facilities = List.of(new FacilityExternal(1L, "Pool"));
        List<PolicyExternal> policies = List.of(new PolicyExternal(2L, "type", "policy", "desc", "y", "n", "park"));
        List<BedExternal> beds = List.of(new BedExternal(3L, 1, "king", "large"));
        List<AmenityExternal> amenities = List.of(new AmenityExternal(4L, "TV", 1));
        List<PhotoExternal> photos = List.of(new PhotoExternal("u", "hd", "d", "c1", "c2", true, 4.5, 9, 1));
        List<RoomExternal> rooms = List.of(new RoomExternal(6L, 1L, "room", "desc", 20.0, "sqm", 2, 1, 3, "rel", beds, amenities, photos));
        HotelLocalizedExternal extLoc = new HotelLocalizedExternal(1L, "type", "parking", "desc", addr, "info", "md", facilities, policies, rooms, photos);
        Hotel hotel = new Hotel(1L, 2L, "Hotel", 4.0, 5, new Coordinates(0, 0), new Contact("p", "f", "e"), new Address("l", "c", "s", "co", "p"), new CheckinPolicy("", "", "", List.of(), ""), "img", true, false);

        HotelI18n i18n = mapper.mapHotelLocalized(extLoc, hotel, "fr");
        assertEquals(Lang.FR, i18n.lang());
        assertEquals(1, i18n.facilities().size());
        assertEquals(1, i18n.policies().size());
        assertEquals(1, i18n.rooms().size());
        assertEquals(1, i18n.rooms().get(0).beds().size());
    }
}
