package com.andriosi.weather.exeception;

public class WeatherException extends RuntimeException {
    private final int statusCode;

    public WeatherException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}