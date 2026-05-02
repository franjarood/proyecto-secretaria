package es.iesdeteis.secretaria.exception;

public class AnuncioMercadoNoPerteneceUsuarioException extends RuntimeException {

    public AnuncioMercadoNoPerteneceUsuarioException(String mensaje) {
        super(mensaje);
    }
}
