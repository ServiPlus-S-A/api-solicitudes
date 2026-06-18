CREATE TABLE solicitudes (
    id_solicitud SERIAL PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_tipo_servicio INT NOT NULL,
    asunto VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
    fecha_apertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    codigo_trazabilidad VARCHAR(20) UNIQUE,
    url_adjunto VARCHAR(255)
);
