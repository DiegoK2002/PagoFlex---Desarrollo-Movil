package cl.pagoflex.pagos.repository;

import cl.pagoflex.pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByIdUsuario(Long idUsuario);

    List<Pago> findByIdCompromiso(Long idCompromiso);
}
