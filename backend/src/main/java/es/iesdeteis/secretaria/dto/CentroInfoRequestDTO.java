package es.iesdeteis.secretaria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CentroInfoRequestDTO {

    // ATRIBUTOS

    @NotBlank(message = "El nombre del centro no puede estar vacío")
    private String nombreCentro;

    private String direccion;
    private String telefono;

    @Email(message = "El formato del email no es válido")
    private String email;

    private String horarioAtencion;
    private String descripcion;
    private String urlWebOficial;
    private Double latitud;
    private Double longitud;

    @NotNull(message = "Debe indicar si la información del centro está activa")
    private Boolean activo;


    // CONSTRUCTORES

    public CentroInfoRequestDTO() {
    }


    // GETTERS Y SETTERS

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

