CREATE TABLE asignaciones (
    id_asignacion BIGSERIAL PRIMARY KEY,
    id_solicitud BIGINT NOT NULL,
    id_consultor BIGINT NOT NULL,
    fecha_asignacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_solicitud_asignacion FOREIGN KEY (id_solicitud) REFERENCES solicitudes(id_solicitud) ON DELETE CASCADE
);

CREATE TABLE adjuntos (
    id_adjunto BIGSERIAL PRIMARY KEY,
    id_solicitud BIGINT NOT NULL,
    url_archivo VARCHAR(255) NOT NULL,
    tipo_archivo VARCHAR(10),
    CONSTRAINT fk_solicitud_adjunto FOREIGN KEY (id_solicitud) REFERENCES solicitudes(id_solicitud) ON DELETE CASCADE
);