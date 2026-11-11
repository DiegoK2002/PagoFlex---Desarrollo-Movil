package cl.pagoflex.usuarios.repository;

import cl.pagoflex.usuarios.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    Optional<Usuario> findByRut(String rut);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByRut(String rut);

    Page<Usuario> findByActivoTrue(Pageable pageable);
}
