package cl.fintech.pagoflex.data.remote

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Url

interface CotizacionesApi {

    @GET
    suspend fun obtenerCotizaciones(
        @Url direccion: String
    ): JsonObject
}
