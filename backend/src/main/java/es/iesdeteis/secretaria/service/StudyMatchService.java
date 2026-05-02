package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.AnuncioAyudaRequestDTO;
import es.iesdeteis.secretaria.dto.AnuncioAyudaResponseDTO;
import es.iesdeteis.secretaria.model.TipoAnuncioAyuda;

import java.util.List;

public interface StudyMatchService {

    List<AnuncioAyudaResponseDTO> listarAnunciosActivos();

    List<AnuncioAyudaResponseDTO> listarPorTipo(TipoAnuncioAyuda tipo);

    List<AnuncioAyudaResponseDTO> listarMisAnuncios();

    AnuncioAyudaResponseDTO obtenerPorId(Long id);

    AnuncioAyudaResponseDTO crear(AnuncioAyudaRequestDTO dto);

    AnuncioAyudaResponseDTO actualizar(Long id, AnuncioAyudaRequestDTO dto);

    void cerrar(Long id);

    void eliminar(Long id);
}
