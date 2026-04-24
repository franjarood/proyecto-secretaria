package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
public class Documento {

    // =========================
    // ATRIBUTOS
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del archivo no puede estar vacío")
    private String nombreArchivo;

    @NotNull(message = "El tipo de documento no puede ser nulo")
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "La ruta del archivo no puede estar vacía")
    private String rutaArchivo;

    @Enumerated(EnumType.STRING)
    private EstadoDocumento estadoRevision;

    private String comentarioRevision;

    private LocalDateTime fechaSubida;

    private LocalDateTime fechaRevision;

    // =========================
    // RELACIONES
    // =========================

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // alumno dueño del documento

    @ManyToOne
    @JoinColumn(name = "subido_por_id")
    private Usuario subidoPor; // quien sube el documento

    @ManyToOne
    @JoinColumn(name = "revisado_por_id")
    private Usuario revisadoPor; // quien revisa el documento

    @ManyToOne
    @JoinColumn(name = "prematricula_id")
    private PreMatricula preMatricula;

    @ManyToOne
    @JoinColumn(name = "turno_id")
    private Turno turno;

    // =========================
    // CONSTRUCTORES
    // =========================

    public Documento() {
    }

    public Documento(String nombreArchivo, TipoDocumento tipoDocumento, String rutaArchivo,
                     Usuario usuario, Usuario subidoPor,
                     PreMatricula preMatricula, Turno turno) {
        this.nombreArchivo = nombreArchivo;
        this.tipoDocumento = tipoDocumento;
        this.rutaArchivo = rutaArchivo;
        this.usuario = usuario;
        this.subidoPor = subidoPor;
        this.preMatricula = preMatricula;
        this.turno = turno;
    }

    // =========================
    // AUDITORÍA AUTOMÁTICA
    // =========================

    @PrePersist
    public void prePersist() {
        this.fechaSubida = LocalDateTime.now();

        if (this.estadoRevision == null) {
            this.estadoRevision = EstadoDocumento.PENDIENTE;
        }
    }

    // =========================
    // MÉTODOS PROPIOS
    // =========================

    public void validar(String comentarioRevision, Usuario revisadoPor) {
        this.estadoRevision = EstadoDocumento.VALIDADO;
        this.comentarioRevision = comentarioRevision;
        this.revisadoPor = revisadoPor;
        this.fechaRevision = LocalDateTime.now();
    }

    public void rechazar(String comentarioRevision, Usuario revisadoPor) {
        this.estadoRevision = EstadoDocumento.RECHAZADO;
        this.comentarioRevision = comentarioRevision;
        this.revisadoPor = revisadoPor;
        this.fechaRevision = LocalDateTime.now();
    }

    public void marcarRequiereRevision(String comentarioRevision, Usuario revisadoPor) {
        this.estadoRevision = EstadoDocumento.REQUIERE_REVISION;
        this.comentarioRevision = comentarioRevision;
        this.revisadoPor = revisadoPor;
        this.fechaRevision = LocalDateTime.now();
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public EstadoDocumento getEstadoRevision() {
        return estadoRevision;
    }

    public void setEstadoRevision(EstadoDocumento estadoRevision) {
        this.estadoRevision = estadoRevision;
    }

    public String getComentarioRevision() {
        return comentarioRevision;
    }

    public void setComentarioRevision(String comentarioRevision) {
        this.comentarioRevision = comentarioRevision;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getSubidoPor() {
        return subidoPor;
    }

    public void setSubidoPor(Usuario subidoPor) {
        this.subidoPor = subidoPor;
    }

    public Usuario getRevisadoPor() {
        return revisadoPor;
    }

    public void setRevisadoPor(Usuario revisadoPor) {
        this.revisadoPor = revisadoPor;
    }

    public PreMatricula getPreMatricula() {
        return preMatricula;
    }

    public void setPreMatricula(PreMatricula preMatricula) {
        this.preMatricula = preMatricula;
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
        return "Documento [id=" + id +
                ", nombreArchivo=" + nombreArchivo +
                ", tipoDocumento=" + tipoDocumento +
                ", estadoRevision=" + estadoRevision + "]";
    }
}