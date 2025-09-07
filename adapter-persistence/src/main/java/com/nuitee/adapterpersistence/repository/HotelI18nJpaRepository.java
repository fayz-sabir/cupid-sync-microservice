package com.nuitee.adapterpersistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nuitee.adapterpersistence.entity.HotelI18nEntity;

public interface HotelI18nJpaRepository extends JpaRepository<HotelI18nEntity, Long> {
    Optional<HotelI18nEntity> findByHotelIdAndLang(Long hotelId, String lang);

    List<HotelI18nEntity> findByHotelId(long hotelId);
}