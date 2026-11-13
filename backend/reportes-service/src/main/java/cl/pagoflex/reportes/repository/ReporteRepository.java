package cl.pagoflex.reportes.repository;

import cl.pagoflex.reportes.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByIdUsuario(Long idUsuario);

    List<Reporte> findByIdAgente(Long idAgente);

    List<Reporte> findByEstado(String estado);

    List<Reporte> findByIdPago(Long idPago);
}
