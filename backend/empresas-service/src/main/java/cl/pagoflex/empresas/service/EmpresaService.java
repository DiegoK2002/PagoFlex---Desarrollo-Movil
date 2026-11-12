package cl.pagoflex.empresas.service;

import cl.pagoflex.empresas.model.Empresa;
import cl.pagoflex.empresas.repository.EmpresaRepository;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public Empresa registrar(Empresa datos) {

        validarDatos(datos);

        Empresa empresa = new Empresa();

        empresa.setNombre(datos.getNombre().trim());
        empresa.setCategoria(datos.getCategoria().trim());
        empresa.setActivo(true);

        return empresaRepository.save(empresa);
    }

    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    public Empresa buscarPorId(Long id) {

        return empresaRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Empresa no encontrada"
                        )
                );
    }

    public Empresa actualizar(Long id, Empresa datos) {

        validarDatos(datos);

        Empresa empresa = buscarPorId(id);

        empresa.setNombre(datos.getNombre().trim());
        empresa.setCategoria(datos.getCategoria().trim());

        return empresaRepository.save(empresa);
    }

    public void eliminar(Long id) {

        Empresa empresa = buscarPorId(id);

        empresa.setActivo(false);

        empresaRepository.save(empresa);
    }

    private void validarDatos(Empresa datos) {

        if (datos == null
                || datos.getNombre() == null
                || datos.getNombre().isBlank()
                || datos.getCategoria() == null
                || datos.getCategoria().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre y la categoria son obligatorios"
            );
        }

        if (datos.getNombre().trim().length() > 100
                || datos.getCategoria().trim().length() > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre y la categoria admiten hasta 100 caracteres"
            );
        }
    }
}
