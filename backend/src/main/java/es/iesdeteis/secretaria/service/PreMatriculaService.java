package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.PreMatricula;

import java.util.List;
import java.util.Optional;

public interface PreMatriculaService {

    // Obtener todas
    List<PreMatricula> findAll();

    // Buscar por ID
    Optional<PreMatricula> findById(Long id);

    // Crear
    PreMatricula save(PreMatricula preMatricula);

    // Actualizar
    PreMatricula update(Long id, PreMatricula preMatricula);

    // Eliminar
    void deleteById(Long id);

    // Cambiar estado
    PreMatricula cambiarEstado(Long id, String estado);
}