package es.iesdeteis.secretaria.dto;

public class CheckInGeoResponseDTO {

    private Boolean checkInAceptado;
    private String mensaje;
    private Double distanciaMetros;
    private Integer precisionMetros;

    public CheckInGeoResponseDTO() {
    }

    public CheckInGeoResponseDTO(Boolean checkInAceptado, String mensaje, Double distanciaMetros, Integer precisionMetros) {
        this.checkInAceptado = checkInAceptado;
        this.mensaje = mensaje;
        this.distanciaMetros = distanciaMetros;
        this.precisionMetros = precisionMetros;
    }

    public Boolean getCheckInAceptado() {
        return checkInAceptado;
    }

    public void setCheckInAceptado(Boolean checkInAceptado) {
        this.checkInAceptado = checkInAceptado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Double getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(Double distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }

    public Integer getPrecisionMetros() {
        return precisionMetros;
    }

    public void setPrecisionMetros(Integer precisionMetros) {
        this.precisionMetros = precisionMetros;
    }
}

