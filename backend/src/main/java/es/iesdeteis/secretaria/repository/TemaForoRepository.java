package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.TemaForo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemaForoRepository extends JpaRepository<TemaForo, Long> {

    List<TemaForo> findByVisibleTrueOrderByFechaCreacionDesc();

    List<TemaForo> findByAutorIdOrderByFechaCreacionDesc(Long autorId);
}

