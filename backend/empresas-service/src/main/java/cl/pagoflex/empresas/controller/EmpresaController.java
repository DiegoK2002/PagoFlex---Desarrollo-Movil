package cl.pagoflex.empresas.controller;

import cl.pagoflex.empresas.model.Empresa;
import cl.pagoflex.empresas.service.EmpresaService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    public ResponseEntity<Empresa> registrar(
            @RequestBody Empresa datos
    ) {

        Empresa empresa = empresaService.registrar(datos);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(empresa);
    }

    @GetMapping
    public ResponseEntity<List<Empresa>> listarTodas() {

        return ResponseEntity.ok(
                empresaService.listarTodas()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                empresaService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> actualizar(
            @PathVariable Long id,
            @RequestBody Empresa datos
    ) {

        return ResponseEntity.ok(
                empresaService.actualizar(id, datos)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        empresaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
