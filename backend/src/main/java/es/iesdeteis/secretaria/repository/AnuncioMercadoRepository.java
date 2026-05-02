package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.AnuncioMercado;
import es.iesdeteis.secretaria.model.EstadoAnuncioMercado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnuncioMercadoRepository extends JpaRepository<AnuncioMercado, Long> {

    List<AnuncioMercado> findByEstadoOrderByFechaPublicacionDesc(EstadoAnuncioMercado estado);

    List<AnuncioMercado> findByUsuarioIdOrderByFechaPublicacionDesc(Long usuarioId);

    Optional<AnuncioMercado> findByIdAndUsuarioId(Long id, Long usuarioId);
}
