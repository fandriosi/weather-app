package com.andriosi.weather.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.andriosi.weather.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.lang.NonNull;

public interface StationRepository extends JpaRepository<Station, UUID> {

    @Override
    @Query("SELECT s FROM Station s LEFT JOIN FETCH s.sensor")
    @NonNull
    List<Station> findAll();

    @Override
    @Query("SELECT s FROM Station s LEFT JOIN FETCH s.sensor WHERE s.id = :id")
    @NonNull
    Optional<Station> findById(UUID id);
    @Query("SELECT s.id FROM Station s")
    List<String> findAllIds();
}
