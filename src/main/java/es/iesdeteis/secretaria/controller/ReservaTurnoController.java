package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.ReservaTurnoCreateDTO;
import es.iesdeteis.secretaria.dto.ReservaTurnoResponseDTO;
import es.iesdeteis.secretaria.exception.ReservaNoEncontradaException;
import es.iesdeteis.secretaria.model.ReservaTurno;
import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.service.ReservaTurnoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaTurnoController {

    // ATRIBUTOS

    private final ReservaTurnoService reservaTurnoService;


    // CONSTRUCTOR

    public ReservaTurnoController(ReservaTurnoService reservaTurnoService) {
        this.reservaTurnoService = reservaTurnoService;
    }


    // MÉTODOS

    // Obtener todas las reservas
    @GetMapping
    public List<ReservaTurnoResponseDTO> getReservas() {
        return reservaTurnoService.findAll().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // Obtener reserva por ID
    @GetMapping("/{id}")
    public ReservaTurnoResponseDTO getReservaById(@PathVariable Long id) {
        ReservaTurno reserva = reservaTurnoService.findById(id)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada"));

        return convertirAResponseDTO(reserva);
    }

    // Crear reserva
    @PostMapping
    public ReservaTurnoResponseDTO saveReserva(@Valid @RequestBody ReservaTurnoCreateDTO dto) {
        ReservaTurno reservaGuardada = reservaTurnoService.saveFromDTO(dto);
        return convertirAResponseDTO(reservaGuardada);
    }

    // Actualizar reserva
    @PutMapping("/{id}")
    public ReservaTurno updateReserva(@PathVariable Long id, @Valid @RequestBody ReservaTurno reservaTurno) {
        return reservaTurnoService.update(id, reservaTurno);
    }

    // Eliminar reserva
    @DeleteMapping("/{id}")
    public void deleteReserva(@PathVariable Long id) {
        reservaTurnoService.deleteById(id);
    }


    // =========================
    // MÉTODOS AUXILIARES
    // =========================

    // Convertir entidad ReservaTurno a DTO de respuesta
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