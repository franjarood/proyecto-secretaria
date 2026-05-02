package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.CheckoutSessionResponseDTO;
import es.iesdeteis.secretaria.dto.PagoTasaRequestDTO;
import es.iesdeteis.secretaria.dto.PagoTasaResponseDTO;

import java.util.List;

public interface PagoTasaService {

    List<PagoTasaResponseDTO> listarMisPagos();

    PagoTasaResponseDTO obtenerPorId(Long id);

    PagoTasaResponseDTO generarPago(PagoTasaRequestDTO dto);

    CheckoutSessionResponseDTO crearCheckout(Long pagoId);

    PagoTasaResponseDTO anular(Long pagoId);

    List<PagoTasaResponseDTO> listarTodos();

    PagoTasaResponseDTO obtenerAdmin(Long id);

    void procesarPagoExito(String stripeSessionId);

    void procesarPagoCancelado(String stripeSessionId);

    void procesarWebhookStripe(String payload, String signatureHeader);
}

