package es.iesdeteis.secretaria.exception;

public class EstadoTurnoInvalidoException extends RuntimeException {


    public EstadoTurnoInvalidoException() {
        super();
    }

    public EstadoTurnoInvalidoException(String message) {
        super(message);
    }
}