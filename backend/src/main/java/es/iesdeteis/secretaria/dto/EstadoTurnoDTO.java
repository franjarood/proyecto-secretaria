package es.iesdeteis.secretaria.dto;

import jakarta.validation.constraints.NotBlank;

public class EstadoTurnoDTO {

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;

    // CONSTRUCTORES
    public EstadoTurnoDTO() {
    }

    public EstadoTurnoDTO(String estado) {
        this.estado = estado;
    }

    // GETTERS Y SETTERS
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}