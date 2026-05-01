package es.iesdeteis.secretaria.dto;

public class AsistenteRecomendacionDTO {

    public enum TipoRecomendacion {
        INFO,
        AVISO,
        URGENTE,
        EXITO
    }

    private String titulo;
    private String mensaje;
    private TipoRecomendacion tipo;
    private String accion;
    private String urlDestino;

    public AsistenteRecomendacionDTO() {
    }

    public AsistenteRecomendacionDTO(String titulo, String mensaje, TipoRecomendacion tipo, String accion, String urlDestino) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.accion = accion;
        this.urlDestino = urlDestino;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public TipoRecomendacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoRecomendacion tipo) {
        this.tipo = tipo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getUrlDestino() {
        return urlDestino;
    }

    public void setUrlDestino(String urlDestino) {
        this.urlDestino = urlDestino;
    }
}

