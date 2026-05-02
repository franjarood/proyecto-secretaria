package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.EstadoAnuncioAyuda;
import es.iesdeteis.secretaria.model.TipoAnuncioAyuda;

import java.time.LocalDateTime;

public class AnuncioAyudaResponseDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private TipoAnuncioAyuda tipo;
    private String modulo;
    private String descripcion;
    private EstadoAnuncioAyuda estado;
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaCierre;
    private String contactoPreferido;

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public TipoAnuncioAyuda getTipo() {
        return tipo;
    }

    public void setTipo(TipoAnuncioAyuda tipo) {
        this.tipo = tipo;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoAnuncioAyuda getEstado() {
        return estado;
    }

    public void setEstado(EstadoAnuncioAyuda estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getContactoPreferido() {
        return contactoPreferido;
    }

    public void setContactoPreferido(String contactoPreferido) {
        this.contactoPreferido = contactoPreferido;
    }
}
