package cl.fintech.pagoflex.model

data class RecuperarContrasenaUiState(
    val correo: String = "",
    val rutNumero: String = "",
    val rutDv: String = "",
    val contrasenaNueva: String = "",
    val repetirContrasena: String = "",

    val contrasenaVisible: Boolean = false,
    val repetirVisible: Boolean = false,

    val errorCorreo: String? = null,
    val errorRut: String? = null,
    val errorContrasena: String? = null,
    val errorRepetir: String? = null,
    val errorGeneral: String? = null,

    val enviando: Boolean = false,
    val recuperada: Boolean = false
)
