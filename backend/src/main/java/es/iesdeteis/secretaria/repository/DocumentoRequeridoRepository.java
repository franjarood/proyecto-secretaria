package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.DocumentoRequerido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRequeridoRepository extends JpaRepository<DocumentoRequerido, Long> {

    List<DocumentoRequerido> findByActivoTrue();

    List<DocumentoRequerido> findByTipoMatriculaId(Long tipoMatriculaId);

    List<DocumentoRequerido> findByCursoId(Long cursoId);

    List<DocumentoRequerido> findByCicloId(Long cicloId);
}
