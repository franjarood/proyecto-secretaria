package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.EstadoDocumento;
import es.iesdeteis.secretaria.model.TipoDocumento;

import java.time.LocalDateTime;

public class DocumentoResponseDTO {

    // =========================
    // ATRIBUTOS
    // =========================

    private Long id;
    private String nombreArchivo;
    private TipoDocumento tipoDocumento;
    private String rutaArchivo;
    private EstadoDocumento estadoRevision;
    private String comentarioRevision;
    private LocalDateTime fechaSubida;
    private LocalDateTime fechaRevision;

    private Long usuarioId;
    private Long subidoPorId;
    private Long revisadoPorId;

    private Long preMatriculaId;
    private Long turnoId;

    // =========================
    // CONSTRUCTORES
    // =========================

    public DocumentoResponseDTO() {
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

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public EstadoDocumento getEstadoRevision() {
        return estadoRevision;
    }

    public String getComentarioRevision() {
        return comentarioRevision;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getSubidoPorId() {
        return subidoPorId;
    }

    public Long getRevisadoPorId() {
        return revisadoPorId;
    }

    public Long getPreMatriculaId() {
        return preMatriculaId;
    }

    public Long getTurnoId() {
        return turnoId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public void setEstadoRevision(EstadoDocumento estadoRevision) {
        this.estadoRevision = estadoRevision;
    }

    public void setComentarioRevision(String comentarioRevision) {
        this.comentarioRevision = comentarioRevision;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setSubidoPorId(Long subidoPorId) {
        this.subidoPorId = subidoPorId;
    }

    public void setRevisadoPorId(Long revisadoPorId) {
        this.revisadoPorId = revisadoPorId;
    }

    public void setPreMatriculaId(Long preMatriculaId) {
        this.preMatriculaId = preMatriculaId;
    }

    public void setTurnoId(Long turnoId) {
        this.turnoId = turnoId;
    }
}