// ============================================
// COMPONENTE HEADER REUTILIZABLE
// ============================================

const Header = {

  /**
   * Inicializa el header
   * @param {Object} userData - {nombre, apellidos, rol, avatar?}
   * @param {Number} notificationCount
   */
  init(userData, notificationCount = 0) {
    this.renderUser(userData);
    this.updateNotifications(notificationCount);
    this.setupEventListeners();
  },

  /**
   * Renderiza los datos del usuario
   */
  renderUser(userData) {
    const userEl = document.getElementById('headerUser');
    if (!userEl) return;

    const nombreCompleto = `${userData.nombre} ${userData.apellidos || ''}`.trim();
    const avatar = userData.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(nombreCompleto)}&background=random`;

    userEl.innerHTML = `
      <img src="${avatar}" alt="${nombreCompleto}" class="header-user-avatar" onerror="this.src='https://ui-avatars.com/api/?name=U&background=random'">
      <div class="header-user-info">
        <div class="header-user-name">${nombreCompleto}</div>
        <div class="header-user-role">${this.formatRole(userData.rol)}</div>
      </div>
    `;
  },

  /**
   * Formatea el nombre del rol para mostrar
   */
  formatRole(rol) {
    const roles = {
      'ALUMNO': 'Alumno',
      'SECRETARIA': 'Secretaría',
      'ADMIN': 'Administrador',
      'CONSERJE': 'Conserje',
      'PROFESOR': 'Profesor'
    };
    return roles[rol] || rol;
  },

  /**
   * Actualiza el contador de notificaciones
   */
  updateNotifications(count) {
    const badge = document.querySelector('.notification-count');
    if (!badge) return;

    if (count > 0) {
      badge.textContent = count > 99 ? '99+' : count;
      badge.style.display = 'flex';
    } else {
      badge.style.display = 'none';
    }
  },

  /**
   * Configura event listeners
   */
  setupEventListeners() {
    // Búsqueda
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        console.log('Search:', e.target.value);
      });

      // Atajo Cmd/Ctrl + K
      document.addEventListener('keydown', (e) => {
        if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
          e.preventDefault();
          searchInput.focus();
        }
      });
    }

    // Notificaciones
    const notifBtn = document.getElementById('notificationsBtn');
    if (notifBtn) {
      notifBtn.addEventListener('click', () => {
        console.log('Notifications clicked');
      });
    }

    // Usuario
    const userEl = document.getElementById('headerUser');
    if (userEl) {
      userEl.addEventListener('click', () => {
        if (confirm('¿Cerrar sesión?')) {
          Auth.logout();
        }
      });
    }
  }
};

window.Header = Header;
