package es.iesdeteis.secretaria.model;

public enum EstadoTurno {
    RESERVADO,
    CONFIRMADO,
    EN_COLA,
    LLAMADO,
    EN_ATENCION,
    PAUSADO,
    REANUDADO,
    FINALIZADO,
    INCOMPLETO,
    CANCELADO;

    // MÉTODO PROPIO
    public boolean esActivo() {
        return this == CONFIRMADO
                || this == EN_COLA
                || this == LLAMADO
                || this == EN_ATENCION
                || this == PAUSADO
                || this == REANUDADO;
    }
}