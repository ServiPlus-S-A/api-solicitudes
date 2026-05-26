CREATE TABLE solicitudes (
    id              UUID PRIMARY KEY,
    titulo          VARCHAR(255) NOT NULL,
    descripcion     VARCHAR(2000) NOT NULL,
    solicitante_id  VARCHAR(255) NOT NULL,
    estado          VARCHAR(50) NOT NULL,
    creado_en       TIMESTAMPTZ NOT NULL,
    actualizado_en  TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_solicitudes_estado ON solicitudes (estado);
CREATE INDEX idx_solicitudes_creado_en ON solicitudes (creado_en DESC);
