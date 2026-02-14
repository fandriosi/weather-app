package com.andriosi.weather.repository;

import java.util.Collection;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andriosi.weather.domain.Reading;
import com.andriosi.weather.web.dto.ReadingResponse;

public interface ReadingRepository extends JpaRepository<Reading, UUID> {

    Collection<ReadingResponse> findByStationIdOrderByObservedAtDesc(UUID stationId);
    
}
