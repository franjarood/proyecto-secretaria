package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.ClimaResponseDTO;

/**
 * Servicio para obtener información del clima.
 */
public interface ClimaService {

    /**
     * Obtiene el clima actual de la ubicación configurada.
     */
    ClimaResponseDTO obtenerClimaActual();


    ClimaResponseDTO obtenerClimaPorCoordenadas(double lat, double lon);
}
