package com.nuitee.adapterpersistencejpa.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nuitee.adapterpersistencejpa.entity.ReviewEntity;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, Long> {
    @Query("select r from ReviewEntity r where r.externalHotelId=:externalHotelId order by r.date desc")
    List<ReviewEntity> findByExternalHotelIdAfterOrderByDateDesc(@Param("externalHotelId") long externalHotelId,
                                                                 Pageable pageable);

    @Query("""
            select r from ReviewEntity r
            where r.externalHotelId = :externalHotelId and r.reviewId in :reviewIds
        """)
    List<ReviewEntity> findByHotelIdAndReviewIdIn(@Param("externalHotelId") long externalHotelId, @Param("reviewIds") List<Long> reviewIds);
}