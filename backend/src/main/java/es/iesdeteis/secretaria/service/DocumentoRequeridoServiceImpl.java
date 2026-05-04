package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.exception.DocumentoRequeridoNoEncontradoException;
import es.iesdeteis.secretaria.model.DocumentoRequerido;
import es.iesdeteis.secretaria.repository.DocumentoRequeridoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentoRequeridoServiceImpl implements DocumentoRequeridoService {

    private final DocumentoRequeridoRepository documentoRequeridoRepository;

    public DocumentoRequeridoServiceImpl(DocumentoRequeridoRepository documentoRequeridoRepository) {
        this.documentoRequeridoRepository = documentoRequeridoRepository;
    }

    @Override
    public List<DocumentoRequerido> findAll() {
        return documentoRequeridoRepository.findAll();
    }

    @Override
    public List<DocumentoRequerido> findActivos() {
        return documentoRequeridoRepository.findByActivoTrue();
    }

    @Override
    public DocumentoRequerido findById(Long id) {
        return documentoRequeridoRepository.findById(id)
                .orElseThrow(() -> new DocumentoRequeridoNoEncontradoException("Documento requerido no encontrado con id: " + id));
    }

    @Override
    public List<DocumentoRequerido> findByTipoMatriculaId(Long tipoMatriculaId) {
        return documentoRequeridoRepository.findByTipoMatriculaId(tipoMatriculaId);
    }

    @Override
    public List<DocumentoRequerido> findByCursoId(Long cursoId) {
        return documentoRequeridoRepository.findByCursoId(cursoId);
    }

    @Override
    public List<DocumentoRequerido> findByCicloId(Long cicloId) {
        return documentoRequeridoRepository.findByCicloId(cicloId);
    }

    @Override
    public DocumentoRequerido save(DocumentoRequerido documentoRequerido) {
        return documentoRequeridoRepository.save(documentoRequerido);
    }

    @Override
    public DocumentoRequerido update(Long id, DocumentoRequerido documentoRequerido) {
        DocumentoRequerido documentoRequeridoExistente = findById(id);

        documentoRequeridoExistente.setNombre(documentoRequerido.getNombre());
        documentoRequeridoExistente.setDescripcion(documentoRequerido.getDescripcion());
        documentoRequeridoExistente.setObligatorio(documentoRequerido.getObligatorio());
        documentoRequeridoExistente.setActivo(documentoRequerido.getActivo());
        documentoRequeridoExistente.setTipoMatricula(documentoRequerido.getTipoMatricula());
        documentoRequeridoExistente.setCurso(documentoRequerido.getCurso());
        documentoRequeridoExistente.setCiclo(documentoRequerido.getCiclo());

        return documentoRequeridoRepository.save(documentoRequeridoExistente);
    }

    @Override
    public void desactivar(Long id) {
        DocumentoRequerido documentoRequerido = findById(id);
        documentoRequerido.setActivo(false);
        documentoRequeridoRepository.save(documentoRequerido);
    }
}
