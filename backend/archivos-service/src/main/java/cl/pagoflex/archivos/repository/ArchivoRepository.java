package cl.pagoflex.archivos.repository;

import cl.pagoflex.archivos.model.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {

    List<Archivo> findByIdUsuario(Long idUsuario);

    List<Archivo> findByIdPago(Long idPago);

    List<Archivo> findByTipo(String tipo);
}
