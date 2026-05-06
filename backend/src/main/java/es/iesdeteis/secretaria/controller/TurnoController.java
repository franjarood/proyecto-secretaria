package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.*;
import es.iesdeteis.secretaria.exception.TurnoNoEncontradoException;
import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.model.Turno;
import es.iesdeteis.secretaria.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turnos")
public class TurnoController {

    // ATRIBUTOS

    private final TurnoService turnoService;


    // CONSTRUCTOR

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }


    // MÉTODOS

    // Obtener turnos según el rol del usuario autenticado
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    @GetMapping
    public List<TurnoResponseDTO> getTurnos() {
        return turnoService.findTurnosSegunRol().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // Obtener turno por ID según el rol del usuario autenticado
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    @GetMapping("/{id}")
    public TurnoResponseDTO getTurnoById(@PathVariable Long id) {
        Turno turno = turnoService.findTurnoByIdSegunRol(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        return convertirAResponseDTO(turno);
    }

    // Crear nuevo turno
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @PostMapping
    public TurnoResponseDTO saveTurno(@Valid @RequestBody Turno turno) {
        Turno turnoGuardado = turnoService.save(turno);
        return convertirAResponseDTO(turnoGuardado);
    }

    // Crear turno a partir de una reserva
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @PostMapping("/desde-reserva/{idReserva}")
    public TurnoResponseDTO crearTurnoDesdeReserva(@PathVariable Long idReserva) {
        Turno turnoCreado = turnoService.crearTurnoDesdeReserva(idReserva);
        return convertirAResponseDTO(turnoCreado);
    }

    // Actualizar turno
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PutMapping("/{id}")
    public TurnoResponseDTO updateTurno(@PathVariable Long id, @Valid @RequestBody Turno turno) {
        Turno turnoActualizado = turnoService.update(id, turno);
        return convertirAResponseDTO(turnoActualizado);
    }

    // Eliminar turno
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteTurno(@PathVariable Long id) {
        turnoService.deleteById(id);
    }

    // Confirmar llegada en kiosko
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @PutMapping("/{id}/confirmar")
    public TurnoResponseDTO confirmArrival(@PathVariable Long id) {
        Turno turnoConfirmado = turnoService.confirmArrival(id);
        return convertirAResponseDTO(turnoConfirmado);
    }

    // Calcular tiempo de espera real
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    @GetMapping("/{id}/espera")
    public Integer getRealWaitingTime(@PathVariable Long id) {
        return turnoService.calculateRealWaitingTime(id);
    }

    // Obtener cola ordenada
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @GetMapping("/cola")
    public List<TurnoColaDTO> getQueue() {
        return turnoService.getQueue().stream()
                .map(t -> new TurnoColaDTO(
                        t.getNumeroTurno(),
                        t.getEstadoTurno().name(),
                        t.getPrioridad(),
                        t.getDuracionEstimada()
                ))
                .toList();
    }

    // Obtener posición del turno en la cola
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    @GetMapping("/{id}/posicion")
    public PosicionTurnoDTO getPosition(@PathVariable Long id) {

        int posicion = turnoService.getPositionInQueue(id);
        int delante = turnoService.getPeopleAhead(id);

        return new PosicionTurnoDTO(posicion, delante);
    }

    // Obtener estado completo del turno
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    @GetMapping("/{id}/estado")
    public EstadoTurnoResponseDTO getEstado(@PathVariable Long id) {

        Turno turno = turnoService.findTurnoByIdSegunRol(id)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        int posicion = 0;
        int delante = 0;
        int espera = 0;

        if (turno.getEstadoTurno() != null && turno.getEstadoTurno().esActivo()) {
            posicion = turnoService.getPositionInQueue(id);
            delante = turnoService.getPeopleAhead(id);
            espera = turnoService.calculateRealWaitingTime(id);
        }

        return new EstadoTurnoResponseDTO(
                posicion,
                delante,
                espera,
                turno.getEstadoTurno().name()
        );
    }

    // Cambiar estado del turno
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @PutMapping("/{id}/estado")
    public TurnoResponseDTO cambiarEstado(@PathVariable Long id,
                                          @Valid @RequestBody EstadoTurnoDTO dto) {
        Turno turnoActualizado = turnoService.cambiarEstado(id, dto.getEstado());
        return convertirAResponseDTO(turnoActualizado);
    }

    // Pasar al siguiente turno de la cola
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @PutMapping("/siguiente")
    public TurnoResponseDTO siguienteTurno() {
        Turno siguiente = turnoService.siguienteTurno();
        return convertirAResponseDTO(siguiente);
    }

    // Reanudar turno y devolverlo a la cola
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @PutMapping("/{id}/reanudar")
    public TurnoResponseDTO reanudarTurno(@PathVariable Long id) {
        Turno turnoReanudado = turnoService.reanudarTurno(id);
        return convertirAResponseDTO(turnoReanudado);
    }

    // Cambiar prioridad del turno
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PutMapping("/{id}/prioridad")
    public TurnoResponseDTO cambiarPrioridad(@PathVariable Long id,
                                             @Valid @RequestBody CambiarPrioridadDTO dto) {

        Turno turnoActualizado = turnoService.cambiarPrioridad(
                id,
                dto.getTipo(),
                dto.getMotivo()
        );

        return convertirAResponseDTO(turnoActualizado);
    }

    // Check-in geolocalizado (adicional)
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    @PostMapping("/{id}/checkin-geo")
    public CheckInGeoResponseDTO checkInGeo(@PathVariable Long id,
                                            @Valid @RequestBody CheckInGeoRequestDTO dto) {
        return turnoService.checkInGeolocalizado(id, dto);
    }


    // MÉTODOS AUXILIARES

    // Convertir entidad Turno a DTO de respuesta
    private TurnoResponseDTO convertirAResponseDTO(Turno turno) {
        return new TurnoResponseDTO(
                turno.getId(),
                turno.getNumeroTurno(),
                turno.getFechaCita(),
                turno.getHoraCita(),
                turno.getHoraLlegada(),
                turno.getEstadoTurno(),
                turno.getPrioridad(),
                turno.getTipoPrioridad(), // 👈 ESTO ES LO NUEVO
                turno.getOrigenTurno(),
                turno.getObservaciones(),
                turno.getDuracionEstimada(),
                turno.getReingreso(),
                turno.getIncidencia(),
                turno.getPrioridadManual(),
                turno.getMotivoPrioridad(),
                turno.getTiposTramite() != null
                        ? turno.getTiposTramite().stream().map(TipoTramite::getNombre).toList()
                        : List.of(),
                turno.getReservaTurno() != null ? turno.getReservaTurno().getId() : null,
                turno.getCreatedAt(),
                turno.getUpdatedAt()
        );
    }
}