package cl.fintech.pagoflex.model

data class EditarPerfilUiState(
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val comuna: String = "",

    val comunas: List<String> = listOf(
        "Santiago", "Providencia", "Las Condes", "\u00d1u\u00f1oa", "Maip\u00fa", "Puente Alto",
        "San Miguel", "La Florida", "Quilicura", "San Bernardo", "Estaci\u00f3n Central"
    ),

    val errorNombres: String? = null,
    val errorApellidos: String? = null,
    val errorCorreo: String? = null,
    val errorComuna: String? = null,
    val errorGeneral: String? = null,

    val guardando: Boolean = false,
    val guardado: Boolean = false
)
