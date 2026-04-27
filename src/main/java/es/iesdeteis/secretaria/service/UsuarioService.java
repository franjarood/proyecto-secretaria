package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.UsuarioActualDTO;
import es.iesdeteis.secretaria.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> findAll();

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByDni(String dni);

    Usuario save(Usuario usuario);

    Usuario update(Long id, Usuario usuarioActualizado);

    void deleteById(Long id);

    UsuarioActualDTO obtenerUsuarioActual();
}