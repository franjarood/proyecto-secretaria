package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.IncidenciaCreateDTO;
import es.iesdeteis.secretaria.exception.IncidenciaNoEncontradaException;
import es.iesdeteis.secretaria.exception.TurnoNoEncontradoException;
import es.iesdeteis.secretaria.model.*;
import es.iesdeteis.secretaria.repository.IncidenciaRepository;
import es.iesdeteis.secretaria.repository.TurnoRepository;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IncidenciaServiceImpl implements IncidenciaService {

    // =========================
    // ATRIBUTOS
    // =========================

    private final IncidenciaRepository incidenciaRepository;
    private final TurnoRepository turnoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;


    // =========================
    // CONSTRUCTOR
    // =========================

    public IncidenciaServiceImpl(IncidenciaRepository incidenciaRepository,
                                 TurnoRepository turnoRepository,
                                 UsuarioRepository usuarioRepository,
                                 NotificacionService notificacionService) {
        this.incidenciaRepository = incidenciaRepository;
        this.turnoRepository = turnoRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }


    // =========================
    // MÉTODOS
    // =========================

    @Override
    public List<Incidencia> findAll() {
        return incidenciaRepository.findAll();
    }

    @Override
    public Optional<Incidencia> findById(Long id) {
        return incidenciaRepository.findById(id);
    }

    @Override
    public Incidencia save(Incidencia incidencia) {

        if (incidencia.getTurno() != null && incidencia.getTurno().getId() != null) {
            Turno turno = turnoRepository.findById(incidencia.getTurno().getId())
                    .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));
            incidencia.setTurno(turno);
        }

        Incidencia incidenciaGuardada = incidenciaRepository.save(incidencia);

        notificarIncidenciaCreada(incidenciaGuardada);

        return incidenciaGuardada;
    }

    @Override
    public Incidencia saveFromDTO(IncidenciaCreateDTO dto) {

        Turno turno = turnoRepository.findById(dto.getTurnoId())
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        Incidencia incidencia = new Incidencia();
        incidencia.setTipo(dto.getTipo());
        incidencia.setDescripcion(dto.getDescripcion());
        incidencia.setResuelta(dto.getResuelta());
        incidencia.setAccionTomada(dto.getAccionTomada());
        incidencia.setTurno(turno);

        Incidencia incidenciaGuardada = incidenciaRepository.save(incidencia);

        notificarIncidenciaCreada(incidenciaGuardada);

        return incidenciaGuardada;
    }

    @Override
    public Incidencia update(Long id, Incidencia incidenciaActualizada) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new IncidenciaNoEncontradaException("Incidencia no encontrada"));

        Boolean estabaResuelta = incidencia.getResuelta();

        incidencia.setTipo(incidenciaActualizada.getTipo());
        incidencia.setDescripcion(incidenciaActualizada.getDescripcion());
        incidencia.setResuelta(incidenciaActualizada.getResuelta());
        incidencia.setAccionTomada(incidenciaActualizada.getAccionTomada());

        if (incidenciaActualizada.getTurno() != null && incidenciaActualizada.getTurno().getId() != null) {
            Turno turno = turnoRepository.findById(incidenciaActualizada.getTurno().getId())
                    .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));
            incidencia.setTurno(turno);
        }

        Incidencia incidenciaGuardada = incidenciaRepository.save(incidencia);

        if (!Boolean.TRUE.equals(estabaResuelta)
                && Boolean.TRUE.equals(incidenciaGuardada.getResuelta())) {
            notificarIncidenciaResuelta(incidenciaGuardada);
        }

        return incidenciaGuardada;
    }

    @Override
    public void deleteById(Long id) {
        if (!incidenciaRepository.existsById(id)) {
            throw new IncidenciaNoEncontradaException("Incidencia no encontrada");
        }

        incidenciaRepository.deleteById(id);
    }

    @Override
    public List<Incidencia> findByTipo(TipoIncidencia tipo) {
        return incidenciaRepository.findByTipo(tipo);
    }

    @Override
    public Incidencia marcarComoResuelta(Long id) {

        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new IncidenciaNoEncontradaException("Incidencia no encontrada"));

        // Evitar notificar dos veces si ya estaba resuelta
        if (Boolean.TRUE.equals(incidencia.getResuelta())) {
            return incidencia;
        }

        incidencia.setResuelta(true);
        incidencia.setAccionTomada("Incidencia marcada como resuelta");

        Incidencia incidenciaGuardada = incidenciaRepository.save(incidencia);

        notificarIncidenciaResuelta(incidenciaGuardada);

        return incidenciaGuardada;
    }


    // =========================
    // MÉTODOS AUXILIARES
    // =========================

    private void notificarIncidenciaCreada(Incidencia incidencia) {

        Usuario usuarioAlumno = obtenerUsuarioDelTurno(incidencia.getTurno());

        if (usuarioAlumno != null) {
            notificacionService.crearNotificacionInterna(
                    "Incidencia en tu turno",
                    "Se ha registrado una incidencia relacionada con tu turno: " + incidencia.getDescripcion(),
                    TipoNotificacion.INCIDENCIA_CREADA,
                    "INCIDENCIA_" + incidencia.getId(),
                    "/turnos",
                    usuarioAlumno
            );
        }

        List<Usuario> usuariosCentro = obtenerUsuariosCentro();

        notificacionService.crearNotificacionParaUsuarios(
                "Nueva incidencia registrada",
                "Se ha registrado una nueva incidencia: " + incidencia.getDescripcion(),
                TipoNotificacion.INCIDENCIA_INTERNA,
                "INCIDENCIA_" + incidencia.getId(),
                "/incidencias",
                usuariosCentro
        );

        if (incidencia.getTipo() == TipoIncidencia.CRITICA) {
            notificacionService.enviarAvisoEmailCentro(
                    "[SECRETARIA] Nueva incidencia crítica",
                    "Se ha registrado una nueva incidencia crítica.\n\n"
                            + "ID incidencia: " + incidencia.getId() + "\n"
                            + "Tipo: " + incidencia.getTipo() + "\n"
                            + "Descripción: " + incidencia.getDescripcion() + "\n"
                            + "Fecha: " + incidencia.getFecha()
            );
        }
    }

    private void notificarIncidenciaResuelta(Incidencia incidencia) {

        Usuario usuarioAlumno = obtenerUsuarioDelTurno(incidencia.getTurno());

        if (usuarioAlumno != null) {
            notificacionService.crearNotificacionInterna(
                    "Incidencia resuelta",
                    "La incidencia asociada a tu turno ha sido resuelta.",
                    TipoNotificacion.INCIDENCIA_RESUELTA,
                    "INCIDENCIA_" + incidencia.getId(),
                    "/turnos",
                    usuarioAlumno
            );
        }
    }

    private Usuario obtenerUsuarioDelTurno(Turno turno) {

        if (turno != null &&
                turno.getReservaTurno() != null &&
                turno.getReservaTurno().getUsuario() != null) {

            return turno.getReservaTurno().getUsuario();
        }

        return null;
    }

    private List<Usuario> obtenerUsuariosCentro() {

        List<Usuario> usuariosCentro = new ArrayList<>();

        usuariosCentro.addAll(usuarioRepository.findByRol(RolUsuario.ADMIN));
        usuariosCentro.addAll(usuarioRepository.findByRol(RolUsuario.SECRETARIA));
        usuariosCentro.addAll(usuarioRepository.findByRol(RolUsuario.CONSERJE));

        return usuariosCentro;
    }
}