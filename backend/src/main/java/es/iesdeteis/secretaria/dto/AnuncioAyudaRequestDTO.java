package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.TipoAnuncioAyuda;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AnuncioAyudaRequestDTO {

    @NotNull
    private TipoAnuncioAyuda tipo;

    @NotBlank
    private String modulo;

    @NotBlank
    private String descripcion;

    private String contactoPreferido;

    // Getters y setters

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

    public String getContactoPreferido() {
        return contactoPreferido;
    }

    public void setContactoPreferido(String contactoPreferido) {
        this.contactoPreferido = contactoPreferido;
    }
}
