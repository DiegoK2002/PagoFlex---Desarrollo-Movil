package cl.fintech.pagoflex.util

import java.security.MessageDigest

object Seguridad {

    fun hashContrasena(correo: String, contrasena: String): String {

        val entrada = correo.trim().lowercase() + ":" + contrasena

        val bytes = MessageDigest.getInstance("SHA-256").digest(entrada.toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { "%02x".format(it) }
    }
}
