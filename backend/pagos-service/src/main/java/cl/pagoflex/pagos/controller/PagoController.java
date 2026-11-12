package cl.pagoflex.pagos.controller;

import cl.pagoflex.pagos.model.Pago;
import cl.pagoflex.pagos.service.PagoService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<Pago> registrar(
            @RequestBody Pago datos
    ) {

        Pago pago = pagoService.registrar(datos);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pago);
    }

    @GetMapping
    public ResponseEntity<List<Pago>> listarTodos() {

        return ResponseEntity.ok(
                pagoService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                pagoService.buscarPorId(id)
        );
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Pago>> listarPorUsuario(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                pagoService.listarPorUsuario(idUsuario)
        );
    }

    @GetMapping("/compromiso/{idCompromiso}")
    public ResponseEntity<List<Pago>> listarPorCompromiso(
            @PathVariable Long idCompromiso
    ) {

        return ResponseEntity.ok(
                pagoService.listarPorCompromiso(idCompromiso)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pago> actualizar(
            @PathVariable Long id,
            @RequestBody Pago datos
    ) {

        return ResponseEntity.ok(
                pagoService.actualizar(id, datos)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        pagoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
