package cl.pagoflex.usuarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validarCampos(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> campos = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                campos.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        return ResponseEntity.badRequest().body(
                Map.of(
                        "codigo", "VALIDACION",
                        "mensaje", "Existen campos inválidos",
                        "campos", campos
                )
        );
    }

    @ExceptionHandler(UsuarioDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> usuarioDuplicado(
            UsuarioDuplicadoException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "codigo", "USUARIO_DUPLICADO",
                        "mensaje", ex.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> datosInvalidos(
            IllegalArgumentException ex
    ) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "codigo", "DATOS_INVALIDOS",
                        "mensaje", ex.getMessage()
                ));
    }
}
