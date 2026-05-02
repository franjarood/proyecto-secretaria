package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.AnuncioMercadoRequestDTO;
import es.iesdeteis.secretaria.dto.AnuncioMercadoResponseDTO;
import es.iesdeteis.secretaria.exception.AnuncioMercadoNoEncontradoException;
import es.iesdeteis.secretaria.exception.AnuncioMercadoNoPerteneceUsuarioException;
import es.iesdeteis.secretaria.model.AnuncioMercado;
import es.iesdeteis.secretaria.model.EstadoAnuncioMercado;
import es.iesdeteis.secretaria.model.HistorialAccion;
import es.iesdeteis.secretaria.model.Usuario;
import es.iesdeteis.secretaria.repository.AnuncioMercadoRepository;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MercadoServiceImpl implements MercadoService {

    private final AnuncioMercadoRepository anuncioMercadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialAccionService historialAccionService;

    public MercadoServiceImpl(AnuncioMercadoRepository anuncioMercadoRepository,
                              UsuarioRepository usuarioRepository,
                              HistorialAccionService historialAccionService) {
        this.anuncioMercadoRepository = anuncioMercadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialAccionService = historialAccionService;
    }

    @Override
    public List<AnuncioMercadoResponseDTO> listarDisponibles() {
        return anuncioMercadoRepository.findByEstadoOrderByFechaPublicacionDesc(EstadoAnuncioMercado.DISPONIBLE).stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public List<AnuncioMercadoResponseDTO> listarMisAnuncios() {
        Usuario actual = obtenerUsuarioActualObligatorio();
        return anuncioMercadoRepository.findByUsuarioIdOrderByFechaPublicacionDesc(actual.getId()).stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public AnuncioMercadoResponseDTO obtenerPorId(Long id) {
        AnuncioMercado anuncio = anuncioMercadoRepository.findById(id)
                .orElseThrow(() -> new AnuncioMercadoNoEncontradoException("Anuncio de mercado no encontrado"));
        return convertirAResponseDTO(anuncio);
    }

    @Override
    public AnuncioMercadoResponseDTO crear(AnuncioMercadoRequestDTO dto) {
        Usuario actual = obtenerUsuarioActualObligatorio();

        AnuncioMercado anuncio = new AnuncioMercado();
        anuncio.setUsuario(actual);
        anuncio.setTitulo(dto.getTitulo());
        anuncio.setDescripcion(dto.getDescripcion());
        anuncio.setPrecio(dto.getPrecio());
        anuncio.setContactoPreferido(dto.getContactoPreferido());
        anuncio.setUbicacion(dto.getUbicacion());
        anuncio.setEstado(EstadoAnuncioMercado.DISPONIBLE);

        AnuncioMercado guardado = anuncioMercadoRepository.save(anuncio);

        registrarHistorial("CREAR_ANUNCIO_MERCADO", "Creado anuncio mercado: " + dto.getTitulo(), guardado.getId());

        return convertirAResponseDTO(guardado);
    }

    @Override
    public AnuncioMercadoResponseDTO actualizar(Long id, AnuncioMercadoRequestDTO dto) {
        AnuncioMercado anuncio = obtenerAnuncioSeguro(id);

        anuncio.setTitulo(dto.getTitulo());
        anuncio.setDescripcion(dto.getDescripcion());
        anuncio.setPrecio(dto.getPrecio());
        anuncio.setContactoPreferido(dto.getContactoPreferido());
        anuncio.setUbicacion(dto.getUbicacion());

        AnuncioMercado guardado = anuncioMercadoRepository.save(anuncio);

        registrarHistorial("ACTUALIZAR_ANUNCIO_MERCADO", "Actualizado anuncio mercado id=" + id, guardado.getId());

        return convertirAResponseDTO(guardado);
    }

    @Override
    public AnuncioMercadoResponseDTO cambiarEstado(Long id, EstadoAnuncioMercado nuevoEstado) {
        AnuncioMercado anuncio = obtenerAnuncioSeguro(id);

        anuncio.setEstado(nuevoEstado);
        AnuncioMercado guardado = anuncioMercadoRepository.save(anuncio);

        registrarHistorial("CAMBIAR_ESTADO_ANUNCIO_MERCADO", "Estado cambiado a " + nuevoEstado + " para anuncio id=" + id, guardado.getId());

        return convertirAResponseDTO(guardado);
    }

    @Override
    public void eliminar(Long id) {
        AnuncioMercado anuncio = obtenerAnuncioSeguro(id);
        anuncioMercadoRepository.delete(anuncio);

        registrarHistorial("ELIMINAR_ANUNCIO_MERCADO", "Eliminado anuncio mercado id=" + id, id);
    }

    // =========================
    // SEGURIDAD (anti-IDOR)
    // =========================

    private AnuncioMercado obtenerAnuncioSeguro(Long id) {
        Usuario actual = obtenerUsuarioActualObligatorio();

        return anuncioMercadoRepository.findByIdAndUsuarioId(id, actual.getId())
                .orElseThrow(() -> new AnuncioMercadoNoPerteneceUsuarioException("No puedes modificar un anuncio que no es tuyo"));
    }

    private Usuario obtenerUsuarioActualObligatorio() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AnuncioMercadoNoPerteneceUsuarioException("Usuario no autenticado");
        }

        String email = auth.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    // =========================
    // MAPEOS
    // =========================

    private AnuncioMercadoResponseDTO convertirAResponseDTO(AnuncioMercado a) {
        AnuncioMercadoResponseDTO dto = new AnuncioMercadoResponseDTO();
        dto.setId(a.getId());
        dto.setUsuarioId(a.getUsuario() != null ? a.getUsuario().getId() : null);
        dto.setUsuarioNombre(a.getUsuario() != null ? a.getUsuario().getNombre() + " " + a.getUsuario().getApellidos() : null);
        dto.setTitulo(a.getTitulo());
        dto.setDescripcion(a.getDescripcion());
        dto.setPrecio(a.getPrecio());
        dto.setEstado(a.getEstado());
        dto.setFechaPublicacion(a.getFechaPublicacion());
        dto.setFechaActualizacion(a.getFechaActualizacion());
        dto.setContactoPreferido(a.getContactoPreferido());
        dto.setUbicacion(a.getUbicacion());
        return dto;
    }

    private void registrarHistorial(String accion, String descripcion, Long idEntidad) {
        HistorialAccion h = new HistorialAccion();
        h.setAccion(accion);
        h.setDescripcion(descripcion);
        h.setEntidadAfectada("Mercado");
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
