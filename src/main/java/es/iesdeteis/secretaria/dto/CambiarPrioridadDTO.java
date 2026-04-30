package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.PrioridadTurno;
import jakarta.validation.constraints.NotNull;

public class CambiarPrioridadDTO {

    @NotNull(message = "El tipo de prioridad es obligatorio")
    private PrioridadTurno tipo;

    private String motivo;

    public CambiarPrioridadDTO() {
    }

    public PrioridadTurno getTipo() {
        return tipo;
    }

    public void setTipo(PrioridadTurno tipo) {
        this.tipo = tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}