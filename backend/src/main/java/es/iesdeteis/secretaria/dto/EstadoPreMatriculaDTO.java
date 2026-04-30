package es.iesdeteis.secretaria.dto;

import jakarta.validation.constraints.NotBlank;

public class EstadoPreMatriculaDTO {

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;

    // CONSTRUCTOR
    public EstadoPreMatriculaDTO() {
    }

    public EstadoPreMatriculaDTO(String estado) {
        this.estado = estado;
    }

    // GETTER Y SETTER
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}