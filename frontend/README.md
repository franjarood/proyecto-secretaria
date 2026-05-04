# Frontend - Secretaría Virtual

## 📁 Estructura del Proyecto

```
frontend/
├── login.html                    # Página de login
├── panel-alumno.html            # Dashboard alumno (pendiente)
├── panel-secretaria.html        # Dashboard secretaria (pendiente)
├── panel-admin.html             # Dashboard admin (pendiente)
├── panel-kiosko.html            # Kiosko público (pendiente)
├── index.html                   # Home pública (pendiente)
│
├── css/
│   ├── reset.css                # Reset y normalización
│   ├── variables.css            # Variables CSS globales
│   ├── base.css                 # Estilos base y utilidades
│   ├── botones.css              # Sistema de botones
│   └── login.css                # Estilos específicos del login
│
├── js/
│   ├── config.js                # Configuración global
│   ├── auth.js                  # Autenticación (Basic Auth temporal)
│   ├── api.js                   # Comunicación con backend
│   │
│   ├── components/              # Componentes reutilizables (pendiente)
│   │   ├── sidebar.js
│   │   ├── header.js
│   │   └── asistente.js
│   │
│   └── pages/                   # Lógica específica por página
│       └── login.js             # Lógica del login
│
└── assets/
    └── icons/                   # Iconos (vacío, se usan SVG inline)
```

## 🔧 Backend

**Puerto:** http://localhost:9001

**Endpoints disponibles:**
- `POST /auth/login` - Login (email, password)
- `GET /usuarios/me` - Usuario autenticado actual
- `GET /dashboard/alumno/{usuarioId}`
- `GET /dashboard/secretaria`
- `GET /dashboard/admin`
- `GET /dashboard/kiosko`
- `GET /asistente/usuario/{usuarioId}`
- `POST /turnos/{id}/checkin-geo`

## 🔐 Autenticación Actual

⚠️ **TEMPORAL - Solo desarrollo/demo**

- **NO usa JWT/Bearer tokens**
- Usa **Basic Auth** (email:password en base64)
- Credenciales en `sessionStorage` (NO seguro para producción)

### Flujo de login:
1. POST `/auth/login` → `{ mensaje, email, rol }`
2. GET `/usuarios/me` (con Basic Auth) → `{ id, nombre, apellidos, email }`
3. Guardar sesión completa
4. Redirigir según rol

## 🎨 Diseño Visual

- Fondo gris-azulado (`#e8edf5`)
- Tarjetas claras con sombras
- Sidebar oscuro (`#1a2332`)
- Botones con gradiente azul/morado
- Diseño tipo SaaS premium
- Colores por rol:
  - **Alumno**: Azul/Morado
  - **Secretaria**: Verde/Esmeralda
  - **Admin**: Morado/Rosa
  - **Conserje/Kiosko**: Naranja/Marrón

## 🚀 Cómo usar

1. Abrir `login.html` en el navegador
2. Introducir credenciales válidas del backend
3. El sistema:
   - Valida con `/auth/login`
   - Obtiene datos completos con `/usuarios/me`
   - Guarda sesión
   - Redirige al panel correspondiente

## 📝 Próximos pasos

1. Crear `panel-alumno.html` con datos reales
2. Crear `panel-secretaria.html`
3. Crear `panel-admin.html`
4. Crear `panel-kiosko.html`
5. Crear `index.html` (home pública)
6. Implementar componentes reutilizables (sidebar, header, asistente)

## ⚠️ Limitaciones conocidas

### Rol PROFESOR
El backend actual NO tiene un endpoint específico para profesores.
Los roles soportados en el login frontend son:
- ALUMNO
- SECRETARIA
- ADMIN
- CONSERJE

Si se necesita soporte para PROFESOR, se debe:
1. Confirmar qué devuelve `/usuarios/me` para un profesor
2. Añadir ruta en `config.js`
3. Añadir mapeo en `login.js`

## 🔒 Migración futura a JWT

Cuando el backend implemente JWT:
1. Reemplazar `auth.js` completamente
2. Modificar `api.js` para usar Bearer tokens
3. NO almacenar password, solo tokens
4. Implementar refresh token si es necesario
