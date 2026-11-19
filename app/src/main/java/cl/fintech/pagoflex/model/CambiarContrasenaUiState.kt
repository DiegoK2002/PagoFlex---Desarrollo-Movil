package cl.fintech.pagoflex.model

data class CambiarContrasenaUiState(
    val contrasenaActual: String = "",
    val contrasenaNueva: String = "",
    val repetirContrasena: String = "",

    val actualVisible: Boolean = false,
    val nuevaVisible: Boolean = false,
    val repetirVisible: Boolean = false,

    val errorActual: String? = null,
    val errorNueva: String? = null,
    val errorRepetir: String? = null,
    val errorGeneral: String? = null,

    val guardando: Boolean = false,
    val cambiada: Boolean = false
)
