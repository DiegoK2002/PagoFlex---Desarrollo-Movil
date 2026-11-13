package cl.pagoflex.archivos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuarios-service")
public interface UsuariosClient {

    @GetMapping("/api/v1/usuarios/{id}")
    UsuarioResumen buscarPorId(@PathVariable("id") Long id);

    record UsuarioResumen(
            Long idUsuario,
            Boolean activo
    ) {
    }
}
