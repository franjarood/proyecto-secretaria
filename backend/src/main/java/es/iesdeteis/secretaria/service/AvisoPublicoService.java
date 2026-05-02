package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.AvisoPublicoRequestDTO;
import es.iesdeteis.secretaria.dto.AvisoPublicoResponseDTO;

import java.util.List;

public interface AvisoPublicoService {

    // =========================
    // CONSULTAS PÚBLICAS
    // =========================

    List<AvisoPublicoResponseDTO> listarVisiblesVigentes();

    List<AvisoPublicoResponseDTO> listarDestacadosVisiblesVigentes();


    // =========================
    // ADMINISTRACIÓN
    // =========================

    List<AvisoPublicoResponseDTO> listarTodos();

    AvisoPublicoResponseDTO obtenerPorId(Long id);

    AvisoPublicoResponseDTO crear(AvisoPublicoRequestDTO dto);

    AvisoPublicoResponseDTO actualizar(Long id, AvisoPublicoRequestDTO dto);

    void eliminar(Long id);
}

