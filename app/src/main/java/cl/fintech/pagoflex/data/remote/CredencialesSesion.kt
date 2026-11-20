package cl.fintech.pagoflex.data.remote

import java.util.Base64

object CredencialesSesion {

    @Volatile
    private var autorizacion: String? = null

    fun establecer(correo: String, contrasena: String) {
        require(correo.isNotBlank() && contrasena.isNotEmpty())

        val datos = "${correo.trim()}:$contrasena"

        val codificado = Base64.getEncoder()
            .encodeToString(datos.toByteArray(Charsets.UTF_8))

        autorizacion = "Basic $codificado"
    }

    fun obtener(): String? = autorizacion

    fun limpiar() {
        autorizacion = null
    }
}