package es.iesdeteis.secretaria.service;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import es.iesdeteis.secretaria.dto.CheckoutSessionResponseDTO;
import es.iesdeteis.secretaria.dto.PagoTasaRequestDTO;
import es.iesdeteis.secretaria.dto.PagoTasaResponseDTO;
import es.iesdeteis.secretaria.exception.EstadoPagoInvalidoException;
import es.iesdeteis.secretaria.exception.PagoNoPerteneceUsuarioException;
import es.iesdeteis.secretaria.exception.PagoTasaNoEncontradoException;
import es.iesdeteis.secretaria.model.*;
import es.iesdeteis.secretaria.repository.PagoTasaRepository;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PagoTasaServiceImpl implements PagoTasaService {

    // ATRIBUTOS

    private final PagoTasaRepository pagoTasaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialAccionService historialAccionService;
    private final StripeCheckoutService stripeCheckoutService;


    // CONSTRUCTOR

    public PagoTasaServiceImpl(PagoTasaRepository pagoTasaRepository,
                              UsuarioRepository usuarioRepository,
                              HistorialAccionService historialAccionService,
                              StripeCheckoutService stripeCheckoutService) {
        this.pagoTasaRepository = pagoTasaRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialAccionService = historialAccionService;
        this.stripeCheckoutService = stripeCheckoutService;
    }


    // MÉTODOS

    @Override
    public List<PagoTasaResponseDTO> listarMisPagos() {
        Usuario actual = obtenerUsuarioActualObligatorio();

        return pagoTasaRepository.findByUsuarioIdOrderByFechaCreacionDesc(actual.getId()).stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public PagoTasaResponseDTO obtenerPorId(Long id) {
        PagoTasa pago = obtenerPagoSeguro(id);
        return convertirAResponseDTO(pago);
    }

    @Override
    public PagoTasaResponseDTO generarPago(PagoTasaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException("Usuario no encontrado"));

        PagoTasa pago = new PagoTasa();
        pago.setUsuario(usuario);
        pago.setConcepto(dto.getConcepto());
        pago.setImporte(dto.getImporte());
        pago.setEstadoPago(EstadoPago.PENDIENTE);
        pago.setObservaciones(dto.getObservaciones());

        PagoTasa guardado = pagoTasaRepository.save(pago);

        // Referencia interna sencilla basada en ID (única y estable)
        String ref = String.format("TAS-%d-%04d", LocalDate.now().getYear(), guardado.getId());
        guardado.setReferenciaInterna(ref);

        guardado = pagoTasaRepository.save(guardado);

        registrarHistorial("GENERAR_PAGO", "Generado pago " + ref + " para usuarioId=" + usuario.getId(), guardado.getId());

        return convertirAResponseDTO(guardado);
    }

    @Override
    public CheckoutSessionResponseDTO crearCheckout(Long pagoId) {
        PagoTasa pago = obtenerPagoSeguro(pagoId);

        if (pago.getEstadoPago() != EstadoPago.PENDIENTE
                && pago.getEstadoPago() != EstadoPago.CANCELADO
                && pago.getEstadoPago() != EstadoPago.ERROR) {
            throw new EstadoPagoInvalidoException("El estado actual (" + pago.getEstadoPago() + ") no permite crear checkout");
        }

        String url = stripeCheckoutService.crearCheckoutUrl(pago);

        registrarHistorial("CREAR_CHECKOUT", "Creada sesión de Stripe para pago " + pago.getReferenciaInterna(), pago.getId());

        return new CheckoutSessionResponseDTO(pago.getId(), url, pago.getEstadoPago(), pago.getReferenciaInterna());
    }

    @Override
    public PagoTasaResponseDTO anular(Long pagoId) {
        PagoTasa pago = pagoTasaRepository.findById(pagoId)
                .orElseThrow(() -> new PagoTasaNoEncontradoException("Pago no encontrado"));

        if (pago.getEstadoPago() == EstadoPago.PAGADO) {
            throw new EstadoPagoInvalidoException("No se puede anular un pago ya PAGADO");
        }

        pago.setEstadoPago(EstadoPago.ANULADO);
        PagoTasa guardado = pagoTasaRepository.save(pago);

        registrarHistorial("ANULAR_PAGO", "Anulado pago " + pago.getReferenciaInterna(), pago.getId());

        return convertirAResponseDTO(guardado);
    }

    @Override
    public List<PagoTasaResponseDTO> listarTodos() {
        return pagoTasaRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    @Override
    public PagoTasaResponseDTO obtenerAdmin(Long id) {
        PagoTasa pago = pagoTasaRepository.findById(id)
                .orElseThrow(() -> new PagoTasaNoEncontradoException("Pago no encontrado"));
        return convertirAResponseDTO(pago);
    }

    @Override
    public void procesarPagoExito(String stripeSessionId) {
        PagoTasa pago = stripeCheckoutService.procesarCheckoutCompletado(stripeSessionId);
        registrarHistorial("PAGO_CONFIRMADO", "Pago confirmado " + pago.getReferenciaInterna(), pago.getId());
    }

    @Override
    public void procesarPagoCancelado(String stripeSessionId) {
        PagoTasa pago = stripeCheckoutService.procesarCheckoutCancelado(stripeSessionId);
        registrarHistorial("PAGO_CANCELADO", "Pago cancelado " + pago.getReferenciaInterna(), pago.getId());
    }

    @Override
    public void procesarWebhookStripe(String payload, String signatureHeader) {
        Event event = stripeCheckoutService.construirEventoWebhook(payload, signatureHeader);

        if (event == null || event.getType() == null) {
            return;
        }

        // Webhook MVP: solo manejamos checkout.session.completed
        if ("checkout.session.completed".equals(event.getType())) {
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            if (dataObjectDeserializer == null) {
                return;
            }

            Session session = null;
            try {
                session = (Session) dataObjectDeserializer.getObject().orElse(null);
            } catch (Exception ignored) {
            }

            if (session != null && session.getId() != null) {
                procesarPagoExito(session.getId());
            }
        }
    }


    // =========================
    // SEGURIDAD (anti-IDOR)
    // =========================

    private PagoTasa obtenerPagoSeguro(Long pagoId) {
        Usuario actual = obtenerUsuarioActualObligatorio();

        boolean esPersonalCentro = actual.getRol() == RolUsuario.ADMIN || actual.getRol() == RolUsuario.SECRETARIA;

        if (esPersonalCentro) {
            return pagoTasaRepository.findById(pagoId)
                    .orElseThrow(() -> new PagoTasaNoEncontradoException("Pago no encontrado"));
        }

        return pagoTasaRepository.findByIdAndUsuarioId(pagoId, actual.getId())
                .orElseThrow(() -> new PagoNoPerteneceUsuarioException("No puedes acceder a un pago que no es tuyo"));
    }

    private Usuario obtenerUsuarioActualObligatorio() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new PagoNoPerteneceUsuarioException("Usuario no autenticado");
        }

        String email = auth.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException("Usuario no encontrado"));
    }


    // =========================
    // MAPEOS
    // =========================

    private PagoTasaResponseDTO convertirAResponseDTO(PagoTasa p) {
        PagoTasaResponseDTO dto = new PagoTasaResponseDTO();
        dto.setId(p.getId());
        dto.setUsuarioId(p.getUsuario() != null ? p.getUsuario().getId() : null);
        dto.setConcepto(p.getConcepto());
        dto.setImporte(p.getImporte());
        dto.setEstadoPago(p.getEstadoPago());
        dto.setReferenciaInterna(p.getReferenciaInterna());
        dto.setFechaCreacion(p.getFechaCreacion());
        dto.setFechaPago(p.getFechaPago());
        dto.setObservaciones(p.getObservaciones());
        return dto;
    }

    private void registrarHistorial(String accion, String descripcion, Long idEntidad) {
        HistorialAccion h = new HistorialAccion();
        h.setAccion(accion);
        h.setDescripcion(descripcion);
        h.setEntidadAfectada("PagoTasa");
        h.setIdEntidad(idEntidad);
        h.setUsuarioResponsable(obtenerIdUsuarioActual());
        historialAccionService.save(h);
    }

    private Long obtenerIdUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .map(Usuario::getId)
                .orElse(null);
    }
}

