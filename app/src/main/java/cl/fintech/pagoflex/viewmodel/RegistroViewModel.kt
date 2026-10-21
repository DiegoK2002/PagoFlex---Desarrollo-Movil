package cl.fintech.pagoflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.fintech.pagoflex.data.PagoFlexDbHelper
import cl.fintech.pagoflex.model.RegistroUiState
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.util.Seguridad
import cl.fintech.pagoflex.util.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val bd = PagoFlexDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onNombresCambia(valor: String) {
        _uiState.update { it.copy(nombres = valor.take(40), errorNombres = null) }
    }

    fun onApellidosCambia(valor: String) {
        _uiState.update { it.copy(apellidos = valor.take(40), errorApellidos = null) }
    }

    fun onRutNumeroCambia(valor: String) {
        _uiState.update { it.copy(rutNumero = valor.filter { c -> c.isDigit() }.take(8), errorRut = null) }
    }

    fun onRutDvCambia(valor: String) {
        _uiState.update {
            it.copy(
                rutDv = valor.filter { c -> c.isDigit() || c.uppercaseChar() == 'K' }.take(1).uppercase(),
                errorRut = null
            )
        }
    }

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor.trim().take(60), errorCorreo = null) }
    }

    fun onComunaSeleccionada(valor: String) {
        _uiState.update { it.copy(comuna = valor, errorComuna = null) }
    }

    fun onContrasenaCambia(valor: String) {
        val nueva = valor.take(30)
        _uiState.update {
            it.copy(
                contrasena = nueva,
                errorContrasena = null,
                errorRepetir = if (it.repetirContrasena.isNotEmpty() && it.repetirContrasena != nueva) {
                    "Las contrase\u00f1as no coinciden"
                } else {
                    null
                }
            )
        }
    }

    fun onRepetirCambia(valor: String) {
        val nueva = valor.take(30)
        _uiState.update {
            it.copy(
                repetirContrasena = nueva,
                errorRepetir = if (nueva.isNotEmpty() && nueva != it.contrasena) {
                    "Las contrase\u00f1as no coinciden"
                } else {
                    null
                }
            )
        }
    }

    fun onAlternarContrasena() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onAlternarRepetir() {
        _uiState.update { it.copy(repetirVisible = !it.repetirVisible) }
    }

    fun onRegistrar() {
        val e = _uiState.value

        val errorNombres = Validaciones.validarNombre(e.nombres, "nombres")
        val errorApellidos = Validaciones.validarNombre(e.apellidos, "apellidos")
        var errorRut = Validaciones.validarRut(e.rutNumero, e.rutDv)
        var errorCorreo = Validaciones.validarCorreo(e.correo)
        val errorComuna = Validaciones.validarComuna(e.comuna)
        val errorContrasena = Validaciones.validarContrasena(
            contrasena = e.contrasena,
            correo = e.correo,
            nombres = "${e.nombres} ${e.apellidos}"
        )
        val errorRepetir = Validaciones.validarRepeticion(e.contrasena, e.repetirContrasena)

        val rutFormateado = if (errorRut == null) Validaciones.formatearRut(e.rutNumero, e.rutDv) else ""
        if (errorRut == null && bd.existeRut(rutFormateado)) {
            errorRut = "Este RUT ya est\u00e1 registrado"
        }
        if (errorCorreo == null && bd.existeCorreo(e.correo)) {
            errorCorreo = "Este correo ya est\u00e1 registrado"
        }

        val hayErrores = listOf(
            errorNombres, errorApellidos, errorRut, errorCorreo,
            errorComuna, errorContrasena, errorRepetir
        ).any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorNombres = errorNombres,
                    errorApellidos = errorApellidos,
                    errorRut = errorRut,
                    errorCorreo = errorCorreo,
                    errorComuna = errorComuna,
                    errorContrasena = errorContrasena,
                    errorRepetir = errorRepetir,
                    registroExitoso = false
                )
            }
            return
        }

        val nombres = Validaciones.normalizarNombre(e.nombres)
        val apellidos = Validaciones.normalizarNombre(e.apellidos)

        val id = bd.registrarUsuario(
            rut = rutFormateado,
            nombres = nombres,
            apellidos = apellidos,
            correo = e.correo,
            contrasenaHash = Seguridad.hashContrasena(e.correo, e.contrasena),
            comuna = e.comuna,
            rol = Rol.USUARIO_FINAL
        )

        if (id == -1L) {
            _uiState.update { it.copy(errorCorreo = "No se pudo guardar el registro. Intenta de nuevo") }
            return
        }

        _uiState.update {
            it.copy(
                registroExitoso = true,
                nombreRegistrado = nombres,
                errorNombres = null, errorApellidos = null, errorRut = null, errorCorreo = null,
                errorComuna = null, errorContrasena = null, errorRepetir = null
            )
        }
    }

    fun onLimpiar() {
        _uiState.value = RegistroUiState()
    }
}
