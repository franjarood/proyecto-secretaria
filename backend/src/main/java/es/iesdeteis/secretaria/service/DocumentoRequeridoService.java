package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.DocumentoRequerido;

import java.util.List;

public interface DocumentoRequeridoService {

    List<DocumentoRequerido> findAll();

    List<DocumentoRequerido> findActivos();

    DocumentoRequerido findById(Long id);

    List<DocumentoRequerido> findByTipoMatriculaId(Long tipoMatriculaId);

    List<DocumentoRequerido> findByCursoId(Long cursoId);

    List<DocumentoRequerido> findByCicloId(Long cicloId);

    DocumentoRequerido save(DocumentoRequerido documentoRequerido);

    DocumentoRequerido update(Long id, DocumentoRequerido documentoRequerido);

    void desactivar(Long id);
}
