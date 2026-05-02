package es.iesdeteis.secretaria.dto;

public class TipoTramitePublicoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionEstimada;
    private Boolean requiereDocumentacion;

    public TipoTramitePublicoDTO() {
    }

    public TipoTramitePublicoDTO(Long id, String nombre, String descripcion,
                                 Integer duracionEstimada, Boolean requiereDocumentacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionEstimada = duracionEstimada;
        this.requiereDocumentacion = requiereDocumentacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(Integer duracionEstimada) {
        this.duracionEstimada = duracionEstimada;
    }

    public Boolean getRequiereDocumentacion() {
        return requiereDocumentacion;
    }

    public void setRequiereDocumentacion(Boolean requiereDocumentacion) {
        this.requiereDocumentacion = requiereDocumentacion;
    }
}

