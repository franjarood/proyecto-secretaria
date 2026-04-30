package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.TipoNotificacion;

import java.time.LocalDateTime;

public class NotificacionResponseDTO {

    private Long id;
    private String titulo;
    private String mensaje;
    private TipoNotificacion tipo;

    private Boolean leida;
    private LocalDateTime creadaEn;

    private String referencia;
    private String urlDestino;

    // EMAIL
    private Boolean enviadaPorEmail;
    private LocalDateTime fechaEnvioEmail;

    // GETTERS Y SETTERS

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }

    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public TipoNotificacion getTipo() { return tipo; }

    public void setTipo(TipoNotificacion tipo) { this.tipo = tipo; }

    public Boolean getLeida() { return leida; }

    public void setLeida(Boolean leida) { this.leida = leida; }

    public LocalDateTime getCreadaEn() { return creadaEn; }

    public void setCreadaEn(LocalDateTime creadaEn) { this.creadaEn = creadaEn; }

    public String getReferencia() { return referencia; }

    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getUrlDestino() { return urlDestino; }

    public void setUrlDestino(String urlDestino) { this.urlDestino = urlDestino; }

    public Boolean getEnviadaPorEmail() { return enviadaPorEmail; }

    public void setEnviadaPorEmail(Boolean enviadaPorEmail) { this.enviadaPorEmail = enviadaPorEmail; }

    public LocalDateTime getFechaEnvioEmail() { return fechaEnvioEmail; }

    public void setFechaEnvioEmail(LocalDateTime fechaEnvioEmail) { this.fechaEnvioEmail = fechaEnvioEmail; }
}