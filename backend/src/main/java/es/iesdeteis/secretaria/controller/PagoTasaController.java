package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.CheckoutSessionResponseDTO;
import es.iesdeteis.secretaria.dto.PagoTasaRequestDTO;
import es.iesdeteis.secretaria.dto.PagoTasaResponseDTO;
import es.iesdeteis.secretaria.service.PagoTasaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PagoTasaController {

    // ATRIBUTOS

    private final PagoTasaService pagoTasaService;


    // CONSTRUCTOR

    public PagoTasaController(PagoTasaService pagoTasaService) {
        this.pagoTasaService = pagoTasaService;
    }


    // =========================
    // ALUMNO / USUARIO
    // =========================

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'ALUMNO', 'CONSERJE', 'PROFESOR')")
    @GetMapping("/pagos/mis-pagos")
    public List<PagoTasaResponseDTO> misPagos() {
        return pagoTasaService.listarMisPagos();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'ALUMNO', 'CONSERJE', 'PROFESOR')")
    @GetMapping("/pagos/{id}")
    public PagoTasaResponseDTO obtener(@PathVariable Long id) {
        return pagoTasaService.obtenerPorId(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'ALUMNO')")
    @PostMapping("/pagos/{id}/crear-checkout")
    public CheckoutSessionResponseDTO crearCheckout(@PathVariable Long id) {
        return pagoTasaService.crearCheckout(id);
    }

    // Stripe redirigirá aquí (normalmente lo consumirá el frontend)
    @GetMapping("/pagos/exito")
    public void exito(@RequestParam(name = "session_id", required = false) String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            pagoTasaService.procesarPagoExito(sessionId);
        }
    }

    @GetMapping("/pagos/cancelado")
    public void cancelado(@RequestParam(name = "session_id", required = false) String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            pagoTasaService.procesarPagoCancelado(sessionId);
        }
    }


    // =========================
    // ADMIN / SECRETARÍA
    // =========================

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PostMapping("/pagos/generar")
    public PagoTasaResponseDTO generar(@Valid @RequestBody PagoTasaRequestDTO dto) {
        return pagoTasaService.generarPago(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @PutMapping("/pagos/{id}/anular")
    public PagoTasaResponseDTO anular(@PathVariable Long id) {
        return pagoTasaService.anular(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @GetMapping("/admin/pagos")
    public List<PagoTasaResponseDTO> listarTodos() {
        return pagoTasaService.listarTodos();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA')")
    @GetMapping("/admin/pagos/{id}")
    public PagoTasaResponseDTO obtenerAdmin(@PathVariable Long id) {
        return pagoTasaService.obtenerAdmin(id);
    }


    // =========================
    // WEBHOOK STRIPE (OPCIONAL)
    // =========================

    @PostMapping("/pagos/webhook/stripe")
    public void webhookStripe(@RequestBody String payload,
                              @RequestHeader(name = "Stripe-Signature", required = false) String signature) {
        if (signature == null || signature.isBlank()) {
            return;
        }
        pagoTasaService.procesarWebhookStripe(payload, signature);
    }
}

