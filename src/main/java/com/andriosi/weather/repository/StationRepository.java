package com.andriosi.weather.repository;

import java.util.UUID;
import com.andriosi.weather.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, UUID> {
}
