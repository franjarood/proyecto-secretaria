package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.exception.TurnoNoEncontradoException;
import es.iesdeteis.secretaria.model.EstadoTurno;
import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.model.Turno;
import es.iesdeteis.secretaria.repository.TipoTramiteRepository;
import es.iesdeteis.secretaria.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;
    private final TipoTramiteRepository tipoTramiteRepository;

    public TurnoServiceImpl(TurnoRepository turnoRepository, TipoTramiteRepository tipoTramiteRepository) {
        this.turnoRepository = turnoRepository;
        this.tipoTramiteRepository = tipoTramiteRepository;
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

        // Cargar trámites completos desde BD
        List<TipoTramite> tramitesCompletos = cargarTramitesCompletos(turno.getTiposTramite());
        turno.setTiposTramite(tramitesCompletos);

        // Calcular duración estimada
        Integer duracion = calculateEstimatedDuration(turno);
        turno.setDuracionEstimada(duracion);

        // Estado inicial al reservar desde casa
        turno.setEstadoTurno(EstadoTurno.RESERVADO);

        // Calcular prioridad automática
        turno.setPrioridad(calculatePriority(turno));

        return turnoRepository.save(turno);
    }

    @Override
    public Turno update(Long id, Turno turnoActualizado) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        // Actualizar datos básicos
        turno.setNumeroTurno(turnoActualizado.getNumeroTurno());
        turno.setFechaCita(turnoActualizado.getFechaCita());
        turno.setHoraCita(turnoActualizado.getHoraCita());
        turno.setHoraLlegada(turnoActualizado.getHoraLlegada());
        turno.setEstadoTurno(turnoActualizado.getEstadoTurno());
        turno.setOrigenTurno(turnoActualizado.getOrigenTurno());
        turno.setObservaciones(turnoActualizado.getObservaciones());

        // Actualizar campos de prioridad inteligente
        turno.setReingreso(turnoActualizado.getReingreso());
        turno.setIncidencia(turnoActualizado.getIncidencia());
        turno.setPrioridadManual(turnoActualizado.getPrioridadManual());
        turno.setMotivoPrioridad(turnoActualizado.getMotivoPrioridad());

        // Cargar trámites completos desde BD
        List<TipoTramite> tramitesCompletos = cargarTramitesCompletos(turnoActualizado.getTiposTramite());
        turno.setTiposTramite(tramitesCompletos);

        // Recalcular duración y prioridad
        turno.setDuracionEstimada(calculateEstimatedDuration(turno));
        turno.setPrioridad(calculatePriority(turno));

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
    // MÉTODOS PROYECTO
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

        List<Turno> cola = getQueue();

        int espera = 0;

        for (Turno t : cola) {

            if (t.getId().equals(turno.getId())) {
                break;
            }

            if (t.getDuracionEstimada() != null) {

                if (t.getEstadoTurno() == EstadoTurno.EN_ATENCION && t.getHoraLlegada() != null) {

                    int consumido = LocalTime.now().toSecondOfDay() - t.getHoraLlegada().toSecondOfDay();
                    consumido = consumido / 60;

                    int restante = t.getDuracionEstimada() - consumido;

                    espera += Math.max(restante, 0);

                } else {
                    espera += t.getDuracionEstimada();
                }
            }
        }

        return espera;
    }

    // Obtener posición del turno en la cola
    @Override
    public int getPositionInQueue(Long id) {

        List<Turno> cola = getQueue();

        for (int i = 0; i < cola.size(); i++) {
            if (cola.get(i).getId().equals(id)) {
                return i + 1;
            }
        }

        throw new TurnoNoEncontradoException("Turno no encontrado en la cola");
    }

    // Obtener cuántos turnos tiene delante
    @Override
    public int getPeopleAhead(Long id) {
        return getPositionInQueue(id) - 1;
    }

    @Override
    public List<Turno> getQueue() {

        List<Turno> turnos = turnoRepository.findAll();
        List<Turno> activos = new ArrayList<>();

        // Quedarse solo con turnos activos
        for (Turno t : turnos) {
            if (t.getEstadoTurno() != null && t.getEstadoTurno().esActivo()) {
                activos.add(t);
            }
        }

        // Ordenar por prioridad y después por hora
        activos.sort((t1, t2) -> {

            // 1. Prioridad
            int cmpPrioridad = Integer.compare(t1.getPrioridad(), t2.getPrioridad());
            if (cmpPrioridad != 0) {
                return cmpPrioridad;
            }

            // 2. Priorizar los que ya llegaron
            if (t1.getHoraLlegada() != null && t2.getHoraLlegada() == null) {
                return -1;
            }
            if (t1.getHoraLlegada() == null && t2.getHoraLlegada() != null) {
                return 1;
            }

            // 3. Si ambos tienen hora de llegada, ordenar por llegada
            if (t1.getHoraLlegada() != null && t2.getHoraLlegada() != null) {
                return t1.getHoraLlegada().compareTo(t2.getHoraLlegada());
            }

            // 4. Si ninguno llegó todavía, ordenar por hora de cita
            return t1.getHoraCita().compareTo(t2.getHoraCita());
        });

        return activos;
    }

    // Cambiar estado del turno
    @Override
    public Turno cambiarEstado(Long id, String estado) {

        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        turno.setEstadoTurno(EstadoTurno.valueOf(estado.toUpperCase()));

        return turnoRepository.save(turno);
    }

    // =========================
    // MÉTODOS AUXILIARES
    // =========================

    // Cargar trámites completos desde BD
    private List<TipoTramite> cargarTramitesCompletos(List<TipoTramite> tiposTramite) {
        List<TipoTramite> tramitesCompletos = new ArrayList<>();

        if (tiposTramite != null) {
            for (TipoTramite t : tiposTramite) {
                TipoTramite tramiteBD = tipoTramiteRepository.findById(t.getId())
                        .orElseThrow(() -> new RuntimeException("Tipo de trámite no encontrado"));
                tramitesCompletos.add(tramiteBD);
            }
        }

        return tramitesCompletos;
    }

    // Calcular prioridad automática
    private Integer calculatePriority(Turno turno) {

        // Prioridad 0 -> caso urgente manual
        if (Boolean.TRUE.equals(turno.getPrioridadManual())) {
            return 0;
        }

        // Prioridad 1 -> reingreso
        if (Boolean.TRUE.equals(turno.getReingreso())) {
            return 1;
        }

        // Prioridad 2 -> cita previa
        if ("ONLINE".equalsIgnoreCase(turno.getOrigenTurno())) {
            return 2;
        }

        // Prioridad 3 -> sin cita
        return 3;
    }

}