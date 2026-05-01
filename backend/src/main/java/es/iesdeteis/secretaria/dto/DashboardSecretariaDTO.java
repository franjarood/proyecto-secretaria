package es.iesdeteis.secretaria.dto;

import java.util.List;

public class DashboardSecretariaDTO {

    private List<TarjetaResumenDTO> tarjetas;
    private List<ActividadRecienteDTO> actividadReciente;
    private List<AccesoRapidoDTO> accesosRapidos;

    public DashboardSecretariaDTO() {
    }

    public DashboardSecretariaDTO(List<TarjetaResumenDTO> tarjetas,
                                  List<ActividadRecienteDTO> actividadReciente,
                                  List<AccesoRapidoDTO> accesosRapidos) {
        this.tarjetas = tarjetas;
        this.actividadReciente = actividadReciente;
        this.accesosRapidos = accesosRapidos;
    }

    public List<TarjetaResumenDTO> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(List<TarjetaResumenDTO> tarjetas) {
        this.tarjetas = tarjetas;
    }

    public List<ActividadRecienteDTO> getActividadReciente() {
        return actividadReciente;
    }

    public void setActividadReciente(List<ActividadRecienteDTO> actividadReciente) {
        this.actividadReciente = actividadReciente;
    }

    public List<AccesoRapidoDTO> getAccesosRapidos() {
        return accesosRapidos;
    }

    public void setAccesosRapidos(List<AccesoRapidoDTO> accesosRapidos) {
        this.accesosRapidos = accesosRapidos;
    }
}

