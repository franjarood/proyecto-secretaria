// ============================================
// COMUNICACIÓN CON EL BACKEND
// ============================================

/*
 * Este módulo maneja todas las peticiones HTTP al backend.
 *
 * AUTENTICACIÓN ACTUAL:
 * - Usa Basic Auth (temporal, compatible con backend actual)
 * - Envía header: Authorization: Basic base64(email:password)
 *
 * MIGRACIÓN FUTURA A JWT:
 * - Cambiar Basic Auth por Bearer token
 * - Header: Authorization: Bearer <token>
 * - Implementar refresh token si necesario
 *
 * IMPORTANTE: NO modifica el backend, solo consume sus endpoints.
 */

const API = {

  /**
   * Realiza una petición al backend
   *
   * @param {string} endpoint - Endpoint relativo (ej: '/dashboard/alumno/123')
   * @param {object} options - Opciones de fetch
   * @param {boolean} skipAuth - Si true, no añade header de autorización (para login)
   * @returns {Promise<any>}
   */
  async request(endpoint, options = {}, skipAuth = false) {
    const url = `${CONFIG.API_BASE_URL}${endpoint}`;

    // Headers por defecto
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers
    };

    // Añadir Basic Auth si el usuario está autenticado y no se salta
    if (!skipAuth) {
      const authHeader = Auth.getBasicAuthHeader();
      if (authHeader) {
        headers['Authorization'] = authHeader;
      }
    }

    try {
      const response = await fetch(url, {
        ...options,
        headers
      });

      // ============================================
      // MANEJO DE ERRORES HTTP
      // ============================================

      // 401 Unauthorized: Token/credenciales inválidas o expiradas
      // Acción: Limpiar sesión y redirigir a login
      if (response.status === 401) {
        console.warn('401 Unauthorized: Sesión inválida o expirada');
        Auth.clearSession();
        window.location.href = CONFIG.ROUTES.LOGIN;
        throw new Error('Sesión expirada. Redirigiendo a login...');
      }

      // 403 Forbidden: Usuario autenticado pero sin permisos
      // Acción: NO limpiar sesión, solo lanzar error para manejo específico
      if (response.status === 403) {
        const errorText = await response.text();
        console.warn('403 Forbidden: Acceso denegado', errorText);
        throw new Error('Acceso denegado. No tienes permisos para esta acción.');
      }

      // Otros errores HTTP (400, 404, 500, etc.)
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Error ${response.status}: ${errorText || response.statusText}`);
      }

      // ============================================
      // RESPUESTA EXITOSA
      // ============================================

      // Intentar parsear JSON
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      }

      // Si no es JSON, devolver texto
      return await response.text();

    } catch (error) {
      // Re-lanzar el error para que lo maneje quien llamó a la función
      console.error('API Error:', error);
      throw error;
    }
  },

  /**
   * GET request
   * @param {string} endpoint
   */
  async get(endpoint) {
    return this.request(endpoint, {
      method: 'GET'
    });
  },

  /**
   * POST request
   * @param {string} endpoint
   * @param {object} data
   */
  async post(endpoint, data) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(data)
    });
  },

  /**
   * PUT request
   * @param {string} endpoint
   * @param {object} data
   */
  async put(endpoint, data) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data)
    });
  },

  /**
   * DELETE request
   * @param {string} endpoint
   */
  async delete(endpoint) {
    return this.request(endpoint, {
      method: 'DELETE'
    });
  },

  // ============================================
  // MÉTODOS ESPECÍFICOS DEL BACKEND
  // ============================================

  /**
   * Login (NO requiere auth previa)
   *
   * ✅ CORREGIDO: Backend espera "email" y "password", no "username"
   *
   * @param {string} email - Email del usuario
   * @param {string} password - Contraseña
   * @returns {Promise<object>} Datos del usuario autenticado
   */
  async login(email, password) {
    const url = `${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.LOGIN}`;

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email,      // ✅ Corregido: se usa "email", no "username"
          password
        })
      });

      if (!response.ok) {
        if (response.status === 401) {
          throw new Error('Email o contraseña incorrectos');
        }
        throw new Error('Error al iniciar sesión');
      }

      return await response.json();

    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  },

  /**
   * Obtener datos completos del usuario autenticado
   * Requiere autenticación con Basic Auth
   *
   * @returns {Promise<object>} { id, nombre, apellidos, email }
   */
  async getUsuarioActual() {
    return this.get(CONFIG.ENDPOINTS.USUARIOS_ME);
  },

  /**
   * Dashboard alumno
   * Requiere autenticación
   *
   * @param {number} usuarioId - ID del usuario
   */
  async getDashboardAlumno(usuarioId) {
    return this.get(`${CONFIG.ENDPOINTS.DASHBOARD_ALUMNO}/${usuarioId}`);
  },

  /**
   * Dashboard secretaria
   * Requiere autenticación y rol secretaria
   */
  async getDashboardSecretaria() {
    return this.get(CONFIG.ENDPOINTS.DASHBOARD_SECRETARIA);
  },

  /**
   * Dashboard admin
   * Requiere autenticación y rol admin
   */
  async getDashboardAdmin() {
    return this.get(CONFIG.ENDPOINTS.DASHBOARD_ADMIN);
  },

  /**
   * Dashboard kiosko (público)
   */
  async getDashboardKiosko() {
    return this.get(CONFIG.ENDPOINTS.DASHBOARD_KIOSKO);
  },

  /**
   * Asistente inteligente
   * Requiere autenticación
   *
   * @param {number} usuarioId - ID del usuario
   */
  async getAsistente(usuarioId) {
    return this.get(`${CONFIG.ENDPOINTS.ASISTENTE}/${usuarioId}`);
  },

  /**
   * Check-in geolocalizado
   * Confirma la llegada del usuario a un turno con coordenadas GPS
   *
   * @param {number} turnoId - ID del turno
   * @param {number} latitud - Latitud GPS
   * @param {number} longitud - Longitud GPS
   */
  async checkinGeo(turnoId, latitud, longitud) {
    const endpoint = CONFIG.ENDPOINTS.TURNO_CHECKIN_GEO.replace('{id}', turnoId);
    return this.post(endpoint, { latitud, longitud });
  }
};

// Hacer API disponible globalmente
window.API = API;
