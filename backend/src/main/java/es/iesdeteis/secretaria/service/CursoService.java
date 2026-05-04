package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.Curso;

import java.util.List;

public interface CursoService {

    List<Curso> findAll();

    List<Curso> findActivos();

    Curso findById(Long id);

    List<Curso> findByCicloId(Long cicloId);

    Curso save(Curso curso);

    Curso update(Long id, Curso curso);

    void desactivar(Long id);
}
