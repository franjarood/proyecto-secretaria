package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "reservas_turno")
public class ReservaTurno {

    // =========================
    // ATRIBUTOS
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de la cita no puede ser nula")
    private LocalDate fechaCita;

    @NotNull(message = "La hora de la cita no puede ser nula")
    private LocalTime horaCita;

    @NotBlank(message = "El código de referencia no puede estar vacío")
    private String codigoReferencia;

    @NotBlank(message = "El origen de la reserva no puede estar vacío")
    private String origenTurno;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estadoReserva;

    // =========================
    // RELACIONES
    // =========================

    @ManyToMany
    @JoinTable(
            name = "reserva_tipo_tramite",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "tipo_tramite_id")
    )
    private List<TipoTramite> tiposTramite;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // =========================
    // AUDITORÍA
    // =========================

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // =========================
    // CONSTRUCTORES
    // =========================

    public ReservaTurno() {
    }

    public ReservaTurno(LocalDate fechaCita, LocalTime horaCita, String codigoReferencia,
                        String origenTurno, EstadoReserva estadoReserva,
                        List<TipoTramite> tiposTramite, Usuario usuario) {
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.codigoReferencia = codigoReferencia;
        this.origenTurno = origenTurno;
        this.estadoReserva = estadoReserva;
        this.tiposTramite = tiposTramite;
        this.usuario = usuario;
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

    public String getCodigoReferencia() {
        return codigoReferencia;
    }

    public void setCodigoReferencia(String codigoReferencia) {
        this.codigoReferencia = codigoReferencia;
    }

    public String getOrigenTurno() {
        return origenTurno;
    }

    public void setOrigenTurno(String origenTurno) {
        this.origenTurno = origenTurno;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public List<TipoTramite> getTiposTramite() {
        return tiposTramite;
    }

    public void setTiposTramite(List<TipoTramite> tiposTramite) {
        this.tiposTramite = tiposTramite;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        return "ReservaTurno [id=" + id +
                ", codigoReferencia=" + codigoReferencia +
                ", fechaCita=" + fechaCita +
                ", horaCita=" + horaCita +
                ", estadoReserva=" + estadoReserva + "]";
    }
}