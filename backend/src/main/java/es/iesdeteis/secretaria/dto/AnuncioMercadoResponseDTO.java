package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.EstadoAnuncioMercado;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AnuncioMercadoResponseDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private EstadoAnuncioMercado estado;
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaActualizacion;
    private String contactoPreferido;
    private String ubicacion;

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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public EstadoAnuncioMercado getEstado() {
        return estado;
    }

    public void setEstado(EstadoAnuncioMercado estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getContactoPreferido() {
        return contactoPreferido;
    }

    public void setContactoPreferido(String contactoPreferido) {
        this.contactoPreferido = contactoPreferido;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
