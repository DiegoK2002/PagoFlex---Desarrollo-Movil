package cl.fintech.pagoflex.data.remote

import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.DELETE


data class EmpresaRemota(
    val idEmpresa: Long,
    val nombre: String,
    val categoria: String?,
    val activo: Boolean?
)

data class CrearEmpresaRequest(
    val nombre: String,
    val categoria: String
)


interface EmpresasApi {

    @GET("api/v1/empresas")
    suspend fun listarTodas(): List<EmpresaRemota>


    @POST("api/v1/empresas")
    suspend fun crearEmpresa(
        @Body datos: CrearEmpresaRequest
    ): EmpresaRemota


    @PUT("api/v1/empresas/{id}")
    suspend fun actualizarEmpresa(
        @Path("id") idEmpresa: Long,
        @Body datos: CrearEmpresaRequest
    ): EmpresaRemota


    @DELETE("api/v1/empresas/{id}")
    suspend fun anularEmpresa(
        @Path("id") idEmpresa: Long
    )



}
