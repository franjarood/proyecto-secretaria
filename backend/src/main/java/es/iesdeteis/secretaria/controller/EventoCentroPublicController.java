package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.EventoCentroResponseDTO;
import es.iesdeteis.secretaria.service.EventoCentroService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/eventos")
public class EventoCentroPublicController {

    // ATRIBUTOS

    private final EventoCentroService eventoCentroService;


    // CONSTRUCTOR

    public EventoCentroPublicController(EventoCentroService eventoCentroService) {
        this.eventoCentroService = eventoCentroService;
    }


    // MÉTODOS

    @GetMapping
    public List<EventoCentroResponseDTO> listarPublicos() {
        return eventoCentroService.listarPublicosVisibles();
    }

    @GetMapping("/proximos")
    public List<EventoCentroResponseDTO> listarProximos() {
        return eventoCentroService.listarPublicosProximos();
    }
}

