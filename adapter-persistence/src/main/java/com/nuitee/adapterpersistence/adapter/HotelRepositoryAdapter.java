package com.nuitee.adapterpersistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nuitee.adapterpersistence.mapper.DomainToEntityMapper;
import com.nuitee.adapterpersistence.mapper.EntityToDomainMapper;
import com.nuitee.adapterpersistence.repository.HotelJpaRepository;
import com.nuitee.domain.model.Hotel;
import com.nuitee.domain.spi.HotelRepositoryPort;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HotelRepositoryAdapter implements HotelRepositoryPort {

    private final HotelJpaRepository repo;
    private final EntityToDomainMapper toDomain;
    private final DomainToEntityMapper toJpa;
    private final Counter findByIdCounter;
    private final Counter upsertCounter;
    private final Counter updateCounter;
    private static final Logger LOGGER = LoggerFactory.getLogger(HotelRepositoryAdapter.class);

    public HotelRepositoryAdapter(HotelJpaRepository repo,
                                  EntityToDomainMapper toDomain,
                                  DomainToEntityMapper toJpa,
                                  MeterRegistry registry) {
        this.repo = repo;
        this.toDomain = toDomain;
        this.toJpa = toJpa;
        this.findByIdCounter = registry.counter("repository.hotel.findById");
        this.upsertCounter = registry.counter("repository.hotel.upsert");
        this.updateCounter = registry.counter("repository.hotel.update");
    }

    @Override
    public Optional<Hotel> findById(long id) {
        LOGGER.info("Finding hotel by id {}", id);
        findByIdCounter.increment();
        return repo.findByHotelId(id).map(toDomain::hotel);
    }

    @Override
    @Transactional
    public void upsert(Hotel hotel) {
        var optionalHotelEntity = repo.findByHotelId(hotel.id());
        if (optionalHotelEntity.isEmpty()) {
            LOGGER.info("Upserting hotel {}", hotel.id());
            upsertCounter.increment();
            repo.save(toJpa.hotel(hotel));
            return;
        }

        LOGGER.info("Updating hotel {}", hotel.id());
        updateCounter.increment();
        var managed = optionalHotelEntity.get();
        repo.save(toJpa.updateHotelEntity(managed, hotel));
    }

    @Override
    public long resolvePkByExternalId(long externalHotelId) {
        return repo.findPkByHotelId(externalHotelId)
            .orElseThrow(() -> new IllegalStateException("Hotel not found for external id=" + externalHotelId));
    }
}