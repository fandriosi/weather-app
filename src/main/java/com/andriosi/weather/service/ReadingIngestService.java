package com.andriosi.weather.service;

import com.andriosi.weather.port.ReadingIngestPort;
import com.andriosi.weather.web.dto.ReadingIngestRequest;
import com.andriosi.weather.web.dto.ReadingResponse;
import org.springframework.stereotype.Service;

@Service
public class ReadingIngestService implements ReadingIngestPort {

    @Override
    public ReadingResponse ingest(ReadingIngestRequest request) {
        // Preenche campos disponíveis, os demais ficam null ou padrão
        return new ReadingResponse(
                null, // stationName não disponível no request
                null, // latitude não disponível no request
                null,
                null,
                null
        );
    }
}
