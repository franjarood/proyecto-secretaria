package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.TipoEventoCentro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class EventoCentroRequestDTO {

    // ATRIBUTOS

    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    private String descripcion;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private String ubicacion;

    @NotNull(message = "Debe indicar el tipo de evento")
    private TipoEventoCentro tipoEvento;

    @NotNull(message = "Debe indicar si el evento es público")
    private Boolean publico;

    @NotNull(message = "Debe indicar si el evento es visible")
    private Boolean visible;


    // CONSTRUCTORES

    public EventoCentroRequestDTO() {
    }


    // GETTERS Y SETTERS

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public TipoEventoCentro getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEventoCentro tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Boolean getPublico() {
        return publico;
    }

    public void setPublico(Boolean publico) {
        this.publico = publico;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}

