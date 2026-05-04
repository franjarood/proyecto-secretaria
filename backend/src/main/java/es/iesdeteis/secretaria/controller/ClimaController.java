package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.ClimaResponseDTO;
import es.iesdeteis.secretaria.service.ClimaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para gestionar información del clima
 * Endpoint proxy que oculta la API key de OpenWeather
 */
@RestController
@RequestMapping("/clima")
public class ClimaController {

    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {
        this.climaService = climaService;
    }

    /**
     * GET /clima/actual
     * Obtiene el clima actual de la ubicación del centro (IES de Teis)
     */
    @GetMapping("/actual")
    public ResponseEntity<ClimaResponseDTO> obtenerClimaActual() {
        ClimaResponseDTO clima = climaService.obtenerClimaActual();
        return ResponseEntity.ok(clima);
    }
}
