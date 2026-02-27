package com.andriosi.weather.web.dto;
import java.time.LocalDateTime;
import java.util.List;

import com.andriosi.weather.dto.Mensurement;

public record ReadingResponse(
        String stationName,
        Double latitude,
        Double longitude,
        LocalDateTime dataHora,
        List<Mensurement> mensurements){

}
