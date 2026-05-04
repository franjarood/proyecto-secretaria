package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.LoginRequestDto;
import es.iesdeteis.secretaria.dto.LoginResponseDto;
import es.iesdeteis.secretaria.dto.RegisterRequestDTO;
import es.iesdeteis.secretaria.dto.RegisterResponseDTO;
import es.iesdeteis.secretaria.exception.CredencialesInvalidasException;
import es.iesdeteis.secretaria.exception.UsuarioDuplicadoException;
import es.iesdeteis.secretaria.model.RolUsuario;
import es.iesdeteis.secretaria.model.Usuario;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    // ATRIBUTOS

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    // CONSTRUCTOR

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO registerRequest) {

        // Validar que el email no exista
        if (usuarioRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UsuarioDuplicadoException("El email ya está registrado");
        }

        // Validar que el DNI no exista
        if (usuarioRepository.findByDni(registerRequest.getDni()).isPresent()) {
            throw new UsuarioDuplicadoException("El DNI ya está registrado");
        }

        // Crear usuario con rol USUARIO forzado
        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setApellidos(registerRequest.getApellidos());
        usuario.setDni(registerRequest.getDni());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setTelefono(registerRequest.getTelefono());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Cifrar contraseña
        usuario.setRol(RolUsuario.USUARIO); // Rol USUARIO forzado, el cliente NO puede elegir
        usuario.setCreadoEn(LocalDateTime.now());

        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Devolver respuesta
        return new RegisterResponseDTO(
                usuarioGuardado.getId(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getApellidos(),
                usuarioGuardado.getEmail(),
                usuarioGuardado.getRol(),
                "Usuario registrado correctamente. Puedes iniciar sesión ahora."
        );
    }
}