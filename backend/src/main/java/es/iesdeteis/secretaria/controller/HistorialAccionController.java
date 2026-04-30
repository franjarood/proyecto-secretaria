package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.exception.HistorialAccionNoEncontradaException;
import es.iesdeteis.secretaria.model.HistorialAccion;
import es.iesdeteis.secretaria.service.HistorialAccionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historial")
public class HistorialAccionController {

    private final HistorialAccionService historialAccionService;

    public HistorialAccionController(HistorialAccionService historialAccionService) {
        this.historialAccionService = historialAccionService;
    }

    // Obtener todo el historial
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @GetMapping
    public List<HistorialAccion> getHistorial() {
        return historialAccionService.findAll();
    }

    // Obtener acción por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @GetMapping("/{id}")
    public HistorialAccion getHistorialById(@PathVariable Long id) {
        return historialAccionService.findById(id)
                .orElseThrow(() -> new HistorialAccionNoEncontradaException("Historial no encontrado"));
    }

    // Crear acción
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @PostMapping
    public HistorialAccion saveHistorial(@Valid @RequestBody HistorialAccion historialAccion) {
        return historialAccionService.save(historialAccion);
    }

    // Actualizar acción
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public HistorialAccion updateHistorial(@PathVariable Long id, @Valid @RequestBody HistorialAccion historialAccion) {
        return historialAccionService.update(id, historialAccion);
    }

    // Eliminar acción
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteHistorial(@PathVariable Long id) {
        historialAccionService.deleteById(id);
    }

    // Obtener historial de un turno concreto
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @GetMapping("/turno/{id}")
    public List<HistorialAccion> getHistorialPorTurno(@PathVariable Long id) {
        return historialAccionService.findByTurnoId(id);
    }
}