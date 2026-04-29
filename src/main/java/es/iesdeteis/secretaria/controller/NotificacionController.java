package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.NotificacionResponseDTO;
import es.iesdeteis.secretaria.service.NotificacionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    // ATRIBUTOS

    private final NotificacionService notificacionService;


    // CONSTRUCTOR

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }


    // MÉTODOS

    // Obtener las notificaciones del usuario autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mis-notificaciones")
    public List<NotificacionResponseDTO> getMisNotificaciones(Authentication authentication) {

        String emailUsuario = authentication.getName();

        return notificacionService.obtenerMisNotificaciones(emailUsuario);
    }

    // Obtener las notificaciones no leídas del usuario autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mis-notificaciones/no-leidas")
    public List<NotificacionResponseDTO> getMisNotificacionesNoLeidas(Authentication authentication) {

        String emailUsuario = authentication.getName();

        return notificacionService.obtenerMisNotificacionesNoLeidas(emailUsuario);
    }

    // Contar notificaciones no leídas del usuario autenticado
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mis-notificaciones/no-leidas/count")
    public Long contarMisNotificacionesNoLeidas(Authentication authentication) {

        String emailUsuario = authentication.getName();

        return notificacionService.contarMisNotificacionesNoLeidas(emailUsuario);
    }


    // Marcar todas las notificaciones como leídas
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/mis-notificaciones/marcar-todas-leidas")
    public void marcarTodasComoLeidas(Authentication authentication) {

        String emailUsuario = authentication.getName();

        notificacionService.marcarTodasComoLeidas(emailUsuario);
    }


    // Marcar una notificación como leída
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/leida")
    public NotificacionResponseDTO marcarComoLeida(@PathVariable Long id,
                                                   Authentication authentication) {

        String emailUsuario = authentication.getName();

        return notificacionService.marcarComoLeida(id, emailUsuario);
    }
}