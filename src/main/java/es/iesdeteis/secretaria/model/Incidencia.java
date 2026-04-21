package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// IMPORTANTE:
// Esto evita bucles infinitos al devolver JSON (Turno → Incidencias → Turno...)
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "incidencias")
public class Incidencia {

    // =========================
    // ATRIBUTOS
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;

    @Column(length = 500)
    private String descripcion;

    private LocalDateTime fecha;

    private Boolean resuelta;

    private String accionTomada;

    // =========================
    // RELACIONES
    // =========================

    @JsonBackReference // Evita recursividad infinita al convertir a JSON
    @ManyToOne
    @JoinColumn(name = "turno_id")
    private Turno turno;

    // =========================
    // CONSTRUCTORES
    // =========================

    public Incidencia() {
    }

    public Incidencia(String tipo, String descripcion, Boolean resuelta, String accionTomada, Turno turno) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.resuelta = resuelta;
        this.accionTomada = accionTomada;
        this.turno = turno;
    }

    // =========================
    // AUDITORÍA AUTOMÁTICA
    // =========================

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

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

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    // =========================
    // TO STRING
    // =========================

    @Override
    public String toString() {
        return "Incidencia [id=" + id +
                ", tipo=" + tipo +
                ", resuelta=" + resuelta + "]";
    }
}