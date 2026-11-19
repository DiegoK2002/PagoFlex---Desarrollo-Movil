package cl.fintech.pagoflex.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.DELETE

data class LoginRequest(
    val correo: String,
    val contrasena: String
)

data class RegistroUsuarioRequest(
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val contrasena: String,
    val comuna: String
)

data class UsuarioResponse(
    val idUsuario: Long,
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val comuna: String,
    val rol: String,
    val fotoArchivoId: String?,
    val activo: Boolean
)

// Datos editables del perfil; el RUT y el rol no se pueden modificar
data class ActualizarUsuarioRequest(
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val comuna: String
)

data class CambiarContrasenaRequest(
    val contrasenaActual: String,
    val contrasenaNueva: String
)

// La identidad se verifica con el correo y el RUT del titular
data class RecuperarContrasenaRequest(
    val correo: String,
    val rut: String,
    val contrasenaNueva: String
)

interface UsuariosApi {
    @GET("api/v1/usuarios")
    suspend fun listarUsuarios(): List<UsuarioResponse>

    @POST("api/v1/usuarios/login")
    suspend fun iniciarSesion(
        @Body datos: LoginRequest
    ): UsuarioResponse

    @POST("api/v1/usuarios/registro")
    suspend fun registrarUsuario(
        @Body datos: RegistroUsuarioRequest
    ): UsuarioResponse

    @DELETE("api/v1/usuarios/{id}")
    suspend fun desactivarUsuario(
        @Path("id") idUsuario: Long
    )

    @PUT("api/v1/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") idUsuario: Long,
        @Body datos: ActualizarUsuarioRequest
    ): UsuarioResponse

    @PUT("api/v1/usuarios/{id}/contrasena")
    suspend fun cambiarContrasena(
        @Path("id") idUsuario: Long,
        @Body datos: CambiarContrasenaRequest
    )

    @PUT("api/v1/usuarios/recuperar-contrasena")
    suspend fun recuperarContrasena(
        @Body datos: RecuperarContrasenaRequest
    )
}
