package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.RolUsuario;
import jakarta.validation.constraints.NotNull;

public class CambiarRolRequestDTO {

    @NotNull(message = "El rol es obligatorio")
    private RolUsuario rol;

    public CambiarRolRequestDTO() {
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}