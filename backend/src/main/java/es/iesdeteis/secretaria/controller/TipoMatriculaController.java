package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.model.TipoMatricula;
import es.iesdeteis.secretaria.service.TipoMatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-matricula")
public class TipoMatriculaController {

    private final TipoMatriculaService tipoMatriculaService;

    public TipoMatriculaController(TipoMatriculaService tipoMatriculaService) {
        this.tipoMatriculaService = tipoMatriculaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public List<TipoMatricula> findAll() {
        return tipoMatriculaService.findAll();
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public List<TipoMatricula> findActivos() {
        return tipoMatriculaService.findActivos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public TipoMatricula findById(@PathVariable Long id) {
        return tipoMatriculaService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public TipoMatricula create(@Valid @RequestBody TipoMatricula tipoMatricula) {
        return tipoMatriculaService.save(tipoMatricula);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TipoMatricula update(@PathVariable Long id, @Valid @RequestBody TipoMatricula tipoMatricula) {
        return tipoMatriculaService.update(id, tipoMatricula);
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        tipoMatriculaService.desactivar(id);
    }
}
