package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.AnuncioMercadoRequestDTO;
import es.iesdeteis.secretaria.dto.AnuncioMercadoResponseDTO;
import es.iesdeteis.secretaria.model.EstadoAnuncioMercado;

import java.util.List;

public interface MercadoService {

    List<AnuncioMercadoResponseDTO> listarDisponibles();

    List<AnuncioMercadoResponseDTO> listarMisAnuncios();

    AnuncioMercadoResponseDTO obtenerPorId(Long id);

    AnuncioMercadoResponseDTO crear(AnuncioMercadoRequestDTO dto);

    AnuncioMercadoResponseDTO actualizar(Long id, AnuncioMercadoRequestDTO dto);

    AnuncioMercadoResponseDTO cambiarEstado(Long id, EstadoAnuncioMercado nuevoEstado);

    void eliminar(Long id);
}
