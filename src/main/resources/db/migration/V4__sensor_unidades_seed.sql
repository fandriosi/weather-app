INSERT INTO sensor_unidades (sensor_id, unidade_id)
SELECT s.id, u.id
FROM sensors s
JOIN unidades u ON u.parametro = 'temperatura_do_ar'
WHERE s.type = 'TEMPERATURE'
ON CONFLICT DO NOTHING;

INSERT INTO sensor_unidades (sensor_id, unidade_id)
SELECT s.id, u.id
FROM sensors s
JOIN unidades u ON u.parametro = 'umidade_relativa_do_ar'
WHERE s.type = 'HUMIDITY'
ON CONFLICT DO NOTHING;

INSERT INTO sensor_unidades (sensor_id, unidade_id)
SELECT s.id, u.id
FROM sensors s
JOIN unidades u ON u.parametro = 'pressao_atmosferica_hpa'
WHERE s.type = 'PRESSURE'
ON CONFLICT DO NOTHING;

INSERT INTO sensor_unidades (sensor_id, unidade_id)
SELECT s.id, u.id
FROM sensors s
JOIN unidades u ON u.parametro = 'velocidade_do_vento_em_m_s'
WHERE s.type = 'WIND_SPEED'
ON CONFLICT DO NOTHING;

INSERT INTO sensor_unidades (sensor_id, unidade_id)
SELECT s.id, u.id
FROM sensors s
JOIN unidades u ON u.parametro = 'direcao_do_vento'
WHERE s.type = 'WIND_DIRECTION'
ON CONFLICT DO NOTHING;

INSERT INTO sensor_unidades (sensor_id, unidade_id)
SELECT s.id, u.id
FROM sensors s
JOIN unidades u ON u.parametro = 'precipitacao_acumulada'
WHERE s.type = 'RAINFALL'
ON CONFLICT DO NOTHING;
