package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.TipoMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoMatriculaRepository extends JpaRepository<TipoMatricula, Long> {

    List<TipoMatricula> findByActivoTrue();
}
