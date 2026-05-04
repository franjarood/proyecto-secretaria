package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.EstadoPreMatriculaDTO;
import es.iesdeteis.secretaria.exception.PreMatriculaNoEncontradaException;
import es.iesdeteis.secretaria.model.PreMatricula;
import es.iesdeteis.secretaria.service.PreMatriculaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prematriculas")
public class PreMatriculaController {

    private final PreMatriculaService preMatriculaService;

    public PreMatriculaController(PreMatriculaService preMatriculaService) {
        this.preMatriculaService = preMatriculaService;
    }

    // Obtener todas
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @GetMapping
    public List<PreMatricula> getAll() {
        return preMatriculaService.findAll();
    }

    // Obtener por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @GetMapping("/{id}")
    public PreMatricula getById(@PathVariable Long id) {
        return preMatriculaService.findById(id)
                .orElseThrow(() -> new PreMatriculaNoEncontradaException("PreMatricula no encontrada"));
    }

    // Crear
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'ADMIN', 'SECRETARIA')")
    @PostMapping
    public PreMatricula create(@Valid @RequestBody PreMatricula preMatricula) {
        return preMatriculaService.save(preMatricula);
    }

    // Actualizar
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PutMapping("/{id}")
    public PreMatricula update(@PathVariable Long id, @Valid @RequestBody PreMatricula preMatricula) {
        return preMatriculaService.update(id, preMatricula);
    }

    // Eliminar
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        preMatriculaService.deleteById(id);
    }

    // Cambiar estado
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PutMapping("/{id}/estado")
    public PreMatricula cambiarEstado(@PathVariable Long id,
                                      @RequestBody @Valid EstadoPreMatriculaDTO dto) {

        return preMatriculaService.cambiarEstado(id, dto.getEstado());
    }
}