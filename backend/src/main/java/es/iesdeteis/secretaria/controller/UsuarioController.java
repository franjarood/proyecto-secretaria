package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.UsuarioActualDTO;
import es.iesdeteis.secretaria.dto.UsuarioResponseDTO;
import es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException;
import es.iesdeteis.secretaria.model.Usuario;
import es.iesdeteis.secretaria.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    // ATRIBUTOS

    private final UsuarioService usuarioService;


    // CONSTRUCTOR

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    // MÉTODOS

    // Obtener todos los usuarios (sin password)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UsuarioResponseDTO> getUsuarios() {

        return usuarioService.findAll().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // Obtener usuario por ID (sin password)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UsuarioResponseDTO getUsuarioById(@PathVariable Long id) {

        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        return convertirAResponseDTO(usuario);
    }

    // Crear usuario (sin devolver password)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UsuarioResponseDTO saveUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario usuarioGuardado = usuarioService.save(usuario);
        return convertirAResponseDTO(usuarioGuardado);
    }

    // Actualizar usuario (sin devolver password)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UsuarioResponseDTO updateUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.update(id, usuario);
        return convertirAResponseDTO(usuarioActualizado);
    }

    // Eliminar usuario
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
    }


    // MÉTODOS AUXILIARES

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'USUARIO', 'ALUMNO')")
    public UsuarioActualDTO obtenerUsuarioActual() {
        return usuarioService.obtenerUsuarioActual();
    }

    // Convertir entidad Usuario a DTO (sin password)
    private UsuarioResponseDTO convertirAResponseDTO(Usuario usuario) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getDni(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol(),
                usuario.getCreadoEn()
        );
    }
}