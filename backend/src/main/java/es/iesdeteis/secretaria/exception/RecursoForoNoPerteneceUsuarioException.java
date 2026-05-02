package es.iesdeteis.secretaria.exception;

public class RecursoForoNoPerteneceUsuarioException extends RuntimeException {

    public RecursoForoNoPerteneceUsuarioException() {
        super();
    }

    public RecursoForoNoPerteneceUsuarioException(String message) {
        super(message);
    }
}

