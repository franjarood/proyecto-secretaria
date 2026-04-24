package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DocumentoCreateDTO {

    // =========================
    // ATRIBUTOS
    // =========================

    @NotBlank(message = "El nombre del archivo es obligatorio")
    private String nombreArchivo;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "La ruta del archivo es obligatoria")
    private String rutaArchivo;

    private Long usuarioId; // 👈 IMPORTANTE

    private Long preMatriculaId;
    private Long turnoId;

    // =========================
    // CONSTRUCTORES
    // =========================

    public DocumentoCreateDTO() {
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getPreMatriculaId() {
        return preMatriculaId;
    }

    public void setPreMatriculaId(Long preMatriculaId) {
        this.preMatriculaId = preMatriculaId;
    }

    public Long getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(Long turnoId) {
        this.turnoId = turnoId;
    }
}