package es.iesdeteis.secretaria.dto;

public class TurnoColaDTO {

    private String numeroTurno;
    private String estado;
    private Integer prioridad;
    private Integer duracionEstimada;

    // CONSTRUCTORES
    public TurnoColaDTO() {
    }

    public TurnoColaDTO(String numeroTurno, String estado, Integer prioridad, Integer duracionEstimada) {
        this.numeroTurno = numeroTurno;
        this.estado = estado;
        this.prioridad = prioridad;
        this.duracionEstimada = duracionEstimada;
    }

    // GETTERS Y SETTERS
    public String getNumeroTurno() {
        return numeroTurno;
    }

    public void setNumeroTurno(String numeroTurno) {
        this.numeroTurno = numeroTurno;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public Integer getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(Integer duracionEstimada) {
        this.duracionEstimada = duracionEstimada;
    }
}