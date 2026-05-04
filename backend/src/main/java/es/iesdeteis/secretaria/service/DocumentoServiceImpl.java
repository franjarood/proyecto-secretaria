package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.DocumentoCreateDTO;
import es.iesdeteis.secretaria.dto.DocumentoResponseDTO;
import es.iesdeteis.secretaria.dto.DocumentoRevisionDTO;
import es.iesdeteis.secretaria.exception.*;
import es.iesdeteis.secretaria.model.*;
import es.iesdeteis.secretaria.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentoServiceImpl implements DocumentoService {

    // =========================
    // ATRIBUTOS
    // =========================

    private final DocumentoRepository documentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PreMatriculaRepository preMatriculaRepository;
    private final TurnoRepository turnoRepository;
    private final NotificacionService notificacionService;


    // =========================
    // CONSTRUCTOR
    // =========================

    public DocumentoServiceImpl(DocumentoRepository documentoRepository,
                                UsuarioRepository usuarioRepository,
                                PreMatriculaRepository preMatriculaRepository,
                                TurnoRepository turnoRepository,
                                NotificacionService notificacionService) {
        this.documentoRepository = documentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.preMatriculaRepository = preMatriculaRepository;
        this.turnoRepository = turnoRepository;
        this.notificacionService = notificacionService;
    }


    // =========================
    // MÉTODOS PRINCIPALES
    // =========================

    @Override
    public DocumentoResponseDTO crearDocumento(DocumentoCreateDTO dto) {

        Usuario subidoPor = obtenerUsuarioAutenticado();

        Usuario usuario = dto.getUsuarioId() != null
                ? usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("No existe el usuario con id: " + dto.getUsuarioId()))
                : subidoPor;

        PreMatricula preMatricula = null;
        if (dto.getPreMatriculaId() != null) {
            preMatricula = preMatriculaRepository.findById(dto.getPreMatriculaId())
                    .orElseThrow(() -> new PreMatriculaNoEncontradaException("No existe la prematrícula con id: " + dto.getPreMatriculaId()));
        }

        Turno turno = null;
        if (dto.getTurnoId() != null) {
            turno = turnoRepository.findById(dto.getTurnoId())
                    .orElseThrow(() -> new TurnoNoEncontradoException("No existe el turno con id: " + dto.getTurnoId()));
        }

        Documento documento = new Documento(
                dto.getNombreArchivo(),
                dto.getTipoDocumento(),
                dto.getRutaArchivo(),
                usuario,
                subidoPor,
                preMatricula,
                turno
        );

        Documento documentoGuardado = documentoRepository.save(documento);

        notificacionService.crearNotificacionInterna(
                "Documento subido",
                "Tu documento '" + documentoGuardado.getNombreArchivo() + "' se ha subido correctamente.",
                TipoNotificacion.DOCUMENTO_SUBIDO,
                "DOCUMENTO_" + documentoGuardado.getId(),
                "/documentos/mis-documentos",
                documentoGuardado.getUsuario()
        );

        return convertirADTO(documentoGuardado);
    }

    @Override
    public DocumentoResponseDTO obtenerPorId(Long id) {
        Documento documento = obtenerDocumentoSeguro(id);

        return convertirADTO(documento);
    }

    @Override
    public List<DocumentoResponseDTO> listarTodos() {
        return documentoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public List<DocumentoResponseDTO> listarPorUsuario(Long usuarioId) {
        validarAccesoUsuario(usuarioId);

        return documentoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public List<DocumentoResponseDTO> listarPorPreMatricula(Long preMatriculaId) {
        validarAccesoPreMatricula(preMatriculaId);

        return documentoRepository.findByPreMatriculaId(preMatriculaId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public List<DocumentoResponseDTO> listarPorTurno(Long turnoId) {
        validarAccesoTurno(turnoId);

        return documentoRepository.findByTurnoId(turnoId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public List<DocumentoResponseDTO> listarPorEstado(EstadoDocumento estado) {
        return documentoRepository.findByEstadoRevision(estado)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }


    // =========================
    // MÉTODOS DE REVISIÓN
    // =========================

    @Override
    public DocumentoResponseDTO validarDocumento(Long id, DocumentoRevisionDTO dto) {
        Documento documento = obtenerDocumento(id);

        comprobarDocumentoRevisable(documento);

        Usuario revisadoPor = obtenerUsuarioAutenticado();

        documento.validar(dto.getComentario(), revisadoPor);

        Documento documentoGuardado = documentoRepository.save(documento);

        notificacionService.crearNotificacionInterna(
                "Documento validado",
                "Tu documento '" + documentoGuardado.getNombreArchivo() + "' ha sido validado correctamente.",
                TipoNotificacion.DOCUMENTO_VALIDADO,
                "DOCUMENTO_" + documentoGuardado.getId(),
                "/documentos/mis-documentos",
                documentoGuardado.getUsuario()
        );

        return convertirADTO(documentoGuardado);
    }

    @Override
    public DocumentoResponseDTO rechazarDocumento(Long id, DocumentoRevisionDTO dto) {
        Documento documento = obtenerDocumento(id);

        comprobarDocumentoRevisable(documento);

        Usuario revisadoPor = obtenerUsuarioAutenticado();

        documento.rechazar(dto.getComentario(), revisadoPor);

        Documento documentoGuardado = documentoRepository.save(documento);

        notificacionService.crearNotificacionInterna(
                "Documento rechazado",
                "Tu documento '" + documentoGuardado.getNombreArchivo() + "' ha sido rechazado. Revisa el comentario de revisión.",
                TipoNotificacion.DOCUMENTO_RECHAZADO,
                "DOCUMENTO_" + documentoGuardado.getId(),
                "/documentos/mis-documentos",
                documentoGuardado.getUsuario()
        );

        return convertirADTO(documentoGuardado);
    }

    @Override
    public DocumentoResponseDTO marcarRequiereRevision(Long id, DocumentoRevisionDTO dto) {
        Documento documento = obtenerDocumento(id);

        Usuario revisadoPor = obtenerUsuarioAutenticado();

        documento.marcarRequiereRevision(dto.getComentario(), revisadoPor);

        Documento documentoGuardado = documentoRepository.save(documento);

        notificacionService.crearNotificacionInterna(
                "Documento requiere revisión",
                "Tu documento '" + documentoGuardado.getNombreArchivo() + "' requiere revisión. Revisa el comentario indicado.",
                TipoNotificacion.DOCUMENTO_REQUIERE_REVISION,
                "DOCUMENTO_" + documentoGuardado.getId(),
                "/documentos/mis-documentos",
                documentoGuardado.getUsuario()
        );

        return convertirADTO(documentoGuardado);
    }


    // =========================
    // ELIMINAR
    // =========================

    @Override
    public void eliminar(Long id) {
        Documento documento = obtenerDocumentoSeguro(id);

        documentoRepository.delete(documento);
    }


    // =========================
    // MÉTODOS AUXILIARES
    // =========================

    private Documento obtenerDocumento(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new DocumentoNoEncontradoException("No existe el documento con id: " + id));
    }

    private Documento obtenerDocumentoSeguro(Long id) {

        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new DocumentoNoEncontradoException("No existe el documento con id: " + id));

        Usuario usuarioActual = obtenerUsuarioAutenticado();

        // USUARIO y ALUMNO solo pueden ver sus propios documentos
        if (usuarioActual.getRol() == RolUsuario.USUARIO || usuarioActual.getRol() == RolUsuario.ALUMNO) {

            if (documento.getUsuario() == null ||
                    !documento.getUsuario().getId().equals(usuarioActual.getId())) {

                throw new DocumentoNoPerteneceUsuarioException(
                        "No tienes permiso para acceder a este documento"
                );
            }
        }

        return documento;
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No existe el usuario autenticado"));
    }

    private void comprobarDocumentoRevisable(Documento documento) {
        if (documento.getEstadoRevision() == EstadoDocumento.VALIDADO) {
            throw new DocumentoNoRevisableException("El documento ya está validado");
        }
    }

    private DocumentoResponseDTO convertirADTO(Documento documento) {
        DocumentoResponseDTO dto = new DocumentoResponseDTO();

        dto.setId(documento.getId());
        dto.setNombreArchivo(documento.getNombreArchivo());
        dto.setTipoDocumento(documento.getTipoDocumento());
        dto.setRutaArchivo(documento.getRutaArchivo());
        dto.setEstadoRevision(documento.getEstadoRevision());
        dto.setComentarioRevision(documento.getComentarioRevision());
        dto.setFechaSubida(documento.getFechaSubida());
        dto.setFechaRevision(documento.getFechaRevision());

        if (documento.getUsuario() != null) {
            dto.setUsuarioId(documento.getUsuario().getId());
        }

        if (documento.getSubidoPor() != null) {
            dto.setSubidoPorId(documento.getSubidoPor().getId());
        }

        if (documento.getRevisadoPor() != null) {
            dto.setRevisadoPorId(documento.getRevisadoPor().getId());
        }

        if (documento.getPreMatricula() != null) {
            dto.setPreMatriculaId(documento.getPreMatricula().getId());
        }

        if (documento.getTurno() != null) {
            dto.setTurnoId(documento.getTurno().getId());
        }

        return dto;
    }

    @Override
    public List<DocumentoResponseDTO> obtenerMisDocumentos() {

        Usuario usuarioActual = obtenerUsuarioAutenticado();

        return documentoRepository.findByUsuarioId(usuarioActual.getId())
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    private void validarAccesoUsuario(Long usuarioId) {
        Usuario usuarioActual = obtenerUsuarioAutenticado();

        // USUARIO y ALUMNO solo pueden ver sus propios documentos
        if ((usuarioActual.getRol() == RolUsuario.USUARIO || usuarioActual.getRol() == RolUsuario.ALUMNO)
                && !usuarioActual.getId().equals(usuarioId)) {
            throw new DocumentoNoPerteneceUsuarioException(
                    "No tienes permiso para acceder a los documentos de otro usuario"
            );
        }
    }

    private void validarAccesoPreMatricula(Long preMatriculaId) {
        Usuario usuarioActual = obtenerUsuarioAutenticado();

        // USUARIO y ALUMNO solo pueden ver documentos de su propia prematrícula
        if (usuarioActual.getRol() == RolUsuario.USUARIO || usuarioActual.getRol() == RolUsuario.ALUMNO) {
            PreMatricula preMatricula = preMatriculaRepository.findById(preMatriculaId)
                    .orElseThrow(() -> new PreMatriculaNoEncontradaException("Prematrícula no encontrada"));

            if (preMatricula.getUsuario() == null ||
                    !preMatricula.getUsuario().getId().equals(usuarioActual.getId())) {
                throw new DocumentoNoPerteneceUsuarioException(
                        "No tienes permiso para acceder a los documentos de esta prematrícula"
                );
            }
        }
    }

    private void validarAccesoTurno(Long turnoId) {
        Usuario usuarioActual = obtenerUsuarioAutenticado();

        // USUARIO y ALUMNO solo pueden ver documentos de su propio turno
        if (usuarioActual.getRol() == RolUsuario.USUARIO || usuarioActual.getRol() == RolUsuario.ALUMNO) {
            Turno turno = turnoRepository.findById(turnoId)
                    .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

            if (turno.getUsuario() == null ||
                    !turno.getUsuario().getId().equals(usuarioActual.getId())) {
                throw new DocumentoNoPerteneceUsuarioException(
                        "No tienes permiso para acceder a los documentos de este turno"
                );
            }
        }
    }
}