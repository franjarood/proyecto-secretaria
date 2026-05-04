package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.TipoMatricula;

import java.util.List;

public interface TipoMatriculaService {

    List<TipoMatricula> findAll();

    List<TipoMatricula> findActivos();

    TipoMatricula findById(Long id);

    TipoMatricula save(TipoMatricula tipoMatricula);

    TipoMatricula update(Long id, TipoMatricula tipoMatricula);

    void desactivar(Long id);
}
