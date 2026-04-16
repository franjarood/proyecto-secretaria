package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.service.TipoTramiteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tipos-tramite")
public class TipoTramiteController {

    private final TipoTramiteService tipoTramiteService;

    public TipoTramiteController(TipoTramiteService tipoTramiteService) {
        this.tipoTramiteService = tipoTramiteService;
    }

    @GetMapping
    public List<TipoTramite> findAll() {
        return tipoTramiteService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<TipoTramite> findById(@PathVariable Long id) {
        return tipoTramiteService.findById(id);
    }

    @PostMapping
    public TipoTramite save(@RequestBody TipoTramite tipoTramite) {
        return tipoTramiteService.save(tipoTramite);
    }

    @PutMapping("/{id}")
    public TipoTramite update(@PathVariable Long id, @RequestBody TipoTramite tipoTramite) {
        return tipoTramiteService.update(id, tipoTramite);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        tipoTramiteService.deleteById(id);
    }
}