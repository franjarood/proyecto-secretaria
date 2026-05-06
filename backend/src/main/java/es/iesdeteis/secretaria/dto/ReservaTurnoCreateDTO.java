package es.iesdeteis.secretaria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
// opcional:
// import jakarta.validation.constraints.Email;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaTurnoCreateDTO {

    @NotNull(message = "La fecha de la cita no puede ser nula")
    private LocalDate fechaCita;

    @NotNull(message = "La hora de la cita no puede ser nula")
    private LocalTime horaCita;

    @NotBlank(message = "El origen del turno no puede estar vacío")
    private String origenTurno;

    @NotEmpty(message = "Debe indicar al menos un tipo de trámite")
    private List<Long> tiposTramiteIds;

    // ✅ Opcional (kiosko): email del visitante para enviar ticket
    // @Email(message = "Email de contacto inválido")
    private String emailContacto;

    public ReservaTurnoCreateDTO() { }

    public ReservaTurnoCreateDTO(LocalDate fechaCita, LocalTime horaCita,
                                 String origenTurno, List<Long> tiposTramiteIds,
                                 String emailContacto) {
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.origenTurno = origenTurno;
        this.tiposTramiteIds = tiposTramiteIds;
        this.emailContacto = emailContacto;
    }

    public LocalDate getFechaCita() { return fechaCita; }
    public void setFechaCita(LocalDate fechaCita) { this.fechaCita = fechaCita; }

    public LocalTime getHoraCita() { return horaCita; }
    public void setHoraCita(LocalTime horaCita) { this.horaCita = horaCita; }

    public String getOrigenTurno() { return origenTurno; }
    public void setOrigenTurno(String origenTurno) { this.origenTurno = origenTurno; }

    public List<Long> getTiposTramiteIds() { return tiposTramiteIds; }
    public void setTiposTramiteIds(List<Long> tiposTramiteIds) { this.tiposTramiteIds = tiposTramiteIds; }

    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }
}