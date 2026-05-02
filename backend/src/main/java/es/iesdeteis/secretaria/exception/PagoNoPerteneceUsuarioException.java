package es.iesdeteis.secretaria.exception;

public class PagoNoPerteneceUsuarioException extends RuntimeException {

    // CONSTRUCTORES

    public PagoNoPerteneceUsuarioException() {
        super();
    }

    public PagoNoPerteneceUsuarioException(String message) {
        super(message);
    }
}

