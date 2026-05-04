// ============================================
// AUTENTICACIÓN (TEMPORAL - BASIC AUTH)
// ============================================

/*
 * ⚠️⚠️⚠️ SOLUCIÓN TEMPORAL - SOLO PARA DESARROLLO/DEMO ⚠️⚠️⚠️
 *
 * Este módulo usa Basic Auth porque el backend actualmente NO implementa
 * autenticación con JWT/Bearer tokens.
 *
 * ADVERTENCIAS CRÍTICAS:
 * - Las credenciales se almacenan en sessionStorage (NO es seguro)
 * - La contraseña se guarda en texto plano en el navegador (NUNCA en producción)
 * - Basic Auth envía credenciales en cada petición (ineficiente y menos seguro)
 *
 * CUÁNDO REEMPLAZAR ESTE ARCHIVO:
 * - Cuando el backend implemente autenticación JWT
 * - Antes de desplegar en producción
 *
 * QUÉ HACER AL MIGRAR A JWT:
 * 1. Reemplazar setSession() para guardar solo el token JWT
 * 2. Cambiar getBasicAuthHeader() por getBearerToken()
 * 3. Implementar refresh token si el backend lo soporta
 * 4. NO almacenar nunca la contraseña, solo tokens
 *
 * NOTA: Este archivo NO debe modificarse en el backend.
 * Solo afecta al frontend. El backend sigue funcionando igual.
 */

const Auth = {

  /**
   * Almacena las credenciales de sesión (TEMPORAL)
   *
   * ⚠️ IMPORTANTE: Se almacena el email (no username) y password
   * porque el backend actual funciona con Basic Auth.
   *
   * En producción con JWT, este método guardaría solo:
   * - accessToken
   * - refreshToken (opcional)
   * - expiresAt
   * - userData (sin contraseña)
   *
   * @param {string} email - Email del usuario
   * @param {string} password - Contraseña (⚠️ TEMPORAL - NO seguro)
   * @param {object} userData - Datos del usuario devueltos por el backend
   */
  setSession(email, password, userData) {
    const session = {
      email,           // Se usa email, no username
      password,        // ⚠️ TEMPORAL - NO seguro para producción
      userData,
      timestamp: Date.now()
    };

    // Guardar en sessionStorage (se borra al cerrar navegador)
    sessionStorage.setItem(CONFIG.SESSION.STORAGE_KEY, JSON.stringify(session));
  },

  /**
   * Obtiene la sesión actual
   * Verifica que no haya expirado
   *
   * @returns {object|null} Sesión o null si no existe/expiró
   */
  getSession() {
    const sessionData = sessionStorage.getItem(CONFIG.SESSION.STORAGE_KEY);
    if (!sessionData) return null;

    try {
      const session = JSON.parse(sessionData);

      // Verificar si la sesión ha expirado
      const elapsed = Date.now() - session.timestamp;
      if (elapsed > CONFIG.SESSION.TIMEOUT) {
        this.clearSession();
        return null;
      }

      return session;
    } catch (error) {
      console.error('Error parsing session:', error);
      this.clearSession();
      return null;
    }
  },

  /**
   * Obtiene las credenciales para Basic Auth
   *
   * @returns {object|null} {email, password} o null
   */
  getCredentials() {
    const session = this.getSession();
    if (!session) return null;

    return {
      email: session.email,
      password: session.password
    };
  },

  /**
   * Genera el header de Basic Auth
   *
   * NOTA: Basic Auth espera "username:password" en base64.
   * En nuestro caso, usamos "email:password".
   *
   * @returns {string|null} Header Authorization o null
   */
  getBasicAuthHeader() {
    const credentials = this.getCredentials();
    if (!credentials) return null;

    // Basic Auth usa el formato: Basic base64(email:password)
    const encoded = btoa(`${credentials.email}:${credentials.password}`);
    return `Basic ${encoded}`;
  },

  /**
   * Verifica si el usuario está autenticado
   *
   * @returns {boolean}
   */
  isAuthenticated() {
    return this.getSession() !== null;
  },

  /**
   * Obtiene los datos del usuario actual
   *
   * @returns {object|null} Datos del usuario o null
   */
  getCurrentUser() {
    const session = this.getSession();
    return session ? session.userData : null;
  },

  /**
   * Cierra la sesión (limpia sessionStorage)
   */
  clearSession() {
    sessionStorage.removeItem(CONFIG.SESSION.STORAGE_KEY);
  },

  /**
   * Redirige a login si no está autenticado
   * Usar al inicio de páginas protegidas
   *
   * @returns {boolean} true si autenticado, false si redirige
   */
  requireAuth() {
    if (!this.isAuthenticated()) {
      window.location.href = CONFIG.ROUTES.LOGIN;
      return false;
    }
    return true;
  },

  /**
   * Logout completo: limpia sesión y redirige a login
   */
  logout() {
    this.clearSession();
    window.location.href = CONFIG.ROUTES.LOGIN;
  }
};

// Hacer Auth disponible globalmente
window.Auth = Auth;
