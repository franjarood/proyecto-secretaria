package es.iesdeteis.secretaria.dto;

public class LoginResponseDto {

    // ATRIBUTOS

    private String mensaje;
    private String email;
    private String rol;


    // CONSTRUCTORES

    public LoginResponseDto() {
    }

    public LoginResponseDto(String mensaje, String email, String rol) {
        this.mensaje = mensaje;
        this.email = email;
        this.rol = rol;
    }


    // GETTERS Y SETTERS

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}