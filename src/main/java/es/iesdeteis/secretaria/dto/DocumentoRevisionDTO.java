package es.iesdeteis.secretaria.dto;

import jakarta.validation.constraints.NotBlank;

public class DocumentoRevisionDTO {

    // =========================
    // ATRIBUTOS
    // =========================

    @NotBlank(message = "El comentario es obligatorio")
    private String comentario;

    // =========================
    // CONSTRUCTORES
    // =========================

    public DocumentoRevisionDTO() {
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}