package es.iesdeteis.secretaria.exception;

public class PagoPasarelaException extends RuntimeException {

    // CONSTRUCTORES

    public PagoPasarelaException() {
        super();
    }

    public PagoPasarelaException(String message) {
        super(message);
    }

    public PagoPasarelaException(String message, Throwable cause) {
        super(message, cause);
    }
}

