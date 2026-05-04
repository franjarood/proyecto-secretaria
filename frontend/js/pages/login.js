// ============================================
// PÁGINA DE LOGIN
// ============================================

/*
 * FLUJO DE AUTENTICACIÓN REAL:
 *
 * 1. Usuario envía email + password
 * 2. POST /auth/login
 *    → Respuesta: { mensaje, email, rol }
 * 3. Si login OK, NO guardar sesión todavía
 * 4. Llamar a GET /usuarios/me (con Basic Auth usando las credenciales del login)
 *    → Respuesta: { id, nombre, apellidos, email }
 * 5. Guardar sesión completa SOLO si ambos pasos fueron exitosos
 * 6. Redirigir según rol
 *
 * IMPORTANTE: Si falla el paso 4, NO se guarda nada y se muestra error
 */

// ============================================
// ESTADO DE LA PÁGINA
// ============================================

const LoginPage = {
  form: null,
  emailInput: null,
  passwordInput: null,
  loginButton: null,
  loginError: null,
  loadingOverlay: null,

  init() {
    // Verificar si ya está autenticado
    if (Auth.isAuthenticated()) {
      this.redirectToPanel();
      return;
    }

    // Obtener elementos del DOM
    this.form = document.getElementById('loginForm');
    this.emailInput = document.getElementById('email');
    this.passwordInput = document.getElementById('password');
    this.loginButton = document.getElementById('loginButton');
    this.loginError = document.getElementById('loginError');
    this.loadingOverlay = document.getElementById('loadingOverlay');

    // Setup event listeners
    this.setupEventListeners();
  },

  setupEventListeners() {
    // Submit del formulario
    this.form.addEventListener('submit', (e) => {
      e.preventDefault();
      this.handleLogin();
    });

    // Toggle mostrar/ocultar contraseña
    const togglePassword = document.getElementById('togglePassword');
    if (togglePassword) {
      togglePassword.addEventListener('click', () => {
        this.togglePasswordVisibility();
      });
    }

    // Limpiar errores al escribir
    this.emailInput.addEventListener('input', () => {
      this.clearFieldError('email');
      this.hideLoginError();
    });

    this.passwordInput.addEventListener('input', () => {
      this.clearFieldError('password');
      this.hideLoginError();
    });

    // Links temporales (pueden implementarse después)
    document.getElementById('forgotPassword')?.addEventListener('click', (e) => {
      e.preventDefault();
      alert('Funcionalidad de recuperación de contraseña próximamente');
    });

    document.getElementById('registerLink')?.addEventListener('click', (e) => {
      e.preventDefault();
      alert('Para solicitar acceso, contacta con la secretaría del centro');
    });
  },

  // ============================================
  // LÓGICA DE LOGIN (FLUJO REAL Y ROBUSTO)
  // ============================================

  async handleLogin() {
    // Validar formulario
    if (!this.validateForm()) {
      return;
    }

    const email = this.emailInput.value.trim();
    const password = this.passwordInput.value;

    // Mostrar loading
    this.showLoading();
    this.disableForm();

    try {
      // PASO 1: Login básico
      console.log('📝 Paso 1: Intentando login...');
      const loginResponse = await API.login(email, password);

      console.log('✅ Login exitoso:', loginResponse);
      console.log('📧 Email:', loginResponse.email);
      console.log('👤 Rol:', loginResponse.rol);

      // PASO 2: Guardar credenciales TEMPORALMENTE solo para hacer /usuarios/me
      // NO guardamos sesión completa todavía
      const tempCredentials = { email, password };

      // PASO 3: Obtener datos completos del usuario (incluyendo ID)
      console.log('📝 Paso 2: Obteniendo datos completos del usuario...');

      // Crear header Basic Auth temporal
      const tempAuthHeader = `Basic ${btoa(`${email}:${password}`)}`;

      const userResponse = await fetch(`${CONFIG.API_BASE_URL}${CONFIG.ENDPOINTS.USUARIOS_ME}`, {
        method: 'GET',
        headers: {
          'Authorization': tempAuthHeader
        }
      });

      if (!userResponse.ok) {
        throw new Error('No se pudieron obtener los datos del usuario');
      }

      const userData = await userResponse.json();

      console.log('✅ Datos del usuario obtenidos:', userData);
      console.log('🆔 ID:', userData.id);
      console.log('👤 Nombre completo:', userData.nombre, userData.apellidos);

      // PASO 4: Guardar sesión completa SOLO si TODO fue exitoso
      Auth.setSession(email, password, {
        id: userData.id,                    // ✅ ID real del backend
        email: userData.email,
        nombre: userData.nombre,
        apellidos: userData.apellidos,
        rol: loginResponse.rol              // Del login
      });

      console.log('✅ Sesión guardada correctamente');

      // PASO 5: Redirigir según rol
      this.redirectToPanel(loginResponse.rol);

    } catch (error) {
      console.error('❌ Error en el login:', error);

      // Limpiar cualquier dato temporal que pudiera haberse guardado
      Auth.clearSession();

      this.showLoginError(error.message || 'Error al iniciar sesión');
      this.hideLoading();
      this.enableForm();
    }
  },

  // ============================================
  // VALIDACIÓN
  // ============================================

  validateForm() {
    let isValid = true;

    // Validar email
    const email = this.emailInput.value.trim();
    if (!email) {
      this.showFieldError('email', 'El correo electrónico es obligatorio');
      isValid = false;
    } else if (!this.isValidEmail(email)) {
      this.showFieldError('email', 'El correo electrónico no es válido');
      isValid = false;
    }

    // Validar password
    const password = this.passwordInput.value;
    if (!password) {
      this.showFieldError('password', 'La contraseña es obligatoria');
      isValid = false;
    } else if (password.length < 3) {
      this.showFieldError('password', 'La contraseña es demasiado corta');
      isValid = false;
    }

    return isValid;
  },

  isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  },

  // ============================================
  // REDIRECCIÓN
  // ============================================

  redirectToPanel(rol = null) {
    // Si no se pasa rol, obtenerlo de la sesión
    if (!rol) {
      const user = Auth.getCurrentUser();
      rol = user?.rol;
    }

    // Mapeo de roles a páginas
    const rolePages = {
      'ALUMNO': CONFIG.ROUTES.ALUMNO,
      'SECRETARIA': CONFIG.ROUTES.SECRETARIA,
      'ADMIN': CONFIG.ROUTES.ADMIN,
      'CONSERJE': CONFIG.ROUTES.KIOSKO
    };

    const targetPage = rolePages[rol] || CONFIG.ROUTES.HOME;

    console.log('🚀 Redirigiendo a:', targetPage);
    window.location.href = targetPage;
  },

  // ============================================
  // UI - ERRORES
  // ============================================

  showFieldError(field, message) {
    const errorElement = document.getElementById(`${field}Error`);
    const inputElement = document.getElementById(field);

    if (errorElement) {
      errorElement.textContent = message;
      errorElement.style.display = 'block';
    }

    if (inputElement) {
      inputElement.classList.add('error');
    }
  },

  clearFieldError(field) {
    const errorElement = document.getElementById(`${field}Error`);
    const inputElement = document.getElementById(field);

    if (errorElement) {
      errorElement.textContent = '';
      errorElement.style.display = 'none';
    }

    if (inputElement) {
      inputElement.classList.remove('error');
    }
  },

  showLoginError(message) {
    if (this.loginError) {
      document.getElementById('loginErrorMessage').textContent = message;
      this.loginError.classList.remove('hidden');
    }
  },

  hideLoginError() {
    if (this.loginError) {
      this.loginError.classList.add('hidden');
    }
  },

  // ============================================
  // UI - LOADING
  // ============================================

  showLoading() {
    if (this.loadingOverlay) {
      this.loadingOverlay.classList.remove('hidden');
    }

    // Cambiar texto del botón
    const btnText = this.loginButton.querySelector('.btn-text');
    if (btnText) {
      btnText.textContent = 'Iniciando sesión...';
    }

    // Añadir clase loading al botón
    this.loginButton.classList.add('loading');
  },

  hideLoading() {
    if (this.loadingOverlay) {
      this.loadingOverlay.classList.add('hidden');
    }

    // Restaurar texto del botón
    const btnText = this.loginButton.querySelector('.btn-text');
    if (btnText) {
      btnText.textContent = 'Iniciar sesión';
    }

    // Quitar clase loading del botón
    this.loginButton.classList.remove('loading');
  },

  // ============================================
  // UI - FORM STATE
  // ============================================

  disableForm() {
    this.emailInput.disabled = true;
    this.passwordInput.disabled = true;
    this.loginButton.disabled = true;
  },

  enableForm() {
    this.emailInput.disabled = false;
    this.passwordInput.disabled = false;
    this.loginButton.disabled = false;
  },

  // ============================================
  // UI - TOGGLE PASSWORD
  // ============================================

  togglePasswordVisibility() {
    const passwordInput = this.passwordInput;
    const iconShow = document.querySelector('.icon-show');
    const iconHide = document.querySelector('.icon-hide');

    if (passwordInput.type === 'password') {
      passwordInput.type = 'text';
      iconShow.classList.add('hidden');
      iconHide.classList.remove('hidden');
    } else {
      passwordInput.type = 'password';
      iconShow.classList.remove('hidden');
      iconHide.classList.add('hidden');
    }
  }
};

// ============================================
// INICIALIZACIÓN
// ============================================

document.addEventListener('DOMContentLoaded', () => {
  LoginPage.init();
});
