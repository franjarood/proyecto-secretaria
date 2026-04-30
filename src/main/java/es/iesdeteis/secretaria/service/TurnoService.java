package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.PrioridadTurno;
import es.iesdeteis.secretaria.model.Turno;

import java.util.List;
import java.util.Optional;

public interface TurnoService {

    // Obtener todos los turnos
    List<Turno> findAll();

    // Buscar turno por ID
    Optional<Turno> findById(Long id);

    // Crear nuevo turno
    Turno save(Turno turno);

    // Crear turno a partir de una reserva
    Turno crearTurnoDesdeReserva(Long reservaId);

    // Actualizar turno existente
    Turno update(Long id, Turno turno);

    // Eliminar turno
    void deleteById(Long id);

    // Calcular duración estimada según los trámites
    Integer calculateEstimatedDuration(Turno turno);

    // Confirmar llegada en kiosko
    Turno confirmArrival(Long id);

    // Calcular tiempo de espera real en cola
    Integer calculateRealWaitingTime(Long id);

    // Obtener cola ordenada por prioridad y hora
    List<Turno> getQueue();

    // Obtener posición del turno en la cola
    int getPositionInQueue(Long id);

    // Obtener número de turnos que tiene delante
    int getPeopleAhead(Long id);

    // Cambiar estado del turno
    Turno cambiarEstado(Long id, String estado);

    // Pasar al siguiente turno de la cola
    Turno siguienteTurno();

    // Reanudar turno y devolverlo a la cola
    Turno reanudarTurno(Long id);

    // Obtener turnos según el rol del usuario autenticado
    List<Turno> findTurnosSegunRol();

    // Obtener un turno por ID según el rol del usuario autenticado
    Optional<Turno> findTurnoByIdSegunRol(Long id);

    Turno cambiarPrioridad(Long id, PrioridadTurno tipo, String motivo);
}