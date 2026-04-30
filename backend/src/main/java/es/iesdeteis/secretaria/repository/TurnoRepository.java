package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.EstadoTurno;
import es.iesdeteis.secretaria.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    // Turnos por estado
    List<Turno> findByEstadoTurno(EstadoTurno estadoTurno);

    // Turnos activos (para cola)
    List<Turno> findByEstadoTurnoIn(List<EstadoTurno> estados);

    // Turnos por fecha (muy útil para el día actual)
    List<Turno> findByFechaCita(LocalDate fechaCita);

    // Turnos de un usuario (a través de la reserva)
    List<Turno> findByReservaTurnoUsuarioEmail(String email);

    // Turno por id SOLO si pertenece al usuario
    Optional<Turno> findByIdAndReservaTurnoUsuarioEmail(Long id, String email);

    // Obtener cola ordenada por prioridad y hora de llegada
    @Query("""
        SELECT t
        FROM Turno t
        WHERE t.estadoTurno = :estado
        ORDER BY t.prioridad DESC, t.horaLlegada ASC, t.createdAt ASC
        """)
    List<Turno> findColaOrdenadaPorPrioridad(EstadoTurno estado);

}