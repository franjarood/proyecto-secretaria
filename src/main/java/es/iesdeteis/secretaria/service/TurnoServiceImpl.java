package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.exception.TurnoNoEncontradoException;
import es.iesdeteis.secretaria.model.EstadoTurno;
import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.model.Turno;
import es.iesdeteis.secretaria.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoServiceImpl(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    @Override
    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }

    @Override
    public Optional<Turno> findById(Long id) {
        return turnoRepository.findById(id);
    }

    @Override
    public Turno save(Turno turno) {

        // Calcular duración estimada
        Integer duracion = calculateEstimatedDuration(turno);
        turno.setDuracionEstimada(duracion);

        // Estado inicial al reservar desde casa
        turno.setEstadoTurno(EstadoTurno.RESERVADO);

        return turnoRepository.save(turno);
    }

    @Override
    public Turno update(Long id, Turno turnoActualizado) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        turno.setNumeroTurno(turnoActualizado.getNumeroTurno());
        turno.setHoraLlegada(turnoActualizado.getHoraLlegada());
        turno.setEstadoTurno(turnoActualizado.getEstadoTurno());
        turno.setPrioridad(turnoActualizado.getPrioridad());
        turno.setOrigenTurno(turnoActualizado.getOrigenTurno());
        turno.setObservaciones(turnoActualizado.getObservaciones());
        turno.setTiposTramite(turnoActualizado.getTiposTramite());

        return turnoRepository.save(turno);
    }

    @Override
    public void deleteById(Long id) {
        if (!turnoRepository.existsById(id)) {
            throw new TurnoNoEncontradoException("Turno no encontrado");
        }

        turnoRepository.deleteById(id);
    }

    // =========================
    // MÉTODOS PROYECTO (TOP)
    // =========================

    @Override
    public Integer calculateEstimatedDuration(Turno turno) {

        // Sumar duración de todos los trámites
        int total = 0;

        if (turno.getTiposTramite() != null) {
            for (TipoTramite t : turno.getTiposTramite()) {
                if (t.getDuracionEstimada() != null) {
                    total += t.getDuracionEstimada();
                }
            }
        }

        return total;
    }

    @Override
    public Turno confirmArrival(Long id) {

        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        // Guardar hora de llegada actual
        turno.setHoraLlegada(LocalTime.now());

        // Cambiar estado a CONFIRMADO
        turno.setEstadoTurno(EstadoTurno.CONFIRMADO);

        return turnoRepository.save(turno);
    }

    @Override
    public Integer calculateRealWaitingTime(Long id) {

        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        // Obtener turnos activos
        List<Turno> activos = turnoRepository.findAll();

        int espera = 0;

        for (Turno t : activos) {

            // Solo contar los activos
            if (t.getEstadoTurno().esActivo()) {

                // Evitar sumarse a sí mismo
                if (!t.getId().equals(turno.getId())) {

                    if (t.getDuracionEstimada() != null) {
                        espera += t.getDuracionEstimada();
                    }
                }
            }
        }

        return espera;
    }
}
