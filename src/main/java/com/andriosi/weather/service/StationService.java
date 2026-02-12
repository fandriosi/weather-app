package com.andriosi.weather.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andriosi.weather.domain.Station;
import com.andriosi.weather.repository.StationRepository;
import com.andriosi.weather.web.dto.StationRequest;
import com.andriosi.weather.web.dto.StationResponse;

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
        station.setLatitude(request.getLatitude());
        station.setLongitude(request.getLongitude());
        Station saved = stationRepository.save(station);
        return new StationResponse(saved.getId(), saved.getName(), saved.getLatitude(), saved.getLongitude());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> list() {
        return stationRepository.findAll().stream()
            .map(station -> new StationResponse(station.getId(), station.getName(), station.getLatitude(), station.getLongitude()))
            .toList();
    }
}
