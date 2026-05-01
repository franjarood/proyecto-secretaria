package es.iesdeteis.secretaria.dto;

import jakarta.validation.constraints.NotNull;

public class CheckInGeoRequestDTO {

    @NotNull(message = "La latitud no puede ser nula")
    private Double latitud;

    @NotNull(message = "La longitud no puede ser nula")
    private Double longitud;

    private Integer precisionMetros;

    public CheckInGeoRequestDTO() {
    }

    public CheckInGeoRequestDTO(Double latitud, Double longitud, Integer precisionMetros) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.precisionMetros = precisionMetros;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Integer getPrecisionMetros() {
        return precisionMetros;
    }

    public void setPrecisionMetros(Integer precisionMetros) {
        this.precisionMetros = precisionMetros;
    }
}

