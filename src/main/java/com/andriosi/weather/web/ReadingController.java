package com.andriosi.weather.web;

import com.andriosi.weather.service.ReadingService;
import com.andriosi.weather.web.dto.ReadingResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readings")
public class ReadingController {

    private final ReadingService readingService;

    public ReadingController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','READER')")
    @GetMapping
    public List<ReadingResponse> listBySensor(@RequestParam("sensorId") Long sensorId) {
        return readingService.listBySensor(sensorId);
    }
}
