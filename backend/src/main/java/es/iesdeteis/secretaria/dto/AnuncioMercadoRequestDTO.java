package es.iesdeteis.secretaria.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class AnuncioMercadoRequestDTO {

    @NotBlank
    private String titulo;

    @NotBlank
    private String descripcion;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal precio;

    private String contactoPreferido;

    private String ubicacion;

    // Getters y setters

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getContactoPreferido() {
        return contactoPreferido;
    }

    public void setContactoPreferido(String contactoPreferido) {
        this.contactoPreferido = contactoPreferido;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
