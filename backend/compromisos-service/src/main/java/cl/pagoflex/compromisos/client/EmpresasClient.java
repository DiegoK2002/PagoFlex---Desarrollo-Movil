package cl.pagoflex.compromisos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "empresas-service")
public interface EmpresasClient {

    @GetMapping("/api/v1/empresas/{id}")
    EmpresaResumen buscarPorId(@PathVariable("id") Long id);

    record EmpresaResumen(
            Long idEmpresa,
            Boolean activo
    ) {
    }
}
