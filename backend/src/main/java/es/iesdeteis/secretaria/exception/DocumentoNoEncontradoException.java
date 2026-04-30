package es.iesdeteis.secretaria.exception;

public class DocumentoNoEncontradoException extends RuntimeException {

    public DocumentoNoEncontradoException(String message) {
        super(message);
    }
}