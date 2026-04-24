package es.iesdeteis.secretaria.exception;

public class DocumentoNoPerteneceUsuarioException extends RuntimeException {

    public DocumentoNoPerteneceUsuarioException(String message) {
        super(message);
    }
}