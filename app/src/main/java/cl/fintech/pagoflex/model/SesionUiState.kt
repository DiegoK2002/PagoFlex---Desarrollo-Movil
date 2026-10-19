package cl.fintech.pagoflex.model

/** Estado de la pantalla de inicio de sesion: lo escrito por la persona y los mensajes de error. */
data class SesionUiState(
    val correo: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorGeneral: String? = null
)
