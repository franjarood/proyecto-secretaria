package es.iesdeteis.secretaria.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHash {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("abc123."));
    }
}