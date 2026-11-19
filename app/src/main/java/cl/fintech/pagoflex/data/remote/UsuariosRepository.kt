package cl.fintech.pagoflex.data.remote

import cl.fintech.pagoflex.model.Usuario

// Acceso a las operaciones de cuenta del servicio de usuarios.
// Recibe la API por constructor para poder reemplazarla en las pruebas.
class UsuariosRepository(
    private val api: UsuariosApi = ApiClient.usuariosApi
) {

    // Actualiza los datos personales y devuelve el usuario con los valores guardados
    suspend fun actualizarPerfil(
        actual: Usuario,
        nombres: String,
        apellidos: String,
        correo: String,
        comuna: String
    ): Usuario {
        require(actual.id > 0L)

        val respuesta = api.actualizarUsuario(
            actual.id,
            ActualizarUsuarioRequest(
                nombres = nombres.trim(),
                apellidos = apellidos.trim(),
                correo = correo.trim(),
                comuna = comuna.trim()
            )
        )

        // El RUT y el rol se conservan: el servidor no los modifica
        return actual.copy(
            nombres = respuesta.nombres,
            apellidos = respuesta.apellidos,
            correo = respuesta.correo,
            comuna = respuesta.comuna
        )
    }

    suspend fun cambiarContrasena(
        idUsuario: Long,
        contrasenaActual: String,
        contrasenaNueva: String
    ) {
        require(idUsuario > 0L)

        api.cambiarContrasena(
            idUsuario,
            CambiarContrasenaRequest(
                contrasenaActual = contrasenaActual,
                contrasenaNueva = contrasenaNueva
            )
        )
    }

    suspend fun recuperarContrasena(
        correo: String,
        rut: String,
        contrasenaNueva: String
    ) {
        api.recuperarContrasena(
            RecuperarContrasenaRequest(
                correo = correo.trim(),
                rut = rut.trim(),
                contrasenaNueva = contrasenaNueva
            )
        )
    }
}
