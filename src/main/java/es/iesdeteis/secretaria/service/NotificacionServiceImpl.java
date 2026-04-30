package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.NotificacionCreateDTO;
import es.iesdeteis.secretaria.dto.NotificacionResponseDTO;
import es.iesdeteis.secretaria.exception.NotificacionNoEncontradaException;
import es.iesdeteis.secretaria.exception.NotificacionNoPerteneceUsuarioException;
import es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException;
import es.iesdeteis.secretaria.model.Notificacion;
import es.iesdeteis.secretaria.model.TipoNotificacion;
import es.iesdeteis.secretaria.model.Usuario;
import es.iesdeteis.secretaria.repository.NotificacionRepository;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    // =========================
    // ATRIBUTOS
    // =========================

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    @Value("${app.email.centro}")
    private String emailCentro;


    // =========================
    // CONSTRUCTOR
    // =========================

    public NotificacionServiceImpl(NotificacionRepository notificacionRepository,
                                   UsuarioRepository usuarioRepository,
                                   EmailService emailService) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }


    // =========================
    // MÉTODOS PRINCIPALES
    // =========================

    @Override
    public NotificacionResponseDTO crearNotificacion(NotificacionCreateDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("El usuario no existe"));

        Notificacion notificacion = new Notificacion(
                dto.getTitulo(),
                dto.getMensaje(),
                dto.getTipo(),
                dto.getReferencia(),
                dto.getUrlDestino(),
                usuario
        );

        Notificacion notificacionGuardada = notificacionRepository.save(notificacion);

        enviarEmailNotificacion(notificacionGuardada, usuario);

        return convertirAResponseDTO(notificacionGuardada);
    }


    @Override
    public void crearNotificacionInterna(String titulo,
                                         String mensaje,
                                         TipoNotificacion tipo,
                                         String referencia,
                                         String urlDestino,
                                         Usuario usuario) {

        Notificacion notificacion = new Notificacion(
                titulo,
                mensaje,
                tipo,
                referencia,
                urlDestino,
                usuario
        );

        Notificacion notificacionGuardada = notificacionRepository.save(notificacion);

        enviarEmailNotificacion(notificacionGuardada, usuario);
    }


    @Override
    public List<NotificacionResponseDTO> obtenerMisNotificaciones(String emailUsuario) {

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("El usuario no existe"));

        return notificacionRepository.findByUsuarioOrderByCreadaEnDesc(usuario)
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<NotificacionResponseDTO> obtenerMisNotificacionesNoLeidas(String emailUsuario) {

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("El usuario no existe"));

        return notificacionRepository.findByUsuarioAndLeidaFalseOrderByCreadaEnDesc(usuario)
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }


    @Override
    public NotificacionResponseDTO marcarComoLeida(Long idNotificacion, String emailUsuario) {

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("El usuario no existe"));

        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new NotificacionNoEncontradaException("La notificación no existe"));

        if (!notificacion.getUsuario().getId().equals(usuario.getId())) {
            throw new NotificacionNoPerteneceUsuarioException("La notificación no pertenece al usuario autenticado");
        }

        notificacion.marcarComoLeida();

        Notificacion notificacionActualizada = notificacionRepository.save(notificacion);

        return convertirAResponseDTO(notificacionActualizada);
    }


    @Override
    public Long contarMisNotificacionesNoLeidas(String emailUsuario) {

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("El usuario no existe"));

        return notificacionRepository.countByUsuarioAndLeidaFalse(usuario);
    }


    @Override
    public void marcarTodasComoLeidas(String emailUsuario) {

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("El usuario no existe"));

        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioAndLeidaFalse(usuario);

        notificaciones.forEach(Notificacion::marcarComoLeida);

        notificacionRepository.saveAll(notificaciones);
    }


    @Override
    public void crearNotificacionParaUsuarios(String titulo,
                                              String mensaje,
                                              TipoNotificacion tipo,
                                              String referencia,
                                              String urlDestino,
                                              List<Usuario> usuarios) {

        if (usuarios == null || usuarios.isEmpty()) {
            return;
        }

        for (Usuario usuario : usuarios) {
            crearNotificacionInterna(
                    titulo,
                    mensaje,
                    tipo,
                    referencia,
                    urlDestino,
                    usuario
            );
        }
    }

    @Override
    public void enviarAvisoEmailCentro(String asunto, String mensaje) {

        try {
            emailService.enviarEmail(emailCentro, asunto, mensaje);
        } catch (Exception e) {
            System.out.println("No se pudo enviar el email al centro: " + e.getMessage());
        }
    }

    @Override
    public List<NotificacionResponseDTO> obtenerTodasNotificaciones() {
        return notificacionRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }


    // =========================
    // MÉTODOS PRIVADOS
    // =========================

    private void enviarEmailNotificacion(Notificacion notificacion, Usuario usuario) {

        try {
            emailService.enviarEmail(
                    usuario.getEmail(),
                    notificacion.getTitulo(),
                    notificacion.getMensaje()
            );

            notificacion.marcarEmailEnviado();
            notificacionRepository.save(notificacion);

        } catch (Exception e) {
            // Si falla el email, no se rompe el sistema
            System.out.println("No se pudo enviar el email de la notificación: " + e.getMessage());
        }
    }


    private NotificacionResponseDTO convertirAResponseDTO(Notificacion notificacion) {

        NotificacionResponseDTO dto = new NotificacionResponseDTO();

        dto.setId(notificacion.getId());
        dto.setTitulo(notificacion.getTitulo());
        dto.setMensaje(notificacion.getMensaje());
        dto.setTipo(notificacion.getTipo());
        dto.setLeida(notificacion.getLeida());
        dto.setCreadaEn(notificacion.getCreadaEn());
        dto.setReferencia(notificacion.getReferencia());
        dto.setUrlDestino(notificacion.getUrlDestino());
        dto.setEnviadaPorEmail(notificacion.getEnviadaPorEmail());
        dto.setFechaEnvioEmail(notificacion.getFechaEnvioEmail());

        return dto;
    }


}