package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.AvisoPublicoRequestDTO;
import es.iesdeteis.secretaria.dto.AvisoPublicoResponseDTO;
import es.iesdeteis.secretaria.exception.AvisoPublicoNoEncontradoException;
import es.iesdeteis.secretaria.model.AvisoPublico;
import es.iesdeteis.secretaria.repository.AvisoPublicoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvisoPublicoServiceImpl implements AvisoPublicoService {

    private final AvisoPublicoRepository avisoPublicoRepository;

    public AvisoPublicoServiceImpl(AvisoPublicoRepository avisoPublicoRepository) {
        this.avisoPublicoRepository = avisoPublicoRepository;
    }

    @Override
    public List<AvisoPublicoResponseDTO> listarVisiblesVigentes() {
        LocalDateTime ahora = LocalDateTime.now();
        return avisoPublicoRepository.findVisiblesVigentes(ahora)
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public List<AvisoPublicoResponseDTO> listarDestacadosVisiblesVigentes() {
        LocalDateTime ahora = LocalDateTime.now();
        return avisoPublicoRepository.findDestacadosVisiblesVigentes(ahora)
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public List<AvisoPublicoResponseDTO> listarTodos() {
        return avisoPublicoRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public AvisoPublicoResponseDTO obtenerPorId(Long id) {
        AvisoPublico aviso = avisoPublicoRepository.findById(id)
                .orElseThrow(() -> new AvisoPublicoNoEncontradoException("El aviso público no existe"));

        return convertirAResponseDTO(aviso);
    }

    @Override
    public AvisoPublicoResponseDTO crear(AvisoPublicoRequestDTO dto) {
        AvisoPublico aviso = new AvisoPublico();
        aplicarDTO(aviso, dto);

        AvisoPublico guardado = avisoPublicoRepository.save(aviso);
        return convertirAResponseDTO(guardado);
    }

    @Override
    public AvisoPublicoResponseDTO actualizar(Long id, AvisoPublicoRequestDTO dto) {
        AvisoPublico aviso = avisoPublicoRepository.findById(id)
                .orElseThrow(() -> new AvisoPublicoNoEncontradoException("El aviso público no existe"));

        aplicarDTO(aviso, dto);

        AvisoPublico actualizado = avisoPublicoRepository.save(aviso);
        return convertirAResponseDTO(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        AvisoPublico aviso = avisoPublicoRepository.findById(id)
                .orElseThrow(() -> new AvisoPublicoNoEncontradoException("El aviso público no existe"));

        avisoPublicoRepository.delete(aviso);
    }

    // =========================
    // MÉTODOS AUXILIARES
    // =========================

    private void aplicarDTO(AvisoPublico aviso, AvisoPublicoRequestDTO dto) {
        aviso.setTitulo(dto.getTitulo());
        aviso.setContenido(dto.getContenido());
        aviso.setTipoAviso(dto.getTipoAviso());
        aviso.setFechaInicio(dto.getFechaInicio());
        aviso.setFechaFin(dto.getFechaFin());
        aviso.setDestacado(dto.getDestacado());
        aviso.setVisible(dto.getVisible());
    }

    private AvisoPublicoResponseDTO convertirAResponseDTO(AvisoPublico aviso) {
        AvisoPublicoResponseDTO dto = new AvisoPublicoResponseDTO();

        dto.setId(aviso.getId());
        dto.setTitulo(aviso.getTitulo());
        dto.setContenido(aviso.getContenido());
        dto.setTipoAviso(aviso.getTipoAviso());
        dto.setFechaInicio(aviso.getFechaInicio());
        dto.setFechaFin(aviso.getFechaFin());
        dto.setDestacado(aviso.getDestacado());
        dto.setVisible(aviso.getVisible());
        dto.setCreatedAt(aviso.getCreatedAt());
        dto.setUpdatedAt(aviso.getUpdatedAt());

        return dto;
    }
}


