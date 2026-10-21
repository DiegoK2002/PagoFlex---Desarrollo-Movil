package cl.fintech.pagoflex.model

data class RegistroUiState(
    val nombres: String = "",
    val apellidos: String = "",
    val rutNumero: String = "",
    val rutDv: String = "",
    val correo: String = "",
    val comuna: String = "",
    val contrasena: String = "",
    val repetirContrasena: String = "",

    val comunas: List<String> = listOf(
        "Santiago", "Providencia", "Las Condes", "\u00d1u\u00f1oa", "Maip\u00fa", "Puente Alto",
        "San Miguel", "La Florida", "Quilicura", "San Bernardo", "Estación Central"
    ),

    val contrasenaVisible: Boolean = false,
    val repetirVisible: Boolean = false,

    val errorNombres: String? = null,
    val errorApellidos: String? = null,
    val errorRut: String? = null,
    val errorCorreo: String? = null,
    val errorComuna: String? = null,
    val errorContrasena: String? = null,
    val errorRepetir: String? = null,

    val registroExitoso: Boolean = false,
    val nombreRegistrado: String = ""
)
