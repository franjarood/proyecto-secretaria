package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "respuestas_foro")
public class RespuestaForo {

    // ATRIBUTOS

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tema_id")
    private TemaForo tema;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @Column(length = 3000)
    private String contenido;

    private LocalDateTime fechaCreacion;

    private Boolean mejorRespuesta;

    private Boolean visible;


    // CONSTRUCTORES

    public RespuestaForo() {
    }


    // AUDITORÍA AUTOMÁTICA

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();

        if (this.mejorRespuesta == null) {
            this.mejorRespuesta = false;
        }

        if (this.visible == null) {
            this.visible = true;
        }
    }


    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TemaForo getTema() {
        return tema;
    }

    public void setTema(TemaForo tema) {
        this.tema = tema;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Boolean getMejorRespuesta() {
        return mejorRespuesta;
    }

    public void setMejorRespuesta(Boolean mejorRespuesta) {
        this.mejorRespuesta = mejorRespuesta;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}

