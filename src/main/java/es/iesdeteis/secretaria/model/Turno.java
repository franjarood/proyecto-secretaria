package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "turnos")
public class Turno {

    // =========================
    // ATRIBUTOS
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de turno no puede estar vacío")
    private String numeroTurno;

    @NotNull(message = "La fecha de la cita no puede ser nula")
    private LocalDate fechaCita;

    @NotNull(message = "La hora de la cita no puede ser nula")
    private LocalTime horaCita;

    private LocalTime horaLlegada;

    @Enumerated(EnumType.STRING)
    private EstadoTurno estadoTurno;

    // Se calcula automáticamente
    private Integer prioridad;

    @NotBlank(message = "El origen del turno no puede estar vacío")
    private String origenTurno; // ONLINE / KIOSKO

    private String observaciones;

    // Se calcula automáticamente
    private Integer duracionEstimada;

    // =========================
    // Cola inteligente
    // =========================

    private Boolean reingreso;          // si vuelve por incidencia previa
    private Boolean incidencia;         // hubo problema durante atención
    private Boolean prioridadManual;    // secretaría decide subir prioridad
    private String motivoPrioridad;     // explicación (opcional)

    // =========================
    // RELACIONES
    // =========================

    @ManyToMany
    @JoinTable(
            name = "turno_tipo_tramite",
            joinColumns = @JoinColumn(name = "turno_id"),
            inverseJoinColumns = @JoinColumn(name = "tipo_tramite_id")
    )
    private List<TipoTramite> tiposTramite;

    // =========================
    // AUDITORÍA
    // =========================

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // =========================
    // CONSTRUCTORES
    // =========================

    public Turno() {
    }

    public Turno(String numeroTurno, LocalDate fechaCita, LocalTime horaCita,
                 String origenTurno, String observaciones, List<TipoTramite> tiposTramite) {
        this.numeroTurno = numeroTurno;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.origenTurno = origenTurno;
        this.observaciones = observaciones;
        this.tiposTramite = tiposTramite;
    }

    // =========================
    // AUDITORÍA AUTOMÁTICA
    // =========================

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public List<TipoTramite> getTiposTramite() {
        return tiposTramite;
    }

    public void setTiposTramite(List<TipoTramite> tiposTramite) {
        this.tiposTramite = tiposTramite;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // =========================
    // TO STRING
    // =========================

    @Override
    public String toString() {
        return "Turno [id=" + id +
                ", numeroTurno=" + numeroTurno +
                ", estadoTurno=" + estadoTurno +
                ", prioridad=" + prioridad + "]";
    }
}