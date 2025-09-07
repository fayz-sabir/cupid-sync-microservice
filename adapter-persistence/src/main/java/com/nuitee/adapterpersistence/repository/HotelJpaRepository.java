package com.nuitee.adapterpersistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nuitee.adapterpersistence.entity.HotelEntity;

public interface HotelJpaRepository extends JpaRepository<HotelEntity, Long> {
    Optional<HotelEntity> findByHotelId(long id);

    @Query("select h.id from HotelEntity h where h.hotelId = :hotelId")
    Optional<Long> findPkByHotelId(@Param("hotelId") Long hotelId);
}
