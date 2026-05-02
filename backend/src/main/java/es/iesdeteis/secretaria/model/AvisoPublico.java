package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "avisos_publicos")
public class AvisoPublico {

    // ATRIBUTOS

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(length = 3000)
    private String contenido;

    @Enumerated(EnumType.STRING)
    private TipoAviso tipoAviso;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private Boolean destacado;
    private Boolean visible;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // CONSTRUCTORES

    public AvisoPublico() {
    }


    // AUDITORÍA AUTOMÁTICA

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.visible == null) {
            this.visible = true;
        }

        if (this.destacado == null) {
            this.destacado = false;
        }

        if (this.tipoAviso == null) {
            this.tipoAviso = TipoAviso.GENERAL;
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

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public TipoAviso getTipoAviso() {
        return tipoAviso;
    }

    public void setTipoAviso(TipoAviso tipoAviso) {
        this.tipoAviso = tipoAviso;
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

    public Boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
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

