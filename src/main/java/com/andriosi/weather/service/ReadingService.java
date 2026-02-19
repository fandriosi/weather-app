package com.andriosi.weather.service;

import com.andriosi.weather.domain.Reading;
import com.andriosi.weather.domain.Station;
import com.andriosi.weather.port.ReadingIngestPort;
import com.andriosi.weather.repository.ReadingRepository;
import com.andriosi.weather.repository.StationRepository;
import com.andriosi.weather.web.dto.ReadingIngestRequest;
import com.andriosi.weather.web.dto.ReadingResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReadingService implements ReadingIngestPort {

    private final ReadingRepository readingRepository;
    private final StationRepository stationRepository;

    public ReadingService(ReadingRepository readingRepository, StationRepository stationRepository) {
        this.readingRepository = readingRepository;
        this.stationRepository = stationRepository; 
    }

    @Override
    @Transactional
    public ReadingResponse ingest(ReadingIngestRequest request) {
        Station station = stationRepository.findById(request.getStationId())
            .orElseThrow(() -> new IllegalArgumentException("Station not found"));

        Reading reading = new Reading();
        reading.setStation(station);
        reading.setValue(request.getValue());
        reading.setUnit(request.getUnit());
        reading.setObservedAt(request.getObservedAt() != null ? request.getObservedAt() : Instant.now());
        reading.setCreatedAt(Instant.now());

        Reading saved = readingRepository.save(reading);
        return new ReadingResponse(saved.getId(), station.getId(), saved.getValue(), saved.getUnit(), saved.getObservedAt());
    }

    @Transactional(readOnly = true)
    public List<ReadingResponse> listByStation(UUID stationId) {
        return readingRepository.findByStationIdOrderByObservedAtDesc(stationId).stream()
            .map(reading -> new ReadingResponse(
                reading.id(),
                stationId,
                reading.value(),
                reading.unit(),
                reading.observedAt()
            ))
            .toList();
    }
}
