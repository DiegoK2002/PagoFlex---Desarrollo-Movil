package cl.pagoflex.compromisos.repository;

import cl.pagoflex.compromisos.model.Compromiso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompromisoRepository extends JpaRepository<Compromiso, Long> {

    List<Compromiso> findByIdUsuario(Long idUsuario);
}
