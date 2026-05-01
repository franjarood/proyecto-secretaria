package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.AsistenteRecomendacionDTO;

import java.util.List;

public interface AsistenteService {

    List<AsistenteRecomendacionDTO> obtenerRecomendacionesUsuario(Long usuarioId);
}

