package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.ReservaTurno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservaTurnoRepository extends JpaRepository<ReservaTurno, Long> {

    List<ReservaTurno> findByUsuarioEmail(String email);

    Optional<ReservaTurno> findByIdAndUsuarioEmail(Long id, String email);
}