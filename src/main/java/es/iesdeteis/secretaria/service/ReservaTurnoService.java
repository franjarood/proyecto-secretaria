package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.ReservaTurnoCreateDTO;
import es.iesdeteis.secretaria.model.ReservaTurno;

import java.util.List;
import java.util.Optional;

public interface ReservaTurnoService {

    // Obtener todas las reservas
    List<ReservaTurno> findAll();

    // Buscar reserva por ID
    Optional<ReservaTurno> findById(Long id);

    // Obtener reservas según el rol del usuario autenticado
    List<ReservaTurno> findReservasSegunRol();

    // Obtener una reserva por ID según el rol del usuario autenticado
    Optional<ReservaTurno> findReservaByIdSegunRol(Long id);

    // Crear nueva reserva
    ReservaTurno save(ReservaTurno reservaTurno);

    // Crear nueva reserva desde DTO
    ReservaTurno saveFromDTO(ReservaTurnoCreateDTO dto);

    // Actualizar reserva existente
    ReservaTurno update(Long id, ReservaTurno reservaTurno);

    // Eliminar reserva
    void deleteById(Long id);
}