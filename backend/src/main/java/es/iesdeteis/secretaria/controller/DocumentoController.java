package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.DocumentoCreateDTO;
import es.iesdeteis.secretaria.dto.DocumentoResponseDTO;
import es.iesdeteis.secretaria.dto.DocumentoRevisionDTO;
import es.iesdeteis.secretaria.model.EstadoDocumento;
import es.iesdeteis.secretaria.service.DocumentoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    // ATRIBUTOS

    private final DocumentoService documentoService;


    // CONSTRUCTOR

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }


    // CREAR DOCUMENTO

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    public DocumentoResponseDTO crearDocumento(@Valid @RequestBody DocumentoCreateDTO dto) {
        return documentoService.crearDocumento(dto);
    }


    // CONSULTAS

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public List<DocumentoResponseDTO> listarTodos() {
        return documentoService.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    public DocumentoResponseDTO obtenerPorId(@PathVariable Long id) {
        return documentoService.obtenerPorId(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    public List<DocumentoResponseDTO> listarPorUsuario(@PathVariable Long usuarioId) {
        return documentoService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/prematricula/{preMatriculaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    public List<DocumentoResponseDTO> listarPorPreMatricula(@PathVariable Long preMatriculaId) {
        return documentoService.listarPorPreMatricula(preMatriculaId);
    }

    @GetMapping("/turno/{turnoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    public List<DocumentoResponseDTO> listarPorTurno(@PathVariable Long turnoId) {
        return documentoService.listarPorTurno(turnoId);
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public List<DocumentoResponseDTO> listarPorEstado(@PathVariable EstadoDocumento estado) {
        return documentoService.listarPorEstado(estado);
    }

    @GetMapping("/mis-documentos")
    @PreAuthorize("hasAnyRole('USUARIO', 'ALUMNO')")
    public List<DocumentoResponseDTO> obtenerMisDocumentos() {
        return documentoService.obtenerMisDocumentos();
    }


    // REVISIÓN DE DOCUMENTOS

    @PutMapping("/{id}/validar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public DocumentoResponseDTO validarDocumento(@PathVariable Long id,
                                                 @Valid @RequestBody DocumentoRevisionDTO dto) {
        return documentoService.validarDocumento(id, dto);
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public DocumentoResponseDTO rechazarDocumento(@PathVariable Long id,
                                                  @Valid @RequestBody DocumentoRevisionDTO dto) {
        return documentoService.rechazarDocumento(id, dto);
    }

    @PutMapping("/{id}/requiere-revision")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public DocumentoResponseDTO marcarRequiereRevision(@PathVariable Long id,
                                                       @Valid @RequestBody DocumentoRevisionDTO dto) {
        return documentoService.marcarRequiereRevision(id, dto);
    }


    // ELIMINAR

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    public void eliminar(@PathVariable Long id) {
        documentoService.eliminar(id);
    }
}