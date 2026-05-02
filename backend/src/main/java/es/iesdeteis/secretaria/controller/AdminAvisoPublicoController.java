package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.AvisoPublicoRequestDTO;
import es.iesdeteis.secretaria.dto.AvisoPublicoResponseDTO;
import es.iesdeteis.secretaria.service.AvisoPublicoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/avisos")
public class AdminAvisoPublicoController {

    // ATRIBUTOS

    private final AvisoPublicoService avisoPublicoService;


    // CONSTRUCTOR

    public AdminAvisoPublicoController(AvisoPublicoService avisoPublicoService) {
        this.avisoPublicoService = avisoPublicoService;
    }


    // MÉTODOS

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @GetMapping
    public List<AvisoPublicoResponseDTO> listarTodos() {
        return avisoPublicoService.listarTodos();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @GetMapping("/{id}")
    public AvisoPublicoResponseDTO obtenerPorId(@PathVariable Long id) {
        return avisoPublicoService.obtenerPorId(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PostMapping
    public AvisoPublicoResponseDTO crear(@Valid @RequestBody AvisoPublicoRequestDTO dto) {
        return avisoPublicoService.crear(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PutMapping("/{id}")
    public AvisoPublicoResponseDTO actualizar(@PathVariable Long id,
                                              @Valid @RequestBody AvisoPublicoRequestDTO dto) {
        return avisoPublicoService.actualizar(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        avisoPublicoService.eliminar(id);
    }
}

