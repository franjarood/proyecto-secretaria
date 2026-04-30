package es.iesdeteis.secretaria.service;

public interface EmailService {

    void enviarEmail(String destinatario, String asunto, String mensaje);
}