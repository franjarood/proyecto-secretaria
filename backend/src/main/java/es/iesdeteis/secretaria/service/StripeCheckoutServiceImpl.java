package es.iesdeteis.secretaria.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import es.iesdeteis.secretaria.exception.PagoPasarelaException;
import es.iesdeteis.secretaria.exception.PagoTasaNoEncontradoException;
import es.iesdeteis.secretaria.exception.StripeNoConfiguradoException;
import es.iesdeteis.secretaria.model.EstadoPago;
import es.iesdeteis.secretaria.model.PagoTasa;
import es.iesdeteis.secretaria.repository.PagoTasaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class StripeCheckoutServiceImpl implements StripeCheckoutService {

    private final PagoTasaRepository pagoTasaRepository;

    @Value("${stripe.secret-key:}")
    private String stripeSecretKey;

    @Value("${stripe.webhook-secret:}")
    private String stripeWebhookSecret;

    @Value("${stripe.success-url:http://localhost:3000/pagos/exito}")
    private String stripeSuccessUrl;

    @Value("${stripe.cancel-url:http://localhost:3000/pagos/cancelado}")
    private String stripeCancelUrl;

    public StripeCheckoutServiceImpl(PagoTasaRepository pagoTasaRepository) {
        this.pagoTasaRepository = pagoTasaRepository;
    }

    @Override
    public String crearCheckoutUrl(PagoTasa pago) {

        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new StripeNoConfiguradoException("Stripe no está configurado (stripe.secret-key vacío)");
        }

        try {
            Stripe.apiKey = stripeSecretKey;

            long unitAmount = convertirAEurosCentimos(pago.getImporte());

            String successUrl = stripeSuccessUrl;
            if (!successUrl.contains("{CHECKOUT_SESSION_ID}")) {
                successUrl = successUrl + (successUrl.contains("?") ? "&" : "?") + "session_id={CHECKOUT_SESSION_ID}";
            }

            String cancelUrl = stripeCancelUrl;
            if (!cancelUrl.contains("{CHECKOUT_SESSION_ID}")) {
                cancelUrl = cancelUrl + (cancelUrl.contains("?") ? "&" : "?") + "session_id={CHECKOUT_SESSION_ID}";
            }

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("eur")
                                                    .setUnitAmount(unitAmount)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(pago.getConcepto())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .putMetadata("pagoId", String.valueOf(pago.getId()))
                    .setClientReferenceId(pago.getReferenciaInterna())
                    .build();

            Session session = Session.create(params);

            pago.setStripeSessionId(session.getId());
            pago.setEstadoPago(EstadoPago.EN_PROCESO);
            pagoTasaRepository.save(pago);

            return session.getUrl();

        } catch (StripeException e) {
            throw new PagoPasarelaException("Error creando sesión de Stripe Checkout", e);
        }
    }

    @Override
    public PagoTasa procesarCheckoutCompletado(String stripeSessionId) {

        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new StripeNoConfiguradoException("Stripe no está configurado (stripe.secret-key vacío)");
        }

        try {
            Stripe.apiKey = stripeSecretKey;

            Session session = Session.retrieve(stripeSessionId);

            PagoTasa pago = pagoTasaRepository.findByStripeSessionId(stripeSessionId)
                    .orElseThrow(() -> new PagoTasaNoEncontradoException("Pago no encontrado para la sesión de Stripe"));

            String paymentStatus = session.getPaymentStatus();
            String paymentIntentId = session.getPaymentIntent();

            pago.setStripePaymentIntentId(paymentIntentId);

            if ("paid".equalsIgnoreCase(paymentStatus)) {
                pago.setEstadoPago(EstadoPago.PAGADO);
                pago.setFechaPago(LocalDateTime.now());
            } else {
                // Si Stripe vuelve pero no está pagado, lo dejamos EN_PROCESO
                pago.setEstadoPago(EstadoPago.EN_PROCESO);
            }

            return pagoTasaRepository.save(pago);

        } catch (StripeException e) {
            throw new PagoPasarelaException("Error consultando estado en Stripe", e);
        }
    }

    @Override
    public PagoTasa procesarCheckoutCancelado(String stripeSessionId) {

        PagoTasa pago = pagoTasaRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new PagoTasaNoEncontradoException("Pago no encontrado para la sesión de Stripe"));

        if (pago.getEstadoPago() != EstadoPago.PAGADO && pago.getEstadoPago() != EstadoPago.ANULADO) {
            pago.setEstadoPago(EstadoPago.CANCELADO);
        }

        return pagoTasaRepository.save(pago);
    }

    @Override
    public Event construirEventoWebhook(String payload, String signatureHeader) {

        if (stripeWebhookSecret == null || stripeWebhookSecret.isBlank()) {
            throw new StripeNoConfiguradoException("Stripe webhook no está configurado (stripe.webhook-secret vacío)");
        }

        try {
            return Webhook.constructEvent(payload, signatureHeader, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            throw new PagoPasarelaException("Firma de webhook de Stripe inválida", e);
        }
    }

    private long convertirAEurosCentimos(BigDecimal importe) {
        if (importe == null) {
            return 0;
        }

        BigDecimal centimos = importe.multiply(BigDecimal.valueOf(100));
        return centimos.longValue();
    }
}

