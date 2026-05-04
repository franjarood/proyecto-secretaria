package es.iesdeteis.secretaria.exception;

public class DocumentoRequeridoNoEncontradoException extends RuntimeException {
    public DocumentoRequeridoNoEncontradoException(String message) {
        super(message);
    }
}
