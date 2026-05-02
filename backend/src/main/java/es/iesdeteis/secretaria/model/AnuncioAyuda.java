package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anuncios_ayuda")
public class AnuncioAyuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private TipoAnuncioAyuda tipo;

    private String modulo;

    @Column(length = 2000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoAnuncioAyuda estado;

    private LocalDateTime fechaPublicacion;

    private LocalDateTime fechaCierre;

    private String contactoPreferido;

    @PrePersist
    protected void onCreate() {
        if (fechaPublicacion == null) {
            fechaPublicacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoAnuncioAyuda.ACTIVO;
        }
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
