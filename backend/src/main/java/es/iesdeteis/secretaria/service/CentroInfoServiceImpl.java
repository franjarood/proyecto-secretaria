package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.CentroInfoRequestDTO;
import es.iesdeteis.secretaria.dto.CentroInfoResponseDTO;
import es.iesdeteis.secretaria.dto.TipoTramitePublicoDTO;
import es.iesdeteis.secretaria.exception.CentroInfoNoEncontradoException;
import es.iesdeteis.secretaria.model.CentroInfo;
import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.repository.CentroInfoRepository;
import es.iesdeteis.secretaria.repository.TipoTramiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CentroInfoServiceImpl implements CentroInfoService {

    // ATRIBUTOS

    private final CentroInfoRepository centroInfoRepository;
    private final TipoTramiteRepository tipoTramiteRepository;


    // CONSTRUCTOR

    public CentroInfoServiceImpl(CentroInfoRepository centroInfoRepository,
                                TipoTramiteRepository tipoTramiteRepository) {
        this.centroInfoRepository = centroInfoRepository;
        this.tipoTramiteRepository = tipoTramiteRepository;
    }


    // MÉTODOS

    @Override
    public CentroInfoResponseDTO obtenerCentroActivo() {
        CentroInfo centro = centroInfoRepository.findFirstByActivoTrue()
                .orElseThrow(() -> new CentroInfoNoEncontradoException("No existe información del centro activa"));

        return convertirAResponseDTO(centro);
    }

    @Override
    public List<TipoTramitePublicoDTO> obtenerTramitesDestacados() {
        return tipoTramiteRepository.findByVisiblePublicamenteTrueAndDestacadoTrue().stream()
                .map(this::convertirTipoTramiteAPublicoDTO)
                .toList();
    }

    @Override
    public List<CentroInfoResponseDTO> findAll() {
        return centroInfoRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public CentroInfoResponseDTO create(CentroInfoRequestDTO dto) {

        CentroInfo centroInfo = new CentroInfo();
        aplicarDTOaEntidad(dto, centroInfo);

        CentroInfo guardado = centroInfoRepository.save(centroInfo);

        if (Boolean.TRUE.equals(guardado.getActivo())) {
            desactivarOtros(guardado.getId());
        }

        return convertirAResponseDTO(guardado);
    }

    @Override
    public CentroInfoResponseDTO update(Long id, CentroInfoRequestDTO dto) {

        CentroInfo centroInfo = centroInfoRepository.findById(id)
                .orElseThrow(() -> new CentroInfoNoEncontradoException("Información del centro no encontrada"));

        aplicarDTOaEntidad(dto, centroInfo);

        CentroInfo guardado = centroInfoRepository.save(centroInfo);

        if (Boolean.TRUE.equals(guardado.getActivo())) {
            desactivarOtros(guardado.getId());
        }

        return convertirAResponseDTO(guardado);
    }


    // MÉTODOS AUXILIARES

    private void desactivarOtros(Long idActivo) {
        List<CentroInfo> todos = centroInfoRepository.findAll();

        for (CentroInfo c : todos) {
            if (c.getId() != null && !c.getId().equals(idActivo) && Boolean.TRUE.equals(c.getActivo())) {
                c.setActivo(false);
                centroInfoRepository.save(c);
            }
        }
    }

    private void aplicarDTOaEntidad(CentroInfoRequestDTO dto, CentroInfo entidad) {
        entidad.setNombreCentro(dto.getNombreCentro());
        entidad.setDireccion(dto.getDireccion());
        entidad.setTelefono(dto.getTelefono());
        entidad.setEmail(dto.getEmail());
        entidad.setHorarioAtencion(dto.getHorarioAtencion());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setUrlWebOficial(dto.getUrlWebOficial());
        entidad.setLatitud(dto.getLatitud());
        entidad.setLongitud(dto.getLongitud());
        entidad.setActivo(dto.getActivo());
    }

    private CentroInfoResponseDTO convertirAResponseDTO(CentroInfo centroInfo) {
        CentroInfoResponseDTO dto = new CentroInfoResponseDTO();

        dto.setId(centroInfo.getId());
        dto.setNombreCentro(centroInfo.getNombreCentro());
        dto.setDireccion(centroInfo.getDireccion());
        dto.setTelefono(centroInfo.getTelefono());
        dto.setEmail(centroInfo.getEmail());
        dto.setHorarioAtencion(centroInfo.getHorarioAtencion());
        dto.setDescripcion(centroInfo.getDescripcion());
        dto.setUrlWebOficial(centroInfo.getUrlWebOficial());
        dto.setLatitud(centroInfo.getLatitud());
        dto.setLongitud(centroInfo.getLongitud());
        dto.setActivo(centroInfo.getActivo());

        return dto;
    }

    private TipoTramitePublicoDTO convertirTipoTramiteAPublicoDTO(TipoTramite tt) {
        return new TipoTramitePublicoDTO(
                tt.getId(),
                tt.getNombre(),
                tt.getDescripcion(),
                tt.getDuracionEstimada(),
                tt.getRequiereDocumentacion()
        );
    }
}

