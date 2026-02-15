package com.andriosi.weather.exeception;

public class ResourceNotFoundException extends WeatherException {

    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}

