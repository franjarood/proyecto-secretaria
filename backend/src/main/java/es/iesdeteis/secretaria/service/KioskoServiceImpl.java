package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.EstadoTurnoResponseDTO;
import es.iesdeteis.secretaria.dto.KioskoPantallaDTO;
import es.iesdeteis.secretaria.exception.TurnoNoEncontradoException;
import es.iesdeteis.secretaria.model.Turno;
import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.repository.TipoTramiteRepository;
import es.iesdeteis.secretaria.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class KioskoServiceImpl implements KioskoService {

    private final TipoTramiteRepository tipoTramiteRepository;
    private final TurnoRepository turnoRepository;
    private final TurnoService turnoService;

    public KioskoServiceImpl(TipoTramiteRepository tipoTramiteRepository,
                             TurnoRepository turnoRepository,
                             TurnoService turnoService) {
        this.tipoTramiteRepository = tipoTramiteRepository;
        this.turnoRepository = turnoRepository;
        this.turnoService = turnoService;
    }

    @Override
    public KioskoPantallaDTO obtenerPantalla() {

        List<String> tramites = obtenerTramitesDisponibles();

        LocalDate hoy = LocalDate.now();
        List<Turno> turnosHoy = turnoRepository.findByFechaCita(hoy);

        int turnosActivosHoy = (int) turnosHoy.stream()
                .filter(t -> t.getEstadoTurno() != null && t.getEstadoTurno().esActivo())
                .count();

        int tiempoMedioEstimado = 0;
        long conDuracion = turnosHoy.stream().map(Turno::getDuracionEstimada).filter(d -> d != null).count();
        if (conDuracion > 0) {
            int suma = turnosHoy.stream()
                    .map(Turno::getDuracionEstimada)
                    .filter(d -> d != null)
                    .mapToInt(Integer::intValue)
                    .sum();
            tiempoMedioEstimado = (int) Math.round((double) suma / (double) conDuracion);
        }

        List<String> avisos = List.of(
                "Ten a mano tu DNI o identificación.",
                "Si tienes cita previa, confirma tu llegada al llegar al centro."
        );

        return new KioskoPantallaDTO(tramites, turnosActivosHoy, tiempoMedioEstimado, avisos);
    }

    @Override
    public List<String> obtenerTramitesDisponibles() {
        return tipoTramiteRepository.findAll().stream()
                .map(TipoTramite::getNombre)
                .toList();
    }

    @Override
    public EstadoTurnoResponseDTO obtenerEstadoTurno(Long idTurno) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new TurnoNoEncontradoException("Turno no encontrado"));

        int posicion = 0;
        int delante = 0;
        int espera = 0;

        if (turno.getEstadoTurno() != null && turno.getEstadoTurno().esActivo()) {
            posicion = turnoService.getPositionInQueue(idTurno);
            delante = turnoService.getPeopleAhead(idTurno);
            espera = turnoService.calculateRealWaitingTime(idTurno);
        }

        return new EstadoTurnoResponseDTO(posicion, delante, espera, turno.getEstadoTurno().name());
    }
}

