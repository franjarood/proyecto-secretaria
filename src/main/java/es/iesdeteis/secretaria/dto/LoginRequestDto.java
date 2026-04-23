package es.iesdeteis.secretaria.dto;

public class LoginRequestDto {

    // ATRIBUTOS

    private String email;
    private String password;


    // CONSTRUCTORES

    public LoginRequestDto() {
    }

    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }


    // GETTERS Y SETTERS

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}