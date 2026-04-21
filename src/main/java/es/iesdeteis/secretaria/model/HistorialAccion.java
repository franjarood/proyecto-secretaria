package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_acciones")
public class HistorialAccion {

    // =========================
    // ATRIBUTOS
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHora;

    private String accion;

    @Column(length = 500)
    private String descripcion;

    private String entidadAfectada;

    private Long idEntidad;

    private Long usuarioResponsable;

    // =========================
    // CONSTRUCTORES
    // =========================

    public HistorialAccion() {
    }

    public HistorialAccion(String accion, String descripcion, String entidadAfectada,
                           Long idEntidad, Long usuarioResponsable) {
        this.accion = accion;
        this.descripcion = descripcion;
        this.entidadAfectada = entidadAfectada;
        this.idEntidad = idEntidad;
        this.usuarioResponsable = usuarioResponsable;
    }

    // =========================
    // AUDITORÍA AUTOMÁTICA
    // =========================

    @PrePersist
    public void prePersist() {
        this.fechaHora = LocalDateTime.now();
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEntidadAfectada() {
        return entidadAfectada;
    }

    public void setEntidadAfectada(String entidadAfectada) {
        this.entidadAfectada = entidadAfectada;
    }

    public Long getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(Long idEntidad) {
        this.idEntidad = idEntidad;
    }

    public Long getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(Long usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    // =========================
    // TO STRING
    // =========================

    @Override
    public String toString() {
        return "HistorialAccion [id=" + id +
                ", accion=" + accion +
                ", entidadAfectada=" + entidadAfectada +
                ", idEntidad=" + idEntidad + "]";
    }
}