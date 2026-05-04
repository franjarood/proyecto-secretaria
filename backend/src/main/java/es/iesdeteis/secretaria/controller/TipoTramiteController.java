package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.service.TipoTramiteService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tipos-tramite")
public class TipoTramiteController {

    private final TipoTramiteService tipoTramiteService;

    public TipoTramiteController(TipoTramiteService tipoTramiteService) {
        this.tipoTramiteService = tipoTramiteService;
    }

    // Obtener todos los tipos de trámite
    @GetMapping
    public List<TipoTramite> findAll() {
        return tipoTramiteService.findAll();
    }

    // Obtener por ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    @GetMapping("/{id}")
    public Optional<TipoTramite> findById(@PathVariable Long id) {
        return tipoTramiteService.findById(id);
    }

    // Crear tipo de trámite
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public TipoTramite save(@Valid @RequestBody TipoTramite tipoTramite) {
        return tipoTramiteService.save(tipoTramite);
    }

    // Actualizar tipo de trámite
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public TipoTramite update(@PathVariable Long id, @Valid @RequestBody TipoTramite tipoTramite) {
        return tipoTramiteService.update(id, tipoTramite);
    }

    // Eliminar tipo de trámite
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        tipoTramiteService.deleteById(id);
    }
}