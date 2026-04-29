package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.NotificacionCreateDTO;
import es.iesdeteis.secretaria.dto.NotificacionResponseDTO;
import es.iesdeteis.secretaria.model.TipoNotificacion;
import es.iesdeteis.secretaria.model.Usuario;

import java.util.List;

public interface NotificacionService {

    // Crear notificación desde DTO
    NotificacionResponseDTO crearNotificacion(NotificacionCreateDTO dto);

    // Crear notificación interna desde otros módulos
    void crearNotificacionInterna(String titulo,
                                  String mensaje,
                                  TipoNotificacion tipo,
                                  String referencia,
                                  String urlDestino,
                                  Usuario usuario);

    // Obtener notificaciones del usuario autenticado
    List<NotificacionResponseDTO> obtenerMisNotificaciones(String emailUsuario);

    // Obtener notificaciones no leídas del usuario autenticado
    List<NotificacionResponseDTO> obtenerMisNotificacionesNoLeidas(String emailUsuario);

    // Marcar una notificación como leída
    NotificacionResponseDTO marcarComoLeida(Long idNotificacion, String emailUsuario);

    Long contarMisNotificacionesNoLeidas(String emailUsuario);

    void marcarTodasComoLeidas(String emailUsuario);

    void crearNotificacionParaUsuarios(String titulo,
                                       String mensaje,
                                       TipoNotificacion tipo,
                                       String referencia,
                                       String urlDestino,
                                       List<Usuario> usuarios);

}