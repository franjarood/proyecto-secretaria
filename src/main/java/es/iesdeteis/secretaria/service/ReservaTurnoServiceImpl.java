package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.exception.ReservaNoEncontradaException;
import es.iesdeteis.secretaria.model.EstadoReserva;
import es.iesdeteis.secretaria.model.ReservaTurno;
import es.iesdeteis.secretaria.model.TipoTramite;
import es.iesdeteis.secretaria.repository.ReservaTurnoRepository;
import es.iesdeteis.secretaria.repository.TipoTramiteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaTurnoServiceImpl implements ReservaTurnoService {

    private final ReservaTurnoRepository reservaTurnoRepository;
    private final TipoTramiteRepository tipoTramiteRepository;

    public ReservaTurnoServiceImpl(ReservaTurnoRepository reservaTurnoRepository,
                                   TipoTramiteRepository tipoTramiteRepository) {
        this.reservaTurnoRepository = reservaTurnoRepository;
        this.tipoTramiteRepository = tipoTramiteRepository;
    }

    @Override
    public List<ReservaTurno> findAll() {
        return reservaTurnoRepository.findAll();
    }

    @Override
    public Optional<ReservaTurno> findById(Long id) {
        return reservaTurnoRepository.findById(id);
    }

    @Override
    public ReservaTurno save(ReservaTurno reservaTurno) {

        List<TipoTramite> tramitesCompletos = cargarTramitesCompletos(reservaTurno.getTiposTramite());
        reservaTurno.setTiposTramite(tramitesCompletos);

        if (reservaTurno.getEstadoReserva() == null) {
            reservaTurno.setEstadoReserva(EstadoReserva.PENDIENTE);
        }

        return reservaTurnoRepository.save(reservaTurno);
    }

    @Override
    public ReservaTurno update(Long id, ReservaTurno reservaTurnoActualizada) {
        ReservaTurno reservaTurno = reservaTurnoRepository.findById(id)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada"));

        reservaTurno.setFechaCita(reservaTurnoActualizada.getFechaCita());
        reservaTurno.setHoraCita(reservaTurnoActualizada.getHoraCita());
        reservaTurno.setCodigoReferencia(reservaTurnoActualizada.getCodigoReferencia());
        reservaTurno.setOrigenTurno(reservaTurnoActualizada.getOrigenTurno());
        reservaTurno.setEstadoReserva(reservaTurnoActualizada.getEstadoReserva());

        List<TipoTramite> tramitesCompletos = cargarTramitesCompletos(reservaTurnoActualizada.getTiposTramite());
        reservaTurno.setTiposTramite(tramitesCompletos);

        return reservaTurnoRepository.save(reservaTurno);
    }

    @Override
    public void deleteById(Long id) {
        if (!reservaTurnoRepository.existsById(id)) {
            throw new ReservaNoEncontradaException("Reserva no encontrada");
        }

        reservaTurnoRepository.deleteById(id);
    }

    // Cargar trámites completos desde BD
    private List<TipoTramite> cargarTramitesCompletos(List<TipoTramite> tiposTramite) {
        List<TipoTramite> tramitesCompletos = new ArrayList<>();

        if (tiposTramite != null) {
            for (TipoTramite t : tiposTramite) {
                TipoTramite tramiteBD = tipoTramiteRepository.findById(t.getId())
                        .orElseThrow(() -> new RuntimeException("Tipo de trámite no encontrado"));
                tramitesCompletos.add(tramiteBD);
            }
        }

        return tramitesCompletos;
    }
}