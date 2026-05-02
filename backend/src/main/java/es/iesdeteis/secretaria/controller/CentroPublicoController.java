package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.CentroInfoResponseDTO;
import es.iesdeteis.secretaria.dto.TipoTramitePublicoDTO;
import es.iesdeteis.secretaria.service.CentroInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/centro")
public class CentroPublicoController {

    // ATRIBUTOS

    private final CentroInfoService centroInfoService;


    // CONSTRUCTOR

    public CentroPublicoController(CentroInfoService centroInfoService) {
        this.centroInfoService = centroInfoService;
    }


    // MÉTODOS

    @GetMapping("/info")
    public CentroInfoResponseDTO obtenerCentroActivo() {
        return centroInfoService.obtenerCentroActivo();
    }

    @GetMapping("/tramites-destacados")
    public List<TipoTramitePublicoDTO> obtenerTramitesDestacados() {
        return centroInfoService.obtenerTramitesDestacados();
    }
}

