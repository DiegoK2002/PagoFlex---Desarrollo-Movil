package cl.pagoflex.reportes.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pagos-service")
public interface PagosClient {

    @GetMapping("/api/v1/pagos/{id}")
    PagoResumen buscarPorId(@PathVariable("id") Long id);

    record PagoResumen(
            Long idPago,
            Long idUsuario
    ) {
    }
}
