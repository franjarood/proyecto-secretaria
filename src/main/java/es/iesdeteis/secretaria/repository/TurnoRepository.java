package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.EstadoTurno;
import es.iesdeteis.secretaria.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

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
}