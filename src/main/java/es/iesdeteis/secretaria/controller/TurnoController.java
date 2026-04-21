package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.exception.TurnoNoEncontradoException;
import es.iesdeteis.secretaria.model.Turno;
import es.iesdeteis.secretaria.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Crear turno (reserva)
    @PostMapping
    public Turno saveTurno(@Valid @RequestBody Turno turno) {
        return turnoService.save(turno);
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
    public Map<String, Integer> getPosition(@PathVariable Long id) {

        int posicion = turnoService.getPositionInQueue(id);
        int delante = turnoService.getPeopleAhead(id);

        Map<String, Integer> response = new HashMap<>();
        response.put("posicion", posicion);
        response.put("personasDelante", delante);

        return response;
    }

    // Obtener estado completo del turno
    @GetMapping("/{id}/estado")
    public Map<String, Object> getEstado(@PathVariable Long id) {

        Turno turno = turnoService.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        int posicion = turnoService.getPositionInQueue(id);
        int delante = turnoService.getPeopleAhead(id);
        int espera = turnoService.calculateRealWaitingTime(id);

        Map<String, Object> response = new HashMap<>();
        response.put("posicion", posicion);
        response.put("personasDelante", delante);
        response.put("tiempoEspera", espera);
        response.put("estado", turno.getEstadoTurno());

        return response;
    }

    // Cambiar estado del turno
    @PutMapping("/{id}/estado")
    public Turno cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return turnoService.cambiarEstado(id, estado);
    }
}