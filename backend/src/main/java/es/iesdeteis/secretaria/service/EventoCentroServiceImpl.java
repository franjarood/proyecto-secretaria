package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.EventoCentroRequestDTO;
import es.iesdeteis.secretaria.dto.EventoCentroResponseDTO;
import es.iesdeteis.secretaria.exception.EventoCentroNoEncontradoException;
import es.iesdeteis.secretaria.model.EventoCentro;
import es.iesdeteis.secretaria.model.HistorialAccion;
import es.iesdeteis.secretaria.model.Usuario;
import es.iesdeteis.secretaria.repository.EventoCentroRepository;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoCentroServiceImpl implements EventoCentroService {

    // ATRIBUTOS

    private final EventoCentroRepository eventoCentroRepository;
    private final HistorialAccionService historialAccionService;
    private final UsuarioRepository usuarioRepository;


    // CONSTRUCTOR

    public EventoCentroServiceImpl(EventoCentroRepository eventoCentroRepository,
                                  HistorialAccionService historialAccionService,
                                  UsuarioRepository usuarioRepository) {
        this.eventoCentroRepository = eventoCentroRepository;
        this.historialAccionService = historialAccionService;
        this.usuarioRepository = usuarioRepository;
    }


    // MÉTODOS

    @Override
    public List<EventoCentroResponseDTO> listarPublicosVisibles() {
        return eventoCentroRepository.findPublicosVisibles().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public List<EventoCentroResponseDTO> listarPublicosProximos() {
        return eventoCentroRepository.findPublicosProximos(LocalDateTime.now()).stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public List<EventoCentroResponseDTO> listarMisEventos() {
        // MVP: devolvemos los eventos públicos visibles. En el futuro se pueden añadir inscripciones.
        return listarPublicosVisibles();
    }

    @Override
    public List<EventoCentroResponseDTO> listarTodos() {
        return eventoCentroRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public EventoCentroResponseDTO obtenerPorId(Long id) {
        EventoCentro evento = eventoCentroRepository.findById(id)
                .orElseThrow(() -> new EventoCentroNoEncontradoException("Evento no encontrado"));

        return convertirAResponseDTO(evento);
    }

    @Override
    public EventoCentroResponseDTO crear(EventoCentroRequestDTO dto) {
        EventoCentro evento = new EventoCentro();
        aplicarDTOaEntidad(dto, evento);

        EventoCentro guardado = eventoCentroRepository.save(evento);

        registrarHistorial("CREAR_EVENTO", "Creado evento: " + guardado.getTitulo(), guardado.getId());

        return convertirAResponseDTO(guardado);
    }

    @Override
    public EventoCentroResponseDTO actualizar(Long id, EventoCentroRequestDTO dto) {
        EventoCentro evento = eventoCentroRepository.findById(id)
                .orElseThrow(() -> new EventoCentroNoEncontradoException("Evento no encontrado"));

        aplicarDTOaEntidad(dto, evento);

        EventoCentro guardado = eventoCentroRepository.save(evento);

        registrarHistorial("ACTUALIZAR_EVENTO", "Actualizado evento: " + guardado.getTitulo(), guardado.getId());

        return convertirAResponseDTO(guardado);
    }

    @Override
    public void eliminar(Long id) {
        EventoCentro evento = eventoCentroRepository.findById(id)
                .orElseThrow(() -> new EventoCentroNoEncontradoException("Evento no encontrado"));

        eventoCentroRepository.deleteById(id);

        registrarHistorial("ELIMINAR_EVENTO", "Eliminado evento: " + evento.getTitulo(), id);
    }


    // MÉTODOS AUXILIARES

    private void aplicarDTOaEntidad(EventoCentroRequestDTO dto, EventoCentro entidad) {
        entidad.setTitulo(dto.getTitulo());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setFechaInicio(dto.getFechaInicio());
        entidad.setFechaFin(dto.getFechaFin());
        entidad.setUbicacion(dto.getUbicacion());
        entidad.setTipoEvento(dto.getTipoEvento());
        entidad.setPublico(dto.getPublico());
        entidad.setVisible(dto.getVisible());
    }

    private EventoCentroResponseDTO convertirAResponseDTO(EventoCentro e) {
        EventoCentroResponseDTO dto = new EventoCentroResponseDTO();
        dto.setId(e.getId());
        dto.setTitulo(e.getTitulo());
        dto.setDescripcion(e.getDescripcion());
        dto.setFechaInicio(e.getFechaInicio());
        dto.setFechaFin(e.getFechaFin());
        dto.setUbicacion(e.getUbicacion());
        dto.setTipoEvento(e.getTipoEvento());
        dto.setPublico(e.getPublico());
        dto.setVisible(e.getVisible());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    private void registrarHistorial(String accion, String descripcion, Long idEntidad) {
        HistorialAccion historialAccion = new HistorialAccion();
        historialAccion.setAccion(accion);
        historialAccion.setDescripcion(descripcion);
        historialAccion.setEntidadAfectada("EventoCentro");
        historialAccion.setIdEntidad(idEntidad);
        historialAccion.setUsuarioResponsable(obtenerIdUsuarioActual());
        historialAccionService.save(historialAccion);
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

