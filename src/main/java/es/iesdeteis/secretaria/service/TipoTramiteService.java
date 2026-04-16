package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.TipoTramite;

import java.util.List;
import java.util.Optional;

public interface TipoTramiteService {

    List<TipoTramite> findAll();

    Optional<TipoTramite> findById(Long id);

    TipoTramite save(TipoTramite tipoTramite);

    TipoTramite update(Long id, TipoTramite tipoTramite);

    void deleteById(Long id);
}