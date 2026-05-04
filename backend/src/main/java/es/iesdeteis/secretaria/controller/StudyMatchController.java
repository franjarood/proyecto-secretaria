package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.AnuncioAyudaRequestDTO;
import es.iesdeteis.secretaria.dto.AnuncioAyudaResponseDTO;
import es.iesdeteis.secretaria.model.TipoAnuncioAyuda;
import es.iesdeteis.secretaria.service.StudyMatchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studymatch")
public class StudyMatchController {

    private final StudyMatchService studyMatchService;

    public StudyMatchController(StudyMatchService studyMatchService) {
        this.studyMatchService = studyMatchService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<List<AnuncioAyudaResponseDTO>> listarAnunciosActivos() {
        return ResponseEntity.ok(studyMatchService.listarAnunciosActivos());
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<List<AnuncioAyudaResponseDTO>> listarPorTipo(@PathVariable TipoAnuncioAyuda tipo) {
        return ResponseEntity.ok(studyMatchService.listarPorTipo(tipo));
    }

    @GetMapping("/mis-anuncios")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<List<AnuncioAyudaResponseDTO>> listarMisAnuncios() {
        return ResponseEntity.ok(studyMatchService.listarMisAnuncios());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<AnuncioAyudaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(studyMatchService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<AnuncioAyudaResponseDTO> crear(@Valid @RequestBody AnuncioAyudaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studyMatchService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<AnuncioAyudaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AnuncioAyudaRequestDTO dto) {
        return ResponseEntity.ok(studyMatchService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/cerrar")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<Void> cerrar(@PathVariable Long id) {
        studyMatchService.cerrar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        studyMatchService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
