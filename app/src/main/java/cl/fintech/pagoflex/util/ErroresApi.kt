package cl.fintech.pagoflex.util

import com.google.gson.JsonParser
import retrofit2.HttpException

object ErroresApi {

    fun mensajeServidor(error: HttpException): String {
        return try {
            val cuerpo = error.response()?.errorBody()?.string().orEmpty()
            JsonParser.parseString(cuerpo)
                .asJsonObject
                .get("mensaje")
                ?.asString
                .orEmpty()
        } catch (excepcion: Exception) {
            ""
        }
    }
}
