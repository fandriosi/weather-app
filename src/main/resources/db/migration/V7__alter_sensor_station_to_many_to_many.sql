-- Criar tabela de junção sensor_stations
CREATE TABLE sensor_stations (
    sensor_id UUID NOT NULL,
    station_id UUID NOT NULL,
    PRIMARY KEY (sensor_id, station_id),
    CONSTRAINT fk_sensor_stations_sensor FOREIGN KEY (sensor_id) REFERENCES sensors(id) ON DELETE CASCADE,
    CONSTRAINT fk_sensor_stations_station FOREIGN KEY (station_id) REFERENCES stations(id) ON DELETE CASCADE
);

-- Migrar dados existentes da tabela sensors para a nova tabela de junção
INSERT INTO sensor_stations (sensor_id, station_id)
SELECT id, station_id FROM sensors WHERE station_id IS NOT NULL;

-- Criar índices para melhor performance
CREATE INDEX idx_sensor_stations_sensor_id ON sensor_stations(sensor_id);
CREATE INDEX idx_sensor_stations_station_id ON sensor_stations(station_id);

-- Remover a coluna station_id da tabela sensors
ALTER TABLE sensors DROP CONSTRAINT fk_sensors_station;
ALTER TABLE sensors DROP COLUMN station_id;
