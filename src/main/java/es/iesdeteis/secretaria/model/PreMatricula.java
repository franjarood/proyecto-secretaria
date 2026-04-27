package es.iesdeteis.secretaria.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "pre_matriculas")
public class PreMatricula {

    // ATRIBUTOS

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    private EstadoPreMatricula estado;

    // Datos del alumno

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombreAlumno;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    private String apellidosAlumno;

    @NotBlank(message = "El DNI no puede estar vacío")
    private String dniAlumno;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    private String emailAlumno;

    @NotBlank(message = "El teléfono no puede estar vacío")
    private String telefonoAlumno;

    // Datos académicos

    @NotBlank(message = "El ciclo no puede estar vacío")
    private String cicloSolicitado;

    @NotBlank(message = "El curso no puede estar vacío")
    private String cursoSolicitado;

    @NotBlank(message = "La modalidad no puede estar vacía")
    private String modalidad;

    // Observaciones

    private String observaciones;

    // Relaciones

    @JsonManagedReference(value = "prematricula-documentos")
    @OneToMany(mappedBy = "preMatricula", cascade = CascadeType.ALL)
    private List<Documento> documentos;


    // CONSTRUCTORES

    public PreMatricula() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoPreMatricula.PENDIENTE;
    }

    public PreMatricula(Long id, LocalDateTime fechaCreacion, EstadoPreMatricula estado,
                        String nombreAlumno, String apellidosAlumno, String dniAlumno,
                        String emailAlumno, String telefonoAlumno,
                        String cicloSolicitado, String cursoSolicitado, String modalidad,
                        String observaciones) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.nombreAlumno = nombreAlumno;
        this.apellidosAlumno = apellidosAlumno;
        this.dniAlumno = dniAlumno;
        this.emailAlumno = emailAlumno;
        this.telefonoAlumno = telefonoAlumno;
        this.cicloSolicitado = cicloSolicitado;
        this.cursoSolicitado = cursoSolicitado;
        this.modalidad = modalidad;
        this.observaciones = observaciones;
    }

    public PreMatricula(String nombreAlumno, String apellidosAlumno, String dniAlumno,
                        String emailAlumno, String telefonoAlumno,
                        String cicloSolicitado, String cursoSolicitado, String modalidad,
                        String observaciones) {
        this.nombreAlumno = nombreAlumno;
        this.apellidosAlumno = apellidosAlumno;
        this.dniAlumno = dniAlumno;
        this.emailAlumno = emailAlumno;
        this.telefonoAlumno = telefonoAlumno;
        this.cicloSolicitado = cicloSolicitado;
        this.cursoSolicitado = cursoSolicitado;
        this.modalidad = modalidad;
        this.observaciones = observaciones;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoPreMatricula.PENDIENTE;
    }


    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public EstadoPreMatricula getEstado() {
        return estado;
    }

    public void setEstado(EstadoPreMatricula estado) {
        this.estado = estado;
    }

    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public String getApellidosAlumno() {
        return apellidosAlumno;
    }

    public void setApellidosAlumno(String apellidosAlumno) {
        this.apellidosAlumno = apellidosAlumno;
    }

    public String getDniAlumno() {
        return dniAlumno;
    }

    public void setDniAlumno(String dniAlumno) {
        this.dniAlumno = dniAlumno;
    }

    public String getEmailAlumno() {
        return emailAlumno;
    }

    public void setEmailAlumno(String emailAlumno) {
        this.emailAlumno = emailAlumno;
    }

    public String getTelefonoAlumno() {
        return telefonoAlumno;
    }

    public void setTelefonoAlumno(String telefonoAlumno) {
        this.telefonoAlumno = telefonoAlumno;
    }

    public String getCicloSolicitado() {
        return cicloSolicitado;
    }

    public void setCicloSolicitado(String cicloSolicitado) {
        this.cicloSolicitado = cicloSolicitado;
    }

    public String getCursoSolicitado() {
        return cursoSolicitado;
    }

    public void setCursoSolicitado(String cursoSolicitado) {
        this.cursoSolicitado = cursoSolicitado;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    @Override
    public String toString() {
        return "PreMatricula [id=" + id + ", nombreAlumno=" + nombreAlumno + "]";
    }
}