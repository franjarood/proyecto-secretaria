package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.CentroInfoRequestDTO;
import es.iesdeteis.secretaria.dto.CentroInfoResponseDTO;
import es.iesdeteis.secretaria.service.CentroInfoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/centro")
public class AdminCentroController {

    // ATRIBUTOS

    private final CentroInfoService centroInfoService;


    // CONSTRUCTOR

    public AdminCentroController(CentroInfoService centroInfoService) {
        this.centroInfoService = centroInfoService;
    }


    // MÉTODOS

    @GetMapping("/info")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CentroInfoResponseDTO> findAll() {
        return centroInfoService.findAll();
    }

    @PostMapping("/info")
    @PreAuthorize("hasRole('ADMIN')")
    public CentroInfoResponseDTO create(@Valid @RequestBody CentroInfoRequestDTO dto) {
        return centroInfoService.create(dto);
    }

    @PutMapping("/info/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CentroInfoResponseDTO update(@PathVariable Long id, @Valid @RequestBody CentroInfoRequestDTO dto) {
        return centroInfoService.update(id, dto);
    }
}

