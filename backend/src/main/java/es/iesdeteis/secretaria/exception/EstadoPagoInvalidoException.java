package es.iesdeteis.secretaria.exception;

public class EstadoPagoInvalidoException extends RuntimeException {

    // CONSTRUCTORES

    public EstadoPagoInvalidoException() {
        super();
    }

    public EstadoPagoInvalidoException(String message) {
        super(message);
    }
}

