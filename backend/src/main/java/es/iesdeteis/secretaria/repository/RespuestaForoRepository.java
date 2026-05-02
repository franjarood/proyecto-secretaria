package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.RespuestaForo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RespuestaForoRepository extends JpaRepository<RespuestaForo, Long> {

    List<RespuestaForo> findByTemaIdAndVisibleTrueOrderByFechaCreacionAsc(Long temaId);

    Optional<RespuestaForo> findByTemaIdAndMejorRespuestaTrue(Long temaId);
}

