package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.AsistenteRecomendacionDTO;
import es.iesdeteis.secretaria.service.AsistenteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asistente")
public class AsistenteController {

    private final AsistenteService asistenteService;

    public AsistenteController(AsistenteService asistenteService) {
        this.asistenteService = asistenteService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'ALUMNO')")
    @GetMapping("/usuario/{usuarioId}")
    public List<AsistenteRecomendacionDTO> obtenerRecomendaciones(@PathVariable Long usuarioId) {
        return asistenteService.obtenerRecomendacionesUsuario(usuarioId);
    }
}

