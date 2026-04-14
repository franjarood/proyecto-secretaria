package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de contrato para servicios de gestión de usuarios.
 */
public interface UsuarioService {

    Usuario crearUsuario(Usuario usuario);

    List<Usuario> obtenerTodos();

    Optional<Usuario> obtenerPorId(Long id);

    Optional<Usuario> obtenerPorEmail(String email);

    Optional<Usuario> obtenerPorDni(String dni);

    Usuario actualizarUsuario(Long id, Usuario usuarioActualizado);

    void eliminarUsuario(Long id);
}