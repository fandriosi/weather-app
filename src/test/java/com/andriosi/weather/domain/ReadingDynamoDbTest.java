package com.andriosi.weather.domain;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class ReadingDynamoDbTest {

    @Test
    void fromDynamoDbStreamJson_parsesFields() throws Exception {
        String json = """
        {
          "dynamodb": {
            "NewImage": {
              "id": { "S": "123e4567-e89b-12d3-a456-426614174000" },
              "value": { "N": "42.1" },
              "unit": { "S": "C" },
              "observedAt": { "S": "2024-02-26T12:00:00Z" },
              "createdAt": { "S": "2024-02-26T12:01:00Z" }
            }
          }
        }
        """;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        Reading reading = Reading.fromDynamoDbStreamJson(node);
        assertEquals("123e4567-e89b-12d3-a456-426614174000", reading.getId().toString());
        assertEquals(42.1, reading.getValue());
        assertEquals("C", reading.getUnit());
        assertEquals(Instant.parse("2024-02-26T12:00:00Z"), reading.getObservedAt());
        assertEquals(Instant.parse("2024-02-26T12:01:00Z"), reading.getCreatedAt());
    }
}
