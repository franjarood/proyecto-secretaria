package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.DocumentoCreateDTO;
import es.iesdeteis.secretaria.dto.DocumentoResponseDTO;
import es.iesdeteis.secretaria.dto.DocumentoRevisionDTO;
import es.iesdeteis.secretaria.dto.UsuarioActualDTO;
import es.iesdeteis.secretaria.model.EstadoDocumento;

import java.util.List;

public interface DocumentoService {

    // Crear documento
    DocumentoResponseDTO crearDocumento(DocumentoCreateDTO dto);

    // Obtener por id
    DocumentoResponseDTO obtenerPorId(Long id);

    // Listar todos
    List<DocumentoResponseDTO> listarTodos();

    List<DocumentoResponseDTO> obtenerMisDocumentos();


    // Filtros
    List<DocumentoResponseDTO> listarPorUsuario(Long usuarioId);
    List<DocumentoResponseDTO> listarPorPreMatricula(Long preMatriculaId);
    List<DocumentoResponseDTO> listarPorTurno(Long turnoId);
    List<DocumentoResponseDTO> listarPorEstado(EstadoDocumento estado);

    // Revisión
    DocumentoResponseDTO validarDocumento(Long id, DocumentoRevisionDTO dto);
    DocumentoResponseDTO rechazarDocumento(Long id, DocumentoRevisionDTO dto);
    DocumentoResponseDTO marcarRequiereRevision(Long id, DocumentoRevisionDTO dto);

    // Eliminar
    void eliminar(Long id);
}