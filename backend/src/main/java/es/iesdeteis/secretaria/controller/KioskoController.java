package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.EstadoTurnoResponseDTO;
import es.iesdeteis.secretaria.dto.KioskoPantallaDTO;
import es.iesdeteis.secretaria.service.KioskoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kiosko")
public class KioskoController {

    private final KioskoService kioskoService;

    public KioskoController(KioskoService kioskoService) {
        this.kioskoService = kioskoService;
    }

    // Opción C: login con rol del centro (CONSERJE / personal autorizado)
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @GetMapping("/pantalla")
    public KioskoPantallaDTO pantalla() {
        return kioskoService.obtenerPantalla();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @GetMapping("/tramites")
    public List<String> tramites() {
        return kioskoService.obtenerTramitesDisponibles();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @GetMapping("/estado-turno/{id}")
    public EstadoTurnoResponseDTO estadoTurno(@PathVariable Long id) {
        return kioskoService.obtenerEstadoTurno(id);
    }
}

