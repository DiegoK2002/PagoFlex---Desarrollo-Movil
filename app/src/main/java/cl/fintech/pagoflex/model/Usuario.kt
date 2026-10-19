package cl.fintech.pagoflex.model

/**
 * Cuenta de una persona registrada en la app (una fila de la tabla "usuarios").
 *
 * La contrasena no forma parte de esta clase: en la base de datos solo se guarda su hash
 * y nunca viaja hacia las pantallas.
 */
data class Usuario(
    val id: Long,
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val comuna: String,
    val rol: Rol
) {
    /** Nombres y apellidos juntos, para mostrar en pantalla. */
    val nombreCompleto: String
        get() = "$nombres $apellidos"
}
