package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.TipoAviso;

import java.time.LocalDateTime;

public class AvisoPublicoResponseDTO {

    // ATRIBUTOS

    private Long id;
    private String titulo;
    private String contenido;
    private TipoAviso tipoAviso;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean destacado;
    private Boolean visible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // CONSTRUCTORES

    public AvisoPublicoResponseDTO() {
    }


    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

