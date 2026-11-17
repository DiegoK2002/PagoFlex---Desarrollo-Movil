package cl.fintech.pagoflex.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.math.BigDecimal

data class PagoRemoto(
    val idPago: Long,
    val idUsuario: Long,
    val idCompromiso: Long,
    val monto: BigDecimal,
    val fechaPago: String,
    val metodoPago: String,
    val referenciaComprobante: String?,
    val activo: Boolean
)

data class RegistrarPagoRequest(
    val idUsuario: Long,
    val idCompromiso: Long,
    val monto: BigDecimal,
    val fechaPago: String,
    val metodoPago: String,
    val referenciaComprobante: String? = null
)

interface PagosApi {

    @GET("api/v1/pagos/usuario/{idUsuario}")
    suspend fun listarPorUsuario(
        @Path("idUsuario") idUsuario: Long
    ): List<PagoRemoto>

    @GET("api/v1/pagos/compromiso/{idCompromiso}")
    suspend fun listarPorCompromiso(
        @Path("idCompromiso") idCompromiso: Long
    ): List<PagoRemoto>

    @POST("api/v1/pagos")
    suspend fun registrarPago(
        @Body datos: RegistrarPagoRequest
    ): PagoRemoto
}
