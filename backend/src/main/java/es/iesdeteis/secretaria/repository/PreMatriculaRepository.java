package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.PreMatricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreMatriculaRepository extends JpaRepository<PreMatricula, Long> {

    // Buscar prematrícula por DNI del alumno
    Optional<PreMatricula> findByDniAlumno(String dniAlumno);
}