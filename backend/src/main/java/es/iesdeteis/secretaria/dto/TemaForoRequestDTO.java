package es.iesdeteis.secretaria.dto;

import jakarta.validation.constraints.NotBlank;

public class TemaForoRequestDTO {

	// ATRIBUTOS

	@NotBlank(message = "El título no puede estar vacío")
	private String titulo;

	@NotBlank(message = "El contenido no puede estar vacío")
	private String contenido;

	private String modulo;


	// CONSTRUCTORES

	public TemaForoRequestDTO() {
	}


	// GETTERS Y SETTERS

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
}

