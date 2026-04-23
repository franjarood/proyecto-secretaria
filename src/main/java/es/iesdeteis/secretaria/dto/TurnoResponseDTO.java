package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.EstadoTurno;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TurnoResponseDTO {

    // ATRIBUTOS

    private Long id;
    private String numeroTurno;
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private LocalTime horaLlegada;
    private EstadoTurno estadoTurno;
    private Integer prioridad;
    private String origenTurno;
    private String observaciones;
    private Integer duracionEstimada;
    private Boolean reingreso;
    private Boolean incidencia;
    private Boolean prioridadManual;
    private String motivoPrioridad;
    private List<String> tiposTramite;
    private Long reservaTurnoId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // CONSTRUCTORES

    public TurnoResponseDTO() {
    }

    public TurnoResponseDTO(Long id, String numeroTurno, LocalDate fechaCita, LocalTime horaCita,
                            LocalTime horaLlegada, EstadoTurno estadoTurno, Integer prioridad,
                            String origenTurno, String observaciones, Integer duracionEstimada,
                            Boolean reingreso, Boolean incidencia, Boolean prioridadManual,
                            String motivoPrioridad, List<String> tiposTramite, Long reservaTurnoId,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.numeroTurno = numeroTurno;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.horaLlegada = horaLlegada;
        this.estadoTurno = estadoTurno;
        this.prioridad = prioridad;
        this.origenTurno = origenTurno;
        this.observaciones = observaciones;
        this.duracionEstimada = duracionEstimada;
        this.reingreso = reingreso;
        this.incidencia = incidencia;
        this.prioridadManual = prioridadManual;
        this.motivoPrioridad = motivoPrioridad;
        this.tiposTramite = tiposTramite;
        this.reservaTurnoId = reservaTurnoId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroTurno() {
        return numeroTurno;
    }

    public void setNumeroTurno(String numeroTurno) {
        this.numeroTurno = numeroTurno;
    }

    public LocalDate getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDate fechaCita) {
        this.fechaCita = fechaCita;
    }

    public LocalTime getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(LocalTime horaCita) {
        this.horaCita = horaCita;
    }

    public LocalTime getHoraLlegada() {
        return horaLlegada;
    }

    public void setHoraLlegada(LocalTime horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    public EstadoTurno getEstadoTurno() {
        return estadoTurno;
    }

    public void setEstadoTurno(EstadoTurno estadoTurno) {
        this.estadoTurno = estadoTurno;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public String getOrigenTurno() {
        return origenTurno;
    }

    public void setOrigenTurno(String origenTurno) {
        this.origenTurno = origenTurno;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(Integer duracionEstimada) {
        this.duracionEstimada = duracionEstimada;
    }

    public Boolean getReingreso() {
        return reingreso;
    }

    public void setReingreso(Boolean reingreso) {
        this.reingreso = reingreso;
    }

    public Boolean getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(Boolean incidencia) {
        this.incidencia = incidencia;
    }

    public Boolean getPrioridadManual() {
        return prioridadManual;
    }

    public void setPrioridadManual(Boolean prioridadManual) {
        this.prioridadManual = prioridadManual;
    }

    public String getMotivoPrioridad() {
        return motivoPrioridad;
    }

    public void setMotivoPrioridad(String motivoPrioridad) {
        this.motivoPrioridad = motivoPrioridad;
    }

    public List<String> getTiposTramite() {
        return tiposTramite;
    }

    public void setTiposTramite(List<String> tiposTramite) {
        this.tiposTramite = tiposTramite;
    }

    public Long getReservaTurnoId() {
        return reservaTurnoId;
    }

    public void setReservaTurnoId(Long reservaTurnoId) {
        this.reservaTurnoId = reservaTurnoId;
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