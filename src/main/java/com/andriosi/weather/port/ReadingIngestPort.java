package com.andriosi.weather.port;

import org.springframework.stereotype.Service;

import com.andriosi.weather.web.dto.ReadingIngestRequest;
import com.andriosi.weather.web.dto.ReadingResponse;
@Service
public interface ReadingIngestPort {
    ReadingResponse ingest(ReadingIngestRequest request);
}
