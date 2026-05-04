/* ============================================
   PANEL ALUMNO PREMIUM - JS PRINCIPAL
   ============================================ */

console.log('✅ panel-alumno-premium.js cargado correctamente');

// Configuración
const APP_CONFIG = window.CONFIG || {};

const PanelAlumnoPremium = {
  API_BASE: APP_CONFIG.API_BASE_URL || 'http://localhost:9001',
  inicializado: false,
  usuarioId: null,
  dashboardData: null,
  climaData: null,
  // Fuente única: CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO (definido en js/config.js)
  ubicacionCentro: null,
  ubicacionUsuario: null,

  // ==================== INICIALIZACIÓN ====================

  async init() {
    if (this.inicializado) {
      console.warn('⚠️ Panel alumno premium ya inicializado');
      return;
    }

    console.log('🚀 Inicializando panel alumno premium...');

    try {
      // Verificar sesión
      const session = window.Auth ? Auth.getSession() : null;
      if (!session || !session.userData) {
        console.error('❌ No hay sesión activa');
        window.location.href = APP_CONFIG.ROUTES?.LOGIN || 'login.html';
        return;
      }

      this.usuarioId = session.userData.id;
      console.log('✓ Usuario ID:', this.usuarioId);

      // Coordenadas del centro (fuente única: js/config.js)
      this.ubicacionCentro = (APP_CONFIG.GOOGLE_MAPS && APP_CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO)
        ? APP_CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO
        : null;

      if (!this.ubicacionCentro || typeof this.ubicacionCentro.lat !== 'number' || typeof this.ubicacionCentro.lng !== 'number') {
        console.error('❌ CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO no está configurado correctamente');
        // Degradar sin romper el panel (la geolocalización/check-in no funcionarán bien sin centro)
        this.ubicacionCentro = { lat: 0, lng: 0, nombre: 'Centro educativo' };
      }

      // Renderizar bienvenida
      this.renderWelcome(session.userData);

      // Inicializar elementos inmediatos
      this.initReloj();
      this.initHeroDinamico();

      // Registrar event listeners
      this.registrarEventListeners();

      // Cargar datos del backend en paralelo
      await Promise.all([
        this.cargarDashboard(),
        this.cargarAsistente(),
        this.cargarClima()
      ]);

      // Inicializar mapa
      setTimeout(() => this.cargarGoogleMaps(), 1000);

      this.inicializado = true;
      console.log('✅ Panel alumno premium inicializado');

    } catch (error) {
      console.error('❌ Error al inicializar panel alumno:', error);
      this.mostrarError('Error al cargar el panel');
    }
  },

  // ==================== CARGA DE DATOS BACKEND ====================

  async cargarDashboard() {
    console.log('📊 Cargando dashboard alumno...');
    try {
      const data = window.API
        ? await API.get(`/dashboard/alumno/${this.usuarioId}`)
        : await this.fetchWithAuth(`${this.API_BASE}/dashboard/alumno/${this.usuarioId}`);

      this.dashboardData = data;
      console.log('✓ Dashboard cargado:', data);

      this.renderStats(data);
      this.renderProximoTurno(data);
      this.renderAccesosRapidos(data);
      this.renderBarras(data);

    } catch (error) {
      console.error('❌ Error al cargar dashboard:', error);
      this.renderStatsDefault();
    }
  },

  async cargarAsistente() {
    console.log('🤖 Cargando asistente...');
    try {
      const data = window.API
        ? await API.get(`/asistente/usuario/${this.usuarioId}`)
        : await this.fetchWithAuth(`${this.API_BASE}/asistente/usuario/${this.usuarioId}`);

      console.log('✓ Asistente cargado:', data);

      if (data && data.length > 0) {
        this.renderAsistente(data);
      } else {
        this.renderAsistenteVacio();
      }
    } catch (error) {
      console.error('⚠️ Error al cargar asistente:', error);
      this.renderAsistenteVacio();
    }
  },

  async cargarClima() {
    console.log('🌤️ [Clima] Llamando a GET /clima/actual...');
    try {
      const data = window.API
        ? await API.get('/clima/actual')
        : await this.fetchWithAuth(`${this.API_BASE}/clima/actual`);

      this.climaData = data;
      console.log('✅ [Clima] Respuesta recibida:', data);

      if (data && data.climaDisponible) {
        console.log('✓ [Clima] Disponible -', data.temperatura + '°C', data.descripcion);
      } else {
        console.warn('⚠️ [Clima] No disponible - usando fallback');
      }

      this.renderClima(data);

    } catch (error) {
      console.error('❌ [Clima] Error HTTP:', error.message || error);
      this.renderClimaFallback();
    }
  },

  async fetchWithAuth(url) {
    const session = window.Auth ? Auth.getSession() : null;
    if (!session) throw new Error('No hay sesión');

    const headers = {
      'Content-Type': 'application/json'
    };

    if (session.email && session.password) {
      headers['Authorization'] = 'Basic ' + btoa(session.email + ':' + session.password);
    }

    const response = await fetch(url, { headers });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return await response.json();
  },

  async postWithAuth(url, body) {
    const session = window.Auth ? Auth.getSession() : null;
    if (!session) throw new Error('No hay sesión');

    const headers = {
      'Content-Type': 'application/json'
    };

    if (session.email && session.password) {
      headers['Authorization'] = 'Basic ' + btoa(session.email + ':' + session.password);
    }

    const response = await fetch(url, {
      method: 'POST',
      headers,
      body: JSON.stringify(body)
    });

    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return await response.json();
  },

  // ==================== RENDERIZADO ====================

  renderWelcome(userData) {
    const nombreElement = document.getElementById('hero-nombre-usuario');
    if (nombreElement) {
      nombreElement.textContent = userData.nombre || 'Alumno';
    }

    const sidebarName = document.getElementById('sidebar-user-name');
    if (sidebarName) {
      sidebarName.textContent = userData.nombre || 'Alumno';
    }

    const topbarName = document.getElementById('topbar-user-name');
    if (topbarName) {
      topbarName.textContent = userData.nombre || 'Alumno';
    }
  },

  renderStats(data) {
    const turnosCount = data.turnosActivos ? data.turnosActivos.length : 0;
    const documentosCount = data.documentosPendientes ? data.documentosPendientes.length : 0;
    const notificaciones = data.notificacionesNoLeidas || 0;
    const estadoPrematricula = this.getEstadoPrematriculaCorto(data.estadoPrematricula || 'No iniciada');

    this.updateStatValue('stat-turnos', turnosCount);
    this.updateStatValue('stat-documentos', documentosCount);
    this.updateStatValue('stat-notificaciones', notificaciones);
    this.updateStatValue('stat-prematricula', estadoPrematricula);
  },

  updateStatValue(elementId, value) {
    const element = document.getElementById(elementId);
    if (element) {
      element.textContent = value;
    }
  },

  renderStatsDefault() {
    this.updateStatValue('stat-turnos', '0');
    this.updateStatValue('stat-documentos', '0');
    this.updateStatValue('stat-notificaciones', '0');
    this.updateStatValue('stat-prematricula', '-');
  },

  renderClima(clima) {
    const iconoEl = document.getElementById('weather-icon') || document.getElementById('clima-icono');
    const temperaturaEl = document.getElementById('weather-temp') || document.getElementById('clima-temperatura');
    const descripcionEl = document.getElementById('weather-desc') || document.getElementById('clima-descripcion');

    if (clima && clima.climaDisponible && clima.temperatura !== null && clima.temperatura !== undefined) {
      if (iconoEl) iconoEl.textContent = clima.icono || '🌤️';
      if (temperaturaEl) temperaturaEl.textContent = `${Math.round(clima.temperatura)}°C`;
      if (descripcionEl) descripcionEl.textContent = clima.descripcion || 'Clima actual';
    } else {
      this.renderClimaFallback();
    }
  },

  renderClimaFallback() {
    const iconoEl = document.getElementById('weather-icon') || document.getElementById('clima-icono');
    const temperaturaEl = document.getElementById('weather-temp') || document.getElementById('clima-temperatura');
    const descripcionEl = document.getElementById('weather-desc') || document.getElementById('clima-descripcion');

    if (iconoEl) iconoEl.textContent = '✨';
    if (temperaturaEl) temperaturaEl.textContent = '--°C';
    if (descripcionEl) descripcionEl.textContent = 'Clima no disponible';
  },

  renderProximoTurno(data) {
    const container = document.getElementById('proximo-turno-container');
    if (!container) return;

    const turno = data.proximoTurno || data.proximaReserva;
    const turnoIdParaCheckin = this.extraerIdTurnoParaCheckin(turno);

    if (turno) {
      container.innerHTML = `
        <div style="padding: 16px; border-radius: 12px; background: linear-gradient(135deg, rgba(79, 70, 229, 0.08), rgba(139, 92, 246, 0.08)); border: 1px solid rgba(79, 70, 229, 0.15);">
          <div style="display: flex; gap: 12px; align-items: start; margin-bottom: 12px;">
            <span style="font-size: 28px;">🎫</span>
            <div style="flex: 1;">
              <h4 style="margin: 0 0 6px; color: #1f2937; font-size: 1.05rem;">Tu próximo turno</h4>
              <p style="margin: 0; color: #6b7280; font-size: 0.9rem;">Número: <strong>#${turno.numeroTurno || turno.id}</strong></p>
            </div>
          </div>
          <p style="margin: 8px 0; color: #374151; font-size: 0.875rem;">
            📅 ${turno.fechaCita ? this.formatDateTime(turno.fechaCita) : 'Fecha pendiente'}
          </p>
          <button class="btn-gradient" style="width: 100%; margin-top: 10px; padding: 12px;"
                  onclick="PanelAlumnoPremium.handleCheckIn(${turnoIdParaCheckin !== null ? turnoIdParaCheckin : 'null'})">
            📍 Hacer check-in
          </button>
        </div>
      `;
    } else {
      container.innerHTML = `
        <div style="padding: 24px; text-align: center; color: #6b7280;">
          <p style="font-size: 2rem; margin-bottom: 8px; opacity: 0.5;">📅</p>
          <p style="margin: 0; font-size: 0.9rem;">No tienes ningún turno próximo.</p>
          <button class="btn-glass" style="margin-top: 14px; padding: 10px 18px; font-size: 0.875rem;"
                  onclick="PanelAlumnoPremium.handleReservarTurno()">
            Reservar turno
          </button>
        </div>
      `;
    }
  },

  renderAccesosRapidos(data) {
    const container = document.getElementById('quick-access-row');
    if (!container) return;

    let accesos = [];

    if (data && data.accesosRapidos && data.accesosRapidos.length > 0) {
      accesos = data.accesosRapidos.slice(0, 6);
    } else {
      accesos = [
        { icon: '📅', titulo: 'Turnos', descripcion: 'Gestiona citas', accion: 'turnos' },
        { icon: '📍', titulo: 'Check-in', descripcion: 'Confirma llegada', accion: 'checkin' },
        { icon: '📝', titulo: 'Matrícula', descripcion: 'Proceso 24/25', accion: 'matricula' },
        { icon: '📄', titulo: 'Documentos', descripcion: 'Revisa pendientes', accion: 'documentos' },
        { icon: '💬', titulo: 'Foro', descripcion: 'Resuelve dudas', accion: 'foro' },
        { icon: '🎉', titulo: 'Eventos', descripcion: 'No te lo pierdas', accion: 'eventos' }
      ];
    }

    const colores = ['purple', 'green', 'blue', 'violet', 'orange', 'pink'];

    container.innerHTML = accesos.map((acceso, index) => {
      const color = colores[index % colores.length];
      const icono = acceso.icon || acceso.icono || '📌';
      const titulo = acceso.titulo || acceso.nombre || '';
      const desc = acceso.descripcion || acceso.subtitulo || '';
      const accion = acceso.accion || acceso.id || titulo.toLowerCase();

      return `
        <button class="quick-action" onclick="PanelAlumnoPremium.handleAccesoRapido('${accion}')">
          <span class="qa-icon ${color}">
            ${icono.length < 3 ? icono : '<svg viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/></svg>'}
          </span>
          <strong>${titulo}</strong>
          <small>${desc}</small>
        </button>
      `;
    }).join('');
  },

  renderBarras(data) {
    const turnosCount = data.turnosActivos ? data.turnosActivos.length : 0;
    const documentosCount = data.documentosPendientes ? data.documentosPendientes.length : 0;
    const notificaciones = data.notificacionesNoLeidas || 0;
    const progresoPrematricula = this.calcularProgresoPrematricula(data.estadoPrematricula || 'No iniciada');

    const maxValue = 10;
    const turnosPercent = Math.min((turnosCount / maxValue) * 100, 100) || 5;
    const docsPercent = Math.min((documentosCount / maxValue) * 100, 100) || 5;
    const notifsPercent = Math.min((notificaciones / maxValue) * 100, 100) || 5;

    this.setBarWidth('bar-turnos', turnosPercent);
    this.setBarWidth('bar-documentos', docsPercent);
    this.setBarWidth('bar-notificaciones', notifsPercent);
    this.setBarWidth('bar-prematricula', progresoPrematricula);
  },

  setBarWidth(elementId, percent) {
    const element = document.getElementById(elementId);
    if (element) {
      element.style.width = `${percent}%`;
    }
  },

  renderAsistente(recomendaciones) {
    const container = document.getElementById('asistente-list');
    if (!container) return;

    container.innerHTML = recomendaciones.slice(0, 5).map(rec => {
      const icon = this.getRecomendacionIcon(rec.tipo || 'INFO');
      return `
        <div style="padding: 12px 14px; border-radius: 10px; background: rgba(255, 255, 255, 0.5); margin-bottom: 8px; cursor: pointer; transition: 0.2s;"
             onmouseover="this.style.background='rgba(255,255,255,0.7)'"
             onmouseout="this.style.background='rgba(255,255,255,0.5)'"
             onclick="PanelAlumnoPremium.handleRecomendacion('${rec.urlDestino || '#'}', '${rec.titulo || rec.mensaje}')">
          <div style="display: flex; gap: 10px; align-items: start;">
            <span style="font-size: 20px; flex-shrink: 0;">${icon}</span>
            <div style="flex: 1;">
              ${rec.titulo ? `<strong style="display: block; margin-bottom: 3px; color: #1f2937; font-size: 0.9rem;">${rec.titulo}</strong>` : ''}
              <span style="color: #4b5563; font-size: 0.85rem;">${rec.mensaje}</span>
            </div>
            <span style="color: #9ca3af; font-size: 1.2rem;">→</span>
          </div>
        </div>
      `;
    }).join('');
  },

  renderAsistenteVacio() {
    const container = document.getElementById('asistente-list');
    if (!container) return;

    container.innerHTML = `
      <div style="padding: 28px; text-align: center; color: #6b7280;">
        <p style="font-size: 2.2rem; margin-bottom: 10px; opacity: 0.4;">✨</p>
        <p style="font-weight: 600; margin-bottom: 4px; color: #374151;">Todo al día</p>
        <p style="font-size: 0.85rem;">No hay recomendaciones pendientes</p>
      </div>
    `;
  },

  initReloj() {
    const updateTime = () => {
      const now = new Date();
      const timeElement = document.getElementById('current-time');
      const dateElement = document.getElementById('current-date');

      if (timeElement) {
        timeElement.textContent = now.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
      }

      if (dateElement) {
        dateElement.textContent = now.toLocaleDateString('es-ES', {
          weekday: 'long',
          day: 'numeric',
          month: 'long'
        });
      }
    };

    updateTime();
    setInterval(updateTime, 60000);
  },

  initHeroDinamico() {
    const now = new Date();
    const hora = now.getHours();

    let saludo = '';
    if (hora >= 6 && hora < 12) {
      saludo = '¡Buenos días!';
    } else if (hora >= 12 && hora < 20) {
      saludo = '¡Buenas tardes!';
    } else {
      saludo = '¡Buenas noches!';
    }

    console.log('✓', saludo);
  },

  // ==================== UBICACIÓN Y CHECK-IN ====================

  getRadioCheckinMetros() {
    return (APP_CONFIG.GOOGLE_MAPS && APP_CONFIG.GOOGLE_MAPS.RADIO_CHECKIN_METROS)
      ? Number(APP_CONFIG.GOOGLE_MAPS.RADIO_CHECKIN_METROS)
      : 500;
  },

  getModoRutaSeleccionado() {
    const select = document.getElementById('select-modo-ruta');
    const modo = (select && select.value) ? String(select.value) : 'WALKING';
    if (modo === 'DRIVING' || modo === 'TRANSIT' || modo === 'WALKING') {
      return modo;
    }
    return 'WALKING';
  },

  travelModeToGoogle(modo) {
    if (typeof google === 'undefined' || !google.maps || !google.maps.TravelMode) {
      return null;
    }

    switch (modo) {
      case 'DRIVING':
        return google.maps.TravelMode.DRIVING;
      case 'TRANSIT':
        return google.maps.TravelMode.TRANSIT;
      case 'WALKING':
      default:
        return google.maps.TravelMode.WALKING;
    }
  },

  travelModeToUrl(modo) {
    switch (modo) {
      case 'DRIVING':
        return 'driving';
      case 'TRANSIT':
        return 'transit';
      case 'WALKING':
      default:
        return 'walking';
    }
  },

  construirUrlGoogleMapsRuta(modo) {
    if (!this.ubicacionUsuario) return null;

    const origin = `${this.ubicacionUsuario.lat},${this.ubicacionUsuario.lng}`;
    const destination = `${this.ubicacionCentro.lat},${this.ubicacionCentro.lng}`;
    const travelmode = this.travelModeToUrl(modo);

    return `https://www.google.com/maps/dir/?api=1&origin=${encodeURIComponent(origin)}&destination=${encodeURIComponent(destination)}&travelmode=${encodeURIComponent(travelmode)}`;
  },

  mostrarToast(mensaje, tipo = 'info') {
    const duration = (APP_CONFIG.UI && APP_CONFIG.UI.TOAST_DURATION) ? APP_CONFIG.UI.TOAST_DURATION : 5000;

    const toast = document.createElement('div');
    toast.setAttribute('role', 'status');
    toast.style.position = 'fixed';
    toast.style.right = '18px';
    toast.style.bottom = '18px';
    toast.style.zIndex = '9999';
    toast.style.maxWidth = '420px';
    toast.style.padding = '12px 14px';
    toast.style.borderRadius = '14px';
    toast.style.fontFamily = 'system-ui, -apple-system, Segoe UI, Roboto, sans-serif';
    toast.style.fontSize = '13px';
    toast.style.fontWeight = '700';
    toast.style.backdropFilter = 'blur(12px)';
    toast.style.boxShadow = '0 16px 40px rgba(0,0,0,.18)';
    toast.style.border = '1px solid rgba(255,255,255,.35)';

    const bg = tipo === 'error'
      ? 'linear-gradient(135deg, rgba(239, 68, 68, 0.90), rgba(249, 115, 22, 0.82))'
      : 'linear-gradient(135deg, rgba(59, 130, 246, 0.86), rgba(139, 92, 246, 0.78))';

    toast.style.background = bg;
    toast.style.color = 'white';
    toast.textContent = mensaje;

    document.body.appendChild(toast);

    setTimeout(() => {
      toast.style.transition = 'opacity 240ms ease';
      toast.style.opacity = '0';
      setTimeout(() => toast.remove(), 260);
    }, duration);
  },

  extraerIdTurnoParaCheckin(obj) {
    if (!obj) return null;

    // Prioridad: campos explícitos de turno
    const candidato = obj.turnoId ?? obj.idTurno;
    if (candidato !== null && candidato !== undefined) {
      const num = Number(candidato);
      return Number.isFinite(num) ? num : null;
    }

    // Solo usar `id` si el objeto parece un Turno (evitar confundir con idReserva)
    const pareceTurno = obj.numeroTurno !== undefined
      || obj.estadoTurno !== undefined
      || obj.horaCita !== undefined
      || obj.horaLlegada !== undefined;

    if (pareceTurno && obj.id !== null && obj.id !== undefined) {
      const num = Number(obj.id);
      return Number.isFinite(num) ? num : null;
    }

    return null;
  },

  obtenerProximoTurnoId() {
    const d = this.dashboardData || {};
    return this.extraerIdTurnoParaCheckin(d.proximoTurno)
      ?? this.extraerIdTurnoParaCheckin(d.proximaReserva);
  },

  async obtenerUbicacion() {
    if (!navigator.geolocation) {
      alert('❌ Tu navegador no soporta geolocalización');
      return null;
    }

    const btnUbi = document.getElementById('btn-usar-ubicacion');
    const distanciaElement = document.getElementById('distancia-centro');

    if (btnUbi) {
      btnUbi.disabled = true;
      btnUbi.innerHTML = '⏳ Obteniendo...';
    }

    try {
      const position = await new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(resolve, reject, {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 0
        });
      });

      this.ubicacionUsuario = {
        lat: position.coords.latitude,
        lng: position.coords.longitude,
        precisionMetros: position.coords.accuracy ? Math.round(position.coords.accuracy) : null
      };

      const distancia = this.calcularDistancia(
        position.coords.latitude,
        position.coords.longitude,
        this.ubicacionCentro.lat,
        this.ubicacionCentro.lng
      );

      if (distanciaElement) {
        const radio = this.getRadioCheckinMetros();
        const dentroRadio = distancia < radio;
        distanciaElement.innerHTML = `
          <div style="padding: 16px; border-radius: 14px; background: linear-gradient(135deg, ${dentroRadio ? 'rgba(16, 185, 129, 0.12)' : 'rgba(251, 146, 60, 0.12)'}, ${dentroRadio ? 'rgba(5, 150, 105, 0.12)' : 'rgba(249, 115, 22, 0.12)'}); border: 1px solid ${dentroRadio ? 'rgba(16, 185, 129, 0.3)' : 'rgba(251, 146, 60, 0.3)'};">
            <div style="display: flex; gap: 12px; align-items: center;">
              <span style="font-size: 32px;">${dentroRadio ? '✅' : '📍'}</span>
              <div style="flex: 1;">
                <strong style="display: block; color: #1f2937; font-size: 1.1rem; margin-bottom: 4px;">
                  ${distancia >= 1000 ? (distancia / 1000).toFixed(1) + ' km' : Math.round(distancia) + ' m'} del centro
                </strong>
                <span style="font-size: 0.875rem; color: #4b5563;">
                  ${dentroRadio ? '✓ Estás cerca, puedes hacer check-in' : `⚠ Acércate más para hacer check-in (radio: ${Math.round(radio)}m)`}
                </span>
              </div>
            </div>
          </div>
        `;
      }

      // Actualizar mapa si está disponible
      if (window.MapaCentro && typeof MapaCentro.actualizarUbicacionAlumno === 'function') {
        MapaCentro.actualizarUbicacionAlumno(position.coords.latitude, position.coords.longitude, distancia);
      }

      const btnCheckin = document.getElementById('btn-checkin');
      if (btnCheckin) {
        btnCheckin.textContent = '📍 Hacer check-in';
      }

      return this.ubicacionUsuario;

    } catch (error) {
      console.error('❌ Error obteniendo ubicación:', error);
      if (distanciaElement) {
        distanciaElement.innerHTML = `
          <p style="padding: 16px; text-align: center; color: #6b7280; font-size: 0.9rem;">
            No se pudo obtener tu ubicación. Permite el acceso en tu navegador.
          </p>
        `;
      }

      return null;
    } finally {
      if (btnUbi) {
        btnUbi.disabled = false;
        btnUbi.innerHTML = '📍 Usar mi ubicación';
      }
    }
  },

  async handleCheckIn(turnoId) {
    if (!turnoId) {
      alert('⚠️ No hay ningún turno activo para hacer check-in');
      return;
    }

    const btnMapa = document.getElementById('btn-checkin');
    const btnInline = document.querySelector('button[onclick*="handleCheckIn"]');
    const btn = btnMapa || btnInline;
    if (btn) {
      btn.disabled = true;
      btn.innerHTML = '⏳ Preparando...';
    }

    try {
      if (!this.ubicacionUsuario) {
        const ubic = await this.obtenerUbicacion();
        if (!ubic) {
          throw new Error('No se pudo obtener la ubicación');
        }
      }

      if (btn) {
        btn.innerHTML = '⏳ Registrando...';
      }

      const payload = {
        latitud: this.ubicacionUsuario.lat,
        longitud: this.ubicacionUsuario.lng
      };

      if (this.ubicacionUsuario.precisionMetros !== null && this.ubicacionUsuario.precisionMetros !== undefined) {
        payload.precisionMetros = this.ubicacionUsuario.precisionMetros;
      }

      const response = window.API
        ? await API.post(`/turnos/${turnoId}/checkin-geo`, payload)
        : await this.postWithAuth(`${this.API_BASE}/turnos/${turnoId}/checkin-geo`, payload);

      alert(`✅ Check-in realizado correctamente\n\nDistancia: ${response.distanciaMetros || '?'}m`);
      await this.cargarDashboard();

    } catch (error) {
      console.error('❌ Error en check-in:', error);
      alert('❌ Error al registrar el check-in. Asegúrate de estar cerca del centro.');
    } finally {
      if (btn) {
        btn.disabled = false;
        btn.innerHTML = '📍 Hacer check-in';
      }
    }
  },

  calcularDistancia(lat1, lon1, lat2, lon2) {
    const R = 6371e3;
    const φ1 = lat1 * Math.PI / 180;
    const φ2 = lat2 * Math.PI / 180;
    const Δφ = (lat2 - lat1) * Math.PI / 180;
    const Δλ = (lon2 - lon1) * Math.PI / 180;

    const a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
              Math.cos(φ1) * Math.cos(φ2) *
              Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  },

  // ==================== EVENT LISTENERS ====================

  registrarEventListeners() {
    console.log('🎯 Registrando event listeners...');

    // Botones hero
    const btnReservarTurno = document.getElementById('btn-reservar-turno');
    if (btnReservarTurno) {
      btnReservarTurno.addEventListener('click', () => this.handleReservarTurno());
      console.log('✓ btn-reservar-turno');
    }

    const btnConfirmarLlegada = document.getElementById('btn-confirmar-llegada');
    if (btnConfirmarLlegada) {
      btnConfirmarLlegada.addEventListener('click', () => this.handleConfirmarLlegada());
      console.log('✓ btn-confirmar-llegada');
    }

    // Botones ubicación
    const btnUsarUbicacion = document.getElementById('btn-usar-ubicacion');
    if (btnUsarUbicacion) {
      btnUsarUbicacion.addEventListener('click', () => this.obtenerUbicacion());
      console.log('✓ btn-usar-ubicacion');
    }

    const btnCheckin = document.getElementById('btn-checkin');
    if (btnCheckin) {
      btnCheckin.addEventListener('click', async () => {
        if (!this.dashboardData) {
          await this.cargarDashboard();
        }

        const turnoId = this.obtenerProximoTurnoId();
        if (!turnoId) {
          alert('⚠️ No tienes ningún turno próximo para hacer check-in');
          return;
        }
        await this.handleCheckIn(turnoId);
      });
      console.log('✓ btn-checkin');
    }

    // Controles de ruta
    const btnIniciarRuta = document.getElementById('btn-iniciar-ruta');
    if (btnIniciarRuta) {
      btnIniciarRuta.addEventListener('click', async () => this.handleIniciarRuta());
      console.log('✓ btn-iniciar-ruta');
    }

    const btnAbrirGoogleMaps = document.getElementById('btn-abrir-google-maps');
    if (btnAbrirGoogleMaps) {
      btnAbrirGoogleMaps.addEventListener('click', async () => this.handleAbrirEnGoogleMaps());
      console.log('✓ btn-abrir-google-maps');
    }

    // Botón logout
    const btnLogout = document.getElementById('btn-logout');
    if (btnLogout) {
      btnLogout.addEventListener('click', () => {
        if (confirm('¿Cerrar sesión?')) {
          if (window.Auth && Auth.clearSession) {
            Auth.clearSession();
          }
          window.location.href = APP_CONFIG.ROUTES?.LOGIN || 'login.html';
        }
      });
      console.log('✓ btn-logout');
    }

    // Menú sidebar
    document.querySelectorAll('.menu-item[data-action]').forEach(item => {
      item.addEventListener('click', (e) => {
        const action = e.currentTarget.getAttribute('data-action');
        document.querySelectorAll('.menu-item').forEach(mi => mi.classList.remove('active'));
        e.currentTarget.classList.add('active');
        this.handleAccion(action);
      });
    });

    // Botones topbar
    document.querySelectorAll('.topbar-premium button[data-action]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const action = e.currentTarget.getAttribute('data-action');
        this.handleAccion(action);
      });
    });

    console.log('✅ Event listeners registrados');
  },

  // ==================== ACCIONES ====================

  handleReservarTurno() {
    alert('📅 Reservar turno\n\nPróximamente podrás reservar turno desde aquí.');
  },

  async handleConfirmarLlegada() {
    const mapa = document.querySelector('.map-card');
    if (mapa) {
      mapa.scrollIntoView({ behavior: 'smooth', block: 'start' });
      await new Promise(resolve => setTimeout(resolve, 350));
    }

    if (!this.dashboardData) {
      await this.cargarDashboard();
    }

    const turnoId = this.obtenerProximoTurnoId();

    if (!this.ubicacionUsuario) {
      await this.obtenerUbicacion();
    }

    if (turnoId && this.ubicacionUsuario) {
      await this.handleCheckIn(turnoId);
      return;
    }

    if (!turnoId) {
      alert('ℹ️ Ubicación obtenida. No tienes ningún turno próximo para hacer check-in.');
    }
  },

  async handleIniciarRuta() {
    // 1) Ubicación del alumno
    if (!this.ubicacionUsuario) {
      const ubic = await this.obtenerUbicacion();
      if (!ubic) {
        return;
      }
    }

    const modo = this.getModoRutaSeleccionado();
    const url = this.construirUrlGoogleMapsRuta(modo);

    // 2) Si Google Maps está cargado, intentamos pintar ruta dentro del mapa
    if (window.MapaCentro && typeof MapaCentro.estaDisponible === 'function' && MapaCentro.estaDisponible()) {
      const travelModeGoogle = this.travelModeToGoogle(modo);

      if (travelModeGoogle && typeof MapaCentro.trazarRutaDesdeAlumno === 'function') {
        const res = await MapaCentro.trazarRutaDesdeAlumno(travelModeGoogle);

        if (res && res.ok) {
          return;
        }

        if (modo === 'TRANSIT') {
          this.mostrarToast('No se pudo calcular ruta en transporte público. Prueba a pie o en coche.', 'error');
        }
      }
    }

    // 3) Fallback: abrir Google Maps con la ruta
    if (url) {
      window.open(url, '_blank', 'noopener');
    }
  },

  async handleAbrirEnGoogleMaps() {
    if (!this.ubicacionUsuario) {
      const ubic = await this.obtenerUbicacion();
      if (!ubic) {
        return;
      }
    }

    const modo = this.getModoRutaSeleccionado();
    const url = this.construirUrlGoogleMapsRuta(modo);
    if (url) {
      window.open(url, '_blank', 'noopener');
    }
  },

  handleAccesoRapido(accion) {
    const acciones = {
      'turnos': () => alert('📅 Gestión de turnos\n\nPróximamente disponible.'),
      'checkin': () => this.handleConfirmarLlegada(),
      'matricula': () => alert('📝 Matrícula\n\nPróximamente disponible.'),
      'documentos': () => alert('📄 Documentación\n\nPróximamente disponible.'),
      'foro': () => alert('💬 Foro académico\n\nPróximamente disponible.'),
      'eventos': () => alert('🎉 Eventos\n\nPróximamente disponible.')
    };

    if (acciones[accion]) {
      acciones[accion]();
    } else {
      alert(`📋 ${accion}\n\nPróximamente disponible.`);
    }
  },

  handleRecomendacion(url, titulo) {
    if (url && url !== '#' && url !== 'undefined') {
      window.location.href = url;
    } else {
      alert(`💡 ${titulo}\n\nEsta recomendación no tiene una acción asociada.`);
    }
  },

  handleAccion(action) {
    const acciones = {
      'inicio': () => window.location.reload(),
      'turnos': () => alert('📅 Gestión de turnos\n\nPróximamente disponible.'),
      'matricula': () => alert('📝 Matrícula\n\nPróximamente disponible.'),
      'documentacion': () => alert('📄 Documentación\n\nPróximamente disponible.'),
      'checkin': () => this.handleConfirmarLlegada(),
      'asistente': () => {
        const asistente = document.querySelector('.assistant-card');
        if (asistente) asistente.scrollIntoView({ behavior: 'smooth', block: 'start' });
      },
      'foro': () => alert('💬 Foro académico\n\nPróximamente disponible.'),
      'mapa': () => {
        const mapa = document.querySelector('.map-card');
        if (mapa) mapa.scrollIntoView({ behavior: 'smooth', block: 'start' });
      },
      'ajustes': () => alert('⚙️ Ajustes\n\nPróximamente disponible.'),
      'notificaciones': () => alert('🔔 Notificaciones\n\nPróximamente disponible.'),
      'mensajes': () => alert('💬 Mensajes\n\nPróximamente disponible.'),
      'tema': () => alert('☀️ Cambiar tema\n\nPróximamente disponible.'),
      'calendario': () => alert('📅 Calendario\n\nPróximamente disponible.'),
      'retos': () => alert('🎯 Retos semanales\n\nPróximamente disponible.')
    };

    if (acciones[action]) {
      acciones[action]();
    } else {
      console.warn('⚠️ Acción no definida:', action);
    }
  },

  // ==================== GOOGLE MAPS ====================

  cargarGoogleMaps() {
    console.log('🗺️ [Google Maps] Intentando cargar...');

    if (!APP_CONFIG.GOOGLE_MAPS) {
      console.error('❌ [Google Maps] CONFIG.GOOGLE_MAPS no configurado');
      this.mostrarFallbackMapa();
      return;
    }

    const apiKey = APP_CONFIG.GOOGLE_MAPS.API_KEY;

    if (!apiKey || apiKey.trim() === '') {
      console.warn('⚠️ [Google Maps] API key vacía. Ubicación funcionará sin mapa.');
      this.mostrarFallbackMapa();
      return;
    }

    if (typeof google !== 'undefined' && google.maps) {
      console.log('✅ [Google Maps] Ya cargado');
      this.initMapa();
      return;
    }

    console.log('📦 [Google Maps] Cargando script...');
    const script = document.createElement('script');
    script.src = `https://maps.googleapis.com/maps/api/js?key=${apiKey}&callback=initGoogleMapsCallback`;
    script.async = true;
    script.defer = true;

    script.onerror = (error) => {
      console.error('❌ [Google Maps] Error al cargar script:', error);
      this.mostrarFallbackMapa();
    };

    // Capturar errores de Google Maps API
    window.gm_authFailure = () => {
      console.error('❌ [Google Maps] InvalidKeyMapError - API key inválida');
      this.mostrarFallbackMapa();
    };

    document.head.appendChild(script);

    // Timeout por si Google Maps no carga
    setTimeout(() => {
      if (typeof google === 'undefined' || !google.maps) {
        console.error('❌ [Google Maps] Timeout - No se cargó en 10 segundos');
        this.mostrarFallbackMapa();
      }
    }, 10000);
  },

  initMapa() {
    if (window.MapaCentro && typeof MapaCentro.inicializar === 'function') {
      console.log('🗺️ [Google Maps] Inicializando con MapaCentro...');
      try {
        MapaCentro.inicializar('mapa-centro');
      } catch (error) {
        console.error('❌ [Google Maps] Error al inicializar:', error);
        this.mostrarFallbackMapa();
      }
    } else {
      console.warn('⚠️ [Google Maps] MapaCentro no disponible');
      this.mostrarFallbackMapa();
    }
  },

  mostrarFallbackMapa() {
    const container = document.getElementById('mapa-centro');
    if (!container) return;

    container.innerHTML = `
      <div style="height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; background: linear-gradient(135deg, rgba(59, 130, 246, 0.08), rgba(147, 51, 234, 0.08)); border-radius: 16px; border: 2px dashed rgba(79, 70, 229, 0.2); padding: 32px; text-align: center;">
        <div style="font-size: 3rem; margin-bottom: 16px; opacity: 0.4;">🗺️</div>
        <p style="font-weight: 600; color: #374151; margin-bottom: 8px; font-size: 1.05rem;">Mapa no disponible</p>
        <p style="font-size: 0.875rem; color: #6b7280; margin-bottom: 4px;">
          La ubicación y distancia funcionan sin necesidad del mapa
        </p>
        <p style="font-size: 0.75rem; color: #9ca3af;">
          Usa "Usar mi ubicación" para calcular tu distancia al centro
        </p>
      </div>
    `;
  },

  // ==================== UTILIDADES ====================

  calcularProgresoPrematricula(estado) {
    const estados = {
      'No iniciada': 0,
      'En proceso': 50,
      'Pendiente de validación': 75,
      'Completada': 100,
      'Validada': 100
    };
    return estados[estado] || 0;
  },

  getEstadoPrematriculaCorto(estado) {
    const cortos = {
      'No iniciada': 'Sin iniciar',
      'En proceso': 'En proceso',
      'Pendiente de validación': 'Pendiente',
      'Completada': 'Completada',
      'Validada': 'Validada'
    };
    return cortos[estado] || estado;
  },

  getRecomendacionIcon(tipo) {
    const icons = {
      'INFO': 'ℹ️',
      'AVISO': '⚠️',
      'URGENTE': '🚨',
      'EXITO': '✅'
    };
    return icons[tipo] || 'ℹ️';
  },

  formatDateTime(fecha) {
    if (!fecha) return '';
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES', {
      day: 'numeric',
      month: 'long',
      hour: '2-digit',
      minute: '2-digit'
    });
  },

  mostrarError(mensaje) {
    console.error('❌', mensaje);
  }
};

// Callback para Google Maps
window.initGoogleMapsCallback = function() {
  console.log('✅ Google Maps cargado');
  if (PanelAlumnoPremium && typeof PanelAlumnoPremium.initMapa === 'function') {
    setTimeout(() => PanelAlumnoPremium.initMapa(), 500);
  }
};

// Exponer globalmente
window.PanelAlumnoPremium = PanelAlumnoPremium;
window.PanelAlumno = window.PanelAlumno || PanelAlumnoPremium;

// Auto-inicializar cuando el DOM esté listo
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => PanelAlumnoPremium.init());
} else {
  PanelAlumnoPremium.init();
}
