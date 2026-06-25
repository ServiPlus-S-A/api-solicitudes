UPDATE solicitudes SET estado = 'PENDIENTE' WHERE estado ILIKE 'pendiente';
ALTER TABLE solicitudes ALTER COLUMN estado SET DEFAULT 'PENDIENTE';