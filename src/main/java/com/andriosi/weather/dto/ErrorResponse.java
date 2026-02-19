package com.andriosi.weather.dto;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String message;
    private String details;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, String details, int status, LocalDateTime timestamp) {
        this.message = message;
        this.details = details;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
