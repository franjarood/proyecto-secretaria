package es.iesdeteis.secretaria.service;

import com.stripe.model.Event;
import es.iesdeteis.secretaria.model.PagoTasa;

public interface StripeCheckoutService {

    String crearCheckoutUrl(PagoTasa pago);

    PagoTasa procesarCheckoutCompletado(String stripeSessionId);

    PagoTasa procesarCheckoutCancelado(String stripeSessionId);

    Event construirEventoWebhook(String payload, String signatureHeader);
}

