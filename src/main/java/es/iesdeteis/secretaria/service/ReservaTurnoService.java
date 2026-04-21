package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.ReservaTurno;

import java.util.List;
import java.util.Optional;

public interface ReservaTurnoService {

    // Obtener todas las reservas
    List<ReservaTurno> findAll();

    // Buscar reserva por ID
    Optional<ReservaTurno> findById(Long id);

    // Crear nueva reserva
    ReservaTurno save(ReservaTurno reservaTurno);

    // Actualizar reserva existente
    ReservaTurno update(Long id, ReservaTurno reservaTurno);

    // Eliminar reserva
    void deleteById(Long id);
}
