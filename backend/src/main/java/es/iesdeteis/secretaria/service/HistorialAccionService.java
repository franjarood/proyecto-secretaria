package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.HistorialAccion;

import java.util.List;
import java.util.Optional;

public interface HistorialAccionService {

    // Obtener todo el historial
    List<HistorialAccion> findAll();

    // Buscar acción por ID
    Optional<HistorialAccion> findById(Long id);

    // Crear nueva acción
    HistorialAccion save(HistorialAccion historialAccion);

    // Actualizar acción existente
    HistorialAccion update(Long id, HistorialAccion historialAccion);

    // Eliminar acción
    void deleteById(Long id);

    // Obtener historial de un turno concreto
    List<HistorialAccion> findByTurnoId(Long turnoId);
}