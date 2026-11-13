package cl.pagoflex.reportes.service;

import cl.pagoflex.reportes.model.Reporte;
import cl.pagoflex.reportes.repository.ReporteRepository;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteService(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    public Reporte registrar(Reporte datos) {

        validarDatos(datos);

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
