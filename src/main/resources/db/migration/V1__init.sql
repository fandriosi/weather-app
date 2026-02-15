CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS sensor_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- pgcrypto
    -- id UUID PRIMARY KEY DEFAULT uuid_generate_v4(), -- uuid-ossp
    name VARCHAR(50) NOT NULL UNIQUE
);
INSERT INTO sensor_type (name) VALUES
    ('TEMPERATURE'),
    ('HUMIDITY'),
    ('PRESSURE'),
    ('WIND_SPEED'),
    ('WIND_DIRECTION'),
    ('RAINFALL'),
    ('MULTI_FUNCTION')
ON CONFLICT (name) DO NOTHING;

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE sensors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    sensor_type_id UUID NOT NULL,
    description TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sensors_sensor_type FOREIGN KEY (sensor_type_id) REFERENCES sensor_type(id)
);

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sensors_updated_at
BEFORE UPDATE ON sensors
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TABLE stations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stations_sensor FOREIGN KEY (sensor_id) REFERENCES sensors(id)
);

CREATE TRIGGER trg_stations_updated_at
BEFORE UPDATE ON stations
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TABLE readings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    station_id UUID NOT NULL,
    reading_value DOUBLE PRECISION NOT NULL,
    unit VARCHAR(64) NOT NULL,
    observed_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_readings_station FOREIGN KEY (station_id) REFERENCES stations(id)
);

CREATE TRIGGER trg_readings_updated_at
BEFORE UPDATE ON readings
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TABLE unidades (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL
);

CREATE INDEX idx_readings_station_id ON readings(station_id);
CREATE INDEX idx_readings_observed_at ON readings(observed_at);
