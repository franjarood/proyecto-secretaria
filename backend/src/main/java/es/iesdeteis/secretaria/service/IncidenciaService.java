package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.IncidenciaCreateDTO;
import es.iesdeteis.secretaria.model.Incidencia;
import es.iesdeteis.secretaria.model.TipoIncidencia;

import java.util.List;
import java.util.Optional;

public interface IncidenciaService {

    // Obtener todas las incidencias
    List<Incidencia> findAll();

    // Buscar incidencia por ID
    Optional<Incidencia> findById(Long id);

    // Crear nueva incidencia
    Incidencia save(Incidencia incidencia);

    // Crear nueva incidencia desde DTO
    Incidencia saveFromDTO(IncidenciaCreateDTO dto);

    // Actualizar incidencia existente
    Incidencia update(Long id, Incidencia incidencia);

    // Eliminar incidencia
    void deleteById(Long id);

    List<Incidencia> findByTipo(TipoIncidencia tipo);

    Incidencia marcarComoResuelta(Long id);
}