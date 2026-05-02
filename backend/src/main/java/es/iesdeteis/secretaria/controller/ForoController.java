package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.RespuestaForoRequestDTO;
import es.iesdeteis.secretaria.dto.RespuestaForoResponseDTO;
import es.iesdeteis.secretaria.dto.TemaForoRequestDTO;
import es.iesdeteis.secretaria.dto.TemaForoResponseDTO;
import es.iesdeteis.secretaria.service.ForoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foro")
public class ForoController {

    // ATRIBUTOS

    private final ForoService foroService;


    // CONSTRUCTOR

    public ForoController(ForoService foroService) {
        this.foroService = foroService;
    }


    // MÉTODOS

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'PROFESOR', 'ALUMNO')")
    @GetMapping("/temas")
    public List<TemaForoResponseDTO> listarTemas() {
        return foroService.listarTemas();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'PROFESOR', 'ALUMNO')")
    @GetMapping("/temas/{id}")
    public TemaForoResponseDTO obtenerTema(@PathVariable Long id) {
        return foroService.obtenerTema(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'PROFESOR', 'ALUMNO')")
    @GetMapping("/mis-temas")
    public List<TemaForoResponseDTO> misTemas() {
        return foroService.listarMisTemas();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'PROFESOR', 'ALUMNO')")
    @PostMapping("/temas")
    public TemaForoResponseDTO crearTema(@Valid @RequestBody TemaForoRequestDTO dto) {
        return foroService.crearTema(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'PROFESOR', 'ALUMNO')")
    @PutMapping("/temas/{id}")
    public TemaForoResponseDTO actualizarTema(@PathVariable Long id, @Valid @RequestBody TemaForoRequestDTO dto) {
        return foroService.actualizarTema(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'PROFESOR', 'ALUMNO')")
    @DeleteMapping("/temas/{id}")
    public void eliminarTema(@PathVariable Long id) {
        foroService.eliminarTema(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'PROFESOR', 'ALUMNO')")
    @PostMapping("/temas/{id}/respuestas")
    public RespuestaForoResponseDTO crearRespuesta(@PathVariable Long id, @Valid @RequestBody RespuestaForoRequestDTO dto) {
        return foroService.crearRespuesta(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'PROFESOR', 'ALUMNO')")
    @PutMapping("/respuestas/{id}/mejor")
    public RespuestaForoResponseDTO marcarMejor(@PathVariable Long id) {
        return foroService.marcarMejorRespuesta(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'PROFESOR', 'ALUMNO')")
    @DeleteMapping("/respuestas/{id}")
    public void eliminarRespuesta(@PathVariable Long id) {
        foroService.eliminarRespuesta(id);
    }
}

