package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.RespuestaForoRequestDTO;
import es.iesdeteis.secretaria.dto.RespuestaForoResponseDTO;
import es.iesdeteis.secretaria.dto.TemaForoRequestDTO;
import es.iesdeteis.secretaria.dto.TemaForoResponseDTO;
import es.iesdeteis.secretaria.exception.RecursoForoNoPerteneceUsuarioException;
import es.iesdeteis.secretaria.exception.RespuestaForoNoEncontradaException;
import es.iesdeteis.secretaria.exception.TemaForoNoEncontradoException;
import es.iesdeteis.secretaria.model.*;
import es.iesdeteis.secretaria.repository.RespuestaForoRepository;
import es.iesdeteis.secretaria.repository.TemaForoRepository;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForoServiceImpl implements ForoService {

    // ATRIBUTOS

    private final TemaForoRepository temaForoRepository;
    private final RespuestaForoRepository respuestaForoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialAccionService historialAccionService;


    // CONSTRUCTOR

    public ForoServiceImpl(TemaForoRepository temaForoRepository,
                           RespuestaForoRepository respuestaForoRepository,
                           UsuarioRepository usuarioRepository,
                           HistorialAccionService historialAccionService) {
        this.temaForoRepository = temaForoRepository;
        this.respuestaForoRepository = respuestaForoRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialAccionService = historialAccionService;
    }


    // MÉTODOS

    @Override
    public List<TemaForoResponseDTO> listarTemas() {
        return temaForoRepository.findByVisibleTrueOrderByFechaCreacionDesc().stream()
                .map(t -> convertirTemaAResponseDTO(t, false))
                .toList();
    }

    @Override
    public TemaForoResponseDTO obtenerTema(Long id) {
        TemaForo tema = temaForoRepository.findById(id)
                .orElseThrow(() -> new TemaForoNoEncontradoException("Tema no encontrado"));

        if (!Boolean.TRUE.equals(tema.getVisible()) && !esAdmin()) {
            throw new TemaForoNoEncontradoException("Tema no encontrado");
        }

        return convertirTemaAResponseDTO(tema, true);
    }

    @Override
    public List<TemaForoResponseDTO> listarMisTemas() {
        Usuario actual = obtenerUsuarioActualObligatorio();

        return temaForoRepository.findByAutorIdOrderByFechaCreacionDesc(actual.getId()).stream()
                .map(t -> convertirTemaAResponseDTO(t, false))
                .toList();
    }

    @Override
    public TemaForoResponseDTO crearTema(TemaForoRequestDTO dto) {
        Usuario actual = obtenerUsuarioActualObligatorio();

        TemaForo tema = new TemaForo();
        tema.setTitulo(dto.getTitulo());
        tema.setContenido(dto.getContenido());
        tema.setModulo(dto.getModulo());
        tema.setAutor(actual);
        tema.setEstado(EstadoTemaForo.ABIERTO);
        tema.setVisible(true);

        TemaForo guardado = temaForoRepository.save(tema);
        registrarHistorial("CREAR_TEMA_FORO", "Creado tema de foro: " + guardado.getTitulo(), guardado.getId());

        return convertirTemaAResponseDTO(guardado, false);
    }

    @Override
    public TemaForoResponseDTO actualizarTema(Long id, TemaForoRequestDTO dto) {
        TemaForo tema = temaForoRepository.findById(id)
                .orElseThrow(() -> new TemaForoNoEncontradoException("Tema no encontrado"));

        validarPropiedadTema(tema);

        tema.setTitulo(dto.getTitulo());
        tema.setContenido(dto.getContenido());
        tema.setModulo(dto.getModulo());

        TemaForo guardado = temaForoRepository.save(tema);
        registrarHistorial("ACTUALIZAR_TEMA_FORO", "Actualizado tema de foro: " + guardado.getTitulo(), guardado.getId());

        return convertirTemaAResponseDTO(guardado, false);
    }

    @Override
    public void eliminarTema(Long id) {
        TemaForo tema = temaForoRepository.findById(id)
                .orElseThrow(() -> new TemaForoNoEncontradoException("Tema no encontrado"));

        validarPropiedadTema(tema);

        tema.setVisible(false);
        temaForoRepository.save(tema);

        registrarHistorial("ELIMINAR_TEMA_FORO", "Eliminado tema de foro: " + tema.getTitulo(), tema.getId());
    }

    @Override
    public RespuestaForoResponseDTO crearRespuesta(Long temaId, RespuestaForoRequestDTO dto) {
        Usuario actual = obtenerUsuarioActualObligatorio();

        TemaForo tema = temaForoRepository.findById(temaId)
                .orElseThrow(() -> new TemaForoNoEncontradoException("Tema no encontrado"));

        if (!Boolean.TRUE.equals(tema.getVisible())) {
            throw new TemaForoNoEncontradoException("Tema no encontrado");
        }

        RespuestaForo respuesta = new RespuestaForo();
        respuesta.setTema(tema);
        respuesta.setAutor(actual);
        respuesta.setContenido(dto.getContenido());
        respuesta.setMejorRespuesta(false);
        respuesta.setVisible(true);

        RespuestaForo guardada = respuestaForoRepository.save(respuesta);

        registrarHistorial("RESPONDER_TEMA_FORO", "Respuesta añadida al temaId=" + temaId, guardada.getId());

        return convertirRespuestaAResponseDTO(guardada);
    }

    @Override
    public RespuestaForoResponseDTO marcarMejorRespuesta(Long respuestaId) {
        RespuestaForo respuesta = respuestaForoRepository.findById(respuestaId)
                .orElseThrow(() -> new RespuestaForoNoEncontradaException("Respuesta no encontrada"));

        TemaForo tema = respuesta.getTema();
        if (tema == null) {
            throw new TemaForoNoEncontradoException("Tema no encontrado");
        }

        validarPropiedadTema(tema);

        // Desmarcar otra mejor respuesta si existía
        respuestaForoRepository.findByTemaIdAndMejorRespuestaTrue(tema.getId())
                .ifPresent(r -> {
                    if (!r.getId().equals(respuesta.getId())) {
                        r.setMejorRespuesta(false);
                        respuestaForoRepository.save(r);
                    }
                });

        respuesta.setMejorRespuesta(true);
        RespuestaForo guardada = respuestaForoRepository.save(respuesta);

        tema.setEstado(EstadoTemaForo.RESUELTO);
        temaForoRepository.save(tema);

        registrarHistorial("MEJOR_RESPUESTA_FORO", "Marcada mejor respuesta en temaId=" + tema.getId(), guardada.getId());

        return convertirRespuestaAResponseDTO(guardada);
    }

    @Override
    public void eliminarRespuesta(Long respuestaId) {
        RespuestaForo respuesta = respuestaForoRepository.findById(respuestaId)
                .orElseThrow(() -> new RespuestaForoNoEncontradaException("Respuesta no encontrada"));

        validarPropiedadRespuesta(respuesta);

        respuesta.setVisible(false);
        respuestaForoRepository.save(respuesta);

        registrarHistorial("ELIMINAR_RESPUESTA_FORO", "Eliminada respuesta de foro id=" + respuestaId, respuestaId);
    }


    // =========================
    // SEGURIDAD (anti-IDOR)
    // =========================

    private void validarPropiedadTema(TemaForo tema) {
        Usuario actual = obtenerUsuarioActualObligatorio();

        if (esAdmin()) {
            return;
        }

        if (tema.getAutor() == null || tema.getAutor().getId() == null || !tema.getAutor().getId().equals(actual.getId())) {
            throw new RecursoForoNoPerteneceUsuarioException("No puedes modificar un tema que no es tuyo");
        }
    }

    private void validarPropiedadRespuesta(RespuestaForo respuesta) {
        Usuario actual = obtenerUsuarioActualObligatorio();

        if (esAdmin()) {
            return;
        }

        if (respuesta.getAutor() == null || respuesta.getAutor().getId() == null || !respuesta.getAutor().getId().equals(actual.getId())) {
            throw new RecursoForoNoPerteneceUsuarioException("No puedes modificar una respuesta que no es tuya");
        }
    }

    private boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Usuario obtenerUsuarioActualObligatorio() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RecursoForoNoPerteneceUsuarioException("Usuario no autenticado");
        }

        String email = auth.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException("Usuario no encontrado"));
    }


    // =========================
    // MAPEOS
    // =========================

    private TemaForoResponseDTO convertirTemaAResponseDTO(TemaForo t, boolean incluirRespuestas) {
        TemaForoResponseDTO dto = new TemaForoResponseDTO();
        dto.setId(t.getId());
        dto.setTitulo(t.getTitulo());
        dto.setContenido(t.getContenido());
        dto.setModulo(t.getModulo());
        dto.setFechaCreacion(t.getFechaCreacion());
        dto.setFechaActualizacion(t.getFechaActualizacion());
        dto.setEstado(t.getEstado());
        dto.setVisible(t.getVisible());

        if (t.getAutor() != null) {
            dto.setAutorId(t.getAutor().getId());
            dto.setAutorNombre(t.getAutor().getNombre() + " " + t.getAutor().getApellidos());
        }

        if (incluirRespuestas) {
            dto.setRespuestas(respuestaForoRepository.findByTemaIdAndVisibleTrueOrderByFechaCreacionAsc(t.getId()).stream()
                    .map(this::convertirRespuestaAResponseDTO)
                    .toList());
        }

        return dto;
    }

    private RespuestaForoResponseDTO convertirRespuestaAResponseDTO(RespuestaForo r) {
        RespuestaForoResponseDTO dto = new RespuestaForoResponseDTO();
        dto.setId(r.getId());
        dto.setTemaId(r.getTema() != null ? r.getTema().getId() : null);
        dto.setContenido(r.getContenido());
        dto.setFechaCreacion(r.getFechaCreacion());
        dto.setMejorRespuesta(r.getMejorRespuesta());
        dto.setVisible(r.getVisible());

        if (r.getAutor() != null) {
            dto.setAutorId(r.getAutor().getId());
            dto.setAutorNombre(r.getAutor().getNombre() + " " + r.getAutor().getApellidos());
        }

        return dto;
    }

    private void registrarHistorial(String accion, String descripcion, Long idEntidad) {
        HistorialAccion h = new HistorialAccion();
        h.setAccion(accion);
        h.setDescripcion(descripcion);
        h.setEntidadAfectada("Foro");
        h.setIdEntidad(idEntidad);
        h.setUsuarioResponsable(obtenerIdUsuarioActual());
        historialAccionService.save(h);
    }

    private Long obtenerIdUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .map(Usuario::getId)
                .orElse(null);
    }
}

