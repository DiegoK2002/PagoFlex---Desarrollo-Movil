package cl.fintech.pagoflex.util

import java.security.MessageDigest

/**
 * Proteccion de contrasenas. La contrasena nunca se guarda en texto plano: en la base de datos
 * queda solo su hash SHA-256. Para iniciar sesion se calcula el hash de lo escrito y se compara.
 * Mejora para produccion: usar un algoritmo lento con sal aleatoria (por ejemplo bcrypt).
 */
object Seguridad {

    /** Calcula el hash (texto hexadecimal) de la contrasena, combinada con el correo para que dos cuentas iguales no repitan hash. */
    fun hashContrasena(correo: String, contrasena: String): String {
        // Texto que se va a transformar: correo normalizado + separador + contrasena
        val entrada = correo.trim().lowercase() + ":" + contrasena
        // SHA-256 devuelve 32 bytes
        val bytes = MessageDigest.getInstance("SHA-256").digest(entrada.toByteArray(Charsets.UTF_8))
        // Cada byte se escribe con dos digitos hexadecimales
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
