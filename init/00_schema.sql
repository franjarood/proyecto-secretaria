-- Schema SQL para secretaria (MariaDB)
--
-- Objetivo: crear el esquema mínimo necesario ANTES de los seeds.
-- Se ejecuta por Docker (docker-entrypoint-initdb.d) al crear la BD desde cero.
--
-- Importante:
-- - DDL derivado de las entidades JPA actuales (sin inventar tablas/campos).
-- - Enums se guardan como STRING => VARCHAR.
-- - Nombres en snake_case según estrategia típica de Hibernate/Spring.
--
-- NOTA: Si en tu entorno Hibernate usa otra estrategia de nombres, ajusta este schema.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- TABLA: usuarios
-- =========================
CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(255) NOT NULL,
  apellidos VARCHAR(255) NOT NULL,
  dni VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  telefono VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  rol VARCHAR(255) NOT NULL,
  creado_en DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- =========================
-- TABLA: tipos_tramite
-- =========================
CREATE TABLE IF NOT EXISTS tipos_tramite (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(255) NOT NULL,
  descripcion VARCHAR(255) NOT NULL,
  duracion_estimada INT NOT NULL,
  requiere_documentacion BIT(1) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- =========================
-- TABLA: reservas_turno
-- =========================
CREATE TABLE IF NOT EXISTS reservas_turno (
  id BIGINT NOT NULL AUTO_INCREMENT,
  fecha_cita DATE NOT NULL,
  hora_cita TIME NOT NULL,
  codigo_referencia VARCHAR(255) NOT NULL,
  origen_turno VARCHAR(255) NOT NULL,
  estado_reserva VARCHAR(255) DEFAULT NULL,
  usuario_id BIGINT DEFAULT NULL,
  created_at DATETIME(6) DEFAULT NULL,
  updated_at DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB;

-- =========================
-- TABLA INTERMEDIA: reserva_tipo_tramite
-- =========================
CREATE TABLE IF NOT EXISTS reserva_tipo_tramite (
  reserva_id BIGINT NOT NULL,
  tipo_tramite_id BIGINT NOT NULL,
  PRIMARY KEY (reserva_id, tipo_tramite_id),
  CONSTRAINT fk_reserva_tt_reserva FOREIGN KEY (reserva_id) REFERENCES reservas_turno (id),
  CONSTRAINT fk_reserva_tt_tipo FOREIGN KEY (tipo_tramite_id) REFERENCES tipos_tramite (id)
) ENGINE=InnoDB;

-- =========================
-- TABLA: turnos
-- =========================
CREATE TABLE IF NOT EXISTS turnos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  numero_turno VARCHAR(255) NOT NULL,
  fecha_cita DATE NOT NULL,
  hora_cita TIME NOT NULL,
  hora_llegada TIME DEFAULT NULL,
  estado_turno VARCHAR(255) DEFAULT NULL,
  prioridad INT DEFAULT NULL,
  tipo_prioridad VARCHAR(255) DEFAULT NULL,
  origen_turno VARCHAR(255) NOT NULL,
  observaciones VARCHAR(255) DEFAULT NULL,
  duracion_estimada INT DEFAULT NULL,
  reingreso BIT(1) DEFAULT NULL,
  incidencia BIT(1) DEFAULT NULL,
  prioridad_manual BIT(1) DEFAULT NULL,
  motivo_prioridad VARCHAR(255) DEFAULT NULL,
  reserva_turno_id BIGINT DEFAULT NULL,
  created_at DATETIME(6) DEFAULT NULL,
  updated_at DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_turno_reserva FOREIGN KEY (reserva_turno_id) REFERENCES reservas_turno (id)
) ENGINE=InnoDB;

-- =========================
-- TABLA INTERMEDIA: turno_tipo_tramite
-- =========================
CREATE TABLE IF NOT EXISTS turno_tipo_tramite (
  turno_id BIGINT NOT NULL,
  tipo_tramite_id BIGINT NOT NULL,
  PRIMARY KEY (turno_id, tipo_tramite_id),
  CONSTRAINT fk_turno_tt_turno FOREIGN KEY (turno_id) REFERENCES turnos (id),
  CONSTRAINT fk_turno_tt_tipo FOREIGN KEY (tipo_tramite_id) REFERENCES tipos_tramite (id)
) ENGINE=InnoDB;

-- =========================
-- TABLA: pre_matriculas
-- =========================
CREATE TABLE IF NOT EXISTS pre_matriculas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  fecha_creacion DATETIME(6) DEFAULT NULL,
  estado VARCHAR(255) DEFAULT NULL,
  nombre_alumno VARCHAR(255) NOT NULL,
  apellidos_alumno VARCHAR(255) NOT NULL,
  dni_alumno VARCHAR(255) NOT NULL,
  email_alumno VARCHAR(255) NOT NULL,
  telefono_alumno VARCHAR(255) NOT NULL,
  ciclo_solicitado VARCHAR(255) NOT NULL,
  curso_solicitado VARCHAR(255) NOT NULL,
  modalidad VARCHAR(255) NOT NULL,
  observaciones VARCHAR(255) DEFAULT NULL,
  usuario_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_prematricula_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB;

-- =========================
-- TABLA: documentos
-- =========================
CREATE TABLE IF NOT EXISTS documentos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre_archivo VARCHAR(255) NOT NULL,
  tipo_documento VARCHAR(255) NOT NULL,
  ruta_archivo VARCHAR(255) NOT NULL,
  estado_revision VARCHAR(255) DEFAULT NULL,
  comentario_revision VARCHAR(255) DEFAULT NULL,
  fecha_subida DATETIME(6) DEFAULT NULL,
  fecha_revision DATETIME(6) DEFAULT NULL,
  usuario_id BIGINT DEFAULT NULL,
  subido_por_id BIGINT DEFAULT NULL,
  revisado_por_id BIGINT DEFAULT NULL,
  prematricula_id BIGINT DEFAULT NULL,
  turno_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_documento_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
  CONSTRAINT fk_documento_subido_por FOREIGN KEY (subido_por_id) REFERENCES usuarios (id),
  CONSTRAINT fk_documento_revisado_por FOREIGN KEY (revisado_por_id) REFERENCES usuarios (id),
  CONSTRAINT fk_documento_prematricula FOREIGN KEY (prematricula_id) REFERENCES pre_matriculas (id),
  CONSTRAINT fk_documento_turno FOREIGN KEY (turno_id) REFERENCES turnos (id)
) ENGINE=InnoDB;

