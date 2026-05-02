package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.AnuncioAyudaRequestDTO;
import es.iesdeteis.secretaria.dto.AnuncioAyudaResponseDTO;
import es.iesdeteis.secretaria.exception.AnuncioAyudaNoEncontradoException;
import es.iesdeteis.secretaria.exception.AnuncioAyudaNoPerteneceUsuarioException;
import es.iesdeteis.secretaria.model.*;
import es.iesdeteis.secretaria.repository.AnuncioAyudaRepository;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudyMatchServiceImpl implements StudyMatchService {

    private final AnuncioAyudaRepository anuncioAyudaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialAccionService historialAccionService;

    public StudyMatchServiceImpl(AnuncioAyudaRepository anuncioAyudaRepository,
                                 UsuarioRepository usuarioRepository,
                                 HistorialAccionService historialAccionService) {
        this.anuncioAyudaRepository = anuncioAyudaRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialAccionService = historialAccionService;
    }

    @Override
    public List<AnuncioAyudaResponseDTO> listarAnunciosActivos() {
        return anuncioAyudaRepository.findByEstadoOrderByFechaPublicacionDesc(EstadoAnuncioAyuda.ACTIVO).stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public List<AnuncioAyudaResponseDTO> listarPorTipo(TipoAnuncioAyuda tipo) {
        return anuncioAyudaRepository.findByTipoAndEstadoOrderByFechaPublicacionDesc(tipo, EstadoAnuncioAyuda.ACTIVO).stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public List<AnuncioAyudaResponseDTO> listarMisAnuncios() {
        Usuario actual = obtenerUsuarioActualObligatorio();
        return anuncioAyudaRepository.findByUsuarioIdOrderByFechaPublicacionDesc(actual.getId()).stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public AnuncioAyudaResponseDTO obtenerPorId(Long id) {
        AnuncioAyuda anuncio = anuncioAyudaRepository.findById(id)
                .orElseThrow(() -> new AnuncioAyudaNoEncontradoException("Anuncio de ayuda no encontrado"));
        return convertirAResponseDTO(anuncio);
    }

    @Override
    public AnuncioAyudaResponseDTO crear(AnuncioAyudaRequestDTO dto) {
        Usuario actual = obtenerUsuarioActualObligatorio();

        AnuncioAyuda anuncio = new AnuncioAyuda();
        anuncio.setUsuario(actual);
        anuncio.setTipo(dto.getTipo());
        anuncio.setModulo(dto.getModulo());
        anuncio.setDescripcion(dto.getDescripcion());
        anuncio.setContactoPreferido(dto.getContactoPreferido());
        anuncio.setEstado(EstadoAnuncioAyuda.ACTIVO);

        AnuncioAyuda guardado = anuncioAyudaRepository.save(anuncio);

        registrarHistorial("CREAR_ANUNCIO_AYUDA", "Creado anuncio StudyMatch tipo=" + dto.getTipo(), guardado.getId());

        return convertirAResponseDTO(guardado);
    }

    @Override
    public AnuncioAyudaResponseDTO actualizar(Long id, AnuncioAyudaRequestDTO dto) {
        AnuncioAyuda anuncio = obtenerAnuncioSeguro(id);

        anuncio.setTipo(dto.getTipo());
        anuncio.setModulo(dto.getModulo());
        anuncio.setDescripcion(dto.getDescripcion());
        anuncio.setContactoPreferido(dto.getContactoPreferido());

        AnuncioAyuda guardado = anuncioAyudaRepository.save(anuncio);

        registrarHistorial("ACTUALIZAR_ANUNCIO_AYUDA", "Actualizado anuncio StudyMatch id=" + id, guardado.getId());

        return convertirAResponseDTO(guardado);
    }

    @Override
    public void cerrar(Long id) {
        AnuncioAyuda anuncio = obtenerAnuncioSeguro(id);

        anuncio.setEstado(EstadoAnuncioAyuda.CERRADO);
        anuncio.setFechaCierre(LocalDateTime.now());

        anuncioAyudaRepository.save(anuncio);

        registrarHistorial("CERRAR_ANUNCIO_AYUDA", "Cerrado anuncio StudyMatch id=" + id, anuncio.getId());
    }

    @Override
    public void eliminar(Long id) {
        AnuncioAyuda anuncio = obtenerAnuncioSeguro(id);
        anuncioAyudaRepository.delete(anuncio);

        registrarHistorial("ELIMINAR_ANUNCIO_AYUDA", "Eliminado anuncio StudyMatch id=" + id, id);
    }

    // =========================
    // SEGURIDAD (anti-IDOR)
    // =========================

    private AnuncioAyuda obtenerAnuncioSeguro(Long id) {
        Usuario actual = obtenerUsuarioActualObligatorio();

        return anuncioAyudaRepository.findByIdAndUsuarioId(id, actual.getId())
                .orElseThrow(() -> new AnuncioAyudaNoPerteneceUsuarioException("No puedes modificar un anuncio que no es tuyo"));
    }

    private Usuario obtenerUsuarioActualObligatorio() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AnuncioAyudaNoPerteneceUsuarioException("Usuario no autenticado");
        }

        String email = auth.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    // =========================
    // MAPEOS
    // =========================

    private AnuncioAyudaResponseDTO convertirAResponseDTO(AnuncioAyuda a) {
        AnuncioAyudaResponseDTO dto = new AnuncioAyudaResponseDTO();
        dto.setId(a.getId());
        dto.setUsuarioId(a.getUsuario() != null ? a.getUsuario().getId() : null);
        dto.setUsuarioNombre(a.getUsuario() != null ? a.getUsuario().getNombre() + " " + a.getUsuario().getApellidos() : null);
        dto.setTipo(a.getTipo());
        dto.setModulo(a.getModulo());
        dto.setDescripcion(a.getDescripcion());
        dto.setEstado(a.getEstado());
        dto.setFechaPublicacion(a.getFechaPublicacion());
        dto.setFechaCierre(a.getFechaCierre());
        dto.setContactoPreferido(a.getContactoPreferido());
        return dto;
    }

    private void registrarHistorial(String accion, String descripcion, Long idEntidad) {
        HistorialAccion h = new HistorialAccion();
        h.setAccion(accion);
        h.setDescripcion(descripcion);
        h.setEntidadAfectada("StudyMatch");
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
