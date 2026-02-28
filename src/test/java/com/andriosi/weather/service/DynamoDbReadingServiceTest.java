package com.andriosi.weather.service;

import com.andriosi.weather.dto.Reading;
import com.andriosi.weather.web.dto.ReadingResponse;
import com.andriosi.weather.web.dto.StationResponse;
import com.andriosi.weather.web.dto.UnidadeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DynamoDbReadingServiceTest {

    private DynamoDbEnhancedClient dynamoDbClient;
    private UnidadeService unidadeService;
    private StationService stationService;
    private DynamoDbReadingService service;
    private DynamoDbTable<Reading> table;

    @BeforeEach
    void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        dynamoDbClient = mock(DynamoDbEnhancedClient.class);
        unidadeService = mock(UnidadeService.class);
        stationService = mock(StationService.class);
        table = mock(DynamoDbTable.class);
        // Always return the mock table for any table name/schema
        when(dynamoDbClient.table(anyString(), any(TableSchema.class))).thenReturn(table);
        service = new DynamoDbReadingService(dynamoDbClient, unidadeService, stationService);
        var tableNameField = DynamoDbReadingService.class.getDeclaredField("tableName");
        tableNameField.setAccessible(true);
        tableNameField.set(service, "mock-table");
    }

    @Test
    void testGetMeteorologicalDataByStation() {
        UUID stationId = UUID.randomUUID();
        Map<String, Double> mensurements = new HashMap<>();
        mensurements.put("temp", 25.0);
        mensurements.put("humidity", 60.0);
        mensurements.put("pressure", 1013.0);
        Reading reading = mock(Reading.class);
        when(reading.getTimestamp()).thenReturn(1709050000L);
        when(reading.getMensurements()).thenReturn(mensurements);

        Page<Reading> page = mock(Page.class);
        when(page.items()).thenReturn(Collections.singletonList(reading));
        Iterable<Page<Reading>> sdkIterable = Collections.singletonList(page);
        PageIterable<Reading> pageIterable = PageIterable.create(() -> sdkIterable.iterator());
        when(table.query(any(QueryConditional.class))).thenReturn(pageIterable);

        StationResponse stationResponse = new StationResponse(stationId, "StationName", 1.0, 2.0, null);
        when(stationService.getById(any(UUID.class))).thenReturn(Optional.of(stationResponse));

        UnidadeResponse unidadeResponse = new UnidadeResponse(UUID.randomUUID(), "Temperatura", "Celsius");
        UnidadeResponse unidadeResponse1 = new UnidadeResponse(UUID.randomUUID(), "Umidade", "Percentual");

        when(unidadeService.findByParametro("temp")).thenReturn(unidadeResponse);
        when(unidadeService.findByParametro("humidity")).thenReturn(unidadeResponse1);
        when(unidadeService.findByParametro("pressure")).thenReturn(null);
        List<ReadingResponse> responses = service.getMeteorologicalDataByStation(stationId);
        assertEquals(1, responses.size());
        ReadingResponse resp = responses.get(0);
        assertEquals("StationName", resp.stationName());
        assertEquals(1.0, resp.latitude());
        assertEquals(2.0, resp.longitude());
        assertTrue(resp.mensurements().containsKey("Temperatura"));
        assertTrue(resp.mensurements().containsKey("Umidade"));
        assertTrue(resp.mensurements().containsKey("pressure"));
        assertEquals(60.0, resp.mensurements().get("Umidade"));
        assertEquals(1013.0, resp.mensurements().get("pressure"));
        assertEquals(25.0, resp.mensurements().get("Temperatura"));
        assertEquals(3, resp.mensurements().size());
        //vefica o valor correto para a data e hora
        assertEquals(LocalDateTime.ofEpochSecond(1709050000L, 0, ZoneOffset.UTC),
                LocalDateTime.ofEpochSecond(reading.getTimestamp(), 0, ZoneOffset.UTC));
    }
}
