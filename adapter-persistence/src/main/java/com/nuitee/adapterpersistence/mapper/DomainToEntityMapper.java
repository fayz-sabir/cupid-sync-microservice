package com.nuitee.adapterpersistence.mapper;

import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import com.nuitee.adapterpersistence.entity.HotelEntity;
import com.nuitee.adapterpersistence.entity.HotelI18nEntity;
import com.nuitee.adapterpersistence.entity.ReviewEntity;
import com.nuitee.domain.i18n.HotelI18n;
import com.nuitee.domain.model.Hotel;
import com.nuitee.domain.model.Review;

@Component
public class DomainToEntityMapper {

    private final ObjectMapper objectMapper;

    public DomainToEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public HotelEntity hotel(Hotel hotel) {
        HotelEntity hotelEntity = new HotelEntity();
        hotelEntity.setHotelId(hotel.id());
        hotelEntity.setChainId(hotel.chainId());
        hotelEntity.setStars(hotel.stars());
        hotelEntity.setRating(hotel.rating());
        hotelEntity.setHeroPhoto(hotel.mainImageTh());
        hotelEntity.setName(hotel.name());
        hotelEntity.setChildAllowed(hotel.childAllowed());
        hotelEntity.setPetsAllowed(hotel.petsAllowed());

        if (hotel.coordinates() != null) {
            hotelEntity.setLat(hotel.coordinates().latitude());
            hotelEntity.setLng(hotel.coordinates().longitude());
        }

        hotelEntity.setContactJson(toJsonOrNull(hotel.contact()));
        hotelEntity.setCheckinPolicyJson(toJsonOrNull(hotel.checkinPolicy()));
        hotelEntity.setAddressJson(toJsonOrNull(hotel.address()));
        return hotelEntity;
    }

    public ReviewEntity review(Review review) {
        ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setHotelId(review.hotelId());
        reviewEntity.setExternalHotelId(review.externalHotelId());
        reviewEntity.setAvgScore((double) review.score());
        reviewEntity.setHeadline(review.headline());
        reviewEntity.setPros(review.pros());
        reviewEntity.setCons(review.cons());
        reviewEntity.setLanguage(review.language().code());
        reviewEntity.setSource(review.source());
        reviewEntity.setDate(review.date());
        reviewEntity.setReviewId(review.id());
        reviewEntity.setCountry(review.country());
        reviewEntity.setType(review.type());
        reviewEntity.setName(review.authorName());
        return reviewEntity;
    }

    public HotelI18nEntity hotelI18n(HotelI18n hotelI18n, Long hotelPkId) {
        HotelI18nEntity hotelI18nEntity = new HotelI18nEntity();
        HotelEntity ref = new HotelEntity();
        ref.setId(hotelPkId);
        hotelI18nEntity.setHotel(ref);
        hotelI18nEntity.setHotelType(hotelI18n.hotelType());
        hotelI18nEntity.setLang(hotelI18n.lang().code().toLowerCase());
        hotelI18nEntity.setDescription(hotelI18n.description());
        hotelI18nEntity.setMarkdownDescription(hotelI18n.markdownDescription());
        hotelI18nEntity.setImportantInfo(hotelI18n.importantInfo());
        hotelI18nEntity.setFacilities(toJsonOrEmptyArray(hotelI18n.facilities()));
        hotelI18nEntity.setRooms(toJsonOrEmptyArray(hotelI18n.rooms()));
        hotelI18nEntity.setPoliciesJson(toJsonOrEmptyArray(hotelI18n.policies()));
        hotelI18nEntity.setParking(hotelI18n.parking());
        hotelI18nEntity.setPhotosJson(toJsonOrEmptyArray(hotelI18n.photos()));
        return hotelI18nEntity;
    }

    public HotelEntity updateHotelEntity(HotelEntity target, Hotel source) {

        target.setChainId(source.chainId());
        target.setStars(source.stars());
        target.setRating(source.rating());
        target.setHeroPhoto(source.mainImageTh());

        if (source.coordinates() != null) {
            target.setLat(source.coordinates().latitude());
            target.setLng(source.coordinates().longitude());
        } else {
            target.setLat(0.0);
            target.setLng(0.0);
        }

        target.setContactJson(toJsonOrNull(source.contact()));
        target.setCheckinPolicyJson(toJsonOrNull(source.checkinPolicy()));
        target.setAddressJson(toJsonOrNull(source.address()));

        return target;
    }

    public HotelI18nEntity updateHotelI18nEntity(HotelI18nEntity target, HotelI18n source) {
        if (source == null) {
            return target;
        }

        target.setLang(source.lang() != null ? source.lang().code().toLowerCase() : null);
        target.setParking(source.parking());
        target.setDescription(source.description());
        target.setMarkdownDescription(source.markdownDescription());
        target.setImportantInfo(source.importantInfo());

        target.setFacilities(toJsonOrEmptyArray(source.facilities()));
        target.setRooms(toJsonOrEmptyArray(source.rooms()));
        target.setPoliciesJson(toJsonOrEmptyArray(source.policies()));
        target.setPhotosJson(toJsonOrEmptyArray(source.photos()));

        target.setHotelType(source.hotelType());

        return target;
    }

    public ReviewEntity updateReviewEntity(ReviewEntity target, Review source) {
        if (source == null) {
            return target;
        }
        target.setExternalHotelId(source.externalHotelId());
        target.setAvgScore((double) source.score());
        target.setHeadline(source.headline());
        target.setPros(source.pros());
        target.setCons(source.cons());
        target.setLanguage(source.language().code());
        target.setSource(source.source());
        target.setDate(source.date());
        target.setCountry(source.country());
        target.setType(source.type());
        target.setName(source.authorName());
        return target;
    }

    private String toJsonOrNull(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON", e);
        }
    }

    private String toJsonOrEmptyArray(Object value) {
        if (value == null) {
            return "[]";
        }
        if (value instanceof Collection<?> c && c.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON array", e);
        }
    }
}