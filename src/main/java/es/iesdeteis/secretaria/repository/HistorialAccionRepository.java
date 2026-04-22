package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.HistorialAccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialAccionRepository extends JpaRepository<HistorialAccion, Long> {

    // Buscar historial por entidad y id
    List<HistorialAccion> findByEntidadAfectadaAndIdEntidad(String entidadAfectada, Long idEntidad);
}