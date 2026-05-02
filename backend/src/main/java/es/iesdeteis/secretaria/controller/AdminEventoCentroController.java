package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.EventoCentroRequestDTO;
import es.iesdeteis.secretaria.dto.EventoCentroResponseDTO;
import es.iesdeteis.secretaria.service.EventoCentroService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/eventos")
public class AdminEventoCentroController {

    // ATRIBUTOS

    private final EventoCentroService eventoCentroService;


    // CONSTRUCTOR

    public AdminEventoCentroController(EventoCentroService eventoCentroService) {
        this.eventoCentroService = eventoCentroService;
    }


    // MÉTODOS

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @GetMapping
    public List<EventoCentroResponseDTO> listarTodos() {
        return eventoCentroService.listarTodos();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @GetMapping("/{id}")
    public EventoCentroResponseDTO obtenerPorId(@PathVariable Long id) {
        return eventoCentroService.obtenerPorId(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PostMapping
    public EventoCentroResponseDTO crear(@Valid @RequestBody EventoCentroRequestDTO dto) {
        return eventoCentroService.crear(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PutMapping("/{id}")
    public EventoCentroResponseDTO actualizar(@PathVariable Long id,
                                              @Valid @RequestBody EventoCentroRequestDTO dto) {
        return eventoCentroService.actualizar(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        eventoCentroService.eliminar(id);
    }
}

