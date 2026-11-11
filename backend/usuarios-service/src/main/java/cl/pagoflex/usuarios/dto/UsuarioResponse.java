package cl.pagoflex.usuarios.dto;

public record UsuarioResponse(

        Long idUsuario,
        String rut,
        String nombres,
        String apellidos,
        String correo,
        String comuna,
        String rol,
        String fotoArchivoId,
        Boolean activo

) {
}
