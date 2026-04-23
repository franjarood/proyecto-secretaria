package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.ReservaTurnoCreateDTO;
import es.iesdeteis.secretaria.dto.ReservaTurnoResponseDTO;
import es.iesdeteis.secretaria.exception.ReservaNoEncontradaException;
import es.iesdeteis.secretaria.model.ReservaTurno;
import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.service.ReservaTurnoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaTurnoController {

    private final ReservaTurnoService reservaTurnoService;

    public ReservaTurnoController(ReservaTurnoService reservaTurnoService) {
        this.reservaTurnoService = reservaTurnoService;
    }

    // Obtener reservas según el rol del usuario autenticado
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'ALUMNO')")
    @GetMapping
    public List<ReservaTurnoResponseDTO> getReservas() {
        return reservaTurnoService.findReservasSegunRol().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // Obtener reserva por ID según el rol del usuario autenticado
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'ALUMNO')")
    @GetMapping("/{id}")
    public ReservaTurnoResponseDTO getReservaById(@PathVariable Long id) {
        ReservaTurno reserva = reservaTurnoService.findReservaByIdSegunRol(id)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada"));

        return convertirAResponseDTO(reserva);
    }

    // Crear reserva
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'ALUMNO')")
    @PostMapping
    public ReservaTurnoResponseDTO saveReserva(@Valid @RequestBody ReservaTurnoCreateDTO dto) {
        ReservaTurno reservaGuardada = reservaTurnoService.saveFromDTO(dto);
        return convertirAResponseDTO(reservaGuardada);
    }

    // Actualizar reserva
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PutMapping("/{id}")
    public ReservaTurnoResponseDTO updateReserva(@PathVariable Long id,
                                                 @Valid @RequestBody ReservaTurno reservaTurno) {
        ReservaTurno reservaActualizada = reservaTurnoService.update(id, reservaTurno);
        return convertirAResponseDTO(reservaActualizada);
    }

    // Eliminar reserva
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @DeleteMapping("/{id}")
    public void deleteReserva(@PathVariable Long id) {
        reservaTurnoService.deleteById(id);
    }

    // =========================
    // MÉTODOS AUXILIARES
    // =========================

    private ReservaTurnoResponseDTO convertirAResponseDTO(ReservaTurno reserva) {
        return new ReservaTurnoResponseDTO(
                reserva.getId(),
                reserva.getFechaCita(),
                reserva.getHoraCita(),
                reserva.getCodigoReferencia(),
                reserva.getOrigenTurno(),
                reserva.getEstadoReserva(),
                reserva.getTiposTramite().stream().map(TipoTramite::getNombre).toList(),
                reserva.getCreatedAt(),
                reserva.getUpdatedAt()
        );
    }
}