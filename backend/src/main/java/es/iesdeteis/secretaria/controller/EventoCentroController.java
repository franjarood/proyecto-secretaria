package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.EventoCentroResponseDTO;
import es.iesdeteis.secretaria.service.EventoCentroService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoCentroController {

    // ATRIBUTOS

    private final EventoCentroService eventoCentroService;


    // CONSTRUCTOR

    public EventoCentroController(EventoCentroService eventoCentroService) {
        this.eventoCentroService = eventoCentroService;
    }


    // MÉTODOS

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'ALUMNO', 'PROFESOR')")
    @GetMapping("/mis-eventos")
    public List<EventoCentroResponseDTO> misEventos() {
        return eventoCentroService.listarMisEventos();
    }
}

