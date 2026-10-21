package cl.fintech.pagoflex.model

data class Usuario(
    val id: Long,
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val comuna: String,
    val rol: Rol
) {
    val nombreCompleto: String
        get() = "$nombres $apellidos"
}
