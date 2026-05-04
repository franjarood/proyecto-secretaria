package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.model.Curso;
import es.iesdeteis.secretaria.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public List<Curso> findAll() {
        return cursoService.findAll();
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public List<Curso> findActivos() {
        return cursoService.findActivos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public Curso findById(@PathVariable Long id) {
        return cursoService.findById(id);
    }

    @GetMapping("/ciclo/{cicloId}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public List<Curso> findByCicloId(@PathVariable Long cicloId) {
        return cursoService.findByCicloId(cicloId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Curso create(@Valid @RequestBody Curso curso) {
        return cursoService.save(curso);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Curso update(@PathVariable Long id, @Valid @RequestBody Curso curso) {
        return cursoService.update(id, curso);
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        cursoService.desactivar(id);
    }
}
