/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.andriosi.weather.exeception;

class ForbiddenException extends WeatherException {

    public ForbiddenException(String message) {
        super(message, 403);
    }

}
