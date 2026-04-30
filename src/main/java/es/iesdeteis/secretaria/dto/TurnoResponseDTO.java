package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.EstadoTurno;
import es.iesdeteis.secretaria.model.PrioridadTurno;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TurnoResponseDTO {

    // =========================
    // ATRIBUTOS
    // =========================

    private Long id;
    private String numeroTurno;
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private LocalTime horaLlegada;
    private EstadoTurno estadoTurno;

    // Prioridad numérica (para orden interno de cola)
    private Integer prioridad;

    // Tipo de prioridad (para mostrar en frontend / API)
    private PrioridadTurno tipoPrioridad;

    private String origenTurno;
    private String observaciones;
    private Integer duracionEstimada;

    // Flags de comportamiento en cola inteligente
    private Boolean reingreso;
    private Boolean incidencia;
    private Boolean prioridadManual;

    // Motivo si la prioridad fue modificada manualmente
    private String motivoPrioridad;

    private List<String> tiposTramite;
    private Long reservaTurnoId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // =========================
    // CONSTRUCTORES
    // =========================

    public TurnoResponseDTO() {
    }

    public TurnoResponseDTO(Long id,
                            String numeroTurno,
                            LocalDate fechaCita,
                            LocalTime horaCita,
                            LocalTime horaLlegada,
                            EstadoTurno estadoTurno,
                            Integer prioridad,
                            PrioridadTurno tipoPrioridad,
                            String origenTurno,
                            String observaciones,
                            Integer duracionEstimada,
                            Boolean reingreso,
                            Boolean incidencia,
                            Boolean prioridadManual,
                            String motivoPrioridad,
                            List<String> tiposTramite,
                            Long reservaTurnoId,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt) {

        this.id = id;
        this.numeroTurno = numeroTurno;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.horaLlegada = horaLlegada;
        this.estadoTurno = estadoTurno;

        this.prioridad = prioridad;
        this.tipoPrioridad = tipoPrioridad;

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


    // =========================
    // GETTERS Y SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public String getNumeroTurno() {
        return numeroTurno;
    }

    public LocalDate getFechaCita() {
        return fechaCita;
    }

    public LocalTime getHoraCita() {
        return horaCita;
    }

    public LocalTime getHoraLlegada() {
        return horaLlegada;
    }

    public EstadoTurno getEstadoTurno() {
        return estadoTurno;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public PrioridadTurno getTipoPrioridad() {
        return tipoPrioridad;
    }

    public String getOrigenTurno() {
        return origenTurno;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public Integer getDuracionEstimada() {
        return duracionEstimada;
    }

    public Boolean getReingreso() {
        return reingreso;
    }

    public Boolean getIncidencia() {
        return incidencia;
    }

    public Boolean getPrioridadManual() {
        return prioridadManual;
    }

    public String getMotivoPrioridad() {
        return motivoPrioridad;
    }

    public List<String> getTiposTramite() {
        return tiposTramite;
    }

    public Long getReservaTurnoId() {
        return reservaTurnoId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    // =========================
    // SETTERS (solo si necesitas)
    // =========================

    public void setTipoPrioridad(PrioridadTurno tipoPrioridad) {
        this.tipoPrioridad = tipoPrioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }
}