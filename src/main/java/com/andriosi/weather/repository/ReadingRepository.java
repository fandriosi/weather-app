package com.andriosi.weather.repository;

import com.andriosi.weather.domain.Reading;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingRepository extends JpaRepository<Reading, UUID> {
    List<Reading> findBySensorIdOrderByObservedAtDesc(UUID sensorId);
}
