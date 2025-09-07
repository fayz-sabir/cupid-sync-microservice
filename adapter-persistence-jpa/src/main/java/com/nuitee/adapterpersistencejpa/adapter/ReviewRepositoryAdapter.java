package com.nuitee.adapterpersistencejpa.adapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nuitee.adapterpersistencejpa.entity.ReviewEntity;
import com.nuitee.adapterpersistencejpa.mapper.DomainToJpaMapper;
import com.nuitee.adapterpersistencejpa.mapper.JpaToDomainMapper;
import com.nuitee.adapterpersistencejpa.repository.ReviewJpaRepository;
import com.nuitee.domain.model.Review;
import com.nuitee.domain.spi.ReviewRepositoryPort;

@Component
public class ReviewRepositoryAdapter implements ReviewRepositoryPort {

    private final ReviewJpaRepository repo;
    private final JpaToDomainMapper toDomain;
    private final DomainToJpaMapper toJpa;

    public ReviewRepositoryAdapter(ReviewJpaRepository repo,
                                   JpaToDomainMapper toDomain,
                                   DomainToJpaMapper toJpa) {
        this.repo = repo;
        this.toDomain = toDomain;
        this.toJpa = toJpa;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findByHotelId(long hotelId, int limit) {
        return repo.findByExternalHotelIdAfterOrderByDateDesc(hotelId, PageRequest.of(0, limit))
            .stream()
            .map(toDomain::review)
            .toList();
    }

    @Override
    @Transactional
    public void upsertAll(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return;
        }

        Map<Long, List<Review>> byHotel = reviews.stream()
            .collect(Collectors.groupingBy(Review::externalHotelId));

        byHotel.forEach((externalHotelId, hotelReviews) -> {
            List<Long> reviewIds = hotelReviews.stream().map(Review::id).distinct().toList();
            Map<Long, ReviewEntity> existingByReviewId = repo.findByHotelIdAndReviewIdIn(externalHotelId, reviewIds)
                .stream().collect(Collectors.toMap(ReviewEntity::getReviewId, e -> e));

            List<ReviewEntity> entities = hotelReviews.stream()
                .map(review -> {
                    ReviewEntity existing = existingByReviewId.get(review.id());
                    if (existing != null) {
                        return toJpa.updateReviewEntity(existing, review);
                    }
                    return toJpa.review(review);
                })
                .toList();

            repo.saveAll(entities);
        });
    }
}