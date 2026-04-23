package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.UsuarioResponseDTO;
import es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException;
import es.iesdeteis.secretaria.model.Usuario;
import es.iesdeteis.secretaria.service.UsuarioService;
import jakarta.validation.Valid;
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
    @GetMapping
    public List<UsuarioResponseDTO> getUsuarios() {

        return usuarioService.findAll().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // Obtener usuario por ID (sin password)
    @GetMapping("/{id}")
    public UsuarioResponseDTO getUsuarioById(@PathVariable Long id) {

        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        return convertirAResponseDTO(usuario);
    }

    // Crear usuario (aquí sí usamos entidad completa)
    @PostMapping
    public Usuario saveUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.save(usuario);
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public Usuario updateUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return usuarioService.update(id, usuario);
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
    }


    // =========================
    // MÉTODOS AUXILIARES
    // =========================

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