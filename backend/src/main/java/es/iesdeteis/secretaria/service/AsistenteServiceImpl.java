package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.AsistenteRecomendacionDTO;
import es.iesdeteis.secretaria.dto.AsistenteRecomendacionDTO.TipoRecomendacion;
import es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException;
import es.iesdeteis.secretaria.model.EstadoDocumento;
import es.iesdeteis.secretaria.model.Turno;
import es.iesdeteis.secretaria.model.Usuario;
import es.iesdeteis.secretaria.repository.DocumentoRepository;
import es.iesdeteis.secretaria.repository.NotificacionRepository;
import es.iesdeteis.secretaria.repository.PreMatriculaRepository;
import es.iesdeteis.secretaria.repository.TurnoRepository;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AsistenteServiceImpl implements AsistenteService {

    private final UsuarioRepository usuarioRepository;
    private final TurnoRepository turnoRepository;
    private final NotificacionRepository notificacionRepository;
    private final DocumentoRepository documentoRepository;
    private final PreMatriculaRepository preMatriculaRepository;

    public AsistenteServiceImpl(UsuarioRepository usuarioRepository,
                               TurnoRepository turnoRepository,
                               NotificacionRepository notificacionRepository,
                               DocumentoRepository documentoRepository,
                               PreMatriculaRepository preMatriculaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.turnoRepository = turnoRepository;
        this.notificacionRepository = notificacionRepository;
        this.documentoRepository = documentoRepository;
        this.preMatriculaRepository = preMatriculaRepository;
    }

    @Override
    public List<AsistenteRecomendacionDTO> obtenerRecomendacionesUsuario(Long usuarioId) {

        validarAccesoAsistenteUsuario(usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        List<AsistenteRecomendacionDTO> recomendaciones = new ArrayList<>();

        // 1) Si el usuario tiene un turno hoy: recomendar ver estado o confirmar llegada
        boolean tieneTurnoHoy = turnoRepository.findAll().stream()
                .filter(t -> perteneceAUsuario(t, usuarioId))
                .anyMatch(t -> LocalDate.now().equals(t.getFechaCita()));

        if (tieneTurnoHoy) {
            recomendaciones.add(new AsistenteRecomendacionDTO(
                    "Tienes un turno hoy",
                    "Puedes consultar el estado de tu turno o confirmar tu llegada al centro.",
                    TipoRecomendacion.INFO,
                    "VER_ESTADO_O_CONFIRMAR",
                    "/turnos"
            ));
        }

        // 2) Notificaciones no leídas
        long noLeidas = notificacionRepository.findAll().stream()
                .filter(n -> n.getUsuario() != null && n.getUsuario().getId().equals(usuarioId))
                .filter(n -> n.getLeida() != null && !n.getLeida())
                .count();

        if (noLeidas > 0) {
            recomendaciones.add(new AsistenteRecomendacionDTO(
                    "Tienes notificaciones pendientes",
                    "Tienes " + noLeidas + " notificación(es) sin leer.",
                    TipoRecomendacion.AVISO,
                    "VER_NOTIFICACIONES",
                    "/notificaciones"
            ));
        }

        // 3) Documentos pendientes o rechazados
        long docsPendientes = documentoRepository.findAll().stream()
                .filter(d -> d.getUsuario() != null && d.getUsuario().getId().equals(usuarioId))
                .filter(d -> d.getEstadoRevision() == EstadoDocumento.PENDIENTE
                        || d.getEstadoRevision() == EstadoDocumento.REQUIERE_REVISION
                        || d.getEstadoRevision() == EstadoDocumento.RECHAZADO)
                .count();

        if (docsPendientes > 0) {
            recomendaciones.add(new AsistenteRecomendacionDTO(
                    "Documentación pendiente",
                    "Tienes " + docsPendientes + " documento(s) pendiente(s) o con revisión.",
                    TipoRecomendacion.AVISO,
                    "SUBIR_O_CORREGIR_DOCUMENTOS",
                    "/documentos/mis-documentos"
            ));
        }

        // 4) Prematrícula en proceso
        preMatriculaRepository.findAll().stream()
                .filter(p -> p.getUsuario() != null && p.getUsuario().getId().equals(usuarioId))
                .findFirst()
                .ifPresent(p -> recomendaciones.add(new AsistenteRecomendacionDTO(
                        "Estado de tu prematrícula",
                        "Tu prematrícula está en estado: " + (p.getEstado() != null ? p.getEstado().name() : "PENDIENTE"),
                        TipoRecomendacion.INFO,
                        "VER_PREMATRICULA",
                        "/prematricula"
                )));

        // 5) Si no hay nada
        if (recomendaciones.isEmpty()) {
            recomendaciones.add(new AsistenteRecomendacionDTO(
                    "Todo está en orden",
                    "No tienes tareas pendientes ahora mismo. ¡Genial, " + usuario.getNombre() + "!",
                    TipoRecomendacion.EXITO,
                    "SIN_ACCION",
                    ""
            ));
        }

        return recomendaciones;
    }

    private boolean perteneceAUsuario(Turno turno, Long usuarioId) {
        return turno != null
                && turno.getReservaTurno() != null
                && turno.getReservaTurno().getUsuario() != null
                && turno.getReservaTurno().getUsuario().getId().equals(usuarioId);
    }

    // =========================
    // Seguridad (anti-IDOR)
    // =========================

    /**
     * Si el usuario autenticado es ALUMNO, solo puede consultar su propio asistente.
     */
    private void validarAccesoAsistenteUsuario(Long usuarioId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || usuarioId == null) {
            return;
        }

        String email = auth.getName();
        Usuario actual = usuarioRepository.findByEmail(email).orElse(null);

        if (actual == null || actual.getRol() == null || actual.getId() == null) {
            return;
        }

        if ((actual.getRol() == es.iesdeteis.secretaria.model.RolUsuario.USUARIO
                || actual.getRol() == es.iesdeteis.secretaria.model.RolUsuario.ALUMNO)
                && !actual.getId().equals(usuarioId)) {
            throw new AccessDeniedException("No puedes acceder al asistente de otro usuario");
        }
    }
}

