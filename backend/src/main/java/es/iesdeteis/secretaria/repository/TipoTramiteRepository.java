package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.TipoTramite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoTramiteRepository extends JpaRepository<TipoTramite, Long> {

	List<TipoTramite> findByVisiblePublicamenteTrueAndDestacadoTrue();

	List<TipoTramite> findByVisiblePublicamenteTrue();
}