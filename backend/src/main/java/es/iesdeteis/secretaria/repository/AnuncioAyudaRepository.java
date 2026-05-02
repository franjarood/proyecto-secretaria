package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.AnuncioAyuda;
import es.iesdeteis.secretaria.model.EstadoAnuncioAyuda;
import es.iesdeteis.secretaria.model.TipoAnuncioAyuda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnuncioAyudaRepository extends JpaRepository<AnuncioAyuda, Long> {

    List<AnuncioAyuda> findByEstadoOrderByFechaPublicacionDesc(EstadoAnuncioAyuda estado);

    List<AnuncioAyuda> findByUsuarioIdOrderByFechaPublicacionDesc(Long usuarioId);

    List<AnuncioAyuda> findByTipoAndEstadoOrderByFechaPublicacionDesc(TipoAnuncioAyuda tipo, EstadoAnuncioAyuda estado);

    Optional<AnuncioAyuda> findByIdAndUsuarioId(Long id, Long usuarioId);
}
