CREATE TABLE sensor_unidades (
    sensor_id UUID NOT NULL,
    unidade_id UUID NOT NULL,
    PRIMARY KEY (sensor_id, unidade_id),
    CONSTRAINT fk_sensor_unidades_sensor FOREIGN KEY (sensor_id) REFERENCES sensors(id),
    CONSTRAINT fk_sensor_unidades_unidade FOREIGN KEY (unidade_id) REFERENCES unidades(id)
);

CREATE INDEX idx_sensor_unidades_sensor_id ON sensor_unidades(sensor_id);
CREATE INDEX idx_sensor_unidades_unidade_id ON sensor_unidades(unidade_id);