-- =========================
-- TABLA: incidencias
-- =========================
CREATE TABLE IF NOT EXISTS incidencias (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tipo VARCHAR(255) NOT NULL,
  descripcion VARCHAR(500) NOT NULL,
  fecha DATETIME(6) DEFAULT NULL,
  resuelta BIT(1) NOT NULL,
  accion_tomada VARCHAR(255) DEFAULT NULL,
  turno_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_incidencia_turno FOREIGN KEY (turno_id) REFERENCES turnos (id)
) ENGINE=InnoDB;

-- =========================
-- TABLA: notificaciones
-- =========================
CREATE TABLE IF NOT EXISTS notificaciones (
  id BIGINT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(255) NOT NULL,
  mensaje VARCHAR(500) NOT NULL,
  tipo VARCHAR(255) NOT NULL,
  leida BIT(1) DEFAULT NULL,
  creada_en DATETIME(6) DEFAULT NULL,
  referencia VARCHAR(255) DEFAULT NULL,
  url_destino VARCHAR(255) DEFAULT NULL,
  enviada_por_email BIT(1) DEFAULT NULL,
  fecha_envio_email DATETIME(6) DEFAULT NULL,
  usuario_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_notificacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB;

-- =========================
-- TABLA: historial_acciones
-- =========================
CREATE TABLE IF NOT EXISTS historial_acciones (
  id BIGINT NOT NULL AUTO_INCREMENT,
  fecha_hora DATETIME(6) DEFAULT NULL,
  accion VARCHAR(255) DEFAULT NULL,
  descripcion VARCHAR(500) DEFAULT NULL,
  entidad_afectada VARCHAR(255) DEFAULT NULL,
  id_entidad BIGINT DEFAULT NULL,
  usuario_responsable BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

