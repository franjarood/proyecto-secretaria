package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findByActivoTrue();

    List<Curso> findByCicloId(Long cicloId);
}
