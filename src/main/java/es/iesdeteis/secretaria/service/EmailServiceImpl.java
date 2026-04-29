package es.iesdeteis.secretaria.service;

import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    // MÉTODOS PRINCIPALES

    @Override
    public void enviarEmail(String destinatario, String asunto, String mensaje) {

        try {
            // De momento simulamos el envío del email
            System.out.println("Enviando email a: " + destinatario);
            System.out.println("Asunto: " + asunto);
            System.out.println("Mensaje: " + mensaje);

        } catch (Exception e) {
            // IMPORTANTE:
            // Si falla el email, no rompemos el sistema
            System.out.println("Error al enviar el email: " + e.getMessage());
        }
    }

}