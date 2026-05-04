package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.model.DocumentoRequerido;
import es.iesdeteis.secretaria.service.DocumentoRequeridoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documentos-requeridos")
public class DocumentoRequeridoController {

    private final DocumentoRequeridoService documentoRequeridoService;

    public DocumentoRequeridoController(DocumentoRequeridoService documentoRequeridoService) {
        this.documentoRequeridoService = documentoRequeridoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public List<DocumentoRequerido> findAll() {
        return documentoRequeridoService.findAll();
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public List<DocumentoRequerido> findActivos() {
        return documentoRequeridoService.findActivos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public DocumentoRequerido findById(@PathVariable Long id) {
        return documentoRequeridoService.findById(id);
    }

    @GetMapping("/por-tipo-matricula/{tipoMatriculaId}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public List<DocumentoRequerido> findByTipoMatriculaId(@PathVariable Long tipoMatriculaId) {
        return documentoRequeridoService.findByTipoMatriculaId(tipoMatriculaId);
    }

    @GetMapping("/por-curso/{cursoId}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public List<DocumentoRequerido> findByCursoId(@PathVariable Long cursoId) {
        return documentoRequeridoService.findByCursoId(cursoId);
    }

    @GetMapping("/por-ciclo/{cicloId}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO', 'SECRETARIA', 'ADMIN')")
    public List<DocumentoRequerido> findByCicloId(@PathVariable Long cicloId) {
        return documentoRequeridoService.findByCicloId(cicloId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoRequerido create(@Valid @RequestBody DocumentoRequerido documentoRequerido) {
        return documentoRequeridoService.save(documentoRequerido);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DocumentoRequerido update(@PathVariable Long id, @Valid @RequestBody DocumentoRequerido documentoRequerido) {
        return documentoRequeridoService.update(id, documentoRequerido);
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        documentoRequeridoService.desactivar(id);
    }
}
