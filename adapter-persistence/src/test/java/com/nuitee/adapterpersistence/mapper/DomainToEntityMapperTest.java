package com.nuitee.adapterpersistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuitee.adapterpersistence.entity.HotelEntity;
import com.nuitee.adapterpersistence.entity.HotelI18nEntity;
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

class DomainToEntityMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DomainToEntityMapper mapper = new DomainToEntityMapper(objectMapper);

    @Test
    void hotelMappingSerializesJsonFields() throws Exception {
        Hotel hotel = new Hotel(
            1L,
            2L,
            "Hotel",
            4.5,
            4,
            new Coordinates(1.0, 2.0),
            new Contact("123", "456", "mail@example.com"),
            new Address("line", "city", "state", "country", "0000"),
            new CheckinPolicy("14", "11", "11", List.of("inst"), "special"),
            "photo.jpg",
            true,
            false
        );

        HotelEntity entity = mapper.hotel(hotel);
        assertEquals(1L, entity.getHotelId());
        Contact contact = objectMapper.readValue(entity.getContactJson(), Contact.class);
        assertEquals("123", contact.phone());
        Address address = objectMapper.readValue(entity.getAddressJson(), Address.class);
        assertEquals("line", address.line());
        CheckinPolicy policy = objectMapper.readValue(entity.getCheckinPolicyJson(), CheckinPolicy.class);
        assertEquals("14", policy.checkinStart());
    }

    @Test
    void hotelI18nMappingSerializesLists() throws Exception {
        Hotel base = new Hotel(1L, 2L, "Hotel", 4.5, 4, new Coordinates(1, 2), null, null, null, null, true, false);
        HotelI18n i18n = new HotelI18n(
            1L,
            base,
            Lang.EN,
            "parking",
            "desc",
            "type",
            "md",
            "info",
            List.of(new PolicyI18n(1L, Lang.EN, "p", "d")),
            List.of(new FacilityI18n(1L, "Wifi")),
            List.of(new RoomI18n(1L, 1L, Lang.EN, "room", "rdesc", 20, "sqm", 2, 0, 2, "rel",
                List.of(new BedI18n(1L, "Queen", "Big", 1)),
                List.of(new Photo("url", "hd", "d", true, 5.0, 1, 1L)),
                List.of(new AmenityI18n(1L, "TV"))
            )),
            List.of(new Photo("url", "hd", "d", true, 5.0, 1, 1L))
        );

        HotelI18nEntity entity = mapper.hotelI18n(i18n, 10L);
        List<FacilityI18n> facilities = objectMapper.readValue(entity.getFacilities(), new TypeReference<>() {});
        assertEquals(1, facilities.size());
        List<PolicyI18n> policies = objectMapper.readValue(entity.getPoliciesJson(), new TypeReference<>() {});
        assertEquals(1, policies.size());
        List<RoomI18n> rooms = objectMapper.readValue(entity.getRooms(), new TypeReference<>() {});
        assertEquals(1, rooms.size());
        List<Photo> photos = objectMapper.readValue(entity.getPhotosJson(), new TypeReference<>() {});
        assertEquals(1, photos.size());
    }
}

