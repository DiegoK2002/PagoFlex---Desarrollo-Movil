package cl.pagoflex.compromisos.controller;

import cl.pagoflex.compromisos.model.Compromiso;
import cl.pagoflex.compromisos.service.CompromisoService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/compromisos")
public class CompromisoController {

    private final CompromisoService compromisoService;

    public CompromisoController(CompromisoService compromisoService) {
        this.compromisoService = compromisoService;
    }

    @PostMapping
    public ResponseEntity<Compromiso> registrar(
            @RequestBody Compromiso datos
    ) {

        Compromiso compromiso = compromisoService.registrar(datos);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(compromiso);
    }

    @GetMapping
    public ResponseEntity<List<Compromiso>> listarTodos() {

        return ResponseEntity.ok(
                compromisoService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compromiso> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                compromisoService.buscarPorId(id)
        );
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Compromiso>> listarPorUsuario(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                compromisoService.listarPorUsuario(idUsuario)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compromiso> actualizar(
            @PathVariable Long id,
            @RequestBody Compromiso datos
    ) {

        return ResponseEntity.ok(
                compromisoService.actualizar(id, datos)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        compromisoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
