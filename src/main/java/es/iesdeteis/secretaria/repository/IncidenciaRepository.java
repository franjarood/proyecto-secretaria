package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
}