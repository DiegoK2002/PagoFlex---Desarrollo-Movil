package cl.pagoflex.archivos.service;

import cl.pagoflex.archivos.client.PagosClient;
import cl.pagoflex.archivos.model.Archivo;
import cl.pagoflex.archivos.repository.ArchivoRepository;

import feign.FeignException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ArchivoService {

    private final ArchivoRepository archivoRepository;
    private final PagosClient pagosClient;
    private final Path directorioBase;

    public ArchivoService(
            ArchivoRepository archivoRepository,
            PagosClient pagosClient,
            @Value("${pagoflex.archivos.directorio}") String directorio
    ) {
        this.archivoRepository = archivoRepository;
        this.pagosClient = pagosClient;
        this.directorioBase = Path.of(directorio)
                .toAbsolutePath()
                .normalize();
    }

    public Archivo subir(
            Long idUsuario,
            Long idPago,
            String tipo,
            MultipartFile archivo
    ) {

        if (idUsuario == null || idUsuario <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El ID del usuario es invalido"
            );
        }

        if (idPago != null && idPago <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El ID del pago es invalido"
            );
        }

        String tipoNormalizado = normalizarTipo(tipo);

        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un archivo"
            );
        }

        if (archivo.getSize() > 10 * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El archivo supera los 10 MB"
            );
        }

        String nombreOriginal = archivo.getOriginalFilename();

        if (nombreOriginal == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre de archivo invalido"
            );
        }

        nombreOriginal = nombreOriginal.replace("\\", "/");
        nombreOriginal = nombreOriginal.substring(
                nombreOriginal.lastIndexOf("/") + 1
        ).trim();

        if (nombreOriginal.isBlank()
                || nombreOriginal.length() > 255
                || nombreOriginal.chars().anyMatch(
                        caracter -> Character.isISOControl(caracter)
                )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre de archivo invalido"
            );
        }

        String extension = obtenerExtension(nombreOriginal);
        String mime = archivo.getContentType();

        validarFormato(extension, mime, tipoNormalizado);
        validarPago(idPago, idUsuario);

        String nombreAlmacenado = UUID.randomUUID() + extension;
        Path destino = resolverRuta(nombreAlmacenado);

        try {

            byte[] contenido = archivo.getBytes();

            validarContenido(contenido, extension);

            Files.createDirectories(directorioBase);

            Files.write(
                    destino,
                    contenido,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
            );

            Archivo registro = new Archivo();

            registro.setIdUsuario(idUsuario);
            registro.setIdPago(idPago);
            registro.setTipo(tipoNormalizado);
            registro.setNombreOriginal(nombreOriginal);
            registro.setNombreAlmacenado(nombreAlmacenado);
            registro.setTipoMime(mime);
            registro.setTamanoBytes((long) contenido.length);
            registro.setRutaRelativa(nombreAlmacenado);
            registro.setActivo(true);

            try {
                return archivoRepository.save(registro);
            } catch (RuntimeException error) {
                Files.deleteIfExists(destino);
                throw error;
            }

        } catch (IOException error) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo guardar el archivo",
                    error
            );
        }
    }

    public List<Archivo> listarTodos() {
        return archivoRepository.findAll();
    }

    public Archivo buscarPorId(Long id) {

        return archivoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Archivo no encontrado"
                        )
                );
    }

    public List<Archivo> listarPorUsuario(Long idUsuario) {
        return archivoRepository.findByIdUsuario(idUsuario);
    }

    public List<Archivo> listarPorPago(Long idPago) {
        return archivoRepository.findByIdPago(idPago);
    }

    public List<Archivo> listarPorTipo(String tipo) {

        return archivoRepository.findByTipo(
                normalizarTipo(tipo)
        );
    }

    public Resource descargar(Long id) {

        Archivo archivo = buscarPorId(id);

        if (!Boolean.TRUE.equals(archivo.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Archivo no disponible"
            );
        }

        Path ruta = resolverRuta(archivo.getNombreAlmacenado());

        if (!Files.isRegularFile(ruta) || !Files.isReadable(ruta)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El archivo fisico no existe"
            );
        }

        return new FileSystemResource(ruta);
    }

    public void eliminar(Long id) {

        Archivo archivo = buscarPorId(id);

        archivo.setActivo(false);

        archivoRepository.save(archivo);
    }

    private void validarPago(Long idPago, Long idUsuario) {

        if (idPago == null) {
            return;
        }

        PagosClient.PagoResumen pago;

        try {

            pago = pagosClient.buscarPorId(idPago);

        } catch (FeignException.NotFound error) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El pago asociado no existe"
            );

        } catch (FeignException error) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar pagos-service",
                    error
            );
        }

        if (pago == null
                || pago.idPago() == null
                || pago.idUsuario() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Respuesta invalida de pagos-service"
            );
        }

        if (!pago.idPago().equals(idPago)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "El pago consultado no coincide"
            );
        }

        if (!pago.idUsuario().equals(idUsuario)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El pago no pertenece al usuario indicado"
            );
        }
    }

    private String normalizarTipo(String tipo) {

        if (tipo == null || tipo.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de archivo es obligatorio"
            );
        }

        String resultado = tipo.trim().toUpperCase(Locale.ROOT);

        if (!resultado.equals("AVATAR")
                && !resultado.equals("COMPROBANTE")
                && !resultado.equals("ADJUNTO")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de archivo no permitido"
            );
        }

        return resultado;
    }

    private String obtenerExtension(String nombre) {

        int posicion = nombre.lastIndexOf(".");

        if (posicion < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El archivo debe tener extension"
            );
        }

        return nombre.substring(posicion).toLowerCase(Locale.ROOT);
    }

    private void validarFormato(
            String extension,
            String mime,
            String tipo
    ) {

        boolean jpeg = (extension.equals(".jpg")
                || extension.equals(".jpeg"))
                && "image/jpeg".equals(mime);

        boolean png = extension.equals(".png")
                && "image/png".equals(mime);

        boolean pdf = extension.equals(".pdf")
                && "application/pdf".equals(mime);

        if (!jpeg && !png && !pdf) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se permiten archivos JPG, PNG o PDF"
            );
        }

        if (tipo.equals("AVATAR") && pdf) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El avatar debe ser una imagen"
            );
        }
    }

    private void validarContenido(
            byte[] contenido,
            String extension
    ) {

        boolean jpeg = contenido.length >= 3
                && (contenido[0] & 0xFF) == 0xFF
                && (contenido[1] & 0xFF) == 0xD8
                && (contenido[2] & 0xFF) == 0xFF;

        byte[] firmaPng = {
                (byte) 0x89, 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A
        };

        boolean png = contenido.length >= 8;

        if (png) {
            for (int i = 0; i < firmaPng.length; i++) {
                if (contenido[i] != firmaPng[i]) {
                    png = false;
                    break;
                }
            }
        }

        boolean pdf = contenido.length >= 5
                && contenido[0] == '%'
                && contenido[1] == 'P'
                && contenido[2] == 'D'
                && contenido[3] == 'F'
                && contenido[4] == '-';

        boolean valido = switch (extension) {
            case ".jpg", ".jpeg" -> jpeg;
            case ".png" -> png;
            case ".pdf" -> pdf;
            default -> false;
        };

        if (!valido) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El contenido no coincide con el formato del archivo"
            );
        }
    }

    private Path resolverRuta(String nombre) {

        Path ruta = directorioBase.resolve(nombre).normalize();

        if (!ruta.startsWith(directorioBase)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ruta de archivo invalida"
            );
        }

        return ruta;
    }
}
