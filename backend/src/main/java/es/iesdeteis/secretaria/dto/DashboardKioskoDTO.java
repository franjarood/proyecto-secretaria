package es.iesdeteis.secretaria.dto;

import java.util.List;

public class DashboardKioskoDTO {

    private List<String> tramitesDisponibles;
    private Integer turnosActivosHoy;
    private Integer tiempoMedioEstimadoMinutos;
    private List<String> avisos;
    private List<AccesoRapidoDTO> accesosRapidos;

    public DashboardKioskoDTO() {
    }

    public DashboardKioskoDTO(List<String> tramitesDisponibles,
                              Integer turnosActivosHoy,
                              Integer tiempoMedioEstimadoMinutos,
                              List<String> avisos,
                              List<AccesoRapidoDTO> accesosRapidos) {
        this.tramitesDisponibles = tramitesDisponibles;
        this.turnosActivosHoy = turnosActivosHoy;
        this.tiempoMedioEstimadoMinutos = tiempoMedioEstimadoMinutos;
        this.avisos = avisos;
        this.accesosRapidos = accesosRapidos;
    }

    public List<String> getTramitesDisponibles() {
        return tramitesDisponibles;
    }

    public void setTramitesDisponibles(List<String> tramitesDisponibles) {
        this.tramitesDisponibles = tramitesDisponibles;
    }

    public Integer getTurnosActivosHoy() {
        return turnosActivosHoy;
    }

    public void setTurnosActivosHoy(Integer turnosActivosHoy) {
        this.turnosActivosHoy = turnosActivosHoy;
    }

    public Integer getTiempoMedioEstimadoMinutos() {
        return tiempoMedioEstimadoMinutos;
    }

    public void setTiempoMedioEstimadoMinutos(Integer tiempoMedioEstimadoMinutos) {
        this.tiempoMedioEstimadoMinutos = tiempoMedioEstimadoMinutos;
    }

    public List<String> getAvisos() {
        return avisos;
    }

    public void setAvisos(List<String> avisos) {
        this.avisos = avisos;
    }

    public List<AccesoRapidoDTO> getAccesosRapidos() {
        return accesosRapidos;
    }

    public void setAccesosRapidos(List<AccesoRapidoDTO> accesosRapidos) {
        this.accesosRapidos = accesosRapidos;
    }
}

