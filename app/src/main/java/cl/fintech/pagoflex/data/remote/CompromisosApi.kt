package cl.fintech.pagoflex.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.math.BigDecimal
import retrofit2.http.PUT
import retrofit2.http.DELETE

data class CompromisoRemoto(
    val idCompromiso: Long,
    val idUsuario: Long,
    val idEmpresa: Long,
    val nombre: String,
    val monto: BigDecimal,
    val fechaVencimiento: String,
    val estado: String,
    val activo: Boolean
)

data class CrearCompromisoRequest(
    val idUsuario: Long,
    val idEmpresa: Long,
    val nombre: String,
    val monto: BigDecimal,
    val fechaVencimiento: String
)

interface CompromisosApi {

    @DELETE("api/v1/compromisos/{id}")
    suspend fun anularCompromiso(
        @Path("id") idCompromiso: Long
    )

    @PUT("api/v1/compromisos/{id}")
    suspend fun actualizarCompromiso(
        @Path("id") idCompromiso: Long,
        @Body datos: CrearCompromisoRequest
    ): CompromisoRemoto

    @GET("api/v1/compromisos")
    suspend fun listarTodos(): List<CompromisoRemoto>


    @GET("api/v1/compromisos/usuario/{idUsuario}")
    suspend fun listarPorUsuario(
        @Path("idUsuario") idUsuario: Long
    ): List<CompromisoRemoto>

    @POST("api/v1/compromisos")
    suspend fun crearCompromiso(
        @Body datos: CrearCompromisoRequest
    ): CompromisoRemoto
}
