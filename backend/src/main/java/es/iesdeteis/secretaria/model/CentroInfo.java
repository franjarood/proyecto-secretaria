package es.iesdeteis.secretaria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "centro_info")
public class CentroInfo {

    // ATRIBUTOS

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCentro;
    private String direccion;
    private String telefono;
    private String email;
    private String horarioAtencion;

    @Column(length = 1500)
    private String descripcion;

    private String urlWebOficial;

    private Double latitud;
    private Double longitud;

    private Boolean activo;


    // CONSTRUCTORES

    public CentroInfo() {
    }

    public CentroInfo(String nombreCentro, String direccion, String telefono, String email,
                      String horarioAtencion, String descripcion, String urlWebOficial,
                      Double latitud, Double longitud, Boolean activo) {
        this.nombreCentro = nombreCentro;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.horarioAtencion = horarioAtencion;
        this.descripcion = descripcion;
        this.urlWebOficial = urlWebOficial;
        this.latitud = latitud;
        this.longitud = longitud;
        this.activo = activo;
    }


    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCentro() {
        return nombreCentro;
    }

    public void setNombreCentro(String nombreCentro) {
        this.nombreCentro = nombreCentro;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHorarioAtencion() {
        return horarioAtencion;
    }

    public void setHorarioAtencion(String horarioAtencion) {
        this.horarioAtencion = horarioAtencion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlWebOficial() {
        return urlWebOficial;
    }

    public void setUrlWebOficial(String urlWebOficial) {
        this.urlWebOficial = urlWebOficial;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}

