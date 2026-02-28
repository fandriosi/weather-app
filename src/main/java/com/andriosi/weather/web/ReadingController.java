package com.andriosi.weather.web;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andriosi.weather.service.DynamoDbReadingService;
import com.andriosi.weather.web.dto.ReadingResponse;

@RestController
@RequestMapping("/api/readings")
public class ReadingController {

    private final DynamoDbReadingService readingService;    
    public ReadingController(DynamoDbReadingService readingService) {
        this.readingService = readingService;
    }
    
    @GetMapping("/{stationId}")
    public List<ReadingResponse> getReadingByStationId(@PathVariable UUID stationId) {
        return readingService.getMeteorologicalDataByStation(stationId);
    }
}
