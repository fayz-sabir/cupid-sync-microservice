package com.nuitee.adapterpersistence.mapper;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import com.nuitee.adapterpersistence.entity.HotelEntity;
import com.nuitee.adapterpersistence.entity.HotelI18nEntity;
import com.nuitee.adapterpersistence.entity.ReviewEntity;

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

@Component
public class EntityToDomainMapper {

    private final ObjectMapper objectMapper;

    public EntityToDomainMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Hotel hotel(HotelEntity hotelEntity) {
        Coordinates coords = new Coordinates(hotelEntity.getLat(), hotelEntity.getLng());

        Contact contact = readJsonOrDefault(hotelEntity.getContactJson(), Contact.class, new Contact(null, null, null));
        CheckinPolicy checkin = readJsonOrDefault(
            hotelEntity.getCheckinPolicyJson(),
            CheckinPolicy.class,
            new CheckinPolicy(null, null, null, List.of(), null)
        );

        Address address = readJsonOrDefault(hotelEntity.getAddressJson(), Address.class, null);

        return new Hotel(
            hotelEntity.getHotelId(),
            hotelEntity.getChainId(),
            hotelEntity.getName(),
            hotelEntity.getRating(),
            hotelEntity.getStars(),
            coords,
            contact,
            address,
            checkin,
            hotelEntity.getHeroPhoto(),
            hotelEntity.isChildAllowed(),
            hotelEntity.isPetsAllowed()
        );
    }

    public Review review(ReviewEntity reviewEntity) {
        return new Review(
            reviewEntity.getReviewId(),
            reviewEntity.getHotelId(),
            reviewEntity.getExternalHotelId(),
            getScore(reviewEntity),
            reviewEntity.getSource(),
            reviewEntity.getDate(),
            reviewEntity.getType(),
            Lang.fromCode(reviewEntity.getLanguage()),
            reviewEntity.getPros(),
            reviewEntity.getCons(),
            reviewEntity.getName(),
            reviewEntity.getCountry(),
            reviewEntity.getHeadline()
        );
    }

    private static int getScore(ReviewEntity reviewEntity) {
        return reviewEntity.getAvgScore() == null ? 0 : reviewEntity.getAvgScore().intValue();
    }

    public HotelI18n hotelI18n(HotelI18nEntity hotelI18nEntity) {
        Hotel hotel = hotel(hotelI18nEntity.getHotel());
        List<PolicyI18n> policies = readJsonListOrDefault(hotelI18nEntity.getPoliciesJson(), new TypeReference<List<PolicyI18n>>() {
        }, List.of());
        List<FacilityI18n> facilities = readJsonListOrDefault(hotelI18nEntity.getFacilities(), new TypeReference<List<FacilityI18n>>() {
        }, List.of());
        List<RoomI18n> rooms = readJsonListOrDefault(hotelI18nEntity.getRooms(), new TypeReference<List<RoomI18n>>() {
        }, List.of());
        List<Photo> photos = readJsonListOrDefault(hotelI18nEntity.getPhotosJson(), new TypeReference<List<Photo>>() {
        }, List.of());

        return new HotelI18n(
            hotel.id(),
            hotel,
            Lang.fromCode(hotelI18nEntity.getLang()),
            nullSafe(hotelI18nEntity.getParking()),
            nullSafe(hotelI18nEntity.getDescription()),
            nullSafe(hotelI18nEntity.getHotelType()),
            nullSafe(hotelI18nEntity.getMarkdownDescription()),
            nullSafe(hotelI18nEntity.getImportantInfo()),
            policies,
            facilities,
            rooms,
            photos
        );
    }

    private <T> T readJsonOrDefault(String json, Class<T> clazz, T fallback) {
        if (isBlank(json)) {
            return fallback;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Failed to deserialize JSON into " + clazz.getSimpleName(), ex);
        }
    }

    private <T> List<T> readJsonListOrDefault(String json, TypeReference<List<T>> typeRef, List<T> fallback) {
        if (isBlank(json)) {
            return fallback;
        }
        try {
            List<T> list = objectMapper.readValue(json, typeRef);
            return (list == null) ? fallback : list;
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Failed to deserialize JSON list: " + typeRef.getType(), ex);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }
}