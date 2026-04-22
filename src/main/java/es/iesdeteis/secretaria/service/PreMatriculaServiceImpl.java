package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.exception.PreMatriculaDuplicadaException;
import es.iesdeteis.secretaria.exception.PreMatriculaNoEncontradaException;
import es.iesdeteis.secretaria.model.EstadoPreMatricula;
import es.iesdeteis.secretaria.model.PreMatricula;
import es.iesdeteis.secretaria.repository.PreMatriculaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreMatriculaServiceImpl implements PreMatriculaService {

    private final PreMatriculaRepository preMatriculaRepository;

    public PreMatriculaServiceImpl(PreMatriculaRepository preMatriculaRepository) {
        this.preMatriculaRepository = preMatriculaRepository;
    }

    @Override
    public List<PreMatricula> findAll() {
        return preMatriculaRepository.findAll();
    }

    @Override
    public Optional<PreMatricula> findById(Long id) {
        return preMatriculaRepository.findById(id);
    }

    @Override
    public PreMatricula save(PreMatricula preMatricula) {

        // Validación: evitar duplicados por DNI
        if (preMatriculaRepository.findByDniAlumno(preMatricula.getDniAlumno()).isPresent()) {
            throw new PreMatriculaDuplicadaException("Ya existe una prematrícula con ese DNI");
        }

        return preMatriculaRepository.save(preMatricula);
    }

    @Override
    public PreMatricula update(Long id, PreMatricula preMatriculaActualizada) {

        PreMatricula preMatricula = preMatriculaRepository.findById(id)
                .orElseThrow(() -> new PreMatriculaNoEncontradaException("PreMatricula no encontrada"));

        // Actualizamos datos
        preMatricula.setNombreAlumno(preMatriculaActualizada.getNombreAlumno());
        preMatricula.setApellidosAlumno(preMatriculaActualizada.getApellidosAlumno());
        preMatricula.setDniAlumno(preMatriculaActualizada.getDniAlumno());
        preMatricula.setEmailAlumno(preMatriculaActualizada.getEmailAlumno());
        preMatricula.setTelefonoAlumno(preMatriculaActualizada.getTelefonoAlumno());
        preMatricula.setCicloSolicitado(preMatriculaActualizada.getCicloSolicitado());
        preMatricula.setCursoSolicitado(preMatriculaActualizada.getCursoSolicitado());
        preMatricula.setModalidad(preMatriculaActualizada.getModalidad());
        preMatricula.setObservaciones(preMatriculaActualizada.getObservaciones());

        return preMatriculaRepository.save(preMatricula);
    }

    @Override
    public void deleteById(Long id) {

        if (!preMatriculaRepository.existsById(id)) {
            throw new PreMatriculaNoEncontradaException("PreMatricula no encontrada");
        }

        preMatriculaRepository.deleteById(id);
    }

    @Override
    public PreMatricula cambiarEstado(Long id, String estado) {

        PreMatricula preMatricula = preMatriculaRepository.findById(id)
                .orElseThrow(() -> new PreMatriculaNoEncontradaException("PreMatricula no encontrada"));

        EstadoPreMatricula nuevoEstado = EstadoPreMatricula.valueOf(estado.toUpperCase());

        preMatricula.setEstado(nuevoEstado);

        return preMatriculaRepository.save(preMatricula);
    }
}