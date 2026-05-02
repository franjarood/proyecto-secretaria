package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.AnuncioMercadoRequestDTO;
import es.iesdeteis.secretaria.dto.AnuncioMercadoResponseDTO;
import es.iesdeteis.secretaria.model.EstadoAnuncioMercado;
import es.iesdeteis.secretaria.service.MercadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mercado")
public class MercadoController {

    private final MercadoService mercadoService;

    public MercadoController(MercadoService mercadoService) {
        this.mercadoService = mercadoService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AnuncioMercadoResponseDTO>> listarDisponibles() {
        return ResponseEntity.ok(mercadoService.listarDisponibles());
    }

    @GetMapping("/mis-anuncios")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AnuncioMercadoResponseDTO>> listarMisAnuncios() {
        return ResponseEntity.ok(mercadoService.listarMisAnuncios());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioMercadoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mercadoService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioMercadoResponseDTO> crear(@Valid @RequestBody AnuncioMercadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mercadoService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioMercadoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AnuncioMercadoRequestDTO dto) {
        return ResponseEntity.ok(mercadoService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioMercadoResponseDTO> cambiarEstado(@PathVariable Long id, @RequestParam EstadoAnuncioMercado estado) {
        return ResponseEntity.ok(mercadoService.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mercadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
