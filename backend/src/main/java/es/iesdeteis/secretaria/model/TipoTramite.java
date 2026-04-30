package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tipos_tramite")
public class TipoTramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "La duración estimada no puede ser nula")
    private Integer duracionEstimada; // en minutos

    @NotNull(message = "Debe indicar si requiere documentación")
    private Boolean requiereDocumentacion;


    // CONSTRUCTORES
    public TipoTramite() {
    }

    public TipoTramite(Long id, String nombre, String descripcion,
                       Integer duracionEstimada, Boolean requiereDocumentacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionEstimada = duracionEstimada;
        this.requiereDocumentacion = requiereDocumentacion;
    }

    public TipoTramite(String nombre, String descripcion,
                       Integer duracionEstimada, Boolean requiereDocumentacion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionEstimada = duracionEstimada;
        this.requiereDocumentacion = requiereDocumentacion;
    }


    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(Integer duracionEstimada) {
        this.duracionEstimada = duracionEstimada;
    }

    public Boolean getRequiereDocumentacion() {
        return requiereDocumentacion;
    }

    public void setRequiereDocumentacion(Boolean requiereDocumentacion) {
        this.requiereDocumentacion = requiereDocumentacion;
    }


    // TO STRING
    @Override
    public String toString() {
        return "TipoTramite [id=" + id + ", nombre=" + nombre + "]";
    }
}