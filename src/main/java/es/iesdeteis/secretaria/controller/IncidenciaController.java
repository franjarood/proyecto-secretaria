package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.IncidenciaCreateDTO;
import es.iesdeteis.secretaria.dto.IncidenciaResponseDTO;
import es.iesdeteis.secretaria.exception.IncidenciaNoEncontradaException;
import es.iesdeteis.secretaria.model.Incidencia;
import es.iesdeteis.secretaria.service.IncidenciaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incidencias")
public class IncidenciaController {

    // ATRIBUTOS

    private final IncidenciaService incidenciaService;


    // CONSTRUCTOR

    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }


    // MÉTODOS

    // Obtener todas las incidencias
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @GetMapping
    public List<IncidenciaResponseDTO> getIncidencias() {
        return incidenciaService.findAll().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // Obtener incidencia por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @GetMapping("/{id}")
    public IncidenciaResponseDTO getIncidenciaById(@PathVariable Long id) {
        Incidencia incidencia = incidenciaService.findById(id)
                .orElseThrow(() -> new IncidenciaNoEncontradaException("Incidencia no encontrada"));

        return convertirAResponseDTO(incidencia);
    }

    // Crear incidencia
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @PostMapping
    public IncidenciaResponseDTO saveIncidencia(@Valid @RequestBody IncidenciaCreateDTO dto) {
        Incidencia incidenciaGuardada = incidenciaService.saveFromDTO(dto);
        return convertirAResponseDTO(incidenciaGuardada);
    }

    // Actualizar incidencia
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PutMapping("/{id}")
    public IncidenciaResponseDTO updateIncidencia(@PathVariable Long id, @Valid @RequestBody Incidencia incidencia) {
        Incidencia incidenciaActualizada = incidenciaService.update(id, incidencia);
        return convertirAResponseDTO(incidenciaActualizada);
    }

    // Eliminar incidencia
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteIncidencia(@PathVariable Long id) {
        incidenciaService.deleteById(id);
    }


    // MÉTODOS AUXILIARES

    // Convertir entidad Incidencia a DTO de respuesta
    private IncidenciaResponseDTO convertirAResponseDTO(Incidencia incidencia) {
        return new IncidenciaResponseDTO(
                incidencia.getId(),
                incidencia.getTipo().name(),
                incidencia.getDescripcion(),
                incidencia.getFecha(),
                incidencia.getResuelta(),
                incidencia.getAccionTomada(),
                incidencia.getTurno().getId()
        );
    }
}