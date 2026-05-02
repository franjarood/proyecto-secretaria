package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_centro")
public class EventoCentro {

    // ATRIBUTOS

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(length = 3000)
    private String descripcion;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private String ubicacion;

    @Enumerated(EnumType.STRING)
    private TipoEventoCentro tipoEvento;

    private Boolean publico;
    private Boolean visible;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // CONSTRUCTORES

    public EventoCentro() {
    }


    // AUDITORÍA AUTOMÁTICA

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.visible == null) {
            this.visible = true;
        }

        if (this.publico == null) {
            this.publico = true;
        }

        if (this.tipoEvento == null) {
            this.tipoEvento = TipoEventoCentro.GENERAL;
        }
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public TipoEventoCentro getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEventoCentro tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Boolean getPublico() {
        return publico;
    }

    public void setPublico(Boolean publico) {
        this.publico = publico;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

