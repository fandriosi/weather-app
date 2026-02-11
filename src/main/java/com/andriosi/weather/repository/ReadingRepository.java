package com.andriosi.weather.repository;

import com.andriosi.weather.domain.Reading;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingRepository extends JpaRepository<Reading, Long> {
    List<Reading> findBySensorIdOrderByObservedAtDesc(Long sensorId);
}
