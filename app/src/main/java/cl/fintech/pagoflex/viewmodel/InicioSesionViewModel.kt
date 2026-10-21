package cl.fintech.pagoflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.fintech.pagoflex.data.PagoFlexDbHelper
import cl.fintech.pagoflex.model.SesionUiState
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.util.Seguridad
import cl.fintech.pagoflex.util.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InicioSesionViewModel(application: Application) : AndroidViewModel(application) {

    private val bd = PagoFlexDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(SesionUiState())
    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(valor: String) {
        _uiState.update {
            it.copy(correo = valor.trim(), errorCorreo = null, errorGeneral = null)
        }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(contrasena = valor, errorContrasena = null, errorGeneral = null)
        }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onIniciar(): Usuario? {
        val estado = _uiState.value

        val errorCorreo = Validaciones.validarCorreo(estado.correo)
        val errorContrasena = if (estado.contrasena.isEmpty()) "Ingresa tu contrase\u00f1a" else null

        if (errorCorreo != null || errorContrasena != null) {
            _uiState.update {
                it.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena, errorGeneral = null)
            }
            return null
        }

        val hash = Seguridad.hashContrasena(estado.correo, estado.contrasena)
        val usuario = bd.buscarPorCredenciales(estado.correo, hash)

        if (usuario == null) {
            _uiState.update { it.copy(errorGeneral = "Correo o contrase\u00f1a incorrectos") }
            return null
        }
        return usuario
    }

    fun onLimpiar() {
        _uiState.value = SesionUiState()
    }
}
