package cl.fintech.pagoflex.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class ReporteRemoto(
    val idReporte: Long,
    val idUsuario: Long,
    val idPago: Long?,
    val tipo: String,
    val asunto: String,
    val descripcion: String,
    val estado: String,
    val respuesta: String?,
    val idAgente: Long?,
    val fechaCreacion: String,
    val activo: Boolean
)

data class RegistrarReporteRequest(
    val idUsuario: Long,
    val idPago: Long? = null,
    val tipo: String,
    val asunto: String,
    val descripcion: String
)

data class SeguimientoReporteRequest(
    val estado: String? = null,
    val respuesta: String? = null,
    val idAgente: Long? = null
)

interface ReportesApi {

    @GET("api/v1/reportes/usuario/{idUsuario}")
    suspend fun listarPorUsuario(
        @Path("idUsuario") idUsuario: Long
    ): List<ReporteRemoto>

    @GET("api/v1/reportes")
    suspend fun listarTodos(): List<ReporteRemoto>

    @POST("api/v1/reportes")
    suspend fun registrar(
        @Body datos: RegistrarReporteRequest
    ): ReporteRemoto

    @PUT("api/v1/reportes/{idReporte}/seguimiento")
    suspend fun actualizarSeguimiento(
        @Path("idReporte") idReporte: Long,
        @Body datos: SeguimientoReporteRequest
    ): ReporteRemoto
}
