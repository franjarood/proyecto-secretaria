package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    // =========================
    // ATRIBUTOS
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Column(length = 500)
    private String mensaje;

    @NotNull(message = "El tipo no puede ser nulo")
    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;

    private Boolean leida;

    private LocalDateTime creadaEn;

    private String referencia;

    private String urlDestino;

    // EMAIL
    private Boolean enviadaPorEmail;
    private LocalDateTime fechaEnvioEmail;

    // =========================
    // RELACIONES
    // =========================

    @NotNull(message = "El usuario no puede ser nulo")
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // =========================
    // CONSTRUCTORES
    // =========================

    public Notificacion() {
    }

    public Notificacion(String titulo, String mensaje, TipoNotificacion tipo,
                        String referencia, String urlDestino, Usuario usuario) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.referencia = referencia;
        this.urlDestino = urlDestino;
        this.usuario = usuario;
    }

    // =========================
    // AUDITORÍA AUTOMÁTICA
    // =========================

    @PrePersist
    public void prePersist() {
        this.creadaEn = LocalDateTime.now();

        if (this.leida == null) {
            this.leida = false;
        }

        if (this.enviadaPorEmail == null) {
            this.enviadaPorEmail = false;
        }
    }

    // =========================
    // MÉTODOS PROPIOS
    // =========================

    public void marcarComoLeida() {
        this.leida = true;
    }

    public void marcarEmailEnviado() {
        this.enviadaPorEmail = true;
        this.fechaEnvioEmail = LocalDateTime.now();
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public Long getId() { return id; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }

    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public TipoNotificacion getTipo() { return tipo; }

    public void setTipo(TipoNotificacion tipo) { this.tipo = tipo; }

    public Boolean getLeida() { return leida; }

    public void setLeida(Boolean leida) { this.leida = leida; }

    public LocalDateTime getCreadaEn() { return creadaEn; }

    public String getReferencia() { return referencia; }

    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getUrlDestino() { return urlDestino; }

    public void setUrlDestino(String urlDestino) { this.urlDestino = urlDestino; }

    public Boolean getEnviadaPorEmail() { return enviadaPorEmail; }

    public LocalDateTime getFechaEnvioEmail() { return fechaEnvioEmail; }

    public Usuario getUsuario() { return usuario; }

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // =========================
    // TO STRING
    // =========================

    @Override
    public String toString() {
        return "Notificacion [id=" + id +
                ", titulo=" + titulo +
                ", tipo=" + tipo +
                ", leida=" + leida + "]";
    }
}