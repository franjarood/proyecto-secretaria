package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.exception.HistorialAccionNoEncontradaException;
import es.iesdeteis.secretaria.model.HistorialAccion;
import es.iesdeteis.secretaria.repository.HistorialAccionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistorialAccionServiceImpl implements HistorialAccionService {

    private final HistorialAccionRepository historialAccionRepository;

    public HistorialAccionServiceImpl(HistorialAccionRepository historialAccionRepository) {
        this.historialAccionRepository = historialAccionRepository;
    }

    @Override
    public List<HistorialAccion> findAll() {
        return historialAccionRepository.findAll();
    }

    @Override
    public Optional<HistorialAccion> findById(Long id) {
        return historialAccionRepository.findById(id);
    }

    @Override
    public HistorialAccion save(HistorialAccion historialAccion) {
        return historialAccionRepository.save(historialAccion);
    }

    @Override
    public HistorialAccion update(Long id, HistorialAccion historialAccionActualizada) {
        HistorialAccion historialAccion = historialAccionRepository.findById(id)
                .orElseThrow(() -> new HistorialAccionNoEncontradaException("Historial no encontrado"));

        historialAccion.setAccion(historialAccionActualizada.getAccion());
        historialAccion.setDescripcion(historialAccionActualizada.getDescripcion());
        historialAccion.setEntidadAfectada(historialAccionActualizada.getEntidadAfectada());
        historialAccion.setIdEntidad(historialAccionActualizada.getIdEntidad());
        historialAccion.setUsuarioResponsable(historialAccionActualizada.getUsuarioResponsable());

        return historialAccionRepository.save(historialAccion);
    }

    @Override
    public void deleteById(Long id) {
        if (!historialAccionRepository.existsById(id)) {
            throw new HistorialAccionNoEncontradaException("Historial no encontrado");
        }

        historialAccionRepository.deleteById(id);
    }

    @Override
    public List<HistorialAccion> findByTurnoId(Long turnoId) {
        return historialAccionRepository.findByEntidadAfectadaAndIdEntidad("Turno", turnoId);
    }
}