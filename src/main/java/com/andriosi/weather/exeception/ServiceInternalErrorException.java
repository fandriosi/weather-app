package com.andriosi.weather.exeception;

public class ServiceInternalErrorException extends WeatherException {

    public ServiceInternalErrorException(String message) {
        super(message, 500);
    }

}
