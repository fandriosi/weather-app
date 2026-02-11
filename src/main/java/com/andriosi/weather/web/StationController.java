package com.andriosi.weather.web;

import com.andriosi.weather.service.StationService;
import com.andriosi.weather.web.dto.StationRequest;
import com.andriosi.weather.web.dto.StationResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PostMapping
    public StationResponse create(@Valid @RequestBody StationRequest request) {
        return stationService.create(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','READER')")
    @GetMapping
    public List<StationResponse> list() {
        return stationService.list();
    }
}
