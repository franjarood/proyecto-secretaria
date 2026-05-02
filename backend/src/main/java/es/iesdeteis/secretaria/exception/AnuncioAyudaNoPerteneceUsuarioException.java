package es.iesdeteis.secretaria.exception;

public class AnuncioAyudaNoPerteneceUsuarioException extends RuntimeException {

    public AnuncioAyudaNoPerteneceUsuarioException(String mensaje) {
        super(mensaje);
    }
}
