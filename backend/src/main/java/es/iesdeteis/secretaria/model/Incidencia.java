package es.iesdeteis.secretaria.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidencias")
public class Incidencia {

    // =========================
    // ATRIBUTOS
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El tipo de incidencia no puede ser nulo")
    @Enumerated(EnumType.STRING)
    private TipoIncidencia tipo;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(length = 500)
    private String descripcion;

    private LocalDateTime fecha;

    @NotNull(message = "El estado de resolución no puede ser nulo")
    private Boolean resuelta;

    private String accionTomada;


    // =========================
    // RELACIONES
    // =========================

    @NotNull(message = "El turno no puede ser nulo")
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "turno_id")
    private Turno turno;


    // =========================
    // CONSTRUCTORES
    // =========================

    public Incidencia() {
    }

    public Incidencia(Long id, TipoIncidencia tipo, String descripcion, LocalDateTime fecha,
                      Boolean resuelta, String accionTomada, Turno turno) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.resuelta = resuelta;
        this.accionTomada = accionTomada;
        this.turno = turno;
    }

    public Incidencia(TipoIncidencia tipo, String descripcion, Boolean resuelta,
                      String accionTomada, Turno turno) {
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

    public TipoIncidencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoIncidencia tipo) {
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

    @Override
    public String toString() {
        return "Incidencia [id=" + id +
                ", tipo=" + tipo +
                ", resuelta=" + resuelta + "]";
    }
}