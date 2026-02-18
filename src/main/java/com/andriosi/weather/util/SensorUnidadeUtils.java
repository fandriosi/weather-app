package com.andriosi.weather.util;

import com.andriosi.weather.domain.Sensor;
import com.andriosi.weather.domain.Unidade;
import java.util.List;

public class SensorUnidadeUtils {

    public static void syncSensorUnidades(Sensor sensor, List<Unidade> unidades) {
        sensor.setUnidades(unidades);
        for (Unidade unidade : unidades) {
            if (!unidade.getSensors().contains(sensor)) {
                unidade.getSensors().add(sensor);
            }
        }
    }
}
