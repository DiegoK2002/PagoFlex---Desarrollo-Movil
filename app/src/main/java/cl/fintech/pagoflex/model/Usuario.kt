package cl.fintech.pagoflex.model

// =====================================================================
//  Usuario.kt
//  ---------------------------------------------------------------------
//  Una sola clase para los 3 roles (como la tabla usuario del MER).
//  Los campos propios de cada rol quedan en null para los demás:
//     usuario final -> comuna, diasAnticipacion (null = avisos desactivados)
//     agente        -> area
//     ejecutivo     -> codigoEmpresa, cargo
// =====================================================================

data class Usuario(
    val codigo: String,
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val rol: Rol,
    val comuna: String? = null,
    val diasAnticipacion: Int? = null,
    val area: String? = null,
    val codigoEmpresa: String? = null,
    val cargo: String? = null,
    val activo: Boolean = true
) {
    val nombreCompleto: String
        get() = "$nombres $apellidos"
}

val usuarioEjemplo = Usuario(
    codigo = "USR001",
    rut = "12345678-5",
    nombres = "Diego Alejandro",
    apellidos = "Muñoz Torres",
    correo = "diego.munoz@ejemplo.cl",
    rol = Rol.USUARIO_FINAL,
    comuna = "Santiago",
    diasAnticipacion = 3,
    area = "Tecnología",
    codigoEmpresa = "EMP001",
    cargo = "Desarrollador",
    activo = true
)