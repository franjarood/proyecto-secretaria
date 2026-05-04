package es.iesdeteis.secretaria.dto;

/**
 * DTO para respuesta de información del clima
 * Diseñado para ocultar la API key de OpenWeather en el backend
 */
public class ClimaResponseDTO {
    private String ciudad;
    private Double temperatura;
    private String descripcion;
    private String icono;
    private Boolean climaDisponible;

    public ClimaResponseDTO() {
    }

    public ClimaResponseDTO(String ciudad, Double temperatura, String descripcion, String icono, Boolean climaDisponible) {
        this.ciudad = ciudad;
        this.temperatura = temperatura;
        this.descripcion = descripcion;
        this.icono = icono;
        this.climaDisponible = climaDisponible;
    }

    // Getters y Setters
    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public Boolean getClimaDisponible() {
        return climaDisponible;
    }

    public void setClimaDisponible(Boolean climaDisponible) {
        this.climaDisponible = climaDisponible;
    }
}
