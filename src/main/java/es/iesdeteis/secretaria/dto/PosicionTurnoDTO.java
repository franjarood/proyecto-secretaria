package es.iesdeteis.secretaria.dto;

public class PosicionTurnoDTO {

    private int posicion;
    private int personasDelante;

    // CONSTRUCTORES
    public PosicionTurnoDTO() {
    }

    public PosicionTurnoDTO(int posicion, int personasDelante) {
        this.posicion = posicion;
        this.personasDelante = personasDelante;
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
}