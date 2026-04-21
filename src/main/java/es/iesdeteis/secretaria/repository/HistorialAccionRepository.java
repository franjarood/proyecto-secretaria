package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.HistorialAccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialAccionRepository extends JpaRepository<HistorialAccion, Long> {
}