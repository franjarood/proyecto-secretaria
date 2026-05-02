package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.AvisoPublico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AvisoPublicoRepository extends JpaRepository<AvisoPublico, Long> {

    @Query("SELECT a FROM AvisoPublico a " +
            "WHERE a.visible = true " +
            "AND (a.fechaInicio IS NULL OR a.fechaInicio <= :ahora) " +
            "AND (a.fechaFin IS NULL OR a.fechaFin >= :ahora) " +
            "ORDER BY a.destacado DESC, a.createdAt DESC")
    List<AvisoPublico> findVisiblesVigentes(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT a FROM AvisoPublico a " +
            "WHERE a.visible = true AND a.destacado = true " +
            "AND (a.fechaInicio IS NULL OR a.fechaInicio <= :ahora) " +
            "AND (a.fechaFin IS NULL OR a.fechaFin >= :ahora) " +
            "ORDER BY a.createdAt DESC")
    List<AvisoPublico> findDestacadosVisiblesVigentes(@Param("ahora") LocalDateTime ahora);
}

