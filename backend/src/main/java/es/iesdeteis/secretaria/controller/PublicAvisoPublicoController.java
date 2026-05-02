package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.AvisoPublicoResponseDTO;
import es.iesdeteis.secretaria.service.AvisoPublicoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/centro/avisos")
public class PublicAvisoPublicoController {

    // ATRIBUTOS

    private final AvisoPublicoService avisoPublicoService;


    // CONSTRUCTOR

    public PublicAvisoPublicoController(AvisoPublicoService avisoPublicoService) {
        this.avisoPublicoService = avisoPublicoService;
    }


    // MÉTODOS

    @GetMapping
    public List<AvisoPublicoResponseDTO> listarVisiblesVigentes() {
        return avisoPublicoService.listarVisiblesVigentes();
    }

    @GetMapping("/destacados")
    public List<AvisoPublicoResponseDTO> listarDestacadosVisiblesVigentes() {
        return avisoPublicoService.listarDestacadosVisiblesVigentes();
    }
}

