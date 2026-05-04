package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.model.Ciclo;
import es.iesdeteis.secretaria.service.CicloService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ciclos")
public class CicloController {

    private final CicloService cicloService;

    public CicloController(CicloService cicloService) {
        this.cicloService = cicloService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public List<Ciclo> findAll() {
        return cicloService.findAll();
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public List<Ciclo> findActivos() {
        return cicloService.findActivos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public Ciclo findById(@PathVariable Long id) {
        return cicloService.findById(id);
    }

    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public Ciclo findByCodigo(@PathVariable String codigo) {
        return cicloService.findByCodigo(codigo);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Ciclo create(@Valid @RequestBody Ciclo ciclo) {
        return cicloService.save(ciclo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Ciclo update(@PathVariable Long id, @Valid @RequestBody Ciclo ciclo) {
        return cicloService.update(id, ciclo);
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        cicloService.desactivar(id);
    }
}
