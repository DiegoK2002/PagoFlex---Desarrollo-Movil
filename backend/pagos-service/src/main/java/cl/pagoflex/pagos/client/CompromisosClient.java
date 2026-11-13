package cl.pagoflex.pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "compromisos-service")
public interface CompromisosClient {

    @GetMapping("/api/v1/compromisos/{id}")
    CompromisoResumen buscarPorId(@PathVariable("id") Long id);

    record CompromisoResumen(
            Long idCompromiso,
            Long idUsuario,
            Boolean activo
    ) {
    }
}
