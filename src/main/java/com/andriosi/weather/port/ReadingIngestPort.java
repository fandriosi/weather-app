package com.andriosi.weather.port;

import com.andriosi.weather.web.dto.ReadingIngestRequest;
import com.andriosi.weather.web.dto.ReadingResponse;

public interface ReadingIngestPort {
    ReadingResponse ingest(ReadingIngestRequest request);
}
