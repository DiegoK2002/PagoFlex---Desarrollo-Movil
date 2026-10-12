
package cl.fintech.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.model.SesionUiState
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.ui.data.DatosEjemplo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InicioSesionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SesionUiState())
    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    // Contraseñas de prueba para la demostración académica.
    // Todas las cuentas utilizan Demo123.

    private val contrasenasDemo = mapOf(
        "crojas@correo.ejemplo" to "Demo123",
        "lmunoz@correo.ejemplo" to "Demo123",
        "rcontreras@correo.ejemplo" to "Demo123",
        "msoto@correo.ejemplo" to "Demo123",
        "fgodoy@correo.ejemplo" to "Demo123",
        "hpizarro@correo.ejemplo" to "Demo123",
        "jreyes@correo.ejemplo" to "Demo123",
        "baraya@correo.ejemplo" to "Demo123",
        "alagos@pagoflex.ejemplo" to "Demo123",
        "dortiz@pagoflex.ejemplo" to "Demo123",
        "rsalinas@pagoflex.ejemplo" to "Demo123",
        "vparedes@pagoflex.ejemplo" to "Demo123",
        "tvidal@pagoflex.ejemplo" to "Demo123",
        "psaavedra@altosmaipo.ejemplo" to "Demo123",
        "mfuentes@bosquenativo.ejemplo" to "Demo123",
        "chenriquez@creditoandino.ejemplo" to "Demo123",
        "fmorales@creditoandino.ejemplo" to "Demo123",
        "gcarrasco@loshalcones.ejemplo" to "Demo123",
        "snunez@itcordillera.ejemplo" to "Demo123",
        "mleiva@plazaoriente.ejemplo" to "Demo123"
    )

    // Eventos de los campos.

    fun onCorreoCambia(valor: String) {
        _uiState.update {
            it.copy(
                correo = valor.trim(),
                errorCorreo = null,
                errorContrasena = null,
                inicioExitoso = false
            )
        }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(
                contrasena = valor,
                errorContrasena = null,
                inicioExitoso = false
            )
        }
    }

    fun onAlternarVisibilidad() {
        _uiState.update {
            it.copy(
                contrasenaVisible = !it.contrasenaVisible
            )
        }
    }

    // Evento del selector de rol.

    fun onRolSeleccionado(rol: Rol) {
        _uiState.update {
            it.copy(
                rol = rol,
                errorCorreo = null,
                errorContrasena = null,
                inicioExitoso = false
            )
        }
    }

    // Valida los campos y comprueba las credenciales.

    fun onIniciar(): Usuario? {
        val estado = _uiState.value

        val correo = estado.correo.trim()

        val errorCorreo = when {
            correo.isBlank() ->
                "Ingresa tu correo"

            !correo.contains("@") ||
                    !correo.substringAfter("@").contains(".") ->
                "Ingresa un correo válido"

            else -> null
        }

        if (errorCorreo != null) {
            _uiState.update {
                it.copy(
                    errorCorreo = errorCorreo,
                    errorContrasena = null,
                    inicioExitoso = false
                )
            }
            return null
        }

        if (estado.contrasena.isBlank()) {
            _uiState.update {
                it.copy(
                    errorCorreo = null,
                    errorContrasena = "Ingresa tu contraseña",
                    inicioExitoso = false
                )
            }
            return null
        }

        // Busca un usuario que coincida con las credenciales
        // y el rol seleccionado. Las cuentas inactivas se rechazan.

        val usuarioEncontrado = DatosEjemplo.usuarios.firstOrNull { usuario ->
            usuario.correo.equals(correo, ignoreCase = true) &&
                    contrasenasDemo[usuario.correo] == estado.contrasena &&
                    usuario.rol == estado.rol &&
                    usuario.activo
        }

        // Actualiza el estado de la pantalla.

        _uiState.update {
            it.copy(
                errorCorreo = if (usuarioEncontrado == null) {
                    "No se pudo iniciar sesión"
                } else {
                    null
                },
                errorContrasena = if (usuarioEncontrado == null) {
                    "Verifica tu correo, contraseña y rol"
                } else {
                    null
                },
                inicioExitoso = usuarioEncontrado != null
            )
        }

        return usuarioEncontrado
    }

    // Restablece el formulario a su estado inicial.

    fun onLimpiar() {
        _uiState.value = SesionUiState()
    }
}
