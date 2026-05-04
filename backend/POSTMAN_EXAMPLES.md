# Ejemplos de Postman - Secretaría Inteligente

## Base URL
```
http://localhost:9001
```

---

## 1. REGISTRO PÚBLICO (POST /auth/register)

### Request
**Método:** POST  
**URL:** `http://localhost:9001/auth/register`  
**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "nombre": "Juan",
  "apellidos": "García López",
  "dni": "12345678A",
  "email": "juan.garcia@example.com",
  "telefono": "612345678",
  "password": "password123"
}
```

### Response Success (201 Created)
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellidos": "García López",
  "email": "juan.garcia@example.com",
  "rol": "USUARIO",
  "mensaje": "Usuario registrado correctamente. Puedes iniciar sesión ahora."
}
```

### Response Error (400 Bad Request) - Email duplicado
```json
{
  "timestamp": "2025-05-04T10:30:00",
  "status": 400,
  "error": "Usuario duplicado",
  "mensaje": "El email ya está registrado"
}
```

### Response Error (400 Bad Request) - DNI duplicado
```json
{
  "timestamp": "2025-05-04T10:30:00",
  "status": 400,
  "error": "Usuario duplicado",
  "mensaje": "El DNI ya está registrado"
}
```

### Response Error (400 Bad Request) - Validación
```json
{
  "timestamp": "2025-05-04T10:30:00",
  "status": 400,
  "error": "Error de validación",
  "detalles": {
    "email": "El email debe tener un formato válido",
    "password": "La contraseña debe tener al menos 6 caracteres"
  }
}
```

**Nota:** El rol USUARIO se asigna automáticamente. El cliente NO puede elegir el rol.

---

## 2. LOGIN (POST /auth/login)

### Request
**Método:** POST  
**URL:** `http://localhost:9001/auth/login`  
**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "email": "juan.garcia@example.com",
  "password": "password123"
}
```

### Response Success (200 OK)
```json
{
  "mensaje": "Login correcto",
  "email": "juan.garcia@example.com",
  "rol": "USUARIO"
}
```

### Response Error (401 Unauthorized)
```json
{
  "timestamp": "2025-05-04T10:35:00",
  "status": 401,
  "error": "Credenciales inválidas",
  "mensaje": "Email o contraseña incorrectos"
}
```

**Nota:** Después del login, usar Basic Auth en Postman (pestaña Authorization > Type: Basic Auth) con email y contraseña.

---

## 3. OBTENER USUARIO ACTUAL (GET /usuarios/me)

### Request
**Método:** GET  
**URL:** `http://localhost:9001/usuarios/me`  
**Headers:**
```
Authorization: Basic anVhbi5nYXJjaWFAZXhhbXBsZS5jb206cGFzc3dvcmQxMjM=
```

### Response Success (200 OK)
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellidos": "García López",
  "email": "juan.garcia@example.com",
  "rol": "USUARIO"
}
```

**Nota:** Este endpoint funciona para USUARIO, ALUMNO, SECRETARIA, CONSERJE y ADMIN.

---

## 4. CREAR PREMATRÍCULA (POST /prematriculas)

### Request - Usuario registrado puede crear prematrícula
**Método:** POST  
**URL:** `http://localhost:9001/prematriculas`  
**Headers:**
```
Authorization: Basic anVhbi5nYXJjaWFAZXhhbXBsZS5jb206cGFzc3dvcmQxMjM=
Content-Type: application/json
```

**Body:**
```json
{
  "nombreAlumno": "Juan",
  "apellidosAlumno": "García López",
  "dniAlumno": "12345678A",
  "emailAlumno": "juan.garcia@example.com",
  "telefonoAlumno": "612345678",
  "cicloSolicitado": "Desarrollo de Aplicaciones Web",
  "cursoSolicitado": "2024/2025",
  "modalidad": "Presencial",
  "observaciones": "Ninguna"
}
```

### Response Success (200 OK)
```json
{
  "id": 1,
  "fechaCreacion": "2025-05-04T10:40:00",
  "estado": "PENDIENTE",
  "nombreAlumno": "Juan",
  "apellidosAlumno": "García López",
  "dniAlumno": "12345678A",
  "emailAlumno": "juan.garcia@example.com",
  "telefonoAlumno": "612345678",
  "cicloSolicitado": "Desarrollo de Aplicaciones Web",
  "cursoSolicitado": "2024/2025",
  "modalidad": "Presencial",
  "observaciones": "Ninguna",
  "documentos": [],
  "usuario": {
    "id": 1,
    "nombre": "Juan",
    "email": "juan.garcia@example.com",
    "rol": "USUARIO"
  }
}
```

