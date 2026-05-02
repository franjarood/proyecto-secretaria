-- Datos iniciales de prueba para secretaria (MariaDB)
-- Se ejecuta automáticamente al crear la BD desde cero (docker-entrypoint-initdb.d)
-- Importante: este script asume que Hibernate crea las tablas (spring.jpa.hibernate.ddl-auto=update)
-- y que el contenedor ejecuta los scripts *después* de que existan.
--
-- Para evitar duplicados: si ya existen usuarios, no hace nada.

START TRANSACTION;

-- =========================
-- Guardas / Control
-- =========================

-- Si ya hay usuarios, no insertamos seed.
SET @usuarios_existentes := (SELECT COUNT(*) FROM usuarios);

-- =========================
-- Usuarios (password BCrypt para: abc123.)
-- =========================

-- Hash BCrypt válido (mismo para todos): abc123.
-- (Reutilizado del usuario admin@centro.local validado en la BD de pruebas)
SET @bcrypt_abc123 := '$2a$10$iImuWG1GE4YKEwInatKqoeZAXqnOAff.meYhZeELfOZ2lmISIlNCS';

-- Insertar solo si la tabla está vacía
INSERT INTO usuarios (nombre, apellidos, dni, email, telefono, password, rol, creado_en)
SELECT 'Admin', 'Centro', '00000000A', 'admin@centro.local', '600000001', @bcrypt_abc123, 'ADMIN', NOW()
WHERE @usuarios_existentes = 0;

INSERT INTO usuarios (nombre, apellidos, dni, email, telefono, password, rol, creado_en)
SELECT 'Secretaría', 'Centro', '00000001B', 'secretaria@centro.local', '600000002', @bcrypt_abc123, 'SECRETARIA', NOW()
WHERE @usuarios_existentes = 0;

INSERT INTO usuarios (nombre, apellidos, dni, email, telefono, password, rol, creado_en)
SELECT 'Profesor', 'Centro', '00000002C', 'profesor@centro.local', '600000003', @bcrypt_abc123, 'PROFESOR', NOW()
WHERE @usuarios_existentes = 0;

INSERT INTO usuarios (nombre, apellidos, dni, email, telefono, password, rol, creado_en)
SELECT 'Conserje', 'Centro', '00000003D', 'conserje@centro.local', '600000004', @bcrypt_abc123, 'CONSERJE', NOW()
WHERE @usuarios_existentes = 0;

INSERT INTO usuarios (nombre, apellidos, dni, email, telefono, password, rol, creado_en)
SELECT 'Alumno', 'Prueba', '00000004E', 'alumno@centro.local', '600000005', @bcrypt_abc123, 'ALUMNO', NOW()
WHERE @usuarios_existentes = 0;

-- Guardar IDs en variables (solo si hemos creado)
SET @id_admin := (SELECT id FROM usuarios WHERE email = 'admin@centro.local' LIMIT 1);
SET @id_secretaria := (SELECT id FROM usuarios WHERE email = 'secretaria@centro.local' LIMIT 1);
SET @id_profesor := (SELECT id FROM usuarios WHERE email = 'profesor@centro.local' LIMIT 1);
SET @id_conserje := (SELECT id FROM usuarios WHERE email = 'conserje@centro.local' LIMIT 1);
SET @id_alumno := (SELECT id FROM usuarios WHERE email = 'alumno@centro.local' LIMIT 1);

-- =========================
-- Tipos de trámite
-- =========================

INSERT INTO tipos_tramite (nombre, descripcion, duracion_estimada, requiere_documentacion)
SELECT 'Matrícula', 'Trámite de matrícula', 20, TRUE
WHERE @usuarios_existentes = 0;

INSERT INTO tipos_tramite (nombre, descripcion, duracion_estimada, requiere_documentacion)
SELECT 'Entrega de documentación', 'Aportación de documentos', 10, TRUE
WHERE @usuarios_existentes = 0;

INSERT INTO tipos_tramite (nombre, descripcion, duracion_estimada, requiere_documentacion)
SELECT 'Solicitud de certificado', 'Solicitud de certificados académicos', 15, FALSE
WHERE @usuarios_existentes = 0;

INSERT INTO tipos_tramite (nombre, descripcion, duracion_estimada, requiere_documentacion)
SELECT 'Consulta general', 'Atención general', 5, FALSE
WHERE @usuarios_existentes = 0;

