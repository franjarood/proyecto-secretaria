package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.CentroInfoRequestDTO;
import es.iesdeteis.secretaria.dto.CentroInfoResponseDTO;
import es.iesdeteis.secretaria.dto.TipoTramitePublicoDTO;

import java.util.List;

public interface CentroInfoService {

    // Público: obtener info del centro activo
    CentroInfoResponseDTO obtenerCentroActivo();

    // Público: trámites destacados
    List<TipoTramitePublicoDTO> obtenerTramitesDestacados();

    // Admin: obtener todas las configuraciones
    List<CentroInfoResponseDTO> findAll();

    // Admin: crear
    CentroInfoResponseDTO create(CentroInfoRequestDTO dto);

    // Admin: actualizar
    CentroInfoResponseDTO update(Long id, CentroInfoRequestDTO dto);
}

