package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.Documento;
import es.iesdeteis.secretaria.model.EstadoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    List<Documento> findByUsuarioId(Long usuarioId);

    List<Documento> findBySubidoPorId(Long usuarioId);

    List<Documento> findByPreMatriculaId(Long preMatriculaId);

    List<Documento> findByTurnoId(Long turnoId);

    List<Documento> findByEstadoRevision(EstadoDocumento estadoRevision);

    List<Documento> findByUsuarioIdAndEstadoRevision(Long usuarioId, EstadoDocumento estadoRevision);
}