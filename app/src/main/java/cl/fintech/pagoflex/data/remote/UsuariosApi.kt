package cl.fintech.pagoflex.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

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

interface UsuariosApi {

    @POST("api/v1/usuarios/login")
    suspend fun iniciarSesion(
        @Body datos: LoginRequest
    ): UsuarioResponse

    @POST("api/v1/usuarios/registro")
    suspend fun registrarUsuario(
        @Body datos: RegistroUsuarioRequest
    ): UsuarioResponse
}
