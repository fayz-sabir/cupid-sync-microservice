package com.nuitee.adapterpersistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuitee.adapterpersistence.entity.HotelEntity;
import com.nuitee.adapterpersistence.entity.HotelI18nEntity;
import com.nuitee.adapterpersistence.entity.ReviewEntity;
import com.nuitee.domain.i18n.FacilityI18n;
import com.nuitee.domain.i18n.HotelI18n;
import com.nuitee.domain.i18n.Lang;
import com.nuitee.domain.i18n.Photo;
import com.nuitee.domain.i18n.PolicyI18n;
import com.nuitee.domain.i18n.RoomI18n;
import com.nuitee.domain.model.Address;
import com.nuitee.domain.model.CheckinPolicy;
import com.nuitee.domain.model.Contact;
import com.nuitee.domain.model.Hotel;
import com.nuitee.domain.model.Review;

class EntityToDomainMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EntityToDomainMapper mapper = new EntityToDomainMapper(objectMapper);

    @Test
    void hotelMappingDeserializesJsonFields() throws Exception {
        HotelEntity entity = new HotelEntity();
        entity.setHotelId(1L);
        entity.setChainId(2L);
        entity.setName("Hotel");
        entity.setRating(4.5);
        entity.setStars(4);
        entity.setLat(1.0);
        entity.setLng(2.0);
        entity.setHeroPhoto("photo");
        entity.setChildAllowed(true);
        entity.setPetsAllowed(false);
        entity.setContactJson(objectMapper.writeValueAsString(new Contact("123", "456", "mail")));
        entity.setCheckinPolicyJson(objectMapper.writeValueAsString(new CheckinPolicy("14", "11", "11", List.of("inst"), "spec")));
        entity.setAddressJson(objectMapper.writeValueAsString(new Address("line", "city", "state", "country", "0000")));

        Hotel hotel = mapper.hotel(entity);
        assertEquals("Hotel", hotel.name());
        assertEquals("123", hotel.contact().phone());
        assertEquals("line", hotel.address().line());
    }

    @Test
    void hotelI18nMappingDeserializesLists() throws Exception {
        HotelEntity hotelEntity = new HotelEntity();
        hotelEntity.setHotelId(1L);
        hotelEntity.setChainId(2L);
        hotelEntity.setName("Hotel");
        hotelEntity.setRating(4.5);
        hotelEntity.setStars(4);
        hotelEntity.setLat(1.0);
        hotelEntity.setLng(2.0);
        hotelEntity.setHeroPhoto("photo");
        hotelEntity.setChildAllowed(true);
        hotelEntity.setPetsAllowed(false);

        HotelI18nEntity entity = new HotelI18nEntity();
        entity.setHotel(hotelEntity);
        entity.setLang("en");
        entity.setParking("parking");
        entity.setDescription("desc");
        entity.setHotelType("type");
        entity.setMarkdownDescription("md");
        entity.setImportantInfo("info");
        entity.setFacilities(objectMapper.writeValueAsString(List.of(new FacilityI18n(1L, "Wifi"))));
        entity.setRooms(objectMapper.writeValueAsString(List.of(new RoomI18n(1L, 1L, Lang.EN, "room", "rdesc", 20, "sqm", 2, 0, 2, "rel", List.of(), List.of(), List.of()))));
        entity.setPoliciesJson(objectMapper.writeValueAsString(List.of(new PolicyI18n(1L, Lang.EN, "p", "d"))));
        entity.setPhotosJson(objectMapper.writeValueAsString(List.of(new Photo("url", "hd", "d", true, 5.0, 1, 1L))));

        HotelI18n i18n = mapper.hotelI18n(entity);
        assertEquals("desc", i18n.description());
        assertEquals(1, i18n.facilities().size());
        assertEquals("Wifi", i18n.facilities().get(0).name());
        assertEquals(1, i18n.policies().size());
        assertEquals(1, i18n.photos().size());
    }

    @Test
    void reviewMapping() {
        ReviewEntity entity = new ReviewEntity();
        entity.setReviewId(5L);
        entity.setHotelId(1L);
        entity.setExternalHotelId(1L);
        entity.setAvgScore(9.0);
        entity.setHeadline("Great");
        entity.setPros("pros");
        entity.setCons("cons");
        entity.setLanguage("fr");
        entity.setSource("booking");
        entity.setDate(LocalDateTime.of(2023, 1, 1, 12, 0));
        entity.setCountry("FR");
        entity.setType("leisure");
        entity.setName("Alice");

        Review review = mapper.review(entity);
        assertEquals("Great", review.headline());
        assertEquals(Lang.FR, review.language());
        assertEquals(9, review.score());
    }
}

