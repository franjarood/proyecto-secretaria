package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.EventoCentroRequestDTO;
import es.iesdeteis.secretaria.dto.EventoCentroResponseDTO;

import java.util.List;

public interface EventoCentroService {

    // Público
    List<EventoCentroResponseDTO> listarPublicosVisibles();

    List<EventoCentroResponseDTO> listarPublicosProximos();

    // Usuario autenticado
    List<EventoCentroResponseDTO> listarMisEventos();

    // Admin
    List<EventoCentroResponseDTO> listarTodos();

    EventoCentroResponseDTO obtenerPorId(Long id);

    EventoCentroResponseDTO crear(EventoCentroRequestDTO dto);

    EventoCentroResponseDTO actualizar(Long id, EventoCentroRequestDTO dto);

    void eliminar(Long id);
}

