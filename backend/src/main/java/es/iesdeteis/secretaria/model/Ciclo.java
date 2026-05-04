package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "ciclos")
public class Ciclo {

    // ATRIBUTOS

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del ciclo no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El código del ciclo no puede estar vacío")
    private String codigo; // Ejemplo: "DAW", "DAM", "ASIR", "SMR"

    private String familiaProfesional; // Ejemplo: "Informática y Comunicaciones"

    @NotBlank(message = "El grado del ciclo no puede estar vacío")
    private String grado; // Ejemplo: "Grado Medio", "Grado Superior", "FP Básica"

    private Boolean activo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // CONSTRUCTORES

    public Ciclo() {
    }

    public Ciclo(String nombre, String codigo, String familiaProfesional, String grado, Boolean activo) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.familiaProfesional = familiaProfesional;
        this.grado = grado;
        this.activo = activo;
    }


    // AUDITORÍA AUTOMÁTICA

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.activo == null) {
            this.activo = true;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFamiliaProfesional() {
        return familiaProfesional;
    }

    public void setFamiliaProfesional(String familiaProfesional) {
        this.familiaProfesional = familiaProfesional;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Ciclo [id=" + id + ", codigo=" + codigo + ", nombre=" + nombre + ", grado=" + grado + "]";
    }
}
