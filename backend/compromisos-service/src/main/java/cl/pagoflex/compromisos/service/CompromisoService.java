package cl.pagoflex.compromisos.service;

import cl.pagoflex.compromisos.model.Compromiso;
import cl.pagoflex.compromisos.repository.CompromisoRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompromisoService {

    private final CompromisoRepository compromisoRepository;

    public CompromisoService(CompromisoRepository compromisoRepository) {
        this.compromisoRepository = compromisoRepository;
    }

    public Compromiso registrar(Compromiso datos) {

        validarDatos(datos);

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
