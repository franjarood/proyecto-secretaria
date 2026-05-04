package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.Ciclo;

import java.util.List;

public interface CicloService {

    List<Ciclo> findAll();

    List<Ciclo> findActivos();

    Ciclo findById(Long id);

    Ciclo findByCodigo(String codigo);

    Ciclo save(Ciclo ciclo);

    Ciclo update(Long id, Ciclo ciclo);

    void desactivar(Long id);
}
