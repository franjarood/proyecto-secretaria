package es.iesdeteis.secretaria.dto;

import java.time.LocalDateTime;

public class RespuestaForoResponseDTO {

    private Long id;
    private Long temaId;

    private Long autorId;
    private String autorNombre;

    private String contenido;
    private LocalDateTime fechaCreacion;

    private Boolean mejorRespuesta;
    private Boolean visible;

    public RespuestaForoResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemaId() {
        return temaId;
    }

    public void setTemaId(Long temaId) {
        this.temaId = temaId;
    }

    public Long getAutorId() {
        return autorId;
    }

    public void setAutorId(Long autorId) {
        this.autorId = autorId;
    }

    public String getAutorNombre() {
        return autorNombre;
    }

    public void setAutorNombre(String autorNombre) {
        this.autorNombre = autorNombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Boolean getMejorRespuesta() {
        return mejorRespuesta;
    }

    public void setMejorRespuesta(Boolean mejorRespuesta) {
        this.mejorRespuesta = mejorRespuesta;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}

