package cl.fintech.pagoflex.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import java.math.BigDecimal

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

interface CompromisosApi {

    @GET("api/v1/compromisos/usuario/{idUsuario}")
    suspend fun listarPorUsuario(
        @Path("idUsuario") idUsuario: Long
    ): List<CompromisoRemoto>
}
