package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.Ciclo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CicloRepository extends JpaRepository<Ciclo, Long> {

    List<Ciclo> findByActivoTrue();

    Optional<Ciclo> findByCodigo(String codigo);
}
