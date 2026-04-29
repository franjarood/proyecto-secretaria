package es.iesdeteis.secretaria.exception;

public class NotificacionNoPerteneceUsuarioException extends RuntimeException {

    public NotificacionNoPerteneceUsuarioException(String message) {
        super(message);
    }
}