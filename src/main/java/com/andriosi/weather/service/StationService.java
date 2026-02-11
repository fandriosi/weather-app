package com.andriosi.weather.service;

import com.andriosi.weather.domain.Station;
import com.andriosi.weather.repository.StationRepository;
import com.andriosi.weather.web.dto.StationRequest;
import com.andriosi.weather.web.dto.StationResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse create(StationRequest request) {
        Station station = new Station();
        station.setName(request.getName());
        station.setLocation(request.getLocation());
        Station saved = stationRepository.save(station);
        return new StationResponse(saved.getId(), saved.getName(), saved.getLocation());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> list() {
        return stationRepository.findAll().stream()
            .map(station -> new StationResponse(station.getId(), station.getName(), station.getLocation()))
            .toList();
    }
}
