CREATE TABLE sensor_files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_id UUID NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    storage_type VARCHAR(16) NOT NULL,
    storage_key VARCHAR(512) NOT NULL,
    storage_url VARCHAR(512),
    created_at TIMESTAMP NOT NULL,
    is_image BOOLEAN NOT NULL,
    CONSTRAINT fk_sensor_files_sensor FOREIGN KEY (sensor_id) REFERENCES sensors(id)
);

CREATE INDEX idx_sensor_files_sensor_id ON sensor_files(sensor_id);
