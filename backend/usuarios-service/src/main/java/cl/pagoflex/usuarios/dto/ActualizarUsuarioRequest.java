package cl.pagoflex.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarUsuarioRequest(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    String nombres,

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    String apellidos,

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    @Size(max = 190)
    String correo,

    @NotBlank(message = "La comuna es obligatoria")
    @Size(max = 120)
    String comuna

) {
}
