package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private String estado;
    private String fechaCreacion;
    private String observaciones;

    @ManyToMany
    @JoinColumn(name = "tipo_tramite_id")
    private TipoTramite tipoTramite;


    // CONSTRUCTORES
    public Turno() {
    }

    public Turno(Long id, String numero, String estado, String fechaCreacion,
                 String observaciones, TipoTramite tipoTramite) {
        this.id = id;
        this.numero = numero;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.observaciones = observaciones;
        this.tipoTramite = tipoTramite;
    }

    public Turno(String numero, String estado, String fechaCreacion,
                 String observaciones, TipoTramite tipoTramite) {
        this.numero = numero;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.observaciones = observaciones;
        this.tipoTramite = tipoTramite;
    }


    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public TipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(TipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }


    // TO STRING
    @Override
    public String toString() {
        return "Turno [id=" + id + ", numero=" + numero + ", estado=" + estado + "]";
    }
}
