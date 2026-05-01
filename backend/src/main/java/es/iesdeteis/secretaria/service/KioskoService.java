package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.EstadoTurnoResponseDTO;
import es.iesdeteis.secretaria.dto.KioskoPantallaDTO;

import java.util.List;

public interface KioskoService {

    KioskoPantallaDTO obtenerPantalla();

    List<String> obtenerTramitesDisponibles();

    EstadoTurnoResponseDTO obtenerEstadoTurno(Long idTurno);
}

