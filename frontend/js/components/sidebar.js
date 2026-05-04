// ============================================
// COMPONENTE SIDEBAR REUTILIZABLE
// ============================================

const Sidebar = {

  /**
   * Inicializa el sidebar con los items de menú y datos de usuario
   * @param {Array} menuItems - [{icon, label, href, badge?, active?}]
   * @param {Object} userData - {nombre, apellidos, rol, avatar?}
   */
  init(menuItems, userData) {
    this.renderMenu(menuItems);
    this.renderUser(userData);
    this.setupEventListeners();
  },

  /**
   * Renderiza el menú de navegación
   */
  renderMenu(items) {
    const nav = document.getElementById('sidebarNav');
    if (!nav) return;

    nav.innerHTML = items.map(item => `
      <a href="${item.href}" class="sidebar-nav-item ${item.active ? 'active' : ''}">
        <span class="sidebar-nav-icon">${item.icon}</span>
        <span class="sidebar-nav-text">${item.label}</span>
        ${item.badge ? `<span class="sidebar-nav-badge">${item.badge}</span>` : ''}
      </a>
    `).join('');
  },

  /**
   * Renderiza los datos del usuario en el footer
   */
  renderUser(userData) {
    const userEl = document.getElementById('sidebarUser');
    if (!userEl) return;

    const nombreCompleto = `${userData.nombre} ${userData.apellidos || ''}`.trim();
    const avatar = userData.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(nombreCompleto)}&background=random`;

    userEl.innerHTML = `
      <img src="${avatar}" alt="${nombreCompleto}" class="sidebar-user-avatar" onerror="this.src='https://ui-avatars.com/api/?name=U&background=random'">
      <div class="sidebar-user-info">
        <div class="sidebar-user-name">${nombreCompleto}</div>
        <div class="sidebar-user-role">${this.formatRole(userData.rol)}</div>
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
   * Configura event listeners
   */
  setupEventListeners() {
    // Click en usuario para logout o menú
    const userEl = document.getElementById('sidebarUser');
    if (userEl) {
      userEl.addEventListener('click', () => {
        if (confirm('¿Cerrar sesión?')) {
          Auth.logout();
        }
      });
    }

    // Toggle sidebar en móvil
    const toggleBtn = document.getElementById('toggleSidebar');
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('sidebarOverlay');

    if (toggleBtn && sidebar && overlay) {
      toggleBtn.addEventListener('click', () => {
        sidebar.classList.toggle('mobile-open');
        overlay.classList.toggle('active');
      });

      overlay.addEventListener('click', () => {
        sidebar.classList.remove('mobile-open');
        overlay.classList.remove('active');
      });
    }
  }
};

window.Sidebar = Sidebar;
