
package cl.pagoflex.usuarios.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
public class EstadoController {

    @GetMapping("/estado")
    public Map<String, String> obtenerEstado() {
        return Map.of(
            "servicio", "usuarios-service",
            "estado", "operativo"
        );
    }
}
