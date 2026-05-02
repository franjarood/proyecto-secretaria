package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "temas_foro")
public class TemaForo {

    // ATRIBUTOS

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(length = 3000)
    private String contenido;

    private String modulo;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @Enumerated(EnumType.STRING)
    private EstadoTemaForo estado;

    private Boolean visible;


    // CONSTRUCTORES

    public TemaForo() {
    }


    // AUDITORÍA AUTOMÁTICA

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();

        if (this.estado == null) {
            this.estado = EstadoTemaForo.ABIERTO;
        }

        if (this.visible == null) {
            this.visible = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
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

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public EstadoTemaForo getEstado() {
        return estado;
    }

    public void setEstado(EstadoTemaForo estado) {
        this.estado = estado;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}

