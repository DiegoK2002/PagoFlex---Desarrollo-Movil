package cl.fintech.pagoflex.data.remote

import retrofit2.http.GET

data class EmpresaRemota(
    val idEmpresa: Long,
    val nombre: String,
    val categoria: String?,
    val activo: Boolean?
)

interface EmpresasApi {

    @GET("api/v1/empresas")
    suspend fun listarTodas(): List<EmpresaRemota>
}
