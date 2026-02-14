package com.andriosi.weather.exeception;

public class SensorFileNotFoundException extends RuntimeException {
    public SensorFileNotFoundException(String message) {
        super(message);
    }
}
