package com.andriosi.weather.repository;

import com.andriosi.weather.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
}
