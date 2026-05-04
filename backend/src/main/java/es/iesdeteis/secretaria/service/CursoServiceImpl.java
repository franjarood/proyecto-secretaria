package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.exception.CursoNoEncontradoException;
import es.iesdeteis.secretaria.model.Curso;
import es.iesdeteis.secretaria.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;

    public CursoServiceImpl(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Override
    public List<Curso> findAll() {
        return cursoRepository.findAll();
    }

    @Override
    public List<Curso> findActivos() {
        return cursoRepository.findByActivoTrue();
    }

    @Override
    public Curso findById(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new CursoNoEncontradoException("Curso no encontrado con id: " + id));
    }

    @Override
    public List<Curso> findByCicloId(Long cicloId) {
        return cursoRepository.findByCicloId(cicloId);
    }

    @Override
    public Curso save(Curso curso) {
        return cursoRepository.save(curso);
    }

    @Override
    public Curso update(Long id, Curso curso) {
        Curso cursoExistente = findById(id);

        cursoExistente.setNombre(curso.getNombre());
        cursoExistente.setDescripcion(curso.getDescripcion());
        cursoExistente.setNivel(curso.getNivel());
        cursoExistente.setActivo(curso.getActivo());
        cursoExistente.setCiclo(curso.getCiclo());

        return cursoRepository.save(cursoExistente);
    }

    @Override
    public void desactivar(Long id) {
        Curso curso = findById(id);
        curso.setActivo(false);
        cursoRepository.save(curso);
    }
}
