package com.andriosi.weather.service;

import com.andriosi.weather.dto.Mensurement;
import com.andriosi.weather.web.dto.ReadingResponse;
import com.andriosi.weather.web.dto.StationResponse;
import com.andriosi.weather.web.dto.UnidadeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DynamoDbReadingService {

    @Value("${app.dynamodb.table-name}")
    private String tableName;

    private final DynamoDbClient dynamoDbClient;
    private final UnidadeService unidadeService;
    private final StationService stationService;
    private final Map<String, StationResponse> stationCache = new HashMap<>();
    private final Map<String, String> unidadeCache = new HashMap<>();

    public DynamoDbReadingService(DynamoDbClient dynamoDbClient,
            UnidadeService unidadeService, StationService stationService) {
        this.dynamoDbClient = dynamoDbClient;
        this.unidadeService = unidadeService;
        this.stationService = stationService;
    }

    public List<ReadingResponse> getMeteorologicalDataByStation() {
        QueryRequest query = QueryRequest.builder()
                .tableName(tableName)
                .build();
        QueryResponse response = dynamoDbClient.query(query);

        Map<String, ReadingResponse> stationMap = new HashMap<>();

        for (Map<String, AttributeValue> item : response.items()) {
            String stationId = item.get("idEstacao").s();
            StationResponse station = resolveStation(stationId);
            long timestamp = Long.parseLong(item.get("timestamp").n());
            LocalDateTime data = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
            if (station == null) {
                continue;
            }

            ReadingResponse stationData = stationMap.computeIfAbsent(stationId, id
                    -> new ReadingResponse(
                            station.name(),
                            station.latitude(),
                            station.longitude(),
                            data,
                            new ArrayList<>())
            );

            // Percorre todos os parâmetros, exceto idEstacao e timestamp
            for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
                String key = entry.getKey();
                if (key.equals("idEstacao") || key.equals("timestamp")) {
                    continue;
                }
                String nomeParametro = resolveUnidade(key);
                if (nomeParametro != null && entry.getValue().n() != null) {
                    Double valor = Double.valueOf(entry.getValue().n());
                    stationData.mensurements().add(new Mensurement(nomeParametro, valor));
                }else if (nomeParametro == null && entry.getValue().s() != null) {
                    // Caso o valor seja uma string, tentamos converter para double
                    Double valor = Double.valueOf(entry.getValue().n());
                    stationData.mensurements().add(new Mensurement(key, valor));
                }
            }
        }
        return new ArrayList<>(stationMap.values());

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
