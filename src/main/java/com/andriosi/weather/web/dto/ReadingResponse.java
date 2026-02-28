package com.andriosi.weather.web.dto;
import java.time.LocalDateTime;
import java.util.Map;

public record ReadingResponse(
        String stationName,
        Double latitude,
        Double longitude,
        LocalDateTime dataHora,
        Map<String, Double> mensurements){

}
