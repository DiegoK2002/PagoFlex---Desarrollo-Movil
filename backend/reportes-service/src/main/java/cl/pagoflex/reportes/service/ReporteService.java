package cl.pagoflex.reportes.service;

import cl.pagoflex.reportes.client.PagosClient;
import cl.pagoflex.reportes.client.UsuariosClient;
import cl.pagoflex.reportes.model.Reporte;
import cl.pagoflex.reportes.repository.ReporteRepository;

import feign.FeignException;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final PagosClient pagosClient;
    private final UsuariosClient usuariosClient;

    public ReporteService(
            ReporteRepository reporteRepository,
            PagosClient pagosClient,
            UsuariosClient usuariosClient
    ) {
        this.reporteRepository = reporteRepository;
        this.pagosClient = pagosClient;
        this.usuariosClient = usuariosClient;
    }

    public Reporte registrar(Reporte datos) {

        validarDatos(datos);
        validarUsuario(datos.getIdUsuario(), true);
        validarPago(datos.getIdPago(), datos.getIdUsuario());

        Reporte reporte = new Reporte();

        reporte.setIdUsuario(datos.getIdUsuario());
        reporte.setIdPago(datos.getIdPago());
        reporte.setTipo(datos.getTipo().trim());
        reporte.setAsunto(datos.getAsunto().trim());
        reporte.setDescripcion(datos.getDescripcion().trim());
        reporte.setEstado("PENDIENTE");
        reporte.setActivo(true);

        return reporteRepository.save(reporte);
    }

    public List<Reporte> listarTodos() {
        return reporteRepository.findAll();
    }

    public Reporte buscarPorId(Long id) {

        return reporteRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Reporte no encontrado"
                        )
                );
    }

    public List<Reporte> listarPorUsuario(Long idUsuario) {
        return reporteRepository.findByIdUsuario(idUsuario);
    }

    public List<Reporte> listarPorAgente(Long idAgente) {
        return reporteRepository.findByIdAgente(idAgente);
    }

    public List<Reporte> listarPorEstado(String estado) {

        return reporteRepository.findByEstado(
                normalizarEstado(estado)
        );
    }

    public List<Reporte> listarPorPago(Long idPago) {
        return reporteRepository.findByIdPago(idPago);
    }

    public Reporte actualizar(Long id, Reporte datos) {

        validarDatos(datos);
        validarUsuario(datos.getIdUsuario(), false);
        validarPago(datos.getIdPago(), datos.getIdUsuario());

        Reporte reporte = buscarPorId(id);

        reporte.setIdUsuario(datos.getIdUsuario());
        reporte.setIdPago(datos.getIdPago());
        reporte.setTipo(datos.getTipo().trim());
        reporte.setAsunto(datos.getAsunto().trim());
        reporte.setDescripcion(datos.getDescripcion().trim());

        return reporteRepository.save(reporte);
    }

    public Reporte actualizarSeguimiento(Long id, Reporte datos) {

        if (datos == null
                || (datos.getEstado() == null
                && datos.getRespuesta() == null
                && datos.getIdAgente() == null)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar datos de seguimiento"
            );
        }

        Reporte reporte = buscarPorId(id);

        if (datos.getEstado() != null) {

            reporte.setEstado(
                    normalizarEstado(datos.getEstado())
            );
        }

        if (datos.getRespuesta() != null) {

            if (datos.getRespuesta().length() > 2000) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La respuesta admite hasta 2000 caracteres"
                );
            }

            reporte.setRespuesta(datos.getRespuesta().trim());
        }

        if (datos.getIdAgente() != null) {

            if (datos.getIdAgente() <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El ID del agente debe ser positivo"
                );
            }

            reporte.setIdAgente(datos.getIdAgente());
        }

        return reporteRepository.save(reporte);
    }

    public void eliminar(Long id) {

        Reporte reporte = buscarPorId(id);

        reporte.setActivo(false);

        reporteRepository.save(reporte);
    }

    private void validarUsuario(Long idUsuario, boolean exigirActivo) {

        UsuariosClient.UsuarioResumen usuario;

        try {

            usuario = usuariosClient.buscarPorId(idUsuario);

        } catch (FeignException.NotFound error) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El usuario asociado no existe"
            );

        } catch (FeignException error) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar usuarios-service",
                    error
            );
        }

        if (usuario == null
                || usuario.idUsuario() == null
                || usuario.activo() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Respuesta invalida de usuarios-service"
            );
        }

        if (!usuario.idUsuario().equals(idUsuario)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "El usuario consultado no coincide"
            );
        }

        if (exigirActivo && !Boolean.TRUE.equals(usuario.activo())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pueden registrar reportes con usuarios inactivos"
            );
        }
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

    private String normalizarEstado(String estado) {

        if (estado == null || estado.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado es obligatorio"
            );
        }

        String estadoNormalizado = estado.trim()
                .toUpperCase(Locale.ROOT);

        if (!estadoNormalizado.equals("PENDIENTE")
                && !estadoNormalizado.equals("EN_PROCESO")
                && !estadoNormalizado.equals("RESUELTO")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado no valido"
            );
        }

        return estadoNormalizado;
    }

    private void validarDatos(Reporte datos) {

        if (datos == null
                || datos.getIdUsuario() == null
                || datos.getIdUsuario() <= 0
                || datos.getTipo() == null
                || datos.getTipo().isBlank()
                || datos.getAsunto() == null
                || datos.getAsunto().isBlank()
                || datos.getDescripcion() == null
                || datos.getDescripcion().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos obligatorios del reporte son invalidos"
            );
        }

        if (datos.getIdPago() != null
                && datos.getIdPago() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El ID del pago debe ser positivo"
            );
        }

        if (datos.getTipo().trim().length() > 50
                || datos.getAsunto().trim().length() > 150
                || datos.getDescripcion().trim().length() > 2000) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Uno o mas campos superan la longitud permitida"
            );
        }
    }
}
