package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificacionCreateDTO {

    @NotBlank
    private String titulo;

    @NotBlank
    private String mensaje;

    @NotNull
    private TipoNotificacion tipo;

    private String referencia;

    private String urlDestino;

    @NotNull
    private Long usuarioId;

    // GETTERS Y SETTERS

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }

    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public TipoNotificacion getTipo() { return tipo; }

    public void setTipo(TipoNotificacion tipo) { this.tipo = tipo; }

    public String getReferencia() { return referencia; }

    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getUrlDestino() { return urlDestino; }

    public void setUrlDestino(String urlDestino) { this.urlDestino = urlDestino; }

    public Long getUsuarioId() { return usuarioId; }

    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}