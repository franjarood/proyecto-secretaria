package es.iesdeteis.secretaria.exception;

public class StripeNoConfiguradoException extends RuntimeException {

    // CONSTRUCTORES

    public StripeNoConfiguradoException() {
        super();
    }

    public StripeNoConfiguradoException(String message) {
        super(message);
    }
}

