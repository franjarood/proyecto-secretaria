package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.EstadoTemaForo;

import java.time.LocalDateTime;
import java.util.List;

public class TemaForoResponseDTO {

	private Long id;
	private String titulo;
	private String contenido;
	private String modulo;

	private Long autorId;
	private String autorNombre;

	private LocalDateTime fechaCreacion;
	private LocalDateTime fechaActualizacion;

	private EstadoTemaForo estado;
	private Boolean visible;

	private List<RespuestaForoResponseDTO> respuestas;

	public TemaForoResponseDTO() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public Long getAutorId() {
		return autorId;
	}

	public void setAutorId(Long autorId) {
		this.autorId = autorId;
	}

	public String getAutorNombre() {
		return autorNombre;
	}

	public void setAutorNombre(String autorNombre) {
		this.autorNombre = autorNombre;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDateTime getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public EstadoTemaForo getEstado() {
		return estado;
	}

	public void setEstado(EstadoTemaForo estado) {
		this.estado = estado;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public List<RespuestaForoResponseDTO> getRespuestas() {
		return respuestas;
	}

	public void setRespuestas(List<RespuestaForoResponseDTO> respuestas) {
		this.respuestas = respuestas;
	}
}

