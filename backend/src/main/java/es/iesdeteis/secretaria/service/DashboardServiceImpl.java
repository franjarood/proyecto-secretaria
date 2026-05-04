package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.*;
import es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException;
import es.iesdeteis.secretaria.model.*;
import es.iesdeteis.secretaria.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final TurnoRepository turnoRepository;
    private final ReservaTurnoRepository reservaTurnoRepository;
    private final DocumentoRepository documentoRepository;
    private final NotificacionRepository notificacionRepository;
    private final PreMatriculaRepository preMatriculaRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final TipoTramiteRepository tipoTramiteRepository;
    private final HistorialAccionRepository historialAccionRepository;

    public DashboardServiceImpl(UsuarioRepository usuarioRepository,
                               TurnoRepository turnoRepository,
                               ReservaTurnoRepository reservaTurnoRepository,
                               DocumentoRepository documentoRepository,
                               NotificacionRepository notificacionRepository,
                               PreMatriculaRepository preMatriculaRepository,
                               IncidenciaRepository incidenciaRepository,
                               TipoTramiteRepository tipoTramiteRepository,
                               HistorialAccionRepository historialAccionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.turnoRepository = turnoRepository;
        this.reservaTurnoRepository = reservaTurnoRepository;
        this.documentoRepository = documentoRepository;
        this.notificacionRepository = notificacionRepository;
        this.preMatriculaRepository = preMatriculaRepository;
        this.incidenciaRepository = incidenciaRepository;
        this.tipoTramiteRepository = tipoTramiteRepository;
        this.historialAccionRepository = historialAccionRepository;
    }

    @Override
    public DashboardAlumnoDTO obtenerDashboardAlumno(Long usuarioId) {

        validarAccesoDashboardAlumno(usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        // Turnos activos del alumno (si no hay, lista vacía)
        List<TurnoResponseDTO> turnosActivos = turnoRepository.findAll().stream()
                .filter(t -> t.getReservaTurno() != null && t.getReservaTurno().getUsuario() != null)
                .filter(t -> t.getReservaTurno().getUsuario().getId().equals(usuarioId))
                .filter(t -> t.getEstadoTurno() != null && t.getEstadoTurno().esActivo())
                .map(this::convertirTurnoAResponseDTO)
                .toList();

        // Próxima reserva (la más cercana en el futuro)
        Optional<ReservaTurno> proximaReservaOpt = reservaTurnoRepository.findAll().stream()
                .filter(r -> r.getUsuario() != null && r.getUsuario().getId().equals(usuarioId))
                .filter(r -> r.getFechaCita() != null)
                .sorted(Comparator.comparing(ReservaTurno::getFechaCita)
                        .thenComparing(ReservaTurno::getHoraCita, Comparator.nullsLast(Comparator.naturalOrder())))
                .findFirst();

        ReservaTurnoResponseDTO proximaReserva = proximaReservaOpt
                .map(this::convertirReservaAResponseDTO)
                .orElse(null);

        // Próximo turno (más cercano por fecha/hora)
        Optional<Turno> proximoTurnoOpt = turnoRepository.findAll().stream()
                .filter(t -> t.getReservaTurno() != null && t.getReservaTurno().getUsuario() != null)
                .filter(t -> t.getReservaTurno().getUsuario().getId().equals(usuarioId))
                .filter(t -> t.getFechaCita() != null)
                .sorted(Comparator.comparing(Turno::getFechaCita)
                        .thenComparing(Turno::getHoraCita, Comparator.nullsLast(Comparator.naturalOrder())))
                .findFirst();

        TurnoResponseDTO proximoTurno = proximoTurnoOpt
                .map(this::convertirTurnoAResponseDTO)
                .orElse(null);

        // Documentos pendientes del alumno
        List<DocumentoResponseDTO> documentosPendientes = documentoRepository.findAll().stream()
                .filter(d -> d.getUsuario() != null && d.getUsuario().getId().equals(usuarioId))
                .filter(d -> d.getEstadoRevision() == EstadoDocumento.PENDIENTE
                        || d.getEstadoRevision() == EstadoDocumento.REQUIERE_REVISION
                        || d.getEstadoRevision() == EstadoDocumento.RECHAZADO)
                .map(this::convertirDocumentoAResponseDTO)
                .toList();

        // Notificaciones no leídas
        Long noLeidas = notificacionRepository.findAll().stream()
                .filter(n -> n.getUsuario() != null && n.getUsuario().getId().equals(usuarioId))
                .filter(n -> n.getLeida() != null && !n.getLeida())
                .count();

        // Prematrícula (si existe)
        String estadoPrematricula = preMatriculaRepository.findAll().stream()
                .filter(p -> p.getUsuario() != null && p.getUsuario().getId().equals(usuarioId))
                .map(p -> p.getEstado() != null ? p.getEstado().name() : "PENDIENTE")
                .findFirst()
                .orElse("SIN_PREMATRICULA");

        // Accesos rápidos alumno
        List<AccesoRapidoDTO> accesosRapidos = List.of(
                new AccesoRapidoDTO("Reservar turno", "Solicita una cita", "RESERVAR_TURNO", "/reservar-turno"),
                new AccesoRapidoDTO("Subir documentación", "Aporta tus documentos", "SUBIR_DOCUMENTACION", "/documentos/subir"),
                new AccesoRapidoDTO("Ver mis notificaciones", "Revisa avisos del centro", "VER_NOTIFICACIONES", "/notificaciones"),
                new AccesoRapidoDTO("Confirmar llegada", "Confirma tu llegada al centro", "CONFIRMAR_LLEGADA", "/kiosko/confirmar")
        );

        return new DashboardAlumnoDTO(
                usuario.getNombre(),
                turnosActivos,
                proximaReserva,
                proximoTurno,
                documentosPendientes,
                noLeidas,
                estadoPrematricula,
                accesosRapidos
        );
    }

    @Override
    public DashboardSecretariaDTO obtenerDashboardSecretaria() {

        LocalDate hoy = LocalDate.now();

        long turnosHoy = turnoRepository.findAll().stream()
                .filter(t -> hoy.equals(t.getFechaCita()))
                .count();

        long turnosEnCola = turnoRepository.findAll().stream()
                .filter(t -> t.getEstadoTurno() == EstadoTurno.EN_COLA)
                .count();

        long turnosEnAtencion = turnoRepository.findAll().stream()
                .filter(t -> t.getEstadoTurno() == EstadoTurno.EN_ATENCION)
                .count();

        long incidenciasAbiertas = incidenciaRepository.findAll().stream()
                .filter(i -> i.getResuelta() == null || !i.getResuelta())
                .count();

        long documentosPendientes = documentoRepository.findAll().stream()
                .filter(d -> d.getEstadoRevision() == EstadoDocumento.PENDIENTE
                        || d.getEstadoRevision() == EstadoDocumento.REQUIERE_REVISION)
                .count();

        long prematriculasEnProceso = preMatriculaRepository.findAll().stream()
                .filter(p -> p.getEstado() != null && p.getEstado() != EstadoPreMatricula.VALIDADA && p.getEstado() != EstadoPreMatricula.RECHAZADA)
                .count();

        List<TarjetaResumenDTO> tarjetas = List.of(
                new TarjetaResumenDTO("Turnos de hoy", String.valueOf(turnosHoy), "Total de turnos con cita hoy"),
                new TarjetaResumenDTO("En cola", String.valueOf(turnosEnCola), "Pendientes de llamar"),
                new TarjetaResumenDTO("En atención", String.valueOf(turnosEnAtencion), "Atendiéndose ahora"),
                new TarjetaResumenDTO("Incidencias abiertas", String.valueOf(incidenciasAbiertas), "Pendientes de resolver"),
                new TarjetaResumenDTO("Documentos pendientes", String.valueOf(documentosPendientes), "Pendientes de revisión"),
                new TarjetaResumenDTO("Prematrículas en proceso", String.valueOf(prematriculasEnProceso), "Pendientes de validación" )
        );

        // Actividad reciente: últimas 10 acciones
        List<ActividadRecienteDTO> actividad;
        try {
            actividad = historialAccionRepository.findAll().stream()
                    .sorted(Comparator.comparing(HistorialAccion::getFechaHora, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .limit(10)
                    .map(h -> new ActividadRecienteDTO(
                            h.getAccion(),
                            h.getDescripcion(),
                            h.getFechaHora()
                    ))
                    .toList();
        } catch (Exception ignored) {
            actividad = List.of();
        }

        List<AccesoRapidoDTO> accesosRapidos = List.of(
                new AccesoRapidoDTO("Ver cola", "Consulta la cola actual", "VER_COLA", "/turnos/cola"),
                new AccesoRapidoDTO("Revisar documentos", "Validar o rechazar documentos", "REVISAR_DOCUMENTOS", "/documentos"),
                new AccesoRapidoDTO("Gestionar incidencias", "Ver incidencias abiertas", "GESTIONAR_INCIDENCIAS", "/incidencias"),
                new AccesoRapidoDTO("Llamar siguiente turno", "Avanza al siguiente", "SIGUIENTE_TURNO", "/turnos/siguiente")
        );

        return new DashboardSecretariaDTO(tarjetas, actividad, accesosRapidos);
    }

    @Override
    public DashboardAdminDTO obtenerDashboardAdmin() {

        long usuariosTotales = usuarioRepository.count();

        long alumnosActivos = usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == RolUsuario.ALUMNO)
                .count();

        long empleadosProfesores = usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == RolUsuario.SECRETARIA
                        || u.getRol() == RolUsuario.CONSERJE
                        || u.getRol() == RolUsuario.PROFESOR)
                .count();

        LocalDate hoy = LocalDate.now();
        long turnosHoy = turnoRepository.findAll().stream()
                .filter(t -> hoy.equals(t.getFechaCita()))
                .count();

        long incidenciasAbiertas = incidenciaRepository.findAll().stream()
                .filter(i -> i.getResuelta() == null || !i.getResuelta())
                .count();

        long documentosPendientes = documentoRepository.findAll().stream()
                .filter(d -> d.getEstadoRevision() == EstadoDocumento.PENDIENTE
                        || d.getEstadoRevision() == EstadoDocumento.REQUIERE_REVISION)
                .count();

        List<TarjetaResumenDTO> tarjetas = List.of(
                new TarjetaResumenDTO("Usuarios", String.valueOf(usuariosTotales), "Total de usuarios"),
                new TarjetaResumenDTO("Alumnos", String.valueOf(alumnosActivos), "Usuarios con rol ALUMNO"),
                new TarjetaResumenDTO("Empleados/Profesores", String.valueOf(empleadosProfesores), "Roles del centro"),
                new TarjetaResumenDTO("Turnos hoy", String.valueOf(turnosHoy), "Citas del día"),
                new TarjetaResumenDTO("Incidencias abiertas", String.valueOf(incidenciasAbiertas), "Pendientes"),
                new TarjetaResumenDTO("Documentos pendientes", String.valueOf(documentosPendientes), "Pendientes de revisión")
        );

        String estadoGeneral = "OK";

        List<AccesoRapidoDTO> accesosRapidos = List.of(
                new AccesoRapidoDTO("Gestionar usuarios", "Alta/baja/roles", "GESTIONAR_USUARIOS", "/usuarios"),
                new AccesoRapidoDTO("Gestionar tipos de trámite", "Catálogo", "GESTIONAR_TIPOS_TRAMITE", "/tipos-tramite"),
                new AccesoRapidoDTO("Ver historial", "Acciones del sistema", "VER_HISTORIAL", "/historial"),
                new AccesoRapidoDTO("Configuración", "Ajustes generales", "CONFIGURACION", "/admin/config")
        );

        return new DashboardAdminDTO(tarjetas, estadoGeneral, accesosRapidos);
    }

    @Override
    public DashboardKioskoDTO obtenerDashboardKiosko() {

        List<String> tramites = tipoTramiteRepository.findAll().stream()
                .map(TipoTramite::getNombre)
                .toList();

        LocalDate hoy = LocalDate.now();
        int turnosActivosHoy = (int) turnoRepository.findAll().stream()
                .filter(t -> hoy.equals(t.getFechaCita()))
                .filter(t -> t.getEstadoTurno() != null && t.getEstadoTurno().esActivo())
                .count();

        int tiempoMedioEstimado = 0;
        List<Turno> turnosHoyList = turnoRepository.findAll().stream()
                .filter(t -> hoy.equals(t.getFechaCita()))
                .toList();

        if (!turnosHoyList.isEmpty()) {
            int suma = turnosHoyList.stream()
                    .map(Turno::getDuracionEstimada)
                    .filter(d -> d != null)
                    .mapToInt(Integer::intValue)
                    .sum();
            long conDuracion = turnosHoyList.stream().map(Turno::getDuracionEstimada).filter(d -> d != null).count();
            if (conDuracion > 0) {
                tiempoMedioEstimado = (int) Math.round((double) suma / (double) conDuracion);
            }
        }

        List<String> avisos = List.of(
                "Recuerda tener tu documentación preparada.",
                "Si vienes con cita, confirma tu llegada al llegar al centro."
        );

        List<AccesoRapidoDTO> accesosRapidos = List.of(
                new AccesoRapidoDTO("Sacar turno", "Solicita un turno", "SACAR_TURNO", "/turnos"),
                new AccesoRapidoDTO("Confirmar llegada", "Confirma tu turno", "CONFIRMAR_LLEGADA", "/turnos/{id}/confirmar"),
                new AccesoRapidoDTO("Consultar estado", "Consulta tu posición", "CONSULTAR_ESTADO", "/turnos/{id}/estado")
        );

        return new DashboardKioskoDTO(tramites, turnosActivosHoy, tiempoMedioEstimado, avisos, accesosRapidos);
    }

    // =========================
    // Conversores a DTO (reutilizando estilo existente)
    // =========================

    private TurnoResponseDTO convertirTurnoAResponseDTO(Turno turno) {
        return new TurnoResponseDTO(
                turno.getId(),
                turno.getNumeroTurno(),
                turno.getFechaCita(),
                turno.getHoraCita(),
                turno.getHoraLlegada(),
                turno.getEstadoTurno(),
                turno.getPrioridad(),
                turno.getTipoPrioridad(),
                turno.getOrigenTurno(),
                turno.getObservaciones(),
                turno.getDuracionEstimada(),
                turno.getReingreso(),
                turno.getIncidencia(),
                turno.getPrioridadManual(),
                turno.getMotivoPrioridad(),
                turno.getTiposTramite() != null
                        ? turno.getTiposTramite().stream().map(TipoTramite::getNombre).toList()
                        : List.of(),
                turno.getReservaTurno() != null ? turno.getReservaTurno().getId() : null,
                turno.getCreatedAt(),
                turno.getUpdatedAt()
        );
    }

    private ReservaTurnoResponseDTO convertirReservaAResponseDTO(ReservaTurno reserva) {
        return new ReservaTurnoResponseDTO(
                reserva.getId(),
                reserva.getFechaCita(),
                reserva.getHoraCita(),
                reserva.getCodigoReferencia(),
                reserva.getOrigenTurno(),
                reserva.getEstadoReserva(),
                reserva.getTiposTramite() != null
                        ? reserva.getTiposTramite().stream().map(TipoTramite::getNombre).toList()
                        : List.of(),
                reserva.getCreatedAt(),
                reserva.getUpdatedAt()
        );
    }

    private DocumentoResponseDTO convertirDocumentoAResponseDTO(Documento documento) {
        DocumentoResponseDTO dto = new DocumentoResponseDTO();

        dto.setId(documento.getId());
        dto.setNombreArchivo(documento.getNombreArchivo());
        dto.setTipoDocumento(documento.getTipoDocumento());
        dto.setRutaArchivo(documento.getRutaArchivo());
        dto.setEstadoRevision(documento.getEstadoRevision());
        dto.setComentarioRevision(documento.getComentarioRevision());
        dto.setFechaSubida(documento.getFechaSubida());
        dto.setFechaRevision(documento.getFechaRevision());

        dto.setUsuarioId(documento.getUsuario() != null ? documento.getUsuario().getId() : null);
        dto.setSubidoPorId(documento.getSubidoPor() != null ? documento.getSubidoPor().getId() : null);
        dto.setRevisadoPorId(documento.getRevisadoPor() != null ? documento.getRevisadoPor().getId() : null);

        dto.setPreMatriculaId(documento.getPreMatricula() != null ? documento.getPreMatricula().getId() : null);
        dto.setTurnoId(documento.getTurno() != null ? documento.getTurno().getId() : null);

        return dto;
    }

    // =========================
    // Seguridad (anti-IDOR)
    // =========================

    /**
     * Si el usuario autenticado es ALUMNO, solo puede consultar su propio dashboard.
     * El personal del centro (ADMIN/SECRETARIA/CONSERJE/PROFESOR) puede consultar otros usuarios.
     */
    private void validarAccesoDashboardAlumno(Long usuarioId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || usuarioId == null) {
            return;
        }

        String email = auth.getName();
        Usuario actual = usuarioRepository.findByEmail(email).orElse(null);

        if (actual == null || actual.getRol() == null || actual.getId() == null) {
            return;
        }

        if ((actual.getRol() == RolUsuario.USUARIO || actual.getRol() == RolUsuario.ALUMNO)
                && !actual.getId().equals(usuarioId)) {
            throw new AccessDeniedException("No puedes acceder al dashboard de otro usuario");
        }
    }
}

