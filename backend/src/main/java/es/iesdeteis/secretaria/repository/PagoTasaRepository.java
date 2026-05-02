package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.EstadoPago;
import es.iesdeteis.secretaria.model.PagoTasa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagoTasaRepository extends JpaRepository<PagoTasa, Long> {

    List<PagoTasa> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    Optional<PagoTasa> findByIdAndUsuarioId(Long id, Long usuarioId);

    Optional<PagoTasa> findTopByOrderByIdDesc();

    Optional<PagoTasa> findByStripeSessionId(String stripeSessionId);

    List<PagoTasa> findByEstadoPago(EstadoPago estadoPago);

    List<PagoTasa> findByUsuarioIdAndEstadoPagoIn(Long usuarioId, List<EstadoPago> estados);
}

