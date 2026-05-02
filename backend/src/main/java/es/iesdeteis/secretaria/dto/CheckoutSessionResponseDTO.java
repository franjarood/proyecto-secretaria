package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.EstadoPago;

public class CheckoutSessionResponseDTO {

    private Long pagoId;
    private String checkoutUrl;
    private EstadoPago estadoPago;
    private String referenciaInterna;

    public CheckoutSessionResponseDTO() {
    }

    public CheckoutSessionResponseDTO(Long pagoId, String checkoutUrl, EstadoPago estadoPago, String referenciaInterna) {
        this.pagoId = pagoId;
        this.checkoutUrl = checkoutUrl;
        this.estadoPago = estadoPago;
        this.referenciaInterna = referenciaInterna;
    }

    public Long getPagoId() {
        return pagoId;
    }

    public void setPagoId(Long pagoId) {
        this.pagoId = pagoId;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getReferenciaInterna() {
        return referenciaInterna;
    }

    public void setReferenciaInterna(String referenciaInterna) {
        this.referenciaInterna = referenciaInterna;
    }
}

