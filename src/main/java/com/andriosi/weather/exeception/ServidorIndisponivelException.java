package com.andriosi.weather.exeception;

public class ServidorIndisponivelException extends WeatherException {

    public ServidorIndisponivelException(String message) {
        super(message, 503);
    }   
}
