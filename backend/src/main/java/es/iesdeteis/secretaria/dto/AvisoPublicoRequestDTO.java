package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.TipoAviso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AvisoPublicoRequestDTO {

    // ATRIBUTOS

    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    @NotBlank(message = "El contenido no puede estar vacío")
    private String contenido;

    @NotNull(message = "El tipo de aviso no puede ser nulo")
    private TipoAviso tipoAviso;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @NotNull(message = "Debe indicar si el aviso es destacado")
    private Boolean destacado;

    @NotNull(message = "Debe indicar si el aviso es visible")
    private Boolean visible;


    // CONSTRUCTORES

    public AvisoPublicoRequestDTO() {
    }


    // GETTERS Y SETTERS

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public TipoAviso getTipoAviso() {
        return tipoAviso;
    }

    public void setTipoAviso(TipoAviso tipoAviso) {
        this.tipoAviso = tipoAviso;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}

