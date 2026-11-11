package cl.pagoflex.usuarios.config;

import cl.pagoflex.usuarios.model.Rol;
import cl.pagoflex.usuarios.repository.RolRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatosInicialesConfig {

    @Bean
    public CommandLineRunner inicializarRoles(
            RolRepository rolRepository
    ) {
        return args -> {

            crearRolSiNoExiste(
                    rolRepository,
                    1L,
                    "USUARIO_FINAL"
            );

            crearRolSiNoExiste(
                    rolRepository,
                    2L,
                    "AGENTE"
            );

            crearRolSiNoExiste(
                    rolRepository,
                    3L,
                    "ADMINISTRADOR"
            );

        };
    }

    private void crearRolSiNoExiste(
            RolRepository repository,
            Long id,
            String nombre
    ) {

        if (repository.existsById(id)) {
            return;
        }

        if (repository.findByNombreRol(nombre).isPresent()) {
            return;
        }

        Rol rol = new Rol(id, nombre);
        repository.save(rol);
    }
}
