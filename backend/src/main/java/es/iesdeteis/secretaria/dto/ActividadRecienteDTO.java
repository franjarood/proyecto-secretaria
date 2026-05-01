package es.iesdeteis.secretaria.dto;

import java.time.LocalDateTime;

public class ActividadRecienteDTO {

    private String titulo;
    private String descripcion;
    private LocalDateTime fecha;

    public ActividadRecienteDTO() {
    }

    public ActividadRecienteDTO(String titulo, String descripcion, LocalDateTime fecha) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}

