package es.iesdeteis.secretaria.config;

import es.iesdeteis.secretaria.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // ATRIBUTOS

    private final CustomUserDetailsService customUserDetailsService;


    // CONSTRUCTOR

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }


    // CONFIGURACIÓN DE SEGURIDAD

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .userDetailsService(customUserDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()

                        // Públicos reales (sin login)
                        .requestMatchers("/clima/**").permitAll()
                        .requestMatchers("/public/**").permitAll()

                        // Catálogos / configuración (coherente con @PreAuthorize de los controllers)
                        .requestMatchers("/tipos-tramite", "/tipos-tramite/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "CONSERJE", "USUARIO", "ALUMNO")

                        .requestMatchers("/ciclos/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "USUARIO", "ALUMNO")

                        .requestMatchers("/cursos/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "USUARIO", "ALUMNO")

                        .requestMatchers("/tipos-matricula/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "USUARIO", "ALUMNO")

                        .requestMatchers("/documentos-requeridos/**")
                        .hasAnyRole("ADMIN", "SECRETARIA", "USUARIO", "ALUMNO")
                        .requestMatchers("/usuarios/me").hasAnyRole("ADMIN", "SECRETARIA", "CONSERJE", "USUARIO", "ALUMNO")
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/incidencias/**").hasAnyRole("ADMIN", "SECRETARIA", "CONSERJE")
                        .requestMatchers("/turnos/**").hasAnyRole("ADMIN", "SECRETARIA", "CONSERJE", "USUARIO", "ALUMNO")
                        .requestMatchers("/reservas/**").hasAnyRole("ADMIN", "SECRETARIA", "USUARIO", "ALUMNO")
                        .requestMatchers("/notificaciones/**").hasAnyRole("ADMIN", "SECRETARIA", "CONSERJE", "USUARIO", "ALUMNO")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }


    // ENCODER DE CONTRASEÑAS

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // AUTHENTICATION MANAGER

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}