package cl.pagoflex.archivos.controller;

import cl.pagoflex.archivos.model.Archivo;
import cl.pagoflex.archivos.service.ArchivoService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/archivos")
public class ArchivoController {

    private final ArchivoService archivoService;

    public ArchivoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    @PostMapping(
            value = "/subir",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Archivo> subir(
            @RequestParam Long idUsuario,
            @RequestParam(required = false) Long idPago,
            @RequestParam String tipo,
            @RequestPart("archivo") MultipartFile archivo
    ) {

        Archivo registro = archivoService.subir(
                idUsuario,
                idPago,
                tipo,
                archivo
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registro);
    }

    @GetMapping
    public ResponseEntity<List<Archivo>> listarTodos() {

        return ResponseEntity.ok(
                archivoService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Archivo> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                archivoService.buscarPorId(id)
        );
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Archivo>> listarPorUsuario(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                archivoService.listarPorUsuario(idUsuario)
        );
    }

    @GetMapping("/pago/{idPago}")
    public ResponseEntity<List<Archivo>> listarPorPago(
            @PathVariable Long idPago
    ) {

        return ResponseEntity.ok(
                archivoService.listarPorPago(idPago)
        );
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Archivo>> listarPorTipo(
            @PathVariable String tipo
    ) {

        return ResponseEntity.ok(
                archivoService.listarPorTipo(tipo)
        );
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargar(
            @PathVariable Long id
    ) {

        Archivo archivo = archivoService.buscarPorId(id);

        Resource recurso = archivoService.descargar(id);

        ContentDisposition disposicion = ContentDisposition
                .attachment()
                .filename(
                        archivo.getNombreOriginal(),
                        StandardCharsets.UTF_8
                )
                .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposicion.toString()
                )
                .contentType(
                        MediaType.parseMediaType(
                                archivo.getTipoMime()
                        )
                )
                .contentLength(archivo.getTamanoBytes())
                .body(recurso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        archivoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
