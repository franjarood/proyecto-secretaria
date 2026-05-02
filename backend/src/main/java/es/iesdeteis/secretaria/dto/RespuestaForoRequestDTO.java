package es.iesdeteis.secretaria.dto;

import jakarta.validation.constraints.NotBlank;

public class RespuestaForoRequestDTO {

	@NotBlank(message = "El contenido no puede estar vacío")
	private String contenido;

	public RespuestaForoRequestDTO() {
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
}

