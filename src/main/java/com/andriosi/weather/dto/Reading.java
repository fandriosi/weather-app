package com.andriosi.weather.dto;

public record Reading(String stationName,
                      double latitude,
                      double longitude,
                      java.time.LocalDateTime dataHora,
                      java.util.List<Mensurement> mensurements) {

}
