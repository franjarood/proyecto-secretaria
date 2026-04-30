package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.LoginRequestDto;
import es.iesdeteis.secretaria.dto.LoginResponseDto;
import es.iesdeteis.secretaria.exception.CredencialesInvalidasException;
import es.iesdeteis.secretaria.model.Usuario;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    // ATRIBUTOS

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;


    // CONSTRUCTOR

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
    }


    // MÉTODOS PROPIOS

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequest) {

        try {
            // Intentamos autenticar al usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            // Si falla, lanzamos nuestra excepción personalizada
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }

        // Si la autenticación va bien, buscamos el usuario
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario no encontrado"));

        // Devolvemos la respuesta
        return new LoginResponseDto(
                "Login correcto",
                usuario.getEmail(),
                usuario.getRol().name()
        );
    }
}