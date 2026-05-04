// ============================================
// COMPONENTE ASISTENTE INTELIGENTE
// ============================================

const Asistente = {

  /**
   * Inicializa el asistente con recomendaciones
   * @param {Array} recomendaciones - [{icono, texto, accion?}]
   */
  init(recomendaciones) {
    this.render(recomendaciones);
  },

  /**
   * Renderiza las recomendaciones
   */
  render(recomendaciones) {
    const listEl = document.getElementById('assistantList');
    if (!listEl) return;

    if (!recomendaciones || recomendaciones.length === 0) {
      listEl.innerHTML = '<div class="card-assistant-body">No hay recomendaciones por ahora.</div>';
      return;
    }

    listEl.innerHTML = recomendaciones.map(rec => `
      <div class="card-assistant-item" data-accion="${rec.accion || ''}">
        <div class="card-assistant-item-icon">${rec.icono}</div>
        <div class="card-assistant-item-text">${rec.texto}</div>
        <svg class="card-assistant-item-arrow" viewBox="0 0 24 24" fill="currentColor">
          <path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/>
        </svg>
      </div>
    `).join('');

    // Event listeners
    listEl.querySelectorAll('.card-assistant-item').forEach(item => {
      item.addEventListener('click', () => {
        const accion = item.dataset.accion;
        if (accion) {
          console.log('Asistente acción:', accion);
          // Ejecutar acción según el tipo
          this.ejecutarAccion(accion);
        }
      });
    });
  },

  /**
   * Ejecuta una acción del asistente
   */
  ejecutarAccion(accion) {
    // Lógica básica de acciones
    if (accion.startsWith('http')) {
      window.location.href = accion;
    } else {
      console.log('Acción no implementada:', accion);
    }
  },

  /**
   * Actualiza las recomendaciones
   */
  actualizar(nuevasRecomendaciones) {
    this.render(nuevasRecomendaciones);
  }
};

window.Asistente = Asistente;
