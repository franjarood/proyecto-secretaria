package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.ReservaTurnoCreateDTO;
import es.iesdeteis.secretaria.exception.ReservaNoEncontradaException;
import es.iesdeteis.secretaria.model.EstadoReserva;
import es.iesdeteis.secretaria.model.ReservaTurno;
import es.iesdeteis.secretaria.model.TipoNotificacion;
import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.model.Usuario;
import es.iesdeteis.secretaria.repository.ReservaTurnoRepository;
import es.iesdeteis.secretaria.repository.TipoTramiteRepository;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaTurnoServiceImpl implements ReservaTurnoService {

    // ATRIBUTOS

    private final ReservaTurnoRepository reservaTurnoRepository;
    private final TipoTramiteRepository tipoTramiteRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;


    // CONSTRUCTOR

    public ReservaTurnoServiceImpl(ReservaTurnoRepository reservaTurnoRepository,
                                   TipoTramiteRepository tipoTramiteRepository,
                                   UsuarioRepository usuarioRepository,
                                   NotificacionService notificacionService) {
        this.reservaTurnoRepository = reservaTurnoRepository;
        this.tipoTramiteRepository = tipoTramiteRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }


    // MÉTODOS PRINCIPALES

    @Override
    public List<ReservaTurno> findAll() {
        return reservaTurnoRepository.findAll();
    }

    @Override
    public Optional<ReservaTurno> findById(Long id) {
        return reservaTurnoRepository.findById(id);
    }

    @Override
    public List<ReservaTurno> findReservasSegunRol() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        boolean esAdminOSecretaria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_SECRETARIA"));

        if (esAdminOSecretaria) {
            return reservaTurnoRepository.findAll();
        }

        return reservaTurnoRepository.findByUsuarioEmail(email);
    }

    @Override
    public Optional<ReservaTurno> findReservaByIdSegunRol(Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        boolean esAdminOSecretaria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_SECRETARIA"));

        if (esAdminOSecretaria) {
            return reservaTurnoRepository.findById(id);
        }

        return reservaTurnoRepository.findByIdAndUsuarioEmail(id, email);
    }

    @Override
    public ReservaTurno save(ReservaTurno reservaTurno) {

        List<TipoTramite> tramitesCompletos = cargarTramitesCompletos(reservaTurno.getTiposTramite());
        reservaTurno.setTiposTramite(tramitesCompletos);

        if (reservaTurno.getEstadoReserva() == null) {
            reservaTurno.setEstadoReserva(EstadoReserva.PENDIENTE);
        }

        ReservaTurno reservaGuardada = reservaTurnoRepository.save(reservaTurno);

        if (reservaGuardada.getUsuario() != null) {
            notificacionService.crearNotificacionInterna(
                    "Reserva creada",
                    "Tu reserva de turno se ha creado correctamente.",
                    TipoNotificacion.RESERVA_CREADA,
                    "RESERVA_" + reservaGuardada.getId(),
                    "/reservas",
                    reservaGuardada.getUsuario()
            );
        }

        return reservaGuardada;
    }

    @Override
    public ReservaTurno saveFromDTO(ReservaTurnoCreateDTO dto) {

        ReservaTurno reservaTurno = new ReservaTurno();

        reservaTurno.setFechaCita(dto.getFechaCita());
        reservaTurno.setHoraCita(dto.getHoraCita());
        reservaTurno.setOrigenTurno(dto.getOrigenTurno());
        reservaTurno.setEmailContacto(dto.getEmailContacto()); // ✅ email del visitante (kiosko)
        reservaTurno.setEstadoReserva(EstadoReserva.PENDIENTE);

        reservaTurno.setCodigoReferencia("RES-" + System.currentTimeMillis());

        List<TipoTramite> tramitesCompletos = cargarTramitesPorIds(dto.getTiposTramiteIds());
        reservaTurno.setTiposTramite(tramitesCompletos);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        reservaTurno.setUsuario(usuario);

        ReservaTurno reservaGuardada = reservaTurnoRepository.save(reservaTurno);

        notificacionService.crearNotificacionInterna(
                "Reserva creada",
                "Tu reserva de turno se ha creado correctamente. Código: " + reservaGuardada.getCodigoReferencia(),
                TipoNotificacion.RESERVA_CREADA,
                "RESERVA_" + reservaGuardada.getId(),
                "/reservas",
                usuario
        );

        return reservaGuardada;
    }

    @Override
    public ReservaTurno update(Long id, ReservaTurno reservaTurnoActualizada) {
        ReservaTurno reservaTurno = reservaTurnoRepository.findById(id)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada"));

        reservaTurno.setFechaCita(reservaTurnoActualizada.getFechaCita());
        reservaTurno.setHoraCita(reservaTurnoActualizada.getHoraCita());
        reservaTurno.setCodigoReferencia(reservaTurnoActualizada.getCodigoReferencia());
        reservaTurno.setOrigenTurno(reservaTurnoActualizada.getOrigenTurno());
        reservaTurno.setEstadoReserva(reservaTurnoActualizada.getEstadoReserva());

        List<TipoTramite> tramitesCompletos = cargarTramitesCompletos(reservaTurnoActualizada.getTiposTramite());
        reservaTurno.setTiposTramite(tramitesCompletos);

        ReservaTurno reservaGuardada = reservaTurnoRepository.save(reservaTurno);

        if (reservaGuardada.getUsuario() != null) {
            if (reservaGuardada.getEstadoReserva() == EstadoReserva.CONFIRMADA) {
                notificacionService.crearNotificacionInterna(
                        "Reserva confirmada",
                        "Tu reserva de turno ha sido confirmada.",
                        TipoNotificacion.RESERVA_CONFIRMADA,
                        "RESERVA_" + reservaGuardada.getId(),
                        "/reservas",
                        reservaGuardada.getUsuario()
                );
            } else if (reservaGuardada.getEstadoReserva() == EstadoReserva.CANCELADA) {
                notificacionService.crearNotificacionInterna(
                        "Reserva cancelada",
                        "Tu reserva de turno ha sido cancelada.",
                        TipoNotificacion.RESERVA_CANCELADA,
                        "RESERVA_" + reservaGuardada.getId(),
                        "/reservas",
                        reservaGuardada.getUsuario()
                );
            }
        }

        return reservaGuardada;
    }

    @Override
    public void deleteById(Long id) {
        if (!reservaTurnoRepository.existsById(id)) {
            throw new ReservaNoEncontradaException("Reserva no encontrada");
        }

        reservaTurnoRepository.deleteById(id);
    }


    // MÉTODOS AUXILIARES

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

    private List<TipoTramite> cargarTramitesPorIds(List<Long> tiposTramiteIds) {
        List<TipoTramite> tramitesCompletos = new ArrayList<>();

        if (tiposTramiteIds != null) {
            for (Long id : tiposTramiteIds) {
                TipoTramite tramiteBD = tipoTramiteRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tipo de trámite no encontrado"));
                tramitesCompletos.add(tramiteBD);
            }
        }

        return tramitesCompletos;
    }
}