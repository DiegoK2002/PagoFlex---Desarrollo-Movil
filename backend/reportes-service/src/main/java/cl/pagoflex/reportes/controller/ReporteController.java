package cl.pagoflex.reportes.controller;

import cl.pagoflex.reportes.model.Reporte;
import cl.pagoflex.reportes.service.ReporteService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @PostMapping
    public ResponseEntity<Reporte> registrar(
            @RequestBody Reporte datos
    ) {

        Reporte reporte = reporteService.registrar(datos);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reporte);
    }

    @GetMapping
    public ResponseEntity<List<Reporte>> listarTodos() {

        return ResponseEntity.ok(
                reporteService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                reporteService.buscarPorId(id)
        );
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Reporte>> listarPorUsuario(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                reporteService.listarPorUsuario(idUsuario)
        );
    }

    @GetMapping("/agente/{idAgente}")
    public ResponseEntity<List<Reporte>> listarPorAgente(
            @PathVariable Long idAgente
    ) {

        return ResponseEntity.ok(
                reporteService.listarPorAgente(idAgente)
        );
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Reporte>> listarPorEstado(
            @PathVariable String estado
    ) {

        return ResponseEntity.ok(
                reporteService.listarPorEstado(estado)
        );
    }

    @GetMapping("/pago/{idPago}")
    public ResponseEntity<List<Reporte>> listarPorPago(
            @PathVariable Long idPago
    ) {

        return ResponseEntity.ok(
                reporteService.listarPorPago(idPago)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reporte> actualizar(
            @PathVariable Long id,
            @RequestBody Reporte datos
    ) {

        return ResponseEntity.ok(
                reporteService.actualizar(id, datos)
        );
    }

    @PutMapping("/{id}/seguimiento")
    public ResponseEntity<Reporte> actualizarSeguimiento(
            @PathVariable Long id,
            @RequestBody Reporte datos
    ) {

        return ResponseEntity.ok(
                reporteService.actualizarSeguimiento(id, datos)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        reporteService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
