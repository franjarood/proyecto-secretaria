package es.iesdeteis.secretaria.dto;

import java.time.LocalDateTime;

public class IncidenciaResponseDTO {

    // ATRIBUTOS

    private Long id;
    private String tipo;
    private String descripcion;
    private LocalDateTime fecha;
    private Boolean resuelta;
    private String accionTomada;
    private Long turnoId;


    // CONSTRUCTORES

    public IncidenciaResponseDTO() {
    }

    public IncidenciaResponseDTO(Long id, String tipo, String descripcion, LocalDateTime fecha,
                                 Boolean resuelta, String accionTomada, Long turnoId) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.resuelta = resuelta;
        this.accionTomada = accionTomada;
        this.turnoId = turnoId;
    }


    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

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

    public LocalDateTime getFecha() {
        return fecha;
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