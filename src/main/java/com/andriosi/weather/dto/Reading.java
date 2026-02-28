package com.andriosi.weather.dto;

import java.util.Map;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Reading {

    private String stationId;

    private Long timestamp;

    private Map<String, Double> mensurements;

    public Reading() {
    }

    public Reading(String stationId, Long timestamp, Map<String, Double> mensurements) {
        this.stationId = stationId;
        this.timestamp = timestamp;
        this.mensurements = mensurements;
    }

    @DynamoDbPartitionKey
    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    @DynamoDbSortKey
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Double> getMensurements() {
        return mensurements;
    }

    public void setMensurements(Map<String, Double> mensurements) {
        this.mensurements = mensurements;
    }
}
