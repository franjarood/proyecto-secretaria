package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.Notificacion;
import es.iesdeteis.secretaria.model.TipoNotificacion;
import es.iesdeteis.secretaria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioOrderByCreadaEnDesc(Usuario usuario);

    List<Notificacion> findByUsuarioAndLeidaFalseOrderByCreadaEnDesc(Usuario usuario);

    Long countByUsuarioAndLeidaFalse(Usuario usuario);

    List<Notificacion> findByUsuarioAndLeidaFalse(Usuario usuario);

    boolean existsByReferenciaAndTipoAndUsuarioEmail(
            String referencia,
            TipoNotificacion tipo,
            String email
    );

}