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

    @NotNull(message = "El estado del turno no puede ser nulo")
    @Enumerated(EnumType.STRING)
    private EstadoTurno estadoTurno;

    @NotNull(message = "La prioridad no puede ser nula")
    private Integer prioridad;

    @NotBlank(message = "El origen del turno no puede estar vacío")
    private String origenTurno;

    private String observaciones;

    private Integer duracionEstimada;

    @ManyToMany
    @JoinTable(
            name = "turno_tipo_tramite",
            joinColumns = @JoinColumn(name = "turno_id"),
            inverseJoinColumns = @JoinColumn(name = "tipo_tramite_id")
    )
    private List<TipoTramite> tiposTramite;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // CONSTRUCTORES
    public Turno() {
    }

    public Turno(Long id, String numeroTurno, LocalDate fechaCita, LocalTime horaCita,
                 LocalTime horaLlegada, EstadoTurno estadoTurno, Integer prioridad,
                 String origenTurno, String observaciones, Integer duracionEstimada,
                 List<TipoTramite> tiposTramite, LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
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
        this.tiposTramite = tiposTramite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Turno(String numeroTurno, LocalDate fechaCita, LocalTime horaCita,
                 LocalTime horaLlegada, EstadoTurno estadoTurno, Integer prioridad,
                 String origenTurno, String observaciones, Integer duracionEstimada,
                 List<TipoTramite> tiposTramite) {
        this.numeroTurno = numeroTurno;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.horaLlegada = horaLlegada;
        this.estadoTurno = estadoTurno;
        this.prioridad = prioridad;
        this.origenTurno = origenTurno;
        this.observaciones = observaciones;
        this.duracionEstimada = duracionEstimada;
        this.tiposTramite = tiposTramite;
    }


    // AUDITORÍA
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public List<TipoTramite> getTiposTramite() {
        return tiposTramite;
    }

    public void setTiposTramite(List<TipoTramite> tiposTramite) {
        this.tiposTramite = tiposTramite;
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


    // TO STRING
    @Override
    public String toString() {
        return "Turno [id=" + id
                + ", numeroTurno=" + numeroTurno
                + ", estadoTurno=" + estadoTurno + "]";
    }
}