// ============================================
// CONFIGURACIÓN GLOBAL
// ============================================

const CONFIG = {
  // URL base del backend
  API_BASE_URL: 'http://localhost:9001',

  // Rutas de la aplicación
  ROUTES: {
    LOGIN: 'login.html',
    ALUMNO: 'panel-alumno.html',
    SECRETARIA: 'panel-secretaria.html',
    ADMIN: 'panel-admin.html',
    KIOSKO: 'panel-kiosko.html',
    HOME: 'index.html'
  },

  // Endpoints del backend
  ENDPOINTS: {
    LOGIN: '/auth/login',
    USUARIOS_ME: '/usuarios/me',
    DASHBOARD_ALUMNO: '/dashboard/alumno',
    DASHBOARD_SECRETARIA: '/dashboard/secretaria',
    DASHBOARD_ADMIN: '/dashboard/admin',
    DASHBOARD_KIOSKO: '/dashboard/kiosko',
    ASISTENTE: '/asistente/usuario',
    KIOSKO_PANTALLA: '/kiosko/pantalla',
    KIOSKO_TRAMITES: '/kiosko/tramites',
    KIOSKO_ESTADO_TURNO: '/kiosko/estado-turno',
    TURNO_CHECKIN_GEO: '/turnos/{id}/checkin-geo'
  },

  // Configuración de sesión
  SESSION: {
    STORAGE_KEY: 'secretariaVirtualSession',
    TIMEOUT: 30 * 60 * 1000 // 30 minutos en milisegundos
  },

  // Configuración de UI
  UI: {
    TOAST_DURATION: 5000,
    ANIMATION_DURATION: 300
  },

  // Google Maps API
  // IMPORTANTE: NO poner API key real aquí. Usar config.local.js
  // Obtener API key en https://console.cloud.google.com/
  // Restringir por HTTP referrers: localhost:63342/*, localhost:5500/*
  // Habilitar: Maps JavaScript API, Directions API
  GOOGLE_MAPS: {
    API_KEY: '',
    CENTRO_EDUCATIVO: {
      lat: 42.25222480662193,
      lng: -8.690217970641129,
      nombre: 'IES de Teis - Vigo'
    },
    RADIO_CHECKIN_METROS: 500
  }
};

// Hacer CONFIG disponible globalmente
window.CONFIG = CONFIG;
