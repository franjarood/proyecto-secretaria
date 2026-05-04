/* ============================================
   PANEL ALUMNO - VERSIÓN PREMIUM MEJORADA
   Con clima real, accesos rápidos, ubicación y mini-gráfica
   ============================================ */

const PanelAlumno = {
  usuarioId: null,
  dashboardData: null,
  recomendaciones: null,
  climaData: null,
  ubicacionCentro: { lat: 42.2406, lng: -8.7207 }, // IES de Teis, Vigo
  ubicacionUsuario: null,

  // ==================== INICIALIZACIÓN ====================

  async init() {
    try {
      const session = Auth.getSession();
      if (!session || !session.userData) {
        window.location.href = CONFIG.ROUTES.LOGIN;
        return;
      }

      this.usuarioId = session.userData.id;

      // Renderizar elementos inmediatos
      this.renderWelcome(session.userData);
      this.initHeroDinamico();
      this.initReloj();

      // Cargar datos del backend en paralelo
      await Promise.all([
        this.cargarDashboard(),
        this.cargarAsistente(),
        this.cargarNotificaciones(),
        this.cargarClima()
      ]);

      // Renderizar accesos rápidos
      this.renderAccesosRapidos();

    } catch (error) {
      console.error('Error al inicializar panel alumno:', error);
      this.mostrarError('Error al cargar el panel');
    }

    // Inicializar mapa después de cargar todo
    setTimeout(() => this.initMapa(), 1500);
  },

  // ==================== INICIALIZACIÓN DEL MAPA ====================

  async initMapa() {
    // Esperar a que Google Maps esté disponible
    if (typeof google === 'undefined' || !google.maps) {
      console.warn('Google Maps no disponible, mostrando fallback');
      return;
    }

    if (window.MapaCentro) {
      const inicializado = await MapaCentro.inicializar('mapa-centro');
      if (inicializado) {
        console.log('Mapa del centro inicializado correctamente');
      }
    }
  },

  // ==================== CARGA DE DATOS BACKEND ====================

  async cargarDashboard() {
    try {
      const data = await API.get(`/dashboard/alumno/${this.usuarioId}`);
      this.dashboardData = data;
      console.log('Dashboard cargado:', data);

      this.renderStats(data);
      this.renderMiniChart(data);
      this.renderProximoTurno(data);
      this.renderEstadoPrematricula(data);

    } catch (error) {
      console.error('Error al cargar dashboard:', error);
      this.renderStatsDefault();
      this.mostrarMensajeInfo('No se pudieron cargar todos los datos. Intenta recargar la página.');
    }
  },

  async cargarAsistente() {
    try {
      const recomendaciones = await API.get(`/asistente/usuario/${this.usuarioId}`);
      this.recomendaciones = recomendaciones;

      if (recomendaciones && recomendaciones.length > 0) {
        this.renderAsistente(recomendaciones);
      } else {
        this.renderAsistenteVacio();
      }
    } catch (error) {
      console.error('Error al cargar asistente:', error);
      this.renderAsistenteVacio();
    }
  },

  async cargarNotificaciones() {
    try {
      const notificaciones = await API.get('/notificaciones/mis-notificaciones');
      const noLeidas = notificaciones.filter(n => !n.leida).length;

      // Actualizar badge
      const badge = document.querySelector('.notification-count');
      if (badge) {
        badge.textContent = noLeidas;
        badge.style.display = noLeidas > 0 ? 'flex' : 'none';
      }
    } catch (error) {
      console.error('Error al cargar notificaciones:', error);
    }
  },

  async cargarClima() {
    try {
      const clima = await API.get('/clima/actual');
      this.climaData = clima;
      this.renderClima(clima);
    } catch (error) {
      console.error('Error al cargar clima:', error);
      this.renderClimaFallback();
    }
  },

  // ==================== RENDERIZADO DE DATOS ====================

  renderWelcome(userData) {
    const nombreElement = document.getElementById('hero-nombre-usuario');
    if (nombreElement) {
      nombreElement.textContent = userData.nombre;
    }
  },

  renderStats(data) {
    // Turnos activos
    const turnosCount = data.turnosActivos ? data.turnosActivos.length : 0;
    this.updateStatValue('stat-turnos', turnosCount);

    // Documentos pendientes
    const documentosCount = data.documentosPendientes ? data.documentosPendientes.length : 0;
    this.updateStatValue('stat-documentos', documentosCount);

    // Notificaciones no leídas
    const notificaciones = data.notificacionesNoLeidas || 0;
    this.updateStatValue('stat-notificaciones', notificaciones);

    // Estado prematrícula
    const estadoPrematricula = data.estadoPrematricula || 'No iniciada';
    this.updateStatValue('stat-prematricula', this.getEstadoPrematriculaCorto(estadoPrematricula));
  },

  updateStatValue(elementId, value) {
    const element = document.getElementById(elementId);
    if (element) {
      const valueElement = element.querySelector('.card-stat-value');
      if (valueElement) {
        valueElement.textContent = value;
      }
    }
  },

  renderStatsDefault() {
    this.updateStatValue('stat-turnos', '0');
    this.updateStatValue('stat-documentos', '0');
    this.updateStatValue('stat-notificaciones', '0');
    this.updateStatValue('stat-prematricula', '-');
  },

  renderClima(clima) {
    // Compatibilidad con ambos grupos de IDs: weather-* y clima-*
    const iconoEl = document.getElementById('weather-icon') || document.getElementById('clima-icono');
    const temperaturaEl = document.getElementById('weather-temp') || document.getElementById('clima-temperatura');
    const descripcionEl = document.getElementById('weather-desc') || document.getElementById('clima-descripcion');

    if (clima && clima.climaDisponible && clima.temperatura) {
      if (iconoEl) iconoEl.textContent = clima.icono || '🌤️';
      if (temperaturaEl) temperaturaEl.textContent = `${Math.round(clima.temperatura)}°C`;
      if (descripcionEl) descripcionEl.textContent = clima.descripcion || 'Clima actual';
    } else {
      this.renderClimaFallback();
    }
  },

  renderClimaFallback() {
    // Compatibilidad con ambos grupos de IDs: weather-* y clima-*
    const iconoEl = document.getElementById('weather-icon') || document.getElementById('clima-icono');
    const temperaturaEl = document.getElementById('weather-temp') || document.getElementById('clima-temperatura');
    const descripcionEl = document.getElementById('weather-desc') || document.getElementById('clima-descripcion');

    if (iconoEl) iconoEl.textContent = '✨';
    if (temperaturaEl) temperaturaEl.textContent = '--';
    if (descripcionEl) descripcionEl.textContent = 'Clima no disponible';
  },

  renderAccesosRapidos() {
    const container = document.getElementById('accesos-rapidos-container');
    if (!container) return;

    // Usar accesos rápidos del backend si existen, sino usar fallback
    let accesos = [];

    if (this.dashboardData && this.dashboardData.accesosRapidos && this.dashboardData.accesosRapidos.length > 0) {
      accesos = this.dashboardData.accesosRapidos.slice(0, 6);
    } else {
      // Fallback con accesos básicos
      accesos = [
        { icon: '🎫', titulo: 'Turnos', descripcion: 'Gestiona tus citas', accion: 'turnos' },
        { icon: '📍', titulo: 'Check-in', descripcion: 'Confirma tu llegada', accion: 'checkin' },
        { icon: '📝', titulo: 'Matrícula', descripcion: 'Estado y trámites', accion: 'matricula' },
        { icon: '📄', titulo: 'Documentos', descripcion: 'Subir archivos', accion: 'documentos' },
        { icon: '💬', titulo: 'Foro', descripcion: 'Comunidad estudiantil', accion: 'foro' },
        { icon: '🎉', titulo: 'Eventos', descripcion: 'Próximas actividades', accion: 'eventos' }
      ];
    }

    container.innerHTML = accesos.map(acceso => `
      <div class="acceso-rapido-item" onclick="PanelAlumno.handleAccesoRapido('${acceso.accion || acceso.titulo}')">
        <div class="acceso-rapido-icon">${acceso.icon || '📌'}</div>
        <div class="acceso-rapido-label">${acceso.titulo}</div>
        ${acceso.descripcion ? `<div class="acceso-rapido-subtitle">${acceso.descripcion}</div>` : ''}
      </div>
    `).join('');
  },

  renderMiniChart(data) {
    const container = document.getElementById('mini-chart-container');
    if (!container) return;

    // Usar SOLO datos reales, no inventar métricas
    const turnosCount = data.turnosActivos ? data.turnosActivos.length : 0;
    const documentosCount = data.documentosPendientes ? data.documentosPendientes.length : 0;
    const notificaciones = data.notificacionesNoLeidas || 0;
    const progresoPrematricula = this.calcularProgresoPrematricula(data.estadoPrematricula || 'No iniciada');

    // Calcular porcentajes para barras (máximo 10 para escalado visual)
    const maxValue = 10;
    const turnosPercent = Math.min((turnosCount / maxValue) * 100, 100);
    const docsPercent = Math.min((documentosCount / maxValue) * 100, 100);
    const notifsPercent = Math.min((notificaciones / maxValue) * 100, 100);

    container.innerHTML = `
      <div class="mini-chart-item">
        <div class="mini-chart-label">Turnos activos</div>
        <div class="mini-chart-bar-container">
          <div class="mini-chart-bar blue" style="width: ${turnosPercent || 5}%">${turnosCount}</div>
        </div>
      </div>
      <div class="mini-chart-item">
        <div class="mini-chart-label">Documentos pendientes</div>
        <div class="mini-chart-bar-container">
          <div class="mini-chart-bar purple" style="width: ${docsPercent || 5}%">${documentosCount}</div>
        </div>
      </div>
      <div class="mini-chart-item">
        <div class="mini-chart-label">Notificaciones</div>
        <div class="mini-chart-bar-container">
          <div class="mini-chart-bar orange" style="width: ${notifsPercent || 5}%">${notificaciones}</div>
        </div>
      </div>
      <div class="mini-chart-item">
        <div class="mini-chart-label">Progreso prematrícula</div>
        <div class="mini-chart-bar-container">
          <div class="mini-chart-bar green" style="width: ${progresoPrematricula}%">${progresoPrematricula}%</div>
        </div>
      </div>
    `;
  },

  renderProximoTurno(data) {
    const container = document.getElementById('proximo-turno-container');
    if (!container) return;

    if (data.proximoTurno) {
      const turno = data.proximoTurno;
      container.innerHTML = `
        <div class="turno-destacado">
          <div class="turno-destacado-header">
            <div class="turno-destacado-icon">🎫</div>
            <div class="turno-destacado-info">
              <h3 class="turno-destacado-title">Tu próximo turno</h3>
              <p class="turno-destacado-numero">Número: <strong>#${turno.numeroTurno}</strong></p>
            </div>
          </div>
          <div class="turno-destacado-body">
            <p class="turno-destacado-fecha">📅 ${this.formatDateTime(turno.fechaCita)}</p>
            <p class="turno-destacado-estado">
              <span class="badge badge-${this.getBadgeColor(turno.estadoTurno)}">${turno.estadoTurno}</span>
            </p>
          </div>
          <button class="btn btn-primary btn-sm" onclick="PanelAlumno.handleCheckIn(${turno.id})" id="btn-checkin-turno">
            📍 Hacer check-in
          </button>
        </div>
      `;
    } else {
      container.innerHTML = `
        <div class="empty-state">
          <div class="empty-state-icon">📅</div>
          <p class="empty-state-text">No tienes turnos programados</p>
          <button class="btn btn-primary btn-sm" onclick="PanelAlumno.handleReservarTurno()">
            Reservar turno
          </button>
        </div>
      `;
    }
  },

  renderEstadoPrematricula(data) {
    const container = document.getElementById('prematricula-progress');
    if (!container) return;

    const estado = data.estadoPrematricula || 'No iniciada';
    const progreso = this.calcularProgresoPrematricula(estado);

    container.innerHTML = `
      <div class="progress-info">
        <span class="progress-label">${estado}</span>
        <span class="progress-percentage">${progreso}%</span>
      </div>
      <div class="progress-bar">
        <div class="progress-bar-fill" style="width: ${progreso}%"></div>
      </div>
    `;
  },

  renderAsistente(recomendaciones) {
    const container = document.getElementById('asistente-list');
    if (!container) return;

    container.innerHTML = recomendaciones.slice(0, 5).map(rec => {
      const icon = this.getRecomendacionIcon(rec.tipo);
      return `
        <div class="card-assistant-item" onclick="PanelAlumno.handleRecomendacion('${rec.urlDestino || '#'}', '${rec.titulo || rec.mensaje}')">
          <div class="card-assistant-item-icon">${icon}</div>
          <div class="card-assistant-item-text">
            ${rec.titulo ? `<strong>${rec.titulo}</strong><br>` : ''}
            <span style="opacity: 0.95;">${rec.mensaje}</span>
          </div>
          <div class="card-assistant-item-arrow">→</div>
        </div>
      `;
    }).join('');
  },

  renderAsistenteVacio() {
    const container = document.getElementById('asistente-list');
    if (!container) return;

    container.innerHTML = `
      <div style="padding: 24px; text-align: center; color: rgba(255,255,255,0.85);">
        <p style="font-size: 2rem; margin-bottom: 8px;">✨</p>
        <p style="font-weight: 600; margin-bottom: 4px;">Todo al día</p>
        <p style="font-size: 0.875rem; opacity: 0.8;">No hay recomendaciones pendientes</p>
      </div>
    `;
  },

  // ==================== HERO DINÁMICO ====================

  initHeroDinamico() {
    const now = new Date();
    const hora = now.getHours();
    const heroMessage = document.getElementById('hero-message');

    if (!heroMessage) return;

    let saludo = '';
    let emoji = '';

    if (hora >= 6 && hora < 12) {
      saludo = '¡Buenos días!';
      emoji = '☀️';
    } else if (hora >= 12 && hora < 20) {
      saludo = '¡Buenas tardes!';
      emoji = '🌤️';
    } else {
      saludo = '¡Buenas noches!';
      emoji = '🌙';
    }

    heroMessage.innerHTML = `${saludo} ${emoji}<br>Todo lo que necesitas, en un solo lugar.<br>Gestiona tu tiempo, trámites y vida académica.`;
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
    setInterval(updateTime, 60000); // Actualizar cada minuto
  },

  // ==================== UBICACIÓN Y CHECK-IN ====================

  async obtenerUbicacion() {
    if (!navigator.geolocation) {
      this.mostrarMensajeError('Tu navegador no soporta geolocalización');
      return;
    }

    const btnUbi = document.getElementById('btn-usar-ubicacion');
    const btnComoLlegar = document.getElementById('btn-como-llegar');
    const distanciaElement = document.getElementById('distancia-centro');

    if (btnUbi) {
      btnUbi.disabled = true;
      btnUbi.innerHTML = '<div class="spinner" style="width: 16px; height: 16px; border: 2px solid currentColor; border-top-color: transparent; border-radius: 50%; animation: spin 0.6s linear infinite;"></div> Obteniendo...';
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
        lng: position.coords.longitude
      };

      const distancia = this.calcularDistancia(
        position.coords.latitude,
        position.coords.longitude,
        this.ubicacionCentro.lat,
        this.ubicacionCentro.lng
      );

      if (distanciaElement) {
        const dentroRadio = distancia < 500; // 500 metros
        distanciaElement.innerHTML = `
          <div class="ubicacion-info-premium ${dentroRadio ? 'dentro-radio' : 'fuera-radio'}">
            <div class="ubicacion-icon-premium">${dentroRadio ? '✅' : '📍'}</div>
            <div class="ubicacion-text-premium">
              <strong>${distancia >= 1000 ? (distancia / 1000).toFixed(1) + ' km' : Math.round(distancia) + ' m'}</strong> del centro<br>
              <span style="font-size: 0.875rem; opacity: 0.9;">
                ${dentroRadio ? 'Estás cerca, puedes hacer check-in' : 'Acércate más para hacer check-in (radio: 500m)'}
              </span>
            </div>
          </div>
        `;
      }

      // Actualizar mapa con ubicación del alumno
      if (window.MapaCentro && MapaCentro.estaDisponible()) {
        MapaCentro.actualizarUbicacionAlumno(
          position.coords.latitude,
          position.coords.longitude,
          distancia
        );
      }

      // Habilitar botón "Abrir en Google Maps"
      const btnAbrirMaps = document.getElementById('btn-abrir-google-maps');
      if (btnAbrirMaps) {
        btnAbrirMaps.disabled = false;
      }

    } catch (error) {
      console.error('Error obteniendo ubicación:', error);
      if (distanciaElement) {
        distanciaElement.innerHTML = `
          <p class="text-secondary text-center" style="padding: 16px;">No se pudo obtener tu ubicación. Permite el acceso en tu navegador.</p>
        `;
      }
    } finally {
      if (btnUbi) {
        btnUbi.disabled = false;
        btnUbi.innerHTML = '📍 Usar mi ubicación';
      }
    }
  },

  async handleCheckIn(turnoId) {
    if (!navigator.geolocation) {
      this.mostrarMensajeError('Tu navegador no soporta geolocalización');
      return;
    }

    const btn = document.getElementById('btn-checkin-turno');
    if (btn) {
      btn.disabled = true;
      btn.innerHTML = '<div class="spinner" style="width: 16px; height: 16px;"></div> Ubicando...';
    }

    try {
      const position = await new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(resolve, reject, {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 0
        });
      });

      if (btn) {
        btn.innerHTML = '<div class="spinner" style="width: 16px; height: 16px;"></div> Registrando...';
      }

      const response = await API.post(`/turnos/${turnoId}/checkin-geo`, {
        latitud: position.coords.latitude,
        longitud: position.coords.longitude
      });

      this.mostrarMensajeExito(`Check-in realizado correctamente\n\nDistancia: ${response.distanciaMetros}m`);

      // Recargar dashboard
      await this.cargarDashboard();

    } catch (error) {
      console.error('Error en check-in:', error);
      this.mostrarMensajeError('Error al registrar el check-in. Asegúrate de estar cerca del centro.');
    } finally {
      if (btn) {
        btn.disabled = false;
        btn.innerHTML = '📍 Hacer check-in';
      }
    }
  },

  handleComoLlegar() {
    if (!this.ubicacionUsuario) {
      this.mostrarMensajeInfo('Primero usa "Usar mi ubicación" para calcular tu posición');
      return;
    }

    // Abrir Google Maps con ruta desde ubicación usuario hasta el centro
    const url = `https://www.google.com/maps/dir/?api=1&origin=${this.ubicacionUsuario.lat},${this.ubicacionUsuario.lng}&destination=${this.ubicacionCentro.lat},${this.ubicacionCentro.lng}&travelmode=walking`;
    window.open(url, '_blank');
  },

  handleConfirmarLlegada() {
    if (this.dashboardData && this.dashboardData.proximoTurno) {
      this.handleCheckIn(this.dashboardData.proximoTurno.id);
    } else {
      this.mostrarMensajeInfo('No tienes ningún turno próximo para confirmar llegada');
    }
  },

  abrirEnGoogleMaps() {
    if (!this.ubicacionUsuario) {
      this.mostrarMensajeInfo('Primero usa "Usar mi ubicación" para obtener tu posición');
      return;
    }

    // Abrir Google Maps en nueva pestaña con ruta desde alumno hasta centro
    const url = `https://www.google.com/maps/dir/?api=1&origin=${this.ubicacionUsuario.lat},${this.ubicacionUsuario.lng}&destination=${this.ubicacionCentro.lat},${this.ubicacionCentro.lng}&travelmode=walking`;
    window.open(url, '_blank');
  },

  // ==================== ACCIONES Y HANDLERS ====================

  handleReservarTurno() {
    this.mostrarMensajePlaceholder('Reservar turno', 'Próximamente podrás:\n• Elegir tipo de trámite\n• Seleccionar fecha y hora\n• Confirmar tu reserva');
  },

  handleAccesoRapido(accion) {
    const mensajes = {
      'turnos': 'Gestión de turnos',
      'checkin': 'Check-in',
      'matricula': 'Matrícula',
      'documentos': 'Documentación',
      'foro': 'Foro académico',
      'studymatch': 'StudyMatch',
      'mercado': 'Mercado interno',
      'eventos': 'Eventos'
    };

    const titulo = mensajes[accion] || 'Funcionalidad';

    // Si es check-in, manejar especialmente
    if (accion === 'checkin') {
      this.handleConfirmarLlegada();
      return;
    }

    this.mostrarMensajePlaceholder(titulo, `${titulo} estará disponible próximamente.`);
  },

  handleRecomendacion(url, titulo) {
    if (url && url !== '#' && url !== 'undefined') {
      window.location.href = url;
    } else {
      this.mostrarMensajePlaceholder('Recomendación', titulo || 'Esta recomendación no tiene una acción asociada.');
    }
  },

  // ==================== UTILIDADES ====================

  calcularDistancia(lat1, lon1, lat2, lon2) {
    const R = 6371e3; // Radio de la Tierra en metros
    const φ1 = lat1 * Math.PI / 180;
    const φ2 = lat2 * Math.PI / 180;
    const Δφ = (lat2 - lat1) * Math.PI / 180;
    const Δλ = (lon2 - lon1) * Math.PI / 180;

    const a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
              Math.cos(φ1) * Math.cos(φ2) *
              Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c; // Distancia en metros
  },

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

  getBadgeColor(estado) {
    const colores = {
      'PENDIENTE': 'warning',
      'EN_CURSO': 'info',
      'COMPLETADO': 'success',
      'CANCELADO': 'error'
    };
    return colores[estado] || 'default';
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

  // ==================== MENSAJES AL USUARIO ====================

  mostrarError(mensaje) {
    const heroMessage = document.getElementById('hero-message');
    if (heroMessage) {
      heroMessage.innerHTML = `<span style="color: rgba(255,255,255,0.9);">⚠️ ${mensaje}</span>`;
    }
  },

  mostrarMensajeExito(mensaje) {
    alert(`✅ ${mensaje}`);
  },

  mostrarMensajeError(mensaje) {
    alert(`❌ ${mensaje}`);
  },

  mostrarMensajeInfo(mensaje) {
    alert(`ℹ️ ${mensaje}`);
  },

  mostrarMensajePlaceholder(titulo, mensaje) {
    alert(`📋 ${titulo}\n\n${mensaje}`);
  }
};

// Inicializar al cargar la página
document.addEventListener('DOMContentLoaded', () => {
  PanelAlumno.init();
});
