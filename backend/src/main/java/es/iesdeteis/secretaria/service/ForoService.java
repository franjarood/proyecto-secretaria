package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.RespuestaForoRequestDTO;
import es.iesdeteis.secretaria.dto.RespuestaForoResponseDTO;
import es.iesdeteis.secretaria.dto.TemaForoRequestDTO;
import es.iesdeteis.secretaria.dto.TemaForoResponseDTO;

import java.util.List;

public interface ForoService {

    List<TemaForoResponseDTO> listarTemas();

    TemaForoResponseDTO obtenerTema(Long id);

    List<TemaForoResponseDTO> listarMisTemas();

    TemaForoResponseDTO crearTema(TemaForoRequestDTO dto);

    TemaForoResponseDTO actualizarTema(Long id, TemaForoRequestDTO dto);

    void eliminarTema(Long id);

    RespuestaForoResponseDTO crearRespuesta(Long temaId, RespuestaForoRequestDTO dto);

    RespuestaForoResponseDTO marcarMejorRespuesta(Long respuestaId);

    void eliminarRespuesta(Long respuestaId);
}

