package cl.pagoflex.usuarios.service;

import cl.pagoflex.usuarios.dto.LoginRequest;
import cl.pagoflex.usuarios.dto.RegistroUsuarioRequest;
import cl.pagoflex.usuarios.dto.UsuarioResponse;
import cl.pagoflex.usuarios.model.Rol;
import cl.pagoflex.usuarios.model.Usuario;
import cl.pagoflex.usuarios.repository.RolRepository;
import cl.pagoflex.usuarios.repository.UsuarioRepository;
import cl.pagoflex.usuarios.util.RutValidator;
import cl.pagoflex.usuarios.exception.UsuarioDuplicadoException;
import cl.pagoflex.usuarios.dto.ActualizarUsuarioRequest;
import cl.pagoflex.usuarios.dto.CambiarContrasenaRequest;
import cl.pagoflex.usuarios.dto.RecuperarContrasenaRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse registrar(
            RegistroUsuarioRequest request
    ) {

        String rut = RutValidator.normalizarYValidar(
                request.rut()
        );

        String correo = request.correo()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByRut(rut)) {
            throw new UsuarioDuplicadoException(
                    "El RUT ya está registrado"
            );
        }

        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new UsuarioDuplicadoException(
                    "El correo ya está registrado"
            );
        }

        String contrasena = request.contrasena();

        if (contrasena.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                    "La contraseña supera el límite permitido"
            );
        }

        Rol rol = rolRepository
                .findByNombreRol("USUARIO_FINAL")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "El rol de usuario no existe"
                        )
                );

        Usuario usuario = new Usuario();

        usuario.setRut(rut);
        usuario.setNombres(request.nombres().trim());
        usuario.setApellidos(request.apellidos().trim());
        usuario.setCorreo(correo);
        usuario.setComuna(request.comuna().trim());

        usuario.setContrasenaHash(
                passwordEncoder.encode(contrasena)
        );

        usuario.setRol(rol);
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);

        return convertirRespuesta(guardado);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse iniciarSesion(
            LoginRequest request
    ) {

        String correo = request.correo()
                .trim()
                .toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCase(correo)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Correo o contraseña incorrectos"
                        )
                );

        boolean contrasenaCorrecta = passwordEncoder.matches(
                request.contrasena(),
                usuario.getContrasenaHash()
        );

        if (!contrasenaCorrecta) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo o contraseña incorrectos"
            );
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La cuenta está desactivada"
            );
        }

        return convertirRespuesta(usuario);

        
    }


        @Transactional(readOnly = true)
        public UsuarioResponse buscarPorId(Long id) {

        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario no encontrado"
                        )
                );

        return convertirRespuesta(usuario);
        }


        @Transactional(readOnly = true)
        public java.util.List<UsuarioResponse> listarTodos() {

        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirRespuesta)
                .toList();
        }


@Transactional
public UsuarioResponse actualizarUsuario(
        Long id,
        ActualizarUsuarioRequest request
) {

    // Buscar al usuario por su ID
    Usuario usuario = usuarioRepository
            .findById(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Usuario no encontrado"
                    )
            );

    // Normalizar el correo
    String correo = request.correo()
            .trim()
            .toLowerCase(Locale.ROOT);

    // Comprobar que el correo no pertenezca a otro usuario
    usuarioRepository.findByCorreoIgnoreCase(correo)
            .ifPresent(otroUsuario -> {
                if (!otroUsuario.getIdUsuario()
                        .equals(usuario.getIdUsuario())) {
                    throw new UsuarioDuplicadoException(
                            "El correo ya está registrado"
                    );
                }
            });

    // Actualizar los datos
    usuario.setNombres(request.nombres().trim());
    usuario.setApellidos(request.apellidos().trim());
    usuario.setCorreo(correo);
    usuario.setComuna(request.comuna().trim());

    // Guardar en MariaDB
    Usuario actualizado = usuarioRepository.save(usuario);

    return convertirRespuesta(actualizado);
}


@Transactional
public void eliminarUsuario(Long id) {

    // Buscar el usuario
    Usuario usuario = usuarioRepository
            .findById(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Usuario no encontrado"
                    )
            );

    // Desactivar la cuenta
    usuario.setActivo(false);

    // Guardar el cambio en MariaDB
    usuarioRepository.save(usuario);
}


@Transactional
public void cambiarContrasena(
        Long id,
        CambiarContrasenaRequest request
) {

    // Buscar al usuario
    Usuario usuario = usuarioRepository
            .findById(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Usuario no encontrado"
                    )
            );

    // Comprobar que la cuenta esté activa
    if (!Boolean.TRUE.equals(usuario.getActivo())) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "La cuenta está desactivada"
        );
    }

    // Comprobar la contraseña actual
    boolean correcta = passwordEncoder.matches(
            request.contrasenaActual(),
            usuario.getContrasenaHash()
    );

    if (!correcta) {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "La contraseña actual es incorrecta"
        );
    }

    // Comprobar el límite de BCrypt
    if (request.contrasenaNueva()
            .getBytes(StandardCharsets.UTF_8).length > 72) {

        throw new IllegalArgumentException(
                "La contraseña nueva es demasiado larga"
        );
    }

    // Cifrar la nueva contraseña con BCrypt
    String nuevoHash = passwordEncoder.encode(
            request.contrasenaNueva()
    );

    // Guardar el nuevo hash
    usuario.setContrasenaHash(nuevoHash);
    usuarioRepository.save(usuario);
}


@Transactional
public void recuperarContrasena(
        RecuperarContrasenaRequest request
) {

    // Buscar al usuario por su correo
    String correo = request.correo()
            .trim()
            .toLowerCase(Locale.ROOT);

    Usuario usuario = usuarioRepository
            .findByCorreoIgnoreCase(correo)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Correo no registrado"
                    )
            );

    // Guardar la contraseña nueva con BCrypt
    String contrasenaNueva = request.contrasenaNueva();

    if (contrasenaNueva.getBytes(StandardCharsets.UTF_8).length > 72) {
        throw new IllegalArgumentException(
                "La contraseña nueva es demasiado larga"
        );
    }

    usuario.setContrasenaHash(
            passwordEncoder.encode(contrasenaNueva)
    );

    usuarioRepository.save(usuario);
}







    private UsuarioResponse convertirRespuesta(
            Usuario usuario
    ) {

        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getRut(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getComuna(),
                usuario.getRol().getNombreRol(),
                usuario.getFotoArchivoId(),
                usuario.getActivo()
        );
    }
}
