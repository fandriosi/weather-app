package com.andriosi.weather.service;

import com.andriosi.weather.dto.Reading;
import com.andriosi.weather.web.dto.ReadingResponse;
import com.andriosi.weather.web.dto.StationResponse;
import com.andriosi.weather.web.dto.UnidadeResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import software.amazon.awssdk.enhanced.dynamodb.Key;

@Service
public class DynamoDbReadingService {

    @Value("${app.dynamodb.table-name}")
    private String tableName;

    private final DynamoDbEnhancedClient dynamoDbClient;
    private final UnidadeService unidadeService;
    private final StationService stationService;
    private final Map<String, StationResponse> stationCache = new HashMap<>();
    private final Map<String, String> unidadeCache = new HashMap<>();

    public DynamoDbReadingService(DynamoDbEnhancedClient dynamoDbClient,
            UnidadeService unidadeService, StationService stationService) {
        this.dynamoDbClient = dynamoDbClient;
        this.unidadeService = unidadeService;
        this.stationService = stationService;
    }

    public List<ReadingResponse> getMeteorologicalDataByStation(UUID stationId) {
        String stationIdStr = stationId.toString();
        List<ReadingResponse> responses = new ArrayList<>();
        DynamoDbTable<Reading> table = dynamoDbClient.table(tableName, TableSchema.fromBean(Reading.class));
        // Query pela partition key
        QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(stationIdStr).build());
        PageIterable<Reading> results = table.query(query);

        populateReadingData(responses, stationIdStr, results);
        return responses;
    }

    public List<ReadingResponse> getAllMeteorologicalData() {
        List<ReadingResponse> allResponses = new ArrayList<>();
        List<String> stationIds = stationService.getAllIds();
        DynamoDbTable<Reading> table = dynamoDbClient.table(tableName, TableSchema.fromBean(Reading.class));
        for (String id : stationIds) {
            QueryConditional query = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(id).build());

            PageIterable<Reading> results = table.query(r -> r
                    .queryConditional(query)
                    .scanIndexForward(false) // Mais recente primeiro
                    .limit(1)
            );

            populateReadingData(allResponses, id, results);
        }
        return allResponses;
    }

    private void populateReadingData(List<ReadingResponse> allResponses, String id, PageIterable<Reading> results) {
        for (Reading entity : results.items()) {
            StationResponse station = resolveStation(id);
            LocalDateTime data = LocalDateTime.ofEpochSecond(entity.getTimestamp(), 0, ZoneOffset.UTC);
            if (station == null) {
                continue;
            }

            Map<String, Double> mensurements = new HashMap<>();
            for (Map.Entry<String, Double> entry : entity.getMensurements().entrySet()) {
                String unidade = resolveUnidade(entry.getKey());
                if (unidade != null) {
                    mensurements.put(unidade, entry.getValue());
                } else {
                    mensurements.put(entry.getKey(), entry.getValue());
                }
            }
            ReadingResponse response = new ReadingResponse(
                    station.name(),
                    station.latitude(),
                    station.longitude(),
                    data,
                    mensurements
            );
            allResponses.add(response);
        }
    }

    private String resolveUnidade(String parametro) {
        if (unidadeCache.containsKey(parametro)) {
            return unidadeCache.get(parametro);
        }
        UnidadeResponse unidade = unidadeService.findByParametro(parametro);
        if (unidade != null) {
            unidadeCache.put(parametro, unidade.nome());
        }
        return unidade != null ? unidade.nome() : null;
    }

    //Método auxiliar para resolver stationId para stationName, latitude e longitude
    private StationResponse resolveStation(String stationId) {
        if (stationCache.containsKey(stationId)) {
            return stationCache.get(stationId);
        }
        try {
            StationResponse station = stationService.getById(UUID.fromString(stationId)).orElse(null);
            if (station != null) {
                stationCache.put(stationId, station);
            }
            return station;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
