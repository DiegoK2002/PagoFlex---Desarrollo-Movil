package cl.pagoflex.compromisos.service;

import cl.pagoflex.compromisos.client.EmpresasClient;
import cl.pagoflex.compromisos.model.Compromiso;
import cl.pagoflex.compromisos.repository.CompromisoRepository;

import feign.FeignException;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompromisoService {

    private final CompromisoRepository compromisoRepository;
    private final EmpresasClient empresasClient;

    public CompromisoService(
            CompromisoRepository compromisoRepository,
            EmpresasClient empresasClient
    ) {
        this.compromisoRepository = compromisoRepository;
        this.empresasClient = empresasClient;
    }

    public Compromiso registrar(Compromiso datos) {

        validarDatos(datos);
        validarEmpresa(datos.getIdEmpresa());

        Compromiso compromiso = new Compromiso();

        compromiso.setIdUsuario(datos.getIdUsuario());
        compromiso.setIdEmpresa(datos.getIdEmpresa());
        compromiso.setNombre(datos.getNombre().trim());
        compromiso.setMonto(datos.getMonto());
        compromiso.setFechaVencimiento(datos.getFechaVencimiento());
        compromiso.setEstado("PENDIENTE");
        compromiso.setActivo(true);

        return compromisoRepository.save(compromiso);
    }

    public List<Compromiso> listarTodos() {
        return compromisoRepository.findAll();
    }

    public Compromiso buscarPorId(Long id) {

        return compromisoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Compromiso no encontrado"
                        )
                );
    }

    public List<Compromiso> listarPorUsuario(Long idUsuario) {
        return compromisoRepository.findByIdUsuario(idUsuario);
    }

    public Compromiso actualizar(Long id, Compromiso datos) {

        validarDatos(datos);
        validarEmpresa(datos.getIdEmpresa());

        Compromiso compromiso = buscarPorId(id);

        compromiso.setIdUsuario(datos.getIdUsuario());
        compromiso.setIdEmpresa(datos.getIdEmpresa());
        compromiso.setNombre(datos.getNombre().trim());
        compromiso.setMonto(datos.getMonto());
        compromiso.setFechaVencimiento(datos.getFechaVencimiento());

        return compromisoRepository.save(compromiso);
    }

    public void eliminar(Long id) {

        Compromiso compromiso = buscarPorId(id);

        compromiso.setActivo(false);

        compromisoRepository.save(compromiso);
    }

    private void validarEmpresa(Long idEmpresa) {

        EmpresasClient.EmpresaResumen empresa;

        try {

            empresa = empresasClient.buscarPorId(idEmpresa);

        } catch (FeignException.NotFound error) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La empresa asociada no existe"
            );

        } catch (FeignException error) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar empresas-service",
                    error
            );
        }

        if (empresa == null || empresa.idEmpresa() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Respuesta invalida de empresas-service"
            );
        }

        if (!empresa.idEmpresa().equals(idEmpresa)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "La empresa consultada no coincide"
            );
        }

        if (!Boolean.TRUE.equals(empresa.activo())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se pueden registrar compromisos con empresas inactivas"
            );
        }
    }

    private void validarDatos(Compromiso datos) {

        if (datos == null
                || datos.getIdUsuario() == null
                || datos.getIdEmpresa() == null
                || datos.getIdUsuario() <= 0
                || datos.getIdEmpresa() <= 0
                || datos.getNombre() == null
                || datos.getNombre().isBlank()
                || datos.getFechaVencimiento() == null
                || datos.getMonto() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Todos los campos del compromiso son obligatorios"
            );
        }

        if (datos.getNombre().trim().length() > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre admite hasta 100 caracteres"
            );
        }

        if (datos.getMonto().compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El monto debe ser mayor que cero"
            );
        }
    }
}
