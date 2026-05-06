package es.iesdeteis.secretaria.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    // ATRIBUTOS

    private final JavaMailSender javaMailSender;

    // MODO DEBUG (true = no envía emails reales)
    private final boolean modoDebug = false;



    // CONSTRUCTOR

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    // MÉTODOS PRINCIPALES

    @Override
    public void enviarEmail(String destinatario, String asunto, String mensaje) {

        if (modoDebug) {
            System.out.println("DEBUG EMAIL → " + destinatario);
            System.out.println("Asunto: " + asunto);
            System.out.println("Mensaje: " + mensaje);
            return;
        }

        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(destinatario);
        email.setSubject(asunto);
        email.setText(mensaje);

        javaMailSender.send(email);
    }
}