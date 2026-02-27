package com.andriosi.weather.domain;

import com.andriosi.weather.domain.Sensor;
import com.andriosi.weather.domain.SensorTypeEntity;
import com.andriosi.weather.domain.Unidade;
import com.andriosi.weather.web.dto.SensorStatus;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SensorTest {

    //Criar testes unitários para a classe Sensor, verificando a criação de instâncias, a associação com SensorTypeEntity e Unidade, e a manipulação dos campos de status e timestamps. 
    @Test
    public void testSensorCreation() {
        // Implementar teste para criação de Sensor
        Sensor sensor = new Sensor();
        sensor.setName("Sensor de Temperatura");
        sensor.setDescription("Sensor para medir a temperatura ambiente");
        sensor.setStatus(SensorStatus.ACTIVE);
        assertEquals("Sensor de Temperatura", sensor.getName());
        assertEquals("Sensor para medir a temperatura ambiente", sensor.getDescription());
        assertEquals(SensorStatus.ACTIVE, sensor.getStatus());
    }
    // Implementar teste para associar arquivos SensorTypeEntity e Unidade
    @Test
    public void testSensorAssociations() {
        Sensor sensor = new Sensor();
        SensorTypeEntity sensorType = new SensorTypeEntity();
        sensorType.setName("Temperatura");
        sensor.setType(sensorType);
       
        Unidade unidade = new Unidade();
        unidade.setNome("Celsius");
        UUID unitId = UUID.randomUUID();
        unidade.setId(unitId);  
        unidade.setSimbolo("°C");
        unidade.setParametro("temperatura_do_ar");
        sensor.getUnidades().add(unidade);

        assertEquals("Temperatura", sensor.getType().getName());    
        assertEquals("Celsius", sensor.getUnidades().get(0).getNome());
        assertEquals("°C", sensor.getUnidades().get(0).getSimbolo());
        assertEquals("temperatura_do_ar", sensor.getUnidades().get(0).getParametro());
    }
}
