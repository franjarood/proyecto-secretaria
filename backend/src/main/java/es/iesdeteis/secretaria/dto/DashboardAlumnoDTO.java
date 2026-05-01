package es.iesdeteis.secretaria.dto;

import java.util.List;

public class DashboardAlumnoDTO {

    private String nombreUsuario;
    private List<TurnoResponseDTO> turnosActivos;
    private ReservaTurnoResponseDTO proximaReserva;
    private TurnoResponseDTO proximoTurno;
    private List<DocumentoResponseDTO> documentosPendientes;
    private Long notificacionesNoLeidas;
    private String estadoPrematricula;
    private List<AccesoRapidoDTO> accesosRapidos;

    public DashboardAlumnoDTO() {
    }

    public DashboardAlumnoDTO(String nombreUsuario,
                              List<TurnoResponseDTO> turnosActivos,
                              ReservaTurnoResponseDTO proximaReserva,
                              TurnoResponseDTO proximoTurno,
                              List<DocumentoResponseDTO> documentosPendientes,
                              Long notificacionesNoLeidas,
                              String estadoPrematricula,
                              List<AccesoRapidoDTO> accesosRapidos) {
        this.nombreUsuario = nombreUsuario;
        this.turnosActivos = turnosActivos;
        this.proximaReserva = proximaReserva;
        this.proximoTurno = proximoTurno;
        this.documentosPendientes = documentosPendientes;
        this.notificacionesNoLeidas = notificacionesNoLeidas;
        this.estadoPrematricula = estadoPrematricula;
        this.accesosRapidos = accesosRapidos;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public List<TurnoResponseDTO> getTurnosActivos() {
        return turnosActivos;
    }

    public void setTurnosActivos(List<TurnoResponseDTO> turnosActivos) {
        this.turnosActivos = turnosActivos;
    }

    public ReservaTurnoResponseDTO getProximaReserva() {
        return proximaReserva;
    }

    public void setProximaReserva(ReservaTurnoResponseDTO proximaReserva) {
        this.proximaReserva = proximaReserva;
    }

    public TurnoResponseDTO getProximoTurno() {
        return proximoTurno;
    }

    public void setProximoTurno(TurnoResponseDTO proximoTurno) {
        this.proximoTurno = proximoTurno;
    }

    public List<DocumentoResponseDTO> getDocumentosPendientes() {
        return documentosPendientes;
    }

    public void setDocumentosPendientes(List<DocumentoResponseDTO> documentosPendientes) {
        this.documentosPendientes = documentosPendientes;
    }

    public Long getNotificacionesNoLeidas() {
        return notificacionesNoLeidas;
    }

    public void setNotificacionesNoLeidas(Long notificacionesNoLeidas) {
        this.notificacionesNoLeidas = notificacionesNoLeidas;
    }

    public String getEstadoPrematricula() {
        return estadoPrematricula;
    }

    public void setEstadoPrematricula(String estadoPrematricula) {
        this.estadoPrematricula = estadoPrematricula;
    }

    public List<AccesoRapidoDTO> getAccesosRapidos() {
        return accesosRapidos;
    }

    public void setAccesosRapidos(List<AccesoRapidoDTO> accesosRapidos) {
        this.accesosRapidos = accesosRapidos;
    }
}

