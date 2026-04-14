package es.iesdeteis.secretaria.repository;

import es.iesdeteis.secretaria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Proporciona operaciones CRUD básicas y búsquedas por email y DNI.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su email único.
     * @param email dirección de correo del usuario
     * @return Optional con el usuario si existe, vacío si no
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por su DNI/NIE único.
     * @param dni documento de identidad del usuario
     * @return Optional con el usuario si existe, vacío si no
     */
    Optional<Usuario> findByDni(String dni);
}
