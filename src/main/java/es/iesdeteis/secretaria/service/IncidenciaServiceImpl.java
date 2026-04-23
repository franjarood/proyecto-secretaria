package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.IncidenciaCreateDTO;
import es.iesdeteis.secretaria.exception.IncidenciaNoEncontradaException;
import es.iesdeteis.secretaria.exception.TurnoNoEncontradoException;
import es.iesdeteis.secretaria.model.Incidencia;
import es.iesdeteis.secretaria.model.Turno;
import es.iesdeteis.secretaria.repository.IncidenciaRepository;
import es.iesdeteis.secretaria.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncidenciaServiceImpl implements IncidenciaService {

    // ATRIBUTOS

    private final IncidenciaRepository incidenciaRepository;
    private final TurnoRepository turnoRepository;


    // CONSTRUCTOR

    public IncidenciaServiceImpl(IncidenciaRepository incidenciaRepository, TurnoRepository turnoRepository) {
        this.incidenciaRepository = incidenciaRepository;
        this.turnoRepository = turnoRepository;
    }


    // MÉTODOS

    @Override
    public List<Incidencia> findAll() {
        return incidenciaRepository.findAll();
    }

    @Override
    public Optional<Incidencia> findById(Long id) {
        return incidenciaRepository.findById(id);
    }

    @Override
    public Incidencia save(Incidencia incidencia) {

        if (incidencia.getTurno() != null && incidencia.getTurno().getId() != null) {
            Turno turno = turnoRepository.findById(incidencia.getTurno().getId())
                    .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));
            incidencia.setTurno(turno);
        }

        return incidenciaRepository.save(incidencia);
    }

    @Override
    public Incidencia saveFromDTO(IncidenciaCreateDTO dto) {

        Turno turno = turnoRepository.findById(dto.getTurnoId())
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        Incidencia incidencia = new Incidencia();
        incidencia.setTipo(dto.getTipo());
        incidencia.setDescripcion(dto.getDescripcion());
        incidencia.setResuelta(dto.getResuelta());
        incidencia.setAccionTomada(dto.getAccionTomada());
        incidencia.setTurno(turno);

        return incidenciaRepository.save(incidencia);
    }

    @Override
    public Incidencia update(Long id, Incidencia incidenciaActualizada) {
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new IncidenciaNoEncontradaException("Incidencia no encontrada"));

        incidencia.setTipo(incidenciaActualizada.getTipo());
        incidencia.setDescripcion(incidenciaActualizada.getDescripcion());
        incidencia.setResuelta(incidenciaActualizada.getResuelta());
        incidencia.setAccionTomada(incidenciaActualizada.getAccionTomada());

        if (incidenciaActualizada.getTurno() != null && incidenciaActualizada.getTurno().getId() != null) {
            Turno turno = turnoRepository.findById(incidenciaActualizada.getTurno().getId())
                    .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));
            incidencia.setTurno(turno);
        }

        return incidenciaRepository.save(incidencia);
    }

    @Override
    public void deleteById(Long id) {
        if (!incidenciaRepository.existsById(id)) {
            throw new IncidenciaNoEncontradaException("Incidencia no encontrada");
        }

        incidenciaRepository.deleteById(id);
    }
}