CREATE TABLE archivos_adjuntos (
    id              UUID PRIMARY KEY,
    solicitud_id    UUID NOT NULL REFERENCES solicitudes (id) ON DELETE CASCADE,
    nombre_archivo  VARCHAR(500) NOT NULL,
    content_type    VARCHAR(255) NOT NULL,
    storage_key     VARCHAR(1000) NOT NULL,
    subido_en       TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_archivos_solicitud_id ON archivos_adjuntos (solicitud_id);
