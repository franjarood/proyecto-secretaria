/* ============================================
   MÓDULO DE MAPA - Google Maps JavaScript API
   Gestión de mapa incrustado con ruta al centro
   ============================================ */

const MapaCentro = {
  mapa: null,
  marcadorCentro: null,
  marcadorAlumno: null,
  directionsService: null,
  directionsRenderer: null,
  mapaDisponible: false,
  ubicacionAlumno: null,

  /**
   * Inicializa el mapa de Google Maps
   * @param {string} containerId - ID del contenedor HTML
   */
  async inicializar(containerId) {
    const container = document.getElementById(containerId);
    if (!container) {
      console.error('Contenedor de mapa no encontrado:', containerId);
      return false;
    }

    // Verificar que Google Maps esté cargado
    if (typeof google === 'undefined' || !google.maps) {
      console.warn('Google Maps no está cargado. Mostrando fallback.');
      this.mostrarFallback(container);
      return false;
    }

    try {
      const centro = CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO;

      // Crear mapa centrado en el centro educativo
      this.mapa = new google.maps.Map(container, {
        center: { lat: centro.lat, lng: centro.lng },
        zoom: 15,
        mapTypeControl: false,
        streetViewControl: false,
        fullscreenControl: false,
        styles: this.obtenerEstilosMapa()
      });

      // Añadir marcador del centro educativo
      this.marcadorCentro = new google.maps.Marker({
        position: { lat: centro.lat, lng: centro.lng },
        map: this.mapa,
        title: centro.nombre,
        icon: {
          path: google.maps.SymbolPath.CIRCLE,
          scale: 10,
          fillColor: '#4f46e5',
          fillOpacity: 1,
          strokeColor: '#ffffff',
          strokeWeight: 3
        }
      });

      // Info window para el centro
      const infoWindow = new google.maps.InfoWindow({
        content: `<div style="padding: 8px; font-family: system-ui;">
          <strong style="color: #4f46e5;">${centro.nombre}</strong><br>
          <span style="font-size: 0.875rem; color: #6b7280;">Centro educativo</span>
        </div>`
      });

      this.marcadorCentro.addListener('click', () => {
        infoWindow.open(this.mapa, this.marcadorCentro);
      });

      // Inicializar servicios de direcciones
      this.directionsService = new google.maps.DirectionsService();
      this.directionsRenderer = new google.maps.DirectionsRenderer({
        map: this.mapa,
        suppressMarkers: false,
        polylineOptions: {
          strokeColor: '#4f46e5',
          strokeWeight: 4,
          strokeOpacity: 0.8
        }
      });

      // Dibujar círculo del radio de check-in
      new google.maps.Circle({
        map: this.mapa,
        center: { lat: centro.lat, lng: centro.lng },
        radius: CONFIG.GOOGLE_MAPS.RADIO_CHECKIN_METROS,
        fillColor: '#10b981',
        fillOpacity: 0.1,
        strokeColor: '#10b981',
        strokeOpacity: 0.4,
        strokeWeight: 2
      });

      this.mapaDisponible = true;
      console.log('Mapa inicializado correctamente');
      return true;

    } catch (error) {
      console.error('Error al inicializar mapa:', error);
      this.mostrarFallback(container);
      return false;
    }
  },

  /**
   * Actualiza la ubicación del alumno en el mapa
   * @param {number} lat - Latitud del alumno
   * @param {number} lng - Longitud del alumno
   * @param {number} distanciaMetros - Distancia calculada al centro
   */
  actualizarUbicacionAlumno(lat, lng, distanciaMetros) {
    if (!this.mapaDisponible || !this.mapa) {
      console.warn('Mapa no disponible para actualizar ubicación');
      return;
    }

    this.ubicacionAlumno = { lat, lng };

    // Eliminar marcador anterior del alumno si existe
    if (this.marcadorAlumno) {
      this.marcadorAlumno.setMap(null);
    }

    // Crear nuevo marcador del alumno
    this.marcadorAlumno = new google.maps.Marker({
      position: { lat, lng },
      map: this.mapa,
      title: 'Tu ubicación',
      icon: {
        path: google.maps.SymbolPath.CIRCLE,
        scale: 8,
        fillColor: '#3b82f6',
        fillOpacity: 1,
        strokeColor: '#ffffff',
        strokeWeight: 2
      }
    });

    // Info window para el alumno
    const dentroRadio = distanciaMetros < CONFIG.GOOGLE_MAPS.RADIO_CHECKIN_METROS;
    const infoAlumno = new google.maps.InfoWindow({
      content: `<div style="padding: 8px; font-family: system-ui;">
        <strong style="color: #3b82f6;">Tu ubicación</strong><br>
        <span style="font-size: 0.875rem; color: #6b7280;">
          ${distanciaMetros >= 1000 ? (distanciaMetros / 1000).toFixed(1) + ' km' : Math.round(distanciaMetros) + ' m'} del centro
        </span><br>
        <span style="font-size: 0.875rem; color: ${dentroRadio ? '#10b981' : '#f59e0b'};">
          ${dentroRadio ? '✅ Dentro del radio' : '⚠️ Fuera del radio'}
        </span>
      </div>`
    });

    this.marcadorAlumno.addListener('click', () => {
      infoAlumno.open(this.mapa, this.marcadorAlumno);
    });

    // Ajustar vista para mostrar ambos marcadores
    const bounds = new google.maps.LatLngBounds();
    bounds.extend({ lat: CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO.lat, lng: CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO.lng });
    bounds.extend({ lat, lng });
    this.mapa.fitBounds(bounds);

    // Calcular y mostrar ruta
    this.mostrarRuta(lat, lng);
  },

  /**
   * Calcula y muestra la ruta desde el alumno hasta el centro
   * @param {number} lat - Latitud del alumno
   * @param {number} lng - Longitud del alumno
   */
  mostrarRuta(lat, lng) {
    if (!this.directionsService || !this.directionsRenderer) {
      console.warn('Servicios de direcciones no disponibles');
      return;
    }

    const origen = { lat, lng };
    const destino = {
      lat: CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO.lat,
      lng: CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO.lng
    };

    const request = {
      origin: origen,
      destination: destino,
      travelMode: google.maps.TravelMode.WALKING
    };

    this.directionsService.route(request, (result, status) => {
      if (status === google.maps.DirectionsStatus.OK) {
        this.directionsRenderer.setDirections(result);
        console.log('Ruta calculada correctamente');
      } else {
        console.warn('No se pudo calcular la ruta:', status);
      }
    });
  },

  /**
   * Limpia la ruta del mapa
   */
  limpiarRuta() {
    if (this.directionsRenderer) {
      this.directionsRenderer.setDirections({ routes: [] });
    }

    if (this.marcadorAlumno) {
      this.marcadorAlumno.setMap(null);
      this.marcadorAlumno = null;
    }

    this.ubicacionAlumno = null;
  },

  /**
   * Centra el mapa en el centro educativo
   */
  centrarEnCentro() {
    if (this.mapa) {
      const centro = CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO;
      this.mapa.setCenter({ lat: centro.lat, lng: centro.lng });
      this.mapa.setZoom(15);
    }
  },

  /**
   * Muestra fallback cuando el mapa no está disponible
   */
  mostrarFallback(container) {
    container.innerHTML = `
      <div style="height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(147, 51, 234, 0.1)); border-radius: var(--border-radius-lg); border: 2px dashed rgba(79, 70, 229, 0.3); padding: 32px; text-align: center;">
        <div style="font-size: 3rem; margin-bottom: 16px; opacity: 0.5;">🗺️</div>
        <p style="font-weight: 600; color: var(--text-primary); margin-bottom: 8px;">Mapa no disponible</p>
        <p style="font-size: 0.875rem; color: var(--text-secondary); margin-bottom: 16px;">
          Configura tu API key de Google Maps en config.js
        </p>
        <p style="font-size: 0.75rem; color: var(--text-secondary); opacity: 0.7;">
          Puedes seguir usando "Usar mi ubicación" para calcular distancia
        </p>
      </div>
    `;
    this.mapaDisponible = false;
  },

  /**
   * Estilos personalizados para el mapa (tema claro/limpio)
   */
  obtenerEstilosMapa() {
    return [
      {
        featureType: 'poi',
        elementType: 'labels',
        stylers: [{ visibility: 'off' }]
      },
      {
        featureType: 'transit',
        elementType: 'labels',
        stylers: [{ visibility: 'off' }]
      }
    ];
  },

  /**
   * Verifica si el mapa está disponible y funcionando
   */
  estaDisponible() {
    return this.mapaDisponible && this.mapa !== null;
  }
};

// Hacer disponible globalmente
window.MapaCentro = MapaCentro;
