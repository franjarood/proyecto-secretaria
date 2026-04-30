package es.iesdeteis.secretaria.dto;

public class UsuarioActualDTO {

    // =========================
    // ATRIBUTOS
    // =========================

    private Long id;
    private String nombre;
    private String apellidos;
    private String email;

    // =========================
    // CONSTRUCTORES
    // =========================

    public UsuarioActualDTO() {
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

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
}