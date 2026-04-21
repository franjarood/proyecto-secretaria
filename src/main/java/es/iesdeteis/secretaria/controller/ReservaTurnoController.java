package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.exception.ReservaNoEncontradaException;
import es.iesdeteis.secretaria.model.ReservaTurno;
import es.iesdeteis.secretaria.service.ReservaTurnoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaTurnoController {

    private final ReservaTurnoService reservaTurnoService;

    public ReservaTurnoController(ReservaTurnoService reservaTurnoService) {
        this.reservaTurnoService = reservaTurnoService;
    }

    // Obtener todas las reservas
    @GetMapping
    public List<ReservaTurno> getReservas() {
        return reservaTurnoService.findAll();
    }

    // Obtener reserva por ID
    @GetMapping("/{id}")
    public ReservaTurno getReservaById(@PathVariable Long id) {
        return reservaTurnoService.findById(id)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada"));
    }

    // Crear reserva
    @PostMapping
    public ReservaTurno saveReserva(@Valid @RequestBody ReservaTurno reservaTurno) {
        return reservaTurnoService.save(reservaTurno);
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
}