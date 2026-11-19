package cl.fintech.pagoflex.util

import com.google.gson.JsonParser
import retrofit2.HttpException

// Utilidades para interpretar las respuestas de error del backend
object ErroresApi {

    // Extrae el campo "mensaje" del cuerpo JSON del error; devuelve vacio si no existe
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
