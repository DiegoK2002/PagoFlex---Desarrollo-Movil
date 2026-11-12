package cl.pagoflex.empresas.repository;

import cl.pagoflex.empresas.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository
        extends JpaRepository<Empresa, Long> {
}