**Nota:** Usuario con rol USUARIO puede crear prematrícula.

---

## 5. VALIDAR PREMATRÍCULA (PUT /prematriculas/{id}/estado) - SOLO SECRETARIA/ADMIN

### Request
**Método:** PUT  
**URL:** `http://localhost:9001/prematriculas/1/estado`  
**Headers:**
```
Authorization: Basic c2VjcmV0YXJpYUBleGFtcGxlLmNvbTpwYXNzd29yZDEyMw==
Content-Type: application/json
```

**Body:**
```json
{
  "estado": "VALIDADA"
}
```

### Response Success (200 OK)
```json
{
  "id": 1,
  "fechaCreacion": "2025-05-04T10:40:00",
  "estado": "VALIDADA",
  "nombreAlumno": "Juan",
  "apellidosAlumno": "García López",
  ...
}
```

**Nota IMPORTANTE:** Al cambiar el estado a VALIDADA, el backend automáticamente convierte el rol del usuario de USUARIO a ALUMNO.

---

## 6. VERIFICAR CONVERSIÓN A ALUMNO (GET /usuarios/me)

### Request - Mismo usuario después de validación
**Método:** GET  
**URL:** `http://localhost:9001/usuarios/me`  
**Headers:**
```
Authorization: Basic anVhbi5nYXJjaWFAZXhhbXBsZS5jb206cGFzc3dvcmQxMjM=
```

### Response Success (200 OK)
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellidos": "García López",
  "email": "juan.garcia@example.com",
  "rol": "ALUMNO"
}
```

**Nota:** El rol cambió de USUARIO a ALUMNO automáticamente.

---

## 7. INTENTAR ACCEDER A MATCH CON ROL USUARIO (GET /studymatch) - DEBE FALLAR

### Request - Usuario con rol USUARIO
**Método:** GET  
**URL:** `http://localhost:9001/studymatch`  
**Headers:**
```
Authorization: Basic dXN1YXJpb0BleGFtcGxlLmNvbTpwYXNzd29yZDEyMw==
```

### Response Error (403 Forbidden)
```json
{
  "timestamp": "2025-05-04T11:00:00",
  "status": 403,
  "error": "Acceso denegado",
  "mensaje": "Access Denied"
}
```

**Nota:** Usuario con rol USUARIO NO puede acceder a StudyMatch (solo ALUMNO).

---

## 8. ACCEDER A MATCH CON ROL ALUMNO (GET /studymatch) - DEBE FUNCIONAR

### Request - Usuario con rol ALUMNO
**Método:** GET  
**URL:** `http://localhost:9001/studymatch`  
**Headers:**
```
Authorization: Basic YWx1bW5vQGV4YW1wbGUuY29tOnBhc3N3b3JkMTIz
```

### Response Success (200 OK)
```json
[
  {
    "id": 1,
    "titulo": "Ayuda con Java",
    "descripcion": "Necesito ayuda con Spring Boot",
    "tipo": "NECESITO_AYUDA",
    "materia": "Programación",
    "estado": "ACTIVO",
    "autor": {
      "id": 2,
      "nombre": "María",
      "email": "maria@example.com"
    },
    "fechaCreacion": "2025-05-03T15:00:00"
  }
]
```

**Nota:** Usuario con rol ALUMNO SÍ puede acceder a StudyMatch.

---

## 9. INTENTAR ACCEDER A MERCADO CON ROL USUARIO (GET /mercado) - DEBE FALLAR

### Request
**Método:** GET  
**URL:** `http://localhost:9001/mercado`  
**Headers:**
```
Authorization: Basic dXN1YXJpb0BleGFtcGxlLmNvbTpwYXNzd29yZDEyMw==
```

### Response Error (403 Forbidden)
```json
{
  "timestamp": "2025-05-04T11:05:00",
  "status": 403,
  "error": "Acceso denegado",
  "mensaje": "Access Denied"
}
```

**Nota:** Usuario con rol USUARIO NO puede acceder al Mercado (solo ALUMNO).

---

## 10. RESERVAR TURNO CON ROL USUARIO (POST /reservas) - DEBE FUNCIONAR

### Request
**Método:** POST  
**URL:** `http://localhost:9001/reservas`  
**Headers:**
```
Authorization: Basic anVhbi5nYXJjaWFAZXhhbXBsZS5jb206cGFzc3dvcmQxMjM=
Content-Type: application/json
```

