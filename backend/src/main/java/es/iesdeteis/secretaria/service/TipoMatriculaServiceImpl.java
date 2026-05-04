package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.exception.TipoMatriculaNoEncontradoException;
import es.iesdeteis.secretaria.model.TipoMatricula;
import es.iesdeteis.secretaria.repository.TipoMatriculaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoMatriculaServiceImpl implements TipoMatriculaService {

    private final TipoMatriculaRepository tipoMatriculaRepository;

    public TipoMatriculaServiceImpl(TipoMatriculaRepository tipoMatriculaRepository) {
        this.tipoMatriculaRepository = tipoMatriculaRepository;
    }

    @Override
    public List<TipoMatricula> findAll() {
        return tipoMatriculaRepository.findAll();
    }

    @Override
    public List<TipoMatricula> findActivos() {
        return tipoMatriculaRepository.findByActivoTrue();
    }

    @Override
    public TipoMatricula findById(Long id) {
        return tipoMatriculaRepository.findById(id)
                .orElseThrow(() -> new TipoMatriculaNoEncontradoException("Tipo de matrícula no encontrado con id: " + id));
    }

    @Override
    public TipoMatricula save(TipoMatricula tipoMatricula) {
        return tipoMatriculaRepository.save(tipoMatricula);
    }

    @Override
    public TipoMatricula update(Long id, TipoMatricula tipoMatricula) {
        TipoMatricula tipoMatriculaExistente = findById(id);

        tipoMatriculaExistente.setNombre(tipoMatricula.getNombre());
        tipoMatriculaExistente.setDescripcion(tipoMatricula.getDescripcion());
        tipoMatriculaExistente.setActivo(tipoMatricula.getActivo());

        return tipoMatriculaRepository.save(tipoMatriculaExistente);
    }

    @Override
    public void desactivar(Long id) {
        TipoMatricula tipoMatricula = findById(id);
        tipoMatricula.setActivo(false);
        tipoMatriculaRepository.save(tipoMatricula);
    }
}
