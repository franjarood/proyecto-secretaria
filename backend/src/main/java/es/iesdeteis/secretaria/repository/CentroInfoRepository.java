package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.CentroInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CentroInfoRepository extends JpaRepository<CentroInfo, Long> {

	Optional<CentroInfo> findFirstByActivoTrue();
}