**Body:**
```json
{
  "fechaCita": "2025-05-10",
  "horaCita": "10:00",
  "tiposTramiteIds": [1, 2]
}
```

### Response Success (201 Created)
```json
{
  "id": 1,
  "fechaCita": "2025-05-10",
  "horaCita": "10:00",
  "codigoReferencia": "R-2025-05-04-001",
  "origenTurno": "RESERVA",
  "estadoReserva": "CONFIRMADA",
  "tiposTramite": ["Matrícula", "Documentación"],
  "createdAt": "2025-05-04T11:10:00",
  "updatedAt": "2025-05-04T11:10:00"
}
```

**Nota:** Usuario con rol USUARIO SÍ puede reservar turno (funcionalidad esencial).

---

## 11. SUBIR DOCUMENTO CON ROL USUARIO (POST /documentos) - DEBE FUNCIONAR

### Request
**Método:** POST  
**URL:** `http://localhost:9001/documentos`  
**Headers:**
```
Authorization: Basic anVhbi5nYXJjaWFAZXhhbXBsZS5jb206cGFzc3dvcmQxMjM=
Content-Type: application/json
```

**Body:**
```json
{
  "tipoDocumento": "DNI",
  "archivoUrl": "https://example.com/dni_juan.pdf",
  "preMatriculaId": 1
}
```

### Response Success (201 Created)
```json
{
  "id": 1,
  "tipoDocumento": "DNI",
  "archivoUrl": "https://example.com/dni_juan.pdf",
  "estadoDocumento": "PENDIENTE_REVISION",
  "fechaSubida": "2025-05-04T11:15:00"
}
```

**Nota:** Usuario con rol USUARIO SÍ puede subir documentos (funcionalidad esencial).

---

## 12. INTENTAR ACCEDER A DASHBOARD DE OTRO USUARIO - DEBE FALLAR

### Request - Usuario ID 1 intentando acceder a dashboard del usuario ID 2
**Método:** GET  
**URL:** `http://localhost:9001/dashboard/alumno/2`  
**Headers:**
```
Authorization: Basic anVhbi5nYXJjaWFAZXhhbXBsZS5jb206cGFzc3dvcmQxMjM=
```

### Response Error (403 Forbidden)
```json
{
  "timestamp": "2025-05-04T11:20:00",
  "status": 403,
  "error": "Acceso denegado",
  "mensaje": "No puedes acceder al dashboard de otro usuario"
}
```

**Nota:** Usuarios solo pueden ver su propio dashboard. Personal del centro (SECRETARIA, ADMIN) sí pueden ver todos.

---

## RESUMEN DE PERMISOS POR ROL

### USUARIO (recién registrado)
✅ Puede:
- Registrarse
- Iniciar sesión
- Ver su perfil (`/usuarios/me`)
- Reservar turno
- Ver sus propios turnos
- Confirmar llegada (check-in)
- Crear prematrícula
- Subir documentos
- Ver sus documentos
- Ver notificaciones propias
- Ver su dashboard

❌ NO puede:
- Acceder a Match de ayuda (`/studymatch`)
- Acceder a Mercado (`/mercado`)
- Acceder a Foro (`/foro`)
- Acceder a Eventos internos
- Ver dashboards de otros usuarios

### ALUMNO (matrícula validada)
✅ Todo lo de USUARIO +
- Acceder a Match de ayuda
- Acceder a Mercado
- Acceder a Foro
- Acceder a Eventos del centro
- Herramientas de comunidad

### SECRETARIA / ADMIN
✅ Gestión completa:
- Validar matrículas (activa conversión USUARIO → ALUMNO)
- Ver todos los usuarios
- Gestionar documentos
- Gestionar turnos
- Ver dashboards de todos

---

## CÓDIGOS HTTP

- **200 OK**: Operación exitosa
- **201 Created**: Recurso creado
- **400 Bad Request**: Datos inválidos o duplicados
- **401 Unauthorized**: Credenciales incorrectas
- **403 Forbidden**: Sin permisos (rol insuficiente)
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

---

## NOTAS IMPORTANTES

1. **Contraseñas cifradas**: Todas las contraseñas se cifran automáticamente con BCrypt
2. **Rol USUARIO forzado**: El cliente NO puede elegir el rol en el registro, siempre es USUARIO
3. **Conversión automática**: Al validar matrícula, el rol cambia de USUARIO a ALUMNO
4. **Validación de propiedad**: Los usuarios solo pueden acceder a sus propios recursos
5. **Basic Auth**: Usar email y contraseña en la autenticación Basic
6. **Puerto**: Backend corre en `http://localhost:9001`
