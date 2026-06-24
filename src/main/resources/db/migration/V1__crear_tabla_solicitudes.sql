CREATE TABLE solicitudes (
    id_solicitud BIGSERIAL PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    id_tipo_servicio BIGINT NOT NULL,
    asunto VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
    fecha_apertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    codigo_trazabilidad VARCHAR(20) UNIQUE,
    ubicacion VARCHAR(100) NOT NULL
);