SET @id_tt_matricula := (SELECT id FROM tipos_tramite WHERE nombre='Matrícula' LIMIT 1);
SET @id_tt_entrega := (SELECT id FROM tipos_tramite WHERE nombre='Entrega de documentación' LIMIT 1);
SET @id_tt_certificado := (SELECT id FROM tipos_tramite WHERE nombre='Solicitud de certificado' LIMIT 1);
SET @id_tt_consulta := (SELECT id FROM tipos_tramite WHERE nombre='Consulta general' LIMIT 1);

-- =========================
-- Reserva del alumno
-- Tabla: reservas_turno
-- Join table: reserva_tipo_tramite (reserva_id, tipo_tramite_id)
-- =========================

INSERT INTO reservas_turno (fecha_cita, hora_cita, codigo_referencia, origen_turno, estado_reserva, usuario_id, created_at, updated_at)
SELECT CURDATE(), '10:30:00', 'REF-ALUMNO-001', 'ONLINE', 'PENDIENTE', @id_alumno, NOW(), NOW()
WHERE @usuarios_existentes = 0;

SET @id_reserva_alumno := (SELECT id FROM reservas_turno WHERE codigo_referencia='REF-ALUMNO-001' LIMIT 1);

INSERT INTO reserva_tipo_tramite (reserva_id, tipo_tramite_id)
SELECT @id_reserva_alumno, @id_tt_matricula
WHERE @usuarios_existentes = 0;

INSERT INTO reserva_tipo_tramite (reserva_id, tipo_tramite_id)
SELECT @id_reserva_alumno, @id_tt_entrega
WHERE @usuarios_existentes = 0;

-- =========================
-- Turnos (hoy)
-- Tabla: turnos
-- Join table: turno_tipo_tramite (turno_id, tipo_tramite_id)
-- =========================

-- Turno principal del alumno (EN_COLA, para dashboards/kiosko)
INSERT INTO turnos (
    numero_turno, fecha_cita, hora_cita, hora_llegada,
    estado_turno, prioridad, tipo_prioridad,
    origen_turno, observaciones, duracion_estimada,
    reingreso, incidencia, prioridad_manual, motivo_prioridad,
    reserva_turno_id, created_at, updated_at
)
SELECT
    'A-001', CURDATE(), '10:30:00', '10:20:00',
    'EN_COLA', 0, 'NORMAL',
    'ONLINE', 'Turno de prueba para alumno', 20,
    FALSE, FALSE, FALSE, NULL,
    @id_reserva_alumno, NOW(), NOW()
WHERE @usuarios_existentes = 0;

SET @id_turno_alumno := (SELECT id FROM turnos WHERE numero_turno='A-001' LIMIT 1);

INSERT INTO turno_tipo_tramite (turno_id, tipo_tramite_id)
SELECT @id_turno_alumno, @id_tt_matricula
WHERE @usuarios_existentes = 0;

-- Varios turnos extra en cola
INSERT INTO turnos (
    numero_turno, fecha_cita, hora_cita, hora_llegada,
    estado_turno, prioridad, tipo_prioridad,
    origen_turno, observaciones, duracion_estimada,
    reingreso, incidencia, prioridad_manual, motivo_prioridad,
    reserva_turno_id, created_at, updated_at
)
SELECT
    'A-002', CURDATE(), '10:40:00', '10:30:00',
    'EN_COLA', 0, 'NORMAL',
    'KIOSKO', 'Turno en cola de prueba', 10,
    FALSE, FALSE, FALSE, NULL,
    @id_reserva_alumno, NOW(), NOW()
WHERE @usuarios_existentes = 0;

SET @id_turno_2 := (SELECT id FROM turnos WHERE numero_turno='A-002' LIMIT 1);
INSERT INTO turno_tipo_tramite (turno_id, tipo_tramite_id)
SELECT @id_turno_2, @id_tt_entrega
WHERE @usuarios_existentes = 0;

INSERT INTO turnos (
    numero_turno, fecha_cita, hora_cita, hora_llegada,
    estado_turno, prioridad, tipo_prioridad,
    origen_turno, observaciones, duracion_estimada,
    reingreso, incidencia, prioridad_manual, motivo_prioridad,
    reserva_turno_id, created_at, updated_at
)
SELECT
    'A-003', CURDATE(), '10:50:00', '10:40:00',
    'EN_COLA', 0, 'NORMAL',
    'KIOSKO', 'Turno en cola de prueba', 5,
    FALSE, FALSE, FALSE, NULL,
    @id_reserva_alumno, NOW(), NOW()
WHERE @usuarios_existentes = 0;

SET @id_turno_3 := (SELECT id FROM turnos WHERE numero_turno='A-003' LIMIT 1);
INSERT INTO turno_tipo_tramite (turno_id, tipo_tramite_id)
SELECT @id_turno_3, @id_tt_consulta
WHERE @usuarios_existentes = 0;

