package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import es.iesdeteis.secretaria.model.RolUsuario;
import java.util.List;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByDni(String dni);

    List<Usuario> findByRol(RolUsuario rol);

}