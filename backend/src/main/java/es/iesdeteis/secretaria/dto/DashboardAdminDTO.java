package es.iesdeteis.secretaria.dto;

import java.util.List;

public class DashboardAdminDTO {

    private List<TarjetaResumenDTO> tarjetas;
    private String estadoGeneralSistema;
    private List<AccesoRapidoDTO> accesosRapidos;

    public DashboardAdminDTO() {
    }

    public DashboardAdminDTO(List<TarjetaResumenDTO> tarjetas,
                             String estadoGeneralSistema,
                             List<AccesoRapidoDTO> accesosRapidos) {
        this.tarjetas = tarjetas;
        this.estadoGeneralSistema = estadoGeneralSistema;
        this.accesosRapidos = accesosRapidos;
    }

    public List<TarjetaResumenDTO> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(List<TarjetaResumenDTO> tarjetas) {
        this.tarjetas = tarjetas;
    }

    public String getEstadoGeneralSistema() {
        return estadoGeneralSistema;
    }

    public void setEstadoGeneralSistema(String estadoGeneralSistema) {
        this.estadoGeneralSistema = estadoGeneralSistema;
    }

    public List<AccesoRapidoDTO> getAccesosRapidos() {
        return accesosRapidos;
    }

    public void setAccesosRapidos(List<AccesoRapidoDTO> accesosRapidos) {
        this.accesosRapidos = accesosRapidos;
    }
}

