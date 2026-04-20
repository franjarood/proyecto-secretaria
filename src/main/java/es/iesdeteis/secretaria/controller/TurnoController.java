package es.iesdeteis.secretaria.controller;

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
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
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
}