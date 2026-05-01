package es.iesdeteis.secretaria.dto;

public class AccesoRapidoDTO {

    private String titulo;
    private String descripcion;
    private String accion;
    private String urlDestino;

    public AccesoRapidoDTO() {
    }

    public AccesoRapidoDTO(String titulo, String descripcion, String accion, String urlDestino) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.accion = accion;
        this.urlDestino = urlDestino;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

