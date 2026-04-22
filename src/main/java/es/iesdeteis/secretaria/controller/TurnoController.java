package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.EstadoTurnoDTO;
import es.iesdeteis.secretaria.dto.EstadoTurnoResponseDTO;
import es.iesdeteis.secretaria.dto.PosicionTurnoDTO;
import es.iesdeteis.secretaria.exception.TurnoNoEncontradoException;
import es.iesdeteis.secretaria.model.Turno;
import es.iesdeteis.secretaria.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    // Obtener todos los turnos
    @GetMapping
    public List<Turno> getTurnos() {
        return turnoService.findAll();
    }

    // Obtener turno por ID
    @GetMapping("/{id}")
    public Turno getTurnoById(@PathVariable Long id) {
        return turnoService.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));
    }

    // Crear nuevo turno
    @PostMapping
    public Turno saveTurno(@Valid @RequestBody Turno turno) {
        return turnoService.save(turno);
    }

    // Crear turno a partir de una reserva
    @PostMapping("/desde-reserva/{idReserva}")
    public Turno crearTurnoDesdeReserva(@PathVariable Long idReserva) {
        return turnoService.crearTurnoDesdeReserva(idReserva);
    }

    // Actualizar turno
    @PutMapping("/{id}")
    public Turno updateTurno(@PathVariable Long id, @Valid @RequestBody Turno turno) {
        return turnoService.update(id, turno);
    }

    // Eliminar turno
    @DeleteMapping("/{id}")
    public void deleteTurno(@PathVariable Long id) {
        turnoService.deleteById(id);
    }

    // Confirmar llegada en kiosko
    @PutMapping("/{id}/confirmar")
    public Turno confirmArrival(@PathVariable Long id) {
        return turnoService.confirmArrival(id);
    }

    // Calcular tiempo de espera real
    @GetMapping("/{id}/espera")
    public Integer getRealWaitingTime(@PathVariable Long id) {
        return turnoService.calculateRealWaitingTime(id);
    }

    // Obtener cola ordenada
    @GetMapping("/cola")
    public List<Turno> getQueue() {
        return turnoService.getQueue();
    }

    // Obtener posición del turno en la cola
    @GetMapping("/{id}/posicion")
    public PosicionTurnoDTO getPosition(@PathVariable Long id) {

        int posicion = turnoService.getPositionInQueue(id);
        int delante = turnoService.getPeopleAhead(id);

        return new PosicionTurnoDTO(posicion, delante);
    }

    // Obtener estado completo del turno
    @GetMapping("/{id}/estado")
    public EstadoTurnoResponseDTO getEstado(@PathVariable Long id) {

        Turno turno = turnoService.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        int posicion = 0;
        int delante = 0;
        int espera = 0;

        if (turno.getEstadoTurno() != null && turno.getEstadoTurno().esActivo()) {
            posicion = turnoService.getPositionInQueue(id);
            delante = turnoService.getPeopleAhead(id);
            espera = turnoService.calculateRealWaitingTime(id);
        }

        return new EstadoTurnoResponseDTO(
                posicion,
                delante,
                espera,
                turno.getEstadoTurno().name()
        );
    }

    // Cambiar estado del turno
    @PutMapping("/{id}/estado")
    public Turno cambiarEstado(@PathVariable Long id,
                               @Valid @RequestBody EstadoTurnoDTO dto) {
        return turnoService.cambiarEstado(id, dto.getEstado());
    }

    // Pasar al siguiente turno de la cola
    @PutMapping("/siguiente")
    public Turno siguienteTurno() {
        return turnoService.siguienteTurno();
    }

    // Reanudar turno y devolverlo a la cola
    @PutMapping("/{id}/reanudar")
    public Turno reanudarTurno(@PathVariable Long id) {
        return turnoService.reanudarTurno(id);
    }
}