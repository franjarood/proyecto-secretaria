package es.iesdeteis.secretaria.exception;

public class IncidenciaNoEncontradaException extends RuntimeException {

    // CONSTRUCTORES

    public IncidenciaNoEncontradaException() {
        super();
    }

    public IncidenciaNoEncontradaException(String message) {
        super(message);
    }
}