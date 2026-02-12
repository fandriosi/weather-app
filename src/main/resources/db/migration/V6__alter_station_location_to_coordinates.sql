-- Remover a coluna location e adicionar latitude e longitude
ALTER TABLE stations DROP COLUMN location;

ALTER TABLE stations ADD COLUMN latitude DOUBLE PRECISION NOT NULL DEFAULT 0.0;
ALTER TABLE stations ADD COLUMN longitude DOUBLE PRECISION NOT NULL DEFAULT 0.0;

-- Remover os valores padrão após adicionar as colunas
ALTER TABLE stations ALTER COLUMN latitude DROP DEFAULT;
ALTER TABLE stations ALTER COLUMN longitude DROP DEFAULT;
