package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.exception.CicloNoEncontradoException;
import es.iesdeteis.secretaria.model.Ciclo;
import es.iesdeteis.secretaria.repository.CicloRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CicloServiceImpl implements CicloService {

    private final CicloRepository cicloRepository;

    public CicloServiceImpl(CicloRepository cicloRepository) {
        this.cicloRepository = cicloRepository;
    }

    @Override
    public List<Ciclo> findAll() {
        return cicloRepository.findAll();
    }

    @Override
    public List<Ciclo> findActivos() {
        return cicloRepository.findByActivoTrue();
    }

    @Override
    public Ciclo findById(Long id) {
        return cicloRepository.findById(id)
                .orElseThrow(() -> new CicloNoEncontradoException("Ciclo no encontrado con id: " + id));
    }

    @Override
    public Ciclo findByCodigo(String codigo) {
        return cicloRepository.findByCodigo(codigo)
                .orElseThrow(() -> new CicloNoEncontradoException("Ciclo no encontrado con código: " + codigo));
    }

    @Override
    public Ciclo save(Ciclo ciclo) {
        return cicloRepository.save(ciclo);
    }

    @Override
    public Ciclo update(Long id, Ciclo ciclo) {
        Ciclo cicloExistente = findById(id);

        cicloExistente.setNombre(ciclo.getNombre());
        cicloExistente.setCodigo(ciclo.getCodigo());
        cicloExistente.setFamiliaProfesional(ciclo.getFamiliaProfesional());
        cicloExistente.setGrado(ciclo.getGrado());
        cicloExistente.setActivo(ciclo.getActivo());

        return cicloRepository.save(cicloExistente);
    }

    @Override
    public void desactivar(Long id) {
        Ciclo ciclo = findById(id);
        ciclo.setActivo(false);
        cicloRepository.save(ciclo);
    }
}