-- =========================
-- Prematrícula
-- Tabla: pre_matriculas
-- =========================

INSERT INTO pre_matriculas (
    fecha_creacion, estado,
    nombre_alumno, apellidos_alumno, dni_alumno, email_alumno, telefono_alumno,
    ciclo_solicitado, curso_solicitado, modalidad, observaciones,
    usuario_id
)
SELECT
    NOW(), 'EN_REVISION',
    'Alumno', 'Prueba', '00000004E', 'alumno@centro.local', '600000005',
    'Desarrollo de Aplicaciones Web', '1º', 'Presencial', 'Prematrícula de prueba',
    @id_alumno
WHERE @usuarios_existentes = 0;

SET @id_prematricula := (SELECT id FROM pre_matriculas WHERE dni_alumno='00000004E' LIMIT 1);

-- =========================
-- Documentos
-- Tabla: documentos
-- =========================

-- Documento pendiente ligado a prematrícula
INSERT INTO documentos (
    nombre_archivo, tipo_documento, ruta_archivo,
    estado_revision, comentario_revision,
    fecha_subida, fecha_revision,
    usuario_id, subido_por_id, revisado_por_id,
    prematricula_id, turno_id
)
SELECT
    'dni_alumno.pdf', 'DNI', '/tmp/dni_alumno.pdf',
    'PENDIENTE', NULL,
    NOW(), NULL,
    @id_alumno, @id_alumno, NULL,
    @id_prematricula, NULL
WHERE @usuarios_existentes = 0;

SET @id_doc_pendiente := (SELECT id FROM documentos WHERE nombre_archivo='dni_alumno.pdf' LIMIT 1);

-- Documento validado ligado al turno
INSERT INTO documentos (
    nombre_archivo, tipo_documento, ruta_archivo,
    estado_revision, comentario_revision,
    fecha_subida, fecha_revision,
    usuario_id, subido_por_id, revisado_por_id,
    prematricula_id, turno_id
)
SELECT
    'foto_alumno.jpg', 'FOTO', '/tmp/foto_alumno.jpg',
    'VALIDADO', 'Documento validado de prueba',
    NOW(), NOW(),
    @id_alumno, @id_alumno, @id_secretaria,
    NULL, @id_turno_alumno
WHERE @usuarios_existentes = 0;

-- =========================
-- Incidencia abierta
-- Tabla: incidencias
-- =========================

INSERT INTO incidencias (tipo, descripcion, fecha, resuelta, accion_tomada, turno_id)
SELECT 'MEDIA', 'Incidencia de prueba: falta un documento.', NOW(), FALSE, NULL, @id_turno_alumno
WHERE @usuarios_existentes = 0;

-- =========================
-- Notificaciones
-- Tabla: notificaciones
-- =========================

INSERT INTO notificaciones (
    titulo, mensaje, tipo, leida, creada_en,
    referencia, url_destino,
    enviada_por_email, fecha_envio_email,
    usuario_id
)
SELECT
    'Tienes una notificación pendiente',
    'Recuerda revisar tus notificaciones.',
    'INFORMACION', FALSE, NOW(),
    'INFO_1', '/notificaciones',
    FALSE, NULL,
    @id_alumno
WHERE @usuarios_existentes = 0;

INSERT INTO notificaciones (
    titulo, mensaje, tipo, leida, creada_en,
    referencia, url_destino,
    enviada_por_email, fecha_envio_email,
    usuario_id
)
SELECT
    'Documento pendiente',
    'Hay un documento pendiente de revisión.',
    'DOCUMENTO_SUBIDO', FALSE, NOW(),
    CONCAT('DOC_', @id_doc_pendiente), '/documentos/mis-documentos',
    FALSE, NULL,
    @id_alumno
WHERE @usuarios_existentes = 0;

-- =========================
-- Historial
-- Tabla: historial_acciones
-- =========================

INSERT INTO historial_acciones (fecha_hora, accion, descripcion, entidad_afectada, id_entidad, usuario_responsable)
SELECT NOW(), 'DATOS_INICIALES', 'Se insertaron datos iniciales de prueba.', 'Sistema', 0, @id_admin
WHERE @usuarios_existentes = 0;

INSERT INTO historial_acciones (fecha_hora, accion, descripcion, entidad_afectada, id_entidad, usuario_responsable)
SELECT NOW(), 'CREACION_TURNO', CONCAT('Se creó el turno de prueba ', 'A-001'), 'Turno', @id_turno_alumno, @id_secretaria
WHERE @usuarios_existentes = 0;

COMMIT;

