package cl.pagoflex.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarContrasenaRequest(

    @NotBlank(message = "La contraseña actual es obligatoria")
    String contrasenaActual,

    @NotBlank(message = "La contraseña nueva es obligatoria")
    @Size(min = 8, max = 72)
    String contrasenaNueva

) {
}
