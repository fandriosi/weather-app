package com.andriosi.weather.service;

import com.andriosi.weather.dto.Reading;
import com.andriosi.weather.web.dto.ReadingResponse;
import com.andriosi.weather.web.dto.StationResponse;
import com.andriosi.weather.web.dto.UnidadeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DynamoDbReadingServiceAllTest {

    private DynamoDbEnhancedClient dynamoDbClient;
    private UnidadeService unidadeService;
    private StationService stationService;
    private DynamoDbReadingService service;
    private DynamoDbTable<Reading> table;

    @BeforeEach
    void setUp() throws Exception {
        dynamoDbClient = mock(DynamoDbEnhancedClient.class);
        unidadeService = mock(UnidadeService.class);
        stationService = mock(StationService.class);
        table = mock(DynamoDbTable.class);
        when(dynamoDbClient.table(anyString(), any(TableSchema.class))).thenReturn(table);
        service = new DynamoDbReadingService(dynamoDbClient, unidadeService, stationService);
        java.lang.reflect.Field field = DynamoDbReadingService.class.getDeclaredField("tableName");
        field.setAccessible(true);
        field.set(service, "dummy-table");
    }

    @Test
    void testGetMeteorologicalDataAll() {
        UUID stationId = UUID.randomUUID();
        UUID stationId2 = UUID.randomUUID();
        Map<String, Double> mensurements = new HashMap<>();
        mensurements.put("temp", 25.0);
        mensurements.put("humidity", 60.0);
        mensurements.put("pressure", 1013.0);
        Reading reading = mock(Reading.class);
        when(reading.getTimestamp()).thenReturn(1709050000L);
        when(reading.getMensurements()).thenReturn(mensurements);

        Map<String, Double> mensurements2 = new HashMap<>();
        mensurements2.put("temp", 18.0);
        mensurements2.put("humidity", 56.0);
        mensurements2.put("pressure", 100.0);
        Reading reading2 = mock(Reading.class);
        when(reading2.getTimestamp()).thenReturn(1709058000L);
        when(reading2.getMensurements()).thenReturn(mensurements2);

        Page<Reading> page = mock(Page.class);
        when(page.items()).thenReturn(Collections.singletonList(reading));
        when(page.items()).thenReturn(Collections.singletonList(reading2));
        
        Iterable<Page<Reading>> sdkIterable = Collections.singletonList(page);
        PageIterable<Reading> pageIterable = PageIterable.create(() -> sdkIterable.iterator());
        when(table.query(any(QueryConditional.class))).thenReturn(pageIterable);

        StationResponse stationResponse = new StationResponse(
                stationId, "StationName-1",
                1.0,
                2.0,
                null);
        StationResponse stationResponse1 = new StationResponse(
                stationId2, "StationName-2",
                3.0,
                4.0,
                null);

        when(stationService.getById(any(UUID.class))).thenReturn(
                Optional.of(Arrays.asList(stationResponse, stationResponse1).get(0)));

        when(stationService.getAllIds()).thenReturn(Collections.singletonList(stationId.toString()));

        UnidadeResponse unidadeResponse = new UnidadeResponse(UUID.randomUUID(), "Temperatura", "Celsius");
        UnidadeResponse unidadeResponse1 = new UnidadeResponse(UUID.randomUUID(), "Umidade", "Percentual");

        when(unidadeService.findByParametro("temp")).thenReturn(unidadeResponse);
        when(unidadeService.findByParametro("humidity")).thenReturn(unidadeResponse1);
        when(unidadeService.findByParametro("pressure")).thenReturn(null);
        List<ReadingResponse> responses = service.getAllMeteorologicalData();
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
