package es.iesdeteis.secretaria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IncidenciaCreateDTO {

    // ATRIBUTOS

    @NotBlank(message = "El tipo de incidencia no puede estar vacío")
    private String tipo;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "El estado de resolución no puede ser nulo")
    private Boolean resuelta;

    private String accionTomada;

    @NotNull(message = "El turno no puede ser nulo")
    private Long turnoId;


    // CONSTRUCTORES

    public IncidenciaCreateDTO() {
    }

    public IncidenciaCreateDTO(String tipo, String descripcion, Boolean resuelta,
                               String accionTomada, Long turnoId) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.resuelta = resuelta;
        this.accionTomada = accionTomada;
        this.turnoId = turnoId;
    }


    // GETTERS Y SETTERS

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getResuelta() {
        return resuelta;
    }

    public void setResuelta(Boolean resuelta) {
        this.resuelta = resuelta;
    }

    public String getAccionTomada() {
        return accionTomada;
    }

    public void setAccionTomada(String accionTomada) {
        this.accionTomada = accionTomada;
    }

    public Long getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(Long turnoId) {
        this.turnoId = turnoId;
    }
}