package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.exception.IncidenciaNoEncontradaException;
import es.iesdeteis.secretaria.model.Incidencia;
import es.iesdeteis.secretaria.service.IncidenciaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    // Obtener todas las incidencias
    @GetMapping
    public List<Incidencia> getIncidencias() {
        return incidenciaService.findAll();
    }

    // Obtener incidencia por ID
    @GetMapping("/{id}")
    public Incidencia getIncidenciaById(@PathVariable Long id) {
        return incidenciaService.findById(id)
                .orElseThrow(() -> new IncidenciaNoEncontradaException("Incidencia no encontrada"));
    }

    // Crear incidencia
    @PostMapping
    public Incidencia saveIncidencia(@Valid @RequestBody Incidencia incidencia) {
        return incidenciaService.save(incidencia);
    }

    // Actualizar incidencia
    @PutMapping("/{id}")
    public Incidencia updateIncidencia(@PathVariable Long id, @Valid @RequestBody Incidencia incidencia) {
        return incidenciaService.update(id, incidencia);
    }

    // Eliminar incidencia
    @DeleteMapping("/{id}")
    public void deleteIncidencia(@PathVariable Long id) {
        incidenciaService.deleteById(id);
    }
}