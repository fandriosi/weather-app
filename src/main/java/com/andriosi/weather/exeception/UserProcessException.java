package com.andriosi.weather.exeception;

public class UserProcessException extends WeatherException {

    public UserProcessException(String message) {
        super(message, 400);
    }

}
