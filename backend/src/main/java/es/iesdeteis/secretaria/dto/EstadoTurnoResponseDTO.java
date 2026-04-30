package es.iesdeteis.secretaria.dto;

public class EstadoTurnoResponseDTO {

    private int posicion;
    private int personasDelante;
    private int tiempoEspera;
    private String estado;

    // CONSTRUCTORES
    public EstadoTurnoResponseDTO() {
    }

    public EstadoTurnoResponseDTO(int posicion, int personasDelante, int tiempoEspera, String estado) {
        this.posicion = posicion;
        this.personasDelante = personasDelante;
        this.tiempoEspera = tiempoEspera;
        this.estado = estado;
    }

    // GETTERS Y SETTERS
    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getPersonasDelante() {
        return personasDelante;
    }

    public void setPersonasDelante(int personasDelante) {
        this.personasDelante = personasDelante;
    }

    public int getTiempoEspera() {
        return tiempoEspera;
    }

    public void setTiempoEspera(int tiempoEspera) {
        this.tiempoEspera = tiempoEspera;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}