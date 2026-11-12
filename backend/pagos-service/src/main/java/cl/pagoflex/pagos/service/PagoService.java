package cl.pagoflex.pagos.service;

import cl.pagoflex.pagos.model.Pago;
import cl.pagoflex.pagos.repository.PagoRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public Pago registrar(Pago datos) {

        validarDatos(datos);

        Pago pago = new Pago();

        pago.setIdUsuario(datos.getIdUsuario());
        pago.setIdCompromiso(datos.getIdCompromiso());
        pago.setMonto(datos.getMonto());
        pago.setFechaPago(datos.getFechaPago());
        pago.setMetodoPago(datos.getMetodoPago().trim());
        pago.setReferenciaComprobante(
                limpiarReferencia(datos.getReferenciaComprobante())
        );
        pago.setActivo(true);

        return pagoRepository.save(pago);
    }

    public List<Pago> listarTodos() {

        return pagoRepository.findAll();
    }

    public Pago buscarPorId(Long id) {

        return pagoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pago no encontrado"
                        )
                );
    }

    public List<Pago> listarPorUsuario(Long idUsuario) {

        return pagoRepository.findByIdUsuario(idUsuario);
    }

    public List<Pago> listarPorCompromiso(Long idCompromiso) {

        return pagoRepository.findByIdCompromiso(idCompromiso);
    }

    public Pago actualizar(Long id, Pago datos) {

        validarDatos(datos);

        Pago pago = buscarPorId(id);

        pago.setIdUsuario(datos.getIdUsuario());
        pago.setIdCompromiso(datos.getIdCompromiso());
        pago.setMonto(datos.getMonto());
        pago.setFechaPago(datos.getFechaPago());
        pago.setMetodoPago(datos.getMetodoPago().trim());
        pago.setReferenciaComprobante(
                limpiarReferencia(datos.getReferenciaComprobante())
        );

        return pagoRepository.save(pago);
    }

    public void eliminar(Long id) {

        Pago pago = buscarPorId(id);

        pago.setActivo(false);

        pagoRepository.save(pago);
    }

    private String limpiarReferencia(String referencia) {

        if (referencia == null || referencia.isBlank()) {
            return null;
        }

        return referencia.trim();
    }

    private void validarDatos(Pago datos) {

        if (datos == null
                || datos.getIdUsuario() == null
                || datos.getIdCompromiso() == null
                || datos.getIdUsuario() <= 0
                || datos.getIdCompromiso() <= 0
                || datos.getMonto() == null
                || datos.getFechaPago() == null
                || datos.getMetodoPago() == null
                || datos.getMetodoPago().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos obligatorios del pago son invalidos"
            );
        }

        if (datos.getMonto().compareTo(BigDecimal.ZERO) <= 0
                || datos.getMonto().precision() > 15
                || datos.getMonto().scale() > 2) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El monto debe ser positivo y tener hasta 2 decimales"
            );
        }

        if (datos.getMetodoPago().trim().length() > 50) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El metodo de pago admite hasta 50 caracteres"
            );
        }

        if (datos.getReferenciaComprobante() != null
                && datos.getReferenciaComprobante().trim().length() > 255) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La referencia admite hasta 255 caracteres"
            );
        }
    }
}
