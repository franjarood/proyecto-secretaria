package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.UsuarioActualDTO;
import es.iesdeteis.secretaria.dto.UsuarioResponseDTO;
import es.iesdeteis.secretaria.exception.UsuarioDuplicadoException;
import es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException;
import es.iesdeteis.secretaria.model.Usuario;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Optional<Usuario> findByDni(String dni) {
        return usuarioRepository.findByDni(dni);
    }

    @Override
    public Usuario save(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new UsuarioDuplicadoException("El email ya existe en el sistema");
        }

        if (usuarioRepository.findByDni(usuario.getDni()).isPresent()) {
            throw new UsuarioDuplicadoException("El DNI ya existe en el sistema");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario update(Long id, Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        if (!usuario.getEmail().equals(usuarioActualizado.getEmail())
                && usuarioRepository.findByEmail(usuarioActualizado.getEmail()).isPresent()) {
            throw new UsuarioDuplicadoException("El email ya existe en el sistema");
        }

        if (!usuario.getDni().equals(usuarioActualizado.getDni())
                && usuarioRepository.findByDni(usuarioActualizado.getDni()).isPresent()) {
            throw new UsuarioDuplicadoException("El DNI ya existe en el sistema");
        }

        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setApellidos(usuarioActualizado.getApellidos());
        usuario.setDni(usuarioActualizado.getDni());
        usuario.setEmail(usuarioActualizado.getEmail());
        usuario.setTelefono(usuarioActualizado.getTelefono());
        usuario.setRol(usuarioActualizado.getRol());

        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado");
        }

        usuarioRepository.deleteById(id);
    }

    @Override
    public UsuarioActualDTO obtenerUsuarioActual() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        return convertirAUsuarioActualDTO(usuario);
    }

    // MÉTODOS AUXILIARES


    private UsuarioActualDTO convertirAUsuarioActualDTO(Usuario usuario) {

        UsuarioActualDTO dto = new UsuarioActualDTO();

        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellidos(usuario.getApellidos());
        dto.setEmail(usuario.getEmail());

        return dto;
    }
}