package es.iesdeteis.secretaria.exception;

public class PagoTasaNoEncontradoException extends RuntimeException {

    // CONSTRUCTORES

    public PagoTasaNoEncontradoException() {
        super();
    }

    public PagoTasaNoEncontradoException(String message) {
        super(message);
    }
}

