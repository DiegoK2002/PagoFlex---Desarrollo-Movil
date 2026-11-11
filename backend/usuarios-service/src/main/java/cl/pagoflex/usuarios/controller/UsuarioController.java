package cl.pagoflex.usuarios.controller;

import cl.pagoflex.usuarios.dto.LoginRequest;
import cl.pagoflex.usuarios.dto.RegistroUsuarioRequest;
import cl.pagoflex.usuarios.dto.UsuarioResponse;
import cl.pagoflex.usuarios.service.UsuarioService;
import cl.pagoflex.usuarios.dto.ActualizarUsuarioRequest;
import cl.pagoflex.usuarios.dto.CambiarContrasenaRequest;
import cl.pagoflex.usuarios.dto.RecuperarContrasenaRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // REGISTRAR UN USUARIO
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody RegistroUsuarioRequest request
    ) {

        UsuarioResponse usuario = usuarioService.registrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }

    // INICIAR SESIÓN
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> iniciarSesion(
            @Valid @RequestBody LoginRequest request
    ) {

        UsuarioResponse usuario = usuarioService.iniciarSesion(request);

        return ResponseEntity.ok(usuario);
    }

    // BUSCAR USUARIO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id
    ) {

        UsuarioResponse usuario = usuarioService.buscarPorId(id);

        return ResponseEntity.ok(usuario);
    }


    // LISTAR TODOS LOS USUARIOS
    @GetMapping
    public ResponseEntity<java.util.List<UsuarioResponse>> listarTodos() {

        java.util.List<UsuarioResponse> usuarios =
                usuarioService.listarTodos();

        return ResponseEntity.ok(usuarios);
    }


    // ACTUALIZAR LOS DATOS DE UN USUARIO
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequest request
    ) {

        UsuarioResponse usuario =
                usuarioService.actualizarUsuario(id, request);

        return ResponseEntity.ok(usuario);
    }


    // DESACTIVAR UN USUARIO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @PathVariable Long id
    ) {

        usuarioService.eliminarUsuario(id);

        return ResponseEntity.noContent().build();
    }


    // CAMBIAR CONTRASEÑA DE UN USUARIO
    @PutMapping("/{id}/contrasena")
    public ResponseEntity<Void> cambiarContrasena(
            @PathVariable Long id,
            @Valid @RequestBody CambiarContrasenaRequest request
    ) {

        usuarioService.cambiarContrasena(id, request);

        return ResponseEntity.noContent().build();
    }


    // RECUPERAR CONTRASEÑA
    @PutMapping("/recuperar-contrasena")
    public ResponseEntity<Void> recuperarContrasena(
            @Valid @RequestBody RecuperarContrasenaRequest request
    ) {

        usuarioService.recuperarContrasena(request);

        return ResponseEntity.noContent().build();
    }





}
