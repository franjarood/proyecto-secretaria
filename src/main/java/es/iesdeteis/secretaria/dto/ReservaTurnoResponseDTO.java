package es.iesdeteis.secretaria.dto;

import es.iesdeteis.secretaria.model.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ReservaTurnoResponseDTO {

    // ATRIBUTOS

    private Long id;
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private String codigoReferencia;
    private String origenTurno;
    private EstadoReserva estadoReserva;
    private List<String> tiposTramite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // CONSTRUCTORES

    public ReservaTurnoResponseDTO() {
    }

    public ReservaTurnoResponseDTO(Long id, LocalDate fechaCita, LocalTime horaCita,
                                   String codigoReferencia, String origenTurno,
                                   EstadoReserva estadoReserva, List<String> tiposTramite,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.codigoReferencia = codigoReferencia;
        this.origenTurno = origenTurno;
        this.estadoReserva = estadoReserva;
        this.tiposTramite = tiposTramite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public LocalDate getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDate fechaCita) {
        this.fechaCita = fechaCita;
    }

    public LocalTime getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(LocalTime horaCita) {
        this.horaCita = horaCita;
    }

    public String getCodigoReferencia() {
        return codigoReferencia;
    }

    public void setCodigoReferencia(String codigoReferencia) {
        this.codigoReferencia = codigoReferencia;
    }

    public String getOrigenTurno() {
        return origenTurno;
    }

    public void setOrigenTurno(String origenTurno) {
        this.origenTurno = origenTurno;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public List<String> getTiposTramite() {
        return tiposTramite;
    }

    public void setTiposTramite(List<String> tiposTramite) {
        this.tiposTramite = tiposTramite;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}