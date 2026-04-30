package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.exception.EstadoTurnoInvalidoException;
import es.iesdeteis.secretaria.exception.ReservaNoEncontradaException;
import es.iesdeteis.secretaria.exception.ReservaYaProcesadaException;
import es.iesdeteis.secretaria.exception.TurnoNoEncontradoException;
import es.iesdeteis.secretaria.model.*;
import es.iesdeteis.secretaria.repository.ReservaTurnoRepository;
import es.iesdeteis.secretaria.repository.TipoTramiteRepository;
import es.iesdeteis.secretaria.repository.TurnoRepository;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TurnoServiceImpl implements TurnoService {

    // ATRIBUTOS

    private final TurnoRepository turnoRepository;
    private final TipoTramiteRepository tipoTramiteRepository;
    private final ReservaTurnoRepository reservaTurnoRepository;
    private final HistorialAccionService historialAccionService;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;


    // CONSTRUCTOR

    public TurnoServiceImpl(TurnoRepository turnoRepository,
                            TipoTramiteRepository tipoTramiteRepository,
                            ReservaTurnoRepository reservaTurnoRepository,
                            HistorialAccionService historialAccionService,
                            UsuarioRepository usuarioRepository,
                            NotificacionService notificacionService) {
        this.turnoRepository = turnoRepository;
        this.tipoTramiteRepository = tipoTramiteRepository;
        this.reservaTurnoRepository = reservaTurnoRepository;
        this.historialAccionService = historialAccionService;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }


    // MÉTODOS CRUD

    @Override
    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }

    @Override
    public Optional<Turno> findById(Long id) {
        return turnoRepository.findById(id);
    }

    @Override
    public List<Turno> findTurnosSegunRol() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        boolean esPersonalCentro = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_SECRETARIA")
                        || a.getAuthority().equals("ROLE_CONSERJE"));

        if (esPersonalCentro) {
            return turnoRepository.findAll();
        }

        return turnoRepository.findByReservaTurnoUsuarioEmail(email);
    }

    @Override
    public Optional<Turno> findTurnoByIdSegunRol(Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        boolean esPersonalCentro = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_SECRETARIA")
                        || a.getAuthority().equals("ROLE_CONSERJE"));

        if (esPersonalCentro) {
            return turnoRepository.findById(id);
        }

        return turnoRepository.findByIdAndReservaTurnoUsuarioEmail(id, email);
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
        asignarPrioridad(turno);

        return turnoRepository.save(turno);
    }

    @Override
    public Turno crearTurnoDesdeReserva(Long reservaId) {

        ReservaTurno reservaTurno = reservaTurnoRepository.findById(reservaId)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada"));

        if (reservaTurno.getEstadoReserva() == EstadoReserva.CONFIRMADA) {
            throw new ReservaYaProcesadaException("La reserva ya ha generado un turno");
        }

        Turno turno = new Turno();

        turno.setNumeroTurno("R-" + reservaTurno.getId());
        turno.setFechaCita(reservaTurno.getFechaCita());
        turno.setHoraCita(reservaTurno.getHoraCita());
        turno.setOrigenTurno(reservaTurno.getOrigenTurno());

        // Cargar trámites completos desde BD
        List<TipoTramite> tramitesCompletos = cargarTramitesCompletos(reservaTurno.getTiposTramite());
        turno.setTiposTramite(new ArrayList<>(tramitesCompletos));

        turno.setReservaTurno(reservaTurno);
        turno.setEstadoTurno(EstadoTurno.RESERVADO);
        turno.setDuracionEstimada(calculateEstimatedDuration(turno));

        // Calcular prioridad automática
        asignarPrioridad(turno);

        reservaTurno.setEstadoReserva(EstadoReserva.CONFIRMADA);
        reservaTurnoRepository.save(reservaTurno);

        Turno turnoGuardado = turnoRepository.save(turno);

        registrarHistorial(
                "CREACION_TURNO",
                "Se creó el turno " + turnoGuardado.getNumeroTurno() +
                        " desde la reserva " + reservaTurno.getCodigoReferencia(),
                turnoGuardado.getId()
        );

        if (reservaTurno.getUsuario() != null) {
            notificacionService.crearNotificacionInterna(
                    "Turno generado",
                    "Se ha generado tu turno correctamente. Número: " + turnoGuardado.getNumeroTurno(),
                    TipoNotificacion.TURNO_GENERADO,
                    "TURNO_" + turnoGuardado.getId(),
                    "/turnos",
                    reservaTurno.getUsuario()
            );
        }

        return turnoGuardado;
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
        turno.setTipoPrioridad(turnoActualizado.getTipoPrioridad());

        // Actualizar reserva asociada
        turno.setReservaTurno(turnoActualizado.getReservaTurno());

        // Cargar trámites completos desde BD
        List<TipoTramite> tramitesCompletos = cargarTramitesCompletos(turnoActualizado.getTiposTramite());
        turno.setTiposTramite(tramitesCompletos);

        // Recalcular duración y prioridad
        turno.setDuracionEstimada(calculateEstimatedDuration(turno));
        asignarPrioridad(turno);

        Turno turnoGuardado = turnoRepository.save(turno);

        if (turnoGuardado.getEstadoTurno() == EstadoTurno.CANCELADO) {

            Usuario usuario = obtenerUsuarioDelTurno(turnoGuardado);

            if (usuario != null) {
                notificacionService.crearNotificacionInterna(
                        "Turno cancelado",
                        "Tu turno " + turnoGuardado.getNumeroTurno() + " ha sido cancelado.",
                        TipoNotificacion.TURNO_CANCELADO,
                        "TURNO_" + turnoGuardado.getId(),
                        "/turnos",
                        usuario
                );
            }
        }

        return turnoGuardado;
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

        turno.setHoraLlegada(LocalTime.now());
        turno.setEstadoTurno(EstadoTurno.EN_COLA);

        // Recalcular prioridad al entrar en cola
        asignarPrioridad(turno);

        Turno turnoGuardado = turnoRepository.save(turno);

        registrarHistorial(
                "CONFIRMACION_LLEGADA",
                "El turno " + turnoGuardado.getNumeroTurno() + " confirmó su llegada y pasó a EN_COLA",
                turnoGuardado.getId()
        );

        return turnoGuardado;
    }

    @Override
    public Integer calculateRealWaitingTime(Long id) {

        Turno turno = obtenerTurnoSeguro(id);

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

    @Override
    public int getPositionInQueue(Long id) {

        Turno turno = obtenerTurnoSeguro(id);

        List<Turno> cola = getQueue();

        for (int i = 0; i < cola.size(); i++) {
            if (cola.get(i).getId().equals(turno.getId())) {
                return i + 1;
            }
        }

        throw new TurnoNoEncontradoException("Turno no encontrado en la cola");
    }

    @Override
    public int getPeopleAhead(Long id) {
        return getPositionInQueue(id) - 1;
    }

    @Override
    public List<Turno> getQueue() {

        List<Turno> turnos = turnoRepository.findAll();
        List<Turno> activos = new ArrayList<>();

        // Filtrar solo turnos activos
        for (Turno t : turnos) {
            if (t.getEstadoTurno() != null && t.getEstadoTurno().esActivo()) {
                activos.add(t);
            }
        }

        activos.sort((t1, t2) -> {

            // --- Orden por prioridad (mayor primero) ---
            int prioridad1 = t1.getPrioridad() != null ? t1.getPrioridad() : 0;
            int prioridad2 = t2.getPrioridad() != null ? t2.getPrioridad() : 0;

            int cmpPrioridad = Integer.compare(prioridad2, prioridad1);
            if (cmpPrioridad != 0) {
                return cmpPrioridad;
            }

            // --- Si tienen misma prioridad, ordenar por hora de llegada ---
            if (t1.getHoraLlegada() != null && t2.getHoraLlegada() == null) {
                return -1;
            }

            if (t1.getHoraLlegada() == null && t2.getHoraLlegada() != null) {
                return 1;
            }

            if (t1.getHoraLlegada() != null && t2.getHoraLlegada() != null) {
                return t1.getHoraLlegada().compareTo(t2.getHoraLlegada());
            }

            // --- Si no hay hora de llegada, ordenar por hora de cita ---
            return t1.getHoraCita().compareTo(t2.getHoraCita());
        });

        return activos;
    }

    @Override
    public Turno cambiarEstado(Long id, String estado) {

        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        EstadoTurno estadoAnterior = turno.getEstadoTurno();

        try {
            EstadoTurno nuevoEstado = EstadoTurno.valueOf(estado.toUpperCase());
            turno.setEstadoTurno(nuevoEstado);

            Turno turnoGuardado = turnoRepository.save(turno);

            registrarHistorial(
                    "CAMBIO_ESTADO",
                    "El turno " + turnoGuardado.getNumeroTurno() +
                            " cambió de " + estadoAnterior + " a " + nuevoEstado,
                    turnoGuardado.getId()
            );

            if (nuevoEstado == EstadoTurno.CANCELADO) {

                Usuario usuario = obtenerUsuarioDelTurno(turnoGuardado);

                if (usuario != null) {
                    notificacionService.crearNotificacionInterna(
                            "Turno cancelado",
                            "Tu turno " + turnoGuardado.getNumeroTurno() + " ha sido cancelado.",
                            TipoNotificacion.TURNO_CANCELADO,
                            "TURNO_" + turnoGuardado.getId(),
                            "/turnos",
                            usuario
                    );
                }
            }

            return turnoGuardado;

        } catch (IllegalArgumentException e) {
            throw new EstadoTurnoInvalidoException("El estado indicado no es válido");
        }
    }

    @Override
    public Turno siguienteTurno() {

        List<Turno> cola = getQueue();

        // Finalizar el turno que esté actualmente en atención
        for (Turno t : cola) {
            if (t.getEstadoTurno() == EstadoTurno.EN_ATENCION) {
                t.setEstadoTurno(EstadoTurno.FINALIZADO);

                Turno turnoFinalizado = turnoRepository.save(t);

                registrarHistorial(
                        "FINALIZACION_TURNO",
                        "El turno " + turnoFinalizado.getNumeroTurno() + " pasó a FINALIZADO",
                        turnoFinalizado.getId()
                );

                Usuario usuarioFinalizado = obtenerUsuarioDelTurno(turnoFinalizado);

                if (usuarioFinalizado != null) {
                    notificacionService.crearNotificacionInterna(
                            "Turno finalizado",
                            "Tu turno " + turnoFinalizado.getNumeroTurno() + " ha finalizado.",
                            TipoNotificacion.TURNO_FINALIZADO,
                            "TURNO_" + turnoFinalizado.getId(),
                            "/turnos",
                            usuarioFinalizado
                    );
                }

                break;
            }
        }

        List<Turno> colaActualizada = getQueue();

        // Llamar al siguiente turno según prioridad y orden de llegada
        for (Turno t : colaActualizada) {
            if (t.getEstadoTurno() == EstadoTurno.EN_COLA
                    || t.getEstadoTurno() == EstadoTurno.REANUDADO) {

                t.setEstadoTurno(EstadoTurno.EN_ATENCION);
                Turno turnoGuardado = turnoRepository.save(t);

                registrarHistorial(
                        "SIGUIENTE_TURNO",
                        "El turno " + turnoGuardado.getNumeroTurno() + " pasó a EN_ATENCION",
                        turnoGuardado.getId()
                );

                Usuario usuarioLlamado = obtenerUsuarioDelTurno(turnoGuardado);

                if (usuarioLlamado != null) {
                    notificacionService.crearNotificacionInterna(
                            "Turno llamado",
                            "Tu turno " + turnoGuardado.getNumeroTurno() + " ha sido llamado.",
                            TipoNotificacion.TURNO_LLAMADO,
                            "TURNO_" + turnoGuardado.getId(),
                            "/turnos",
                            usuarioLlamado
                    );
                }

                notificarUsuariosCercanos();

                return turnoGuardado;
            }
        }

        throw new TurnoNoEncontradoException("No hay más turnos en espera");
    }

    @Override
    public Turno cambiarPrioridad(Long id, PrioridadTurno tipo, String motivo) {

        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        // Cambiar prioridad manualmente
        turno.setTipoPrioridad(tipo);
        turno.setPrioridadManual(true);
        turno.setMotivoPrioridad(motivo);

        // Recalcular prioridad numérica
        asignarPrioridad(turno);

        Turno turnoGuardado = turnoRepository.save(turno);

        // Guardar historial
        registrarHistorial(
                "CAMBIO_PRIORIDAD",
                "Se cambió la prioridad del turno "
                        + turnoGuardado.getNumeroTurno()
                        + " a " + tipo
                        + ". Motivo: " + motivo,
                turnoGuardado.getId()
        );

        return turnoGuardado;
    }

    @Override
    public Turno reanudarTurno(Long id) {

        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        if (turno.getEstadoTurno() != EstadoTurno.EN_ATENCION
                && turno.getEstadoTurno() != EstadoTurno.PAUSADO) {
            throw new IllegalStateException("Solo se pueden reanudar turnos en atención o pausados");
        }

        turno.setEstadoTurno(EstadoTurno.REANUDADO);

        // Al reanudar por incidencia, se considera compensación
        turno.setReingreso(true);
        turno.setTipoPrioridad(PrioridadTurno.COMPENSACION);
        turno.setMotivoPrioridad("Reingreso por incidencia o pausa en la atención");

        asignarPrioridad(turno);

        Turno turnoGuardado = turnoRepository.save(turno);

        registrarHistorial(
                "REANUDACION_TURNO",
                "El turno " + turnoGuardado.getNumeroTurno() + " volvió a la cola como REANUDADO",
                turnoGuardado.getId()
        );

        return turnoGuardado;
    }




    // =========================
    // MÉTODOS AUXILIARES
    // =========================


    private void notificarUsuariosCercanos() {

        List<Turno> cola = getQueue();

        for (int i = 0; i < cola.size(); i++) {

            Turno turno = cola.get(i);

            if (turno.getEstadoTurno() != EstadoTurno.EN_COLA
                    && turno.getEstadoTurno() != EstadoTurno.REANUDADO) {
                continue;
            }

            int turnosDelante = i;

            if (turnosDelante <= 2) {

                Usuario usuario = obtenerUsuarioDelTurno(turno);

                if (usuario != null) {

                    String mensaje;

                    if (turnosDelante == 0) {
                        mensaje = "Es tu turno. Dirígete al mostrador.";
                    } else if (turnosDelante == 1) {
                        mensaje = "Queda 1 turno antes que el tuyo.";
                    } else {
                        mensaje = "Quedan " + turnosDelante + " turnos antes que el tuyo.";
                    }

                    notificacionService.crearNotificacionInterna(
                            "Tu turno está cerca",
                            mensaje,
                            TipoNotificacion.TURNO_PROXIMO,
                            "TURNO_" + turno.getId(),
                            "/turnos",
                            usuario
                    );
                }
            }
        }
    }

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




    // =========================
    // PRIORIDAD INTELIGENTE
    // =========================

    private void asignarPrioridad(Turno turno) {

        // --- Determinar tipo de prioridad ---
        if (turno.getTipoPrioridad() == null) {

            if (Boolean.TRUE.equals(turno.getPrioridadManual())) {
                turno.setTipoPrioridad(PrioridadTurno.ESPECIAL);

            } else if (Boolean.TRUE.equals(turno.getReingreso())) {
                turno.setTipoPrioridad(PrioridadTurno.COMPENSACION);

            } else {
                turno.setTipoPrioridad(PrioridadTurno.NORMAL);
            }
        }

        // --- Asignar valor numérico para ordenar ---
        turno.setPrioridad(calcularValorPrioridad(turno.getTipoPrioridad()));
    }

    private Integer calcularValorPrioridad(PrioridadTurno tipoPrioridad) {

        if (tipoPrioridad == null) {
            return 0;
        }

        return switch (tipoPrioridad) {
            case URGENTE -> 100;        // máxima prioridad
            case COMPENSACION -> 80;    // usuario perjudicado por incidencia
            case ESPECIAL -> 60;        // prioridad manual del centro
            case ALTA -> 40;            // caso importante
            case NORMAL -> 0;           // turno normal
        };
    }

    private Turno obtenerTurnoSeguro(Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        boolean esPersonalCentro = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_SECRETARIA")
                        || a.getAuthority().equals("ROLE_CONSERJE"));

        if (esPersonalCentro) {
            return turnoRepository.findById(id)
                    .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));
        }

        return turnoRepository.findByIdAndReservaTurnoUsuarioEmail(id, email)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));
    }

    private void registrarHistorial(String accion, String descripcion, Long idEntidad) {
        HistorialAccion historialAccion = new HistorialAccion();

        historialAccion.setAccion(accion);
        historialAccion.setDescripcion(descripcion);
        historialAccion.setEntidadAfectada("Turno");
        historialAccion.setIdEntidad(idEntidad);
        historialAccion.setUsuarioResponsable(null);

        historialAccionService.save(historialAccion);
    }

    private Long obtenerIdUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .map(Usuario::getId)
                .orElse(null);
    }

    private Usuario obtenerUsuarioDelTurno(Turno turno) {

        if (turno.getReservaTurno() != null &&
                turno.getReservaTurno().getUsuario() != null) {

            return turno.getReservaTurno().getUsuario();
        }

        return null;
    }
}