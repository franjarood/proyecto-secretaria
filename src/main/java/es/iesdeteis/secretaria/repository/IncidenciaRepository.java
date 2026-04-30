package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.Incidencia;
import es.iesdeteis.secretaria.model.TipoIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    List<Incidencia> findByTipo(TipoIncidencia tipo);

    List<Incidencia> findByResuelta(Boolean resuelta);
}