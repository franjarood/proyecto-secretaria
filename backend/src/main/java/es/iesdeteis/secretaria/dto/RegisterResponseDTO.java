package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.RolUsuario;

public class RegisterResponseDTO {

    private Long id;
    private String nombre;
    private String apellidos;
    private String email;
    private RolUsuario rol;
    private String mensaje;

    // CONSTRUCTORES

    public RegisterResponseDTO() {
    }

    public RegisterResponseDTO(Long id, String nombre, String apellidos, String email, RolUsuario rol, String mensaje) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.rol = rol;
        this.mensaje = mensaje;
    }

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
