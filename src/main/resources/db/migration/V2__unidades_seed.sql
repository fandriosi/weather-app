ALTER TABLE unidades ADD COLUMN simbolo VARCHAR(64);
ALTER TABLE unidades ADD COLUMN parametro VARCHAR(255);

UPDATE unidades SET simbolo = '' WHERE simbolo IS NULL;
UPDATE unidades
SET parametro = regexp_replace(lower(nome || ' ' || simbolo), '[^a-z0-9]+', '_', 'g')
WHERE parametro IS NULL;
UPDATE unidades
SET parametro = regexp_replace(parametro, '^_+|_+$', '', 'g')
WHERE parametro IS NOT NULL;

ALTER TABLE unidades ALTER COLUMN simbolo SET NOT NULL;
ALTER TABLE unidades ALTER COLUMN parametro SET NOT NULL;

INSERT INTO unidades (nome, simbolo, parametro) VALUES
    ('Temperatura do ar', '°C', 'temperatura_do_ar'),
    ('Temperatura do solo', '°C', 'temperatura_do_solo'),
    ('Umidade Relativa do Ar', '%', 'umidade_relativa_do_ar'),
    ('Ponto de Orvalho', '°C', 'ponto_de_orvalho'),
    ('Velocidade do Vento em m/s', 'm/s', 'velocidade_do_vento_em_m_s'),
    ('Velocidade do Vento em km/h', 'km/h', 'velocidade_do_vento_em_km_h'),
    ('Direção do Vento', 'graus (°)', 'direcao_do_vento'),
    ('Precipitação - Acumulada', 'mm', 'precipitacao_acumulada'),
    ('Precipitação - Intensidade', 'mm/h', 'precipitacao_intensidade'),
    ('Radiação Global (Irradiancia Global)', 'W/m²', 'radiacao_global_irradiancia_global'),
    ('Radiação Difusa (Irradiancia Difusa)', 'W/m²', 'radiacao_difusa_irradiancia_difusa'),
    ('Radiação Direta (Irradiancia Direta)', 'W/m²', 'radiacao_direta_irradiancia_direta'),
    ('Irradiação Acumulada', 'J/m²', 'irradiacao_acumulada_j_m2'),
    ('Irradiação Acumulada', 'kWh/m²', 'irradiacao_acumulada_kwh_m2'),
    ('Pressão Atmosférica', 'hPa', 'pressao_atmosferica_hpa'),
    ('Pressão Atmosférica', 'mbar', 'pressao_atmosferica_mbar'),
    ('Umidade do Solo', '%', 'umidade_do_solo'),
    ('Fluxo de calor do Solo', 'W/m²', 'fluxo_de_calor_do_solo'),
    ('Evapotranspiração', 'mm', 'evapotranspiracao'),
    ('Visibilidade', 'm', 'visibilidade'),
    ('Particulas em Suspensão (PM2.5/PM10)', 'µg/m³', 'particulas_em_suspensao_pm2_5_pm10'),
    ('Tensão da bateria', 'V', 'tensao_da_bateria');
