package cl.fintech.pagoflex.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

data class ArchivoRemoto(
    val idArchivo: Long,
    val idUsuario: Long,
    val idPago: Long?,
    val tipo: String,
    val nombreOriginal: String,
    val tipoMime: String,
    val tamanoBytes: Long,
    val fechaSubida: String?,
    val activo: Boolean
)

interface ArchivosApi {

    @GET("api/v1/archivos/usuario/{idUsuario}")
    suspend fun listarPorUsuario(
        @Path("idUsuario") idUsuario: Long
    ): List<ArchivoRemoto>

    @GET("api/v1/archivos/{idArchivo}/descargar")
    suspend fun descargar(
        @Path("idArchivo") idArchivo: Long
    ): ResponseBody

    @Multipart
    @POST("api/v1/archivos/subir")
    suspend fun subir(
        @Part("idUsuario") idUsuario: RequestBody,
        @Part("tipo") tipo: RequestBody,
        @Part archivo: MultipartBody.Part
    ): ArchivoRemoto

    @DELETE("api/v1/archivos/{idArchivo}")
    suspend fun eliminar(
        @Path("idArchivo") idArchivo: Long
    )
}
