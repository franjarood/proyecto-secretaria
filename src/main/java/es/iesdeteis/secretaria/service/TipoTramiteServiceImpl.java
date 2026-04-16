package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.repository.TipoTramiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoTramiteServiceImpl implements TipoTramiteService {

    private final TipoTramiteRepository tipoTramiteRepository;

    public TipoTramiteServiceImpl(TipoTramiteRepository tipoTramiteRepository) {
        this.tipoTramiteRepository = tipoTramiteRepository;
    }

    @Override
    public List<TipoTramite> findAll() {
        return tipoTramiteRepository.findAll();
    }

    @Override
    public Optional<TipoTramite> findById(Long id) {
        return tipoTramiteRepository.findById(id);
    }

    @Override
    public TipoTramite save(TipoTramite tipoTramite) {
        return tipoTramiteRepository.save(tipoTramite);
    }

    @Override
    public TipoTramite update(Long id, TipoTramite tipoTramite) {
        tipoTramite.setId(id);
        return tipoTramiteRepository.save(tipoTramite);
    }

    @Override
    public void deleteById(Long id) {
        tipoTramiteRepository.deleteById(id);
    }
}