package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.Turno;

import java.util.List;
import java.util.Optional;

public interface TurnoService {

    // Obtener todos los turnos
    List<Turno> findAll();

    // Buscar turno por ID
    Optional<Turno> findById(Long id);

    // Crear nuevo turno (reserva)
    Turno save(Turno turno);

    // Actualizar turno existente
    Turno update(Long id, Turno turno);

    // Eliminar turno
    void deleteById(Long id);

    // Calcular duración estimada según los trámites
    Integer calculateEstimatedDuration(Turno turno);

    // Confirmar llegada en kiosko (máx 15 min antes)
    Turno confirmArrival(Long id);

    // Calcular tiempo de espera real en cola
    Integer calculateRealWaitingTime(Long id);
}