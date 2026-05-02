package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.EventoCentro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventoCentroRepository extends JpaRepository<EventoCentro, Long> {

    @Query("SELECT e FROM EventoCentro e " +
            "WHERE e.publico = true AND e.visible = true " +
            "ORDER BY e.fechaInicio ASC")
    List<EventoCentro> findPublicosVisibles();

    @Query("SELECT e FROM EventoCentro e " +
            "WHERE e.publico = true AND e.visible = true " +
            "AND (e.fechaInicio IS NULL OR e.fechaInicio >= :ahora) " +
            "ORDER BY e.fechaInicio ASC")
    List<EventoCentro> findPublicosProximos(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT COUNT(e) > 0 FROM EventoCentro e " +
            "WHERE e.publico = true AND e.visible = true " +
            "AND e.fechaInicio IS NOT NULL AND e.fechaInicio <= :limite")
    boolean existsEventoProximo(@Param("limite") LocalDateTime limite);
}

