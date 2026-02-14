package com.andriosi.weather.web;

import com.andriosi.weather.domain.RoleName;
import com.andriosi.weather.domain.SensorType;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lookups")
public class LookupController {

    @GetMapping("/roles")
    public List<String> listRoles() {
        return Arrays.stream(RoleName.values())
            .map(Enum::name)
            .toList();
    }

    @GetMapping("/sensor-types")
    public List<String> listSensorTypes() {
        return Arrays.stream(SensorType.values())
            .map(Enum::name)
            .toList();
    }

}
