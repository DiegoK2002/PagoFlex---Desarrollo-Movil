package cl.pagoflex.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecuperarContrasenaRequest(

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    String correo,

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, max = 72)
    String contrasenaNueva

) {
}
